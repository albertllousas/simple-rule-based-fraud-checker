package com.fraudchecker.application.service

import com.fraudchecker.domain.model.CountryCode
import com.fraudchecker.domain.model.CustomerId
import com.fraudchecker.domain.model.DifferentAccountsLoginsFromSameIPRule
import com.fraudchecker.domain.model.DomainEventPublisher
import com.fraudchecker.domain.model.FeatureFlags
import com.fraudchecker.domain.model.FraudRule
import com.fraudchecker.domain.model.FraudRuleRepository
import com.fraudchecker.domain.model.IPAddress
import com.fraudchecker.domain.model.LoginFromNonResidenceCountryRule
import com.fraudchecker.domain.model.SuspiciousActivity.DifferentAccountsLoginsFromSameIp
import com.fraudchecker.domain.model.SuspiciousActivity.LoginFromNonResidenceCountry
import com.fraudchecker.domain.model.SuspiciousActivityDetected
import com.fraudchecker.domain.model.service.CheckFraudRule
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.UUID

class CheckSuspiciousLoginActivityServiceShould {

    private val fraudRuleRepository = mockk<FraudRuleRepository>()

    private val featureFlags = mockk<FeatureFlags>()

    private val checkFraudRule = mockk<CheckFraudRule>()

    private val domainEventPublisher = mockk<DomainEventPublisher>(relaxed = true)

    private val checkSuspiciousLoginActivity: CheckSuspiciousLoginActivity =
        CheckSuspiciousLoginActivityService(
            fraudRuleRepository = fraudRuleRepository,
            featureFlags = featureFlags,
            checkFraudRule = checkFraudRule,
            domainEventPublisher = domainEventPublisher
        )

    @Test
    fun `should check all suspicious login activity for a customer`() {
        val customerId = CustomerId(UUID.randomUUID())
        val loginFromDifferentCountry = SuspiciousActivityDetected(
            customerId, LoginFromNonResidenceCountry(CountryCode("AFG"))
        )
        val loginsFromSameIp = SuspiciousActivityDetected(
            customerId, DifferentAccountsLoginsFromSameIp(IPAddress("168.212.226.204"))
        )
        every {
            fraudRuleRepository.findAllLoginRules()
        } returns listOf(LoginFromNonResidenceCountryRule, DifferentAccountsLoginsFromSameIPRule)
        every { featureFlags.isEnabled(any()) } returns true
        every { checkFraudRule(LoginFromNonResidenceCountryRule, customerId) } returns listOf(loginFromDifferentCountry)
        every { checkFraudRule(DifferentAccountsLoginsFromSameIPRule, customerId) } returns listOf(loginsFromSameIp)

        checkSuspiciousLoginActivity(CheckSuspiciousLoginActivityRequest(customerId.value))

        verify {
            domainEventPublisher.publish(loginFromDifferentCountry)
            domainEventPublisher.publish(loginsFromSameIp)
        }
    }

    @Test
    fun `should check suspicious activity when rules are disabled by feature flag`() {
        val customerId = CustomerId(UUID.randomUUID())
        every { fraudRuleRepository.findAllLoginRules() } returns listOf(LoginFromNonResidenceCountryRule,
            DifferentAccountsLoginsFromSameIPRule)
        every { featureFlags.isEnabled(any()) } returns false

        checkSuspiciousLoginActivity(CheckSuspiciousLoginActivityRequest(customerId.value))

        verify { domainEventPublisher wasNot Called }
    }
}
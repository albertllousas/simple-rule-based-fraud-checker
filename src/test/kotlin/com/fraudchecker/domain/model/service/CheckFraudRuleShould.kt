package com.fraudchecker.domain.model.service

import com.fraudchecker.domain.model.CountryCode
import com.fraudchecker.domain.model.Customer
import com.fraudchecker.domain.model.CustomerFinder
import com.fraudchecker.domain.model.CustomerId
import com.fraudchecker.domain.model.DeviceId
import com.fraudchecker.domain.model.DifferentAccountsLoginsFromSameDeviceRule
import com.fraudchecker.domain.model.DifferentAccountsLoginsFromSameIPRule
import com.fraudchecker.domain.model.IPAddress
import com.fraudchecker.domain.model.LastCustomerLogins
import com.fraudchecker.domain.model.Login
import com.fraudchecker.domain.model.LoginFromNonResidenceCountryRule
import com.fraudchecker.domain.model.LoginHistory
import com.fraudchecker.domain.model.SuspiciousActivity.DifferentAccountsLoginsFromSameDevice
import com.fraudchecker.domain.model.SuspiciousActivity.DifferentAccountsLoginsFromSameIp
import com.fraudchecker.domain.model.SuspiciousActivity.LoginFromNonResidenceCountry
import com.fraudchecker.domain.model.SuspiciousActivityDetected
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.now
import java.util.UUID.randomUUID

class CheckFraudRuleShould {

    private val customerId = CustomerId(randomUUID())

    private val deviceId = DeviceId(randomUUID())

    private val login = Login(customerId, IPAddress("51.195.103.74"), deviceId, now(), CountryCode("AFG"))

    private val loginHistory = mockk<LoginHistory>()

    private val customerFinder = mockk<CustomerFinder>()

    private val checkFraudRule = CheckFraudRule(customerFinder, loginHistory)

    @Test
    fun `not execute a rule if there is no recent login for the customer`() {
        every { loginHistory.getLastSuccessfulLogin(customerId) } returns null

        val result = checkFraudRule(DifferentAccountsLoginsFromSameDeviceRule, customerId)

        assertThat(result).isEmpty()
    }

    @Test
    fun `execute 'DifferentAccountsLoginsFromSameDeviceRule' fraud rule`() {
        every { loginHistory.getLastSuccessfulLogin(customerId) } returns login
        every { loginHistory.findByDeviceId(deviceId) } returns listOf(login)
        val stubbedRule = mockk<DifferentAccountsLoginsFromSameDeviceRule>()
        val suspiciousActivityDetected = SuspiciousActivityDetected(
            customerId,
            DifferentAccountsLoginsFromSameDevice(login.deviceId)
        )
        every { stubbedRule.check(listOf(login)) } returns listOf(suspiciousActivityDetected)

        val result = checkFraudRule(stubbedRule, customerId)

        assertThat(result).isEqualTo(listOf(suspiciousActivityDetected))
    }

    @Test
    fun `execute 'DifferentAccountsLoginsFromSameIPRule' fraud rule`() {
        val stubbedRule = mockk<DifferentAccountsLoginsFromSameIPRule>()
        val suspiciousActivityDetected = SuspiciousActivityDetected(
            customerId,
            DifferentAccountsLoginsFromSameIp(login.ip)
        )
        every { loginHistory.findByIP(login.ip) } returns listOf(login)
        every { loginHistory.getLastSuccessfulLogin(customerId) } returns login
        every { stubbedRule.check(listOf(login)) } returns listOf(suspiciousActivityDetected)

        val result = checkFraudRule(stubbedRule, customerId)

        assertThat(result).isEqualTo(listOf(suspiciousActivityDetected))
    }

    @Test
    fun `execute 'LoginFromNonResidenceCountryRule' fraud rule`() {
        val stubbedRule = mockk<LoginFromNonResidenceCountryRule>()
        val suspiciousActivityDetected = SuspiciousActivityDetected(
            customerId,
            LoginFromNonResidenceCountry(login.ipCountryCode)
        )
        val customer = Customer(login.customerId, CountryCode("ESP"))
        every { customerFinder.find(login.customerId) } returns customer
        every { loginHistory.findByCustomer(login.customerId) } returns listOf(login)
        every { loginHistory.getLastSuccessfulLogin(customerId) } returns login
        every { stubbedRule.check(LastCustomerLogins(listOf(login), customer)) } returns listOf(
            suspiciousActivityDetected)

        val result = checkFraudRule(stubbedRule, customerId)

        assertThat(result).isEqualTo(listOf(suspiciousActivityDetected))
    }
}
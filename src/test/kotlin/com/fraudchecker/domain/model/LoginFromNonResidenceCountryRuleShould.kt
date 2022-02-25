package com.fraudchecker.domain.model

import com.fraudchecker.domain.model.SuspiciousActivity.LoginFromNonResidenceCountry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class LoginFromNonResidenceCountryRuleShould {

    @Test
    fun `not detect any suspicious activity for logins in the same country as the residence of the customer`() {
        val customer = Customer(
            customerId = CustomerId(UUID.randomUUID()),
            countryCode = CountryCode("ESP")
        )
        val login = Login(
            customerId = customer.customerId,
            ip = IPAddress("51.195.103.74"),
            deviceId = DeviceId(UUID.randomUUID()),
            ts = LocalDateTime.now(),
            ipCountryCode = CountryCode("ESP")
        )
        val lastLogins = LastCustomerLogins(listOf(login), customer)

        val result = LoginFromNonResidenceCountryRule.check(lastLogins)

        assertThat(result).isEqualTo(emptyList<SuspiciousActivityDetected>())
    }

    @Test
    fun `detect suspicious activity when there is a login different from the residence country of the costumer`() {
        val customer = Customer(
            customerId = CustomerId(UUID.randomUUID()),
            countryCode = CountryCode("ESP")
        )
        val login = Login(
            customerId = customer.customerId,
            ip = IPAddress("51.195.103.74"),
            deviceId = DeviceId(UUID.randomUUID()),
            ts = LocalDateTime.now(),
            ipCountryCode = CountryCode("AFG")
        )
        val suspiciousActivityDetected = SuspiciousActivityDetected(
            customerId = customer.customerId,
            suspiciousActivity = LoginFromNonResidenceCountry(CountryCode("AFG"))
        )
        val lastLogins = LastCustomerLogins(listOf(login), customer)

        val result = LoginFromNonResidenceCountryRule.check(lastLogins)

        assertThat(result).isEqualTo(listOf(suspiciousActivityDetected))
    }
}
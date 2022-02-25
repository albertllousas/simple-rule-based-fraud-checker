package com.fraudchecker.domain.model

import com.fraudchecker.domain.model.SuspiciousActivity.DifferentAccountsLoginsFromSameDevice
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.now
import java.util.UUID

internal class DifferentAccountsLoginsFromSameDeviceRuleShould {

    @Test
    fun `not detect any suspicious activity for logins with the same device and same customer`() {
        val deviceId = DeviceId(UUID.randomUUID())
        val customerId = CustomerId(UUID.randomUUID())
        val logins = listOf(
            Login(customerId, IPAddress("51.195.103.74"), deviceId, now(), CountryCode("ESP")),
            Login(customerId, IPAddress("51.195.103.74"), deviceId, now(), CountryCode("ESP"))
        )
        val result = DifferentAccountsLoginsFromSameDeviceRule.check(logins)

        assertThat(result).isEqualTo(emptyList<SuspiciousActivityDetected>())
    }

    @Test
    fun `detect suspicious activity when there are logins with the same device for different customers`() {
        val firstDeviceId = DeviceId(UUID.randomUUID())
        val secondDeviceId = DeviceId(UUID.randomUUID())
        val firstCustomerId = CustomerId(UUID.randomUUID())
        val secondCustomerId = CustomerId(UUID.randomUUID())
        val thirdCustomerId = CustomerId(UUID.randomUUID())
        val logins = listOf(
            Login(firstCustomerId, IPAddress("51.195.103.74"), firstDeviceId, now(), CountryCode("ESP")),
            Login(secondCustomerId, IPAddress("51.195.103.75"), secondDeviceId, now(), CountryCode("ESP")),
            Login(thirdCustomerId, IPAddress("51.195.103.7g"), firstDeviceId, now(), CountryCode("ESP"))
        )
        val result = DifferentAccountsLoginsFromSameDeviceRule.check(logins)

        assertThat(result).isEqualTo(
            listOf(
                SuspiciousActivityDetected(
                    customerId = firstCustomerId,
                    suspiciousActivity = DifferentAccountsLoginsFromSameDevice(firstDeviceId)
                ),
                SuspiciousActivityDetected(
                    customerId = thirdCustomerId,
                    suspiciousActivity = DifferentAccountsLoginsFromSameDevice(firstDeviceId)
                )
            )
        )
    }
}
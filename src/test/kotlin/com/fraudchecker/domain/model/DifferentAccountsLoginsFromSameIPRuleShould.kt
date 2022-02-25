package com.fraudchecker.domain.model

import com.fraudchecker.domain.model.SuspiciousActivity.DifferentAccountsLoginsFromSameIp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.now
import java.util.UUID

class DifferentAccountsLoginsFromSameIPRuleShould {

    @Test
    fun `not detect any suspicious activity for logins with the same IP and same customer`() {
        val deviceId = DeviceId(UUID.randomUUID())
        val ipAddress = IPAddress("51.195.103.74")
        val customerId = CustomerId(UUID.randomUUID())
        val logins = listOf(
            Login(customerId, ipAddress, deviceId, now(), CountryCode("ESP")),
            Login(customerId, ipAddress, deviceId, now(), CountryCode("ESP"))
        )
        val result = DifferentAccountsLoginsFromSameIPRule.check(logins)

        assertThat(result).isEqualTo(emptyList<SuspiciousActivityDetected>())
    }

    @Test
    fun `detect suspicious activity when there are logins with the same device for different customers`() {
        val firstIpAddress = IPAddress("51.195.103.74")
        val secondIpAddress = IPAddress("51.195.103.75")
        val firstCustomerId = CustomerId(UUID.randomUUID())
        val secondCustomerId = CustomerId(UUID.randomUUID())
        val thirdCustomerId = CustomerId(UUID.randomUUID())
        val logins = listOf(
            Login(firstCustomerId, firstIpAddress, DeviceId(UUID.randomUUID()), now(), CountryCode("ESP")),
            Login(secondCustomerId, secondIpAddress, DeviceId(UUID.randomUUID()), now(), CountryCode("ESP")),
            Login(thirdCustomerId, firstIpAddress, DeviceId(UUID.randomUUID()), now(), CountryCode("ESP"))
        )
        val result = DifferentAccountsLoginsFromSameIPRule.check(logins)

        assertThat(result).isEqualTo(
            listOf(
                SuspiciousActivityDetected(
                    customerId = firstCustomerId,
                    suspiciousActivity = DifferentAccountsLoginsFromSameIp(firstIpAddress)
                ),
                SuspiciousActivityDetected(
                    customerId = thirdCustomerId,
                    suspiciousActivity = DifferentAccountsLoginsFromSameIp(firstIpAddress)
                )
            )
        )
    }
}
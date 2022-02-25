package com.fraudchecker.infrastructure.adapter.inbound

import com.fraudchecker.application.service.CheckSuspiciousLoginActivity
import com.fraudchecker.application.service.CheckSuspiciousLoginActivityRequest
import com.fraudchecker.domain.model.CountryCode
import com.fraudchecker.domain.model.CustomerId
import com.fraudchecker.domain.model.DeviceId
import com.fraudchecker.domain.model.IPAddress
import com.fraudchecker.domain.model.Login
import com.fraudchecker.infrastructure.adapter.inbound.LoginEvent.LoginFailed
import com.fraudchecker.infrastructure.adapter.inbound.LoginEvent.LoginSucceeded
import com.fraudchecker.infrastructure.adapter.outbound.InMemoryLoginHistoryRepository
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.now
import java.util.UUID.randomUUID

class InMemoryLoginStreamConsumerShould {

    private val loginHistoryRepository = mockk<InMemoryLoginHistoryRepository>(relaxed = true)

    private val checkSuspiciousLoginActivity = mockk<CheckSuspiciousLoginActivity>(relaxed = true)

    private val loginStreamConsumer = InMemoryLoginStreamConsumer(
        loginHistoryRepository = loginHistoryRepository,
        checkSuspiciousLoginActivity = checkSuspiciousLoginActivity
    )

    @Test
    fun `react to a new succeeded login`() {
        val loginSucceeded = LoginSucceeded(randomUUID(), randomUUID(), "168.212.226.204", "ESP", now())

        loginStreamConsumer.reactTo(loginSucceeded)

        verify {
            loginHistoryRepository.add(
                Login(
                    customerId = CustomerId(loginSucceeded.customerId),
                    ip = IPAddress(loginSucceeded.ip),
                    deviceId = DeviceId(loginSucceeded.deviceId),
                    ts = loginSucceeded.on,
                    ipCountryCode = CountryCode(loginSucceeded.ipCountryCode)
                )
            )
            checkSuspiciousLoginActivity(CheckSuspiciousLoginActivityRequest(loginSucceeded.customerId))
        }
    }

    @Test
    fun `ignore failed logins`() {
        val loginFailed = LoginFailed("168.212.226.204", randomUUID(), "ESP", now())

        loginStreamConsumer.reactTo(loginFailed)

        verify {
            loginHistoryRepository wasNot Called
            checkSuspiciousLoginActivity wasNot Called
        }
    }
}

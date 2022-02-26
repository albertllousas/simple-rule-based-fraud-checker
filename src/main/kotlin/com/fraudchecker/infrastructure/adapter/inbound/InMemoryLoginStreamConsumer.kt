package com.fraudchecker.infrastructure.adapter.inbound

import com.fraudchecker.application.service.CheckSuspiciousLoginActivity
import com.fraudchecker.application.service.CheckSuspiciousLoginActivityRequest
import com.fraudchecker.domain.model.CountryCode
import com.fraudchecker.domain.model.CustomerId
import com.fraudchecker.domain.model.DeviceId
import com.fraudchecker.domain.model.IPAddress
import com.fraudchecker.domain.model.Login
import com.fraudchecker.infrastructure.adapter.inbound.LoginEvent.LoginSucceeded
import com.fraudchecker.infrastructure.adapter.outbound.InMemoryLoginHistoryRepository
import java.time.LocalDateTime
import java.util.UUID

// Dump impl, real one would be kafka or other event stream platform
class InMemoryLoginStreamConsumer(
    private val loginHistoryRepository: InMemoryLoginHistoryRepository, // since this is infra depending on infra, we don't need to use the interface
    private val checkSuspiciousLoginActivity: CheckSuspiciousLoginActivity,
) {

    fun reactTo(event: LoginEvent) =
        when (event) {
            is LoginSucceeded -> {
                loginHistoryRepository.add(event.toDomain())
                // this flow could be decoupled publishing an event `SuspiciousLoginActivityCheckRequested`, consume it and trigger `checkSuspiciousLoginActivity` in EDA fashion
                checkSuspiciousLoginActivity(CheckSuspiciousLoginActivityRequest(event.customerId))
            }
            else -> Unit
        }

    private fun LoginSucceeded.toDomain() = Login(
        customerId = CustomerId(this.customerId),
        ip = IPAddress(this.ip),
        deviceId = DeviceId(this.deviceId),
        ts = this.on,
        ipCountryCode = CountryCode(this.ipCountryCode)
    )
}

// events from external systems
sealed class LoginEvent {
    data class LoginFailed(
        val ip: String,
        val deviceId: UUID,
        val ipCountryCode: String,
        val on: LocalDateTime,
    ) : LoginEvent()

    data class LoginSucceeded(
        val customerId: UUID,
        val deviceId: UUID,
        val ip: String,
        val ipCountryCode: String,
        val on: LocalDateTime,
    ) : LoginEvent()
}

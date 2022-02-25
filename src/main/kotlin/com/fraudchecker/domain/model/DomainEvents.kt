package com.fraudchecker.domain.model

import java.time.LocalDateTime

sealed class DomainEvent

data class SuspiciousActivityDetected(
    val customerId: CustomerId,
    val suspiciousActivity: SuspiciousActivity
) : DomainEvent()

sealed interface SuspiciousActivity {

    data class DifferentAccountsLoginsFromSameIp(val ip: IPAddress) : SuspiciousActivity

    data class DifferentAccountsLoginsFromSameDevice(val deviceId: DeviceId) : SuspiciousActivity

    data class LoginFromNonResidenceCountry(val loginCountryCode: CountryCode) : SuspiciousActivity
}

interface DomainEventPublisher {
    fun publish(domainEvent: DomainEvent)
}

interface DomainEventSubscriber {
    fun handle(domainEvent: DomainEvent)
}
package com.fraudchecker.domain.model

import java.time.LocalDateTime

interface LoginHistory {
    fun getLastSuccessfulLogin(customerId: CustomerId): Login?
    fun findByDeviceId(deviceId: DeviceId): List<Login>
    fun findByIP(ip: IPAddress): List<Login>
    fun findByCustomer(customerId: CustomerId): List<Login>
}

data class Login(
    val customerId: CustomerId,
    val ip: IPAddress,
    val deviceId: DeviceId,
    val ts: LocalDateTime,
    val ipCountryCode: CountryCode
)

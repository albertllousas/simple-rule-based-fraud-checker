package com.fraudchecker.infrastructure.adapter.outbound

import com.fraudchecker.domain.model.CustomerId
import com.fraudchecker.domain.model.DeviceId
import com.fraudchecker.domain.model.IPAddress
import com.fraudchecker.domain.model.Login
import com.fraudchecker.domain.model.LoginHistory

class InMemoryLoginHistoryRepository(private var history: List<Login>) : LoginHistory {

    override fun getLastSuccessfulLogin(customerId: CustomerId): Login? =
        history.filter { it.customerId == customerId }.maxByOrNull { it.ts }

    override fun findByDeviceId(deviceId: DeviceId): List<Login> = history.filter { it.deviceId == deviceId }

    override fun findByIP(ip: IPAddress): List<Login> = history.filter { it.ip == ip }

    override fun findByCustomer(customerId: CustomerId): List<Login> = history.filter { it.customerId == customerId }

    fun add(login: Login) {
        history = history + login
    }
}
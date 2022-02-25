package com.fraudchecker.infrastructure.adapter.inbound

import com.fraudchecker.domain.model.CountryCode
import com.fraudchecker.domain.model.Customer
import com.fraudchecker.domain.model.CustomerId
import com.fraudchecker.infrastructure.adapter.inbound.CustomerEvent.CustomerCreated
import com.fraudchecker.infrastructure.adapter.inbound.CustomerEvent.CustomerDeleted
import com.fraudchecker.infrastructure.adapter.outbound.InMemoryCustomerReplicationRepository
import java.time.LocalDateTime
import java.util.UUID

// dump impl, real one would be kafka or other event stream platform
class InMemoryCustomerStreamConsumer(
    // since this is infra depending on infra, we don't need to use the interface
    private val customerRepository: InMemoryCustomerReplicationRepository,
) {
    fun reactTo(event: CustomerEvent) =
        when (event) {
            is CustomerCreated ->
                customerRepository.add(Customer(CustomerId(event.customerId), CountryCode(event.residenceCountryCode)))
            is CustomerDeleted ->
                customerRepository.delete(CustomerId(event.customerId))
        }
}

// events from external systems
sealed class CustomerEvent {
    data class CustomerCreated(
        val customerId: UUID,
        val residenceCountryCode: String,
        val on: LocalDateTime,
    ) : CustomerEvent()

    data class CustomerDeleted(
        val customerId: UUID,
        val on: LocalDateTime,
    ) : CustomerEvent()
}

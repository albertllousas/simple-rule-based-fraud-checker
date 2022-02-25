package com.fraudchecker.infrastructure.adapter.inbound

import com.fraudchecker.domain.model.CountryCode
import com.fraudchecker.domain.model.Customer
import com.fraudchecker.domain.model.CustomerId
import com.fraudchecker.infrastructure.adapter.inbound.CustomerEvent.CustomerCreated
import com.fraudchecker.infrastructure.adapter.inbound.CustomerEvent.CustomerDeleted
import com.fraudchecker.infrastructure.adapter.outbound.InMemoryCustomerReplicationRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.now
import java.util.UUID.randomUUID

class InMemoryCustomerStreamConsumerShould {

    private val customerReplicationRepository = mockk<InMemoryCustomerReplicationRepository>(relaxed = true)

    private val customerStreamConsumer = InMemoryCustomerStreamConsumer(customerReplicationRepository)

    @Test
    fun `react to a new created customer event`() {
        val created = CustomerCreated(randomUUID(), "ESP", now())

        customerStreamConsumer.reactTo(created)

        verify {
            customerReplicationRepository.add(
                Customer(CustomerId(created.customerId), CountryCode(created.residenceCountryCode))
            )
        }
    }

    @Test
    fun `react to a deleted customer event`() {
        val deleted = CustomerDeleted(randomUUID(), now())

        customerStreamConsumer.reactTo(deleted)

        verify {
            customerReplicationRepository.delete(CustomerId(deleted.customerId))
        }
    }
}
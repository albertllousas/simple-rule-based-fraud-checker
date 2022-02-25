package com.fraudchecker.infrastructure.adapter.outbound

import com.fraudchecker.domain.model.Customer
import com.fraudchecker.domain.model.CustomerFinder
import com.fraudchecker.domain.model.CustomerId

class InMemoryCustomerReplicationRepository(private var customers: List<Customer>) : CustomerFinder {

    override fun find(customerId: CustomerId): Customer? = customers.find { it.customerId == customerId }

    fun add(customer: Customer) {
        customers = customers.filterNot { it.customerId == customer.customerId } + customer
    }

    fun delete(customerId: CustomerId) {
        customers = customers.filterNot { it.customerId == customerId }
    }
}
package com.fraudchecker.domain.model

interface CustomerFinder {
    fun find(customerId: CustomerId): Customer?
}

data class Customer(val customerId: CustomerId, val countryCode: CountryCode)
package com.fraudchecker.domain.model

import java.util.UUID

@JvmInline
value class CustomerId(val value: UUID)

@JvmInline
value class DeviceId(val value: UUID)

@JvmInline
value class IPAddress(val value: String)

data class CountryCode(val countryCode: String)

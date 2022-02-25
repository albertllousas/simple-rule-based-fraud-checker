package com.fraudchecker.infrastructure.adapter.outbound

import com.fraudchecker.domain.model.FeatureFlags
import com.fraudchecker.domain.model.FraudRule

class InMemoryFeatureFlags(private val flags: Map<String, Boolean>): FeatureFlags {
    override fun isEnabled(rule: FraudRule<*>): Boolean = flags.getOrDefault(rule::class.simpleName!!, false)
}
package com.fraudchecker.domain.model

interface FeatureFlags {
    fun isEnabled(rule: FraudRule<*>): Boolean
}
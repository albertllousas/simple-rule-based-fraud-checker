package com.fraudchecker.domain.model

interface FraudRuleRepository {
    fun findAllLoginRules(): List<FraudRule<*>>
}
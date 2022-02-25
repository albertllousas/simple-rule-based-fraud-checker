package com.fraudchecker.infrastructure.adapter.outbound

import com.fraudchecker.domain.model.FraudRule
import com.fraudchecker.domain.model.FraudRuleRepository

class InMemoryFraudRepository(private val rules: List<FraudRule<*>>):FraudRuleRepository {
    override fun findAllLoginRules(): List<FraudRule<*>> = rules.toList()
}
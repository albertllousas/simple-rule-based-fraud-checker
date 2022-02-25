package com.fraudchecker.application.service

import com.fraudchecker.domain.model.CustomerId
import com.fraudchecker.domain.model.DomainEventPublisher
import com.fraudchecker.domain.model.FeatureFlags
import com.fraudchecker.domain.model.FraudRuleRepository
import com.fraudchecker.domain.model.service.CheckFraudRule

class CheckSuspiciousLoginActivityService(
    private val fraudRuleRepository: FraudRuleRepository,
    private val featureFlags: FeatureFlags,
    private val checkFraudRule: CheckFraudRule,
    private val domainEventPublisher: DomainEventPublisher,
) : CheckSuspiciousLoginActivity {

    override fun invoke(request: CheckSuspiciousLoginActivityRequest) {
        fraudRuleRepository.findAllLoginRules()
            .filter(featureFlags::isEnabled)
            .map { checkFraudRule(it, CustomerId(request.customerId)) }
            .flatten()
            .forEach { suspiciousActivity ->  domainEventPublisher.publish(suspiciousActivity) }
    }
}

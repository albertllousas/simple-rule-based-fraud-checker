package com.fraudchecker.domain.model.service

import com.fraudchecker.domain.model.CustomerFinder
import com.fraudchecker.domain.model.CustomerId
import com.fraudchecker.domain.model.DifferentAccountsLoginsFromSameDeviceRule
import com.fraudchecker.domain.model.DifferentAccountsLoginsFromSameIPRule
import com.fraudchecker.domain.model.FraudRule
import com.fraudchecker.domain.model.LastCustomerLogins
import com.fraudchecker.domain.model.LoginFromNonResidenceCountryRule
import com.fraudchecker.domain.model.LoginHistory
import com.fraudchecker.domain.model.SuspiciousActivityDetected

class CheckFraudRule(
    private val customerFinder: CustomerFinder,
    private val loginHistory: LoginHistory,
) {

    // if this method grows, then it will be time to abstract, for now we leave it like this to favor
    // YAGNI and clean code (easy to understand and change) principles
    operator fun invoke(rule: FraudRule<*>, customerId: CustomerId): List<SuspiciousActivityDetected> =
        loginHistory.getLastSuccessfulLogin(customerId)
            ?.let { lastLogin ->
                when (rule) {
                    is DifferentAccountsLoginsFromSameDeviceRule ->
                        rule.check(loginHistory.findByDeviceId(lastLogin.deviceId))
                    is DifferentAccountsLoginsFromSameIPRule ->
                        rule.check(loginHistory.findByIP(lastLogin.ip))
                    is LoginFromNonResidenceCountryRule ->
                        customerFinder.find(customerId)?.let {
                            rule.check(LastCustomerLogins(loginHistory.findByCustomer(lastLogin.customerId), it))
                        }
                }
            } ?: emptyList()
}
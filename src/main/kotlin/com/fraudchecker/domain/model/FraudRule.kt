package com.fraudchecker.domain.model

import com.fraudchecker.domain.model.SuspiciousActivity.DifferentAccountsLoginsFromSameDevice
import com.fraudchecker.domain.model.SuspiciousActivity.DifferentAccountsLoginsFromSameIp
import com.fraudchecker.domain.model.SuspiciousActivity.LoginFromNonResidenceCountry


sealed interface FraudRule<A> {
    fun check(context: A): List<SuspiciousActivityDetected>
}

/**
 * Naive and non-performant implementations, just for the sake of the demo
 */

data class LastCustomerLogins(val logins: List<Login>, val customer: Customer)

object LoginFromNonResidenceCountryRule : FraudRule<LastCustomerLogins> {

    override fun check(context: LastCustomerLogins): List<SuspiciousActivityDetected> =
        context.logins
            .find { it.ipCountryCode != context.customer.countryCode }
            ?.let {
                listOf(
                    SuspiciousActivityDetected(
                        context.customer.customerId,
                        LoginFromNonResidenceCountry(it.ipCountryCode))
                )
            } ?: emptyList()
}

object DifferentAccountsLoginsFromSameIPRule : FraudRule<List<Login>> {

    override fun check(context: List<Login>): List<SuspiciousActivityDetected> =
        context
            .groupBy { it.ip }
            .toList()
            .flatMap { (ip, loginsByIp) ->
                val customers = loginsByIp.groupBy { it.customerId }.keys
                if (customers.size > 1)
                    customers.map {
                        SuspiciousActivityDetected(it, DifferentAccountsLoginsFromSameIp(ip))
                    }
                else emptyList()
            }
}

object DifferentAccountsLoginsFromSameDeviceRule : FraudRule<List<Login>> {

    override fun check(context: List<Login>): List<SuspiciousActivityDetected> =
        context
            .groupBy { it.deviceId }
            .toList()
            .flatMap { (deviceId, loginsByDeviceId) ->
                val customers = loginsByDeviceId.groupBy { it.customerId }.keys
                if (customers.size > 1)
                    customers.map {
                        SuspiciousActivityDetected(it, DifferentAccountsLoginsFromSameDevice(deviceId))
                    }
                else emptyList()
            }
}


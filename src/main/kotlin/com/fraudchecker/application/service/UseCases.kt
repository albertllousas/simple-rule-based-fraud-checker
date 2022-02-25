package com.fraudchecker.application.service

import java.util.UUID

typealias CheckSuspiciousLoginActivity = (CheckSuspiciousLoginActivityRequest) -> Unit

data class CheckSuspiciousLoginActivityRequest(val customerId: UUID)
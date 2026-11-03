package com.example.domain.privacy

import java.util.regex.Pattern

object PrivacyFilter {
    // Detect OTP, verification code, security code, password, 2FA
    private val otpPattern = Pattern.compile(
        "(?:otp|verification code|security code|passcode|one-time password|do not share|auth code)[^0-9]{0,20}([0-9]{4,8})",
        Pattern.CASE_INSENSITIVE
    )

    private val isolatedNumberPattern = Pattern.compile(
        "\\b([0-9]{4,8})\\b"
    )

    fun isOtpOrAuthCode(sender: String, body: String): Boolean {
        val lower = body.lowercase()
        val senderLower = sender.lowercase()

        // Check if message clearly indicates an OTP / 2FA code
        if (lower.contains("otp") ||
            lower.contains("verification code") ||
            lower.contains("one-time password") ||
            lower.contains("security code") ||
            lower.contains("auth code") ||
            lower.contains("login code") ||
            lower.contains("do not share this") ||
            lower.contains("is your secret code")
        ) {
            return true
        }

        if (senderLower.contains("verify") || senderLower.contains("auth") || senderLower.contains("otp")) {
            return true
        }

        return false
    }

    fun maskBody(body: String): String {
        if (body.length <= 8) return "********"
        val prefix = body.take(4)
        val suffix = body.takeLast(4)
        val maskedMiddle = "*".repeat((body.length - 8).coerceIn(4, 24))
        return "$prefix$maskedMiddle$suffix"
    }

    fun redactSensitiveNumbers(body: String): String {
        // Redacts 4-8 digit codes in body with [REDACTED]
        val matcher = otpPattern.matcher(body)
        if (matcher.find()) {
            return matcher.replaceAll("[REDACTED-CODE]")
        }
        return body
    }
}

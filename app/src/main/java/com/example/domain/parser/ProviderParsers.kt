package com.example.domain.parser

import com.example.domain.model.ParsedSmsResult
import java.util.regex.Pattern

data class ProviderProfile(
    val providerId: String,
    val displayName: String,
    val trustedSenders: List<String>,
    val description: String
)

object ProviderProfiles {
    val bKash = ProviderProfile(
        providerId = "bkash",
        displayName = "bKash",
        trustedSenders = listOf("bKash", "BKASH", "16247"),
        description = "MFS provider in Bangladesh. TrxID alphanumeric token."
    )

    val nagad = ProviderProfile(
        providerId = "nagad",
        displayName = "Nagad",
        trustedSenders = listOf("NAGAD", "Nagad", "16167"),
        description = "Postal MFS provider in Bangladesh. TxnID identifier."
    )

    val rocket = ProviderProfile(
        providerId = "rocket",
        displayName = "Rocket (DBBL)",
        trustedSenders = listOf("Rocket", "ROCKET", "16216", "DBBL"),
        description = "Dutch-Bangla Bank mobile banking. TxnId token."
    )

    val upay = ProviderProfile(
        providerId = "upay",
        displayName = "Upay (UCB)",
        trustedSenders = listOf("UPAY", "Upay", "16268"),
        description = "UCB mobile financial service. Trx ID reference."
    )

    val allProfiles = listOf(bKash, nagad, rocket, upay)
}

object BKashParser : SmsParser {
    override val parserId: String = "bkash"

    private val incomingPattern = Pattern.compile(
        "You have received Tk\\s*([0-9,]+(?:\\.[0-9]{1,2})?)\\s*from\\s*([0-9A-Za-z]+).*?TrxID\\s*([A-Za-z0-9]+)",
        Pattern.CASE_INSENSITIVE or Pattern.DOTALL
    )

    private val outgoingIndicators = listOf("cash out", "cash-out", "sent to", "you have sent", "payment to", "payment of", "failed", "reversed", "charge of")

    override fun canParse(sender: String, body: String): Boolean {
        val s = sender.uppercase()
        return ProviderProfiles.bKash.trustedSenders.any { s.contains(it.uppercase()) } ||
                body.contains("bKash", ignoreCase = true) ||
                body.contains("TrxID", ignoreCase = true)
    }

    override fun parse(sender: String, body: String): ParsedSmsResult {
        val s = sender.uppercase()
        val isSenderTrusted = ProviderProfiles.bKash.trustedSenders.any { s.contains(it.uppercase()) }

        val lowerBody = body.lowercase()
        val isOutgoing = outgoingIndicators.any { lowerBody.contains(it) }

        if (isOutgoing) {
            return ParsedSmsResult(
                provider = "bkash",
                status = "outgoing_filtered",
                isPaymentCandidate = false,
                senderMatch = isSenderTrusted,
                templateMatch = true,
                isOutgoingOrReversal = true,
                reviewReason = "Message is an outgoing payment, cash-out, or reversal."
            )
        }

        val matcher = incomingPattern.matcher(body)
        if (matcher.find()) {
            val amount = matcher.group(1)?.replace(",", "")?.toDoubleOrNull()
            val trxId = matcher.group(3)

            val isCandidate = isSenderTrusted && amount != null && amount > 0 && !trxId.isNullOrBlank()

            return ParsedSmsResult(
                provider = "bkash",
                transactionId = trxId,
                amount = amount,
                currency = "BDT",
                status = if (isCandidate) "parsed_candidate" else if (!isSenderTrusted) "unknown_sender" else "needs_review",
                isPaymentCandidate = isCandidate,
                senderMatch = isSenderTrusted,
                templateMatch = true,
                isOutgoingOrReversal = false,
                reviewReason = if (!isSenderTrusted) "Untrusted sender ID: SMS sender labels may be spoofed" else null,
                confidence = if (isCandidate) 0.98f else 0.5f
            )
        }

        return ParsedSmsResult(
            provider = "bkash",
            status = "template_mismatch",
            isPaymentCandidate = false,
            senderMatch = isSenderTrusted,
            templateMatch = false,
            reviewReason = "Message does not match the official incoming received-money template structure."
        )
    }
}

object NagadParser : SmsParser {
    override val parserId: String = "nagad"

    private val incomingPattern = Pattern.compile(
        "Money received.*?Amount:\\s*Tk\\s*([0-9,]+(?:\\.[0-9]{1,2})?).*?TxnID:\\s*([A-Za-z0-9]+)",
        Pattern.CASE_INSENSITIVE or Pattern.DOTALL
    )

    private val outgoingIndicators = listOf("cash out", "cash-out", "send money", "payment to", "failed", "reversed")

    override fun canParse(sender: String, body: String): Boolean {
        val s = sender.uppercase()
        return ProviderProfiles.nagad.trustedSenders.any { s.contains(it.uppercase()) } ||
                body.contains("Nagad", ignoreCase = true)
    }

    override fun parse(sender: String, body: String): ParsedSmsResult {
        val s = sender.uppercase()
        val isSenderTrusted = ProviderProfiles.nagad.trustedSenders.any { s.contains(it.uppercase()) }

        val lowerBody = body.lowercase()
        val isOutgoing = outgoingIndicators.any { lowerBody.contains(it) }

        if (isOutgoing) {
            return ParsedSmsResult(
                provider = "nagad",
                status = "outgoing_filtered",
                isPaymentCandidate = false,
                senderMatch = isSenderTrusted,
                templateMatch = true,
                isOutgoingOrReversal = true,
                reviewReason = "Outgoing transfer or debit alert"
            )
        }

        val matcher = incomingPattern.matcher(body)
        if (matcher.find()) {
            val amount = matcher.group(1)?.replace(",", "")?.toDoubleOrNull()
            val txnId = matcher.group(2)

            val isCandidate = isSenderTrusted && amount != null && amount > 0 && !txnId.isNullOrBlank()

            return ParsedSmsResult(
                provider = "nagad",
                transactionId = txnId,
                amount = amount,
                currency = "BDT",
                status = if (isCandidate) "parsed_candidate" else if (!isSenderTrusted) "unknown_sender" else "needs_review",
                isPaymentCandidate = isCandidate,
                senderMatch = isSenderTrusted,
                templateMatch = true,
                isOutgoingOrReversal = false,
                reviewReason = if (!isSenderTrusted) "Untrusted sender ID" else null,
                confidence = if (isCandidate) 0.98f else 0.5f
            )
        }

        return ParsedSmsResult(
            provider = "nagad",
            status = "template_mismatch",
            isPaymentCandidate = false,
            senderMatch = isSenderTrusted,
            templateMatch = false,
            reviewReason = "Did not match incoming Nagad money-received template"
        )
    }
}

object RocketParser : SmsParser {
    override val parserId: String = "rocket"

    private val incomingPattern = Pattern.compile(
        "Tk\\s*([0-9,]+(?:\\.[0-9]{1,2})?)\\s*received from.*?TxnId:\\s*([0-9A-Za-z]+)",
        Pattern.CASE_INSENSITIVE or Pattern.DOTALL
    )

    private val outgoingIndicators = listOf("cash out", "paid to", "failed", "reversed", "sent to")

    override fun canParse(sender: String, body: String): Boolean {
        val s = sender.uppercase()
        return ProviderProfiles.rocket.trustedSenders.any { s.contains(it.uppercase()) } ||
                body.contains("Rocket", ignoreCase = true)
    }

    override fun parse(sender: String, body: String): ParsedSmsResult {
        val s = sender.uppercase()
        val isSenderTrusted = ProviderProfiles.rocket.trustedSenders.any { s.contains(it.uppercase()) }

        val lowerBody = body.lowercase()
        if (outgoingIndicators.any { lowerBody.contains(it) }) {
            return ParsedSmsResult(
                provider = "rocket",
                status = "outgoing_filtered",
                isPaymentCandidate = false,
                senderMatch = isSenderTrusted,
                templateMatch = true,
                isOutgoingOrReversal = true,
                reviewReason = "Outgoing or reversal notification"
            )
        }

        val matcher = incomingPattern.matcher(body)
        if (matcher.find()) {
            val amount = matcher.group(1)?.replace(",", "")?.toDoubleOrNull()
            val txnId = matcher.group(2)

            val isCandidate = isSenderTrusted && amount != null && amount > 0 && !txnId.isNullOrBlank()

            return ParsedSmsResult(
                provider = "rocket",
                transactionId = txnId,
                amount = amount,
                currency = "BDT",
                status = if (isCandidate) "parsed_candidate" else if (!isSenderTrusted) "unknown_sender" else "needs_review",
                isPaymentCandidate = isCandidate,
                senderMatch = isSenderTrusted,
                templateMatch = true,
                isOutgoingOrReversal = false,
                reviewReason = if (!isSenderTrusted) "Untrusted Rocket sender" else null,
                confidence = if (isCandidate) 0.95f else 0.5f
            )
        }

        return ParsedSmsResult(
            provider = "rocket",
            status = "template_mismatch",
            isPaymentCandidate = false,
            senderMatch = isSenderTrusted,
            templateMatch = false
        )
    }
}

object UpayParser : SmsParser {
    override val parserId: String = "upay"

    private val incomingPattern = Pattern.compile(
        "Received Tk\\s*([0-9,]+(?:\\.[0-9]{1,2})?).*?Trx ID:\\s*([0-9A-Za-z]+)",
        Pattern.CASE_INSENSITIVE or Pattern.DOTALL
    )

    override fun canParse(sender: String, body: String): Boolean {
        val s = sender.uppercase()
        return ProviderProfiles.upay.trustedSenders.any { s.contains(it.uppercase()) } ||
                body.contains("upay", ignoreCase = true)
    }

    override fun parse(sender: String, body: String): ParsedSmsResult {
        val s = sender.uppercase()
        val isSenderTrusted = ProviderProfiles.upay.trustedSenders.any { s.contains(it.uppercase()) }

        val matcher = incomingPattern.matcher(body)
        if (matcher.find()) {
            val amount = matcher.group(1)?.replace(",", "")?.toDoubleOrNull()
            val trxId = matcher.group(2)

            val isCandidate = isSenderTrusted && amount != null && amount > 0 && !trxId.isNullOrBlank()

            return ParsedSmsResult(
                provider = "upay",
                transactionId = trxId,
                amount = amount,
                currency = "BDT",
                status = if (isCandidate) "parsed_candidate" else if (!isSenderTrusted) "unknown_sender" else "needs_review",
                isPaymentCandidate = isCandidate,
                senderMatch = isSenderTrusted,
                templateMatch = true,
                isOutgoingOrReversal = false,
                confidence = if (isCandidate) 0.95f else 0.5f
            )
        }

        return ParsedSmsResult(
            provider = "upay",
            status = "template_mismatch",
            isPaymentCandidate = false,
            senderMatch = isSenderTrusted,
            templateMatch = false
        )
    }
}

object BankAlertParser : SmsParser {
    override val parserId: String = "bank_alert"

    private val amountPattern = Pattern.compile("(?:credited|deposited|received)\\s*(?:with|by|of)?\\s*(?:USD|EUR|GBP|Tk|BDT|\\$|€|£)?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE)
    private val refPattern = Pattern.compile("(?:Ref|RefNo|Txn|Trx|ID)[:#\\s]+([A-Za-z0-9_-]+)", Pattern.CASE_INSENSITIVE)
    private val currencyPattern = Pattern.compile("(USD|EUR|GBP|BDT|Tk|\\$|€|£)", Pattern.CASE_INSENSITIVE)

    override fun canParse(sender: String, body: String): Boolean {
        val b = body.lowercase()
        return b.contains("credited") || b.contains("deposited") || b.contains("acct") || b.contains("a/c") || b.contains("card")
    }

    override fun parse(sender: String, body: String): ParsedSmsResult {
        var amount: Double? = null
        var refId: String? = null
        var currency: String? = null

        val amtMatcher = amountPattern.matcher(body)
        if (amtMatcher.find()) {
            amount = amtMatcher.group(1)?.replace(",", "")?.toDoubleOrNull()
        }

        val curMatcher = currencyPattern.matcher(body)
        if (curMatcher.find()) {
            val symbol = curMatcher.group(1)
            currency = when (symbol?.uppercase()) {
                "$" -> "USD"
                "€" -> "EUR"
                "£" -> "GBP"
                "TK", "BDT" -> "BDT"
                else -> symbol?.uppercase()
            }
        }

        val refMatcher = refPattern.matcher(body)
        if (refMatcher.find()) {
            refId = refMatcher.group(1)
        }

        val isCandidate = amount != null && amount > 0

        return ParsedSmsResult(
            provider = "bank",
            transactionId = refId,
            amount = amount,
            currency = currency,
            status = if (isCandidate) "parsed_candidate" else "needs_review",
            isPaymentCandidate = isCandidate,
            senderMatch = true,
            templateMatch = isCandidate,
            isOutgoingOrReversal = false,
            confidence = if (isCandidate) 0.85f else 0.4f
        )
    }
}

object GenericTransactionParser : SmsParser {
    override val parserId: String = "generic"

    private val amountPattern = Pattern.compile("(?:\\$|€|£|USD|EUR|GBP|BDT|Tk)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE)
    private val idPattern = Pattern.compile("(?:trx|txn|ref|tx_id|id)[\\s:#]+([A-Za-z0-9_-]{6,})", Pattern.CASE_INSENSITIVE)

    override fun canParse(sender: String, body: String): Boolean = true

    override fun parse(sender: String, body: String): ParsedSmsResult {
        var amount: Double? = null
        var trxId: String? = null
        var currency: String? = null

        val amtMatcher = amountPattern.matcher(body)
        if (amtMatcher.find()) {
            amount = amtMatcher.group(1)?.replace(",", "")?.toDoubleOrNull()
            currency = when {
                body.contains("$") || body.contains("USD", ignoreCase = true) -> "USD"
                body.contains("€") || body.contains("EUR", ignoreCase = true) -> "EUR"
                body.contains("£") || body.contains("GBP", ignoreCase = true) -> "GBP"
                body.contains("Tk", ignoreCase = true) || body.contains("BDT", ignoreCase = true) -> "BDT"
                else -> null
            }
        }

        val idMatcher = idPattern.matcher(body)
        if (idMatcher.find()) {
            trxId = idMatcher.group(1)
        }

        return ParsedSmsResult(
            provider = "generic",
            transactionId = trxId,
            amount = amount,
            currency = currency,
            status = if (trxId != null && amount != null) "parsed_candidate" else "needs_review",
            isPaymentCandidate = trxId != null && amount != null,
            senderMatch = false,
            templateMatch = false,
            reviewReason = if (trxId == null) "transaction_reference_missing" else null,
            confidence = if (amount != null && trxId != null) 0.60f else 0.2f
        )
    }
}

object ParserRegistry {
    private val parsers = listOf(
        BKashParser,
        NagadParser,
        RocketParser,
        UpayParser,
        BankAlertParser
    )

    fun parseMessage(sender: String, body: String): ParsedSmsResult {
        for (parser in parsers) {
            if (parser.canParse(sender, body)) {
                return parser.parse(sender, body)
            }
        }
        return GenericTransactionParser.parse(sender, body)
    }
}

package com.example

import com.example.domain.model.WisfyPayload
import com.example.domain.parser.ParserRegistry
import com.example.domain.privacy.PrivacyFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WisfyLogicTest {

    @Test
    fun testBKashValidIncomingCandidate() {
        val sender = "bKash"
        val body = "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. Balance Tk 15,240.50. TrxID 9K8A7L6M2P at 31/10/2026"
        val parsed = ParserRegistry.parseMessage(sender, body)

        assertEquals("bkash", parsed.provider)
        assertEquals(2500.0, parsed.amount ?: 0.0, 0.001)
        assertEquals("9K8A7L6M2P", parsed.transactionId)
        assertEquals("BDT", parsed.currency)
        assertEquals("parsed_candidate", parsed.status)
        assertTrue(parsed.isPaymentCandidate)
        assertTrue(parsed.senderMatch)
        assertFalse(parsed.isOutgoingOrReversal)
    }

    @Test
    fun testBKashOutgoingCashOutIsFiltered() {
        val sender = "bKash"
        val body = "Cash Out Tk 500.00 to 01712345678. Fee Tk 7.50. Balance Tk 14,733.00. TrxID 9K8A7L6M2Q"
        val parsed = ParserRegistry.parseMessage(sender, body)

        assertEquals("bkash", parsed.provider)
        assertEquals("outgoing_filtered", parsed.status)
        assertFalse(parsed.isPaymentCandidate)
        assertTrue(parsed.isOutgoingOrReversal)
    }

    @Test
    fun testUntrustedSenderCannotBeCandidate() {
        val sender = "RandomNumber017"
        val body = "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. TrxID 9K8A7L6M2P"
        val parsed = ParserRegistry.parseMessage(sender, body)

        assertFalse(parsed.senderMatch)
        assertFalse(parsed.isPaymentCandidate)
        assertEquals("unknown_sender", parsed.status)
    }

    @Test
    fun testNagadValidIncomingCandidate() {
        val sender = "NAGAD"
        val body = "Money received. Amount: Tk 1,200.00 from 01987654321. TxnID: 7X89QPL2. Balance: Tk 6,400.00"
        val parsed = ParserRegistry.parseMessage(sender, body)

        assertEquals("nagad", parsed.provider)
        assertEquals(1200.0, parsed.amount ?: 0.0, 0.001)
        assertEquals("7X89QPL2", parsed.transactionId)
        assertEquals("BDT", parsed.currency)
        assertEquals("parsed_candidate", parsed.status)
        assertTrue(parsed.isPaymentCandidate)
    }

    @Test
    fun testRocketValidIncomingCandidate() {
        val sender = "Rocket"
        val body = "Tk 3,000.00 received from 01812345678. TxnId: 8A99LKJ1. Balance Tk 8,500.00"
        val parsed = ParserRegistry.parseMessage(sender, body)

        assertEquals("rocket", parsed.provider)
        assertEquals(3000.0, parsed.amount ?: 0.0, 0.001)
        assertEquals("8A99LKJ1", parsed.transactionId)
        assertEquals("BDT", parsed.currency)
        assertEquals("parsed_candidate", parsed.status)
        assertTrue(parsed.isPaymentCandidate)
    }

    @Test
    fun testPrivacyFilterDetectsOtp() {
        val otpBody = "Your verification code is 849201. Do not share this with anyone."
        assertTrue(PrivacyFilter.isOtpOrAuthCode("GoogleAuth", otpBody))

        val regularBody = "Meeting at 5pm today at the office."
        assertFalse(PrivacyFilter.isOtpOrAuthCode("Alice", regularBody))
    }

    @Test
    fun testMaskBody() {
        val secret = "Secret message text with sensitive details"
        val masked = PrivacyFilter.maskBody(secret)
        assertTrue(masked.contains("***"))
        assertEquals("Secr", masked.take(4))
        assertEquals("ails", masked.takeLast(4))
    }

    @Test
    fun testWisfySchema11PayloadSerialization() {
        val payload = WisfyPayload.createSyntheticTest("device_test_123")
        val json = payload.toJsonString()

        assertNotNull(json)
        assertTrue(json.contains("\"schema_version\": \"1.1\""))
        assertTrue(json.contains("\"deduplication\":"))
        assertTrue(json.contains("\"payment_candidate\":"))
        assertTrue(json.contains("\"server_verified\": false"))
        assertTrue(json.contains("\"idempotency_key\":"))
    }
}

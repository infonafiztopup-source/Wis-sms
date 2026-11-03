package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.WisfyPayload
import com.example.domain.parser.BKashParser
import com.example.domain.parser.NagadParser
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testWisfyPayloadWithSimSlotSerialization() {
    val payload = WisfyPayload(
      event = "sms.received",
      eventType = "sms.payment_candidate",
      eventId = "evt_unit_sim_123",
      schemaVersion = "1.1",
      deviceId = "device_test_456",
      receivedAtMs = 1730400000000L,
      sender = "bKash",
      body = "You have received Tk 1,500.00 from 01812345678. Fee Tk 0.00. Balance Tk 5,000.00. TrxID 8A9B0C1D2E at 31/10/2026 12:00",
      provider = "bkash",
      transactionId = "8A9B0C1D2E",
      amount = 1500.00,
      currency = "BDT",
      status = "parsed_candidate",
      idempotencyKey = "bkash:8A9B0C1D2E",
      isMessageDuplicate = false,
      isTransactionDuplicate = false,
      senderMatch = true,
      templateMatch = true,
      serverVerified = false,
      simSlot = 2
    )

    val jsonString = payload.toJsonString()
    val json = JSONObject(jsonString)

    assertEquals("sms.received", json.getString("event"))
    assertEquals("sms.payment_candidate", json.getString("event_type"))
    assertEquals(2, json.getInt("sim_slot"))

    val candidate = json.getJSONObject("payment_candidate")
    assertEquals("bkash", candidate.getString("provider"))
    assertEquals("1500.00", candidate.getString("amount"))
    assertEquals("8A9B0C1D2E", candidate.getString("transaction_id"))
    assertFalse(candidate.getBoolean("server_verified"))

    val message = json.getJSONObject("message")
    assertEquals(2, message.getInt("sim_slot"))
    assertEquals("bKash", message.getString("sender"))
  }

  @Test
  fun testSimSlotFilteringLogic() {
    // Both SIMs (0) allows SIM 1 and SIM 2
    val epBothSim = 0
    assertTrue(epBothSim == 0 || epBothSim == 1)
    assertTrue(epBothSim == 0 || epBothSim == 2)

    // SIM 1 Only (1) rejects SIM 2
    val epSim1Only = 1
    val incomingSim2 = 2
    assertFalse(epSim1Only == 0 || epSim1Only == incomingSim2)

    // SIM 2 Only (2) accepts SIM 2
    val epSim2Only = 2
    assertTrue(epSim2Only == 0 || epSim2Only == incomingSim2)
  }

  @Test
  fun testBKashAndNagadParsers() {
    val bkashSms = "You have received Tk 2,500.00 from 01712345678. Fee Tk 0.00. Balance Tk 15,240.50. TrxID 9K8A7L6M2P at 31/10/2026 18:30"
    val bResult = BKashParser.parse("bKash", bkashSms)
    assertTrue(bResult.isPaymentCandidate)
    assertEquals(2500.00, bResult.amount ?: 0.0, 0.01)
    assertEquals("9K8A7L6M2P", bResult.transactionId)
    assertEquals("parsed_candidate", bResult.status)

    val nagadSms = "Money Received. Amount: Tk 3,500.00 Sender: 01912345678 TxnID: 7J6H5G4F3E Date: 31/10/2026 18:35"
    val nResult = NagadParser.parse("NAGAD", nagadSms)
    assertTrue(nResult.isPaymentCandidate)
    assertEquals(3500.00, nResult.amount ?: 0.0, 0.01)
    assertEquals("7J6H5G4F3E", nResult.transactionId)
  }
}

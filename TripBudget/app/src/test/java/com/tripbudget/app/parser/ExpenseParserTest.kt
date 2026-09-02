package com.tripbudget.app.parser

import com.tripbudget.app.data.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseParserTest {

    @Test
    fun `parses description dash amount and word currency`() {
        val result = ExpenseParser.parse("coffee - 5 euro", fallbackCurrencyCode = "USD")
        requireNotNull(result)
        assertEquals("Coffee", result.description)
        assertEquals(500L, result.amountMinorUnits)
        assertEquals("EUR", result.currencyCode)
        assertEquals(Category.FOOD, result.category)
        assertTrue(result.confident)
    }

    @Test
    fun `parses currency symbol before amount`() {
        val result = ExpenseParser.parse("taxi to airport €12.50", fallbackCurrencyCode = "USD")
        requireNotNull(result)
        assertEquals("Taxi to airport", result.description)
        assertEquals(1250L, result.amountMinorUnits)
        assertEquals("EUR", result.currencyCode)
        assertEquals(Category.TRANSPORT, result.category)
    }

    @Test
    fun `falls back to trip currency when none is mentioned`() {
        val result = ExpenseParser.parse("hotel 90", fallbackCurrencyCode = "GBP")
        requireNotNull(result)
        assertEquals(9000L, result.amountMinorUnits)
        assertEquals("GBP", result.currencyCode)
        assertEquals(Category.STAYS, result.category)
        assertTrue(!result.confident)
    }

    @Test
    fun `unrecognized category falls back to other`() {
        val result = ExpenseParser.parse("souvenir magnet 3.50", fallbackCurrencyCode = "EUR")
        requireNotNull(result)
        assertEquals(Category.OTHER, result.category)
    }

    @Test
    fun `no amount returns null so the UI can't save garbage`() {
        assertNull(ExpenseParser.parse("dinner with friends", fallbackCurrencyCode = "EUR"))
    }

    @Test
    fun `blank input returns null`() {
        assertNull(ExpenseParser.parse("   ", fallbackCurrencyCode = "EUR"))
    }
}

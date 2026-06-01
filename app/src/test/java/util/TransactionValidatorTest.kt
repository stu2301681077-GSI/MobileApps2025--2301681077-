package com.example.kasichka.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionValidatorTest {

    @Test
    fun parseAmount_returnsAmount_whenInputIsValid() {
        val result = TransactionValidator.parseAmount("50.25")

        assertEquals(50.25, result ?: 0.0, 0.001)
    }

    @Test
    fun parseAmount_acceptsCommaAsDecimalSeparator() {
        val result = TransactionValidator.parseAmount("25,50")

        assertEquals(25.50, result ?: 0.0, 0.001)
    }

    @Test
    fun parseAmount_returnsNull_whenInputIsEmpty() {
        val result = TransactionValidator.parseAmount("")

        assertNull(result)
    }

    @Test
    fun parseAmount_returnsNull_whenInputIsZero() {
        val result = TransactionValidator.parseAmount("0")

        assertNull(result)
    }

    @Test
    fun parseAmount_returnsNull_whenInputIsNegative() {
        val result = TransactionValidator.parseAmount("-10")

        assertNull(result)
    }

    @Test
    fun parseAmount_returnsNull_whenInputIsText() {
        val result = TransactionValidator.parseAmount("abc")

        assertNull(result)
    }

    @Test
    fun isRequiredTextValid_returnsTrue_whenTextIsNotEmpty() {
        val result = TransactionValidator.isRequiredTextValid("Храна")

        assertTrue(result)
    }

    @Test
    fun isRequiredTextValid_returnsFalse_whenTextIsBlank() {
        val result = TransactionValidator.isRequiredTextValid("   ")

        assertFalse(result)
    }
}


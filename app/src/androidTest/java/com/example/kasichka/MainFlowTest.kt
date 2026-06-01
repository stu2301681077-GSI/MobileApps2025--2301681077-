package com.example.kasichka

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class MainFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun addExpenseTransaction_displaysTransactionInList() {
        onView(withId(R.id.addTransactionButton))
            .perform(click())

        onView(withId(R.id.amountEditText))
            .perform(replaceText("12.50"), closeSoftKeyboard())

        onView(withId(R.id.expenseRadioButton))
            .perform(click())

        onView(withId(R.id.categoryEditText))
            .perform(replaceText("Test Category"), closeSoftKeyboard())

        onView(withId(R.id.descriptionEditText))
            .perform(replaceText("Espresso Test Expense"), closeSoftKeyboard())

        onView(withId(R.id.noteEditText))
            .perform(replaceText("Created by UI test"), closeSoftKeyboard())

        onView(withId(R.id.saveButton))
            .perform(click())

        onView(withId(R.id.transactionListFragment))
            .perform(click())

        onView(withText("Espresso Test Expense"))
            .check(matches(withText("Espresso Test Expense")))
    }
}


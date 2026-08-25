package com.udacity.project.spire

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testBottomNavigation() {
        // Initial state: Buildings should be visible (check for recycler view)
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))

        // Click on My Visits
        onView(withId(R.id.myVisitsFragment)).perform(click())
        // In MyVisitsFragment, check for "Visited" chip.
        onView(withText("Visited")).check(matches(isDisplayed()))

        // Click on Statistics
        onView(withId(R.id.statisticsFragment)).perform(click())
        onView(withText("Total Buildings")).check(matches(isDisplayed()))
        
        // Click back to Buildings
        onView(withId(R.id.buildingsFragment)).perform(click())
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
    }
}

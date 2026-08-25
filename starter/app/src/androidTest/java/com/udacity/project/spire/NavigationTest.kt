package com.udacity.project.spire

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testBottomNavigation() {
        // Wait for the app to settle and avoid InjectEventSecurityException
        Thread.sleep(2000)

        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))

        onView(allOf(withId(R.id.myVisitsFragment), isDisplayed())).perform(click())
        
        Thread.sleep(1000)
        
        onView(withText("Visited")).check(matches(isDisplayed()))

        onView(allOf(withId(R.id.statisticsFragment), isDisplayed())).perform(click())
        
        // Wait for fragment transition
        Thread.sleep(1000)
        
        onView(withText("Total Buildings")).check(matches(isDisplayed()))
        
        onView(allOf(withId(R.id.buildingsFragment), isDisplayed())).perform(click())
        
        // Wait for fragment transition
        Thread.sleep(1000)

        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
    }
}

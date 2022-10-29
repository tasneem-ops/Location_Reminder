package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.ToastMatcher
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeAndroidDataSource
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class ReminderListFragmentTest {
    private lateinit var repository :ReminderDataSource
    @Before
    fun setUp() {
        stopKoin()
        val testModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                RemindersListViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            single { FakeAndroidDataSource() as ReminderDataSource }
            //single { LocalDB.createRemindersDao(ApplicationProvider.getApplicationContext()) }
        }
        startKoin{
            androidContext(ApplicationProvider.getApplicationContext())
            modules(testModule)
        }
        repository = GlobalContext.get().get()

    }

    @After
    fun clear(){
        stopKoin()
    }

    @Test
    fun NoData_DisplayedInUI(){
        //GIVEN -No data

        //WHEN - reminder list fragment is launched
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        //THEN - text view is displayed with "No Data" text
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches(withText("No Data")))

    }

    @Test
    fun reminder_DisplayedInRecyclerView() = runBlockingTest {
        //GIVEN - Reminder in database
        val reminder = ReminderDTO("Say Hi to friends", "", "Sydney", -33.865143, 151.209900)
        repository.saveReminder(reminder)

        //WHEN - reminderList fragment is launched
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        //THEN - The reminder is shown in screen and noDataText is not displayed
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.title)).check(matches(withText(reminder.title)))
        onView(withId(R.id.description)).check(matches(withText(reminder.description)))
        onView(withId(R.id.reminder_location)).check(matches(withText(reminder.location)))
    }

    @Test
    fun onAddReminderFABClicked_navigateToSaveReminderFragment(){
        //GIVEN - on reminder list fragment
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment{ Navigation.setViewNavController(it.view!!, navController) }

        //WHEN - AddReminder FAB is clicked
        onView(withId(R.id.addReminderFAB)).perform(click())

        //THEN - navigate to save reminder fragment
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )

    }

    @Test
    fun onAddReminderFABClicked_showToast(){
        //GIVEN - on reminder list fragment
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment{ Navigation.setViewNavController(it.view!!, navController) }

        //WHEN - AddReminder FAB is clicked
        onView(withId(R.id.addReminderFAB)).perform(click())

        //THEN - show toast
        onView(withText("Enter Reminder data")).inRoot(ToastMatcher()).check(matches(isDisplayed()))


    }

}
package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.FakeAndroidDataSource
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.*
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SaveReminderFragmentTest{
    //private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var repository :ReminderDataSource
    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
//    @Before
//    fun registerIdlingResource() {
//        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
//        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
//    }
//
//    /**
//     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
//     */
//    @After
//    fun unregisterIdlingResource() {
//        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
//        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
//    }
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
    fun onSelectLocationButtonClicked_navigateToSelectLocationFragment(){
        //GIVEN - on reminder list fragment
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment{ Navigation.setViewNavController(it.view!!, navController) }

        //WHEN - AddReminder FAB is clicked
        onView(withId(R.id.selectLocation)).perform(ViewActions.click())

        //THEN - navigate to save reminder fragment
        verify(navController).navigate(
            SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment()
        )

    }

    @Test
    fun onSaveReminderFABClicked_missingTitleReminder_ShowSnakbar(){
        //GIVEN - on save reminder screen with null title
        launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        //WHEN - save reminder button is clicked
        onView(withId(R.id.saveReminder)).perform(ViewActions.click())

        //THEN - snack bar is shown with text "Please enter title"
        onView(withText("Please enter title")).check(matches(isDisplayed()))
    }
    @Test
    fun onSaveReminderFABClicked_missingLocationReminder_ShowSnakbar(){
        //GIVEN - on save reminder screen with null location
        launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.reminderTitle)).perform(replaceText("Visit my sister"))
        //WHEN - save reminder button is clicked
        onView(withId(R.id.saveReminder)).perform(ViewActions.click())

        //THEN - snack bar is shown with text "Please select location"
        onView(withText("Please select location")).check(matches(isDisplayed()))
    }

}
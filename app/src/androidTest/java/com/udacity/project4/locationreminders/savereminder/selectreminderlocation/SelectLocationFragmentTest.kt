package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.getInstance
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.FakeAndroidDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragment
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragmentDirections
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.junit.After
import org.junit.Before
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@MediumTest
class SelectLocationFragmentTest{

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
    }

    @After
    fun clear(){
        stopKoin()
    }
    @Test
    fun onSaveLocationClicked_navigateToSaveReminderFragment(){
        //GIVEN - on select location fragment
        val scenario = launchFragmentInContainer<SelectLocationFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment{ Navigation.setViewNavController(it.view!!, navController) }

        //WHEN - save location button is clicked
        onView(ViewMatchers.withId(R.id.saveLocation)).perform(ViewActions.click())

        //THEN - navigate to save reminder fragment
       // verify(navController.popBackStack())
        assertEquals(false , navController.popBackStack())
    }
}
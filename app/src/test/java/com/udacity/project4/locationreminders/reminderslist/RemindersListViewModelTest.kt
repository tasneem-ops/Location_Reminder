package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersListViewModelTest{
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUpViewModel() {
        stopKoin()
        stopKoin() // to remove 'A Koin Application has already been started'
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules()
        }
        dataSource = FakeDataSource()
        val reminder_1 = ReminderDTO("Eat", "At an Asian restaurant", "Sydney", -33.865143, 151.209900)
        val reminder_2 = ReminderDTO("Say Hello to friends", " ", "Sydney", -33.865143, 151.209900)
        dataSource.addReminders(reminder_1, reminder_2)
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext() , dataSource)
    }
    @After
    fun clean(){
        stopKoin()
    }

    @Test
    fun loadReminders_showsErrorSnackBar(){
        //GIVEN - shouldReturnError is true
        dataSource.setReturnError(true)

        //WHEN - loadReminders function is called
        remindersListViewModel.loadReminders()

        //THEN - result should return error "Test exception"
        assertEquals("Test exception", remindersListViewModel.showSnackBar.getOrAwaitValue())
    }

}
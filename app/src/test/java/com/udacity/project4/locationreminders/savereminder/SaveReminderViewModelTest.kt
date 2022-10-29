package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
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
class SaveReminderViewModelTest{
    private lateinit var saveReminderViewModel: SaveReminderViewModel
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
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext() , dataSource)
    }
    @After
    fun clean(){
        stopKoin()
    }


    @Test
    fun validateEnteredData_validData_returnsTrue(){
        //GIVEN - reminder with valid title and location
        val data = ReminderDataItem("Eat", "At an Asian restaurant", "Sydney", -33.865143, 151.209900)

        //WHEN - validateEnteredData function is called
        val returnValue = saveReminderViewModel.validateEnteredData(data)
        //THEN - return value should be true
        assertEquals(true, returnValue)
    }

    @Test
    fun onClear_LiveDataObjectsNull(){
        //WHEN - onClear() function is called
        saveReminderViewModel.onClear()

        //THEN - livedata objects values equal null
        assertEquals(null, saveReminderViewModel.reminderTitle.getOrAwaitValue())
        assertEquals(null, saveReminderViewModel.reminderDescription.getOrAwaitValue())
        assertEquals(null, saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue())
        assertEquals(null, saveReminderViewModel.selectedPOI.getOrAwaitValue())
        assertEquals(null, saveReminderViewModel.latitude.getOrAwaitValue())
        assertEquals(null, saveReminderViewModel.longitude.getOrAwaitValue())

    }


    @Test
    fun saveReminder_ReminderData_savesRemindertoDB() = mainCoroutineRule.runBlockingTest{
        //GIVEN - reminder with valid data
        val reminder = ReminderDataItem("Eat", "At an Asian restaurant", "Sydney", -33.865143, 151.209900)

        //WHEN - saveReminder function is called
        saveReminderViewModel.saveReminder(reminder)

        //THEN - reminder is saved in DB
        val result = dataSource.getReminder(reminder.id) as Result.Success
        assertEquals(reminder.title , result.data.title)
        assertEquals(reminder.description , result.data.description)
        assertEquals(reminder.location , result.data.location)
        assertEquals(reminder.latitude , result.data.latitude)
        assertEquals(reminder.longitude , result.data.longitude)

    }
    @Test
    fun saveRemindersLoading_ShowLoading(){
        //Given - a remider to be saved in repository
        val reminder = ReminderDataItem("Eat", "At an Asian restaurant", "Sydney", -33.865143, 151.209900)
        //When - reminder is not saved yet
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminder)
        //Then - showLoading should be true
        assertEquals(true , saveReminderViewModel.showLoading.getOrAwaitValue())
        //When - dispatcher is resumed and reminder saved
        mainCoroutineRule.resumeDispatcher()
        //Then - showLoading should be false
        assertEquals(false, saveReminderViewModel.showLoading.getOrAwaitValue())
    }
}
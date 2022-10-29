package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest{

    private lateinit var localRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localRepository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDb() = database.close()
    @Test
    fun insertReminderandGetById() = runTest{
        // GIVEN - Insert a reminder
        val reminder = ReminderDTO(
            "Say Hello to friends",
            " ",
            "Sydney",
            -33.865143,
            151.209900)
        localRepository.saveReminder(reminder)

        // WHEN - Get the reminder by id from the database.
        val result = localRepository.getReminder(reminder.id)

        //THEN - The loaded data contains the expected values
        assertEquals(true , localRepository.succeeded)
        result as Result.Success
        assertEquals(result.data.title, reminder.title)
        assertEquals(result.data.description, reminder.description)
        assertEquals(result.data.location, reminder.location)
        assertEquals(result.data.latitude, reminder.latitude)
        assertEquals(result.data.longitude, reminder.longitude)

    }
    @Test
    fun reminderNotFound_returnError() = runTest{
        //Given - No data in the database
        localRepository.deleteAllReminders()

        //When - getReminder function is called and reminder not found
        val result = localRepository.getReminder("x")

        //Then - it should return error
        assertEquals(Result.Error("Reminder not found!"), result)
    }
}
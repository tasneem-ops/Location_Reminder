package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {
    private lateinit var database : RemindersDatabase
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderandGetById() = runBlockingTest{
        // GIVEN - Insert a reminder
        val reminder = ReminderDTO(
            "Say Hello to friends",
            " ",
            "Sydney",
            -33.865143,
            151.209900)
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the reminder by id from the database.
        val loadedReminder = database.reminderDao().getReminderById(reminder.id)

        //THEN - The loaded data contains the expected values
        assertThat(loadedReminder as ReminderDTO, notNullValue())
        assertEquals(loadedReminder.title, reminder.title)
        assertEquals(loadedReminder.description, reminder.description)
        assertEquals(loadedReminder.location, reminder.location)
        assertEquals(loadedReminder.latitude, reminder.latitude)
        assertEquals(loadedReminder.longitude, reminder.longitude)

    }

}
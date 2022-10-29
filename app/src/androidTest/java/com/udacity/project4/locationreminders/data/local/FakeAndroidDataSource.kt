package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


class FakeAndroidDataSource : ReminderDataSource {
    var remindersServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    var shouldReturnError : Boolean = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError){
            return Result.Error("Test exception")
        }
        return Result.Success(remindersServiceData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError){
            return Result.Error("Test exception")
        }
        if (remindersServiceData.get(id) != null){
            return Result.Success(remindersServiceData.get(id)!!)
        }
        return Result.Error("Couldn't Find Reminder")
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.values.removeAll(remindersServiceData.values)
    }

    fun addReminders(vararg reminders : ReminderDTO) {
        for (reminder in reminders) {
            remindersServiceData[reminder.id] = reminder
        }
    }

    fun addReminder(reminder: ReminderDTO){
        remindersServiceData.values.add(reminder)
    }
}
package com.udacity.project4.locationreminders

import android.os.IBinder
import android.view.WindowManager
import androidx.test.espresso.Root
import org.junit.internal.matchers.TypeSafeMatcher
import org.junit.runner.Description

class ToastMatcher : TypeSafeMatcher<Root>() {

    public override fun matchesSafely(root: Root): Boolean {
        val type: Int = root.getWindowLayoutParams().get().type
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            val windowToken: IBinder = root.getDecorView().getWindowToken()
            val appToken: IBinder = root.getDecorView().getApplicationWindowToken()
            if (windowToken === appToken) {
                // windowToken == appToken means this window isn't contained by any other windows.
                // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                return true
            }
        }
        return false
    }

    override fun describeTo(description: org.hamcrest.Description?) {
        description?.appendText("is toast")    }
}
package com.udacity.project4.locationreminders.geofence

import com.google.android.gms.location.Geofence.NEVER_EXPIRE


internal object GeofencingConstants {
    //set Geofence to never expire
    val GEOFENCE_EXPIRATION: Long = NEVER_EXPIRE
    const val GEOFENCE_RADIUS_IN_METERS = 100f
}


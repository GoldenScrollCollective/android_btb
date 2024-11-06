package org.lemonadestand.btb

import android.app.Application

class App : Application() {
    companion object {
        lateinit var instance: App
    }

//    override fun onCreate() {
//        super.onCreate()
//
//        // Obtain the FCM token
//        getFcmToken()
//    }
//
//    private fun getFcmToken() {
//        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
//
//        }
//        FirebaseInstanceId.getInstance().instanceId
//            .addOnSuccessListener { instanceIdResult: InstanceIdResult ->
//                val token: String = instanceIdResult.token
//                // Use or send the FCM token as needed
//                Log.d("FCM Token", token)
//            }
//            .addOnFailureListener { exception ->
//                // Handle the failure to obtain the FCM token
//                Log.e("FCM Token", "Error getting FCM token", exception)
//            }
//    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
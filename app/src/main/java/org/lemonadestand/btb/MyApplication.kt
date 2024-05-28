package org.lemonadestand.btb

import android.app.Application

import android.util.Log
//import com.google.firebase.iid.FirebaseInstanceId
//import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging

class MyApplication : Application() {

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
}
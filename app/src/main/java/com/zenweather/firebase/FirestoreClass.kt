package com.zenweather.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.zenweather.activities.MainActivity
import com.zenweather.activities.MyProfileActivity
import com.zenweather.activities.SignInActivity
import com.zenweather.activities.SignUpActivity
import com.zenweather.model.User
import com.zenweather.utils.Constants


class FirestoreClass {

    // Create a instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun registerGoogleUser(activity: SignInActivity, userInfo: User) {

        try{
            var createDoc = true


            mFireStore.collection(Constants.USERS)
                // The document id to get the Fields of user.
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        createDoc = false
                        Log.d("rrr1", createDoc.toString())
                        val loggedInUser = document.toObject(User::class.java)!!
                        activity.signInSuccess(loggedInUser)
                    }
                    else{
                        mFireStore.collection(Constants.USERS)
                            // Document ID for users fields. Here the document it is the User ID.
                            .document(getCurrentUserID())
                            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                            .set(userInfo, SetOptions.merge())
                            .addOnSuccessListener {
                                activity.signInSuccess(userInfo)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Here call a function of base activity for transferring the result to it.
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while getting loggedIn user details",
                        e
                    )
                }
//            Log.d("rrr", createDoc.toString())
//            if(createDoc){
//                mFireStore.collection(Constants.USERS)
//                    // Document ID for users fields. Here the document it is the User ID.
//                    .document(getCurrentUserID())
//                    // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
//                    .set(userInfo, SetOptions.merge())
//                    .addOnSuccessListener {
//                        activity.signInSuccess(userInfo)
//                    }
//                    .addOnFailureListener { e ->
//                        activity.hideProgressDialog()
//                        Log.e(
//                            activity.javaClass.simpleName,
//                            "Error writing document",
//                            e
//                        )
//                    }
//                    .addOnFailureListener { e ->
//                        // Here call a function of base activity for transferring the result to it.
//                        activity.hideProgressDialog()
//
//                        Log.e(
//                            activity.javaClass.simpleName,
//                            "Error while getting loggedIn user details",
//                            e
//                        )
//                    }
//            }
        }
        catch (_:java.lang.Exception){

        }


    }


    fun loadUserData(activity: Activity) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!

                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }


    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS) // Collection Name
            .document(getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                // Notify the success result.
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }


    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
}
package com.example.socialapp.daos

import com.example.socialapp.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//Its work is to putting user's  data in user k database
class UserDao {

    val db = FirebaseFirestore.getInstance()               //instance of database
    val userCollection = db.collection("users")

    fun addUser( user : User?){

        //we don't want to do it on mainThread thats why using Couroutines
        user?.let {
            GlobalScope.launch(Dispatchers.IO){
                userCollection.document(user.uid).set(it)
            }
        }
    }

    fun  getUserById(uId : String) : Task<DocumentSnapshot>{
        return userCollection.document(uId).get()                                      //all information of user from its Id //it is aynchronous
    }
}

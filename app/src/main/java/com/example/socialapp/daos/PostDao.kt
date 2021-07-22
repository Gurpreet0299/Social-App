package com.example.socialapp.daos

import com.example.socialapp.models.Post
import com.example.socialapp.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {

    val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("Post")                           //"post" collection will be created on its own in firebase

    val auth = Firebase.auth                    //for finding current user

    fun addPost(text : String){
        val currentUserId = auth.currentUser!!.uid       //app will crash if we didn't have any id logged in

        //Using corountines
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!    //this is returning task and we need object of User class we use toObject
            val currentTime = System.currentTimeMillis()    //return current time
            val post = Post(text ,user ,currentTime)

            postCollection.document().set(post)
        }
    }
}
package com.example.socialapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socialapp.daos.PostDao
import com.example.socialapp.daos.UserDao
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_main.*

class CreatePostActivity : AppCompatActivity() {
    lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        postDao = PostDao()
        postbtn.setOnClickListener {
            val input = postInput.text.toString().trim()
            if(input.isNotEmpty()){
               postDao.addPost(input)
                finish()                                                       //it'll close the current actvity and will go back
            }
        }


    }
}
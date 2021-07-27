package com.example.socialapp

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.Utils.Companion.getTimeAgo
import com.example.socialapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PostAdapter(options: FirestoreRecyclerOptions<Post>) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(options) {

    class PostViewHolder(itemView : View) :RecyclerView.ViewHolder(itemView){

        val postText :TextView = itemView.findViewById(R.id.postTitle)
        val userText :TextView = itemView.findViewById(R.id.userName)
        val createdAt :TextView = itemView.findViewById(R.id.createdAt)
        val likeCount :TextView = itemView.findViewById(R.id.likeCount)
        val userImage : ImageView = itemView.findViewById(R.id.userImage)
        val likeButton :ImageView = itemView.findViewById(R.id.likeButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {

        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName                               //createdBy is a object of user class
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedBy.size.toString()                            //likeby is an array stores ids of users liked the post
        holder.createdAt.text = getTimeAgo(model.createdAt)                                //return required text and millisecond long value given to it
    }
}
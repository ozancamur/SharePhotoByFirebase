package com.ozancamur.sharephotosbyfirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ozancamur.sharephotosbyfirebase.R
import com.ozancamur.sharephotosbyfirebase.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview.view.*

class FeedRecyclerAdapter(val postList : ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder (itemView : View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyclerview,parent,false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.recycler_row_user_email.text = postList[position].userEmail
        holder.itemView.recycler_row_user_explanation.text = postList[position].userExplanation


        // picasso android
        Picasso.get().load(postList[position].imagerURL).into(holder.itemView.recycler_row_imageview)


    }

    override fun getItemCount(): Int {
        return postList.size
    }


}
package com.example.brzodolokacije.Adapters

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Activities.ProfileVisitActivity
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Posts.Follower
import com.example.brzodolokacije.Posts.Like
import com.example.brzodolokacije.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FollowersAdapter(val followersList : List<Follower>, val context : Context) :
    RecyclerView.Adapter<FollowersAdapter.MainViewHolder>() {

    var dataList = followersList

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(follower: Follower, index: Int) {

            val followerPfp = itemView.findViewById<CircleImageView>(R.id.followerProfilePhoto)
            val followerUsername = itemView.findViewById<TextView>(R.id.followerUsername)
            val wholeFollower = itemView.findViewById<LinearLayout>(R.id.followerLinLayout)

            Picasso.get().load(Constants.BASE_URL + "User/avatar/" + follower.follower).into(followerPfp)
            followerUsername.text=follower.follower

            wholeFollower.setOnClickListener{
                val intent = Intent(context, ProfileVisitActivity::class.java)
                intent.putExtra("visit",follower.follower)
                intent.putExtra("backToProfile", "returnToProfile")
                Handler(Looper.getMainLooper()).postDelayed({
                    context.startActivity(intent)
                }, 30)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowersAdapter.MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_follower, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

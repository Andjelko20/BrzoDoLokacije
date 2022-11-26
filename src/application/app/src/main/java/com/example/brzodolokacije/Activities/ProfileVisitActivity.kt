package com.example.brzodolokacije.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Fragments2.LocationsFragment
import com.example.brzodolokacije.Fragments2.PostsFragment
import com.example.brzodolokacije.Fragments2.ProfileVisitPostsFragment
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.ModelsDto.ResetPasswordDto
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Models.Validation
import com.example.brzodolokacije.Posts.VisitUserProfile
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.ak1.BubbleTabBar
import io.ak1.OnBubbleClickListener
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_resetpassword.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileVisitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile_visit)

        replaceFragmentOnProfile(PostsFragment())

        val bubbleTabBarProfileVisit = findViewById<BubbleTabBar>(R.id.bubbleTabBarProfileProfileVisit)
        bubbleTabBarProfileVisit.addBubbleListener(object : OnBubbleClickListener {
            override fun onBubbleClick(id: Int) {
                when(id){
                    R.id.posts -> replaceFragmentOnProfile(ProfileVisitPostsFragment())
                    R.id.visitedLocations -> replaceFragmentOnProfile(LocationsFragment()) //napraviti poseban fragment gde ce se prikazivati lokacije korisnika (zavisi od implementacije Location fragmenta)

                    else -> {}
                }

            }
        })

        val retrofit = Client(this).buildService(Api::class.java)
        val sessionManager = SessionManager(this)
        if (sessionManager != null && VisitUserProfile.getVisit()!=""){
            val appUser=sessionManager.fetchUsername()
            val username=VisitUserProfile.getVisit()
            retrofit.fetchUserProfileInfo(username).enqueue(object: Callback<DefaultResponse>
            {
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false") {
                        //                    Log.d(response.body()?.error.toString(), response.body()?.message.toString());
                        val userProfileInfoStr: String = response.body()?.message.toString();
                        val gson = Gson()
                        val userProfileInfo: UserProfile = gson.fromJson(userProfileInfoStr, UserProfile::class.java)

                        val user = findViewById<TextView>(R.id.usernameProfileVisit)
                        val postsNum = findViewById<TextView>(R.id.postsNumProfileVisit)
                        val followersNum = findViewById<TextView>(R.id.followersNumProfileVisit)
                        val likesNum = findViewById<TextView>(R.id.likesNumProfileVisit)
                        val imeprezime = findViewById<TextView>(R.id.imeprezimeProfileVisit)
                        val opis = findViewById<TextView>(R.id.opisProfileVisit)
                        val pfp = findViewById<CircleImageView>(R.id.profilePictureProfileVisit)
                        val follow = findViewById<Button>(R.id.followBtnProfileVisit)
                        val message = findViewById<Button>(R.id.messageBtnProfileVisit)
                        val exit = findViewById<Button>(R.id.exitProfileVisit)
                        //val bubbleTabBarProfileVisit = view.findViewById<BubbleTabBar>(R.id.bubbleTabBarProfileProfileVisit)

                        if(username==appUser)
                        {
                            follow.setVisibility(View.GONE)
                            message.setVisibility(View.GONE);
                        }
                        else
                        {
                            follow.setVisibility(View.VISIBLE);
                            message.setVisibility(View.VISIBLE);
                        }
                        val path : String= Constants.BASE_URL + "User/avatar/" + username
                        Picasso.get().load(path).into(pfp)
                        user.text = userProfileInfo.username
                        postsNum.text = userProfileInfo.numOfPosts.toString()
                        followersNum.text = userProfileInfo.numOfFollowers.toString()
                        likesNum.text = userProfileInfo.totalNumOfLikes.toString();
                        imeprezime.text = userProfileInfo.name;
                        opis.text = userProfileInfo.description;

                        exit.setOnClickListener{
                            VisitUserProfile.setVisit("")
                            val intent = Intent(this@ProfileVisitActivity,MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    else
                    {
                        Toast.makeText(this@ProfileVisitActivity,"Unable to get user info",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(this@ProfileVisitActivity,"Something went wrong. Try again later",Toast.LENGTH_SHORT).show()
                }

            })
        }

        else
        {
            Toast.makeText(this@ProfileVisitActivity,"Something went wring. Try again later.",Toast.LENGTH_SHORT).show()
        }


    }

    private fun replaceFragmentOnProfile(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_profileProfileVisit, fragment)
        fragmentTransaction.commit()
    }
}
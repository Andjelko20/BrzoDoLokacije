package com.example.brzodolokacije.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Adapters.FollowersAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Fragments2.LocationsFragment
import com.example.brzodolokacije.Fragments2.ProfileVisitLocationsFragment
import com.example.brzodolokacije.Fragments2.ProfileVisitPostsFragment
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.UserProfileVisit
import com.example.brzodolokacije.Posts.Follower
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Posts.HomeFragmentState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.ak1.BubbleTabBar
import io.ak1.OnBubbleClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileVisitActivity : AppCompatActivity() {

    private lateinit var username : String
    private lateinit var shouldSave : String
    private lateinit var backToProfile : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_visit)

        username = intent.getStringExtra("visit").toString()
        shouldSave=intent.getStringExtra("saveHomeState").toString()
        backToProfile = intent.getStringExtra("backToProfile").toString()


        val profileVisitPostFragment = ProfileVisitPostsFragment()
        val profileVisitLocationsFragment = ProfileVisitLocationsFragment()

        val bundle = Bundle()
        bundle.putString("username",username)

        profileVisitPostFragment.arguments = bundle
        profileVisitLocationsFragment.arguments = bundle

        replaceFragmentOnProfile(profileVisitPostFragment)

        val bubbleTabBarProfileVisit = findViewById<BubbleTabBar>(R.id.bubbleTabBarProfileProfileVisit)
        bubbleTabBarProfileVisit.addBubbleListener(object : OnBubbleClickListener {
            override fun onBubbleClick(id: Int) {
                when(id){
                    R.id.posts -> replaceFragmentOnProfile(profileVisitPostFragment)
                    R.id.visitedLocations -> replaceFragmentOnProfile(profileVisitLocationsFragment)

                    else -> {}
                }

            }
        })

        val retrofit = Client(this).buildService(Api::class.java)
        val sessionManager = SessionManager(this)
        if (username!="null"){
            val appUser=sessionManager.fetchUsername()
            retrofit.fetchUserProfileInfo(username).enqueue(object: Callback<DefaultResponse>
            {
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false") {
                        //Log.d(response.body()?.error.toString(), response.body()?.message.toString());
                        val userProfileInfoStr: String = response.body()?.message.toString();
                        val gson = Gson()
                        val userProfileInfo: UserProfileVisit = gson.fromJson(userProfileInfoStr, UserProfileVisit::class.java)

                        val followersLayout = findViewById<LinearLayout>(R.id.prviDeoFollowersProfileVisit)
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
                            if(userProfileInfo.isFollowed)
                            {
                                follow.text="Following"
                                follow.setBackgroundColor(getResources().getColor(R.color.light_blue))
                            }

                            follow.setOnClickListener{
                                if(shouldSave=="saveIt") HomeFragmentState.shouldSave(false)
                                retrofit.followUnfollow(username).enqueue(object: Callback<DefaultResponse>
                                {
                                    override fun onResponse(
                                        call: Call<DefaultResponse>,
                                        response: Response<DefaultResponse>
                                    ) {
                                        if(response.body()?.error.toString()=="false")
                                        {
                                            val state = response.body()?.message.toString()
                                            if(state=="followed")
                                            {
                                                follow.text="Following"
                                                follow.setBackgroundColor(getResources().getColor(R.color.light_blue))
                                            }
                                            else
                                            {
                                                follow.text="Follow"
                                                follow.setBackgroundColor(getResources().getColor(R.color.dark_blue))
                                            }
                                            retrofit.refreshFollows(username).enqueue(object: Callback<DefaultResponse>
                                            {
                                                override fun onResponse(
                                                    call: Call<DefaultResponse>,
                                                    response: Response<DefaultResponse>
                                                ) {
                                                    if(response.body()?.error.toString()=="false")
                                                    {
                                                        val newNumOfFollowers= response.body()?.message.toString()
                                                        followersNum.text = newNumOfFollowers
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<DefaultResponse>,
                                                    t: Throwable
                                                ) {
//                                                    Log.d("follows","greska menjanje br pratilaca")
                                                    findViewById<Button>(R.id.exitProfileVisit).setOnClickListener{
                                                        val intent = Intent(this@ProfileVisitActivity,MainActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }

                                            })
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<DefaultResponse>,
                                        t: Throwable
                                    ) {
                                        Toast.makeText(this@ProfileVisitActivity,"Something went wrong. Try again later",Toast.LENGTH_SHORT).show()
                                        findViewById<Button>(R.id.exitProfileVisit).setOnClickListener{
                                            val intent = Intent(this@ProfileVisitActivity,MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }

                                })
                            }

                            message.setOnClickListener{
//                                Toast.makeText(this@ProfileVisitActivity,"message",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@ProfileVisitActivity, ChatActivity::class.java)
                                intent.putExtra("messageUser",username)
                                intent.putExtra("directMessage","directMessage")
                                startActivity(intent)
//                                finish()
                            }
                        }
                        val path : String= Constants.BASE_URL + "User/avatar/" + username
                        Picasso.get().load(path).into(pfp)
                        user.text = userProfileInfo.username
                        postsNum.text = userProfileInfo.numOfPosts.toString()
                        followersNum.text = userProfileInfo.numOfFollowers.toString()
                        likesNum.text = userProfileInfo.totalNumOfLikes.toString();
                        imeprezime.text = userProfileInfo.name;
                        opis.text = userProfileInfo.description;
                        if(opis.text == "" && imeprezime.text == "")
                        {
                            opis.setVisibility(View.GONE)
                            imeprezime.setVisibility(View.GONE)
                        }
                        else if(opis.text != "" && imeprezime.text != "")
                        {
                            opis.setVisibility(View.VISIBLE)
                            imeprezime.setVisibility(View.VISIBLE)
                        }
                        else if(opis.text != "" && imeprezime.text == "")
                        {
                            imeprezime.setVisibility(View.GONE)
                            opis.setVisibility(View.VISIBLE)
                        }
                        else if(opis.text == "" && imeprezime.text != "")
                        {
                            opis.setVisibility(View.GONE)
                            imeprezime.setVisibility(View.VISIBLE)
                        }

                        exit.setOnClickListener{
                            val intent = Intent(this@ProfileVisitActivity,MainActivity::class.java)
                            if(backToProfile == "returnToProfile")
                            {
                                intent.putExtra("backToProfile", "returnToProfile")
                            }
                            startActivity(intent)
                            finish()
                        }

                        followersLayout.setOnClickListener {
                            retrofit.getFollowers(username).enqueue(object: Callback<DefaultResponse>{
                                override fun onResponse(
                                    call: Call<DefaultResponse>,
                                    response: Response<DefaultResponse>
                                ) {
                                    if(response.body()?.error.toString() == "false")
                                    {
//                                        val followerSection = findViewById<LinearLayout>(R.id.followerSection)
//                                        val naslovfollowerSection = followerSection.findViewById<TextView>(R.id.dragFollowerSection)
//                                        naslovfollowerSection.text = username + "'s followers"
                                        val followersListStr: String = response.body()?.message.toString()

                                        val typeToken = object : TypeToken<List<Follower>>() {}.type
                                        val followersList = Gson().fromJson<List<Follower>>(followersListStr, typeToken)

                                        val bottomSheet: View = LayoutInflater.from(this@ProfileVisitActivity).inflate(R.layout.followers_section,null)

                                        val rvFollower = bottomSheet.findViewById<RecyclerView>(R.id.rv_followers)
                                        rvFollower.adapter = FollowersAdapter(followersList, this@ProfileVisitActivity)
                                        rvFollower.layoutManager= LinearLayoutManager(this@ProfileVisitActivity)

                                        val dialog = BottomSheetDialog(this@ProfileVisitActivity)
                                        dialog.setContentView(bottomSheet)
                                        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                                        dialog.show()
                                    }
                                    else
                                    {
//                                        Log.d("error", response.body()?.error.toString());
                                    }

                                }

                                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
//                                    Log.d("failed", "")
                                }

                            })
                        }
                    }
                    else
                    {
                        Toast.makeText(this@ProfileVisitActivity,"Unable to get user info",Toast.LENGTH_SHORT).show()
                        findViewById<Button>(R.id.exitProfileVisit).setOnClickListener{
                            val intent = Intent(this@ProfileVisitActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(this@ProfileVisitActivity,"Something went wrong. Try again later.",Toast.LENGTH_SHORT).show()
                    findViewById<Button>(R.id.exitProfileVisit).setOnClickListener{
                        val intent = Intent(this@ProfileVisitActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ProfileVisitActivity,MainActivity::class.java)
        if(backToProfile == "returnToProfile")
        {
            intent.putExtra("backToProfile", "returnToProfile")
        }
        startActivity(intent)
        finish()
    }
}
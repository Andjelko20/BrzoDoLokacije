package com.example.brzodolokacije.Fragments2

import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Activities.ActivityAddPost
import com.example.brzodolokacije.Activities.ActivityEditProfile
import com.example.brzodolokacije.Activities.FollowersListActivity
import com.example.brzodolokacije.Activities.MainActivity
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.Posts.HomeFragmentState
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.ak1.BubbleTabBar
import io.ak1.OnBubbleClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val languageToLoad = "US"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity?.getBaseContext()?.getResources()?.updateConfiguration(
            config,
            activity?.getBaseContext()?.getResources()?.getDisplayMetrics()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        replaceFragmentOnProfile(PostsFragment())

//        options_meni.setOnClickListener{
//            Toast.makeText(this.requireActivity(),"Tekstnkei",Toast.LENGTH_SHORT).show()
//        }

        val bubbleTabBarProfile = view.findViewById<BubbleTabBar>(R.id.bubbleTabBarProfile);
        addPost.setOnClickListener{
            activity?.let{
                val intent = Intent (it, ActivityAddPost::class.java)
                it.startActivity(intent)
                HomeFragmentState.shouldSave(false)
            }

        }

        bubbleTabBarProfile.addBubbleListener(object : OnBubbleClickListener {
            override fun onBubbleClick(id: Int) {
                when(id){
                    R.id.posts -> replaceFragmentOnProfile(PostsFragment())
                    R.id.visitedLocations -> replaceFragmentOnProfile(LocationsFragment())

                    else -> {}
                }

            }
        })

        val sessionManager= this.context?.let { SessionManager(it) }
        if (sessionManager != null) {
            view.findViewById<TextView>(R.id.username).text="${sessionManager.fetchUsername()}"
        }

        val usernameSm = sessionManager?.fetchUsername()

        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        if (usernameSm != null) {
            retrofit.fetchUserProfileInfo(usernameSm).enqueue(object: Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false")
                    {
    //                    Log.d(response.body()?.error.toString(), response.body()?.message.toString());
                        val userProfileInfoStr: String = response.body()?.message.toString();
                        val gson = Gson()
                        val userProfileInfo: UserProfile = gson.fromJson(userProfileInfoStr, UserProfile::class.java)

                        val followersLayout = view.findViewById<LinearLayout>(R.id.prviDeoFollowers)
                        val username = view.findViewById<TextView>(R.id.username)
                        val postsNum = view.findViewById<TextView>(R.id.postsNum)
                        val followersNum = view.findViewById<TextView>(R.id.followersNum)
                        val likesNum = view.findViewById<TextView>(R.id.likesNum)
                        val imeprezime = view.findViewById<TextView>(R.id.imeprezime)
                        val opis = view.findViewById<TextView>(R.id.opis)
                        val pfp = view.findViewById<CircleImageView>(R.id.profilePicture)

                        username.text = userProfileInfo.username
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

                        followersLayout.setOnClickListener{
                            activity?.let{
                                val intent = Intent (it, FollowersListActivity::class.java)
                                it.startActivity(intent)
                                HomeFragmentState.shouldSave(false)
                            }
                        }


//                        val avatarEncoded = userProfileInfo.profilePicture;

//                        val imageBytes = Base64.decode(avatarEncoded, Base64.DEFAULT)
//                        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        Picasso.get().load(Constants.BASE_URL + "User/avatar/" + usernameSm).into(pfp)
                    }
                    else
                    {
                        Log.d("error not false", "");
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.d("failed","");
                }

            })
        }
    }

    private fun replaceFragmentOnProfile(fragment: Fragment) {
        val fragmentManager = getParentFragmentManager()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_profile, fragment)
        fragmentTransaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
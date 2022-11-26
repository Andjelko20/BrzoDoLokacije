package com.example.brzodolokacije.Fragments2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.ak1.BubbleTabBar
import io.ak1.OnBubbleClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileVisitFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileVisitFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_visit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        replaceFragmentOnProfile(PostsFragment())
//
//        val bubbleTabBarProfileVisit = view.findViewById<BubbleTabBar>(R.id.bubbleTabBarProfileProfileVisit)
//        bubbleTabBarProfileVisit.addBubbleListener(object : OnBubbleClickListener {
//            override fun onBubbleClick(id: Int) {
//                when(id){
//                    R.id.posts -> replaceFragmentOnProfile(PostsFragment())
//                    R.id.visitedLocations -> replaceFragmentOnProfile(LocationsFragment())
//
//                    else -> {}
//                }
//
//            }
//        })
//
//        val retrofit = Client(requireActivity()).buildService(Api::class.java)
//        val sessionManager = context?.let { SessionManager(it) }
//        if (sessionManager != null){
//            val appUser=sessionManager.fetchUsername()
//            retrofit.fetchUserProfileInfo(username).enqueue(object: Callback<DefaultResponse>
//            {
//                override fun onResponse(
//                    call: Call<DefaultResponse>,
//                    response: Response<DefaultResponse>
//                ) {
//                    if(response.body()?.error.toString() == "false") {
//                        //                    Log.d(response.body()?.error.toString(), response.body()?.message.toString());
//                        val userProfileInfoStr: String = response.body()?.message.toString();
//                        val gson = Gson()
//                        val userProfileInfo: UserProfile = gson.fromJson(userProfileInfoStr, UserProfile::class.java)
//
//                        val user = view.findViewById<TextView>(R.id.usernameProfileVisit)
//                        val postsNum = view.findViewById<TextView>(R.id.postsNumProfileVisit)
//                        val followersNum = view.findViewById<TextView>(R.id.followersNumProfileVisit)
//                        val likesNum = view.findViewById<TextView>(R.id.likesNumProfileVisit)
//                        val imeprezime = view.findViewById<TextView>(R.id.imeprezimeProfileVisit)
//                        val opis = view.findViewById<TextView>(R.id.opisProfileVisit)
//                        val pfp = view.findViewById<CircleImageView>(R.id.profilePictureProfileVisit)
//                        val follow=view.findViewById<Button>(R.id.followBtnProfileVisit)
//                        val message=view.findViewById<Button>(R.id.messageBtnProfileVisit)
//                        val exit=view.findViewById<Button>(R.id.exitProfileVisit)
//                        //val bubbleTabBarProfileVisit = view.findViewById<BubbleTabBar>(R.id.bubbleTabBarProfileProfileVisit)
//
//                        if(username==appUser)
//                        {
//                            follow.setVisibility(View.GONE)
//                            message.setVisibility(View.GONE);
//                        }
//                        else
//                        {
//                            follow.setVisibility(View.VISIBLE);
//                            message.setVisibility(View.VISIBLE);
//                        }
//
//                        Picasso.get().load(userProfileInfo.profilePicture).into(pfp)
//                        user.text = userProfileInfo.username
//                        postsNum.text = userProfileInfo.numOfPosts.toString()
//                        followersNum.text = userProfileInfo.numOfFollowers.toString()
//                        likesNum.text = userProfileInfo.totalNumOfLikes.toString();
//                        imeprezime.text = userProfileInfo.name;
//                        opis.text = userProfileInfo.description;
//
//                        //loadFragments(bubbleTabBarProfileVisit)
//                    }
//                    else
//                    {
//                        Toast.makeText(activity,"Unable to get user info",Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
//                    Toast.makeText(activity,"Something went wrong. Try again later",Toast.LENGTH_SHORT).show()
//                }
//
//            })
//        }
//
//        else
//        {
//            Toast.makeText(context,"Something went wring. Try again later.",Toast.LENGTH_SHORT).show()
//        }


    }

    private fun replaceFragmentOnProfile(fragment: Fragment) {
        val fragmentManager = getParentFragmentManager()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_profileProfileVisit, fragment)
        fragmentTransaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileVisitFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileVisitFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
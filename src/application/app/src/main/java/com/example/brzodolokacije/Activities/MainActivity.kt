package com.example.brzodolokacije.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Fragments2.ExploreFragment
import com.example.brzodolokacije.Fragments2.HomeFragment
import com.example.brzodolokacije.Fragments2.ProfileFragment
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Posts.PrivremeneSlikeZaFeed
import com.example.brzodolokacije.Posts.VisitUserProfile
import com.example.brzodolokacije.R
import com.example.brzodolokacije.databinding.ActivityMainBinding
import io.ak1.BubbleTabBar
import io.ak1.OnBubbleClickListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PrivremeneSlikeZaFeed.addPhotos()

        options_meni.setOnClickListener{
            val popupMenu = PopupMenu(this,it)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.editProfileMeni ->{
                        val intent = Intent(this, ActivityEditProfile::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.logoutMeni ->{
                        logOut()
                        true
                    }
                    else -> false
                }

            }
            popupMenu.inflate(R.menu.meni)
            popupMenu.show()
        }

        val intent = getIntent()
        val provera = intent.getStringExtra("backToProfile");
        if (provera != null)
        {
            replaceFragment(ProfileFragment())
            bubbleTabBar.visibility = View.INVISIBLE;
            bubbleTabBar2.visibility = View.VISIBLE;
        }
        else
        {
            replaceFragment(HomeFragment())
            bubbleTabBar.visibility = View.VISIBLE;
            bubbleTabBar2.visibility = View.INVISIBLE;
        }

        bubbleTabBar.addBubbleListener(object : OnBubbleClickListener{
            override fun onBubbleClick(id: Int) {
                when(id){
                    R.id.explore -> replaceFragment(ExploreFragment())
                    R.id.home -> replaceFragment(HomeFragment())
                    R.id.profile -> replaceFragment(ProfileFragment())

                    else -> {}
                }

            }
        })

        bubbleTabBar2.addBubbleListener(object : OnBubbleClickListener{
            override fun onBubbleClick(id: Int) {
                when(id){
                    R.id.explore -> replaceFragment(ExploreFragment())
                    R.id.home -> replaceFragment(HomeFragment())
                    R.id.profile -> replaceFragment(ProfileFragment())

                    else -> {}
                }

            }
        })
    }
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.meni, menu)
        Log.d("Proveri2",menuInflater.inflate(R.menu.meni, menu).toString())
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editProfileMeni ->{
                Toast.makeText(this, "call code", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.logoutMeni ->{
                Toast.makeText(this, "sms code", Toast.LENGTH_LONG).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } /*else {
            Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_SHORT).show()
        }*/
        backPressedTime = System.currentTimeMillis()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    fun logOut()
    {
        val sessionManager = SessionManager(this)
        val retrofit = Client(this).buildService(Api::class.java)
        retrofit.authentication().enqueue(object:
            Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString() == "false")
                {
                    sessionManager.deleteAuthToken()
                    sessionManager.deleteUsername()
                    VisitUserProfile.setVisit("")

                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity,t.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }



}
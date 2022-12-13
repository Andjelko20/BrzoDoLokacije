package com.example.brzodolokacije.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Fragments2.ExploreFragment
import com.example.brzodolokacije.Fragments2.HomeFragment
import com.example.brzodolokacije.Fragments2.ProfileFragment
import com.example.brzodolokacije.Managers.HomeToExploreCommunication
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Posts.HomeFragmentState
import com.example.brzodolokacije.R
import com.example.brzodolokacije.databinding.ActivityMainBinding
import io.ak1.OnBubbleClickListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(),HomeToExploreCommunication {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0

    private var homeFragment : HomeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                ExploreFragment.LOCATION_REQUEST_CODE
            )

            return
        }

        val wrapper: Context = ContextThemeWrapper(this, R.style.MyPopupMenu)

        homeFragment = HomeFragment()

        options_meni.setVisibility(View.GONE)
        inboxIcon.setVisibility(View.VISIBLE)
        inboxIcon.setOnClickListener{
            HomeFragmentState.shouldSave(true)
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            intent.putExtra("inbox","inbox")
            startActivity(intent)
        }
        val intent = getIntent()
        val provera = intent.getStringExtra("backToProfile");
        if (provera != null)
        {
            replaceFragment(ProfileFragment())
            inboxIcon.setVisibility(View.GONE)
            options_meni.setVisibility(View.VISIBLE)
            options_meni.setOnClickListener{
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when(item.itemId){
                        R.id.editProfileMeni ->{
                            val intent = Intent(this@MainActivity, ActivityEditProfile::class.java)
                            startActivity(intent)
                            true
                        }
                        R.id.logoutMeni ->{
                            logOut()
                            true
                        }
                        R.id.changePasswordMeni ->{
                            val intent = Intent(this@MainActivity, ChangePasswordActivity::class.java)
                            startActivity(intent)
                            true
                        }
                        R.id.deleteAccountMeni ->{
                            val intent = Intent(this@MainActivity, DeleteAccountActivity::class.java)
                            startActivity(intent)
                            true
                        }
                        else -> false
                    }

                }
                popupMenu.inflate(R.menu.meni)
                popupMenu.show()
            }
            bubbleTabBar.visibility = View.INVISIBLE;
            bubbleTabBar2.visibility = View.VISIBLE;
        }
        else
        {
            replaceFragment(HomeFragment())
            options_meni.setVisibility(View.GONE)
            inboxIcon.setVisibility(View.VISIBLE)
            inboxIcon.setOnClickListener{
                HomeFragmentState.shouldSave(true)
                val intent = Intent(this@MainActivity, ChatActivity::class.java)
                intent.putExtra("inbox","inbox")
                startActivity(intent)
            }
            bubbleTabBar.visibility = View.VISIBLE;
            bubbleTabBar2.visibility = View.INVISIBLE;
        }

        bubbleTabBar.addBubbleListener(object : OnBubbleClickListener{
            override fun onBubbleClick(id: Int) {
                when(id){
                    R.id.explore -> {
                        replaceFragment(ExploreFragment())
                        options_meni.setVisibility(View.GONE)
                        inboxIcon.setVisibility(View.GONE)
                    }
                    R.id.home -> {
                        replaceFragment(homeFragment!!)
                        options_meni.setVisibility(View.GONE)
                        inboxIcon.setVisibility(View.VISIBLE)
                        inboxIcon.setOnClickListener{
                            HomeFragmentState.shouldSave(true)
                            val intent = Intent(this@MainActivity, ChatActivity::class.java)
                            intent.putExtra("inbox","inbox")
                            startActivity(intent)
                        }
                    }
                    R.id.profile -> {
                        replaceFragment(ProfileFragment())
                        inboxIcon.setVisibility(View.GONE)
                        options_meni.setVisibility(View.VISIBLE)
                        options_meni.setOnClickListener{
                            val popupMenu = PopupMenu(wrapper, it)
                            popupMenu.setOnMenuItemClickListener { item ->
                                when(item.itemId){
                                    R.id.editProfileMeni ->{
                                        val intent = Intent(this@MainActivity, ActivityEditProfile::class.java)
                                        startActivity(intent)
                                        true
                                    }
                                    R.id.logoutMeni ->{
                                        logOut()
                                        true
                                    }
                                    R.id.changePasswordMeni ->{
                                        val intent = Intent(this@MainActivity, ChangePasswordActivity::class.java)
                                        startActivity(intent)
                                        true
                                    }
                                    R.id.deleteAccountMeni ->{
                                        val intent = Intent(this@MainActivity, DeleteAccountActivity::class.java)
                                        startActivity(intent)
                                        true
                                    }
                                    else -> false
                                }
                            }
                            popupMenu.inflate(R.menu.meni)
                            popupMenu.show()
                        }
                    }

                    else -> {}
                }

            }
        })

        bubbleTabBar2.addBubbleListener(object : OnBubbleClickListener{
            override fun onBubbleClick(id: Int) {
                when(id){
                    R.id.explore -> {
                        replaceFragment(ExploreFragment())
                        options_meni.setVisibility(View.GONE)
                        inboxIcon.setVisibility(View.GONE)
                    }
                    R.id.home -> {
                        replaceFragment(homeFragment!!)
                        options_meni.setVisibility(View.GONE)
                        inboxIcon.setVisibility(View.VISIBLE)
                        inboxIcon.setOnClickListener{
                            HomeFragmentState.shouldSave(true)
                            val intent = Intent(this@MainActivity, ChatActivity::class.java)
                            intent.putExtra("inbox","inbox")
                            startActivity(intent)
                        }
                    }
                    R.id.profile -> {
                        replaceFragment(ProfileFragment())
                        inboxIcon.setVisibility(View.GONE)
                        options_meni.setVisibility(View.VISIBLE)
                        options_meni.setOnClickListener{
                            val popupMenu = PopupMenu(wrapper, it)
                            popupMenu.setOnMenuItemClickListener { item ->
                                when(item.itemId){
                                    R.id.editProfileMeni ->{
                                        val intent = Intent(this@MainActivity, ActivityEditProfile::class.java)
                                        startActivity(intent)
                                        true
                                    }
                                    R.id.logoutMeni ->{
                                        logOut()
                                        true
                                    }
                                    R.id.changePasswordMeni ->{
                                        val intent = Intent(this@MainActivity, ChangePasswordActivity::class.java)
                                        startActivity(intent)
                                        true
                                    }
                                    R.id.deleteAccountMeni ->{
                                        val intent = Intent(this@MainActivity, DeleteAccountActivity::class.java)
                                        startActivity(intent)
                                        true
                                    }
                                    else -> false
                                }
                            }
                            popupMenu.inflate(R.menu.meni)
                            popupMenu.show()
                        }
                    }
                    else -> {}
                }
            }
        })
    }
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.meni, menu)
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
            HomeFragmentState.shouldSave(false)
            HomeFragmentState.list(null)
            finishAffinity()
            finish()
        }
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
        HomeFragmentState.shouldSave(false)
        HomeFragmentState.list(null)
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

                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun proslediLokaciju(lokacija: String) {
        val bundle = Bundle()
        bundle.putString("showLocation",lokacija)
        val exploreFragment = ExploreFragment()
        exploreFragment.arguments = bundle
        replaceFragment(exploreFragment)
    }
}
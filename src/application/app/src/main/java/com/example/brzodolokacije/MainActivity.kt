package com.example.brzodolokacije

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.databinding.ActivityMainBinding
import com.example.brzodolokacije.fragments.ExploreFragment
import com.example.brzodolokacije.fragments.HomeFragment
import com.example.brzodolokacije.fragments.ProfileFragment
import io.ak1.BubbleTabBar
import io.ak1.OnBubbleClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        bubbleTabBar.addBubbleListener(object : OnBubbleClickListener{
            override fun onBubbleClick(id: Int) {
                Log.d("iddd", id.toString())
                when(id){
                    R.id.explore -> replaceFragment(ExploreFragment())
                    R.id.home -> replaceFragment(HomeFragment())
                    R.id.profile -> replaceFragment(ProfileFragment())

                    else -> {}
                }

            }
        })

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

}
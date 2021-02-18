package com.example.together

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.together.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(SurroundingsFragment())

        var binding = DataBindingUtil.setContentView<ActivityMainBinding>(
                this, R.layout.activity_main)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.surroundings -> {
                    replaceFragment(SurroundingsFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.feeling -> {
                    replaceFragment(FeelingFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.my -> {
                    // 마이페이지
                    replaceFragment(MyFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }


    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}
package com.example.together.my

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.together.R
import com.example.together.databinding.ActivityProtectorBinding

class ProtectorActivity : AppCompatActivity() {
    lateinit var binding: ActivityProtectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_protector)

        if (existsFollow(1)) {
            // TODO: 팔로우 목록 띄우기
        } else {
            binding.tvNoFollow.text = "팔로우 중인 피보호자가 없습니다"
        }

        binding.tvFollowerDisabled.setOnClickListener {
            binding.llFollowing.visibility = View.INVISIBLE
            binding.llFollower.visibility = View.VISIBLE

            if (existsFollow(2)) {
                // TODO: 팔로우 목록 띄우기
            } else {
                binding.tvNoFollow.text = "팔로워가 없습니다"
            }
        }

        binding.tvFollowingDisabled.setOnClickListener {
            binding.llFollower.visibility = View.INVISIBLE
            binding.llFollowing.visibility = View.VISIBLE

            if (existsFollow(1)) {
                // TODO: 팔로우 목록 띄우기
            } else {
                binding.tvNoFollow.text = "팔로우 중인 피보호자가 없습니다"
            }
        }
    }

    fun existsFollow(type: Int): Boolean {
        // TODO: 팔로우 목록

        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.protector_options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.follow_user -> {
                val intent = Intent(applicationContext, AddActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_up, R.anim.stay)
                finish() // 중요!!
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
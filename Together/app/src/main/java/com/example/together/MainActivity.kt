package com.example.together

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.together.databinding.ActivityMainBinding
import com.example.together.my.MyFragment
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.user.UserApiClient
import java.util.*

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 사용자 정보 요청
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                if (user.kakaoAccount?.email != null) {
                    Log.i(TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: ${user.id}" +
                            "\n이메일: ${user.kakaoAccount?.email}" +
                         "\n닉네임: ${user.kakaoAccount?.profile?.nickname}")
                } else if (user.kakaoAccount?.emailNeedsAgreement == false) {
                    Log.e(TAG, "사용자 계정에 이메일 없음. 꼭 필요하다면 동의항목 설정에서 수집 기능을 활성화 해보세요.")
                } else if (user.kakaoAccount?.emailNeedsAgreement == true) {
                    Log.d(TAG, "사용자에게 이메일 제공 동의를 받아야 합니다.")

                    // 사용자에게 이메일 제공 동의 요청
                    val scopes = Arrays.asList("account_email")
                    LoginClient.instance.loginWithNewScopes(applicationContext, scopes) { token, emailError ->
                        if (emailError != null) {
                            Log.e(TAG, "이메일 제공 동의 실패", emailError)
                        } else {
                            Log.d(TAG, "allowed scopes: ${token!!.scopes}")

                            // 사용자 정보 재요청
                            UserApiClient.instance.me { emailUser, error ->
                                if (error != null) {
                                    Log.e(TAG, "사용자 정보 요청 실패", error)
                                } else if (emailUser != null) {
                                    Log.i(TAG, "이메일: $emailUser.kakaoAccount?.email")
                                }
                            }
                        }
                    }
                }
            }
        }

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
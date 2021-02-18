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
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSocket = IO.socket(serverAddr)!!
        try {
            // socket과 server 연결
            // server의 io.on() 실행
            mSocket.connect()

            Log.d("onConnect", "서버에 연결 완료")
        } catch (e: URISyntaxException) {
            Log.e("onConnect", e.reason)
        }

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

                    saveUserInfo(user.kakaoAccount?.email.toString())
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
                                    Log.i(TAG, "이메일: ${emailUser.kakaoAccount?.email}")
                                    saveUserInfo(emailUser.kakaoAccount?.email.toString())
                                }
                            }
                        }
                    }
                }
            }
        }

        replaceFragment(SurroundingsFragment())

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
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

    fun saveUserInfo(id: String) {
        GlobalApplication.prefs.userId = id
        mSocket.emit("login", id)
    }
}
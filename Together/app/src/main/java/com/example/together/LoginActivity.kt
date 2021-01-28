package com.example.together

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.together.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    val Tag = "LoginActivity"
    lateinit var mSocket: Socket
    lateinit var userName: String
    var users: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        userName = "현재 사용자"

        val preferences = getSharedPreferences("myInfo", Context.MODE_PRIVATE)
        val prefData = preferences.getString("token", null)
        if (prefData != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        try {
            mSocket = IO.socket(serverAddr)

            // socket과 server 연결
            // server의 io.on() 실행
            mSocket.connect()

            Log.d(Tag, "서버에 연결 완료")
        } catch (e: URISyntaxException) {
            Log.e(Tag, e.reason)
        }

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("newUser", onNewUser)
        mSocket.on("myMsg", onMyMessage)
        mSocket.on("newMsg", onNewMessage)
        mSocket.on("logout", onLogout)

        val bLogin = binding.bLogin
        bLogin.setOnClickListener {
            mSocket.emit("say", "안녕")
        }

        val bKakao = binding.bKakao
        bKakao.setOnClickListener {
            // 로그인 공통 callback 구성
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(ContentValues.TAG, "로그인 실패", error)
                }
                else if (token != null) {
                    Log.i(ContentValues.TAG, "로그인 성공 ${token.accessToken}")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    val pref = getSharedPreferences("myInfo", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = pref.edit()
                    editor.putString("token", token.accessToken)
                    editor.apply()
                }
                else {
                    Log.e(ContentValues.TAG, "토큰 생성 실패")
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {

                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
                Log.i("로그인", "카카오톡 로그인으로 이동")
            }
        }
    }

    val onConnect: Emitter.Listener = Emitter.Listener {
        mSocket.emit("login", userName)
        Log.d(Tag, "Socket is connected with $userName")
    }

    val onMyMessage = Emitter.Listener {
        Log.d("on", "Mymessage has been triggered.")
        Log.d("mymsg : ", it[0].toString())
    }

    val onNewMessage = Emitter.Listener {
        Log.d("on", "New message has been triggered.")
        Log.d("new msg : ", it[0].toString())
    }

    val onLogout = Emitter.Listener {
        Log.d("on", "Logout has been triggered.")

        try {
            val jsonObj: JSONObject = it[0] as JSONObject // it[0]: Any형
            Log.d("logout ", jsonObj.getString("disconnected"))
            Log.d("WHO IS ON NOW", jsonObj.getString("whoIsOn"))

            //Disconnect socket!
            mSocket.disconnect()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    val onMessageRecieved: Emitter.Listener = Emitter.Listener {
        try {
            val receivedData: Any = it[0]
            Log.d(Tag, receivedData.toString())

        } catch (e: Exception) {
            Log.e(Tag, "error", e)
        }
    }

    val onNewUser: Emitter.Listener = Emitter.Listener {

        var data = it[0]
        if (data is String) {
            users = data.split(",").toTypedArray() // 파싱
            for (a: String in users) {
                Log.d("user", a)
            }
        } else {
            Log.d("error", "Something went wrong")
        }

    }

    override fun onStop() {
        super.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}
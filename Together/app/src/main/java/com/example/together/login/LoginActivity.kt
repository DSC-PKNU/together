package com.example.together.login

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.together.MainActivity
import com.example.together.R
import com.example.together.data.User
import com.example.together.databinding.ActivityLoginBinding
import com.example.together.serverAddr
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

class LoginActivity : AppCompatActivity() {
    val TAG = "LoginActivity"

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mContext: Context

    lateinit var mSocket: Socket
    lateinit var userName: String
    var users: Array<String> = arrayOf()
    lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mContext = applicationContext

        userName = "현재 사용자"

        val preferences = getSharedPreferences("myInfo", Context.MODE_PRIVATE)
        val prefData = preferences.getString("token", null)
        if (prefData != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        mSocket = IO.socket(serverAddr)!!
        try {
            // socket과 server 연결
            // server의 io.on() 실행
            mSocket.connect()

            Log.d("onConnect", "서버에 연결 완료")
        } catch (e: URISyntaxException) {
            Log.e("onConnect", e.reason)
        }

//        mSocket.on(Socket.EVENT_CONNECT, onLogin)
        mSocket.on("login", onLogin)
        mSocket.on("newUser", onNewUser)
        mSocket.on("myMsg", onMyMessage)
        mSocket.on("newMsg", onNewMessage)
        mSocket.on("logout", onLogout)

        val bLogin = binding.bLogin
        bLogin.setOnClickListener {
            mSocket.emit("say", "안녕")
            Log.i("Tag", "서버에 채팅 전송됨")
        }

        val bKakao = binding.bKakao
        bKakao.setOnClickListener {
            // 로그인 공통 callback 구성
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(ContentValues.TAG, "로그인 실패", error)
                }
                else if (token != null) {
                    Log.i(TAG, "로그인 성공 ${token.accessToken}")

                    val pref = getSharedPreferences("myInfo", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = pref.edit()
                    editor.putString("token", token.accessToken)
                    editor.apply()

                    // 사용자 정보 요청
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            Log.i(TAG, "사용자 정보 요청 성공" +
                                    "\n회원번호: ${user.id}" +
                                    "\n이메일: ${user.kakaoAccount?.email}" +
                                    "\n닉네임: ${user.kakaoAccount?.profile?.nickname}")

                            register(user.id.toString(), user.kakaoAccount?.profile!!.nickname)
                        }
                    }
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

        binding.tvJoin.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }

    val onLogin: Emitter.Listener = Emitter.Listener {
        mSocket.emit("login", userName)
        Log.d("onConnect", "Socket is connected with ${userName}")
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
            Log.d("onMessageReceived", receivedData.toString())

        } catch (e: Exception) {
            Log.e("onMessageReceived", "error", e)
        }
    }

    val onNewUser: Emitter.Listener = Emitter.Listener {

        val data = it[0]
        if (data != null) {
//            users = data.split(",").toTypedArray() // 파싱
//            for (a: String in users) {
//                Log.d("user", a)
//            }

            Log.d("current users", data.toString())
        } else {
            Log.e("error", "Something went wrong")
        }

    }

    fun register(id: String, name: String) {
        val url = "/join"
        val data = JSONObject()
        data.accumulate("user_id", id)
        data.accumulate("user_pw", "default")
        data.accumulate("name", name)

        GlobalScope.launch {
            var con: HttpURLConnection? = null
            var reader: BufferedReader? = null
            try {
                val url = URL(serverAddr + url)
                con = url.openConnection() as HttpURLConnection?
                con!!.requestMethod = "POST"
                con.setRequestProperty("Cache-Control", "no-cache")
                con.setRequestProperty("content-Type", "application/json")
                con.setRequestProperty("Accept", "text/html")
                con.doOutput = true
                con.doInput = true
                con!!.connect()

                val outStream: OutputStream = con.outputStream
                val writer: BufferedWriter = BufferedWriter(OutputStreamWriter(outStream))
                writer.write(data.toString())
                writer.flush()
                writer.close()

                val stream: InputStream = con.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                val buffer = StringBuffer()

                var line: String? = reader.readLine()
                while (line != null) {
                    buffer.append(line)
                    line = reader.readLine()
                }

                Log.d("Join Response", buffer.toString())

                return@launch
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (con != null) {
                    con.disconnect()
                }
                try {
                    if (reader != null) {
                        reader.close()
                    }

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
package com.example.together.utils

import android.util.Log
import com.example.together.data.User
import com.example.together.serverAddr
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException

class Connection() {

    companion object {
        val mSocket = IO.socket(serverAddr)!!
        lateinit var mUser: User

        fun onConnect(): Socket {
            try {
                // socket과 server 연결
                // server의 io.on() 실행
                mSocket.connect()

                Log.d("onConnect", "서버에 연결 완료")
            } catch (e: URISyntaxException) {
                Log.e("onConnect", e.reason)
            }

            mSocket.on(Socket.EVENT_CONNECT, onLogin);
            mSocket.on("newUser", onNewUser)
            mSocket.on("myMsg", onMyMessage)
            mSocket.on("newMsg", onNewMessage)
            mSocket.on("logout", onLogout)

            return mSocket
        }

        val onLogin: Emitter.Listener = Emitter.Listener {
            mSocket.emit("login", mUser.id)
            Log.d("onConnect", "Socket is connected with ${mUser.id}")
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
    }
}
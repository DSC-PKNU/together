package com.example.together.my

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.together.EmergencyCallSetting
import com.example.together.GlobalApplication
import com.example.together.R
import com.example.together.databinding.FragmentMyBinding
import com.example.together.login.LoginActivity
import com.example.together.serverAddr
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class MyFragment : Fragment() {
    var myListItem: ArrayList<String>? = null
    var logoutListItem: ArrayList<String>? = null

    private lateinit var binding: FragmentMyBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my, container,
                false)

        val pref = requireActivity()
                .getSharedPreferences("myInfo", Context.MODE_PRIVATE)
        val userName = binding.userName
        val userEmail = binding.userEmail
        val lvMy = binding.lvMy
        val lvLogout = binding.lvLogout
        initListItem()
        val myAdapter = MyAdapter(this.context, myListItem)
        val logoutAdapter = LogoutAdapter(this.context, logoutListItem)
        lvMy.adapter = myAdapter
        lvLogout.adapter = logoutAdapter
        lvMy.onItemClickListener = OnItemClickListener { adapterView, v, position, id ->
            Toast.makeText(context, myAdapter.getItem(position).toString(),
                    Toast.LENGTH_SHORT).show()
            when (position) {
                0 -> {
                    Toast.makeText(context, "긴급전화번호 관리 페이지로 넘어갑니다",
                            Toast.LENGTH_LONG).show()
                    val callSettingIntent = Intent(activity, EmergencyCallSetting::class.java)
                    startActivity(callSettingIntent)
                }
                1 -> {
                }
                2 -> {
                }
                else -> {
                }
            }
        }
        lvLogout.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val dialog = LogoutDialogFragment()
            //                dialog.show(getFragmentManager(), "로그아웃");
            // TODO: dialog 오류 해결
            UserApiClient.instance.unlink { error: Throwable? ->
                if (error != null) {
                    Log.e(ContentValues.TAG, "로그아웃(연결 끊기) 실패.", error)
                } else {
                    Log.i(ContentValues.TAG, "로그아웃(연결 끊기) 성공. SDK에서 토큰 삭제됨")
                    val editor = pref.edit()
                    editor.remove("token")
                    editor.apply()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(activity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (GlobalApplication.prefs.kakao) {
            // 사용자 정보 요청
            UserApiClient.instance.me { user: User?, error: Throwable? ->
                if (error != null) {
                    Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    if (user.kakaoAccount!!.email != null) {
                        Log.i(ContentValues.TAG, """
                             사용자 정보 요청 성공
                             회원번호: ${user.id}
                             이메일: ${user.kakaoAccount!!.email}
                             닉네임: ${user.kakaoAccount!!.profile!!.nickname}
                             """.trimIndent())
                        userName.text = user.kakaoAccount!!.profile!!.nickname
                        userEmail.text = user.kakaoAccount!!.email
                    } else if (user.kakaoAccount!!.emailNeedsAgreement == false) {
                        Log.e(ContentValues.TAG, "사용자 계정에 이메일 없음. 꼭 필요하다면 동의항목 설정에서 수집 기능을 활성화 해보세요.")
                    } else if (user.kakaoAccount!!.emailNeedsAgreement == true) {
                        Log.d(ContentValues.TAG, "사용자에게 이메일 제공 동의를 받아야 합니다.")

                        // 사용자에게 이메일 제공 동의 요청
                        val scopes = Arrays.asList("account_email")
                        LoginClient.instance.loginWithNewScopes(requireContext(), scopes) { token, emailError ->
                            if (emailError != null) {
                                Log.e(ContentValues.TAG, "이메일 제공 동의 실패", emailError)
                            } else {
                                Log.d(ContentValues.TAG, "allowed scopes: $scopes")

                                // 사용자 정보 재요청
                                UserApiClient.instance.me { emailUser: User?, emailError2: Throwable? ->
                                    if (emailError2 != null) {
                                        Log.e(ContentValues.TAG, "사용자 정보 요청 실패", emailError2)
                                    } else if (emailUser != null) {
                                        Log.i(ContentValues.TAG, "이메일: " + emailUser.kakaoAccount!!.email)
                                        userEmail.text = emailUser.kakaoAccount!!.email
                                        userName.text = emailUser.kakaoAccount!!.profile!!.nickname

                                        // TODO: Room 사용해서 UI가 데이터 변화를 감지하도록 변경
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            getData()
        }

        return binding.root
    }

    fun initListItem() {
        myListItem = ArrayList()
        logoutListItem = ArrayList()
        myListItem!!.add("긴급 전화번호 관리")
        myListItem!!.add("보호자 관리")
        myListItem!!.add("피보호자 관리")
        logoutListItem!!.add("로그아웃")
    }

    fun getData() {
        val data = JSONObject()
        val url = URL("$serverAddr/cur_user")
        data.accumulate("user_id", GlobalApplication.prefs.userId)

        lateinit var allText: String

        GlobalScope.launch(Dispatchers.Main) {

            val con: HttpURLConnection? = url.openConnection() as HttpURLConnection?
            con!!.requestMethod = "POST"
            con.setRequestProperty("Cache-Control", "no-cache")
            con.setRequestProperty("content-Type", "application/json")
            con.setRequestProperty("Accept", "text/html")
            con.doOutput = true
            con.doInput = true

            suspend fun coroutineRequest(url: URL) =
                    withContext(IO) { // runs at I/O level and frees the Main thread
                        with(con) {
                            val outStream: OutputStream = con.outputStream
                            val writer: BufferedWriter = BufferedWriter(OutputStreamWriter(outStream))
                            writer.write(data.toString())
                            writer.flush()
                            writer.close()

                            allText = inputStream.bufferedReader().use(BufferedReader::readText)
                            Log.d("coroutineRequest", allText)
                        }
                    }

            coroutineRequest(url)

            binding.userName.text = allText
            binding.userEmail.text = GlobalApplication.prefs.userId
        }
    }
}
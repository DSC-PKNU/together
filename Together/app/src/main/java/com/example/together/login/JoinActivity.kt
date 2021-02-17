package com.example.together.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.together.R
import com.example.together.databinding.ActivityJoinBinding
import com.example.together.serverAddr
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class JoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)

        binding.etId.editText!!.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (checkLen()) {
                    binding.etId.error = "아이디 중복확인"
                } else {
                    binding.etId.error = "6자 이상의 영문 혹은 영문과 숫자를 조합\n아이디 중복확인"
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.bCheck.setOnClickListener {
            if (checkLen()) {
                checkValid()
            } else {
                val dialog = CheckFragment()
                dialog.show(supportFragmentManager, "ID_CHECK")
            }
        }

//        binding.etPassword.editText!!.addTextChangedListener(object: TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        })

        binding.etConfirm.editText!!.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.etPassword.editText!!.text.toString() !=
                        binding.etConfirm.editText!!.text.toString()) {
                    binding.etConfirm.error = "동일한 비밀번호를 입력해주세요"
                } else {
                    binding.etConfirm.error = null
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.bJoin.setOnClickListener {
            register()

            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show()

            onBackPressed()
        }
    }

    fun checkLen(): Boolean {
        return binding.etId.editText!!.text.length >= 6
    }

    fun checkDup(): Boolean {
        // TODO: server
        return true
    }

    fun checkValid() {
        if (checkLen() && checkDup()) binding.etId.error = null
        else if (checkLen() && !checkDup()) binding.etId.error = "아이디 중복 확인"
        else binding.etId.error = "아이디 중복 확인\n6자 이상의 영문 혹은 영문과 숫자를 조합"
    }

    fun register() {
        val url = "/post"
        val data = JSONObject()
        data.accumulate("user_id", "사용자 아이디")
        data.accumulate("name", "사용자 이름")

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
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getData() {
        val url = "/users"
        val data = JSONObject()
        data.accumulate("user_id", "사용자 아이디")
        data.accumulate("name", "사용자 이름")

        GlobalScope.launch {
            var con: HttpURLConnection? = null
            var reader: BufferedReader? = null
            try {
                val url = URL(serverAddr + url)
                con = url.openConnection() as HttpURLConnection?
                con!!.connect()

                val stream: InputStream = con.inputStream
                val reader = BufferedReader(InputStreamReader(stream))
                val buffer: StringBuffer = StringBuffer()
                var line: String? = ""
                line = reader.readLine()
                while (line != null) {
                    Log.d("line", line.toString())
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
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
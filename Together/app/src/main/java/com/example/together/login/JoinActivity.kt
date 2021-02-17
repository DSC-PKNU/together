package com.example.together.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.together.R
import com.example.together.databinding.ActivityJoinBinding

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
}
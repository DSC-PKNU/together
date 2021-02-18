package com.example.together.login

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class LoginFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("아이디 또는 비밀번호 오류입니다")
                    .setPositiveButton("닫기") { dialog, id ->
                    }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
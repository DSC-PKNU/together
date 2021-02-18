package com.example.together

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.kakao.sdk.user.UserApiClient


class LogoutDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val pref = requireActivity()
                .getSharedPreferences("myInfo", Context.MODE_PRIVATE)

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialog_logout)
                    .setPositiveButton(R.string.yes,
                            DialogInterface.OnClickListener { dialog, id ->
                                UserApiClient.instance.unlink { error: Throwable? ->
                                    if (error != null) {
                                        Log.e(ContentValues.TAG, "로그아웃(연결 끊기) 실패.", error)
                                    } else {
                                        Log.i(ContentValues.TAG, "로그아웃(연결 끊기) 성공. SDK에서 토큰 삭제됨")
                                        val editor: SharedPreferences.Editor = pref.edit()
                                        editor.remove("token")
                                        editor.apply()

                                        val intent = Intent(activity, LoginActivity::class.java)
                                        startActivity(intent)
                                        Toast.makeText(activity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    null
                                }
                            })
                    .setNegativeButton(R.string.no,
                            DialogInterface.OnClickListener { dialog, id ->
                                // User cancelled the dialog
                            })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
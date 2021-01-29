package com.example.together

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class EmergencyCallSetting : Fragment(){

    val data = arrayListOf(
            Contact(1,
                    "엄마",
                    "01011112222",
            R.drawable.ic_location),
            Contact(2,
            "아빠",
            "01011112223",
            R.drawable.ic_location),
            Contact(3,
                    "동생",
                    "01011112224",
                    R.drawable.ic_location)
    );

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.emergency_call_setting, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = CallAdapter(data,view.context)

        view.findViewById<Button>(R.id.add_number).setOnClickListener {
            val contactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivity(contactIntent)
            getActivity()?.finish();
        }

        return view;
    }

}
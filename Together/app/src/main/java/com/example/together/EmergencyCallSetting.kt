package com.example.together

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.Context
import android.content.ContentResolver
import android.database.Cursor
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.provider.ContactsContract
import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import android.view.*
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.emergency_call_list.view.*
import java.util.*

class EmergencyCallSetting : Activity() {

    var data= arrayListOf<Contact>(
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
    )


    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.emergency_call_setting)

            val adapter =CallAdapter(data,this)
            //val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            //recyclerView.adapter = CallAdapter(data,this)
            //val adapter=recyclerView.adapter

            adapter.setItemClickListener(object :  CallAdapter.OnItemClickListener {
                override fun onClick(v: View, position: Int) {
                    data.removeAt(position)
                    adapter.notifyDataSetChanged()
                }
            })

        findViewById<FloatingActionButton>(R.id.add_number).setOnClickListener {
                val contactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(contactIntent, 10)

                fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                    super.onActivityResult(requestCode, resultCode, data)
                    if (resultCode == Activity.RESULT_OK) {

                        val context = this
                        if (context != null) {
                            val contentResolver: ContentResolver = context.contentResolver

                            val listUrl = ContactsContract.Contacts.CONTENT_URI
                            val proj = arrayOf(
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.PHOTO_ID
                            )


                            val cursor = contentResolver.query(listUrl, proj, null, null, null)

                            if (cursor != null) {

                                cursor.moveToFirst();
                                var Name = cursor.getString(0)
                                var Phone = cursor.getString(1)
                                var Image = cursor.getInt(2)

                                cursor.close()


                                adapter.addItem(Contact(adapter.getItemCount() + 1, Name, Phone, Image))
                                adapter.notifyDataSetChanged()
                            }

                        }
                    }

                }
                finish();
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.call_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId) {
            R.id.addCall -> {
                val adapter =CallAdapter(data,this)

                val contactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(contactIntent, 10)

                fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                    super.onActivityResult(requestCode, resultCode, data)
                    if (resultCode == Activity.RESULT_OK) {

                        val context = this
                        if (context != null) {
                            val contentResolver: ContentResolver = context.contentResolver

                            val listUrl = ContactsContract.Contacts.CONTENT_URI
                            val proj = arrayOf(
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.PHOTO_ID
                            )


                            val cursor = contentResolver.query(listUrl, proj, null, null, null)

                            if (cursor != null) {

                                cursor.moveToFirst();
                                var Name = cursor.getString(0)
                                var Phone = cursor.getString(1)
                                var Image = cursor.getInt(2)

                                cursor.close()


                                adapter.addItem(Contact(adapter.getItemCount() + 1, Name, Phone, Image))
                                adapter.notifyDataSetChanged()
                            }

                        }
                    }

                }
                finish();
                return true}
        }
        return super.onOptionsItemSelected(item)
    }
}
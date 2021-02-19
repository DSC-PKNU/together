package com.example.together

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CallAdapter (val data: ArrayList<Contact>, val context: Context): RecyclerView.Adapter<CallAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(context).inflate(R.layout.emergency_call_list, p0, false))

    override fun getItemCount(): Int = data.size

    fun addItem(Item: Contact){
        data.add(Item)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.name.text = data[p1].name
        p0.phone.text = data[p1].phone

        if ( p0.profileImage!= null) {
            p0.profileImage.setImageResource(data[p1].profileImage)
        } else {
            p0.profileImage?.setImageResource(R.drawable.ic_face_background)
        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.name)
        val phone = itemView.findViewById<TextView>(R.id.phone)
        val profileImage = itemView.findViewById<ImageView>(R.id.profileImage)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    private lateinit var itemClickListener : OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}
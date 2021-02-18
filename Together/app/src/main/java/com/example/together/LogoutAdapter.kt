package com.example.together

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class LogoutAdapter: BaseAdapter {
    var mContext: Context? = null
    var mLayoutInflater: LayoutInflater? = null
    var item: ArrayList<String>? = null

    constructor(context: Context?, data: ArrayList<String>?) {
        mContext = context
        mLayoutInflater = LayoutInflater.from(mContext)
        item = data
    }

    override fun getCount(): Int {
        return item!!.size
    }

    override fun getItem(position: Int): Any? {
        return item!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = mLayoutInflater!!.inflate(R.layout.logout_list_item, null)
        val textView = view.findViewById<TextView>(R.id.tvLogout)
        textView.text = item!![position]
        return view
    }
}
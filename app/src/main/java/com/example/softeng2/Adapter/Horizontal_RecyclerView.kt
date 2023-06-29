package com.example.softeng2.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softeng2.R

class Horizontal_RecyclerView : RecyclerView.Adapter<Horizontal_RecyclerView.MyViewholder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Horizontal_RecyclerView.MyViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return MyViewholder(view)
    }

    override fun onBindViewHolder(holder: Horizontal_RecyclerView.MyViewholder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class MyViewholder(itemView: View): RecyclerView.ViewHolder(itemView){

    }
}
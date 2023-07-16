package com.example.softeng2.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softeng2.CalendarActivity
import com.example.softeng2.DoctorsActivity
import com.example.softeng2.databinding.DoctorCardBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DoctorListAdapter(
    private val documentList: MutableList<Map<String, Any>>,
    private val itemcount: Int,
    private val context: Context,
    private val puid: String
) : RecyclerView.Adapter<DoctorListAdapter.ViewHolder>(){
    var num=0
    class ViewHolder(private val doctorCardBinding:DoctorCardBinding, private val context: Context) : RecyclerView.ViewHolder(doctorCardBinding.root){
        fun bind(doc: Map<String, Any>, pos:Int) {
            Log.d("errorasd",doc.get("UID").toString())
                    doctorCardBinding.tvName.text = (
                            doc.get("lname").toString()
                                    + ", " + doc.get("fname").toString()
                                    + " " + doc.get("mname")
                                .toString() + ".")

                        doctorCardBinding.tvType.text = (
                                doc.get("type").toString())

                        doctorCardBinding.tvFee.text = ("PHP " +
                                doc.get("rate").toString())

            doctorCardBinding.btnBook.setOnClickListener() {
                val uid= (context as DoctorsActivity).intent.getStringExtra("PUID")
                val intent = Intent(context, CalendarActivity::class.java)
                intent.putExtra("PUID",uid)
                intent.putExtra("DUID",doc.get("UID").toString())
                Log.d("asdc",uid+" " + doc.get("UID").toString())
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DoctorCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding,context)
    }

    override fun getItemCount(): Int {
        Log.d("aasda",itemcount.toString())
        return itemcount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(documentList[position], position)
    }
}
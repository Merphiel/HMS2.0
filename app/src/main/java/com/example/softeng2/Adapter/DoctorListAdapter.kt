package com.example.softeng2.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.softeng2.CalendarActivity
import com.example.softeng2.DoctorsActivity
import com.example.softeng2.databinding.DoctorCardBinding
import com.example.softeng2.databinding.ItemListBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DoctorListAdapter(private val context: Context,puid:String) : RecyclerView.Adapter<DoctorListAdapter.ViewHolder>(){
    var num=0
    class ViewHolder(private val doctorCardBinding:DoctorCardBinding, private val context: Context) : RecyclerView.ViewHolder(doctorCardBinding.root){
        fun bind(duid:String, pos:Int) {
            val db = Firebase.firestore
            val doctorsCollectionRef = db.collection("doctors")

            Log.d("errorasd",duid)
            Log.d("errorasd",duid)
            doctorsCollectionRef.document(duid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        Log.d("errorasd","test")
                    doctorCardBinding.tvName.text = (
                            documentSnapshot.data?.get("lname").toString()
                                    + ", " + documentSnapshot.data?.get("fname").toString()
                                    + " " + documentSnapshot.data?.get("mname")
                                .toString() + ".")

                        doctorCardBinding.tvType.text = (
                                documentSnapshot.data?.get("type").toString())

                        doctorCardBinding.tvFee.text = ("PHP " +
                                documentSnapshot.data?.get("rate").toString())
                } else {

                    }


                }
                .addOnFailureListener { exception ->
                    println("Error getting documents from 'doctors' collection: $exception")
                }

            doctorCardBinding.btnBook.setOnClickListener() {
                val uid= (context as DoctorsActivity).intent.getStringExtra("PUID")
                val intent = Intent(context, CalendarActivity::class.java)
                intent.putExtra("PUID",uid)
                intent.putExtra("DUID",duid)
                Log.d("asdc",uid+" " + duid)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DoctorCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding,context)
    }

    override fun getItemCount(): Int {    val db = Firebase.firestore
        val doctorsCollectionRef = db.collection("doctors")
        doctorsCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
            }
            .addOnFailureListener { exception ->
                println("Error getting documents from 'doctors' collection: $exception")
            }
        return 2;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val db = Firebase.firestore
        val doctorsCollectionRef = db.collection("doctors")

        doctorsCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    val documentId = documentSnapshot.id

                    Log.d("errorasddid",documentId)
                    holder.bind(documentId, position)
                    num++;
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents from 'doctors' collection: $exception")
            }
    }
}
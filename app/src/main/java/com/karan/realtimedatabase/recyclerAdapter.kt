package com.karan.realtimedatabase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent

class recyclerAdapter(var array: ArrayList<Items>, private var recyclerBtn: Recycler_btn) :
    RecyclerView.Adapter<recyclerAdapter.ViewHolder>() {
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.etName)
        val btn_del: Button = view.findViewById(R.id.btn_Delete)
        val btn_update: Button = view.findViewById(R.id.btn_update)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(
            R.layout.recyclerview_list,
            parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = array[position]
        holder.name.text=currentItem.name
        holder.btn_del.setOnClickListener {
            recyclerBtn.delete_data(currentItem, position)
        }
        holder.btn_update.setOnClickListener {
            recyclerBtn.update_data(currentItem,position)
        }
        holder.name.setOnClickListener {
            recyclerBtn.click(currentItem,position)
        }

    }
}
package com.balivo.jsonparser

/**
 * Created by balivo on 3/12/18.
 */
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

class UsersAdapter(private var users: ArrayList<HashMap<String, String>>)
    : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item, parent, false)
        return ViewHolder(v)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textName.text = users[position]["name"]
        holder.textEmail.text = users[position]["primary_email"]
    }
    override fun getItemCount(): Int = users.size
    /**
     * We will use this method to update the adapter
     * @param users
     */
    fun updateAdapter(users: ArrayList<HashMap<String, String>>) {
        this.users = users
        notifyDataSetChanged()
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.text_name)
        val textEmail: TextView = itemView.findViewById(R.id.text_email)
    }
}
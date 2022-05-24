package com.dicoding.fauzimaulana.consumerapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.fauzimaulana.consumerapp.databinding.ItemRowUsersBinding

class ListUserAdapter(private val listUser: ArrayList<User>): RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {

    private  var onItemClickCallback : OnItemClickCallback? = null

    class ListViewHolder(private val binding: ItemRowUsersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(user.avatar)
                    .apply(RequestOptions().override(55,55))
                    .into(imgUsers)
                tvName.text = user.name
                tvUsername.text = user.username
                tvLocation.text = user.location
            }
        }
    }

    fun setOnItemClickCallback (
        onItemClickCallback: OnItemClickCallback) {
            this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUser[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClick(listUser[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listUser.size

    interface OnItemClickCallback {
        fun onItemClick(data: User)
    }
}
package com.dicoding.fauzimaulana.mysubmission1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.fauzimaulana.mysubmission1.databinding.ItemRowUsersBinding

class FavoriteUserAdapter(private val listUserFavorite: ArrayList<User>): RecyclerView.Adapter<FavoriteUserAdapter.ListViewHolder>() {

    private var onItemClickCallback : OnItemClickCallbackFavorite? = null

    class ListViewHolder(private val binding: ItemRowUsersBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(user.avatar)
                    .apply(RequestOptions().override(55, 55))
                    .into(imgUsers)
                tvName.text = user.name
                tvUsername.text = user.username
                tvLocation.text = user.location
            }
        }
    }

    fun setOnItemClickCallbackFavorite(onItemClickCallback: OnItemClickCallbackFavorite) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUserFavorite[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClick(listUserFavorite[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listUserFavorite.size

    interface OnItemClickCallbackFavorite {
        fun onItemClick(data: User)
    }
}
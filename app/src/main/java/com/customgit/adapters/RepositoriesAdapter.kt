package com.customgit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.customgit.core.data_classes.Repository
import com.customgit.databinding.ForRepositoriesRecViewBinding

class RepositoriesAdapter(private val listener: Clickable) :
    RecyclerView.Adapter<RepositoriesAdapter.MainPageViewHolder>() {

    var repos = listOf<Repository>()

    inner class MainPageViewHolder(private val binding: ForRepositoriesRecViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repository: Repository) {
            binding.nameRepo.text = repository.fullName
            binding.langRepo.text = repository.language

            itemView.setOnClickListener {
                listener.clickToSeeDetailsRepo(repository)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainPageViewHolder {
        val binding = ForRepositoriesRecViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MainPageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainPageViewHolder, position: Int) {
        holder.bind(repos[position])
    }

    override fun getItemCount(): Int {
        return repos.size
    }
}

interface Clickable {
    fun clickToSeeDetailsRepo(repo: Repository)
}
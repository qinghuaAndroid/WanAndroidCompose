package com.example.application.ui.github

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.application.R
import java.util.Locale

class GitHubRepositoryAdapter :
    BaseQuickAdapter<GitHubRepository, BaseViewHolder>(R.layout.item_github_repository) {

    var onRepositoryClick: ((GitHubRepository) -> Unit)? = null

    init {
        setOnItemClickListener { _, _, position -> onRepositoryClick?.invoke(data[position]) }
    }

    private fun formatCount(value: Int): String {
        return if (value >= 1_000) String.format(
            Locale.US,
            "%.1fk",
            value / 1_000f
        ) else value.toString()
    }

    private fun GitHubRepository.descriptionOrFallback(): String {
        return description ?: context.getString(R.string.github_projects_default_description)
    }

    private fun GitHubRepository.languageOrFallback(): String {
        return language ?: context.getString(R.string.github_projects_unknown_language)
    }

    private fun GitHubRepository.updatedDate(): String {
        return updatedAt?.take(10).orEmpty()
    }
    override fun convert(holder: BaseViewHolder, item: GitHubRepository) {
        holder.setText(R.id.tvRepoName, item.name)
        holder.setText(R.id.tvOwner, item.owner.login)
        holder.setText(R.id.tvDescription, item.descriptionOrFallback())
        holder.setText(R.id.tvLanguage, item.languageOrFallback())
        holder.setText(
            R.id.tvStars,
            context.getString(R.string.github_projects_stars, formatCount(item.stargazersCount))
        )
        holder.setText(
            R.id.tvUpdated,
            context.getString(R.string.github_projects_updated, item.updatedDate())
        )
        Glide.with(context)
            .load(item.owner.avatarUrl)
            .circleCrop()
            .into(holder.getView<ImageView>(R.id.ivOwnerAvatar))
    }
}

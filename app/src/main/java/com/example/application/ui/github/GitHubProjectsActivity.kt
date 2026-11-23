package com.example.application.ui.github

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.R
import com.example.application.databinding.ActivityGithubProjectsBinding
import com.example.application.ui.browser.BrowserNormalActivity
import com.example.common.constant.Const
import com.example.core.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GitHubProjectsActivity : BaseActivity<ActivityGithubProjectsBinding>() {
    private val viewModel by viewModels<GitHubProjectsViewModel>()
    private val repositoryAdapter by lazy { GitHubRepositoryAdapter() }

    override fun getLayoutId(): Int = R.layout.activity_github_projects

    override fun initData(savedInstanceState: Bundle?) = Unit

    override fun initView() {
        title = getString(R.string.github_projects_title)
        binding.rvGithubRepositories.layoutManager = LinearLayoutManager(this)
        binding.rvGithubRepositories.adapter = repositoryAdapter
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        repositoryAdapter.onRepositoryClick = ::openRepository

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    override fun loadData() {
        viewModel.refresh()
    }

    private fun render(state: GitHubProjectsUiState) {
        binding.swipeRefresh.isRefreshing = state.isLoading
        repositoryAdapter.setList(state.repositories)
        binding.tvEmptyState.isVisible = !state.isLoading && state.repositories.isEmpty()
        binding.tvEmptyState.text = state.errorMessage ?: getString(R.string.github_projects_empty)
    }

    private fun openRepository(repository: GitHubRepository) {
        startActivity(Intent(this, BrowserNormalActivity::class.java).apply {
            putExtra(Const.WEB_TITLE, repository.fullName)
            putExtra(Const.WEB_URL, repository.htmlUrl)
        })
    }
}

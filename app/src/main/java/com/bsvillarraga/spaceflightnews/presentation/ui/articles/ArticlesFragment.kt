package com.bsvillarraga.spaceflightnews.presentation.ui.articles

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bsvillarraga.spaceflightnews.R
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.core.extensions.init
import com.bsvillarraga.spaceflightnews.core.extensions.onLoadMoreScrollListener
import com.bsvillarraga.spaceflightnews.core.extensions.queryTextListener
import com.bsvillarraga.spaceflightnews.core.extensions.toResourceGlide
import com.bsvillarraga.spaceflightnews.databinding.FragmentArticlesBinding
import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bsvillarraga.spaceflightnews.presentation.permission.PermissionChainManager
import com.bsvillarraga.spaceflightnews.presentation.permission.PermissionHandler
import com.bsvillarraga.spaceflightnews.presentation.permission.PermissionRequest
import com.bsvillarraga.spaceflightnews.presentation.permission.PermissionType
import com.bsvillarraga.spaceflightnews.presentation.ui.articles.adapter.ArticleAdapter
import com.bsvillarraga.spaceflightnews.presentation.ui.articles.extensions.showState
import com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel.ArticlesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticlesFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentArticlesBinding
    private lateinit var adapter: ArticleAdapter

    private var searchView: SearchView? = null
    private var searchMenuItem: MenuItem? = null

    private val viewModel: ArticlesViewModel by viewModels()
    private lateinit var permissionHandler: PermissionHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticlesBinding.inflate(layoutInflater)
        permissionHandler = PermissionHandler(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearch()
        setupRecyclerView()
        fetchArticle()
        observeArticle()
    }

    private fun setupSearch() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter(
            onItemClicked = { article ->
                navigateArticlesToArticleDetail(article)
            }
        )

        binding.rcvArticles.init(this@ArticlesFragment.adapter, requireContext())
        binding.rcvArticles.onLoadMoreScrollListener {
            loadMoreArticle()
        }
    }

    private fun loadMoreArticle() {
        if (adapter.itemCount == 0) return
        adapter.setLoading(true)
        viewModel.fetchArticles(loadMore = true)
    }

    private fun fetchArticle() {
        viewModel.fetchArticles()
    }

    private fun observeArticle() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.articles.collect { resource ->
                    handleResource(resource)
                }
            }
        }
    }

    private fun handleResource(resource: Resource<List<Article>>) {
        when (resource) {
            is Resource.Error -> showError()
            is Resource.Loading -> showLoading()
            is Resource.Success -> loadData(resource.data)
        }
    }

    private fun loadData(articles: List<Article>?) {
        adapter.submitList(articles)

        if (articles.isNullOrEmpty()) {
            showWithoutInformation()
        } else {
            binding.contentInformation.root.visibility = View.GONE
            hideLoading()
        }
    }

    private fun showWithoutInformation() {
        binding.showState(showNoInfo = true)
        binding.contentInformation.ivInformation.toResourceGlide(
            requireContext(),
            R.drawable.without_information
        )
    }

    private fun showError() {
        binding.showState(showError = true)
        binding.contentInformation.apply {
            ivInformation.toResourceGlide(requireContext(), R.drawable.error)

            contentError.btnRetry.setOnClickListener {
                viewModel.fetchArticles(reload = true)
            }
        }
    }

    private fun hideLoading() {
        binding.showState(showLoading = false)
    }

    private fun showLoading() {
        binding.showState(showLoading = true)
        binding.contentLoading.apply {
            ivLoading.toResourceGlide(
                requireContext(),
                R.drawable.astronaut_with_space_shuttle_loader
            )
        }
    }

    private fun navigateArticlesToArticleDetail(article: Article) {
        findNavController().navigate(
            ArticlesFragmentDirections.actionArticlesFragmentToArticleDetailFragment(
                articleId = article.id,
                newsSite = article.newsSite
            )
        )
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_search, menu)

        searchMenuItem = menu.findItem(R.id.action_search)
        searchView = searchMenuItem?.actionView as SearchView
        searchView?.queryHint = "Buscar..."

        searchView?.queryTextListener(
            onSubmit = { query ->
                viewModel.onSearchQueryChanged(query)
            },
            onTextChanged = { query ->
                viewModel.onSearchQueryChanged(query ?: "")
            }
        )
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_voice_search -> {
                requestRecordAudio()
                true
            }

            else -> false
        }
    }

    private fun requestRecordAudio() {
        PermissionChainManager(permissionHandler)
            .addPermission(
                PermissionRequest(
                    type = PermissionType.RECORD_AUDIO,
                    onGranted = {
                        startVoiceSearch()
                    }
                )
            )
            .execute()
    }

    private fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo...")
        }
        try {
            voiceSearchLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "Tu dispositivo no admite búsqueda por voz",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val voiceSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val spokenText = results?.get(0) ?: return@registerForActivityResult

                Handler(Looper.getMainLooper()).postDelayed({
                    searchMenuItem?.expandActionView()
                    searchView?.setQuery(spokenText, true)
                }, 200)
            }
        }
}
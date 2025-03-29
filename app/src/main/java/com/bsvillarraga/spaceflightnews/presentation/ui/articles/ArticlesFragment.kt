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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bsvillarraga.spaceflightnews.R
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.databinding.FragmentArticlesBinding
import com.bsvillarraga.spaceflightnews.domain.model.Article
import com.bsvillarraga.spaceflightnews.presentation.permission.PermissionChainManager
import com.bsvillarraga.spaceflightnews.presentation.permission.PermissionHandler
import com.bsvillarraga.spaceflightnews.presentation.permission.PermissionRequest
import com.bsvillarraga.spaceflightnews.presentation.permission.PermissionType
import com.bsvillarraga.spaceflightnews.presentation.ui.articles.adapter.ArticleAdapter
import com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel.ArticlesViewModel
import com.bumptech.glide.Glide
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

        binding.rcvArticles.apply {
            adapter = this@ArticlesFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!recyclerView.canScrollVertically(1)) {
                        loadMoreArticle()
                    }
                }
            })
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
        }

        hideLoading()
    }

    private fun showWithoutInformation() {
        binding.contentInformation.root.visibility = View.VISIBLE
        binding.rcvArticles.visibility = View.GONE

        binding.contentInformation.apply {
            contentError.root.visibility = View.GONE
            contentWithoutInformation.root.visibility = View.VISIBLE

            Glide.with(requireContext())
                .load(R.drawable.without_information)
                .centerCrop()
                .override(ivInformation.width, ivInformation.height)
                .into(ivInformation)
        }
    }

    private fun showError() {
        binding.rcvArticles.visibility = View.GONE
        binding.contentLoading.root.visibility = View.GONE
        binding.contentInformation.root.visibility = View.VISIBLE

        binding.contentInformation.apply {
            contentError.root.visibility = View.VISIBLE
            contentWithoutInformation.root.visibility = View.GONE

            Glide.with(requireContext())
                .load(R.drawable.error)
                .centerCrop()
                .override(ivInformation.width, ivInformation.height)
                .into(ivInformation)
        }

    }

    private fun hideLoading() {
        binding.contentLoading.root.visibility = View.GONE
        binding.rcvArticles.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.rcvArticles.visibility = View.GONE
        binding.contentLoading.root.visibility = View.VISIBLE
        binding.contentInformation.root.visibility = View.GONE

        binding.contentLoading.apply {
            Glide.with(requireContext())
                .asGif()
                .load(R.drawable.astronaut_with_space_shuttle_loader)
                .centerCrop()
                .override(ivLoading.width, ivLoading.height)
                .into(ivLoading)
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

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.onSearchQueryChanged(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.onSearchQueryChanged(newText ?: "")
                }

                return true
            }
        })
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
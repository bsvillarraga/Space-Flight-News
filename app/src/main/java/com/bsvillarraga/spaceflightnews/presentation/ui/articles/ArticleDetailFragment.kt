package com.bsvillarraga.spaceflightnews.presentation.ui.articles

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bsvillarraga.spaceflightnews.R
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.core.extensions.toFormattedDate
import com.bsvillarraga.spaceflightnews.databinding.FragmentArticleDetailsBinding
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel.ArticleDetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticleDetailFragment : Fragment() {
    private var articleId: Long = -1L
    private var titleToolbar: String? = null

    private lateinit var binding: FragmentArticleDetailsBinding
    private val viewModel: ArticleDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            articleId = it.getLong("articleId", -1L)
            titleToolbar = it.getString("newsSite")
        }

        if (articleId == -1L && titleToolbar == null) {
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = titleToolbar

        setupToolbar()
        fetchArticle()
        observeArticle()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = titleToolbar
    }

    private fun fetchArticle() {
        viewModel.fetchArticleById(articleId)
    }

    private fun observeArticle() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.article.collect { resource ->
                    Log.e("TAG", "onViewCreated: $resource")
                    handleResource(resource)
                }
            }
        }
    }

    private fun handleResource(resource: Resource<ArticleDetail>) {
        when (resource) {
            is Resource.Error -> Unit
            is Resource.Loading -> showLoading()
            is Resource.Success -> loadData(resource.data)
        }
    }

    private fun loadData(article: ArticleDetail?) {
        article?.let {
            with(binding.contentCard) {
                tvArticleTitle.text = it.title
                tvDate.text = getString(R.string.published_at, it.publishedAt.toFormattedDate())
                tvAuthors.text = getString(
                    R.string.authors,
                    it.authors.joinToString(", ") { author -> author.name })
                articleContent.text = it.summary

                btnContinueReading.setOnClickListener {
                    openWebPage(article.url)
                }
            }

            Glide.with(requireContext())
                .load(it.imageUrl)
                .centerCrop()
                .into(binding.headerImage)
        } ?: Log.e("loadData", "Muestra mensaje de sin datos")

        hideLoading()
    }

    private fun hideLoading() {
        binding.motionLayout.visibility = View.VISIBLE
        binding.contentLoading.root.visibility = View.GONE
    }

    private fun showLoading() {
        binding.motionLayout.visibility = View.GONE
        binding.contentLoading.root.visibility = View.VISIBLE

        binding.contentLoading.apply {
            Glide.with(requireContext())
                .asGif()
                .load(R.drawable.astronaut_with_space_shuttle_loader)
                .centerCrop()
                .override(ivLoading.width, ivLoading.height)
                .into(ivLoading)
        }
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            showToast("No se encontró un navegador web")
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
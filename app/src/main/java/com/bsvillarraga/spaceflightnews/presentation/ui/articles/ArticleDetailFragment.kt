package com.bsvillarraga.spaceflightnews.presentation.ui.articles

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bsvillarraga.spaceflightnews.R
import com.bsvillarraga.spaceflightnews.core.common.Resource
import com.bsvillarraga.spaceflightnews.core.extensions.toFormattedDate
import com.bsvillarraga.spaceflightnews.core.extensions.toNetworkGlide
import com.bsvillarraga.spaceflightnews.core.extensions.toResourceGlide
import com.bsvillarraga.spaceflightnews.databinding.FragmentArticleDetailsBinding
import com.bsvillarraga.spaceflightnews.domain.model.ArticleDetail
import com.bsvillarraga.spaceflightnews.presentation.ui.articles.extensions.showState
import com.bsvillarraga.spaceflightnews.presentation.ui.articles.viewmodel.ArticleDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

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

    //Se establece el título de la barra de acción
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = titleToolbar
    }

    //Se realiza la primera carga de datos
    private fun fetchArticle() {
        viewModel.fetchArticleById(articleId, reload = true)
    }

    //Se observa el estado de la respuesta
    private fun observeArticle() {
        viewModel.article.observe(viewLifecycleOwner) { resource ->
            handleResource(resource)
        }
    }

    //Se manejan los diferentes estados de la respuesta
    private fun handleResource(resource: Resource<ArticleDetail>) {
        when (resource) {
            is Resource.Error -> showError()
            is Resource.Loading -> showLoading()
            is Resource.Success -> loadData(resource.data)
        }
    }

    //Se cargan los datos en la vista
    private fun loadData(article: ArticleDetail?) {
        article?.let {
            with(binding.contentCard) {
                tvArticleTitle.text = it.title
                tvDate.text = getString(R.string.published_at, it.publishedAt.toFormattedDate())
                tvAuthors.text = getString(
                    R.string.authors,
                    if (it.authors.isEmpty()) {
                        "No author"
                    } else {
                        it.authors.joinToString(", ") { author -> author.name }
                    }
                )
                articleContent.text = it.summary



                btnContinueReading.setOnClickListener {
                    openWebPage(article.url)
                }
            }

            binding.headerImage.toNetworkGlide(requireContext(), it.imageUrl)
        }

        hideLoading()
    }

    //Oculta el estado de carga.
    private fun hideLoading() {
        binding.showState(showLoading = false)
    }

    //Muestra el estado de carga.
    private fun showLoading() {
        binding.showState(showLoading = true)

        binding.contentLoading.apply {
            ivLoading.toResourceGlide(
                requireContext(),
                R.drawable.astronaut_with_space_shuttle_loader
            )
        }
    }

    //Muestra un mensaje de error cuando ocurre un problema en la carga de datos.
    private fun showError() {
        binding.showState(showError = true)
        binding.contentInformation.apply {
            ivInformation.toResourceGlide(requireContext(), R.drawable.error)

            contentError.btnRetry.setOnClickListener {
                fetchArticle()
            }
        }
    }

    //Abre una página web
    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            showToast("No se encontró un navegador web")
        }
    }

    //Muestra un mensaje Toast
    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
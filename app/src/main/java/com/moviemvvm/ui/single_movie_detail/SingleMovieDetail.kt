package com.moviemvvm.ui.single_movie_detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.moviemvvm.R
import com.moviemvvm.data.api.POSTER_BASE_URL
import com.moviemvvm.data.api.TheMovieDBClient
import com.moviemvvm.data.api.TheMovieDBInterface
import com.moviemvvm.data.repository.NetworkState
import com.moviemvvm.data.vo.MovieDetails
import kotlinx.android.synthetic.main.activity_single_movie_detail.*
import java.text.NumberFormat
import java.util.*

class SingleMovieDetail : AppCompatActivity() {

    private lateinit var movieRepository: MovieDetailsRepository
    private lateinit var viewModel: SingleMovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_movie_detail)


        val movieId = intent.getIntExtra("id", 1)

        val apiService: TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this){
            bindUI(it)
        }

        viewModel.networkState.observe(this){
            progress_bar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            tv_error.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        }

    }

    private fun bindUI(it: MovieDetails) {

        tv_movie_title.text = it.title
        tv_movie_tagline.text = it.tagline
        tv_movie_release.text = it.releaseDate
        tv_movie_rating.text = it.rating.toString()
        tv_movie_runtime.text = ("${it.runtime.toString()} Minutes")
        tv_movie_overview.text = it.overview

        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
        tv_movie_budget.text = formatCurrency.format(it.budget)
        tv_movie_revenue.text = formatCurrency.format(it.revenue)

        val moviePosterURL = "$POSTER_BASE_URL${it.posterPath}"
        Glide.with(this)
                .load(moviePosterURL)
                .into(iv_movie_poster)

    }

    private fun getViewModel(movieId: Int): SingleMovieViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory{
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SingleMovieViewModel(movieRepository, movieId) as T
                }

            }).get(SingleMovieViewModel::class.java)
}
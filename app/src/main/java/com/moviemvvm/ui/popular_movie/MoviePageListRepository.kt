package com.moviemvvm.ui.popular_movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.moviemvvm.data.api.POST_PER_PAGE
import com.moviemvvm.data.api.TheMovieDBInterface
import com.moviemvvm.data.repository.MovieDataSource
import com.moviemvvm.data.repository.MovieDataSourceFactory
import com.moviemvvm.data.repository.NetworkState
import com.moviemvvm.data.vo.Movie
import io.reactivex.disposables.CompositeDisposable

class MoviePageListRepository(private val apiService: TheMovieDBInterface) {

    lateinit var moviePageList: LiveData<PagedList<Movie>>
    lateinit var movieDataSourceFactory: MovieDataSourceFactory

    fun fetchLiveMoviePageList(compositeDisposable: CompositeDisposable): LiveData<PagedList<Movie>>{
        movieDataSourceFactory = MovieDataSourceFactory(apiService, compositeDisposable)

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(POST_PER_PAGE)
                .build()

        moviePageList = LivePagedListBuilder(movieDataSourceFactory, config).build()

        return moviePageList
    }

    fun getNetworkState(): LiveData<NetworkState> =
            Transformations.switchMap<MovieDataSource, NetworkState>(
                    movieDataSourceFactory.movieLiveDataSource, MovieDataSource::networkState
            )

}
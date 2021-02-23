package com.moviemvvm.data.api

import com.moviemvvm.data.vo.MovieDetails
import com.moviemvvm.data.vo.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBInterface {

    // https://api.themoviedb.org/3/movie/popular?api_key=cb6b5c7268f2c64e55df8f887910158a
    // https://api.themoviedb.org/3/movie/581389?api_key=cb6b5c7268f2c64e55df8f887910158a
    // https://api.themoviedb.org/3/

    @GET("movie/popular")
    fun getPopularMovie(@Query("page") page: Int): Single<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") id: Int): Single<MovieDetails>

}
package com.moviemvvm.data.repository

import com.moviemvvm.data.repository.NetworkState.Companion.ENDOFLIST

enum class Status{
    RUNNING,
    SUCCES,
    FAILED
}

class NetworkState(val status: Status, val message: String) {

    companion object {
        val LOADED: NetworkState
        val LOADING: NetworkState
        val ERROR: NetworkState
        val ENDOFLIST: NetworkState


        init {
            LOADED = NetworkState(Status.SUCCES, "Succes")
            LOADING = NetworkState(Status.RUNNING, "Running")
            ERROR = NetworkState(Status.FAILED, "Something Went Wrong")
            ENDOFLIST = NetworkState(Status.FAILED, "You Have Reached The End")
        }

    }

}
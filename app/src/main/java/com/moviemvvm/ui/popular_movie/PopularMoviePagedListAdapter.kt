package com.moviemvvm.ui.popular_movie

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moviemvvm.R
import com.moviemvvm.data.api.POSTER_BASE_URL
import com.moviemvvm.data.repository.NetworkState
import com.moviemvvm.data.vo.Movie
import com.moviemvvm.ui.single_movie_detail.SingleMovieDetail
import kotlinx.android.synthetic.main.movie_list_item.view.*
import kotlinx.android.synthetic.main.network_state_item.view.*

class PopularMoviePagedListAdapter(val context: Context): PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        if (viewType == MOVIE_VIEW_TYPE){
            view = layoutInflater.inflate(R.layout.movie_list_item, parent, false)
            return MovieItemViewHolder(view)
        }else{
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_VIEW_TYPE){
            (holder as MovieItemViewHolder).bind(getItem(position), context)
        }else{
            networkState?.let {
                (holder as NetworkStateItemViewHolder).bind(it)
            }
        }
    }

    private fun hasExtraRow(): Boolean =
            networkState != null && networkState != NetworkState.LOADED

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }



    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && (position == itemCount - 1)){
            NETWORK_VIEW_TYPE
        }else{
            MOVIE_VIEW_TYPE
        }
    }

    class MovieDiffCallback: DiffUtil.ItemCallback<Movie>(){
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }

    class MovieItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(movie: Movie?, context: Context){
            itemView.tv_title_popular.text = movie?.title
            itemView.tv_release_date_popular.text = movie?.releaseDate

            val moviePosterUrl = "$POSTER_BASE_URL${movie?.posterPath}"
            Glide.with(itemView.context)
                    .load(moviePosterUrl)
                    .into(itemView.img_movie_poster)

            itemView.setOnClickListener{
                Intent(context, SingleMovieDetail::class.java).also {
                    it.putExtra("id", movie?.id)
                    context.startActivity(it)
                }
            }
        }
    }

    class NetworkStateItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(networkState: NetworkState){
            if (networkState != null && networkState == NetworkState.LOADING){
                itemView.progres_bar_item.visibility = View.VISIBLE
            }else{
                itemView.progres_bar_item.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR){
                itemView.tv_error_message_item.visibility = View.VISIBLE
                itemView.tv_error_message_item.text = networkState.message
            }else if(networkState != null && networkState == NetworkState.ENDOFLIST) {
                itemView.tv_error_message_item.visibility = View.VISIBLE
                itemView.tv_error_message_item.text = networkState.message
            }else{
                itemView.tv_error_message_item.visibility = View.GONE
            }
        }
    }

    fun setNetworkState(newNetworkState: NetworkState){
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()

        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow){
            if (hadExtraRow){                               //hadExtraRow is true and hasExtraRow is false
                notifyItemRemoved(super.getItemCount())     //remove  the progress bar at the end
            }else{                                          //hadExtraRow is false and hasExtraRow is true
                notifyItemInserted(super.getItemCount())    //add the progress bar at the end
            }
        }else if(hasExtraRow && (previousState != newNetworkState)){ //hasExtraRow is true and hadExtraRow is true
            notifyItemChanged(itemCount - 1)
        }
    }
}
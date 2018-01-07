package com.example.coskun.explorephotos.ui.photo_list

import android.support.v4.view.ViewCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.coskun.explorephotos.R
import com.example.coskun.explorephotos.entensions.inflate
import com.example.coskun.explorephotos.entensions.setImageUrl
import com.example.coskun.explorephotos.entity.Photo
import kotlinx.android.synthetic.main.item_photo.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.launch

/**
 * Created by Coskun Yalcinkaya.
 */
class PhotoAdapter(private var photoList: List<Photo> = listOf(), private val listener: (ImageView) -> Unit) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private val diffCallback by lazy(LazyThreadSafetyMode.NONE) {DiffCallback()}

    private val eventActor = actor<List<Photo>>(capacity = Channel.CONFLATED) {for (list in channel) internalUpdate(list)}

    override fun onBindViewHolder(holder: PhotoViewHolder?, position: Int) = holder!!.bind(photoList[position], listener)

    override fun getItemCount() = photoList.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = PhotoViewHolder(parent!!.inflate(R.layout.item_photo))

    fun updateData(newData : List<Photo>) {
        eventActor.offer(newData)
    }

    private suspend fun internalUpdate(list : List<Photo>){
        val filteredList = list.distinctBy {it.id}
        val result = DiffUtil.calculateDiff(diffCallback.apply { newList = filteredList }, false)
        photoList = filteredList
        launch (UI) {
            result.dispatchUpdatesTo(this@PhotoAdapter)
        }.join()
    }

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bind(photo: Photo, listener: (ImageView) -> Unit) = with(itemView){
            txt_photoTitle.text = photo.title
            img_photo.setImageUrl("https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}_q.jpg")
            ViewCompat.setTransitionName(img_photo, photo.id)
            itemView.setOnClickListener { listener.invoke(img_photo) }
        }
    }

    private inner class DiffCallback : DiffUtil.Callback(){
        lateinit var newList: List<Photo>
        override fun getOldListSize() = photoList.size
        override fun getNewListSize() = newList.size
        override fun areContentsTheSame(oldItemPosition : Int, newItemPosition : Int) = true
        override fun areItemsTheSame(oldItemPosition : Int, newItemPosition : Int) = photoList[oldItemPosition] == newList[newItemPosition]
    }
}
package com.abdurrahimgayretli.marsroverphotos.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdurrahimgayretli.marsroverphotos.Model.Photo
import com.abdurrahimgayretli.marsroverphotos.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rover_photos.view.*

class MarsAdapter( private var mListener :onItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var photos = ArrayList<Photo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rover_photos, parent, false)
        return PhotoHolder(v)}

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PhotoHolder ->{
                holder.bind(photos[position])
            }
        }
    }

    inner class PhotoHolder(itemView : View) : RecyclerView.ViewHolder(itemView),View.OnClickListener{
        fun bind(photo : Photo){
            val image = photo.img_src.replace("http","https")
            Glide.with(itemView.context).load(image).into(itemView.roverImageCard)
        }

        init{
            itemView.roverImageCard.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position!=RecyclerView.NO_POSITION){
                mListener.onItemClick(v,position,photos)
            }
        }

    }
    interface onItemClickListener{
        fun onItemClick(view:View?, position:Int,photos : List<Photo>)
    }

    fun addList(items: ArrayList<Photo>){
        photos.addAll(items)
        notifyDataSetChanged()
    }
    fun clear(){
        photos.clear()
        notifyDataSetChanged()
    }
}
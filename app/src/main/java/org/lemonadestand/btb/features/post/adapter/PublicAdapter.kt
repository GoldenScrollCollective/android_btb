package org.lemonadestand.btb.features.post.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.post.models.PostModelDate
import org.lemonadestand.btb.features.post.models.Post

class PublicAdapter(
    private var list: ArrayList<PostModelDate>,
    var context: Context
) : RecyclerView.Adapter<PublicAdapter.ViewHolder>() {

    var onPreview: ((value: Post) -> Unit)? = null

    private var onItemClick: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.layout_company_posts_item, parent, false)
        return ViewHolder(listItem)
    }

    fun setOnItemClick(onItemClickListener: OnItemClickListener) {
        this.onItemClick = onItemClickListener
    }

    @SuppressWarnings("InvalidSetHasFixedSize")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.tvDate.text = getDate(data.date!!)

        val tempPostList: ArrayList<Post> = ArrayList()

        for (i in 0 until data.postList.size) {
            if (getDate(data.postList[i].created) == getDate(data.date!!)) {
                tempPostList.add(data.postList[i])
            }
        }
        Log.i("context=>>", tempPostList.toString())
        val adapter = PublicSubAdapter(tempPostList, context, position)
        onItemClick?.let { adapter.setItemClick(it) }
        adapter.onPreview = { post -> onPreview?.invoke(post) }
        holder.recyclerView.setHasFixedSize(true)
        holder.recyclerView.layoutManager = LinearLayoutManager(context)
        holder.recyclerView.adapter = adapter
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDate: TextView
        var recyclerView: RecyclerView

        init {
            tvDate = itemView.findViewById(R.id.tv_date)
            recyclerView = itemView.findViewById(R.id.rv_public_sub)
        }
    }
    fun updateData(list : ArrayList<PostModelDate>)
    {
        this.list= list
        notifyDataSetChanged()
    }
}
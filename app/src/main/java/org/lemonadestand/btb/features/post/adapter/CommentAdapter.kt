package org.lemonadestand.btb.features.post.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.features.post.models.CommentModelDate
import org.lemonadestand.btb.features.post.models.Post

class CommentAdapter(private val list: ArrayList<CommentModelDate>, var context: Context) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.layout_company_posts_item, parent, false)
        return ViewHolder(listItem)
    }

    @SuppressWarnings("InvalidSetHasFixedSize")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.tvDate.text = getDate(data.date!!)

        val tempPostList: ArrayList<Post> = ArrayList()

        for (i in 0 until data.commentList.size) {
            if (getDate(data.commentList[i].created!!) == getDate(data.date!!)) {
                tempPostList.add(data.commentList[i])
            }
        }
        val adapter = CommentSubAdapter(tempPostList, context)
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
}
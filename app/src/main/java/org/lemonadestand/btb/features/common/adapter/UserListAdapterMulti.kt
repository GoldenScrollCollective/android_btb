package org.lemonadestand.btb.features.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.extenstions.hide
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.extenstions.show

class UserListAdapterMulti(private var list: ArrayList<UserListModel>, var context: Context) :
    RecyclerView.Adapter<UserListAdapterMulti.ViewHolder>() {

    private var onItemClick: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.row_user_list_multi, parent, false)
        return ViewHolder(listItem)
    }

    fun setOnItemClick(onItemClickListener: OnItemClickListener) {
        this.onItemClick = onItemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.tvUserName.text = data.name
        if (data.isSelected) {
            holder.icCheck.show()
        } else {
            holder.icCheck.hide()
        }

        holder.itemView.setOnClickListener {
            list[position].isSelected = !list[position].isSelected
            notifyDataSetChanged()
            //onItemClick!!.onItemClicked(data, position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserName: TextView
        var icCheck: ImageView

        init {
            tvUserName = itemView.findViewById(R.id.tv_user_name)
            icCheck = itemView.findViewById(R.id.ic_check)
        }
    }

    fun updateList(userList: ArrayList<UserListModel>) {
        this.list = userList
    }

    fun getSelectedUserList(): ArrayList<UserListModel> {

        var selectedList : ArrayList<UserListModel> = ArrayList()
        for (i in 0 until list.size)
        {
            if (list[i].isSelected)
            {
                selectedList.add(list[i])
            }
        }
        return selectedList
    }
}
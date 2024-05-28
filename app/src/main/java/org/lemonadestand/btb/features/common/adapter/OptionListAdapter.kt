package org.lemonadestand.btb.features.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.features.interest.models.Option
import org.lemonadestand.btb.extenstions.hide
import org.lemonadestand.btb.interfaces.OnItemClickListener

import org.lemonadestand.btb.features.common.fragments.OptionListFragment.Companion.optionListFragmentIndex
import org.lemonadestand.btb.extenstions.show

class OptionListAdapter(private var list: ArrayList<Option>, var context: Context) :
    RecyclerView.Adapter<OptionListAdapter.ViewHolder>() {

    private var onItemClick: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.row_team_list, parent, false)
        return ViewHolder(listItem)
    }

    fun setOnItemClick(onItemClickListener: OnItemClickListener) {
        this.onItemClick = onItemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.tvUserName.text = data.label

        if (data.isCheck) {
            holder.icCheck.show()
        } else {
            holder.icCheck.hide()
        }
        holder.itemView.setOnClickListener {
            onItemClick!!.onItemClicked(data,optionListFragmentIndex)
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

    fun updateList(userList: ArrayList<Option>) {
        this.list = userList

    }
}
package com.example.shoppinglist.adaptor

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.view.animation.AnimationUtils
import com.example.shoppinglist.R
import com.example.shoppinglist.activities.ScrollingActivity
import com.example.shoppinglist.data.AppDatabase
import com.example.shoppinglist.data.Item
import com.example.shoppinglist.touch.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*

class ItemAdaptor : RecyclerView.Adapter<ItemAdaptor.ViewHolder>, ItemTouchHelperCallback {

    var shoppingItems = mutableListOf<Item>()

    private val context: Context

    constructor(context: Context, listItems: List<Item>) : super() {
        this.context = context
        shoppingItems.addAll(listItems)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(
            R.layout.item_row, viewGroup, false
        )

        return ViewHolder(itemRowView)
    }

    override fun getItemCount(): Int {
        return shoppingItems.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentItem = shoppingItems.get(viewHolder.adapterPosition)

        var checkBoxAnim = AnimationUtils.loadAnimation(this.context, R.anim.checkbox_anim)

        setIconImage(viewHolder)

        viewHolder.tvName.text = currentItem.itemName
        viewHolder.cbDone.isChecked = currentItem.done
        viewHolder.cbDone.setOnClickListener{
            currentItem.done = viewHolder.cbDone.isChecked
            viewHolder.cbDone.startAnimation(checkBoxAnim)
            updateItem(currentItem)
        }

        initializeItemButtons(viewHolder)

    }

    private fun initializeItemButtons(viewHolder: ViewHolder) {
        val currentItem = shoppingItems.get(viewHolder.adapterPosition)

        viewHolder.btnDelete.setOnClickListener {
            deleteItem(viewHolder.adapterPosition)
        }
        viewHolder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditItemDialog(currentItem, viewHolder.adapterPosition)
        }

    }

    private fun setIconImage(viewHolder: ViewHolder) {

        when (shoppingItems.get(viewHolder.adapterPosition).itemCategory) {
            context.getString(R.string.food) -> viewHolder.ivIcon.setImageResource(R.drawable.ic_local_pizza)
            context.getString(R.string.drink) -> viewHolder.ivIcon.setImageResource(R.drawable.ic_local_bar)
            context.getString(R.string.household_goods) -> viewHolder.ivIcon.setImageResource(R.drawable.ic_local_laundry_service)
            context.getString(R.string.clothes) -> viewHolder.ivIcon.setImageResource(R.drawable.ic_local_mall)
            context.getString(R.string.other) -> viewHolder.ivIcon.setImageResource(R.drawable.ic_filter_drama)
        }

    }

    fun addItem(item: Item) {
        shoppingItems.add(0, item)
        notifyItemInserted(0)
    }

    private fun deleteItem(deletePosition: Int) {
        Thread {
            AppDatabase.getInstance(context).itemDao().deleteItem(shoppingItems.get(deletePosition))
            (context as ScrollingActivity).runOnUiThread{
                shoppingItems.removeAt(deletePosition)
                notifyItemRemoved(deletePosition)
            }
        }.start()
    }

    fun deleteAll() {
       Thread {
           AppDatabase.getInstance(context).itemDao().deleteAll()
           (context as ScrollingActivity).runOnUiThread{
               shoppingItems = mutableListOf()
               notifyDataSetChanged()
           }
        }.start()

    }

    fun updateItem(item: Item) {
        Thread {
            AppDatabase.getInstance(context).itemDao().updateItem(item)
        }.start()
    }

    fun updateItem(item: Item, editIndex: Int) {
        shoppingItems.set(editIndex, item)
        notifyItemChanged(editIndex)
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(shoppingItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var ivIcon = itemView.ivIcon
        var tvName = itemView.tvName
        var cbDone = itemView.cbDone
        var btnDelete = itemView.btnDelete
        var btnEdit = itemView.btnEdit
    }

}
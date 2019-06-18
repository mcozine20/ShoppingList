package com.example.shoppinglist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import com.example.shoppinglist.activities.ScrollingActivity
import com.example.shoppinglist.data.Item
import kotlinx.android.synthetic.main.new_item_dialog.*
import kotlinx.android.synthetic.main.new_item_dialog.view.*
import java.lang.RuntimeException
import java.util.*

class NewItemDialog : DialogFragment() {

    interface ItemHandler {
        fun itemCreated(item: Item)
        fun itemUpdated(item: Item)
        fun deleteAllItems()
    }

    private lateinit var itemHandler: ItemHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ItemHandler) {
            itemHandler = context
        } else {
            throw RuntimeException(
                activity?.getString(R.string.dialog_runtime_exception))
        }
    }

    private lateinit var spItemCategory: Spinner
    private lateinit var etItemName: EditText
    private lateinit var etItemDescription: EditText
    private lateinit var etItemPrice: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(R.string.new_item)

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_item_dialog, null
        )

        val categoriesAdapter = ArrayAdapter.createFromResource(
            activity,
            R.array.array_categories, android.R.layout.simple_spinner_item
        )
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spItemCategory = rootView.spinnerCategory

        spItemCategory.adapter = categoriesAdapter


        etItemName = rootView.etName
        etItemDescription = rootView.etDescription
        etItemPrice = rootView.etPrice
        builder.setView(rootView)

        val arguments = this.arguments

        if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
            val shoppingItem = arguments.getSerializable(ScrollingActivity.KEY_ITEM_TO_EDIT) as Item

            when (shoppingItem.itemCategory) {
                activity?.getString(R.string.food) -> spItemCategory.setSelection(1)
                activity?.getString(R.string.drink) -> spItemCategory.setSelection(2)
                activity?.getString(R.string.household_goods) -> spItemCategory.setSelection(3)
                activity?.getString(R.string.clothes) -> spItemCategory.setSelection(4)
                activity?.getString(R.string.other) -> spItemCategory.setSelection(5)
            }
            etItemName.setText(shoppingItem.itemName)
            etItemDescription.setText(shoppingItem.itemDescription)
            etItemPrice.setText(shoppingItem.itemPrice)

            builder.setTitle(R.string.edit_item)
        }

        builder.setPositiveButton(R.string.ok) {
                dialog, witch -> // empty
        }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etItemName.text.isNotEmpty()) {
                val arguments = this.arguments
                if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
                    handleItemEdit()
                } else {
                    handleItemCreate()
                }

                dialog.dismiss()
            } else {
                etItemName.error = activity?.getString(R.string.field_empty_error)
            }
        }
    }

    private fun handleItemCreate() {
        itemHandler.itemCreated(
            Item(
                null,
                getItemCategory(),
                etItemName.text.toString(),
                etItemDescription.text.toString(),
                etItemPrice.text.toString(),
                false
            )
        )
    }

    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM_TO_EDIT
        ) as Item
        itemToEdit.itemCategory = getItemCategory()
        itemToEdit.itemName = etItemName.text.toString()
        itemToEdit.itemDescription = etItemDescription.text.toString()
        itemToEdit.itemPrice = etItemPrice.text.toString()

        itemHandler.itemUpdated(itemToEdit)
    }

    private fun getItemCategory() : String {
        var itemCategory = ""

        when (spItemCategory.selectedItemPosition) {
            1 -> itemCategory = activity!!.getString(R.string.food)
            2 -> itemCategory = activity!!.getString(R.string.drink)
            3 -> itemCategory = activity!!.getString(R.string.household_goods)
            4 -> itemCategory = activity!!.getString(R.string.clothes)
            5 -> itemCategory = activity!!.getString(R.string.other)
        }

        return itemCategory
    }

}

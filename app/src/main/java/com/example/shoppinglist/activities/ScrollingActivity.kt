package com.example.shoppinglist.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import com.example.shoppinglist.NewItemDialog
import com.example.shoppinglist.R
import com.example.shoppinglist.adaptor.ItemAdaptor
import com.example.shoppinglist.data.AppDatabase
import com.example.shoppinglist.data.Item
import com.example.shoppinglist.touch.ItemRecyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ScrollingActivity : AppCompatActivity(), NewItemDialog.ItemHandler {

    lateinit var itemAdaptor: ItemAdaptor

    companion object {
        const val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
        const val KEY_WAS_OPEN = "KEY_WAS_OPEN"
        const val TAG_ITEM_DIALOG = "TAG_ITEM_DIALOG"
        const val EDIT_ITEM_DIALOG = "EDIT_ITEM_DIALOG"
        const val tutorialPrimaryText = R.string.tutorial_primary
        const val tutorialSecondaryText = R.string.tutorial_secondary
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            showAddItemDialog()
        }

        if (!wasOpenedEarlier()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText(tutorialPrimaryText)
                .setSecondaryText(tutorialSecondaryText)
                .show()
        }

        saveFirstOpenInfo()

        initRecyclerViewFromDB()

    }

    private fun saveFirstOpenInfo() {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean(KEY_WAS_OPEN, true)
        editor.apply()
    }

    private fun wasOpenedEarlier(): Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean(KEY_WAS_OPEN, false)
    }

    private fun initRecyclerViewFromDB() {
        Thread {
            var itemsList = AppDatabase.getInstance(this@ScrollingActivity).itemDao().getAllItems()

            runOnUiThread {
                itemAdaptor = ItemAdaptor(this, itemsList)
                recyclerItem.layoutManager = LinearLayoutManager(this)
                recyclerItem.adapter = itemAdaptor

                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                recyclerItem.addItemDecoration(itemDecoration)

                val callback = ItemRecyclerTouchCallback(itemAdaptor)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerItem)
            }

        }.start()
    }

    private fun showAddItemDialog() {
        NewItemDialog().show(supportFragmentManager, TAG_ITEM_DIALOG)
    }

    var editIndex: Int = -1

    fun showEditItemDialog(itemToEdit: Item, idx: Int) {
        editIndex = idx
        val editItemDialog = NewItemDialog()
        val bundle = Bundle()

        bundle.putSerializable(KEY_ITEM_TO_EDIT, itemToEdit)
        editItemDialog.arguments = bundle
        editItemDialog.show(supportFragmentManager, EDIT_ITEM_DIALOG)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item?.itemId == R.id.action_new_item) {
            showAddItemDialog()
        } else if (item?.itemId == R.id.action_delete_all) {
            deleteAllItems()
        }

        return super.onOptionsItemSelected(item)

    }

    override fun itemCreated(item: Item) {
        Thread {
            val newId = AppDatabase.getInstance(this).itemDao().insertItem(item)
            item.itemId = newId
            runOnUiThread {
                itemAdaptor.addItem(item)
            }
        }.start()
    }

    override fun itemUpdated(item: Item) {
        Thread {
            AppDatabase.getInstance(this@ScrollingActivity).itemDao().updateItem(item)
            runOnUiThread {
                itemAdaptor.updateItem(item, editIndex)
            }
        }.start()
    }

    override fun deleteAllItems() {
        Thread {
            AppDatabase.getInstance(this@ScrollingActivity).itemDao().deleteAll()
            runOnUiThread{
                itemAdaptor.deleteAll()
            }
        }.start()
    }

}

package com.example.shoppinglist.data

import android.arch.persistence.room.*

@Dao
interface ItemDAO {
    @Query("SELECT * FROM shopping")
    fun getAllItems(): List<Item>

    @Insert
    fun insertItem(item: Item): Long

    @Insert
    fun insertMultipleItems(vararg item: Item): List<Long>

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateItem(item: Item)

    @Query("DELETE FROM shopping")
    fun deleteAll()
}
package com.example.shoppinglist.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shopping")
data class Item(
    @PrimaryKey(autoGenerate = true) var itemId: Long?,
    @ColumnInfo(name = "category") var itemCategory: String,
    @ColumnInfo(name = "name") var itemName: String,
    @ColumnInfo(name = "description") var itemDescription: String,
    @ColumnInfo(name = "price") var itemPrice: String,
    @ColumnInfo(name = "status") var done: Boolean
) : Serializable
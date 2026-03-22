package com.example.checkbill.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(
    val id: String,
    val name: String
) : Parcelable

@Parcelize
data class BillItem(
    val id: String,
    val name: String,
    val price: Double,
    val sharedBy: List<String> = emptyList() // Store Member IDs who share this item
) : Parcelable

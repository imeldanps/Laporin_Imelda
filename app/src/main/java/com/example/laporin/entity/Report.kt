package com.example.laporin.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Report(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var location: String = "",
    var imageUrl: String = "",
    var status: String = "Menunggu"
) : Parcelable

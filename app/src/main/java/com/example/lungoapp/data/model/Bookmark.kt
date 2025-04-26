package com.example.lungoapp.data.model

import com.google.gson.annotations.SerializedName

data class Bookmark(
    @SerializedName("bm_id")
    val bookmarkId: Int,
    @SerializedName("word")
    val word: String,
    @SerializedName("word_tr")
    val wordTr: String,
    @SerializedName("user_id")
    val userId: Int
) 
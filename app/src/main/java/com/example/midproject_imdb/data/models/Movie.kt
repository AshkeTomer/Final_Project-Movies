package com.example.midproject_imdb.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(

    @ColumnInfo("title")
    val title:String,

    @ColumnInfo(name = "content_desc")
    val description:String,

    @ColumnInfo("image")
    val photo: String?=null,

    @ColumnInfo(name = "user_comments")
    val userComments: String? = null,

    @ColumnInfo(name = "rating")
    val rating: Float = 0.0f,

    @ColumnInfo(name = "release_date")
    val releaseDate: String = "",

    )


    {
        @PrimaryKey(autoGenerate = true)
        var id : Int = 0
    }
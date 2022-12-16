package com.example.myapplication.authentication.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userRegister")
data class RegisterEntity(
    @PrimaryKey
    @ColumnInfo(name = "emailAddress")
    var emailAddress: String,
    @ColumnInfo(name = "firstName")
    var firstName: String,
    @ColumnInfo(name = "lastName")
    var lastName: String,
    @ColumnInfo(name = "password")
    var password: String
    )
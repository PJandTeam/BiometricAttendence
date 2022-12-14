package com.app.biometricattendence.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userRegister")
data class RegisterEntity(
    @ColumnInfo(name = "name")
    var name: String,
    @PrimaryKey
    @ColumnInfo(name = "empId")
    var empId: String = "",
    @ColumnInfo(name = "dob")
    var dob: String,
    @ColumnInfo(name = "doj")
    var doj: String,
    @ColumnInfo(name = "mobile")
    var mobile: String,
    @ColumnInfo(name = "team")
    var team: String,
    @ColumnInfo(name = "time")
    var time: Long,
    @ColumnInfo(name = "total_worked_hours")
    var total_worked_hours: Int = 0
    )
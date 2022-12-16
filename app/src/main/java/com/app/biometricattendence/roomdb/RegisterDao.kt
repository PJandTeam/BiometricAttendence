package com.example.myapplication.authentication.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegisterDao {

    @Insert
    suspend fun insert(register: RegisterEntity)

    @Query("SELECT * FROM userRegister ORDER BY emailAddress DESC")
    fun getAllUsers(): LiveData<List<RegisterEntity>>

//    @Query("DELETE FROM Register_users_table")
//    suspend fun deleteAll(): Int

    @Query("SELECT * FROM userRegister WHERE emailAddress LIKE :emailAddress")
    suspend fun getEmailAddress(emailAddress: String): RegisterEntity?
}
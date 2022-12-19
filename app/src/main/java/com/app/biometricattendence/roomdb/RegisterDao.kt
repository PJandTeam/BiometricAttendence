package com.app.biometricattendence.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RegisterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(register: RegisterEntity)

    @Query("SELECT * FROM userRegister ORDER BY empId DESC")
    fun getAllUsers(): LiveData<List<RegisterEntity>>

//    @Query("DELETE FROM Register_users_table")
//    suspend fun deleteAll(): Int

    @Query("SELECT * FROM userRegister WHERE empId LIKE :empId")
    suspend fun getEmpId(empId: String): RegisterEntity?
}
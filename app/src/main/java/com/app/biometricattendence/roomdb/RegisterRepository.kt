package com.example.myapplication.authentication.roomdb

class RegisterRepository(private val dao: RegisterDao) {

    val users = dao.getAllUsers()
    suspend fun insert(user: RegisterEntity) {
        return dao.insert(user)
    }

    suspend fun getEmailAddress(emailAddress: String):RegisterEntity?{
        return dao.getEmailAddress(emailAddress)
    }
    //suspend fun deleteAll(): Int {
    //    return dao.deleteAll()
    //}

}
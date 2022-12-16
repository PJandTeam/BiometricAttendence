package com.app.biometricattendence.roomdb

class RegisterRepository(private val dao: RegisterDao) {

    val users = dao.getAllUsers()
    suspend fun insert(user: RegisterEntity) {
        return dao.insert(user)
    }

    suspend fun getEmpID(empId: String): RegisterEntity?{
        return dao.getEmpId(empId)
    }
    //suspend fun deleteAll(): Int {
    //    return dao.deleteAll()
    //}

}
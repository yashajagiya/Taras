package com.example.taras.api.apiClass

import com.example.taras.api.apiClass.apiLink.baseUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object F1ApiObject {

    //https://api.openf1.org/v1/championship_drivers?session_key=latest
     fun getObject (): Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getApiservice () : ApiF1DataInterface{
        return getObject().create(ApiF1DataInterface::class.java)
    }
}
package com.example.queuedemo_kotlin.Retrofit

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by Belal on 10/5/2017.
 */
interface SendQueueApi {
    //the base URL for our API
//make sure you are not using localhost
//find the ip usinc ipconfig command
//this is our multipart request
//we have two parameters on is name and other one is description
    @Multipart
    @POST("{id}")
    fun typePost(@Part("values") datavalues: RequestBody
                 ,@Part("DPTCODE") dptcode: RequestBody
                 ,@Part("tel") tel: RequestBody
                 , @Path(value = "id",encoded = true) id: String)
            : Call<retrofitData>


}


package com.example.queuedemo_tps700

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.queuedemo_kotlin.Retrofit.retrofitData
import com.example.queuedemo_kotlin.Retrofit.SendQueueApi
import com.google.gson.Gson
import io.socket.client.IO
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class TypeFragment : Fragment(), View.OnClickListener {

    var telnum: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_type, container, false)
        // Inflate the layout for this fragment
        val TypeA = v.findViewById<Button>(R.id.TypeAbtn)
        TypeA.setOnClickListener(this)
        val TypeB = v.findViewById<Button>(R.id.TypeBbtn)
        TypeB.setOnClickListener(this)
        val TypeC = v.findViewById<Button>(R.id.TypeCbtn)
        TypeC.setOnClickListener(this)

        val bundle = this.arguments

        if (bundle != null) {
            Log.d("telnum", "onCreateView: ${bundle.getString("telnum")}" )
            telnum = bundle.getString("telnum").toString()
        }

        return v
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.TypeAbtn -> {
                RequestQueue("กุมารเวชกรรม", "A",telnum)
            }
            R.id.TypeBbtn -> {
                RequestQueue("เวชศาสตร์ฟื้นฟู", "B",telnum)
            }
            R.id.TypeCbtn -> {
                RequestQueue("อายุรกรรม", "C",telnum)
            }

        }
    }


    fun RequestQueue(type: String, typecode: String, telnum: String) {
//        val URL = "http://18.139.84.245:4001/addQueue/"
        val URL = "http://10.0.0.205:4001/addQueue/"
        val ID = "5e14a0eba3d7600cac019599/"
        var Queue: String = ""
        var QueueRemain: String = ""

//                Log.d("urllll", URL)
//                val Upload = resources.getString(R.string.Upload)
//        dialogupload = ProgressDialog(context)
//        dialogupload!!.setMessage("please wait ...")
//        dialogupload!!.setCancelable(false)
//        dialogupload!!.show()


        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                requestBuilder.header("Accept", "application/json")
                return chain.proceed(requestBuilder.build())
            }
        })

        val httpClient = httpClientBuilder.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        var bodyType = type.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        var bodyTypeCode =
            typecode.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        var bodyTel =
            telnum.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())

        val api: SendQueueApi = retrofit.create<SendQueueApi>(SendQueueApi::class.java)
        val call: Call<retrofitData> = api.typePost(bodyType, bodyTypeCode, bodyTel, ID)

        //finally performing the call
        //finally performing the call
        call.enqueue(object : Callback<retrofitData> {
            override fun onResponse(
                call: Call<retrofitData>,
                response: Response<retrofitData>
            ) {
                if (response.isSuccessful) {
//                    ToastMessage().message(this@TypeActivity, R.string.fileuploadSuccess.toString())

                    val js = Gson().toJson(response.body())
                    var respData = response.body()

                    var json: JSONObject? = null
                    try {
                        json = JSONObject(js)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    val value: JSONObject = json?.get("message") as JSONObject
                    Queue = value.get("queue").toString()
                    QueueRemain = value.get("queueWait").toString()
//                    dialogupload!!.dismiss()
                    Log.d("ressss", js)
                    Log.d("ressss", value.toString())
                    Log.d("ressss", Queue)
                    Log.d("ressss", QueueRemain)

                    val socket = IO.socket("http://10.0.0.205:4001")
//                    val socket = IO.socket("http://18.139.84.245:4001")
                    // Sending an object
                    // Sending an object
                    val obj = JSONObject()
                    obj.put("addQueue", "Updated")
                    socket.emit("addQueue", obj)
                    socket.connect()

                    // Receiving an object
                    // Receiving an object
                    socket.on("addQueue") { args ->
                        socket.disconnect()
                    }

                    val bundle = Bundle()
                    bundle.putString("queue", Queue)
                    bundle.putString("queueRemain", QueueRemain)

                    val fragment = QueueFragment()
                    fragment.setArguments(bundle)

                    getFragmentManager()
                        ?.beginTransaction()
                        ?.replace(R.id.contaner, fragment)
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }

            override fun onFailure(call: Call<retrofitData>, t: Throwable) {
                Log.d("ressss", t.toString())
                PublicAction().Toastmessage(activity!!, t.message)
            }
        })
    }


}


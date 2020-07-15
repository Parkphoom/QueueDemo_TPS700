package com.example.queuedemo_tps700

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class UserInfoFragment : Fragment(),View.OnClickListener{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_user_info, container, false)

        val homebtn = v.findViewById<Button>(R.id.homepage_btn)
        homebtn.setOnClickListener(this)
        val nextbtn = v.findViewById<Button>(R.id.next_btn)
        nextbtn.setOnClickListener(this)

        return v
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.homepage_btn ->{
                val fragment = Service_Fragment()
                PublicAction().changefragment(activity!!,fragment)
            }
            R.id.next_btn ->{
                val fragment = InputTelnoFragment()
                PublicAction().changefragment(activity!!,fragment)
            }
        }
    }

}

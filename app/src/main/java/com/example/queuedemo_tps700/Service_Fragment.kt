package com.example.queuedemo_tps700

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class Service_Fragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_service, container, false)

        val btn = v.findViewById<Button>(R.id.checkPrivilege_btn)
        btn.setOnClickListener{
            val fragment = SelectCheckFragment()
            PublicAction().changefragment(activity!!,fragment)
        }

        return v
    }

}

package com.example.queuedemo_tps700

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class SelectCheckFragment : Fragment() , View.OnClickListener {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_selectcheck, container, false)
        // Inflate the layout for this fragment

        val icCard = v.findViewById<Button>(R.id.icCard)
        icCard.setOnClickListener {
            val fragment = CheckPrivilegeFragment()
            PublicAction().changefragment(activity!!,fragment) }
        return v
    }

    override fun onClick(v: View) {
        when (v.id) {
        R.id.icCard -> {
            val fragment = CheckPrivilegeFragment()
            PublicAction().changefragment(activity!!,fragment)
        }
    }
}


}

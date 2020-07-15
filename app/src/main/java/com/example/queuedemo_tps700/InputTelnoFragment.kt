package com.example.queuedemo_tps700

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment


class InputTelnoFragment : Fragment(), View.OnClickListener {

    var thaiIdcardText: EditText? = null
    var thaiIdcard: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_input_telno, container, false)
        initView(v)

        return v
    }

    private fun initView(v: View) {
        thaiIdcardText = v.findViewById(R.id.edittextTelnum)

        val zero = v.findViewById<Button>(R.id.zero_btn)
        zero.setOnClickListener(this)
        val one = v.findViewById<Button>(R.id.one_btn)
        one.setOnClickListener(this)
        val two = v.findViewById<Button>(R.id.two_btn)
        two.setOnClickListener(this)
        val three = v.findViewById<Button>(R.id.three_btn)
        three.setOnClickListener(this)
        val four = v.findViewById<Button>(R.id.four_btn)
        four.setOnClickListener(this)
        val five = v.findViewById<Button>(R.id.five_btn)
        five.setOnClickListener(this)
        val six = v.findViewById<Button>(R.id.six_btn)
        six.setOnClickListener(this)
        val seven = v.findViewById<Button>(R.id.seven_btn)
        seven.setOnClickListener(this)
        val eight = v.findViewById<Button>(R.id.eight_btn)
        eight.setOnClickListener(this)
        val nine = v.findViewById<Button>(R.id.nine_btn)
        nine.setOnClickListener(this)
        val delete = v.findViewById<Button>(R.id.delete_btn)
        delete.setOnClickListener(this)
        val nextbtn = v.findViewById<Button>(R.id.next_btn)
        nextbtn.setOnClickListener(this)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputTelnoFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.one_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "1"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }
            R.id.two_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "2"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }
            R.id.three_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "3"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }
            R.id.four_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "4"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }
            R.id.five_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "5"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }
            R.id.six_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "6"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }
            R.id.seven_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "7"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }
            R.id.eight_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "8"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }
            R.id.nine_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "9"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }
            R.id.zero_btn -> {
                if (thaiIdcard?.length!! < 10) {
                    thaiIdcard += "0"
                    thaiIdcardText?.setText(thaiIdcard)
                }

            }

            R.id.delete_btn -> {

                val length: Int = thaiIdcardText?.text!!.length
                if (length > 0) {
                    thaiIdcard = thaiIdcard!!.substring(0, thaiIdcard!!.length - 1)
                    thaiIdcardText!!.text.delete(length - 1, length)
                }
            }
            R.id.next_btn -> {
                when {
                    thaiIdcard.isNullOrEmpty() -> {
                        Toast.makeText(requireContext(), "กรุณากรอกเบอร์โทรศัพท์", Toast.LENGTH_SHORT)
                            .show()
                    }
                    thaiIdcard!!.length < 10 -> {
                        Toast.makeText(requireContext(), "เบอร์โทรศัพท์ไม่ครบ", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Log.d("telnum", "onClick: $thaiIdcard")
                        val bundle = Bundle()
                        bundle.putString("telnum", thaiIdcard) // Put anything what you want

                        val fragment = TypeFragment()
                        fragment.arguments = bundle
                        PublicAction().changefragment(activity!!, fragment)
                    }
                }

            }
        }
    }
}
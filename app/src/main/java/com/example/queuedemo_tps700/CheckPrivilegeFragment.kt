package com.example.queuedemo_tps700

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.queuedemo_tps700.ic.ScaniccardAction
import kotlinx.android.synthetic.main.fragment_check_privilege.*


class CheckPrivilegeFragment : Fragment(), View.OnClickListener {
    private var listener: OnFragmentInteractionListener? = null
    var thaiIdcardText: EditText? = null
    var thaiIdcard: String? = ""
    val scAction = ScaniccardAction()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_check_privilege, container, false)
        // Inflate the layout for this fragment

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
        val icbtn = v.findViewById<Button>(R.id.scanIC_btn)
        icbtn.setOnClickListener(this)
        val cancelbtn = v.findViewById<Button>(R.id.cancle_btn)
        cancelbtn.setOnClickListener(this)
        val homepagebtn = v.findViewById<Button>(R.id.gohomepage_btn)
        homepagebtn.setOnClickListener(this)
        val checkprivilege  = v.findViewById<Button>(R.id.checkPrivilege_btn)
        checkprivilege.setOnClickListener(this)
        val showHideBtn  = v.findViewById<Button>(R.id.showHideBtn)
        showHideBtn.setOnClickListener(this)
        val errorAlert  = v.findViewById<TextView>(R.id.errorAlert)
        errorAlert.setOnClickListener(this)

        thaiIdcardText = v.findViewById<EditText>(R.id.edittextIcCard)
        var hide = false
        showHideBtn.setOnClickListener {
            if(hide == false){
                thaiIdcardText!!.transformationMethod = HideReturnsTransformationMethod.getInstance()

                showHideBtn.setBackgroundResource(R.drawable.show)
                hide = true
            } else{
                thaiIdcardText!!.transformationMethod = PasswordTransformationMethod.getInstance()
                showHideBtn.setBackgroundResource(R.drawable.hide)
                hide = false
            }
        }

        scAction.PreRead(activity)



        return v
    }


    override fun onClick(v: View) {
        when (v.id) {

            R.id.one_btn -> {
                thaiIdcard += "1"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.two_btn -> {
                thaiIdcard += "2"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.three_btn -> {
                thaiIdcard += "3"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.four_btn -> {
                thaiIdcard += "4"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.five_btn -> {
                thaiIdcard += "5"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.six_btn -> {
                thaiIdcard += "6"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.seven_btn -> {
                thaiIdcard += "7"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.eight_btn -> {
                thaiIdcard += "8"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.nine_btn -> {
                thaiIdcard += "9"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.zero_btn -> {
                thaiIdcard += "0"
                thaiIdcardText?.setText(thaiIdcard)
            }
            R.id.delete_btn -> {

                val length: Int = thaiIdcardText?.getText()!!.length
                if (length > 0) {
                    thaiIdcard = thaiIdcard!!.substring(0, thaiIdcard!!.length - 1)
                    thaiIdcardText!!.getText().delete(length - 1, length)
                }
            }
            R.id.scanIC_btn -> {

                thaiIdcard = ""
                thaiIdcardText?.setText("")
                val dialog = ProgressDialog.show(activity, "", "กำลังอ่านบัตร...", true)
                dialog.show()
                val handler = Handler()
                handler.postDelayed({
                    //your code here
                    val dataReader = scAction.ReadAll(activity)
                    if(dataReader!= null){
                        thaiIdcardText?.setText(dataReader["CID"].toString())
                        thaiIdcard = dataReader["CID"].toString()
                        errorAlert.visibility = View.GONE
                    }

                    dialog.dismiss()
                }, 100)


            }
            R.id.cancle_btn -> {
                thaiIdcard = ""
                thaiIdcardText?.setText("")
            }
            R.id.gohomepage_btn -> {
                val fragment = Service_Fragment()
                PublicAction().changefragment(activity!!,fragment)
            }
            R.id.checkPrivilege_btn -> {
                if(thaiIdcard.isNullOrEmpty()){
                    errorAlert.setText("***กรุณากรอกเลขบัตรประชาชน***")
                    errorAlert.visibility = View.VISIBLE
                }else if(thaiIdcard!!.length < 13){
                    errorAlert.setText("***กรอกเลขบัตรประชาชนไม่ครบ***")
                    errorAlert.visibility = View.VISIBLE
                }
                else{
                    errorAlert.visibility = View.GONE
                    val fragment = UserInfoFragment()
                    PublicAction().changefragment(activity!!,fragment)
                }

            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }




}

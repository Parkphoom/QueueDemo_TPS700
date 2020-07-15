package com.example.queuedemo_tps700

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.queuedemo_tps700.printer.ThermalPrinterFunction
import com.szsicod.print.escpos.PrinterAPI
import com.szsicod.print.io.InterfaceAPI
import com.szsicod.print.io.USBAPI
import com.szsicod.print.log.Logger
import com.szsicod.print.utils.BitmapUtils.convertGreyImgByFloyd
import io.socket.utf8.UTF8
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class QueueFragment : Fragment(), View.OnClickListener {

    //Printer
    var mPrinter = PrinterAPI.getInstance()
    private var runnable: Runnable? = null
    private var progressDialog: ProgressDialog? = null

    var printHeader: String? = null
    var printborder: String? = null
    var queue: String = ""
    var QueueRemain: String? = ""

    private val connect_type_string = arrayOf("USB")
    private val REQUEST_CODE_PICK_IMAGE = 1
    private var editTextText: EditText? = null
    private var editTextBarcode: EditText? = null
    private var editTextQRCode: EditText? = null
    private var ll_root: LinearLayout? = null
    private var et_times: EditText? = null
    private var et_time: EditText? = null
    private var isPrintTextCut = false
    private var imageView: ImageView? = null
    private var spinnerConnectType: Spinner? = null
    private var button_print_opencd: Button? = null
    private var buttoncConnect: Button? = null
    private var buttoncDisonnect: Button? = null
    private var buttoncPrintText: Button? = null
    private var buttoncPrintPos: Button? = null
    private var button_print_loop: Button? = null
    private var buttoncPrintBarcode: Button? = null
    private var buttoncPrintQRCode: Button? = null
    private var buttoncPrintBitmap: Button? = null
    private var button_getversion: Button? = null
    private var button_update: Button? = null
    private var button_cut: Button? = null
    private var button_clear: Button? = null
    private var btnClearReceive: Button? = null
    private var button_cc: Button? = null
    private var button_print_58: Button? = null
    private var button_print_80: Button? = null
    private var button_print_left: Button? = null
    private var button_print_center: Button? = null
    private var button_print_right: Button? = null
    private var cb_print_bitmap_cut: CheckBox? = null
    private var cut_bitmap = false
    private var btnTextHex: Button? = null
    private var btn_yaoqiu: Button? = null
    private var cb_cut: CheckBox? = null
    private var etBarWidth: EditText? = null
    private var cb_barWidth: CheckBox? = null
    private var isSetBarHeight = false
    private var etQRsize: EditText? = null
    private var etReceiver: EditText? = null
    private var btnResetBd: Button? = null
    private var etResetBd: EditText? = null
    private var spBarSystem: Spinner? = null
    private var barSystem = 0
    private var statusFlag = false
    private var buttontest: Button? = null
    private var version: TextView? = null

    private var thermalprint: ThermalPrinterFunction? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_queue, container, false)

        val bundle = this.arguments
        val textQueue = v.findViewById<TextView>(R.id.textQueue)
        val textQueueRemain = v.findViewById<TextView>(R.id.textQueueRemain)

        val printbutton = v.findViewById<Button>(R.id.printbtn)
        printbutton.setOnClickListener(this)

        if (bundle != null) { // handle your code here.
            textQueue.text = bundle["queue"].toString()
            queue = bundle["queue"].toString()
            Log.d("bundlee", bundle["queue"].toString())

            textQueueRemain.text = "จำนวนคิวที่รอ : ${bundle.get("queueRemain")} คิว"
            QueueRemain = bundle["queueRemain"].toString()
            Log.d("bundlee", bundle["queueRemain"].toString())
        }



        return v
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.printbtn -> {
                val fragment = Service_Fragment()
                PublicAction().changefragment(activity!!,fragment)
                printHeader = ("\nWAC"
                        + "\n*************************"
                        + "\n$queue"
                        + "\nจำนวนคิวที่รอ : $QueueRemain คิว")


//                custom()
                thermalprint?.printBitmap(activity!!, queue, QueueRemain.toString())
            }

        }
    }

    override fun onResume() {
        super.onResume()
        //scrollView.scrollTo(0,0);
//解决大尺寸不能滑到顶部的问题
//        ll_root = findViewById<View>(R.id.ll_root) as LinearLayout
//        ll_root!!.isFocusable = true
//        ll_root!!.isFocusableInTouchMode = true
//        ll_root!!.requestFocus()


//        Connect_Devices()
    }

    private fun Connect_Devices() {
        thermalprint = ThermalPrinterFunction()
        thermalprint?.progressRun(activity!!)
        runnable = Runnable {
            var io: InterfaceAPI? = null
            io = USBAPI(activity)
            if (PrinterAPI.SUCCESS == mPrinter!!.connect(io)) {
                thermalprint?.handler?.sendEmptyMessage(0)
            } else {
                thermalprint?.handler?.sendEmptyMessage(1)
            }
        }
        Thread(runnable).start()
    }
}

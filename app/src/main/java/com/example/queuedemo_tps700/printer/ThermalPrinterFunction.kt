package com.example.queuedemo_tps700.printer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.queuedemo_tps700.PublicAction
import com.example.queuedemo_tps700.QueueFragment
import com.example.queuedemo_tps700.R
import com.example.queuedemo_tps700.Service_Fragment
import com.szsicod.print.escpos.PrinterAPI
import com.szsicod.print.io.InterfaceAPI
import com.szsicod.print.io.USBAPI
import com.szsicod.print.log.Logger
import com.szsicod.print.utils.BitmapUtils
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class ThermalPrinterFunction {

    //Printer
    var mPrinter = PrinterAPI.getInstance()
    private var runnable: Runnable? = null
    private var progressDialog: ProgressDialog? = null
    private var statusFlag = false
    val activity = FragmentActivity()

    companion object {
        private const val STATUS = 3
        private const val openfileDialogId = 0
        private const val NO_CONNECT = 10
        fun GetTime(): String {
            val simpleDateFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // HH:mm:ss
            val date = Date(System.currentTimeMillis())
            return simpleDateFormat.format(date)
        }
    }

    val isConnect: Boolean
        get() {
            var isConnect = true
            if (!mPrinter!!.isConnect) {
                handler.sendEmptyMessage(NO_CONNECT)
                isConnect = false
            }
            return isConnect
        }

    private var states = false //开始打印标志

    internal inner class StateThread : Runnable {
        override fun run() { //延时启动，过热检测不能一开始就检测
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            while (!statusFlag) {
                if (mPrinter!!.isConnect) {
                    if (!mPrinter!!.statusOverhead) {
                        activity?.runOnUiThread(Runnable {
                            showTip("Hint", "The printer is overheat, pls print later")
                            statusFlag = true //关闭线程
                            states = false //打印完成
                            return@Runnable
                        })
                    }
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    //参数为超时时间 ms 如果再这段时间内没有数据返回,说明正在打印 这个只对缺纸处理
                    val status = mPrinter!!.getStatusForPrinting(5)
                    if (status == 1) {
                        Logger.i("打印完成")
                        if (states) {
                            activity?.runOnUiThread {
                                //                                showTip("Hint", "Print complete")
                                statusFlag = true //关闭线程
                                states = false //打印完成
                            }
                        } else {
                            statusFlag = true //关闭线程
                        }
                    } else if (status == 2) {
                        Logger.i("缺纸")
                        if (states) {
                            activity?.runOnUiThread {
                                // ProgressDialog.show(this@PrintActivity,"提示","缺纸",true,true);
                                showTip("Hint", "No paper")
                                statusFlag = true //关闭线程
                                states = false //打印完成
                            }
                        }
                    } else if (status == 0) {
                        Logger.i("打印中")
                        states = true //开始打印
                    } else {
                        Logger.i("数据传输错误")
                    }
                }
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun showTip(title: String?, message: String?) {
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Confirm", null)
            .show()
    }

    fun createBitmapp(word: String, textsize: Float): Bitmap {
        val width = 500;
        val height = 90;
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        val canvas = Canvas(bitmap);

        val paint = Paint()
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(textsize);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(word, (width / 2F), (height / 2F), paint);
//
        return bitmap
    }


    fun custom() {
        if (!isConnect) {
            Toast.makeText(
                activity, "not Connect",
                Toast.LENGTH_SHORT
            ).show()
        }
        //todo
        runnable = Runnable {
            //                val mess = etQRsize!!.text.toString()
//                val mess = "4"
//                if ("" == mess || null == mess) {
//                    return@Runnable
//                }
            //居中
            mPrinter!!.setAlignMode(1)
            //设置行距
            mPrinter!!.setLineSpace(10)
            //打印二维码
//                if (PrinterAPI.SUCCESS == mPrinter!!.printQRCode(mess, 8, false)) {
            mPrinter!!.printFeed()
            try {
//                        if (PrinterAPI.SUCCESS == mPrinter!!.printString(printborder, "GBK", true)) { //换行n*行距
                mPrinter!!.printAndFeedLine(2)
//                            mPrinter!!.printString("TestTestTestTestTest" + "", "GBK", true)
                mPrinter!!.setCharSize(2, 0)

                mPrinter!!.printString("จำนวนคิวที่รอ", "TIS620", true)
                SystemClock.sleep(500)
                //设置字符的大小
//                            mPrinter!!.setCharSize(1, 0)
//                            mPrinter!!.printString("Other Print Test", "GBK", false)
                //恢复
                mPrinter!!.setDefaultLineSpace()
                mPrinter!!.setCharSize(0, 0)
                mPrinter!!.printFeed()
                mPrinter!!.printAndFeedLine(5)
                mPrinter!!.fullCut()


//                        }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
//                }
        }
        Thread(runnable).start()
    }


    @SuppressLint("HandlerLeak")
    private open inner class NoLeakHandler internal constructor(activity: FragmentActivity?) :
        Handler() {
        var wf: WeakReference<FragmentActivity?>? = null

        init {
            wf = WeakReference(activity)
        }
    }

    var handler: Handler =
        @SuppressLint("HandlerLeak")
        object : NoLeakHandler(activity) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    PrinterAPI.ERR_PARAM -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            activity, "Parameter error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    PrinterAPI.NOSUPPROT -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            activity, "Strings are not supported",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    PrinterAPI.FAIL -> progressDialog!!.dismiss() // 销毁对话框
                    PrinterAPI.SUCCESS -> {
                        progressDialog!!.dismiss() // 销毁对话框
//                        Toast.makeText(
//                            activity,
//                            R.string.func_success,
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                    PrinterAPI.STRINGLONG -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            activity, "Length is not supported please re-enter",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    1 -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            activity,
                            "func failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    2 -> Toast.makeText(
                        activity, "socket connect fail",
                        Toast.LENGTH_SHORT
                    ).show()
                    STATUS -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            activity, msg.obj as String,
                            Toast.LENGTH_SHORT
                        ).show()
//                        etReceiver!!.setText("")
//                        etReceiver!!.setText("The current state is:" + msg.obj as String)
                    }
//                    0x100 -> etReceiver!!.setText("")
                    NO_CONNECT -> {
//                        etReceiver!!.setText("")
//                        etReceiver!!.setText("Current device isn't connected")
                        Toast.makeText(
                            activity, "Current device isn't connected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                super.handleMessage(msg)
            }
        }


    fun progressRun(context: FragmentActivity) {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
            //   progressDialog.cancel();
        }
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle(R.string.print_posing)
        progressDialog!!.setMessage("Please waiting...")
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER) // 设置进度条对话框//样式（水平，旋转）
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    var thread: Thread? = null

    fun startState() {
        if (thread != null) {
            statusFlag = true //关闭线程
            states = false //打印完成
            thread = null
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        statusFlag = false
        states = true
        thread = Thread(StateThread())
        thread!!.start()
    }


    fun printBitmap(context: FragmentActivity, queue: String, QueueRemain: String) {
        if (!isConnect) return
        progressRun(context)
        startState()

//        val width = 120
//        val height = 60
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val canvas =  Canvas(bitmap)
//
//        val paint =  Paint()
//        paint.setColor(Color.WHITE)
//        paint.setStyle(Paint.Style.FILL)
//        canvas.drawPaint(paint)
//
//        paint.setColor(Color.BLACK)
//        paint.setAntiAlias(true)
//        paint.setTextSize(16F)
//        paint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText(printHeader!!, 120F, 60F, paint)

        val queueTitle = createBitmapp("คิวลำดับที่", 40F)
        val queueRemainTitle = createBitmapp("จำนวนคิวที่รอ $QueueRemain คิว", 30F)
        runnable = object : Runnable {
            override fun run() {

                try {
                    mPrinter!!.setAlignMode(1)
                    if (PrinterAPI.SUCCESS == mPrinter!!.printRasterBitmap(toGrayscale(queueTitle))) {
                        mPrinter!!.setCharSize(5, 5)
                        mPrinter!!.printString(queue, "UTF-8", true)
                        mPrinter!!.printRasterBitmap(toGrayscale(queueRemainTitle))
                        handler.sendEmptyMessage(0)
                        mPrinter!!.printAndFeedLine(1)
                    } else {
                        handler.sendEmptyMessage(1)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                mPrinter!!.printFeed()
                mPrinter!!.fullCut()
                //清除打印缓存区 避免对后面数据的影响 有些设置会恢复默认值
                mPrinter!!.init()
                mPrinter!!.initAllPrinter(2)

                val fragment = Service_Fragment()
                PublicAction().changefragment(context, fragment)
            }
        }
        Thread(runnable).start()
    }

    // 40mm*8=320px
    fun cut(bmp: Bitmap?): Int {
        var bitmap: Bitmap? = null
        var height = 120
        var avgHeight = bmp!!.height / height
        var top = 0
        if (bmp.height % height != 0) {
            avgHeight++
        }
        for (i in 0 until avgHeight) {
            if (top + height > bmp.height) height = bmp.height - top
            bitmap = Bitmap.createBitmap(bmp, 0, top, bmp.width, height)
            top = top + height
            try {
                mPrinter!!.printRasterBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                return -1
            }
        }
        return 0
    }

    fun toGrayscale(bmpOriginal: Bitmap?): Bitmap {
        val width: Int
        val height: Int
        height = bmpOriginal!!.height
        width = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return BitmapUtils.convertGreyImgByFloyd(bmpGrayscale)
    }
}

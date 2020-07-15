package com.example.queuedemo_tps700.printer

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.queuedemo_tps700.R
import com.szsicod.print.escpos.PrinterAPI
import com.szsicod.print.io.InterfaceAPI
import com.szsicod.print.io.USBAPI
import com.szsicod.print.log.Logger
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class PrintActivity : AppCompatActivity() {
    private val connect_type_string = arrayOf("USB")
    var mPrinter = PrinterAPI.getInstance()
    private val REQUEST_CODE_PICK_IMAGE = 1
    private var runnable: Runnable? = null
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
    private var progressDialog: ProgressDialog? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print)

        buttoncPrintText = findViewById<View>(R.id.printBtn) as Button
        buttoncPrintText!!.setOnClickListener {
//            printQRcode()
//            printText()
            custom()
            // Log.i(TAG, "onClick: " + mPrinter.getPrinterVersion() + mPrinter.getPrinterVersionNew());
        }

//        findView()
//        this@PrintActivity = this
//        version!!.text = "Version：20181024"

        setListener()
    }

    private fun setListener() {
//        printText()
//        printTextLoop()
//        printBitmap()
//        printBarcode()
//        printQRcode()
//        sendHex()
//        setAlignType()
//        cutPage()
//        printerVersion
//        update()
//        selectPageSize()
//        clearText()
//        openCashBox()
//        selectImagePrint()
//        printTextAndHex()
        custom()
//        btnClearReceive!!.setOnClickListener { etReceiver!!.setText("") }
    }

    //客户要求的打印格式
     fun custom() {
//        btn_yaoqiu!!.setOnClickListener(View.OnClickListener {
        buttoncPrintText!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            //todo
            runnable = Runnable {
//                val mess = etQRsize!!.text.toString()
                val mess = "4"
                if ("" == mess || null == mess) {
                    return@Runnable
                }
                //居中
                mPrinter!!.setAlignMode(1)
                //设置行距
                mPrinter!!.setLineSpace(10)
                //打印二维码
                if (PrinterAPI.SUCCESS == mPrinter!!.printQRCode(mess, 8, false)) {
                    mPrinter!!.printFeed()
                    try {
                        if (PrinterAPI.SUCCESS == mPrinter!!.printString("121212121151515", "GBK", true))
                        { //换行n*行距
                            mPrinter!!.printAndFeedLine(8)
                            mPrinter!!.printString("TestTestTestTestTest" + "", "GBK", true)
                            mPrinter!!.setCharSize(2, 0)
                            mPrinter!!.printString("1111111111", "GBK", true)
                            SystemClock.sleep(500)
                            mPrinter!!.printAndFeedLine(6)
                            //设置字符的大小
//                            mPrinter!!.setCharSize(1, 0)
//                            mPrinter!!.printString("Other Print Test", "GBK", false)
                            //恢复
                            mPrinter!!.setDefaultLineSpace()
                            mPrinter!!.setCharSize(0, 0)
                            mPrinter!!.printFeed()
                            mPrinter!!.fullCut()
                        }
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                }
            }
            Thread(runnable).start()
        })
    }

    //打印文本和识别文本的十六进制,现在支持 文本+指令,指令+文本 ,指令+文本+指令,指令
    private fun printTextAndHex() {
        btnTextHex!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            startState()
            Thread(Runnable {
                try {
                    textHex(editTextText!!.text.toString())
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    Log.i("log", "run: $e")
                }
            }).start()
        })
    }

    private fun selectImagePrint() {
        imageView!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            imageFromAlbum
        })
    }

    private fun openCashBox() {
        button_print_opencd!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            runnable = Runnable { mPrinter!!.openCashDrawer(0, 50, 100) }
            Thread(runnable).start()
        })
    }

    private fun clearText() {
        button_clear!!.setOnClickListener { editTextText!!.setText("") }
    }

    private fun selectPageSize() {
        button_print_58!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            startState()
            mPrinter!!.set58mm()
        })
        button_print_80!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            startState()
            mPrinter!!.set80mm()
        })
    }

    private fun update() {
        button_update!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            showDialog(openfileDialogId)
        })
    }

    //				progressRun();
    private val printerVersion: Unit
        private get() {
            button_getversion!!.setOnClickListener(View.OnClickListener {
                if (!isConnect) return@OnClickListener
                //				progressRun();
                runnable = Runnable {
                    val s: String =
                        StatusDescribe.getStatusDescribe(mPrinter!!.status)
                    val message = Message()
                    message.obj = s
                    message.what =
                        STATUS
                    handler.sendMessage(message)
                    Logger.i("status$s")
                }
                Thread(runnable).start()
            })
        }

    private fun cutPage() {
        button_cut!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            progressRun()
            //startState();
            runnable = Runnable {
                if (PrinterAPI.SUCCESS == mPrinter!!.fullCut()) {
                    handler.sendEmptyMessage(0)
                } else {
                    handler.sendEmptyMessage(1)
                }
            }
            Thread(runnable).start()
        })
    }

    private fun printBitmap() {
        buttoncPrintBitmap!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            progressRun()
            startState()
            runnable = object : Runnable {
                private val resources = getResources()
                override fun run() {
                    imageView!!.isDrawingCacheEnabled = true
                    if (cut_bitmap) {
                        if (PrinterAPI.SUCCESS == cut(
                                toGrayscale(imageView!!.getDrawingCache())
                            )
                        ) {
                            try {
                                mPrinter!!.printAndFeedLine(5)
                                Thread.sleep(100)
                                mPrinter!!.fullCut()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            handler.sendEmptyMessage(0)
                        } else {
                            handler.sendEmptyMessage(1)
                        }
                    } else {
                        try {
                            if (PrinterAPI.SUCCESS == mPrinter!!.printRasterBitmap(
                                    toGrayscale(imageView!!.getDrawingCache())
                                )
                            ) {
                                handler.sendEmptyMessage(0)
                                mPrinter!!.printAndFeedLine(3)
                            } else {
                                handler.sendEmptyMessage(1)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    imageView!!.isDrawingCacheEnabled = false
                    mPrinter!!.printFeed()
                    //清除打印缓存区 避免对后面数据的影响 有些设置会恢复默认值
                    mPrinter!!.init()
                    mPrinter!!.initAllPrinter(2)
                }
            }
            Thread(runnable).start()
        })
    }

    private fun setAlignType() {
        button_print_left!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            mPrinter!!.setAlignMode(0)
        })
        button_print_center!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            mPrinter!!.setAlignMode(1)
        })
        button_print_right!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            mPrinter!!.setAlignMode(2)
        })
    }

    private fun printQRcode() {
        buttoncPrintText!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            progressRun()
            startState()
            runnable = Runnable {
//                val a = editTextQRCode!!.text.toString()
                val a = "8798756465"
//                val size = etQRsize!!.text.toString().trim { it <= ' ' }
                val size = "20"
                if ("" == size) {
                    handler.sendEmptyMessage(PrinterAPI.ERR_PARAM)
                    return@Runnable
                }
                val i = size.toInt()
                if (PrinterAPI.SUCCESS == mPrinter!!.printQRCode(a, i, false)) {
                    try {
                        mPrinter!!.printString(a, "GBK", true)
                        mPrinter!!.printAndFeedLine(5)
                        mPrinter!!.fullCut()
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                    handler.sendEmptyMessage(0)
                } else {
                    handler.sendEmptyMessage(1)
                }
            }
            Thread(runnable).start()
        })
    }

    private fun printBarcode() {
        buttoncPrintBarcode!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            progressRun()
            runnable = Runnable {
                val size =
                    etBarWidth!!.text.toString().trim { it <= ' ' }
                if ("" != size && isSetBarHeight) {
                    try {
                        val num = size.toInt()
                        mPrinter!!.setBarCodeWidth(num)
                    } catch (e: Exception) {
                    }
                } else {
                    mPrinter!!.setBarCodeWidth(2)
                }
                val mess = editTextBarcode!!.text.toString()
                try {
                    val PrintStates =
                        mPrinter!!.printBarCode(barSystem, mess.length, mess)
                    if (PrinterAPI.SUCCESS == PrintStates) {
                        startState()
                        mPrinter!!.printAndFeedLine(3)
                        handler.sendEmptyMessage(0)
                    } else if (PrinterAPI.STRINGLONG == PrintStates) {
                        handler.sendEmptyMessage(-3)
                    } else {
                        handler.sendEmptyMessage(-1)
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
            Thread(runnable).start()
        })
    }

    private fun printTextLoop() {
        button_print_loop!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            if (CheakStates() != 1) return@OnClickListener
            val times = Integer
                .valueOf(et_times!!.text.toString())
            val time = Integer.valueOf(et_time!!.text.toString())
            progressRun()
            //开始状态轮训
            startState()
            //mPrinter.setAlignMode(1);
            runnable = Runnable {
                try {
                    for (i in 0 until times) {
                        if (PrinterAPI.SUCCESS == mPrinter!!.printString(
                                editTextText!!.text.toString(),
                                "GBK",
                                true
                            )
                        ) {
                            mPrinter!!.printAndFeedLine(5)
                            mPrinter!!.fullCut()
                            Thread.sleep(time.toLong())
                        } else {
                            handler.sendEmptyMessage(1)
                        }
                    }
                    handler.sendEmptyMessage(0)
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            Thread(runnable).start()
        })
    }

    /**
     * 按十六进制符打印
     * 需要启动一次 Printer.comeInHex()；
     * 就回进入hex打印模式
     * 如果想要退出需要按走纸键三次或者重启打印机
     * 否则其他命令无效
     * Printer.comeInHex()只需要执行一次就可以
     */
    private fun sendHex() {
        buttoncPrintPos!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            Log.i(ContentValues.TAG, "onClick: hex")
            mPrinter!!.comeInHex()
            runnable = Runnable { mPrinter!!.hexMsg("##" + "abc" + "##") }
            Thread(runnable).start()
        })
    }

    private fun printText() {
        buttoncPrintText!!.setOnClickListener(View.OnClickListener {
            if (!isConnect) return@OnClickListener
            if (CheakStates() != 1) return@OnClickListener
            progressRun()
            runnable = Runnable {
                try { //开始状态轮训
                    startState()
                    mPrinter!!.initAllPrinter(2)
                    //设置字体
                    mPrinter!!.fontSizeSet(8)
                    mPrinter!!.setFontStyle(0)
                    mPrinter!!.setAlignMode(1)
                    //mPrinter.setAlignMode(1);//居中1
                    if (PrinterAPI.SUCCESS == mPrinter!!.printString("asdasdd 123456", "GBK", true)) {
                        try {
                            mPrinter!!.printAndFeedLine(6)
                            Thread.sleep(100)
                            mPrinter!!.fullCut()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }

//                        if (isPrintTextCut) {
//                            try {
//                                mPrinter!!.printAndFeedLine(5)
//                                Thread.sleep(100)
//                                mPrinter!!.fullCut()
//                            } catch (e: InterruptedException) {
//                                e.printStackTrace()
//                            }
//                        } else {
//                            mPrinter!!.printAndFeedLine(5)
//                        }
                        handler.sendEmptyMessage(0)
                    } else {
                        handler.sendEmptyMessage(1)
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
            Thread(runnable).start()
        })
    }



    private fun Connect_Devices() {
        progressRun()
        runnable = Runnable {
            var io: InterfaceAPI? = null
            io = USBAPI(this@PrintActivity)
            if (PrinterAPI.SUCCESS == mPrinter!!.connect(io)) {
                handler.sendEmptyMessage(0)
            } else {
                handler.sendEmptyMessage(1)
            }
        }
        Thread(runnable).start()
    }


    var handler: Handler =
        object : NoLeakHandler(this@PrintActivity) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    PrinterAPI.ERR_PARAM -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            this@PrintActivity, "Parameter error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    PrinterAPI.NOSUPPROT -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            this@PrintActivity, "Strings are not supported",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    PrinterAPI.FAIL -> progressDialog!!.dismiss() // 销毁对话框
                    PrinterAPI.SUCCESS -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            this@PrintActivity,
                            R.string.func_success,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    PrinterAPI.STRINGLONG -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            this@PrintActivity, "Length is not supported please re-enter",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    1 -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            this@PrintActivity,
                            R.string.func_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    2 -> Toast.makeText(
                        this@PrintActivity, "socket connect fail",
                        Toast.LENGTH_SHORT
                    ).show()
                    STATUS -> {
                        progressDialog!!.dismiss() // 销毁对话框
                        Toast.makeText(
                            this@PrintActivity, msg.obj as String,
                            Toast.LENGTH_SHORT
                        ).show()
                        etReceiver!!.setText("")
                        etReceiver!!.setText("The current state is:" + msg.obj as String)
                    }
                    0x100 -> etReceiver!!.setText("")
                    NO_CONNECT -> {
                        etReceiver!!.setText("")
                        etReceiver!!.setText("Current device isn't connected")
                        Toast.makeText(
                            this@PrintActivity, "Current device isn't connected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                super.handleMessage(msg)
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
        Connect_Devices()
    }

    protected val imageFromAlbum: Unit
        protected get() {
            val intent =
                Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            mPrinter!!.setAlignMode(1)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }



    fun createBitmapp(): Bitmap {
        val b =
            Bitmap.createBitmap(imageView!!.drawingCache)
        val bottomBmp =
            Bitmap.createBitmap(300, 3600, Bitmap.Config.RGB_565)
        bottomBmp.eraseColor(Color.parseColor("#ffffff"))
        val canvas = Canvas()
        canvas.setBitmap(bottomBmp)
        val textPaint = Paint()
        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL
        textPaint.textScaleX = 32f
        for (i in 0..34) {
            canvas.drawText("123213", 20f, i * 100 + 80.toFloat(), textPaint)
        }
        canvas.drawBitmap(b, 220f, 0f, null)
        return bottomBmp
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

    @Deprecated("")
    override fun onCreateDialog(id: Int): Dialog? {
        if (id == openfileDialogId) {
            val images: MutableMap<String, Int> =
                HashMap()
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
            images[OpenFileDialog.sRoot] =
                R.drawable.filedialog_root // 根目录图标
            images[OpenFileDialog.sParent] =
                R.drawable.filedialog_folder_up // 返回上一层的图标
            images[OpenFileDialog.sFolder] =
                R.drawable.filedialog_folder // 文件夹图标
            images["bin"] =
                R.drawable.ic_launcher_foreground // bin文件图标
            images[OpenFileDialog.sEmpty] =
                R.drawable.filedialog_root
            return OpenFileDialog.createDialog(
                id, this, "open",
                object :
                    OpenFileDialog.CallbackBundle {
                    override fun callback(bundle: Bundle) {
                        val filepath = bundle.getString("path")
                        firmwareUpdate(filepath)
                    }
                }, ".bin;", images
            )
        }
        return null
    }

    private fun progressRun() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
            //   progressDialog.cancel();
        }
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(R.string.print_posing)
        progressDialog!!.setMessage("Please waiting...")
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER) // 设置进度条对话框//样式（水平，旋转）
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun firmwareUpdate(filepath: String?) {
        progressRun()
        runnable = Runnable {
            val file = File(filepath)
            if (mPrinter!!.updateFirmware(file) == 0) {
                handler.sendEmptyMessage(0)
            } else {
                handler.sendEmptyMessage(1)
            }
        }
        Thread(runnable).start()
    }

    /***
     * 图片去色,返回灰度图片
     *
     * @param bmpOriginal
     * 传入的图片
     * @return 去色后的图片
     */
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
        return convertGreyImgByFloyd(bmpGrayscale)
    }

    fun convertGreyImgByFloyd(img: Bitmap): Bitmap {
        val width = img.width // 获取位图的宽
        val height = img.height // 获取位图的高
        val pixels = IntArray(width * height) // 通过位图的大小创建像素点数组
        img.getPixels(pixels, 0, width, 0, 0, width, height)
        val gray = IntArray(height * width)
        for (i in 0 until height) {
            for (j in 0 until width) {
                val grey = pixels[width * i + j]
                val red = grey and 0x00FF0000 shr 16
                gray[width * i + j] = red
            }
        }
        var e = 0
        for (i in 0 until height) {
            for (j in 0 until width) {
                val g = gray[width * i + j]
                if (g >= 128) {
                    pixels[width * i + j] = -0x1
                    e = g - 255
                } else {
                    pixels[width * i + j] = -0x1000000
                    e = g - 0
                }
                if (j < width - 1 && i < height - 1) { // 右边像素处理
                    gray[width * i + j + 1] += 3 * e / 8
                    // 下
                    gray[width * (i + 1) + j] += 3 * e / 8
                    // 右下
                    gray[width * (i + 1) + j + 1] += e / 4
                } else if (j == width - 1 && i < height - 1) { // 靠右或靠下边的像素的情况
// 下方像素处理
                    gray[width * (i + 1) + j] += 3 * e / 8
                } else if (j < width - 1 && i == height - 1) { // 右边像素处理
                    gray[width * i + j + 1] += e / 4
                }
            }
        }
        val mBitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.RGB_565
        )
        mBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return mBitmap
    }

    @Throws(UnsupportedEncodingException::class)
    fun textHex(text: String?): Boolean {
        var text = text
        if (null == text || "" == text) {
            return false
        }
        val hex = ""
        text = text.trim { it <= ' ' }
        val length = text.length
        val pattern = Pattern.compile("\\#\\#+")
        val matcher = pattern.matcher(text)
        var count = 0
        val position = ArrayList<Int>()
        while (matcher.find()) {
            count++
            Log.i("log", "textHex1: start" + matcher.start())
            position.add(matcher.start())
        }
        if (count <= 1) {
            if ("" != text) {
                return if (mPrinter!!.printString(text, "GBK", false) == 0) true else false
            }
        }
        if (length < 4) {
            return false
        }
        if (position.size == 2) {
            val start = position[0]
            val end = position[1]
            //文本+指令
            if (start > 0 && text.length - 1 == end + 1) {
                val mess = text.substring(0, start)
                Log.i("log", "textHex1: $mess")
                val cmd = text.substring(start, text.length)
                Log.i("log", "textHex1: $cmd")
                if (mPrinter!!.printString(mess, "GBK", false) == PrinterAPI.SUCCESS) {
                    SystemClock.sleep(500)
                    mPrinter!!.hexMsg(cmd)
                    handler.sendEmptyMessage(0)
                } else {
                    handler.sendEmptyMessage(1)
                }
            }
            //只有指令
            if (start == 0 && text.length - 1 == end + 1) {
                mPrinter!!.hexMsg(text)
                return true
            }
            //指令+文本
            if (start == 0 && end + 1 < text.length - 1) {
                val cmd = text.substring(0, end + 2)
                Log.i("log", "textHex1: $cmd")
                val mess = text.substring(end + 2)
                if (mPrinter!!.hexMsg(cmd) == PrinterAPI.SUCCESS) {
                    SystemClock.sleep(500)
                    mPrinter!!.printString(mess, "GBK", false)
                    handler.sendEmptyMessage(0)
                } else {
                    handler.sendEmptyMessage(1)
                }
                Log.i("log", "textHex1: $mess")
                return true
            }
            //文本+指令+文本
            if (start > 0 && end + 1 < text.length - 1) {
                val front_mess = text.substring(0, start)
                Log.i("log", "textHex1: $front_mess")
                val cmd = text.substring(start, end + 2)
                val after_mess = text.substring(end + 2)
                Log.i("log", "textHex1: $cmd")
                Log.i("log", "textHex1: $after_mess")
                if (mPrinter!!.printString(front_mess, "GBK", false) == PrinterAPI.SUCCESS) {
                    SystemClock.sleep(500)
                    mPrinter!!.hexMsg(cmd)
                    if ("" != after_mess) {
                        SystemClock.sleep(500)
                        mPrinter!!.printString(after_mess, "GBK", false)
                    }
                    handler.sendEmptyMessage(0)
                } else {
                    handler.sendEmptyMessage(1)
                }
                return true
            }
        }
        if (position.size == 4) {
            var cmd = ""
            var mess = ""
            var cmd2 = ""
            var after_mess = ""
            if (position[3] + 1 == text.length - 1) {
                cmd = text.substring(0, position[1] + 2)
                mess = text.substring(position[1] + 2, position[2])
                cmd2 = text.substring(position[2])
                // 指令+文本+指令+文本
            } else if (position[3] + 1 < text.length - 1) {
                cmd = text.substring(0, position[1] + 2)
                mess = text.substring(position[1] + 2, position[2])
                cmd2 = text.substring(position[2], position[3] + 2)
                after_mess = text.substring(position[3] + 2)
            }
            if (mPrinter!!.hexMsg(cmd) == PrinterAPI.SUCCESS) {
                SystemClock.sleep(500)
                if (mPrinter!!.printString(mess, "GBK", false) == -1) {
                    return false
                }
                if ("" != cmd2) {
                    SystemClock.sleep(500)
                    mPrinter!!.hexMsg(cmd2)
                }
                if ("" != after_mess) {
                    SystemClock.sleep(500)
                    if (mPrinter!!.printString(after_mess, "GBK", false) == -1) {
                        return false
                    }
                }
                handler.sendEmptyMessage(0)
            } else {
                handler.sendEmptyMessage(1)
            }
            return true
        }
        //不符规则 当做字符窜处理
        return if (mPrinter!!.printString(text, "GBK", false) == 0) true else false
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

    private open inner class NoLeakHandler internal constructor(activity: PrintActivity) :
        Handler() {
        var wf: WeakReference<PrintActivity>? = null

        init {
            wf = WeakReference(activity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (mPrinter != null) {
            mPrinter!!.disconnect()
            mPrinter = null
            Logger.i("资源已经关闭")
        }
        statusFlag = true
    }

    /**
     * 开启轮询
     */
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
                        runOnUiThread(Runnable {
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
                            runOnUiThread {
                                showTip("Hint", "Print complete")
                                statusFlag = true //关闭线程
                                states = false //打印完成
                            }
                        } else {
                            statusFlag = true //关闭线程
                        }
                    } else if (status == 2) {
                        Logger.i("缺纸")
                        if (states) {
                            runOnUiThread {
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

    /**
     * 打印前判断
     *
     * @return
     */
    fun showTip(title: String?, message: String?) {
        AlertDialog.Builder(this@PrintActivity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Confirm", null)
            .show()
    }

    private fun CheakStates(): Int {
        StatusDescribe.getStatusDescribe(mPrinter!!.status)
        if (!mPrinter!!.statusOverhead) {
            showTip("Hint", "Printer Overheat")
            return 0
        }
        if (StatusDescribe.Headlift()) {
            showTip("Hint", "Cover open")
            return 0
        }
        if (StatusDescribe.isOutOfPaper()) {
            showTip("Hint", "Nopaper")
            return 0
        }
        if (StatusDescribe.isPaperDone()) {
            val dialog =
                synDialog(this@PrintActivity, "The paper will run out, whether to continue print")
            dialog.setTitle("Hint")
            return dialog.showDialog()
        }
        return 1
    }

    private val str1 = "---------------------------------\n" + String.format(
        "%1$-31s \n",
        "Store：Test Store1"
    ) + String.format(
        "%1$-31s \n",
        GetTime()
    ) +
            "---------------------------------\n" + String.format(
        "%1$-12s" + "%2$-9s" + "%3$-5s" + "%4$-9s\n",
        "Product",
        "Price",
        "Number",
        "Total"
    ) + String.format(
        "%1$-12s" + "%2$-9s" + "%3$-5s" + "%4$-9s\n",
        "Product",
        "100.10",
        "1",
        "1.1"
    )
    private val str = "-------------------------------\n" +
            "Store：Test Store 1 \n" +
            "Date：2018-10-11 11：11\n" +
            "-------------------------------\n" +
            "Product          Price       Number     Total\n" +
            "Test Product 1\n" +
            "111111111111     1.1          1         1.1\n" +
            "Test Product 2\n" +
            "222222222222     2.2          1         2.2\n" +
            "Test Product 3\n" +
            "222222222222     2.2          1         2.2\n" +
            "Test Product 4\n" +
            "222222222222     2.2          1         2.2\n" +
            "-------------------------------\n" +
            " Product Number：4                 Total：7.7\n" +
            "Payment：WechatPay                          7.7\n" +
            "Order No：1111111111111111"

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
}
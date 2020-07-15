package com.example.queuedemo_tps700.ic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.queuedemo_tps700.PublicAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

import amlib.ccid.Error;
import amlib.ccid.Reader;
import amlib.hw.HWType;
import amlib.hw.HardwareInterface;

public class ScaniccardAction {

    private Reader mReader;
    private HardwareInterface mMyDev;
    private UsbDevice mUsbDev;
    private UsbManager mManager;
    AlertDialog.Builder mSlotDialog;
    AlertDialog.Builder mPowerDialog;
    private byte mSlotNum;
    private PendingIntent mPermissionIntent;

    private Button testcard;

    private ArrayAdapter<String> mReaderAdapter;
    private TextView mTextViewReader;
    private EditText mEditTextApdu;
    private ProgressDialog mCloseProgress;
    //private EditText mEditTextMode;

    private Spinner mModeSpinner;
    private Spinner mReaderSpinner;
    private ArrayAdapter<String> mModeList;
    //	private ArrayAdapter<String> mModeAdapter;
    private String mStrMessage;
    private final String mode2 = "I2c Mode";
    private final String mode3 = "SLE4428 Mode";
    private final byte DEFAULT_SN_LEN = 32;
    private Context mContext;


    private static final String TAG = "Alcor-Test";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    public void PreRead(Context context){
        mSlotNum = (byte)0;
        Log.d(TAG," onCreate");
        try {
            mMyDev = new HardwareInterface(HWType.eUSB);

        }catch(Exception e){
            mStrMessage = "Get Exception : " + e.getMessage();
            Log.e(TAG, mStrMessage);

        }
        // Get USB manager
        Log.d(TAG," mManager");
        mManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        toRegisterReceiver(context);

        EnumeDev(context);
        Log.d("tessss", String.valueOf(EnumeDev(context)));

    }

    public JSONObject ReadAll(Context context) {
        //****  SET POWER ON ****//
        int ret;
        ret = poweron();
        JSONObject resultReader = new JSONObject();
        PublicAction pa = new PublicAction();

        if (ret == Error.READER_SUCCESSFUL) {
            pa.Toastmessage(context,"power on successfully");
            resultReader = getATR();
            return resultReader;
            //textViewResult.setText("power on successfully");
        } else if (ret == Error.READER_NO_CARD) {
            pa.Toastmessage(context,"No card");
            return null;
        } else {
            pa.Toastmessage(context,"power on fail:"+  Integer.toString(ret));
            return null;
            //textViewResult.setText("power on fail:"+  Integer.toString(ret));
        }

    }

    private JSONObject getATR(){
        JSONObject objReader = new JSONObject();
        //***** GET ATR*********//
        String atr;
        try {
            atr = mReader.getAtrString();
            //mTextViewResult.setText(" ATR:"+ atr);
        } catch (Exception e) {
            mStrMessage = "Get Exception : " + e.getMessage();
        }
        //***********************//


        byte[] recByte = null;
        recByte = TestSendAPDUcommand("00A4040008A000000054480001"); //SELECT COMMAND

        recByte = TestSendAPDUcommand("80B0000402000D"); //CID
        recByte = TestSendAPDUcommand("00C000000D"); //CID
        String str_CID = null;
        try {
            str_CID = new String(recByte, "TIS620");
            str_CID = str_CID.substring(0, 13);
            try {
                objReader.put("CID",str_CID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        str_public_SID = str_CID;

        String[] arrayInfo;

        recByte = TestSendAPDUcommand("80B00011020064"); //FULL NANE TH
        recByte = TestSendAPDUcommand("00C0000064"); //FULL NANE TH
        String fn_th = null;
        try {
            fn_th = new String(recByte, "TIS620");
            fn_th = fn_th.replace("\n", "");
            arrayInfo = fn_th.split("\\#", -1);
            String fullnameTH = arrayInfo[0] + "" + arrayInfo[1] + " " + arrayInfo[2] + "" + arrayInfo[3];
            fullnameTH = fullnameTH.replaceAll("\\s+", " ");
            fullnameTH = fullnameTH.replace("\\0", "").replace("\0", "");
            try {
                objReader.put("NameTH",fullnameTH);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("tessss",fullnameTH);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        recByte = TestSendAPDUcommand("80B00075020064"); //FULL NANE EN
        recByte = TestSendAPDUcommand("00C0000064"); //FULL NANE EN
        String fn_en = null;
        try {
            fn_en = new String(recByte, "TIS620");
            fn_en = fn_en.replace("\n", "");
            arrayInfo = fn_en.split("\\#", -1);
            String fullnameEN = arrayInfo[0] + "" + arrayInfo[1] + " " + arrayInfo[2] + "" + arrayInfo[3];
            fullnameEN = fullnameEN.replaceAll("\\s+", " ");
            fullnameEN = fullnameEN.replace("\\0", "").replace("\0", "");
            try {
                objReader.put("NameEN",fullnameEN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("tessss",fullnameEN);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return objReader;


//        recByte = TestSendAPDUcommand("80B000E1020001"); //GENDER
//        recByte = TestSendAPDUcommand("00C0000001"); //GENDER
//        String str_Gender = null;
//        try {
//            str_Gender = new String(recByte, "TIS620");
//            boolean b = str_Gender.startsWith("1");
//            if (b == true) {
//                str_Gender = "ชาย";
//            } else {
//                str_Gender = "หญิง";
//            }
////            txtGender.setText("เพศ : " + str_Gender);
//            Log.d("tessss",str_Gender);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        recByte = TestSendAPDUcommand("80B000D9020008"); //DOB
//        recByte = TestSendAPDUcommand("00C0000008"); //DOB
//        String str_DOB = null;
//        try {
//            str_DOB = new String(recByte, "TIS620");
//            str_DOB = str_DOB.substring(0, 8);
//            //txtDOB.setText("เกิดวันที่ : " + str_DOB.substring(6,8) + "/" + str_DOB.substring(4,6) + "/" + str_DOB.substring(0,4));
//            Log.d("tessss","เกิดวันที่ : " + str_DOB.substring(6,8) + "/" + str_DOB.substring(4,6) + "/" + str_DOB.substring(0,4));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        recByte = TestSendAPDUcommand("80B00167020012"); //ISSUE/EXPIRE
//        recByte = TestSendAPDUcommand("00C0000012"); //ISSUE/EXPIRE
//        String str_ISSUEEXPIRE = null;
//        try {
//            str_ISSUEEXPIRE = new String(recByte, "TIS620");
//            String strIssue = str_ISSUEEXPIRE.substring(0, 8);
//            String strExpire = str_ISSUEEXPIRE.substring(8, 16);
////            txtIssue.setText("วันออกบัตร : " + strIssue.substring(6, 8) + "/" + strIssue.substring(4, 6) + "/" + strIssue.substring(0, 4));
////            txtExpire.setText("วันบัตรหมดอายุ : " + strExpire.substring(6, 8) + "/" + strExpire.substring(4, 6) + "/" + strExpire.substring(0, 4));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//
//        recByte = TestSendAPDUcommand("80B01579020064"); //Address
//        recByte = TestSendAPDUcommand("00C0000064"); //Address
//        String strAddress = null;
//        try {
//            strAddress = new String(recByte, "TIS620");
//            strAddress = strAddress.replace("#", " ");
//            strAddress = strAddress.replaceAll("\\s+", " ");
//            strAddress = strAddress.replace("\\0", "").replace("\0", "");
////            txtAddress.setText("ที่อยู่ : " + strAddress);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

//
//        recByte = TestSendAPDUcommand("00A4040008A000000054480001"); //SELECT COMMAND
//
//        //#Photo
//        int[] pRevAPDULen = new int[1];
//        byte[] COMMAND;
//        byte[] recvBuffer;
//        String hexstring = "";
//        String tmp;
//        int r;
//
//        for (r = 0; r <= 20; r++) {
//            COMMAND = new byte[]{
//                    (byte) 0x80,
//                    (byte) 0xB0,
//                    (byte) ((byte) 0x01 + r),
//                    (byte) ((byte) 0x7B - r),
//                    (byte) 0x02,
//                    (byte) 0x00,
//                    (byte) 0xFF};
//            recvBuffer = new byte[2];
//            mReader.transmit(COMMAND, COMMAND.length, recvBuffer, pRevAPDULen);
//            COMMAND = new byte[]{(byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) 0xFF};
//            recvBuffer = new byte[255];
//            mReader.transmit(COMMAND, COMMAND.length, recvBuffer, pRevAPDULen);
//
//            tmp = bytesToHex(recvBuffer);
//            hexstring = hexstring + tmp;
//        }
//
//        byte[] byteRawHex = hexStringToByteArray(hexstring);
//        imgBase64String = Base64.encodeToString(byteRawHex, Base64.NO_WRAP);
//
//        Bitmap bitmapCard = BitmapFactory.decodeByteArray(byteRawHex, 0, byteRawHex.length);
//        imgPhoto.setImageBitmap(bitmapCard);
//        imgPhoto.setImageBitmap(bitmapCard);
//
//
//        //*** Feature Save Data To Device ***//
//        //++ save text + picture
//        str_All_Data = txtCID.getText() + "\n" +
//                txtFullnameTH.getText() + "\n" +
//                txtFullnameEN.getText() + "\n" +
//                txtGender.getText() + "\n" +
//                txtDOB.getText() + "\n" +
//                txtIssue.getText() + "\n" +
//                txtExpire.getText() + "\n" +
//                txtAddress.getText() + "\n" +
//                txtAddress2.getText() + "\n" +
//                txtPhone.getText() + "\n" +
//                txtEmail.getText();
//
//        writeToText(str_All_Data);
//        String filename = str_public_SID + ".png";
//
//        File dest = new File(sd, filename);
//        try {
//            FileOutputStream out = new FileOutputStream(dest);
//            bitmapCard.compress(Bitmap.CompressFormat.PNG, 90, out);
//            out.flush();
//            out.close();
//            galleryAddPic(dest.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String path = sd + "/" + filename;
//        File imgFile = new File(path);
//        if (imgFile.exists()) {
//            //pic4Mail = imgFile;
//        }
//        //---
//
//        //++ save csv
//        writeToCsv(str_All_Data);
//        //--
//        //++ save excel
//        writeToExcel(str_All_Data, imgFile);  // export to Excel
        //--
        //************************//
        //new CloseTask().execute();
    }

    private byte[] TestSendAPDUcommand(String strAPDU) {
        byte[] pSendAPDU;
        byte[] pRecvRes = new byte[300];
        int[] pRevAPDULen = new int[1];
        String apduStr;
        int sendLen, result;

        pRevAPDULen[0] = 300;
        apduStr = strAPDU.trim();
        pSendAPDU = toByteArray(apduStr);
        sendLen = pSendAPDU.length;

        try {
            result = mReader.transmit(pSendAPDU, sendLen, pRecvRes, pRevAPDULen);
            if (result == Error.READER_SUCCESSFUL) {
                //mTextViewResult.setText("Receive APDU: "+ logBuffer(pRecvRes, pRevAPDULen[0]));
                return pRecvRes;
            } else {
//                mTextViewResult.setText("Fail to Send APDU: " + Integer.toString(result)
//                        + "("+ Integer.toHexString(mReader.getCmdFailCode()) +")");
                Log.e(TAG, "Fail to Send APDU: " + Integer.toString(result)
                        + "(" + Integer.toHexString(mReader.getCmdFailCode()) + ")");
                return null;
            }
        } catch (Exception e) {
            mStrMessage = "Get Exception : " + e.getMessage();
            Log.e(TAG, mStrMessage);
            return null;
        }
    }

    private byte[] toByteArray(String hexString) {

        int hexStringLength = hexString.length();
        byte[] byteArray = null;
        int count = 0;
        char c;
        int i;

        // Count number of hex characters
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {

                    byteArray[len] = (byte) (value << 4);

                } else {

                    byteArray[len] |= value;
                    len++;
                }

                first = !first;
            }
        }

        return byteArray;
    }

    private int poweron() {
        int result = Error.READER_SUCCESSFUL;
        Log.d(TAG, "poweron");
        //check slot status first
        if (getSlotStatus() == Error.READER_NO_CARD) {
            Log.d(TAG, "Card Absent");
            return Error.READER_NO_CARD;
        }
        try {
            result = mReader.setPower(Reader.CCID_POWERON);
        } catch (Exception e) {
            Log.e(TAG, "PowerON Get Exception : " + e.getMessage());
        }
        return result;
    }

    private int getSlotStatus() {
        int ret = Error.READER_NO_CARD;
        byte[] pCardStatus = new byte[1];

        /*detect card hotplug events*/
        try {
            if (mReader.getCardStatus(pCardStatus) == Error.READER_SUCCESSFUL) {
                //Log.d(TAG,"cmd OK  mSlotStatus = " +mSlotStatus);
                if (pCardStatus[0] == Reader.SLOT_STATUS_CARD_ABSENT) {
                    ret = Error.READER_NO_CARD;
                } else
                    ret = Error.READER_SUCCESSFUL;
            }
        } catch (Exception e) {
            mStrMessage = "Get Exception : " + e.getMessage();
//            mTextViewResult.setText(mStrMessage);
        }
        return ret;
    }
    private void toRegisterReceiver(Context context){
        // Register receiver for USB permission
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(mReceiver, filter);
    }

     private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Broadcast Receiver");

            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            onDevPermit(device);
                        }
                    } else {
                        Log.d(TAG, "Permission denied for device " + device.getDeviceName());
                    }
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                Log.d(TAG, "Device Detached");
                onDetache(intent);

//                synchronized (this) {
//                    updateReaderList(intent);
//                }

            }
        }/*end of onReceive(Context context, Intent intent) {*/
    };

    private int EnumeDev(Context context)
    {
        UsbDevice device = null;
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        Log.d(TAG," EnumeDev");
//        mReaderAdapter.clear();
        while(deviceIterator.hasNext()){
            device = deviceIterator.next();
            Log.d(TAG," "+ Integer.toHexString(device.getVendorId()) +" " +Integer.toHexString(device.getProductId()));
            if(isAlcorReader(device))
            {
                Log.d(TAG,"Found Device");
                device.getDeviceName();
                for (UsbDevice device1 : mManager.getDeviceList().values()) {
                    if (device.getDeviceName().equals(device1.getDeviceName())) {
                        UsbDevice dev = device1;
                        if (dev != null)
                            checkSlotNumber(dev,device1);
                    }
                }

            }
        }
        //requestDevPerm();

        return 0;
    }

    private boolean isAlcorReader(UsbDevice udev){
        if (udev.getVendorId() == 0x058f
                && ((udev.getProductId() == 0x9540) || (udev.getProductId() == 0x9560)
                || (udev.getProductId() == 0x9520)  || (udev.getProductId() == 0x9522)
                || (udev.getProductId() == 0x9525) || (udev.getProductId() == 0x9526))
        )
            return true;
        return false;
    }

    private void checkSlotNumber(UsbDevice uDev, UsbDevice deviceName){
        if(uDev.getProductId() == 0x9522 || uDev.getProductId() == 0x9525 ||uDev.getProductId() == 0x9526 )
            mSlotDialog.show();
        else{
            mSlotNum = (byte)0;
            requestDevPerm(deviceName);
        }
    }

    private void requestDevPerm(UsbDevice deviceName){
        UsbDevice dev = deviceName;
        if (dev != null)
            mManager.requestPermission(dev, mPermissionIntent);
        else
            Log.e(TAG,"selected not found");
    }

    private void onDevPermit(UsbDevice dev){
        mUsbDev = dev;
        try {
            new OpenTask().execute(dev);
        }
        catch(Exception e){
            mStrMessage = "Get Exception : " + e.getMessage();
            Log.e(TAG, mStrMessage);
        }
    }


    private void onDetache(Intent intent){
        UsbDevice   udev = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (udev != null ) {
            if (udev.equals(mUsbDev) ){
                closeReaderUp();
            }
        }
        else {
            Log.d(TAG,"usb device is null");
        }
    }
    private int closeReaderUp(){
        Log.d(TAG, "Closing reader...");
        int ret = 0;

        if (mReader!= null)
        {
            ret = mReader.close();
        }
        return ret;
    }

    private class OpenTask extends AsyncTask<UsbDevice, Void, Integer> {

        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(UsbDevice... params) {
            int status = 0;
            try {
                status = InitReader() ;
                if ( status != 0){
                    Log.e(TAG, "fail to initial reader");
                    return status;
                }
                //status = mReader.connect();
            } catch (Exception e) {
                mStrMessage = "Get Exception : " + e.getMessage();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != 0) {
//                mTextViewResult.setText("Open fail: "+ Integer.toString(result));
                Log.e(TAG,"Open fail: "+ Integer.toString(result));
            }else{
//                mTextViewResult.setText("Open successfully");
                Log.e(TAG,"Open successfully");
            }
        }
    }

    private int InitReader()
    {
        int Status = 0;
        boolean init;//
        Log.d(TAG, "InitReader");
        try {
            init = mMyDev.Init(mManager, mUsbDev);
            if (!init){
                Log.e(TAG, "Device init fail");
                return -1;
            }
            //	mMyDev.setLog(mContext,true, 0xff);
            mReader = new Reader(mMyDev);
            mReader.setSlot(mSlotNum);

        }
        catch(Exception e){

            mStrMessage = "Get Exception : " + e.getMessage();
            Log.e(TAG, mStrMessage);
            return -1;
        }
        return Status;
    }


}

package com.example.queuedemo_tps700.printer;
import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.queuedemo_tps700.R;

/**
 * 同步对话框，等待用户点击执行对应的操作
 */
public class synDialog extends Dialog
{
    private int dialogResult;
    private Handler mHandler ;
    private static int CANCEL=0;
    private  static int OK=1;
    public synDialog(Activity context, String mailName)
    {

        super(context);
        setOwnerActivity(context);
        onCreate();
        TextView promptLbl = (TextView) findViewById(R.id.textView);
        promptLbl.setText(mailName);
    }
    public void setDialogResult(int dialogResult)
    {
        this.dialogResult = dialogResult;
    }

    public void onCreate() {
        setContentView(R.layout.dialog_tip);
        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView)
            {
                endDialog(synDialog.CANCEL);
            }
        });
        findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView)
            {
                endDialog(synDialog.OK);
            }
        });
    }

    public void endDialog(int result)
    {
        dismiss();
        setDialogResult(result);
        Message m = mHandler.obtainMessage();
        mHandler.sendMessage(m);
    }

    public int showDialog()
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                // process incoming messages here
                //super.handleMessage(msg);
                throw new RuntimeException();
            }
        };
        super.show();
        try {
            Looper.getMainLooper().loop();
        }
        catch(RuntimeException e2)
        {
        }
        return dialogResult;
    }

}
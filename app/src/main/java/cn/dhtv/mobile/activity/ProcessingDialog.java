package cn.dhtv.mobile.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import cn.dhtv.mobile.R;

/**
 * Created by Jack on 2015/6/12.
 */
public class ProcessingDialog extends Dialog {
    public ProcessingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_processing);
    }
}

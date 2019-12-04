package com.kirer.rxdelayaction.validator;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.kirer.lib.RxDelayAction;
import com.kirer.lib.Validator;

/**
 * @author Kirer
 * @create 2019/12/2
 * @Describe
 */
public class ProtocolValidator implements Validator {

    private Context mContext;
    private boolean isAgree;

    public ProtocolValidator(Context context) {
        this.mContext = context;
    }

    public void setAgree(boolean agree) {
        isAgree = agree;
    }

    @Override
    public boolean check() {
        return isAgree;
    }

    @Override
    public void doValid() {
        new AlertDialog.Builder(mContext)
                .setTitle("Protocol")
                .setMessage("Agree the Protocol")
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isAgree = true;
                        RxDelayAction.getInstance().doContinue();
                    }
                })
                .create()
                .show();
    }

}

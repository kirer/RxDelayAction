package com.kirer.rxdelayaction.validator;

import android.os.Handler;
import android.os.Message;

import com.kirer.lib.DisposableValidator;

/**
 * @author Kirer
 * @create 2019/12/2
 * @Describe
 */
public class ProtocolPreValidator extends DisposableValidator {

    private OnProtocolPreListener mListener;

    public interface OnProtocolPreListener {
        void onComplete(boolean isAgree);
    }

    public ProtocolPreValidator(OnProtocolPreListener listener) {
        this.mListener = listener;
    }

    @Override
    public void doDisposableValid() {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(12 == msg.what){
                    if(null != mListener){
                        mListener.onComplete(false);
                    }
                    postCompleted();
                }
            }
        };
        // 模拟网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    handler.sendEmptyMessage(12);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

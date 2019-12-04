package com.kirer.rxdelayaction;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kirer.lib.RxDelayAction;
import com.kirer.lib.Action;
import com.kirer.lib.Validator;
import com.kirer.rxdelayaction.validator.AuthValidator;
import com.kirer.rxdelayaction.validator.LoginValidator;
import com.kirer.rxdelayaction.validator.ProtocolValidator;
import com.kirer.rxdelayaction.validator.ProtocolPreValidator;

/**
 * @author Kirer
 * @create 2019/12/4
 * @Describe
 */
public class MainActivity extends AppCompatActivity {

    private TextView mLoginStatusTv;
    private TextView mAuthStatusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoginStatusTv = findViewById(R.id.login_status_tv);
        mAuthStatusTv = findViewById(R.id.auth_status_tv);
        initViewData();
    }

    private void initViewData() {
        mLoginStatusTv.setText("Login::" + App.getInstance().isLogin());
        mAuthStatusTv.setText("Auth::" + App.getInstance().isAuth());
    }

    public void onShowDialog(View view) {
        final ProtocolValidator protocolValidator = new ProtocolValidator(this);
        ProtocolPreValidator protocolPreValidator = new ProtocolPreValidator(new ProtocolPreValidator.OnProtocolPreListener() {
            @Override
            public void onComplete(boolean isAgree) {
                protocolValidator.setAgree(isAgree);
            }
        });
        RxDelayAction
                .getInstance()
                .create()
                .lifecycle(this)
                .addValidator(protocolPreValidator)
                .addValidator(protocolValidator)
                .addValidator(new AuthValidator(this))
                .action(new Action() {
                    @Override
                    public void onVerified(Validator valid) {
                        if (valid instanceof LoginValidator) {
                            mLoginStatusTv.setText("Login::" + App.getInstance().isLogin());
                        }
                        if (valid instanceof AuthValidator) {
                            mAuthStatusTv.setText("Auth::" + App.getInstance().isAuth());
                        }
                    }

                    @Override
                    public void onCompleted() {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Dialog")
                                .setMessage("This is content")
                                .create()
                                .show();
                    }
                })
                .doCall();

    }

    public void onDoNothing(View view) {
        RxDelayAction
                .getInstance()
                .create()
                .lifecycle(this)
                .addValidator(new LoginValidator(this))
                .addValidator(new AuthValidator(this))
                .doCall();
    }

    public void onLoginStatusChange(View view) {
        App.getInstance().setLogin(!App.getInstance().isLogin());
        initViewData();
    }

    public void onAuthStatusChange(View view) {
        App.getInstance().setAuth(!App.getInstance().isAuth());
        initViewData();
    }


}

package com.kirer.rxdelayaction;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kirer.lib.RxDelayAction;

/**
 * @author Kirer
 * @create 2019/12/2
 * @Describe
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLogin(View view) {
        App.getInstance().setLogin(true);
        RxDelayAction.getInstance().doContinue();
        finish();
    }

}

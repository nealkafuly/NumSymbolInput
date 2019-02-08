package com.kafuly.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.LinearLayout;

import com.kafuly.keyboard.KeyboardType;
import com.kafuly.keyboard.SecurityConfigure;
import com.kafuly.keyboard.SecurityKeyboard;


public class MainActivity extends AppCompatActivity {
    SecurityKeyboard securityKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.main_me_rl);
        SecurityConfigure securityConfigure = new SecurityConfigure();
        securityConfigure.setDefaultKeyboardType(KeyboardType.NUMBER);
        securityKeyboard = new SecurityKeyboard(layout, securityConfigure);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (securityKeyboard != null) {
            securityKeyboard.release();
        }
    }
}

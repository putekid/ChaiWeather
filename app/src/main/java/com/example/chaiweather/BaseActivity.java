package com.example.chaiweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.chaiweather.util.ActivityCollector;
import com.example.chaiweather.util.LogUtil;

public class BaseActivity extends AppCompatActivity {

    protected static int backPressedCount = 0;
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG,this.getClass().getSimpleName() + "onCreate()调用");
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        if(backPressedCount == 0){
            Toast.makeText(this,"再次点击返回键退出程序", Toast.LENGTH_SHORT).show();
            backPressedCount++;
        }else if(backPressedCount > 0){
            backPressedCount = 0;
            ActivityCollector.finishAll();
        }
    }
}

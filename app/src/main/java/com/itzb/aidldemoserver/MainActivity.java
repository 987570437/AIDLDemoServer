package com.itzb.aidldemoserver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String name = "123";
    private String pwd = "456";
    private EditText etName;
    private EditText etPwd;
    private ILoginInterface iLoginInterface;
    private boolean isBindServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        etName = findViewById(R.id.et_name);
        etPwd = findViewById(R.id.et_pwd);
        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        bindResultService();//开启远程服务

    }

    private void bindResultService() {
        Intent intent = new Intent();
        intent.setAction("result_action");//设置服务端应用action(服务唯一标识)
        intent.setPackage("com.itzb.aidldemoclient");//设置Server服务端包名
        bindService(intent, conn, BIND_AUTO_CREATE);//启动服务
        //改变标志位
        isBindServer = true;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iLoginInterface = ILoginInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void login() {
        name = etName.getText().toString();
        pwd = etPwd.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(MainActivity.this, "账号或者密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("登录");
        dialog.setMessage("登录中......");
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                SystemClock.sleep(2000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismiss();
                            boolean status = false;
                            if ("123".equals(name) || "456".equals(pwd)) {
                                status = true;
                                Log.d("zb", "数据匹配成功");
                            } else {
                                status = false;
                                Log.d("zb", "数据匹配失败");
                            }
                            iLoginInterface.loginCallback(status, name);
                            finish();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (isBindServer) {
            unbindService(conn);
        }
        super.onDestroy();
    }
}

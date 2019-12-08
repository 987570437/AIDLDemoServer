package com.itzb.aidldemoserver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.itzb.aidldemoserver.ILoginInterface;
import com.itzb.aidldemoserver.MainActivity;

public class LoginService extends Service {
    public LoginService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ILoginInterface.Stub() {
            @Override
            public void login() throws RemoteException {
                Log.d("zb", "服务端收到了: ");

                Intent intent = new Intent(LoginService.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void loginCallback(boolean loginStatus, String loginName) throws RemoteException {

            }
        };
    }
}

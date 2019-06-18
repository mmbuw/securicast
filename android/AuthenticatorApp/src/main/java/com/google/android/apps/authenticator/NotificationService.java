package com.google.android.apps.authenticator;

import android.app.IntentService;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * This service manages the response from the user it gives during the authentication process.
 *
 * @author Thomas Dressel
 */

public class NotificationService extends IntentService {

    private Handler mHandler;
    private Bundle mBundle;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public NotificationService() {
        super("com.google.android.apps.authenticator2");
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Intent intentFinal = intent;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String action = intentFinal.getAction();

                if(action.equals(Notifications.ACTION_ACCEPT)) {
                    mBundle = intentFinal.getExtras();
                    doBindService(getApplicationContext());
                    Toast.makeText(getApplicationContext(), intentFinal.getExtras().getString(Notifications.EXTRA_YES), Toast.LENGTH_SHORT).show();
                } else if(action.equals(Notifications.ACTION_CANCEL)) {
                    Toast.makeText(getApplicationContext(), intentFinal.getExtras().getString(Notifications.EXTRA_NO), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Notifications.NOTIFICATION_ID);
    }

    // Following code is needed to bind to the BluetoothService
    // to call the sendNotificationToDevices
    private boolean mIsBound;
    private BluetoothService mBluetoothService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.LocalBinder)service).getService();
            mBluetoothService.sendData(mBundle.getString(Notifications.EXTRA_USER), (BluetoothDevice) mBundle.getParcelable(Notifications.EXTRA_DEVICE));
            mBundle.clear();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    void doBindService(Context context) {
        context.bindService(new Intent(context, BluetoothService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService(Context context) {
        if (mIsBound) {
            context.unbindService(mConnection);
            mIsBound = false;
        }
    }
}

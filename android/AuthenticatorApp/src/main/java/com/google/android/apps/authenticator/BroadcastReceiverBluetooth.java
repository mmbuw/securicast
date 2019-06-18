package com.google.android.apps.authenticator;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.apps.authenticator2.R;

/**
 * This broadcast receiver reacts to the change of the bluetooth adapter.
 *
 * @author Thomas Dressel
 */

public class BroadcastReceiverBluetooth extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BluetoothService.class);
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(serviceIntent);
        } else switch (intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE)) {
            case BluetoothAdapter.STATE_TURNING_OFF:
                context.stopService(serviceIntent);
                Notifications.showSimpleNotification(context, R.string.notification_no_bluetooth_title, R.string.notification_no_bluetooth_text);
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                context.startService(serviceIntent);
                break;
        }
    }
}

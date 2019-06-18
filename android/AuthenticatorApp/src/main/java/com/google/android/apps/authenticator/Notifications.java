package com.google.android.apps.authenticator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.android.apps.authenticator2.R;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * This class provides methods to show notifications.
 * Also, the notification for the authentication process with the second factor
 * is provided in this class.
 *
 * @author Thomas Dressel
 */

public class Notifications {

    public static final int NOTIFICATION_ID = 1337;
    public static final String ACTION_ACCEPT = "ACTION_ACCEPT";
    public static final String ACTION_CANCEL = "ACTION_CANCEL";
    public static final String EXTRA_YES = "EXTRA_YES";
    public static final String EXTRA_NO = "EXTRA_NO";
    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String EXTRA_PASSPHRASE = "EXTRA_PASSPHRASE";
    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    public static final int NOTIFICATION_REQUEST_CODE = 1317;

    public static void showSimpleNotification(Context context, int title, int text) {
        showSimpleNotification(context, context.getString(title), context.getString(text));
    }

    public static void showSimpleNotification(Context context, String title, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_authenticator)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(title)
                .setContentText(text);

        Intent resultIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static void showUserInteractionNotification(Context context, String user, String passphrase, BluetoothDevice device) { //String title, String text) {

        Intent resultIntentYes = new Intent(context, NotificationService.class);
        resultIntentYes.setAction(ACTION_ACCEPT);
        resultIntentYes.putExtra(EXTRA_YES, "Confirmed");
        resultIntentYes.putExtra(EXTRA_USER, user);
        resultIntentYes.putExtra(EXTRA_PASSPHRASE, passphrase);
        resultIntentYes.putExtra(EXTRA_DEVICE, device);
        PendingIntent pendingIntentYes = PendingIntent.getService(context, NOTIFICATION_REQUEST_CODE, resultIntentYes, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent resultIntentNo = new Intent(context, NotificationService.class);
        resultIntentNo.setAction(ACTION_CANCEL);
        resultIntentNo.putExtra(EXTRA_NO, "Canceled");
        PendingIntent pendingIntentNo = PendingIntent.getService(context, NOTIFICATION_REQUEST_CODE, resultIntentNo, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action yesAction = new NotificationCompat.Action.Builder(R.drawable.ic_accept, context.getString(R.string.notification_authentication_accept), pendingIntentYes).build();
        NotificationCompat.Action noAction = new NotificationCompat.Action.Builder(R.drawable.ic_cancel, context.getString(R.string.notification_authentication_cancel), pendingIntentNo).build();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_authenticator)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("SecuriCast")
                .setContentText(passphrase)
                .addAction(yesAction)
                .addAction(noAction);

        mBuilder.extend(new NotificationCompat.WearableExtender().addAction(yesAction).addAction(noAction));

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }
}

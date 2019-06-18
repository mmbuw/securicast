package com.google.android.apps.authenticator;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Configuration and start of the bluetooth advertisement.
 *
 * @author Thomas Dressel
 */

public class BluetoothService extends Service {

    static final String TAG = "BluetoothService";

    private BluetoothLeAdvertiser mBluetoothAdvertiser;
    private BluetoothGattServer mGattServer;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private HashSet<BluetoothDevice> mBluetoothDevices;
    private AuthenticatorService authenticatorService;
    private BluetoothGattService mBluetoothGattService;

    private ArrayList<BluetoothDevice> connectedDevices;


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "SecuriCast Service Running!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate: Service Started");

        mBluetoothDevices = new HashSet<>();

        authenticatorService = new AuthenticatorService();
        mBluetoothGattService = authenticatorService.getBluetoothGattService();

        advertise(getApplicationContext());
    }

    public void advertise(Context context) {
        connectedDevices = new ArrayList<BluetoothDevice>();

        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Check if Bluetooth is enabled.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            return;
        }

        // Check for advertising support
        if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "Multiple advertisement is not supported", Toast.LENGTH_SHORT).show();
            return;
        }

        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
                .setConnectable(true)
                .build();

        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(authenticatorService.getServiceUUID())
                .build();

        mGattServer = mBluetoothManager.openGattServer(getApplicationContext(), mGattServerCallback);
        mGattServer.addService(mBluetoothGattService);

        mBluetoothAdvertiser.startAdvertising(settings, advertiseData, advertiseCallback);
    }

    public void sendData(String user, BluetoothDevice device) {
        String encryptedData = BluetoothHandler.getEncryptedBluetoothData(user);
        authenticatorService.setAuthenticatorReadCharacteristic(encryptedData);
        sendNotificationToDevice(device, authenticatorService.getAuthenticatorReadCharacteristic());
    }

    public void sendNotificationToDevice(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {
        if (mBluetoothDevices.isEmpty()) {
            Toast.makeText(this, "no device connected", Toast.LENGTH_SHORT).show();
        } else {
            boolean indicate = (characteristic.getProperties()
                    & BluetoothGattCharacteristic.PROPERTY_INDICATE)
                    == BluetoothGattCharacteristic.PROPERTY_INDICATE;
            // true for indication (acknowledge) and false for notification (unacknowledge).
            mGattServer.notifyCharacteristicChanged(device, characteristic, indicate);
        }
    }

    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    mBluetoothDevices.add(device);
    //                updateConnectedDevicesStatus();
                    Log.v(TAG, "Connected to device: " + device.getAddress());
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    mBluetoothDevices.remove(device);
    //                updateConnectedDevicesStatus();
                    Log.v(TAG, "Disconnected from device: " + device.getAddress());
                }
            } else {
                mBluetoothDevices.remove(device);
    //                updateConnectedDevicesStatus();
                Log.e(TAG, "onConnectionStateChange: Error");
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.d(TAG, "Device tried to read characteristic: " + characteristic.getUuid());
            if (offset != 0) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset, null);
                return;
            }
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
            authenticatorService.setAuthenticatorReadCharacteristic("afterRead");
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            String jsonObject = new String(value);
            String user = null;
            String passphrase = null;
            try {
                JSONObject obj = new JSONObject(jsonObject);
                user = obj.getString("otpUser");
                passphrase = obj.getString("passphrase");
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Boolean activatedNotification = sharedPref.getBoolean("wbt_notification", false);
                if (activatedNotification) {
                    Notifications.showUserInteractionNotification(getApplicationContext(), user, passphrase, device);
                } else {
                    sendData(user, device);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            int status = 0; //mCurrentServiceFragment.writeCharacteristic(characteristic, offset, value);
            if (responseNeeded) {
                mGattServer.sendResponse(device, requestId, status, 0, null);
            }
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.d(TAG, "Notification sent. Status: " + status);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
            BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded,
                    offset, value);
            if(responseNeeded) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,
            /* No need to respond with offset */ 0,
            /* No need to respond with a value */ null);
            }
        }
    };

    /*
    * Callback handles events from the framework describing
    * if we were successful in starting the advertisement requests.
    */
    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.i(TAG, "Peripheral Advertise Started.");
            Log.d(TAG, "onStartSuccess: " + mGattServer.toString());
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.i(TAG, "Peripheral Advertise Failed: " + errorCode);
        }
    };

}

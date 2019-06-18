package com.google.android.apps.authenticator;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.ParcelUuid;

import java.util.UUID;

/**
 * Setup of the provided bluetooth service.
 *
 * @author Thomas Dressel
 */

public class AuthenticatorService {
    private static final UUID AUTHENTICATOR_READ_CHARACTERISTIC_UUID =
            UUID.fromString("00002001-0000-1000-8000-00805f9b34fb");
    private static final UUID AUTHENTICATOR_SERVICE_UUID =
            UUID.fromString("00001337-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothGattService mAuthenticatorService;
    private BluetoothGattCharacteristic mAuthenticatorCharacteristic;

    public AuthenticatorService() {
        mAuthenticatorCharacteristic = new BluetoothGattCharacteristic(AUTHENTICATOR_READ_CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        mAuthenticatorCharacteristic.addDescriptor(new BluetoothGattDescriptor(CLIENT_CHARACTERISTIC_CONFIGURATION_UUID,
                (BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE)));

        mAuthenticatorService = new BluetoothGattService(AUTHENTICATOR_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        mAuthenticatorService.addCharacteristic(mAuthenticatorCharacteristic);
    }

    public BluetoothGattService getBluetoothGattService() {
        return mAuthenticatorService;
    }

    public BluetoothGattCharacteristic getAuthenticatorReadCharacteristic() {
        return mAuthenticatorCharacteristic;
    }

    public void setAuthenticatorReadCharacteristic(String value) {
        mAuthenticatorCharacteristic.setValue(value);
    }

    public ParcelUuid getServiceUUID() {
        return new ParcelUuid(AUTHENTICATOR_SERVICE_UUID);
    }

}


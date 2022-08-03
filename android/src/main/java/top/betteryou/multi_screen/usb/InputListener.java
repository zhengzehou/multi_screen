package top.betteryou.multi_screen.usb;

import static android.content.ContentValues.TAG;

import android.hardware.input.InputManager;

import io.flutter.Log;

//输入型设备，包括扫码枪、键盘、鼠标等。使用普通的USB插拔广播，无法监听到此类设备的插拔。
public class InputListener implements InputManager.InputDeviceListener {
    @Override public void onInputDeviceAdded(int id) {
        // Called whenever an input device has been added to the system.
        Log.d(TAG, "onInputDeviceAdded");
    }

    @Override public void onInputDeviceRemoved(int id) {
        // Called whenever an input device has been removed from the system.
        Log.d(TAG, "onInputDeviceRemoved");
    }

    @Override public void onInputDeviceChanged(int id) {
        // Called whenever the properties of an input device have changed since they were last queried.
        Log.d(TAG, "onInputDeviceChanged");
    }
}


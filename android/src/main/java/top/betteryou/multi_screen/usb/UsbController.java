package top.betteryou.multi_screen.usb;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

public interface UsbController
{

    /**
     * USB设备接入时的接口
     * @param usbMonitor USB监听广播对象
     * @param usbManager USB状态、管理对象
     * @param usbDevice USB设备对象
     */
    void onDeviceInsert(UsbMonitor usbMonitor, UsbManager usbManager, UsbDevice usbDevice);

    //USB设备拔出时的接口
    void onDevicePullOut(UsbMonitor usbMonitor, UsbManager usbManager, UsbDevice usbDevice);

    //连接USB设备（打开USB设备）的接口
    void onDeviceOpen(UsbMonitor usbMonitor, UsbManager usbManager, UsbDevice usbDevice);

}



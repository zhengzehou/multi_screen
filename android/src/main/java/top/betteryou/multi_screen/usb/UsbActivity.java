package top.betteryou.multi_screen.usb;


import android.content.Context;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;


public class UsbActivity {
    private Context mContext;
    public UsbActivity(Context context){
        this.mContext = context;
        initData();
    }
    private Timer timer; //计时器对象
    private TimerTask timerTask; //计时器任务对象

    private UsbCDC usbCDC; //当前连接的USB设备对象
    private MyHandler myHandler; //消息处理中心对象
    private UsbMonitor usbMonitor; //USB监听广播对象

    public void onDeviceInsert(UsbMonitor usbMonitor, UsbManager usbManager, UsbDevice usbDevice) {
        usbMonitor.requestOpenDevice(usbDevice); //请求USB连接权限
    }

    public void onDevicePullOut(UsbMonitor usbMonitor, UsbManager usbManager, UsbDevice usbDevice) {
        closeAll(); //执行关闭所有连接的方法
//        myHandler.sendEmptyMessage(MyHandler.USB_CONNECT_FAILED); //向消息中心发送 断开连接 信息
    }

    public void onDeviceOpen(UsbMonitor usbMonitor, UsbManager usbManager, UsbDevice usbDevice) {
        usbCDC = new UsbCDC(); //创建USB连接的对象
        UsbDeviceConnection connection = usbManager.openDevice(usbDevice); //获取此USB链路
        usbCDC.openCDC(usbDevice, connection); //连接USB设备（打开USB设备）
    }

    public String sendMsg(String msg,String deviceId) {
        usbCDC.send(msg+deviceId+"\n");
        return "success";
    }

    protected void onDestroy() {
        closeAll();//执行关闭所有连接的方法
    }

    //关闭所有连接
    public void closeAll() {
        if (usbCDC != null) {
            usbCDC.close();
            usbCDC = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if(usbMonitor != null){
            usbMonitor.unregister();
        }
    }


    //加载数据
    private void initData() {
        myHandler = new MyHandler(); //实例化消息处理中心
//        myHandler = new MyHandler(mContext,UsbActivity.this); //实例化消息处理中心
        usbMonitor = new UsbMonitor(this,mContext); //实例化USB广播监听
        usbMonitor.register(); //注册USB广播监听，注册之后，才可以正常监听USB设备

//        InputManager manager = (InputManager) mContext.getSystemService(Context.INPUT_SERVICE);
//        manager.registerInputDeviceListener(new InputListener(), null);

    }

}



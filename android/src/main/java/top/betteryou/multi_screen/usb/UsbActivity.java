package top.betteryou.multi_screen.usb;


import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import top.betteryou.multi_screen.printer.PrintOrderDataMaker;
import top.betteryou.multi_screen.printer.PrintUtil2;
import top.betteryou.multi_screen.printer.PrinterWriter58mm;
import top.betteryou.multi_screen.usbprinter.UsbPrinterTest;
import top.betteryou.multi_screen.utils.BitmapUtils;


public class UsbActivity {
    private Context mContext;
    public UsbActivity(Context context){
        this.mContext = context;
        initData();
    }
    private Timer timer; //计时器对象
    private TimerTask timerTask; //计时器任务对象

//    private UsbCDC usbCDC; //当前连接的USB设备对象
    private Map<String,UsbCDC> usbCDCMap = new HashMap<>();
    private MyHandler myHandler; //消息处理中心对象
    private UsbMonitor usbMonitor; //USB监听广播对象

    public void onDeviceInsert(UsbMonitor usbMonitor, UsbManager usbManager, UsbDevice usbDevice) {
        usbMonitor.requestOpenDevice(usbDevice); //请求USB连接权限
    }

    public void onDevicePullOut(UsbMonitor usbMonitor, UsbManager usbManager, UsbDevice usbDevice) {
        closeAll(usbDevice.getDeviceName()); //执行关闭所有连接的方法
//        myHandler.sendEmptyMessage(MyHandler.USB_CONNECT_FAILED); //向消息中心发送 断开连接 信息
    }

    public void onDeviceOpen(UsbMonitor usbMonitor, UsbManager usbManager, UsbDevice usbDevice) {
        UsbCDC usbCDC = new UsbCDC(myHandler); //创建USB连接的对象
        UsbDeviceConnection connection = usbManager.openDevice(usbDevice); //获取此USB链路
        usbCDC.openCDC(usbDevice, connection); //连接USB设备（打开USB设备）
        usbCDCMap.put(usbDevice.getDeviceName(),usbCDC);
    }

    public String sendMsg(String msg,String deviceName) {
        UsbCDC usbCDC = usbCDCMap.get(deviceName);
        if(usbCDC == null){
            usbCDC = new UsbCDC(myHandler); //创建USB连接的对象
            UsbManager usbManager = (UsbManager) this.mContext.getSystemService(Context.USB_SERVICE); //获取USB设备管理
            Map<String,UsbDevice> deviceHashMap = usbManager.getDeviceList();
            if(deviceHashMap.isEmpty()){

            }else{
                UsbDevice usbDevice = null;
                for(Map.Entry<String,UsbDevice> entry : deviceHashMap.entrySet()){
                    if(entry.getValue().getDeviceName().equals(deviceName)){
                        usbDevice = entry.getValue();
                        UsbDeviceConnection connection = usbManager.openDevice(usbDevice); //获取此USB链路
                        usbCDC.openCDC(usbDevice, connection); //连接USB设备（打开USB设备）
                        usbCDCMap.put(usbDevice.getDeviceName(),usbCDC);
                        break;
                    }
                }
            }
            usbCDC = usbCDCMap.get(deviceName);
        }
        if(usbCDC != null){
            new UsbPrinterTest(mContext,usbCDC).USBPrinter();
//            List<byte[]> bytesList = new PrintOrderDataMaker(mContext,"",200,200).getPrintData(PrinterWriter58mm.TYPE_58);
//            for(byte[] bytes : bytesList)
//                usbCDC.sendBytes(bytes);
//            printText(usbCDC,1);
        }
        return "success";
    }

//    public static void printText(UsbCDC usbCDC, int number) {
//        try {
//            PrintUtil2 pUtil = new PrintUtil2();
//
//            usbCDC.sendBytes(pUtil.printStartNumber(number));
//            usbCDC.sendBytes(pUtil.setConcentration(25));
//            // 店铺名 居中 放大
//            usbCDC.sendBytes(pUtil.setFontSize(1));
//            usbCDC.sendBytes(pUtil.setTextBold(true)); // 是否加粗
//            usbCDC.sendBytes(pUtil.printAlignment(1)); // 对齐方式
//            usbCDC.sendBytes(pUtil.printText("The credentials of cashier"));
//            usbCDC.sendBytes(pUtil.setTextBold(false)); // 关闭加粗
//            usbCDC.sendBytes(pUtil.setFontSize(0)); // 字体大小
//            usbCDC.sendBytes(pUtil.printLine()); // 线
//            usbCDC.sendBytes(pUtil.printAlignment(0));
//            usbCDC.sendBytes(pUtil.printLine());
//
////
//            usbCDC.sendBytes(pUtil.printLine());
////
//            usbCDC.sendBytes(pUtil.printTwoColumn("Time: ", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
//            usbCDC.sendBytes(pUtil.printLine());
////
//            usbCDC.sendBytes(pUtil.printTwoColumn("order number:", System.currentTimeMillis() + ""));
//            usbCDC.sendBytes(pUtil.printLine());
//
//            usbCDC.sendBytes(pUtil.printTwoColumn("Payer:", "VitaminChen"));
//            usbCDC.sendBytes(pUtil.printLine());
//
//            // 分隔线
//            usbCDC.sendBytes(pUtil.printDashLine());
//            usbCDC.sendBytes(pUtil.printLine());
//
//            //打印商品列表
//            usbCDC.sendBytes(pUtil.printText("commodity"));
//            usbCDC.sendBytes(pUtil.printTabSpace(2));
//            usbCDC.sendBytes(pUtil.printText("Quantity"));
//            usbCDC.sendBytes(pUtil.printTabSpace(1));
//            usbCDC.sendBytes(pUtil.printText("unit price"));
//            usbCDC.sendBytes(pUtil.printLine());
//
//            usbCDC.sendBytes(pUtil.printThreeColumn("iphone6", "1", "4999.00"));
////            usbCDC.sendBytes(pUtil.printThreeColumn("iphone6", "1", "4999.00"));
//
//            usbCDC.sendBytes(pUtil.printDashLine());
//            usbCDC.sendBytes(pUtil.printLine());
//
//            usbCDC.sendBytes(pUtil.printTwoColumn("order amount:", "9998.00"));
//            usbCDC.sendBytes(pUtil.printLine());
//
//            usbCDC.sendBytes(pUtil.printTwoColumn("Amount received:", "10000.00"));
//            usbCDC.sendBytes(pUtil.printLine());
//
//            usbCDC.sendBytes(pUtil.printTwoColumn("Change:", "2.00"));
//            usbCDC.sendBytes(pUtil.printLine());
//
//            usbCDC.sendBytes(pUtil.printDashLine());
//            usbCDC.sendBytes(pUtil.printLine());
//
//            usbCDC.sendBytes(pUtil.printAlignment(1));
////            usbCDC.sendBytes(pUtil.printBarcode("123456", 80, 2));
////            usbCDC.sendBytes(pUtil.printLine());
////            usbCDC.sendBytes(pUtil.printQR("1234456", 200, 200));
//            usbCDC.sendBytes(pUtil.printLine(2));
//            usbCDC.sendBytes("========================".getBytes());
//            usbCDC.sendBytes(pUtil.printEndNumber());
//
//
//        } catch (IOException e) {
//
//        }
//    }

    protected void onDestroy() {
        closeAll(null);//执行关闭所有连接的方法
    }

    //关闭所有连接
    public void closeAll(String deviceName) {
        Iterator<String> it = usbCDCMap.keySet().iterator();
        while (it.hasNext()){
            String k = it.next();
            UsbCDC usbCDC = usbCDCMap.get(k);
            if(deviceName != null){
                if(k.equals(deviceName)){
                    usbCDC.close();
                    usbCDCMap.remove(k);
                }
            }else {
                usbCDC.close();
                usbCDCMap.remove(k);
            }
        }
        if(deviceName == null || usbCDCMap.isEmpty()) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (usbMonitor != null) {
                usbMonitor.unregister();
            }
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



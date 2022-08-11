package top.betteryou.multi_screen;


import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.view.Display;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.sunmi.externalprinterlibrary.api.ConnectCallback;
import com.sunmi.externalprinterlibrary.api.SunmiPrinter;
import com.sunmi.externalprinterlibrary.api.SunmiPrinterApi;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import top.betteryou.multi_screen.screen.DifferentDisplay;
import top.betteryou.multi_screen.screen.ScreenManager;
import top.betteryou.multi_screen.usb.UsbActivity;
import top.betteryou.multi_screen.utils.EventHandler;

/**
 * MultiScreenPlugin
 */
public class MultiScreenPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel channel;
    private Context mContext;
    private ScreenManager screenManager;
    private UsbActivity usbActivity;
    private final static String eventChannelKey = "presentationEventChannel";
    private EventHandler mEventHandler;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "multi_screen");
        io.flutter.plugin.common.EventChannel eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), eventChannelKey);
//        mEventHandler = EventHandler.getInstance();
//        eventChannel.setStreamHandler(mEventHandler);
        channel.setMethodCallHandler(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "getPlatformInfo":
                Map<String,String> deviceInfo = new HashMap<>();
                deviceInfo.put("version",Build.VERSION.RELEASE);
                deviceInfo.put("host",Build.HOST);
                deviceInfo.put("manufacturer",Build.MANUFACTURER);
                deviceInfo.put("hardware",Build.HARDWARE);
//                deviceInfo.put("cpu_abi",Build.CPU_ABI);
//                deviceInfo.put("cpu_abi2",Build.CPU_ABI2);
                deviceInfo.put("board",Build.BOARD);
                deviceInfo.put("brand",Build.BRAND);
                deviceInfo.put("device",Build.DEVICE);
                deviceInfo.put("display",Build.DISPLAY);
                deviceInfo.put("fingerprint",Build.FINGERPRINT);
                deviceInfo.put("model",Build.MODEL);
                deviceInfo.put("product",Build.PRODUCT);
                deviceInfo.put("user",Build.USER);
//                deviceInfo.put("serial",Build.SERIAL);
                deviceInfo.put("id",Build.ID);
                result.success(deviceInfo);
                break;
            case "init":
                screenManager = ScreenManager.init(mContext);
                usbActivity = new UsbActivity(mContext);
                result.success(0);
                break;
            case "getDisNum":
                Display[] displays = screenManager.getDisNum();
                Map<String, Object> res = new HashMap<>();
                for (int i = 0; i < displays.length; i++) {
                    Map<String, Object> dis = new HashMap<>();
                    dis.put("disName", displays[i].getName());
                    dis.put("disId", displays[i].getDisplayId());
                    dis.put("disRotation", displays[i].getRotation());
                    dis.put("disState", displays[i].getState());
                    dis.put("disMode", displays[i].getMode().toString());
                    res.put("dis" + i, dis);
                }
                result.success(res);
                Map<String, Object> test = new HashMap<>();
                test.put("method","page1");
                Map<String, String> test2 = new HashMap<>();
                test2.put("first","firstData");
                test.put("value",test2);
                mEventHandler.response(test);
                break;
            case "setContentView":
                try {
                    int index = call.argument("index");
                    final String rout = call.argument("rout");
                    try {
                        screenManager.setContentView(new DifferentDisplay(mContext, screenManager.getDisNum()[index]) {
                            protected View getLayoutView() {
                                return null;
                            }

                            protected String viewRout() {
                                return rout;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.success(screenManager.isShowing());
                }catch (Exception e){
                    Log.e(call.method, e.toString());
                }
                break;
            case "deviceList":
                UsbManager usbManager = (UsbManager) this.mContext.getSystemService(Context.USB_SERVICE); //获取USB设备管理
                HashMap<String,UsbDevice> deviceHashMap = usbManager.getDeviceList();
                result.success(deviceHashMap);
                break;
            case "sunmiPrinter":
                String content = call.argument("content");
                String deviceId = call.argument("deviceId");
                String rst = usbActivity.sendMsg(content,deviceId);
                result.success(rst);
//                result.success(sunmiPrinter(content));
                break;
            case "getMACAddress":
                result.success(getMACAddress());
                break;
            case "getIpAddress":
                result.success(getIpAddress());
                break;
            case "close":
                screenManager.close();
                usbActivity.closeAll();
                break;
            case "subscribeMsg":
                mEventHandler.response(call.arguments);
                break;

        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        mContext = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }

    public String getMACAddress() {
        String macAddress = "000000000000";
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        List<WifiNetworkSuggestion> macAddresses;
            macAddresses = wifiManager.getNetworkSuggestions();
            if (macAddresses != null && macAddresses.size() > 0) {
                macAddress = macAddresses.get(0).getBssid().toString();
            }
        }
        return macAddress;
    }

    public String getIpAddress() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                if(hostIp != null)
                    break;
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
//                Log.e("tiwolf", "getIpAddress: 开机获取ip="+ni.getName() );
//                if (ni.getName().equals(ipType)) {
                    Enumeration<InetAddress> ias = ni.getInetAddresses();
                    while (ias.hasMoreElements()) {
                        ia = ias.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();
                        // 过滤掉127段的ip地址
                        if (!"127.0.0.1".equals(ip)) {
                            hostIp = ia.getHostAddress();
                            break;
                        }
                    }
//                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }

//    public String serialTest(){
//        UsbManager manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
//// 获取设备
//        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//        UsbDevice device = null ;
//        if(deviceList.isEmpty()){
//            return "";
//        }
//        for(String k : deviceList.keySet()){
//            device = deviceList.get(k);
//        }
//        int count = device.getInterfaceCount();
//        for (int i = 0;i < count; i++) {
//            UsbInterface intf = device.getInterface(i);
//            // 之后我们会根据 intf的 getInterfaceClass 判断是哪种类型的Usb设备，
//            // 并且结合 device.getVectorID() 或者厂家ID进行过滤，比如 UsbConstants.USB_CLASS_PRINTER
//            if (intf.getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER) {
//                // 这个device就是你要找的UsbDevice，此时还需要进行权限判断
//                if (usbManager.hasPermission(device) == false) { // 没有权限
//                    PendingIntent permissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
//                    IntentFilter filter = new IntentFilter("com.android.example.USB_PERMISSION");
//                    registerReceiver(mUsbReceiver, filter);
//                    return;
//                }
//            }
//        }
//    }

    public String sunmiPrinter(String text) {
        try {
            SunmiPrinterApi.getInstance().setPrinter(SunmiPrinter.SunmiNTPrinter);
            if(!SunmiPrinterApi.getInstance().isConnected()){
                SunmiPrinterApi.getInstance().connectPrinter(mContext, new ConnectCallback() {
                    public void onFound() {
                        //发现打印机会回调此⽅方法
                    }
                    public void onUnfound() {
                        //如果没找到打印机会回调此⽅方法
                    }
                    public void onConnect() {
                        //连接成功后会回调此⽅方法，则可以打印
                    }
                    public void onDisconnect() {
                        //连接中打印机断开会回调此⽅方法，此时将中断打印
                    }
                });
            }
            SunmiPrinterApi.getInstance().printerInit();
            SunmiPrinterApi.getInstance().printText(text+"\n");
            SunmiPrinterApi.getInstance().printQrCode(text, 4, 0);
            SunmiPrinterApi.getInstance().disconnectPrinter(mContext);
            return "success";
        }catch (Exception e){
            return e.getMessage();
        }
    }

}
package com.ule.multi_screen;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.Display;
import android.view.View;

import com.ule.multi_screen.screen.DifferentDisplay;
import com.ule.multi_screen.screen.ScreenManager;
import com.ule.multi_screen.utils.EventHandler;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * PresentationPlugin
 */
public class MultiPresentationPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private Context mContext;
    private ScreenManager screenManager;
    private static String EventChannel = "presentationEventChannel";
    private io.flutter.plugin.common.EventChannel eventChannel;
    private EventHandler mEventHandler;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "presentation");
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), EventChannel);
        mEventHandler = EventHandler.getInstance();
        eventChannel.setStreamHandler(mEventHandler);
        channel.setMethodCallHandler(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "getPlatformInfo":
                Map<String,String> deviceInfo = new HashMap<>();
                deviceInfo.put("version",Build.VERSION.RELEASE);
                deviceInfo.put("host",Build.HOST);
                deviceInfo.put("manufacturer",Build.MANUFACTURER);
                deviceInfo.put("hardware",Build.HARDWARE);
                deviceInfo.put("cpu_abi",Build.CPU_ABI);
                deviceInfo.put("cpu_abi2",Build.CPU_ABI2);
                deviceInfo.put("board",Build.BOARD);
                deviceInfo.put("brand",Build.BRAND);
                deviceInfo.put("device",Build.DEVICE);
                deviceInfo.put("display",Build.DISPLAY);
                deviceInfo.put("fingerprint",Build.FINGERPRINT);
                deviceInfo.put("model",Build.MODEL);
                deviceInfo.put("product",Build.PRODUCT);
                deviceInfo.put("cpu_abi2",Build.USER);
                deviceInfo.put("serial",Build.SERIAL);
                deviceInfo.put("id",Build.ID);
                result.success(deviceInfo);
                break;
            case "init":
                screenManager = ScreenManager.init(mContext);
                result.success(0);
                break;
            case "getDisNum":
                Display[] displays = screenManager.getDisNum();
                Map<String, Object> res = new HashMap<>();
                for (int i = 0; i < displays.length; i++) {
                    Map<String, Object> dis = new HashMap<>();
                    dis.put("disName", displays[i].getName());
                    dis.put("disId", displays[i].getDisplayId());
                    dis.put("disRataion", displays[i].getRotation());
                    dis.put("disState", displays[i].getState());
                    dis.put("disMode", displays[i].getMode().toString());
                    res.put("dis" + i, dis);
                }
                result.success(res);
                break;
            case "setContentView":
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
                break;
            case "close":
                screenManager.close();
                break;
            case "subscribeMsg":
                mEventHandler.response(call.arguments);
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
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
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }
}
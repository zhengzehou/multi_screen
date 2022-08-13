package top.betteryou.multi_screen.usbprinter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * 作者：CaoLiulang
 * ❤
 * Date：2022/5/30
 * ❤
 * 模块：USB打印类
 */
public class UsbPrinter{

    private static final String TAG = "UsbPrinter";
    public static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";
    @SuppressLint("StaticFieldLeak")
    private static UsbPrinter mInstance;
    private Context mContext;
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbEndpoint ep, printerEp;
    private UsbInterface usbInterface;
    private static final int TIME_OUT = 100000;

    private UsbPrinter() {
    }

    public static UsbPrinter getInstance() {
        if (mInstance == null) {
            synchronized (UsbPrinter.class) {
                if (mInstance == null) {
                    mInstance = new UsbPrinter();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化打印机，需要与destroy对应
     *
     * @param context 上下文
     */
    public void initPrinter(Context context) {
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        // 注册广播监听usb设备
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        mContext.registerReceiver(mUsbDeviceReceiver, filter);
        // 列出所有的USB设备，并且都请求获取USB权限
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            // 得到此设备的一个接口
            usbInterface = device.getInterface(0);
            // 获取接口的类别 7代表连接的是打印机
            if (usbInterface.getInterfaceClass() == 7) {
                // 1137     85      1027
                // 26728     1280      1045+2
                if (!mUsbManager.hasPermission(device)) {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                } else {
                    connectUsbPrinter(device);
                }
            }
        }

    }

    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && mUsbDevice != null) {
                        connectUsbPrinter(mUsbDevice);
                    } else {
                        Toast.makeText(mContext,  "USB设备请求被拒绝", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (mUsbDevice != null) {
                    Toast.makeText(mContext, "有设备拔出", Toast.LENGTH_LONG).show();
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Toast.makeText(mContext, "有设备插入", Toast.LENGTH_LONG).show();
                if (mUsbDevice != null) {
                    if (!mUsbManager.hasPermission(mUsbDevice)) {
                        mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
                    }
                }
            }
        }
    };

    /**
     * 关闭usb的端口及取消注册的广播监听
     */
    public void close() {
        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
        }
        mContext.unregisterReceiver(mUsbDeviceReceiver);
        mContext = null;
        mUsbManager = null;
    }

    /**
     * 连接打印机设备
     *
     * @param mUsbDevice 识别到的打印机设备
     */
    private void connectUsbPrinter(UsbDevice mUsbDevice) {
        if (mUsbDevice != null) {
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                // 获取指定的index 获得此接口的一个节点 返回一个UsbEndpoint
                ep = usbInterface.getEndpoint(i);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        int pid = mUsbDevice.getProductId();
                        int vid = mUsbDevice.getVendorId();
                        mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                        printerEp = ep;
                        if (mUsbDeviceConnection != null) {
                            Toast.makeText(mContext, "设备已连接", Toast.LENGTH_LONG).show();
                            // 在使用UsbInterface进行数据的写入写出之前，要申明对其的专有访问权限，防止通信混乱
                            mUsbDeviceConnection.claimInterface(usbInterface, true);
                        }
                    }
                }
            }
        } else {
            Toast.makeText(mContext, "未发现可用的打印机", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 安卓9.0之前
     * 只要你传送的数据不大于16384 bytes，传送不会出问题，一旦数据大于16384 bytes，也可以传送，
     * 只是大于16384后面的数据就会丢失，获取到的数据永远都是前面的16384 bytes，
     * 所以，android USB Host 模式与HID使用bulkTransfer（endpoint，buffer，length，timeout）通讯时
     * buffer的长度不能超过16384。
     * <p>
     * controlTransfer( int requestType, int request , int value , int index , byte[] buffer , int length , int timeout)
     * 该方法通过0节点向此设备传输数据，传输的方向取决于请求的类别，如果requestType 为 USB_DIR_OUT 则为写数据 ， USB _DIR_IN ,则为读数据
     */
    private void write(byte[] bytes) {
        if (mUsbDeviceConnection != null) {
            // 通过给定的endpoint 来进行大量的数据传输，传输的方向取决于该节点的方向，
            // buffer是要发送或接收的字节数组，length是该字节数组的长度，失败则返回负数
            // 下行端点，字节数组消息，消息长度，响应时间
            int len = mUsbDeviceConnection.bulkTransfer(printerEp, bytes, bytes.length, TIME_OUT);
        } else {
            if (Looper.myLooper()==null) {
                Looper.prepare();
            }
            handler.sendEmptyMessage(0);
            Looper.loop();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(mContext, "未发现可用的打印机", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 初始化
     */
    public void init() {
        write(ESCUtil.initPrinter());
    }

    /**
     * 打印文字
     *
     * @param msg 打印的内容
     */
    public void printText(String msg) {
        byte[] bytes = new byte[0];
        try {
            bytes = msg.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        write(bytes);
    }

    /**
     * 换行打印文字
     *
     * @param msg 打印的内容
     */
    public void printTextNewLine(String msg) {
        byte[] bytes = new byte[0];
        try {
            bytes = msg.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        write(new String("\n").getBytes());
        write(bytes);
    }

    /**
     * 换行
     */
    public void printLine() {
//        write(new byte[]{10});
        write(new String("\n").getBytes());
    }

    /**
     * 打印空行
     *
     * @param size 几行
     */
    public void printLine(int size) {
        for (int i = 0; i < size; i++) {
            printText("\n");
        }
    }

    /**
     * 设置字体大小
     *
     * @param size 0:正常大小 1:两倍高 2:两倍宽 3:两倍大小 4:三倍高 5:三倍宽 6:三倍大 7:四倍高 8:四倍宽 9:四倍大小 10:五倍高 11:五倍宽 12:五倍大小
     */
    public void setTextSize(int size) {
        write(ESCUtil.setTextSize(size));
    }

    /**
     * 字体加粗
     *
     * @param isBold true/false
     */
    public void bold(boolean isBold) {
        if (isBold) {
            write(ESCUtil.boldOn());
        } else {
            write(ESCUtil.boldOff());
        }
    }

    /**
     * 打印一维条形码
     *
     * @param data 条码
     */
    public void printBarCode(String data) {
//        write(ESCUtil.getPrintBarCode(data, 5, 90, 5, 2));
        write(ESCUtil.getPrintBarCode(data, 5, 90, 4, 2));
    }

    /**
     * 打印二维码
     *
     * @param data 打印的内容
     */
    public void printQrCode(String data) {
        write(ESCUtil.getPrintQrCode(data, 250));
    }

    /**
     * 设置对齐方式
     *
     * @param position 0居左 1居中 2居右
     */
    public void setAlign(int position) {
        byte[] bytes = null;
        switch (position) {
            case 0:
                bytes = ESCUtil.alignLeft();
//                bytes = new byte[]{ 0x1b, 0x61, 0x30 };
//                bytes = new byte[]{27, 97, (byte) 0};
                break;
            case 1:
                bytes = ESCUtil.alignCenter();
//                bytes = new byte[]{ 0x1b, 0x61, 0x31 };
//                bytes = new byte[]{27, 97, (byte) 1};
                break;
            case 2:
                bytes = ESCUtil.alignRight();
//                bytes = new byte[]{ 0x1b, 0x61, 0x32 };
//                bytes = new byte[]{27, 97, (byte) 2};
                break;
            default:
                break;
        }
        write(bytes);
    }

    /**
     * 获取字符串的宽度
     *
     * @param str 取字符
     * @return 宽度
     */
    public int getStringWidth(String str) {
        int width = 0;
        for (char c : str.toCharArray()) {
            width += isChinese(c) ? 2 : 1;
        }
        return width;
    }

    /**
     * 判断是否中文
     * GENERAL_PUNCTUATION 判断中文的“号
     * CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
     * HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
     *
     * @param c 字符
     * @return 是否中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 切纸
     */
    public void cutPager() {
        write(ESCUtil.cutter());
    }

}
package top.betteryou.multi_screen.usb;


import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyHandler extends Handler {

    public static final int OUTPUT = 0; //发送消息
    public static final int INPUT = 1; //接收消息

    public static final int USB_CONNECT_SUCCESS = 2; //USB设备连接成功
    public static final int USB_CONNECT_FAILED = 3; //USB设备连接失败或断开连接

    public static boolean USB_CONNECT_STATE = false; //当前USB设备连接状态

    private Context context; //上下文
    private UsbActivity mainActivity;
    MyHandler( Context context,UsbActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        String msgStr = null;
        switch (msg.what) {
            case USB_CONNECT_SUCCESS:  //USB设备连接成功
                MyHandler.USB_CONNECT_STATE  = true; //连接状态改变为true
                Toast.makeText(context,"连接成功",Toast.LENGTH_LONG).show();
                msgStr = "连接成功";
                break;
            case USB_CONNECT_FAILED: //USB设备连接失败
                MyHandler.USB_CONNECT_STATE  = false;
                mainActivity.closeAll(); //连接断开或连接失败，执行关闭所有连接和对象的方法
                Toast.makeText(context,"断开连接",Toast.LENGTH_LONG).show();
                msgStr = "断开连接";
                break;
            case OUTPUT:  //发送消息
                msgStr = "[TX]"+gteNowDate()+": "+msg.obj.toString()+"\n"; //给控件填充意图发送来的信息
                break;
            case INPUT:  //接收消息
                msgStr = "[RX]"+gteNowDate()+": "+msg.obj.toString()+"\n";
                break;
        }

    }

    /**
     * 返回格式化后的当前时间
     * @return 当前时间字符串形式
     */
    private String gteNowDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("HH:mm:ss");// 时：分：秒
        Date date = new Date();// 获取当前时间戳
        return sdf.format(date); //返回格式化后的时间戳
    }

}



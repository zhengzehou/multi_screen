package top.betteryou.multi_screen.usbprinter;

import java.io.UnsupportedEncodingException;

import top.betteryou.multi_screen.usb.UsbCDC;

/**
 * 模块：USB打印字符处理类
 */
public class UsbPrinterBytesUtils {
    private static UsbPrinterBytesUtils usbPrinterBytesUtils;
    UsbCDC usbCDC;
    private UsbPrinterBytesUtils(UsbCDC usbCDC) {
        this.usbCDC = usbCDC;
    }

    public static UsbPrinterBytesUtils getInst(UsbCDC usbCDC){
        if(usbPrinterBytesUtils == null){
            usbPrinterBytesUtils = new UsbPrinterBytesUtils(usbCDC);
        }
        return usbPrinterBytesUtils;
    }


    private void write(byte[] bytes) {
        usbCDC.sendBytes(bytes);
    }

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

    public void printBytes(byte[] msg) {
        write(msg);
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
//        write(ESCUtil.getPrintQrCode2(data, 100));
        write(ESCUtil.getPrintQrCode(data, 250));
//        write(ESCUtil.getPrintQrCode(data, 16,1));
//        write(ESCUtil.getPrintDoubleQrCode(data, data,16,1));
    }
    public void printQrCode2(String data) {
        write(ESCUtil.getPrintQrCode2(data, 250));
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
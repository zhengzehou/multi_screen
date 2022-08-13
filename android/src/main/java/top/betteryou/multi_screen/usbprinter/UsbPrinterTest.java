package top.betteryou.multi_screen.usbprinter;


import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import top.betteryou.multi_screen.printer.GPrinterCommand;
import top.betteryou.multi_screen.usb.UsbCDC;


public class UsbPrinterTest {
    UsbPrinterBytesUtils usbPrinter ;
    Context mContext;
    public UsbPrinterTest(Context mContext,UsbCDC usbCDC){
        this.mContext = mContext;
        this.usbPrinter = UsbPrinterBytesUtils.getInst(usbCDC);
    }



    /**
     * USB串口打印
     */
    public void USBPrinter() {
        usbPrinter.init();
        usbPrinter.printBytes(GPrinterCommand.LINE_SPACING_DEFAULT);
        // 设置加粗
        usbPrinter.bold(true);
        // 文字大小
        usbPrinter.setTextSize(3);
        // 设置文字居中
        usbPrinter.setAlign(1);
        // 打印文字并换行
        usbPrinter.printTextNewLine("结账单");
        // 取消加粗
        usbPrinter.bold(false);
        // 换2行
        usbPrinter.printLine(2);
        usbPrinter.setTextSize(0);

        usbPrinter.printBarCode("20225301314");
        usbPrinter.printLine(2);
//        byte[] end = { 0x1d, 0x4c, 0x1f, 0x00 };
//        usbPrinter.printBytes(end);
//        Bitmap qrImage = BitmapUtils.createQRCodeBitmap("qr123456", 200, "2");
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        qrImage.compress(Bitmap.CompressFormat.PNG,100,baos);
//        ArrayList<byte[]> datas = PrinterUtils.decodeBitmapToDataList(qrImage, 200);
//        for(byte[] bytes : datas){
//            usbPrinter.printBytes(bytes);
//        }
//            String str = sendPhoto(usbPrinter,qrImage);
//        usbPrinter.printText(str);
//        byte[] bytes = BitmapUtils.draw2PxPoint(qrImage);
//        qrImage = null;
//        byte[] bytes1 = new byte[4];
//        bytes1[0] = GS;
//        bytes1[1] = 0x76;
//        bytes1[2] = 0x30;
//        bytes1[3] = 0x00;
//        bytes1 = BytesUtil.byteMerger(bytes1, bytes);
//        usbPrinter.printBytes(bytes1);
//        byte[] bytes =baos.toByteArray();
//        usbPrinter.printBytes(bytes);
//        usbPrinter.printBytes(end);
//        usbPrinter.printTextNewLine("-------------------------------");
//
//        usbPrinter.printTextNewLine("-------------------------------");
//        usbPrinter.printQrCode("20225301314+CF15545655454+ssssss");



//        usbPrinter.printTextNewLine("-------------------------------");

        // 设置文字居左对齐
        usbPrinter.setAlign(0);
        usbPrinter.printTextNewLine("流水号:20225301314");
        usbPrinter.printTextNewLine("单号:CF15545655454");
        usbPrinter.printTextNewLine("结账时间:2022/5/30");
        usbPrinter.printTextNewLine("收银员:***");
//         换2行
        usbPrinter.printLine(2);
        usbPrinter.printTextNewLine("菜品       数量 重量    金额");
        usbPrinter.printTextNewLine("-------------------------------");
//        for (int i = 0; i < addcpbean.size(); i++) {
//            if (addcpbean.get(i).getName().length() == 1) {
//                usbPrinter.printTextNewLine(addcpbean.get(i).getName() + "          " + addcpbean.get(i).getQuantity() + "   -     " + addcpbean.get(i).getSubtotal());
//            } else if (addcpbean.get(i).getName().length() == 2) {
//                usbPrinter.printTextNewLine(addcpbean.get(i).getName() + "        " + addcpbean.get(i).getQuantity() + "   -     " + addcpbean.get(i).getSubtotal());
//            } else if (addcpbean.get(i).getName().length() == 3) {
//                usbPrinter.printTextNewLine(addcpbean.get(i).getName() + "      " + addcpbean.get(i).getQuantity() + "   -     " + addcpbean.get(i).getSubtotal());
//            } else if (addcpbean.get(i).getName().length() == 4) {
//                usbPrinter.printTextNewLine(addcpbean.get(i).getName() + "    " + addcpbean.get(i).getQuantity() + "   -     " + addcpbean.get(i).getSubtotal());
//            } else if (addcpbean.get(i).getName().length() == 5) {
//                usbPrinter.printTextNewLine(addcpbean.get(i).getName() + "  " + addcpbean.get(i).getQuantity() + "   -     " + addcpbean.get(i).getSubtotal());
//            } else {
//                usbPrinter.printTextNewLine(addcpbean.get(i).getName());
//                usbPrinter.printTextNewLine("            " + addcpbean.get(i).getQuantity() + "   -     " + addcpbean.get(i).getSubtotal());
//            }
//        }
        usbPrinter.printTextNewLine("-------------------------------");
        // 设置文字居右对齐
        usbPrinter.setAlign(0);
        usbPrinter.printTextNewLine("订单金额:￥200元");
        usbPrinter.printTextNewLine("优惠金额:￥50元");
        // 支付金额
        usbPrinter.printTextNewLine("支付金额:￥150元");
        usbPrinter.printTextNewLine("-------------------------------");
        usbPrinter.bold(true);
        // 文字大小
        usbPrinter.setTextSize(2);
        usbPrinter.printTextNewLine("备注：不要放辣椒，早点配送");
        // 换2行
        usbPrinter.printLine(2);
        // 文字大小
        usbPrinter.setTextSize(1);
        //居中
        usbPrinter.setAlign(1);
        usbPrinter.printTextNewLine("欢迎下次光临");
        usbPrinter.printLine(6);

//        Bitmap image = BitmapUtils.createQRCodeBitmap("qr123456", 200, "2");
////        usbPrinter.printText(sendPhoto(image));
////        usbPrinter.printLine(2);
//        usbPrinter.printBytes(BitmapUtils.genBitmapCode(image,false,false));
//        usbPrinter.printLine(2);
////        usbPrinter.printBytes(BitmapUtils.draw2PxPoint(image));
////        usbPrinter.printLine(2);
//        usbPrinter.printQrCode2("20225301314+CF15545655454+ssssss");
//        usbPrinter.printLine(2);
//        usbPrinter.printQrCode("20225301314+CF15545655454+ssssss");
//        usbPrinter.printLine(2);
        // 切纸
        usbPrinter.cutPager();
    }

    /**
     * 将图片转换成十六进制字符串
     * @param
     * @return
     */
    public static String sendPhoto(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int widthByte = (width - 1) / 8 + 1;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        List<byte[]> dataList = new ArrayList<>();
        ///图片二值化处理
        for (int y = 0; y < height; y++) {
            byte[] rowData = new byte[widthByte];
            byte temp = 0;
            int offset;
            for (int x = 0; x < width; x++) {
                int pixel = pixels[width * y + x];
                int alpha = pixel >> 24 & 0xFF;
                int red = pixel >> 16 & 0xFF;
                int green = pixel >> 8 & 0xFF;
                int blue = pixel & 0xFF;
                int value = alpha == 0 ? 0 : (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11) > 127 ? 0 : 1;
                offset = x % 8;
                if (value == 1) {
                    temp |= (0x80 >> offset);
                }
                if (offset == 7 || x >= width - 1) {
                    rowData[x / 8] = temp;
                    temp = 0;
                }
            }

            dataList.add(rowData);
        }
        String photoStr="";
        StringBuilder dataGet=new StringBuilder();
        for (int i = 0; i < height; i++) {
            dataGet.append(byte2hex(dataList.get(i)));
        }
        photoStr+=photoStr+dataGet.toString();
        return photoStr;


    }
    /**
     * 转16进制字符串
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b)
    {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }


        }
        return sb.toString().toUpperCase();
    }
}

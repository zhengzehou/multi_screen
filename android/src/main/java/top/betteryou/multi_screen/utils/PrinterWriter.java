package top.betteryou.multi_screen.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import top.betteryou.multi_screen.usbprinter.BytesUtil;
import top.betteryou.multi_screen.usbprinter.ESCUtil;


/**
 * 打印机写入器
 */
public abstract class PrinterWriter {

    public static final int HEIGHT_PARTING_DEFAULT = 255;
    private static final String CHARSET = "gb2312";
    private ByteArrayOutputStream bos;
    private int heightParting;

    public PrinterWriter() throws IOException {
        this(HEIGHT_PARTING_DEFAULT);
    }

    public PrinterWriter(int parting) throws IOException {
        if (parting <= 0 || parting > HEIGHT_PARTING_DEFAULT)
            heightParting = HEIGHT_PARTING_DEFAULT;
        else
            heightParting = parting;
        init();
    }

//    /**
//     * 重置
//     * 使用 init 替代
//     *
//     * @throws IOException 异常
//     */
//    @Deprecated
//    public void reset() throws IOException {
//        init();
//    }

    /**
     * 初始化
     *
     * @throws IOException 异常
     */
    public void init() throws IOException {
        bos = new ByteArrayOutputStream();
        write(PrinterUtils.initPrinter());
    }

//    /**
//     * 获取预打印数据并关闭流
//     *
//     * @return 预打印数据
//     * @throws IOException 异常
//     */
//    @SuppressWarnings("unused")
//    @Deprecated
//    public byte[] getData() throws IOException {
//        return getDataAndClose();
//    }

    /**
     * 获取预打印数据并重置流
     *
     * @return 预打印数据
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public byte[] getDataAndReset() throws IOException {
        byte[] data;
        bos.flush();
        data = bos.toByteArray();
        bos.reset();
        return data;
    }

    /**
     * 获取预打印数据并关闭流
     *
     * @return 预打印数据
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public byte[] getDataAndClose() throws IOException {
        byte[] data;
        bos.flush();
        data = bos.toByteArray();
        bos.close();
        bos = null;
        return data;
    }

    /**
     * 写入数据
     *
     * @param data 数据
     * @throws IOException 异常
     */
    public void write(byte[] data) throws IOException {
        if (bos == null)
            init();
        bos.write(data);
    }

    /**
     * 设置居中
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setAlignCenter() throws IOException {
        write(PrinterUtils.alignCenter());
    }

    /**
     * 设置左对齐
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setAlignLeft() throws IOException {
        write(PrinterUtils.alignLeft());
    }

    /**
     * 设置右对齐
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setAlignRight() throws IOException {
        write(PrinterUtils.alignRight());
    }

    /**
     * 开启着重
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setEmphasizedOn() throws IOException {
        write(PrinterUtils.emphasizedOn());
    }

    /**
     * 关闭着重
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setEmphasizedOff() throws IOException {
        write(PrinterUtils.emphasizedOff());
    }

    /**
     * 设置文字大小
     *
     * @param size 文字大小 （0～7）（默认0）
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setFontSize(int size) throws IOException {
        write(PrinterUtils.fontSizeSetBig(size));
    }

    /**
     * 设置行高度
     *
     * @param height 行高度
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setLineHeight(int height) throws IOException {
        if (height >= 0 && height <= 255)
            write(PrinterUtils.printLineHeight((byte) height));
    }

    /**
     * 写入字符串
     *
     * @param string 字符串
     * @throws IOException 异常
     */
    public void print(String string) throws IOException {
        print(string, CHARSET);
    }

    /**
     * 写入字符串
     *
     * @param string      字符串
     * @param charsetName 编码方式
     * @throws IOException 异常
     */
    public void print(String string, String charsetName) throws IOException {
        if (string == null)
            return;
        write(string.getBytes(charsetName));
    }

    /**
     * 写入一条横线
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void printLine() throws IOException {
        int length = getLineWidth();
        String line = "";
        while (length > 0) {
            line += "- ";
            length--;
        }
        print(line);
    }

    /**
     * 获取横线线宽
     *
     * @return 横线线宽
     */
    protected abstract int getLineWidth();

    /**
     * 一行输出
     *
     * @param str1     字符串
     * @param str2     字符串
     * @param textSize 文字大小
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void printInOneLine(String str1, String str2, int textSize) throws IOException {
        printInOneLine(str1, str2, textSize, CHARSET);
    }

    public void printInOneLine(String str1, String str2, String str3, int textSize) throws IOException {
        printInOneLine(str1, str2, str3, CHARSET);
    }

    /**
     * 一行输出
     *
     * @param str1        字符串
     * @param str2        字符串
     * @param textSize    文字大小
     * @param charsetName 编码方式
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void printInOneLine(String str1, String str2, int textSize, String charsetName) throws IOException {
        int lineLength = getLineStringWidth(textSize);
        int needEmpty = lineLength - (getStringWidth(str1) + getStringWidth(str2)) % lineLength;
        String empty = "";
        while (needEmpty > 0) {
            empty += " ";
            needEmpty--;
        }
        print(str1 + empty + str2, charsetName);
    }

    /**
     * 一行输出
     *
     * @param str1        字符串
     * @param str2        字符串
     * @param charsetName 编码方式
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void printInOneLine(String str1, String str2, String str3, String charsetName) throws IOException {
        int lineLength = getLineStringWidth(0);
        int needEmpty = (lineLength - (getStringWidth(str1) + getStringWidth(str2) + getStringWidth(str3)) % lineLength) / 2;
        String empty = "";
        while (needEmpty > 0) {
            empty += " ";
            needEmpty--;
        }
        print(str1 + empty + str2 + empty + str3, charsetName);
    }


    /**
     * 获取一行字符串长度
     *
     * @param textSize 文字大小
     * @return 一行字符串长度
     */
    protected abstract int getLineStringWidth(int textSize);

    private int getStringWidth(String str) {
        int width = 0;
        for (char c : str.toCharArray()) {
            width += isChinese(c) ? 2 : 1;
        }
        return width;
    }

//    /**
//     * 打印 Drawable 图片
//     *
//     * @param res Resources
//     * @param id  资源ID
//     * @throws IOException 异常
//     */
//    @SuppressWarnings("unused")
//    @Deprecated
//    public void printDrawable(Resources res, int id) throws IOException {
//        int maxWidth = getDrawableMaxWidth();
//        Bitmap image = scalingBitmap(res, id, maxWidth);
//        if (image == null)
//            return;
//        byte[] command = PrinterUtils.decodeBitmap(image, heightParting);
//        image.recycle();
//        try {
//            if (command != null) {
//                write(command);
//            }
//        } catch (IOException e) {
//            throw new IOException(e.getMessage());
//        }
//    }

    /**
     * 获取图片数据流
     *
     * @param res Resources
     * @param id  资源ID
     * @return 数据流
     */
    public ArrayList<byte[]> getImageByte(Resources res, int id) {
        int maxWidth = getDrawableMaxWidth();
        Bitmap image = scalingBitmap(res, id, maxWidth);
        if (image == null)
            return null;
        ArrayList<byte[]> data = PrinterUtils.decodeBitmapToDataList(image, heightParting);
        image.recycle();
        return data;
    }

    /**
     * 获取图片最大宽度
     *
     * @return 图片最大宽度
     */
    protected abstract int getDrawableMaxWidth();

    /**
     * 缩放图片
     *
     * @param res      资源
     * @param id       ID
     * @param maxWidth 最大宽
     * @return 缩放后的图片
     */
    private Bitmap scalingBitmap(Resources res, int id, int maxWidth) {
        if (res == null)
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置只量取宽高
        BitmapFactory.decodeResource(res, id, options);// 量取宽高
        options.inJustDecodeBounds = false;
        // 粗略缩放
        if (maxWidth > 0 && options.outWidth > maxWidth) {
            // 超过限定宽
            double ratio = options.outWidth / (double) maxWidth;// 计算缩放比
            int sampleSize = (int) Math.floor(ratio);// 向下取整，保证缩放后不会低于最大宽高
            if (sampleSize > 1) {
                options.inSampleSize = sampleSize;// 设置缩放比，原图的几分之一
            }
        }
        try {
            Bitmap image = BitmapFactory.decodeResource(res, id, options);
            final int width = image.getWidth();
            final int height = image.getHeight();
            // 精确缩放
            if (maxWidth <= 0 || width <= maxWidth) {
                return image;
            }
            final float scale = maxWidth / (float) width;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizeImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
            image.recycle();
            return resizeImage;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

//    /**
//     * 打印 Drawable 图片
//     *
//     * @param drawable 图片
//     * @throws IOException 异常
//     */
//    @SuppressWarnings("unused")
//    @Deprecated
//    public void printDrawable(Drawable drawable) throws IOException {
//        int maxWidth = getDrawableMaxWidth();
//        Bitmap image = scalingDrawable(drawable, maxWidth);
//        if (image == null)
//            return;
//        byte[] command = PrinterUtils.decodeBitmap(image, heightParting);
//        image.recycle();
//        try {
//            if (command != null) {
//                write(command);
//            }
//        } catch (IOException e) {
//            throw new IOException(e.getMessage());
//        }
//    }

    /**
     * 获取图片数据流
     *
     * @param drawable 图片
     * @return 数据流
     */
    public ArrayList<byte[]> getImageByte(Drawable drawable) {
        int maxWidth = getDrawableMaxWidth();
        Bitmap image = scalingDrawable(drawable, maxWidth);
        if (image == null)
            return null;
        ArrayList<byte[]> data = PrinterUtils.decodeBitmapToDataList(image, heightParting);
        image.recycle();
        return data;
    }

    /**
     * 缩放图片
     *
     * @param drawable 图片
     * @param maxWidth 最大宽
     * @return 缩放后的图片
     */
    private Bitmap scalingDrawable(Drawable drawable, int maxWidth) {
        if (drawable == null || drawable.getIntrinsicWidth() == 0
                || drawable.getIntrinsicHeight() == 0)
            return null;
        final int width = drawable.getIntrinsicWidth();
        final int height = drawable.getIntrinsicHeight();
        try {
            Bitmap image = null;//Bitmap.createBitmap(width, height,
//                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                            : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(image);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            // 精确缩放
            if (maxWidth <= 0 || width <= maxWidth) {
                return image;
            }
            final float scale = maxWidth / (float) width;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizeImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
            image.recycle();
            return resizeImage;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

//    /**
//     * 打印 Bitmap 图片
//     *
//     * @param image 图片
//     * @throws IOException 异常
//     */
//    @SuppressWarnings("unused")
//    @Deprecated
//    public void printBitmap(Bitmap image) throws IOException {
//        int maxWidth = getDrawableMaxWidth();
//        Bitmap scalingImage = scalingBitmap(image, maxWidth);
//        if (scalingImage == null)
//            return;
//        byte[] command = PrinterUtils.decodeBitmap(scalingImage, heightParting);
//        scalingImage.recycle();
//        try {
//            if (command != null) {
//                write(command);
//            }
//        } catch (IOException e) {
//            throw new IOException(e.getMessage());
//        }
//    }

    /**
     * 获取图片数据流
     *
     * @param image 图片
     * @return 数据流
     */
    public ArrayList<byte[]> getImageByte(Bitmap image) {
        int maxWidth = getDrawableMaxWidth();
        Bitmap scalingImage = scalingBitmap(image, maxWidth);
        if (scalingImage == null)
            return null;
        ArrayList<byte[]> data = PrinterUtils.decodeBitmapToDataList(image, heightParting);
        image.recycle();
        return data;
    }

    /**
     * 缩放图片
     *
     * @param image    图片
     * @param maxWidth 最大宽
     * @return 缩放后的图片
     */
    private Bitmap scalingBitmap(Bitmap image, int maxWidth) {
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0)
            return null;
        try {
            final int width = image.getWidth();
            final int height = image.getHeight();
            // 精确缩放
            float scale = 1;
            if (maxWidth <= 0 || width <= maxWidth) {
                scale = maxWidth / (float) width;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

//    /**
//     * 打印图片文件
//     *
//     * @param filePath 图片
//     * @throws IOException 异常
//     */
//    @SuppressWarnings("unused")
//    @Deprecated
//    public void printImageFile(String filePath) throws IOException {
//        Bitmap image;
//        try {
//            int width;
//            int height;
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(filePath, options);
//            width = options.outWidth;
//            height = options.outHeight;
//            if (width <= 0 || height <= 0)
//                return;
//            options.inJustDecodeBounds = false;
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            image = BitmapFactory.decodeFile(filePath, options);
//        } catch (OutOfMemoryError | Exception e) {
//            return;
//        }
//        printBitmap(image);
//    }

    /**
     * 获取图片数据流
     *
     * @param filePath 图片路径
     * @return 数据流
     */
    public ArrayList<byte[]> getImageByte(String filePath) {
        Bitmap image;
        try {
            int width;
            int height;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            width = options.outWidth;
            height = options.outHeight;
            if (width <= 0 || height <= 0)
                return null;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            image = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError | Exception e) {
            return null;
        }
        return getImageByte(image);
    }

    public void printBarcode() throws IOException {
        write(PrinterUtils.print_bar_code(PrinterUtils.BarCode.CODE128,"123445565565"));
        write(ESCUtil.getPrintBarCode("123445565565",5, 90, 4, 2));
    }

    public void printQrCode() throws IOException {
        write(ESCUtil.getPrintQrCode("123445565565",250));
    }

    /**
     * printer barcode
     *
     * @param text
     * @param Height
     * @param Width  1-4
     * @throws IOException
     */
    public byte[] printBarcode(String text, int Height, int Width) throws IOException {
        int dataLen=text.getBytes ("GB2312").length;
        byte [] b1 = new byte[]{0x1D};
        b1 = bytesAppend(b1,"h".getBytes());
        b1 = bytesAppend(b1,(Height+"").getBytes());
        b1 = bytesAppend(b1,new byte[]{0x1D});
        b1 = bytesAppend(b1,"w".getBytes());
        b1 = bytesAppend(b1,(Width+"").getBytes());
        b1 = bytesAppend(b1,new byte[]{0x1D});
        b1 = bytesAppend(b1,"k".getBytes());
        b1 = bytesAppend(b1,new byte[]{PrinterUtils.BarCode.CODE128});
        b1 = bytesAppend(b1,(dataLen+"").getBytes());
        b1 = bytesAppend(b1,(text).getBytes());
        return b1;
    }
    /**
     * 打印单个二维码 sunmi自定义指令
     *
     * @param code: 二维码数据
     * @param :     二维码块大小(单位:点, 取值 1 至 16 )
     * @param :     二维码纠错等级(0 至 3)
     *              0 -- 纠错级别L ( 7%)
     *              1 -- 纠错级别M (15%)
     *              2 -- 纠错级别Q (25%)
     *              3 -- 纠错级别H (30%)
     */
    public static byte[] getPrintQrCode(String code, int moduleSize, int errorLevel) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(setQrCodeSize(moduleSize));
            buffer.write(setQrCodeErrorLevel(errorLevel));
            buffer.write(getQrCodeBytes(code));
            buffer.write(getBytesForPrintQrCode(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }
    /**
     * 光栅打印二维码
     */
    public static byte[] getPrintQrCode(String data, int size) {
        byte[] bytes1 = new byte[4];
        bytes1[0] = GS;
        bytes1[1] = 0x76;
        bytes1[2] = 0x30;
        bytes1[3] = 0x00;
        byte[] bytes2 = BytesUtil.getZxingQrCode(data, size);
        return bytes2 == null ? new byte[]{} : BytesUtil.byteMerger(bytes1, bytes2);
    }
    /**
     * 打印已存入数据的二维码
     */
    private static byte[] getBytesForPrintQrCode(boolean single) {
        byte[] dtmp;
        // 同一行只打印一个QRCode， 后面加换行
        if (single) {
            dtmp = new byte[9];
            dtmp[8] = 0x0A;
        } else {
            dtmp = new byte[8];
        }
        dtmp[0] = 0x1D;
        dtmp[1] = 0x28;
        dtmp[2] = 0x6B;
        dtmp[3] = 0x03;
        dtmp[4] = 0x00;
        dtmp[5] = 0x31;
        dtmp[6] = 0x51;
        dtmp[7] = 0x30;
        return dtmp;
    }

    /**
     * 二维码存入指令
     */
    private static byte[] getQrCodeBytes(String code) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            byte[] d = code.getBytes("GB18030");
            int len = d.length + 3;
            if (len > 7092) {
                len = 7092;
            }
            buffer.write((byte) 0x1D);
            buffer.write((byte) 0x28);
            buffer.write((byte) 0x6B);
            buffer.write((byte) len);
            buffer.write((byte) (len >> 8));
            buffer.write((byte) 0x31);
            buffer.write((byte) 0x50);
            buffer.write((byte) 0x30);
            for (int i = 0; i < d.length && i < len; i++) {
                buffer.write(d[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }
    /**
     * Group separator 组分离器
     */
    public static final byte GS = 0x1D;
    /**
     * 二维码块大小设置指令
     */
    private static byte[] setQrCodeSize(int moduleSize) {
        byte[] dtmp = new byte[8];
        dtmp[0] = GS;
        dtmp[1] = 0x28;
        dtmp[2] = 0x6B;
        dtmp[3] = 0x03;
        dtmp[4] = 0x00;
        dtmp[5] = 0x31;
        dtmp[6] = 0x43;
        dtmp[7] = (byte) moduleSize;
        return dtmp;
    }
    /**
     * 二维码纠错等级设置指令
     */
    private static byte[] setQrCodeErrorLevel(int errorLevel) {
        byte[] dtmp = new byte[8];
        dtmp[0] = GS;
        dtmp[1] = 0x28;
        dtmp[2] = 0x6B;
        dtmp[3] = 0x03;
        dtmp[4] = 0x00;
        dtmp[5] = 0x31;
        dtmp[6] = 0x45;
        dtmp[7] = (byte) (48 + errorLevel);
        return dtmp;
    }
    /**
     * 打印和包装（水平方向）
     */
    public static final byte LF = 0x0A;
    /**
     * 打印一维条形码
     */
    public static byte[] getPrintBarCode(String data, int logy, int height, int width, int textPosition) {
        if (logy < 0 || logy > 10) {
            return new byte[]{LF};
        }

        if (width < 2 || width > 6) {
            width = 2;
        }

        if (textPosition < 0 || textPosition > 3) {
            textPosition = 0;
        }

        if (height < 1 || height > 255) {
            height = 162;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            buffer.write(new byte[]{0x1D, 0x66, 0x01, 0x1D, 0x48, (byte) textPosition,
                    0x1D, 0x77, (byte) width, 0x1D, 0x68, (byte) height, 0x0A});

            byte[] barcode;
            if (logy == 10) {
                barcode = BytesUtil.getBytesFromDecString(data);
            } else {
                barcode = data.getBytes("GB18030");
            }

            if (logy > 7) {
                buffer.write(new byte[]{0x1D, 0x6B, 0x49, (byte) (barcode.length + 2), 0x7B, (byte) (0x41 + logy - 8)});
            } else {
                buffer.write(new byte[]{0x1D, 0x6B, (byte) (logy + 0x41), (byte) barcode.length});
            }
            buffer.write(barcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }

    private byte[] bytesAppend(byte[] b1,byte[] b2){
        byte []b = new byte[b1.length+b2.length];
        System.arraycopy(b1,0,b,0,b1.length);
        System.arraycopy(b2,0,b,b1.length,b2.length);
        return  b;
    }

    /**
     * 输出并换行
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void printLineFeed() throws IOException {
        write(PrinterUtils.printLineFeed());
    }

    /**
     * 进纸切割
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void feedPaperCut() throws IOException {
        write(PrinterUtils.feedPaperCut());
    }

    /**
     * 进纸切割（留部分）
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void feedPaperCutPartial() throws IOException {
        write(PrinterUtils.feedPaperCutPartial());
    }

    /**
     * 设置图片打印高度分割值
     * 最大允许255像素
     *
     * @param parting 高度分割值
     */
    @SuppressWarnings("unused")
    public void setHeightParting(int parting) {
        if (parting <= 0 || parting > HEIGHT_PARTING_DEFAULT)
            return;
        heightParting = parting;
    }

    /**
     * 获取图片打印高度分割值
     *
     * @return 高度分割值
     */
    @SuppressWarnings("unused")
    public int getHeightParting() {
        return heightParting;
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
}
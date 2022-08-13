package top.betteryou.multi_screen.utils;

import static android.graphics.Bitmap.createBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Random;

import io.flutter.Log;

/**
 * Bitmap相关工具类
 * Created by tsy on 16/7/26.
 */
public class BitmapUtils {

    private static final String TAG = "BitmapUtils";

    public static String convertBitmapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    /**
     * 根据文字创建一个 bitmap  ， 打印的位图需要 黑字 白底
     *
     * @param text
     * @param height
     * @param width
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public Bitmap getNewBitMap(String text, int height, int width) {
        Bitmap newBitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(newBitmap, 0, 0, null);
        TextPaint textPaint = new TextPaint();
        //       String familyName ="宋体";
        //  Typeface font = Typeface.create(familyName,Typeface.BOLD);
        // textPaint.setTypeface(font);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(height * 2 / 3);
//        textPaint.setColor(Color.rgb(0, 0, 0));
        textPaint.setColor(Color.BLACK);
        //     "在Android开发中，Canvas.drawText不会换行，即使一个很长的字符串也只会显示一行，超出部分会隐藏在屏幕之外.StaticLayout是android中处理文字的一个工具类，StaticLayout 处理了文字换行的问题";

        StaticLayout sl = StaticLayout.Builder.obtain(text,0,text.length(),textPaint,newBitmap.getWidth()).build();
//                new StaticLayout(text, textPaint, newBitmap.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        textPaint.setStyle(Paint.Style.FILL);
        canvas.translate(0, height / 10);
        sl.draw(canvas);
        return newBitmap;
    }


    /**
     * 生成二维码bitmap 要转换的地址或字符串,可以是中文
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createQRImage(String url, final int width, final int height) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap createQRCodeBitmap(String content, int size) {
        return createQRCodeBitmap(content, size, size,"UTF-8","H", "1", Color.BLACK, Color.WHITE);
    }
    public static Bitmap createQRCodeBitmap(String content, int size,String margin) {
        return createQRCodeBitmap(content, size, size,"UTF-8","H", margin, Color.BLACK, Color.WHITE);
    }
    public static Bitmap createQRCodeBitmap(String content, int size,String error_correction_level,String margin) {
        return createQRCodeBitmap(content, size, size,"UTF-8",error_correction_level, margin, Color.BLACK, Color.WHITE);
    }
    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmap(String content, int width,int height,
                                            String character_set,String error_correction_level,
                                            String margin,int color_black, int color_white) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }




    /**
     * 把一张Bitmap图片转化为打印机可以打印的字节流
     *
     * @param bmp
     * @return
     */
    public static byte[] draw2PxPoint(Bitmap bmp) {
        //用来存储转换后的 bitmap 数据。为什么要再加1000，这是为了应对当图片高度无法
        //整除24时的情况。比如bitmap 分辨率为 240 * 250，占用 7500 byte，
        //但是实际上要存储11行数据，每一行需要 24 * 240 / 8 =720byte 的空间。再加上一些指令存储的开销，
        //所以多申请 1000byte 的空间是稳妥的，不然运行时会抛出数组访问越界的异常。
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
        byte[] data = new byte[size];
        int k = 0;
        //设置行距为0的指令
        data[k++] = 0x1B;
        data[k++] = 0x33;
        data[k++] = 0x00;
        // 逐行打印
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            //打印图片的指令
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33;
            data[k++] = (byte) (bmp.getWidth() % 256); //nL
            data[k++] = (byte) (bmp.getWidth() / 256); //nH
            //对于每一行，逐列打印
            for (int i = 0; i < bmp.getWidth(); i++) {
                //每一列24个像素点，分为3个字节存储
                for (int m = 0; m < 3; m++) {
                    //每个字节表示8个像素点，0表示白色，1表示黑色
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        data[k] += data[k] + b;
                    }
                    k++;
                }
            }
//            data[k++] = 10;//换行
            data[k++]=0x0A;
        }
        return data;
    }
    /**
     * 灰度图片黑白化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bit 位图
     * @return
     */
    public static byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     */
    private static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);  //灰度转化公式
        return gray;
    }




    /**
     * 将图片转换成POS机能打印的byte[]类型
     *
     * @param bm
     * @param doubleWidth
     * @param doubleHeight
     * @return
     */
    public static byte[] genBitmapCode(Bitmap bm, boolean doubleWidth, boolean doubleHeight) {
        int w = bm.getWidth();
        int h = bm.getHeight();
//        if(w > MAX_BIT_WIDTH)
//            w = MAX_BIT_WIDTH;
        int bitw = ((w + 7) / 8) * 8;
        int bith = h;
        int pitch = bitw / 8;
        byte[] cmd = {0x1D, 0x76, 0x30, 0x00, (byte) (pitch & 0xff), (byte) ((pitch >> 8) & 0xff), (byte) (bith & 0xff), (byte) ((bith >> 8) & 0xff)};
        byte[] bits = new byte[bith * pitch];
        // 倍宽
        if (doubleWidth) {
            cmd[3] |= 0x01;
        }
        // 倍高
        if (doubleHeight) {
            cmd[3] |= 0x02;
        }
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int color = bm.getPixel(x, y);
                if ((color & 0xFF) < 128) {
                    bits[y * pitch + x / 8] |= (0x80 >> (x % 8));
                }
            }
        }

        ByteBuffer bb = ByteBuffer.allocate(cmd.length + bits.length);
        bb.put(cmd);
        bb.put(bits);

        return bb.array();
    }



    public static Bitmap fromText(float textSize, String text) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);

        Paint.FontMetricsInt fm = paint.getFontMetricsInt();

        int width = 100;//(int)paint.measureText(text);
        int height = 100;//fm.descent - fm.ascent;
        Log.d(TAG, "fromText: height = "+height);
        Log.d(TAG, "fromText: width = "+width);


        Bitmap bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 0, fm.leading - fm.ascent, paint);

        Log.d(TAG, "drawText fm.leading = "+fm.leading );
        Log.d(TAG, "drawText: fm.ascent = "+fm.ascent);
        canvas.save();

        return bitmap;
    }


    public static Bitmap convertToBMW(Bitmap bmp,int threshold,int densy) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int k = 0;
                int grey = pixels[width * i + j];
                // 分离三原色
                alpha = ((grey & 0xFF000000) >> 24);
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                if (red > threshold) {
                    red = 255;
                } else {
                    red = 0;
                }
                if (blue > threshold) {
                    blue = 255;
                } else {
                    blue = 0;
                }
                if (green > threshold) {
                    green = 255;
                } else {
                    green = 0;
                }
                pixels[width * i + j] = alpha << 24 | red << 16 | green << 8
                        | blue;

                if (pixels[width * i + j] == 0xffffffff) {
                    pixels[width * i + j] = 0xffffffff;//白色
                } else {
                    pixels[width * i + j] = 0xff000000;//-16777216;//黑色
                }
            }
        }
        pixels = setDensity(densy,pixels,width,height);
        Bitmap newBmp = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
        return resizeBmp;
    }

    public static  int[] setDensity(int density,int[] pixels,int width,int height){
        int[] result = new int[width*height];
        Random random = new Random();
        int myDensity = 0;

        if( density > 10 || density < 1){
            return pixels;
        }

        for (int i = 0; i < width*height; i++) {
            if(pixels[i] == 0xff000000){
                myDensity = random.nextInt(10);
                if( 0 <= myDensity && myDensity <= density){
                    result[i] = 0xff000000;//黑色
                }
            }else{
                result[i] = pixels[i];
            }
        }
        return result;
    }

    public static Bitmap createBlackWhiteImage(Bitmap image, float[] radios) {

        int width = image.getWidth();   //获取位图的宽
        int height = image.getHeight();  //获取位图的高

        Bitmap result = createBitmap(width, height, image.getConfig());

        int alpha = 0xff;
        int r = 0;
        int g = 0;
        int b = 0;
        int max = 0;
        int min = 0;
        int mid = 0;
        int gray = 0;

        float radioMax = 0;
        float radioMaxMid = 0;

        if (radios == null) {
            // 红        黄         绿         青         蓝        紫
            radios = new float[]{0.4f, 0.6f, 0.4f, 0.6f, 0.2f, 0.8f};
        }
        int[] resultPixle = new int[width * height];
        image.getPixels(resultPixle, 0, width, 0, 0, width, height);

        for (int i = 0; i < width; i++) {//一行行扫描
            for (int j = 0; j < height; j++) {

                gray = resultPixle[j * width + i];
//                gray = image.getPixel(i,j);//此方法效率极低，不要出现在循环体中，否则将导致极度耗时

                alpha = gray >>> 24;
                r = (gray >> 16) & 0x000000ff;
                g = (gray >> 8) & 0x000000ff;
                b = gray & 0x000000ff;


                if (r >= g && r >= b) {
                    max = r;
                    radioMax = radios[0];
                }
                if (g >= r && g >= b) {
                    max = g;
                    radioMax = radios[2];
                }
                if (b >= r && b >= g) {
                    max = b;
                    radioMax = radios[4];
                }
                if (r <= g && r <= b) { // g+ b = cyan 青色
                    min = r;
                    radioMaxMid = radios[3];
                }

                if (b <= r && b <= g) {//r+g = yellow 黄色
                    min = b;
                    radioMaxMid = radios[1];
                }
                if (g <= r && g <= b) {//r+b = m 洋红
                    min = g;
                    radioMaxMid = radios[5];
                }

                mid = r + g + b - max - min;

                //公式：gray= (max - mid) * ratio_max + (mid - min) * ratio_max_mid + min

                gray = (int) ((max - mid) * radioMax + (mid - min) * radioMaxMid + min);

                gray = (alpha << 24) | (gray << 16) | (gray << 8) | gray;

                //                2000x3500大图，耗时相差2~5倍左右
                //bitmap在循环中设置像素点，这个操作会导致耗时严重，耗时7秒。4096x4096图耗时22秒
                //                result.setPixel(i, j, gray);
                resultPixle[j * width + i] = gray;//直接改变数组，最后bitmap再设像素
            }
        }

        result.setPixels(resultPixle, 0, width, 0, 0, width, height);//最后bitmap再设像素

        return result;
    }


    public static Bitmap bitMapZoom(Bitmap bitmap,float horizlzoom, float vertialzoom) {
        Matrix matrix = new Matrix();
        if(horizlzoom <= 0 ||vertialzoom <= 0){
            return bitmap;
        }
        matrix.postScale(horizlzoom,vertialzoom); //长和宽放大缩小的比例
        Bitmap resizeBmp = createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

}
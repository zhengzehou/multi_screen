package top.betteryou.multi_screen.printer;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.betteryou.multi_screen.utils.BitmapUtils;

public class PrintUtil2 {
    private static final String TAG= PrintUtil2.class.getName ();
    public final static int WIDTH_PIXEL=384;


    /**
     * init Pos
     *
     * @throws IOException
     */
    public PrintUtil2() throws IOException {
        initPrinter ();
    }


    /**
     * init printer
     *
     * @throws IOException
     */
    public byte[] initPrinter() throws IOException {
        return new byte[]{0x1B,0x40};
    }

    /**
     * 获取打印状态
     * Get print status
     *
     * @throws IOException
     */
    public byte[] printState() throws IOException {
        return new byte[]{0x1D,0x61,0x22};
    }

    /**
     * 设置语言
     * language setting
     *
     * @param mode
     * @throws IOException
     */
    public byte[] setLanguage(int mode) throws IOException {
        return new byte[]{0x1B,0x23,0x23,0x53,0x4C,0x41,0x4E,(byte) mode};

    }

    /**
     * 设置浓度 25-39
     * Set encoding
     *
     * @param level
     * @throws IOException
     */
    public byte[] setConcentration(int level) throws IOException {
        return new byte[]{0x1B,0x23,0x23,0x53,0x54,0x44,0x50,(byte) level};
    }

    /**
     * 设置编码 2：utf-8
     * Set encoding
     *
     * @param encode
     * @throws IOException
     */
    public byte[] setEncode(int encode) throws IOException {
        byte b;
        if (encode == 1) {
            b = (byte) 2;
        } else {
            b = (byte) encode;
        }
        return new byte[]{0x1B,0x23,0x23,0x43,0x44,0x54,0x50,b};
    }

    /**
     * 文本强调模式
     * Text emphasis mode
     *
     * @param bold
     */
    public byte[] setTextBold(boolean bold) throws IOException {
        byte b;
        if (bold) {
            b = 0x01;
        } else {
            b = (0x00);
        }
        return new byte[]{0x1B,0x45,b};
    }

    /**
     * 设置字体大小
     * Set font size
     *
     * @param mode
     * @throws IOException
     */
    public byte[] setFontSize(int mode) throws IOException {
        byte[] data = {0x00,0x01,0x11};
        // mode = normal 0x00  middle 0x01  big  0x11
        return new byte[]{0x1D,0x21,data[mode]};
    }

    /**
     * 打印换行
     * Print line break
     *
     * @return length 需要打印的空行数 Number of blank lines to be printed
     * @throws IOException
     */
    public byte[] printLine(int lineNum) throws IOException {
        byte[] bb = "\n".getBytes();
        byte[] bytes = new byte[lineNum*bb.length];
        for (int i=0; i < lineNum; i++) {
            System.arraycopy(bb,0,bytes,0,bb.length);
        }
        return bytes;
    }

    /**
     * 打印换行
     * Print line breaks (only line breaks)
     *
     * @throws IOException
     */
    public byte[] printLine() throws IOException {
        return printLine (1);
    }

    /**
     * 打印空白
     * Print blank (a tab position, about 4 Chinese characters)
     *
     * @param length 需要打印空白的长度 Need to print the length of the blank,
     * @throws IOException
     */
    public byte[] printTabSpace(int length) throws IOException {
        byte[] bb = "\t".getBytes();
        byte[] bytes = new byte[length*bb.length];
        for (int i=0; i < length; i++) {
            System.arraycopy(bb,0,bytes,0,bb.length);
        }
        return bytes;
    }

    /**
     * 绝对打印位置
     * Absolute print position
     *
     * @return
     * @throws IOException
     */
    public byte[] setLocation(int offset) throws IOException {
        byte[] bs=new byte[4];
        bs[0]=0x1B;
        bs[1]=0x24;
        bs[2]=(byte) (offset % 256);
        bs[3]=(byte) (offset / 256);
        return bs;
    }

    public byte[] getGbk(String stText) throws IOException {
        byte[] returnText=stText.getBytes ("GB2312"); // Must be placed in try
        return returnText;
    }

    private int getStringPixLength(String str) {
        int pixLength=0;
        char c;
        for (int i=0; i < str.length (); i++) {
            c=str.charAt (i);
            if (isChineseChar (c)) {
                pixLength+=24;
            } else {
                pixLength+=12;
            }
        }
        return pixLength;
    }

    private boolean isChinese(String text){
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(text);
        if (m.find()) {
            return true;
        }
        return false;
    }
    boolean isChineseChar(Character c){
        if(!(19968 <= c && c <40869)) {
            return false;
        }
        return true;
    }

    public int getOffset(String str) {
        return WIDTH_PIXEL - getStringPixLength (str);
    }

    /**
     * 打印文字
     * Print text
     *
     * @param text
     * @throws IOException
     */
    public byte[] printText(String text) throws IOException {
        return text.getBytes();
    }

    /**
     * 对齐0:左对齐，1：居中，2：右对齐
     * Alignment 0: Left alignment, 1: Center, 2: Right alignment
     */
    public byte[] printAlignment(int type) throws IOException {
        byte[] align = {0x00,0x01,0x02};
        return new byte[]{0x1b,0x61,align[type]};
    }

    public byte[] printLeftMargin(int Param) throws IOException {
        return new byte[]{0x1D,0x4C,(byte) Param,(byte) (Param >> 8)};
    }

    /**
     * Large Text
     *
     * @param text
     * @throws IOException
     */
    public byte[] printLargeText(String text) throws IOException {
        byte[] b1 = new byte[]{0x1b,0x21,(byte) 48};
        byte[] b2 = text.getBytes();
        byte[] b3 = new byte[]{0x1b,0x21,(byte) 0};
        byte []b = bytesAppend(b1,b2);
        b = bytesAppend(b,b3);
        return b;
    }
    private byte[] bytesAppend(byte[] b1,byte[] b2){
        byte []b = new byte[b1.length+b2.length];
        System.arraycopy(b1,0,b,0,b1.length);
        System.arraycopy(b2,0,b,b1.length,b2.length);
        return  b;
    }


    /**
     * 开启一票一证
     * Open one ticket and one certificate
     *
     * @param bool
     * @throws IOException
     */
    public byte[] printEnableCertificate(boolean bool) throws IOException {
        byte b;
        if (bool) {
            b = (0x31);
        } else {
            b = (0x30);
        }
        return new byte[]{0x1B,0x23,0x23,0x46,0x54,0x4B,0x54,b};
    }

    /**
     * 小票开始
     * Small ticket start
     *
     * @param number
     * @throws IOException
     */
    public byte[] printStartNumber(int number) throws IOException {
        byte[] topByte=new byte[]{0x1D, 0x23, 0x53};
        byte[] endByte=createByteCode (number+"");
        byte[] senByte= new byte[topByte.length+endByte.length];//(topByte, endByte);
        System.arraycopy(topByte, 0, senByte, 0, topByte.length);
        System.arraycopy(endByte, 0, senByte, topByte.length, endByte.length);
        return senByte;
    }

    /**
     * 小票结尾
     * End of ticket
     *
     * @throws IOException
     */
    public byte[] printEndNumber() throws IOException {
        return new byte[]{0x1D,0x23,0x45};
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
        b1 = bytesAppend(b1,new byte[]{73});
        b1 = bytesAppend(b1,(dataLen+"").getBytes());
        b1 = bytesAppend(b1,(text).getBytes());
        return b1;

    }
    private byte[] createByteCode(String content) {
        byte[] arr = new byte[content.length()];
        for (int i = 0; i < content.length(); i++) {
            arr[i] = (byte) content.charAt(i);
        }
        return arr;
    }
    /**
     * printer QR
     *
     * @param text
     * @param height
     * @param width  384
     * @throws IOException
     */
    public byte[] printQR(String text, int height, int width) throws IOException {
        Bitmap bitmap= BitmapUtils.fromText (14,text);
        byte[] bmpByteArray=draw2PxPoint (bitmap);
        return bmpByteArray;
    }

    /**
     * 启用黑标检测
     * Enable black mark detection
     *
     * @param bool
     * @throws IOException
     */
    public byte[] printEnableMark(boolean bool) throws IOException {
        // enable block mark
        byte b;
        if (bool) {
            b = (0x44);
        } else {
            b = (0x66);
        }
        return new byte[]{0x1F,0x1B,0x1F,(byte)0x80,0x04,0x05,0x06,b};
    }

    /**
     * 转到下一个黑色标记
     * Go to next black mark
     *
     * @throws IOException
     */
    public byte[] printGoToNextMark() throws IOException {
        // check mark
        return new byte[]{0x1D,0x0C};
    }

    /**
     * 打印功能列表
     * Print function list
     *
     * @throws IOException
     */
    public byte[] printFeatureList() throws IOException {
        return new byte[]{0x1B,0x23,0x46};
    }

    /**
     * 重置打印机
     * reset printer
     *
     * @throws IOException
     */
    public byte[] resetPrint() throws IOException {
        return new byte[]{0x1B,0x23,0x23,0x52,0x54,0x46,0x41};
    }

    /**
     * 获取打印固件版本
     *
     * @throws IOException
     */
    public byte[] getVersion() throws IOException {
        return new byte[]{0x1D,0x49,0x41};
    }


    public byte[] printTwoColumn(String title, String content) throws IOException {
        int iNum=0;
        byte[] byteBuffer=new byte[100];
        byte[] tmp;

        tmp=getGbk (title);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=setLocation (getOffset (content));
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=getGbk (content);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);

        return byteBuffer;
    }

    public byte[] printThreeColumn(String left, String middle, String right) throws IOException {
        int iNum=0;
        byte[] byteBuffer=new byte[200];
        byte[] tmp=new byte[0];

        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=getGbk (left);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        int pixLength=getStringPixLength (left) % WIDTH_PIXEL;
        if (pixLength > WIDTH_PIXEL / 2 || pixLength == 0) {
            middle="\n\t\t" + middle;
        }

        tmp=setLocation (192);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=getGbk (middle);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=setLocation (getOffset (right));
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);
        iNum+=tmp.length;

        tmp=getGbk (right);
        System.arraycopy (tmp, 0, byteBuffer, iNum, tmp.length);

        return byteBuffer;
    }


    public byte[] printDashLine() throws IOException {
        return printText ("--------------------------------");
    }

    public byte[] printBitmap(Bitmap bmp) throws IOException {
        // bmp=BitmapUtils.compressPic (bmp);
        byte[] bmpByteArray=draw2PxPoint (bmp);
//        printRawBytes (bmpByteArray);
        return bmpByteArray;
    }


    /*************************************************************************
     * 假设一个360*360的图片，分辨率设为24, 共分15行打印 每一行,是一个 360 * 24 的点阵,y轴有24个点,存储在3个byte里面。
     * 即每个byte存储8个像素点信息。因为只有黑白两色，所以对应为1的位是黑色，对应为0的位是白色
     *
     * Suppose a 360*360 picture, the resolution is set to 24, a total of 15 lines are printed.
     * Each line is a 360 * 24 dot matrix, the y-axis has 24 points, stored in 3 bytes.
     * That is, each byte stores 8 pixel information.
     * Because there are only black and white, the bit corresponding to 1 is black, and the bit corresponding to 0 is white
     **************************************************************************/
    private byte[] draw2PxPoint(Bitmap bmp) {
        // 先设置一个足够大的size，最后在用数组拷贝复制到一个精确大小的byte数组中
        // First set a large enough size, and finally use the array copy to copy to an accurate size byte array
        int size=bmp.getWidth () * bmp.getHeight () / 8 + 1000;
        byte[] tmp=new byte[size];
        int k=0;
        // 设置行距为0 Set line spacing to 0
        tmp[k++]=0x1B;
        tmp[k++]=0x33;
        tmp[k++]=0x00;
        // 居中打印 Center print
        tmp[k++]=0x1B;
        tmp[k++]=0x61;
        tmp[k++]=1;
        for (int j=0; j < bmp.getHeight () / 24f; j++) {
            tmp[k++]=0x1B;
            tmp[k++]=0x2A;// 0x1B 2A 表示图片打印指令 Image print order
            tmp[k++]=33; // m=33时，选择24点密度打印 When m=33, choose 24-point density printing
            tmp[k++]=(byte) (bmp.getWidth () % 256); // nL
            tmp[k++]=(byte) (bmp.getWidth () / 256); // nH
            for (int i=0; i < bmp.getWidth (); i++) {
                for (int m=0; m < 3; m++) {
                    for (int n=0; n < 8; n++) {
                        byte b=px2Byte (i, j * 24 + m * 8 + n, bmp);
                        tmp[k]+=tmp[k] + b;
                    }
                    k++;
                }
            }
            tmp[k++]=10;// 换行
        }
        // 恢复默认行距 Restore default line spacing
        tmp[k++]=0x1B;
        tmp[k++]=0x32;

        byte[] result=new byte[k];
        System.arraycopy (tmp, 0, result, 0, k);
        return result;
    }

    /**
     * 图片二值化，黑色是1，白色是0
     * Image binarization, black is 1, white is 0
     *
     * @param x   横坐标 Abscissa
     * @param y   纵坐标 Y-axis
     * @param bit 位图 bitmap
     * @return
     */
    private byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth () && y < bit.getHeight ()) {
            byte b;
            int pixel=bit.getPixel (x, y);
            int red=(pixel & 0x00ff0000) >> 16; // 取高两位 High two
            int green=(pixel & 0x0000ff00) >> 8; // 取中两位 Hit two
            int blue=pixel & 0x000000ff; // 取低两位 Lower two
            int gray=RGB2Gray (red, green, blue);
            if (gray < 128) {
                b=1;
            } else {
                b=0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     * Image grayscale conversion
     */
    private int RGB2Gray(int r, int g, int b) {
        int gray=(int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式 Grayscale conversion formula
        return gray;
    }


}

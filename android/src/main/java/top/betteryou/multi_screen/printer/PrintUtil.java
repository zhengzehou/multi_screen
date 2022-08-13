package top.betteryou.multi_screen.printer;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.betteryou.multi_screen.utils.BitmapUtils;

public class PrintUtil {

    private static final String TAG=PrintUtil.class.getName ();
    private OutputStreamWriter mWriter=null;
    private OutputStream mOutputStream=null;
    public final static int WIDTH_PIXEL=384;


    /**
     * init Pos
     *
     * @param encoding 编码
     * @throws IOException
     */
    public PrintUtil(OutputStream outputStream, String encoding) throws IOException {
        mWriter=new OutputStreamWriter (outputStream, encoding);
        mOutputStream=outputStream;
        initPrinter ();
    }

    public void print(byte[] bs) throws IOException {
        mOutputStream.write (bs);
    }

    public void printRawBytes(byte[] bytes) throws IOException {
        mOutputStream.write (bytes);
        mOutputStream.flush ();
    }

    /**
     * init printer
     *
     * @throws IOException
     */
    public void initPrinter() throws IOException {
        mWriter.write (0x1B);
        mWriter.write (0x40);
        mWriter.flush ();
    }

    /**
     * 获取打印状态
     * Get print status
     *
     * @throws IOException
     */
    public void printState() throws IOException {
        mWriter.write (0x1D);
        mWriter.write (0x61);
        mWriter.write (0x22);
        mWriter.flush ();
    }

    /**
     * 设置语言
     * language setting
     *
     * @param mode
     * @throws IOException
     */
    public void setLanguage(int mode) throws IOException {
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x23);
        mWriter.write (0x53);
        mWriter.write (0x4C);
        mWriter.write (0x41);
        mWriter.write (0x4E);
        mWriter.write ((byte) mode);
        mWriter.flush ();
    }

    /**
     * 设置浓度 25-39
     * Set encoding
     *
     * @param level
     * @throws IOException
     */
    public void setConcentration(int level) throws IOException {
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x23);
        mWriter.write (0x53);
        mWriter.write (0x54);
        mWriter.write (0x44);
        mWriter.write (0x50);
        mWriter.write ((byte) level);
        mWriter.flush ();
    }

    /**
     * 设置编码 2：utf-8
     * Set encoding
     *
     * @param encode
     * @throws IOException
     */
    public void setEncode(int encode) throws IOException {
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x23);
        mWriter.write (0x43);
        mWriter.write (0x44);
        mWriter.write (0x54);
        mWriter.write (0x59);
        if (encode == 1) {
            mWriter.write ((byte) 2);
        } else {
            mWriter.write ((byte) encode);
        }
        mWriter.flush ();
    }

    /**
     * 文本强调模式
     * Text emphasis mode
     *
     * @param bold
     */
    public void setTextBold(boolean bold) throws IOException {
        mWriter.write (0x1B);
        mWriter.write (0x45);
        if (bold) {
            mWriter.write (0x01);
        } else {
            mWriter.write (0x00);
        }
        mWriter.flush ();
    }

    /**
     * 设置字体大小
     * Set font size
     *
     * @param mode
     * @throws IOException
     */
    public void setFontSize(int mode) throws IOException {
        byte[] data = {0x00,0x01,0x11};
        // mode = normal 0x00  middle 0x01  big  0x11
        mWriter.write (0x1D);
        mWriter.write (0x21);
        mWriter.write (data[mode]);
        mWriter.flush ();
    }

    /**
     * 打印换行
     * Print line break
     *
     * @return length 需要打印的空行数 Number of blank lines to be printed
     * @throws IOException
     */
    public void printLine(int lineNum) throws IOException {
        for (int i=0; i < lineNum; i++) {
            mWriter.write ("\n");
        }
        mWriter.flush ();
    }

    /**
     * 打印换行
     * Print line breaks (only line breaks)
     *
     * @throws IOException
     */
    public void printLine() throws IOException {
        printLine (1);
    }

    /**
     * 打印空白
     * Print blank (a tab position, about 4 Chinese characters)
     *
     * @param length 需要打印空白的长度 Need to print the length of the blank,
     * @throws IOException
     */
    public void printTabSpace(int length) throws IOException {
        for (int i=0; i < length; i++) {
            mWriter.write ("\t");
        }
        mWriter.flush ();
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
    public void printText(String text) throws IOException {
        mWriter.write (text);
        mWriter.flush ();
    }

    /**
     * 对齐0:左对齐，1：居中，2：右对齐
     * Alignment 0: Left alignment, 1: Center, 2: Right alignment
     */
    public void printAlignment(int type) throws IOException {
        byte[] align = {0x00,0x01,0x02};
        mWriter.write (0x1b);
        mWriter.write (0x61);
        mWriter.write (align[type]);
        mWriter.flush ();
    }

    public void printLeftMargin(int Param) throws IOException {
        mWriter.write (0x1D);
        mWriter.write (0x4C);
        mWriter.write ((byte) Param);
        mWriter.write ((byte) (Param >> 8));
        mWriter.flush ();
    }

    /**
     * Large Text
     *
     * @param text
     * @throws IOException
     */
    public void printLargeText(String text) throws IOException {
        mWriter.write (0x1b);
        mWriter.write (0x21);
        mWriter.write (48);
        mWriter.write (text);
        mWriter.write (0x1b);
        mWriter.write (0x21);
        mWriter.write (0);
        mWriter.flush ();
    }


    /**
     * 开启一票一证
     * Open one ticket and one certificate
     *
     * @param bool
     * @throws IOException
     */
    public void printEnableCertificate(boolean bool) throws IOException {
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x23);
        mWriter.write (0x46);
        mWriter.write (0x54);
        mWriter.write (0x4B);
        mWriter.write (0x54);
        if (bool) {
            mWriter.write (0x31);
        } else {
            mWriter.write (0x30);
        }
        mWriter.flush ();
    }

    /**
     * 小票开始
     * Small ticket start
     *
     * @param number
     * @throws IOException
     */
    public void printStartNumber(int number) throws IOException {
        byte[] topByte=new byte[]{0x1D, 0x23, 0x53};
        byte[] endByte=createByteCode (number+"");
        byte[] senByte= new byte[topByte.length+endByte.length];//(topByte, endByte);
        System.arraycopy(topByte, 0, senByte, 0, topByte.length);
        System.arraycopy(endByte, 0, senByte, topByte.length, endByte.length);
        mOutputStream.write (senByte);
        mOutputStream.flush ();
    }

    /**
     * 小票结尾
     * End of ticket
     *
     * @throws IOException
     */
    public void printEndNumber() throws IOException {
        mWriter.write (0x1D);
        mWriter.write (0x23);
        mWriter.write (0x45);
        mWriter.flush ();
    }


    /**
     * printer barcode
     *
     * @param text
     * @param Height
     * @param Width  1-4
     * @throws IOException
     */
    public void printBarcode(String text, int Height, int Width) throws IOException {
        int dataLen=text.getBytes ("GB2312").length;
        mWriter.write (0x1D);
        mWriter.write ("h");
        mWriter.write (Height);

        mWriter.write (0x1D);
        mWriter.write ("w");
        mWriter.write (Width);

        mWriter.write (0x1D);
        mWriter.write ("k");
        mWriter.write (73);
        mWriter.write (dataLen);
        mWriter.write (text);
        mWriter.flush ();

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
    public void printQR(String text, int height, int width) throws IOException {
        Bitmap bitmap= BitmapUtils.fromText (14,text);
        byte[] bmpByteArray=draw2PxPoint (bitmap);
        mOutputStream.write (bmpByteArray);
        mWriter.flush ();
    }

    /**
     * 启用黑标检测
     * Enable black mark detection
     *
     * @param bool
     * @throws IOException
     */
    public void printEnableMark(boolean bool) throws IOException {
        // enable block mark
        mWriter.write (0x1F);
        mWriter.write (0x1B);
        mWriter.write (0x1F);
        mWriter.write ((byte) 0x80);
        mWriter.write (0x04);
        mWriter.write (0x05);
        mWriter.write (0x06);
        if (bool) {
            mWriter.write (0x44);
        } else {
            mWriter.write (0x66);
        }
        mWriter.flush ();
    }

    /**
     * 转到下一个黑色标记
     * Go to next black mark
     *
     * @throws IOException
     */
    public void printGoToNextMark() throws IOException {
        // check mark
        mWriter.write (0x1D);
        mWriter.write (0x0C);
        mWriter.flush ();
    }

    /**
     * 打印功能列表
     * Print function list
     *
     * @throws IOException
     */
    public void printFeatureList() throws IOException {
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x46);
        mWriter.flush ();
    }

    /**
     * 重置打印机
     * reset printer
     *
     * @throws IOException
     */
    public void resetPrint() throws IOException {
        mWriter.write (0x1B);
        mWriter.write (0x23);
        mWriter.write (0x23);
        mWriter.write (0x52);
        mWriter.write (0x54);
        mWriter.write (0x46);
        mWriter.write (0x41);
        mWriter.flush ();
    }

    /**
     * 获取打印固件版本
     *
     * @throws IOException
     */
    public void getVersion() throws IOException {
        mWriter.write (0x1D);
        mWriter.write (0x49);
        mWriter.write (0x41);
        mWriter.flush ();
    }


    public void printTwoColumn(String title, String content) throws IOException {
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

        print (byteBuffer);
    }

    public void printThreeColumn(String left, String middle, String right) throws IOException {
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

        print (byteBuffer);
    }


    public void printDashLine() throws IOException {
        printText ("--------------------------------");
    }

    public void printBitmap(Bitmap bmp) throws IOException {
        // bmp=BitmapUtils.compressPic (bmp);
        byte[] bmpByteArray=draw2PxPoint (bmp);
        printRawBytes (bmpByteArray);
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

    public static void printText(OutputStream mOutputStream,int number) {
        try {
            PrintUtil pUtil = new PrintUtil(mOutputStream, "GB2312");
            pUtil.printStartNumber(number);
            pUtil.setConcentration(25);
            // 店铺名 居中 放大
            pUtil.setFontSize(2);
            pUtil.setTextBold(true); // 是否加粗
            pUtil.printAlignment(1); // 对齐方式
            pUtil.printText("The credentials of cashier");
            pUtil.setTextBold(false); // 关闭加粗
            pUtil.setFontSize(0); // 字体大小
            pUtil.printLine(); // 线
            pUtil.printAlignment(0);
            pUtil.printLine();

            pUtil.printTwoColumn("Time: ", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            pUtil.printLine();

            pUtil.printTwoColumn("order number:", System.currentTimeMillis() + "");
            pUtil.printLine();

            pUtil.printTwoColumn("Payer:", "VitaminChen");
            pUtil.printLine();

            // 分隔线
            pUtil.printDashLine();
            pUtil.printLine();

            //打印商品列表
            pUtil.printText("commodity");
            pUtil.printTabSpace(2);
            pUtil.printText("Quantity");
            pUtil.printTabSpace(1);
            pUtil.printText("    unit price");
            pUtil.printLine();

            pUtil.printThreeColumn("iphone6", "1", "4999.00");
            pUtil.printThreeColumn("iphone6", "1", "4999.00");

            pUtil.printDashLine();
            pUtil.printLine();

            pUtil.printTwoColumn("order amount:", "9998.00");
            pUtil.printLine();

            pUtil.printTwoColumn("Amount received:", "10000.00");
            pUtil.printLine();

            pUtil.printTwoColumn("Change:", "2.00");
            pUtil.printLine();

            pUtil.printDashLine();
            pUtil.printLine();
            pUtil.printAlignment(1);
            pUtil.printBarcode("123456", 80, 2);
            pUtil.printLine();
            pUtil.printQR("1234456", 200, 200);
            pUtil.printLine(2);
            pUtil.printEndNumber();

        } catch (IOException e) {

        }
    }

}

package top.betteryou.multi_screen.printer;

public class GPrinterCommand {

    public static final byte[] left = new byte[]{0x1b, 0x61, 0x00};// 靠左
    public static final byte[] center = new byte[]{0x1b, 0x61, 0x01};// 居中
    public static final byte[] right = new byte[]{0x1b, 0x61, 0x02};// 靠右
    public static final byte[] bold = new byte[]{0x1b, 0x45, 0x01};// 选择加粗模式
    public static final byte[] bold_cancel = new byte[]{0x1b, 0x45, 0x00};// 取消加粗模式
    public static final byte[] text_normal_size = new byte[]{0x1d, 0x21, 0x00};// 字体不放大
    public static final byte[] text_big_height = new byte[]{0x1b, 0x21, 0x10};// 高加倍
    public static final byte[] text_big_size = new byte[]{0x1d, 0x21, 0x11};// 宽高加倍
    public static final byte[] reset = new byte[]{0x1b, 0x40};//复位打印机
    public static final byte[] print = new byte[]{0x0a};//打印并换行
    public static final byte[] under_line = new byte[]{0x1b, 0x2d, 2};//下划线
    public static final byte[] under_line_cancel = new byte[]{0x1b, 0x2d, 0};//下划线

    /**
     * 设置默认行间距
     */
    public static byte[] LINE_SPACING_DEFAULT = new byte[]{0x1b, 0x32};

    /**
     * 设置条形码高
     */
    public static byte[] BAR_CODE_H = new byte[]{0x1d, 0x68,0x6e};

    /**
     * 设置条形码宽
     */
    public static byte[] BAR_CODE_W = new byte[]{0x1d, 0x77, 0x3};

    /**
     * 换行
     */
    public static byte[] WRAP = new byte[]{0x0A};

    /**
     * 走纸（防止内容已打印，包含内容的纸张打印机未全部吐出来）
     */
    public static byte[] PAPER_FEED = new byte[]{0x1b, 0x4a, (byte) 0x96};

    /**
     * 切纸
     */
    public static byte[] PAPER_CUT = new byte[]{0x1d, 0x56, 0x00, 0x30};

    /**
     * 走纸
     *
     * @param n 行数
     * @return 命令
     */
    public static byte[] walkPaper(byte n) {
        return new byte[]{0x1b, 0x64, n};
    }

    /**
     * 设置横向和纵向移动单位
     *
     * @param x 横向移动
     * @param y 纵向移动
     * @return 命令
     */
    public static byte[] move(byte x, byte y) {
        return new byte[]{0x1d, 0x50, x, y};
    }

}
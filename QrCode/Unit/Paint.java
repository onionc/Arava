package Unit;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

/**
 * 画图
 */
public class Paint {

    int width;
    int height;
    int times;
    BufferedImage bi;

    /**
     * 指定数据和放大倍数绘制正方形图像。
     * 默认黑底和绘制白色模块。
     * @param data 数据，true 为白，false 为黑，可使用reverse反转。限制为正方形，如果行列不相等，以行为标准
     * @param times 放大倍数
     * @param reverse 反转，reverse=false，绘制data中true的数据；反之，reverse=true, 绘制data中false的数据。 
     */
    public Paint(boolean data[][], int times, boolean reverse){
        // 通过放大倍数（再次）计算宽度
        this.times = times>0 ? times : 1;
        this.width = this.height = data.length * this.times;
        
        this.bi = new BufferedImage(width, height,BufferedImage.TYPE_BYTE_BINARY);
        
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        
        for(int x=0; x<data.length; x++){
            for(int y=0; y<data[x].length; y++){
                if(data[x][y] ^ reverse){
                    g2.fillRect(y*times, x*times, times, times);
                }
            }
        }
    }
    

    /**
     * 指定图像宽度和数据
     * @param width 最终的图像宽度
     * @param height 图像高度
     * @param data
     * @param reverse
     */
    public Paint(int width, boolean data[][], boolean reverse){
        // 通过宽度计算放大倍数
        this(data, width/data.length, reverse);
    }

    /**
     * 默认reverse参数的构造函数
     */
    public Paint(boolean data[][], int times){
        this(data, times, false);
    }
    public Paint(int width, boolean data[][]){
        this(data, width/data.length, false);
    }
    
    /**
     * 保存图片
     * @param path
     */
    public void save(String path){
        try{
            ImageIO.write(this.bi, "JPEG", new FileOutputStream(path));
        }catch(IOException ignore){
            Log.getLogger().severe(path + " error.");
        }
    }
    
}

class PaintTest{
    public static void main(String[] args) throws IOException{
        boolean data[][] = {
            {true, false, true},
            {false, true, false},
            {false, true, false}
        };
        // 分别以放大比例、指定宽来绘制图片
        new Paint(data, 10).save("./image/a.jpg");
        new Paint(300, data).save("./image/a1.jpg");
        // 反转数据颜色
        new Paint(300, data, true).save("./image/a2.jpg");

        // 用int数组当做数据绘制
        int data1[][] = {{1,1,0}, {1,0,0}, {0,0,1}};
        new Paint(300, Common.intToBoolInMatrix(data1)).save("./image/a3.jpg");

        // finder patterns
        new Paint(300, Common.intToBoolInMatrix(Data.FinderPatterns), true).save("./image/a5.png");
    }
}
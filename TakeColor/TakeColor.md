## 取色器

备哥以前发我了一个小工具，[TakeColor](https://www.onlinedown.net/soft/39923.htm) 取色器。来复刻一个吧。

  ![1584410231478](http://image.acfuu.com/mdImages/202003/1584410231478.png)

分析一下：顶部是菜单，左侧框显示当前鼠标所在的颜色（下面显示当前坐标和颜色值，默认RGB），中间框显示鼠标周围区域，右侧显示取色的列表（按alt+c）取色。下面一行是颜色值，并且可以切换不同的模式。打开调色盘会显示底部的颜色框和调节滑块。

### TakeColor v1.0 

先来实现一个简单的屏幕取色。

鼠标的移动监听器中的 moved 和 dragged。moved 只在组件内有效，dragged 是拖动鼠标才产生的事件。在 [java 取得鼠标所在位置屏幕的颜色](https://www.cnblogs.com/liubin0509/archive/2012/11/21/2781063.html) 中发现了可以获取鼠标位置和颜色的代码：

```java
Point mousepoint = MouseInfo.getPointerInfo().getLocation();
Color pixel = robot.getPixelColor(mousepoint.x, mousepoint.y);
```

再加上定时器，就可以即时获取鼠标所在点像素的颜色了。

![takecolor_v1](http://image.acfuu.com/mdImages/202003/takecolor_v1.gif)

代码如下：

```java
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.MouseInfo;
import java.awt.AWTException;
import java.awt.Robot;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Timer;
import java.util.TimerTask;

class ColorFrame extends JFrame{

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 200;
    private JPanel mainPanel;
    private Robot robot;

    public ColorFrame(){
        // 窗体设置
        //setSize(WIDTH, HEIGHT);
        // 窗体居中
        Dimension centre = this.getScreenCentre();
        setLocation(centre.width - WIDTH/2, centre.height - HEIGHT/2);

        setTitle("TakeColor");
        setUndecorated(true); // 关闭所有框架装饰

        // 添加 panel
        mainPanel = new JPanel();
        add(mainPanel);
        pack();

        // 鼠标监听
        mouseListener();
    }

    /**
     * 鼠标监听，利用 timer
     */
    public void mouseListener() {
        try{
            robot = new Robot();
        }catch(AWTException e){
            e.printStackTrace();
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setPanelColor();
            }
        }, 100, 100);
    }

    /**
     * 获取位置和颜色，并设置panel颜色
     */
    private void setPanelColor(){
        Point point = MouseInfo.getPointerInfo().getLocation();
        Color pixel = robot.getPixelColor(point.x, point.y);
        System.out.println("Location:x=" + point.x + ", y=" + point.y + "\t" + pixel);
        mainPanel.setBackground(pixel);
    }
    
    /**
     * 获取屏幕中心点
     * @return Dimension
     */
    public Dimension getScreenCentre(){
        // 获取屏幕分辨率
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension(screenSize.width/2, screenSize.height/2);
    }

    /**
     * 重置窗口大小
     */
    public Dimension getPreferredSize(){
        return new Dimension(WIDTH, HEIGHT);
    }
}

public class TakeColor{
    public static void main(String [] args){
        EventQueue.invokeLater(() -> {
            ColorFrame f = new ColorFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭事件
            f.setVisible(true); // 显示窗体
        });
    }
}
```

之后[更改了代码结构](https://github.com/onionc/Arava/blob/4927722d6ed7eba2a78cd3ad24980bf6b9e63177/TakeColor/TakeColor.java)，main() 调用 frame, frame 只 add(panel) & pack()，主要操作都在 JPanel 中进行。

### TakeColor v1.1

这一版显示坐标和颜色值，放大鼠标周围区域。

#### 1. 显示颜色、位置以及颜色值

用一个 JPanel 设置背景颜色来显示当前颜色，两个 JLable 用来显示当前位置和颜色值。（为了节省计算量，鼠标在移动之前不重新获取颜色和放大区域。）

 ![1584608287035](http://image.acfuu.com/mdImages/202003/1584608287035.png)

#### 2. 放大镜

放大镜的功能，本来打算使用 Color 数组存储鼠标周围区域像素的Color,然后放大的话，每个像素放大成四个点。但是使用如下面这种代码，每个像素点都获取颜色`getPixelColor()`会很慢。

```java
for(int i=0; i<10; i++){
    for(int j=0; j<10; j++){
        areaColor[i][j] = robot.getPixelColor(x+i, y+i);
    }
}
```

找其他的不用动像素的办法：[使用Java实现截取电脑屏幕的功能](https://blog.csdn.net/Aiwen8/article/details/52373515)中发现可以屏幕截取然后展示

```java
// 截取图形，输出
Image image = robot.createScreenCapture(new Rectangle(0, 0, width, height));
BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
Graphics g = bi.createGraphics();
g.drawImage(image, 0, 0, width, height, null);
```

##### 2.1 屏幕动态截图效果

为了直接将image对象显示到界面上，我们可以覆盖 JComponent 类的 `paintCompoent()`，得到 Graphics 变量用来绘制。因为 JPanel 继承了 JComponent, 所以同样可以用 paintCompoent()

```java
class TakeColorPanel extends JPanel{
	/**
	 * 执行的方法
	 */
	public function xxx(){
    	// 截取以光标所在点为中心点，宽高为100像素的屏幕图像
    	int x = mousePoint.x;
        int y = mousePoint.y;
        Rectangle r = new Rectangle (x-50, y-50, 100, 100);
        areaImage = robot.createScreenCapture(r);
        
        repaint(); // 重绘，调用 paintComponent
    }
	
	/**
     * 绘制界面
     */
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(areaImage,120,10,null); // 绘制到中间区域
    }

}
```

展示屏幕截图的效果：**ROOM·屠宰场**

  ![room.png](http://image.acfuu.com/mdImages/202003/room.png)

 动图：

  ![room.png](http://image.acfuu.com/mdImages/202003/room.gif)

*真的像特拉法尔加·罗的能力*

> **ROOM·屠宰场**
>
> 能在自己制造的半球状/全球状空间里进行物件转换，能够将物体或是生物切成数块后在他控制的范围内重新组合，被分离的生物不会死、不痛、也不会流血。此外，罗可以借着此技能帮助特定伤患清除体内的有害物质，达到治疗伤患的效果。也能够进行瞬间移动，进入常理下无法进入的建筑物内。

##### 2.2 放大

发现 drawImage() 就可以放大图片。

 ![drawImage.png](http://image.acfuu.com/mdImages/202003/drawImage.png)





对于50\*50像素的图片，对于下面的代码， 第二行会显示放大1倍的效果（变成了100\*100）

```
g2.drawImage(areaImage,120,10,null);
g2.drawImage(areaImage,180,10,100,100,null);
```

 ![drawImageCompare.png](http://image.acfuu.com/mdImages/202003/drawImageCompare.png)

放大效果完成。

之后添加边框，和原版对比的效果如下：

 ![zoom.png](http://image.acfuu.com/mdImages/202003/zoom.png)



疑惑：在vscode中修改 paintComponent ，代码修改保存之后会即时的改变。这不是变成了脚本语言了么？

### TakeColor v1.2

这一版添加按键记录颜色







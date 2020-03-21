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

这一版添加按键记录颜色历史。

但是没有在颜色添加上颜色值（JLabel样式好像有边框，设置的高度总是不对，不会调），直接用填充矩形来展示颜色历史：

```java
// 右侧用矩形填充颜色
colorRecordRect.setFrameFromDiagonal(230,10+20*i,230+100,10+20*(i+1));
g2.draw(colorRecordRect);
g2.fill(colorRecordRect);
```

 ![colorRecord.png](http://image.acfuu.com/mdImages/202003/colorRecord.png)



第二天发现 JLabel 高度是好着, 我将 setBounds() 的后两个参数宽高，写成右下角坐标了。所以改用 JLabel 来设置背景色和显示文字：

```java
private JLabel colorRecordValue[] = new JLabel[colorRecordMax];
......
// label 组件添加
// 右侧的颜色背景和颜色值label。位置和大小230, 10, 100, 100
for(int i=0; i<colorRecordMax;i++){
    colorRecordValue[i] = new JLabel();
    colorRecordValue[i].setOpaque(true); // 背景不透明  
    colorRecordValue[i].setBounds(230, 10 + i*20, 100, 20);
    add(colorRecordValue[i]);
}
......
// 显示颜色
for(Color c: colorQueue){
    Color penC = new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue()); // 反色
    // 字体颜色设置
    colorRecordValue[i].setForeground(penC);
    colorRecordValue[i].setText(getColorText(c));
    // 背景色
    colorRecordValue[i].setBackground(c);
    
    if(++i>colorRecordMax)
		break;
}
```

注意，设置 JLabel 背景颜色的时候需要设置背景不透明 `jlabel.setOpaque(true);`

文字颜色值不能一直是黑色，不然黑色背景就会看不到。这里取反色，即 Color(255-R, 255-G, 255-B) 的值。

效果如下：

 ![tkv1.2.png](http://image.acfuu.com/mdImages/202003/tkv1.2.png)



### TakeColor v1.3

这一版添加颜色模式的 select 框、颜色输入框和复制。

**下拉框**

使用下拉框放置颜色模式，获取选项时可以直接使用 `colorModeCombo.getSelectedItem()` 而不是书中这种根据索引再获取 `colorModeCombo.getItemAt(colorModeCombo.getSelectedIndex())`，书中应该是用来展示各种用法的。

将一个字符串赋值给枚举值时，需要使用 valueOf 方法将字符串转为枚举值，参考[如何判断枚举和字符串相等(最简便方法)](https://www.iteye.com/blog/nannan408-2194152)：

```java
enum A {  
	a, b, c, d;  
}  

A aa = A.valueOf(A.class, "a");  
System.out.println(aa == A.a);  
```

所以当选项改变，需要改变当前颜色模式：(selete 选项值转 String 是因为 getSlectedItem() 返回的是 current selected Object )

```java
colorModeCombo.addActionListener(event -> 
{
    currentColorMode = ColorMode.valueOf((String)colorModeCombo.getSelectedItem());
});
```

颜色框和按钮都是常规的，访问剪切板参考[Java操作系统剪贴板(Clipboard)实现复制和粘贴](https://blog.csdn.net/xietansheng/article/details/70478266) 。

我们的复刻取色器项目到这里就完成了，菜单不想加了，因为菜单加上去也最多加个放大倍数的效果，意义不大了。



最终成果：上面是原工具，下面是复刻版

 ![takecolor.png](http://image.acfuu.com/mdImages/202003/takecolor.png)



Tips: 控制台编译时如果出现编码错误，可以指定编码编译`javac -encoding utf-8 TakeColor.java`



$^{周六下午阴阴的天，听着歌，敲着没有什么实际意义代码感觉好舒服}$
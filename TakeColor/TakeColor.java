import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.MouseInfo;
import java.awt.AWTException;
import java.awt.Robot;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import java.util.Timer;
import java.util.TimerTask;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Graphics;

import java.awt.geom.*;

import javax.swing.InputMap;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.awt.Event.ActionEvent;
import javax.awt.Event.ActionListener;

/**
 * frame
 */
class TakeColorFrame extends JFrame{
    private static final long serialVersionUID = 1L;

    public TakeColorFrame() {
        add(new TakeColorPanel());
        pack();
    }
}

class TakeColorPanel extends JPanel{

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 200;
    private JPanel colorPanel; // 颜色展示面板
    private JPanel recordPanel; // 颜色记录框
    private JLabel coordsJlabel; // 坐标信息
    private JLabel colorJlabel; // 颜色信息
    
    enum ColorMode {RGB, HTML, HEX, HSL, HSB}; // 颜色模式
    ColorMode currentColorMode = ColorMode.RGB;

    private Point mousePoint; // 光标点
    private Image areaImage; // 待放大的图片

    private Robot robot;
    private int zoomValue = 3; // 放大倍数，（只会放大到100像素）
    private static final int ZoomMax = 100; // 总的放大倍数，不能更改
    private int sideLength = ZoomMax/zoomValue;

    // 上一次的光标位置
    private Point prevPoint = null;

    // 交叉线
    Line2D crossHorizontal;
    Line2D crossVertical;
    
    public TakeColorPanel(){
        // 窗体居中
        Dimension centre = this.getScreenCentre();
        setLocation(centre.width - WIDTH/2, centre.height - HEIGHT/2);

        // 无布局方式
        setLayout(null);


        // 左侧的颜色框和显示的坐标，颜色值
        // 添加 panel 组件用来做颜色框
        colorPanel = new JPanel();
        colorPanel.setBounds(10, 10, 100, 60);
        add(colorPanel);
        // 坐标
        coordsJlabel = new JLabel();
        coordsJlabel.setBounds(10, 70, 100, 20);
        add(coordsJlabel);
        // 颜色值
        colorJlabel = new JLabel();
        colorJlabel.setBounds(10, 90, 100, 20);
        add(colorJlabel);

        // 中间的放大镜效果直接用图片绘制，这里需要十字线（位置10,120 大小100,100）
        crossHorizontal = new Line2D.Double(120+50, 10, 120+50, 110);
        crossVertical = new Line2D.Double(120, 10+50, 120+100, 10+50);
     
        // 右侧
        recordPanel = new JPanel();
        recordPanel.setBounds(230, 10, 100, 100);
        add(recordPanel);

        // 键盘检测
        Action recordAction = new RecordAction();  // '记录'动作
        InputMap imap = recordPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        imap.put(KeyStroke.getKeyStroke("alt C"), "record.Color");
        ActionMap amap = recordPanel.getActionMap();
        amap.put("record.Color", recordAction);


        // 鼠标监听
        mouseListener();
    }

    public class RecordAction extends AbstractAction{
        private static final long serialVersionUID = 1L;

        public RecordAction() {
            System.out.println("2123");
        }
         @override
        public void actionPerformed(ActionEvent event){
            System.out.println(mousePoint);
        }
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
     * 获取位置和颜色，并显示位置和颜色信息
     */
    private void setPanelColor(){
        // 获取光标位置之后，与上次比对，避免重复运行
        mousePoint = MouseInfo.getPointerInfo().getLocation();
        
        if(mousePoint.equals(prevPoint)){
            return;
        }else{
            prevPoint = mousePoint;
        }
        
        Color pixel = robot.getPixelColor(mousePoint.x, mousePoint.y);
        colorPanel.setBackground(pixel);

        coordsJlabel.setText(String.format("[%d, %d]", mousePoint.x, mousePoint.y));
        colorJlabel.setText(setColorLabel(pixel));

        // 获取区域
        getMouseArea();
    }

    /**
     * 根据当前颜色模式显示颜色值
     * @param Color c
     * @return
     */
    private String setColorLabel(Color c){
        String s = "";
        switch(currentColorMode){
            case RGB:
                s = String.format("%d, %d, %d", c.getRed(), c.getGreen(), c.getBlue());
                break;
            case HTML:
                break;
            case HEX:
                break;
            case HSL:
                break;
            case HSB:
                break;
            default:
                break;
        }
        return s;
        
    }

    /**
     * 获取鼠标区域
     */
    protected void getMouseArea(){

        int x = mousePoint.x;
        int y = mousePoint.y;
        
        Rectangle r = new Rectangle (x-sideLength/2, y-sideLength/2, sideLength, sideLength);
        areaImage = robot.createScreenCapture(r);
     
        repaint(); // 重绘，调用 paintComponent
    }
    /**
     * 放大屏幕
     */
    protected void zoom(){
        
    }

    /**
     * 绘制界面
     */
    public void paintComponent(Graphics g){
        // 父类的paitComponent需要绘制其他默认的组件，比如左侧颜色框panel以及坐标和颜色值label。如果不执行，则绘制的区域会重叠
        super.paintComponent(g);
        
        // 中间放大镜
        Graphics2D g2 = (Graphics2D) g;
        //g2.drawImage(areaImage,10,300,null); // 原大小
        g2.drawImage(areaImage,120,10,100,100,null);
        // 放大镜的十字线
        g2.setPaint(Color.RED);
        g2.draw(crossHorizontal);
        g2.draw(crossVertical);



        // 绘制各组件的边框
        g2.setPaint(Color.BLACK);
        Rectangle2D colorRect = new Rectangle2D.Double();
        Rectangle2D zoomRect = new Rectangle2D.Double();
        //Rectangle2D colorRect = new Rectangle2D.Double();
        colorRect.setFrameFromDiagonal(10-1, 10-1, 110, 70);
        zoomRect.setFrameFromDiagonal(120-1, 10-1, 120+100, 110);
        g2.draw(colorRect);
        g2.draw(zoomRect);
    }

}

public class TakeColor{
    public static void main(String [] args){
        EventQueue.invokeLater(() -> {
            TakeColorFrame f = new TakeColorFrame();
            f.setTitle("TakeColor");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭事件
            f.setVisible(true); // 显示窗体
        });
    }
}
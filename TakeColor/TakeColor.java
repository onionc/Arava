import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
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

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Graphics;

import java.awt.geom.*;

import javax.swing.InputMap;
import javax.swing.AbstractAction;

/**
 * frame
 */
class TakeColorFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public TakeColorFrame() {
        add(new TakeColorPanel());
        pack();
    }
}

class TakeColorPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 200;
    enum ColorMode {
        RGB, HTML, HEX, HSL, HSB
    }; // 颜色模式

    private Robot robot;
    private final JPanel colorPanel; // 颜色展示面板
    //private final JPanel recordPanel; // 颜色记录框
    private final JLabel coordsJlabel; // 坐标信息
    private final JLabel colorJlabel; // 颜色信息
    private Point mousePoint; // 光标点
    private Image areaImage; // 待放大的图片
    private final int zoomValue = 3; // 放大倍数，（只会放大到100像素）
    private static final int ZoomMax = 100; // 总的放大倍数，不能更改
    private final int sideLength = ZoomMax / zoomValue;
    // 上一次的光标位置
    private Point prevPoint = null;
    // 当前颜色模式
    private static ColorMode currentColorMode = ColorMode.RGB;
    // 交叉线
    private final Line2D crossHorizontal;
    private final Line2D crossVertical;
    // 记录颜色历史记录, 用LinkedList队列，
    private int colorRecordMax = 5; // 记录的color record个数
    private LinkedList<Color> colorQueue = new LinkedList<Color>(); 

    // 边框
    private final Rectangle2D colorRect = new Rectangle2D.Double();
    private final Rectangle2D zoomRect = new Rectangle2D.Double();
    private final Rectangle2D recordRect = new Rectangle2D.Double();
    // 颜色记录时绘制边框
    private static Rectangle2D colorRecordRect = new Rectangle2D.Double();
    // private JLabel colorRecordValue[] = new JLabel[colorRecordMax];

    
    public TakeColorPanel() {
        // 窗体居中
        final Dimension centre = this.getScreenCentre();
        setLocation(centre.width - WIDTH / 2, centre.height - HEIGHT / 2);

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

        // 放大镜的十字线（位置10,120 大小100,100）
        crossHorizontal = new Line2D.Double(120+50, 10, 120+50, 10+100);
        crossVertical = new Line2D.Double(120, 10+50, 120+100, 10+50);

        // 右侧 位置和大小230, 10, 100, 100
        // 右侧的颜色值label，颜色直接绘制
        // for(int i=0; i<colorRecordMax;i++){
        //     colorRecordValue[i] = new JLabel("", JLabel.LEADING);
        //     colorRecordValue[i].setOpaque(true); // 背景不透明  
        //     colorRecordValue[i].setBounds(230, 10 + i*12, 100, (i+1)*12);
        //     add(colorRecordValue[i]);
        // }
        
        // add(recordPanel);
        

        // 键盘检测
        final Action recordAction = new RecordAction(); // '记录'动作
        final InputMap imap = colorPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        imap.put(KeyStroke.getKeyStroke("alt C"), "record.Color");
        final ActionMap amap = colorPanel.getActionMap();
        amap.put("record.Color", recordAction);

        // 鼠标监听
        mouseListener();
    }

    /**
     * 按键alt+c触发此动作，用来记录颜色值
     */
    public class RecordAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public RecordAction() {
            System.out.println("2123");
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            // 记录当前的颜色
            Color t = robot.getPixelColor(mousePoint.x, mousePoint.y);
            // 从头进尾出，就可以满足绘制到顶部的记录是最新的
            colorQueue.offerFirst(t);
            if(colorQueue.size()>colorRecordMax){
                colorQueue.pollLast(); // 删除尾
            }
            repaint();
        }

    }

    /**
     * 获取屏幕中心点
     * @return Dimension
     */
    public Dimension getScreenCentre(){
        // 获取屏幕分辨率
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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
        }catch(final AWTException e){
            e.printStackTrace();
        }

        final Timer timer = new Timer();
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
        
        final Color pixel = robot.getPixelColor(mousePoint.x, mousePoint.y);
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
    private String setColorLabel(final Color c){
        String s = "";
        switch(currentColorMode){
            case RGB:
                s = String.format("%d, %d, %d", c.getRed(), c.getGreen(), c.getBlue());
                break;
            case HTML:
                s = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
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
     * 获取鼠标周围区域图片
     */
    protected void getMouseArea(){

        final int x = mousePoint.x;
        final int y = mousePoint.y;
        
        final Rectangle r = new Rectangle (x-sideLength/2, y-sideLength/2, sideLength, sideLength);
        areaImage = robot.createScreenCapture(r);
     
        repaint(); // 重绘，调用 paintComponent
    }

    /**
     * 绘制界面
     */
    public void paintComponent(final Graphics g){
        // 父类的paitComponent需要绘制其他默认的组件，比如左侧颜色框panel以及坐标和颜色值label。如果不执行，则绘制的区域会重叠
        super.paintComponent(g);
        
        // 中间放大镜
        final Graphics2D g2 = (Graphics2D) g;
        //g2.drawImage(areaImage,10,300,null); // 原大小
        g2.drawImage(areaImage,120,10,100,100,null);
        // 放大镜的十字线
        g2.setPaint(Color.RED);
        g2.draw(crossHorizontal);
        g2.draw(crossVertical);

        // 右侧颜色历史记录
        paintColorRecord(g2);
        

        // 绘制各组件的边框
        paintBorder(g2);
        
    }

    /**
     * @param Graphics2D
     * 绘制历史记录
     */
    private void paintColorRecord(final Graphics2D g2){
        int i = 0;

        for(Color c: colorQueue){
            g2.setColor(c);
            g2.setPaint(c);
            colorRecordRect.setFrameFromDiagonal(230,10+20*i,230+100,10+20*(i+1));
            g2.draw(colorRecordRect);
            g2.fill(colorRecordRect);

            // 放置颜色值
            // g2.setPaint(Color.BLACK);
            // colorRecordValue[i].setText(setColorLabel(c));
            // colorRecordValue[i].setBackground(c);
          
            if(++i>colorRecordMax)
                break;
        }
    }

    /**
     * @param Graphics2D
     * 绘制边框
     */
    private void paintBorder(Graphics2D g2){
        g2.setPaint(Color.BLACK);
        colorRect.setFrameFromDiagonal(10-1, 10-1, 110, 70);
        zoomRect.setFrameFromDiagonal(120-1, 10-1, 120+100, 110);
        recordRect.setFrameFromDiagonal(230-1, 10-1, 230+100, 110);
        g2.draw(colorRect);
        g2.draw(zoomRect);
        g2.draw(recordRect);
    }

}

public class TakeColor{
    public static void main(final String [] args){
        EventQueue.invokeLater(() -> {
            final TakeColorFrame f = new TakeColorFrame();
            f.setTitle("TakeColor");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭事件
            f.setVisible(true); // 显示窗体
        });
    }
}
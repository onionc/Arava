import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.MouseInfo;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Robot;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.Timer;
import java.util.TimerTask;


/**
 * frame
 */
class TakeColorFrame extends JFrame{
    public TakeColorFrame(){
        add(new TakeColorPanel());
        pack();
    }
}

class TakeColorPanel extends JPanel{

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 200;
    private JPanel mainPanel;
    private Robot robot;

    public TakeColorPanel(){

        // 窗体居中
        Dimension centre = this.getScreenCentre();
        setLocation(centre.width - WIDTH/2, centre.height - HEIGHT/2);

        // 布局方式
        setLayout(new BorderLayout());

        // 添加 panel 组件
        mainPanel = new JPanel();
        add(mainPanel);



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
            TakeColorFrame f = new TakeColorFrame();
            f.setTitle("TakeColor");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭事件
            f.setVisible(true); // 显示窗体
        });
    }
}
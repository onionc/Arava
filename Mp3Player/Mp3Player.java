
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.text.DefaultCaret;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.Rectangle;

class PlayerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    MusicFile musics;
    public PlayerFrame() {
        add(new PlayerPanel(this));
        setUndecorated(true); // 关闭所有框架装饰
        pack();
        this.musics = new MusicFile();
    }
}
    
class PlayerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 150;
    // 显示框
    JTextArea showBox;
    String showHelpMsg = "[/h] 帮助\n[/q] 退出\n[/move x y] 移动窗口位置到x,y\n[/c] 清屏\n[open xxx] 打开歌曲目录xxx\n[play id] 播放编号为id的歌曲";
    String showMsg = showHelpMsg;
    // 命令框
    JTextField commandField;

    // 绿色
    final Color GREEN = new Color(88, 124, 12);

    // frame object
    PlayerFrame pf;

    public PlayerPanel(final PlayerFrame pf) {
        this.pf = pf;
        setBackground(Color.BLACK);
        // 布局方式：无
        setLayout(null);

        // 添加一个展示框
        showBox = new JTextArea(100, 100);
        showBox.setBounds(0, 0, 350, 120);
        showBox.setForeground(GREEN);
        showBox.setBackground(Color.BLACK);
        showBox.setLineWrap(true); // 自动换行
        showBox.setWrapStyleWord(true); // 超过即换行
        showBox.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        MatteBorder noBorder = new MatteBorder(0, 0, 0, 0, Color.BLACK);
        showBox.setBorder(noBorder);
        System.out.println(showBox.getWrapStyleWord());
        showBox.setFocusable(false); // 不获取焦点
        
       System.out.prinln(this.musics);
        // add(showBox);
        // 添加滚动条
        JScrollPane js = new JScrollPane(showBox);
        js.setBounds(0, 0, 350, 120);
        js.setBorder(noBorder);
        //分别设置水平和垂直滚动条总是隐藏
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        js.getVerticalScrollBarPolicy();
        add(js);

        // 添加一个命令输入框
        commandField = new JTextField();
        commandField.setBounds(0, 130, 350, 20);
        commandField.setBackground(Color.BLACK);
        final MatteBorder borderField = new MatteBorder(0, 1, 1, 0, GREEN);
        commandField.setBorder(borderField);
        commandField.setForeground(Color.GREEN);
        commandField.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        add(commandField);

        // 键盘事件
        InputMap imap = commandField.getInputMap(JComponent.WHEN_FOCUSED);
        // 回车，输入命令
        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "command.enter");
        commandField.getActionMap().put("command.enter", new KeyboardAction("enter"));
        // 右箭头，下一曲
        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "command.right");
        commandField.getActionMap().put("command.right", new KeyboardAction("next", js));

    }

    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * 键盘动作
     */
    private class KeyboardAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        private String name;
        private JScrollPane js;
        public KeyboardAction(String name) {
            this.name = name;
        }

        public KeyboardAction(String name, JScrollPane js){
            this.name = name;
            this.js = js;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            System.out.println(this.name);
            switch(this.name){
                case "enter":
                    final String commandStr = commandField.getText();
                    commandField.setText("");
                    // 执行命令
                    parseCommand(commandStr);
                    repaint();
                    break;
                case "up":
                
                // JScrollBar bar  = js.getVerticalScrollBar();
                
                // Rectangle a = js.getViewportBorderBounds();// setBorder(showBoxField);
                // System.out.println(a.height);
                // System.out.println(bar.getHeight());
                // bar.setValue(a.height-10);
                    break;
                default:
                    break;
            }
            
        }
        
    }

    /**
     * 解析命令
     * @param command
     */
    public void parseCommand(final String commandStr){
        System.out.println(showBox.getText());
        String newMsg = "";
        String commandKey = "", commandValue = "";
        final String[] t = commandStr.split("\\s+");
        try{
            int i=0;
            // 获取命令字符串，有空值则跳过
            if(t[i].equals("")){
                i++;
            }
            commandKey = t[i++]; 
            // 以/开始的是特殊命令
            if(commandKey.charAt(0) == '/'){
                switch(commandKey){
                    case "/h":
                        showMsg = showHelpMsg;
                        break;
                    case "/c":
                        showMsg = "";
                        break;
    
                    case "/q":
                        System.exit(0);
                        return;
                    case "/move":
                        final int x = Integer.valueOf(t[i++]);
                        final int y = Integer.valueOf(t[i++]);
                        this.pf.setLocation(x,y);
                        newMsg = "窗口位置已重置";
                        break;
                }
            }else{
                // 命令值
                commandValue = t[i];
                String r = Command.runCommand(commandKey, commandValue);
                newMsg = commandKey + " " + commandValue + "\n" + r;
                
            }
        }catch(final ArrayIndexOutOfBoundsException e){
            // 命令错误
            newMsg = "";
        }
        
        if(newMsg.length()>0){
            showMsg = showBox.getText() + "\n" + newMsg;
            //showBox.append(newMsg);
        }
        return;
    }



    /**
     * 重绘界面
     */
    public void paintComponent(final Graphics g){
        

        super.paintComponent(g);
        //final Graphics2D g2 = (Graphics2D) g;
        // 位置重置
        
        // 显示框刷新
        showBox.setText(showMsg);

    }
    
}

class Command{
        /**
     * 执行普通命令
     * @param key
     * @param value
     */
    public static String runCommand(String key, String value){
        String msg = "";
        if(key.equals("open")){
            //try{
                File path = new File(value);
                String dir = path.toURI().toString();
                if(!path.exists()){
                   msg = dir + " 目录未找到."; 
                }else{
                    List<File> musics = getMusicFiles(path);
                    int sum = musics.size();
                    if(sum > 0){
                        msg = String.format("%s 目录下共 %d 首歌曲", dir, sum);
                    }else{
                        msg = String.format("%s 目录下无 mp3 后缀歌曲", dir);
                    }
                }
        }
        return msg;
    }

    /**
     * 获取目录下的mp3文件
     * @param path
     * @return
     */
    private static List<File> getMusicFiles(File path){
        List<File> musics = new ArrayList<File>();

        if(!path.exists()) return null;

        File[] files = path.listFiles();
        System.out.println(files);
        for(File f : files){
            if(f.toString().endsWith(".mp3")){
                musics.add(f);
            }
        }

        return musics;
    }
}

public class Mp3Player{
    public static void main(final String[] args) throws IOException{
        EventQueue.invokeLater(() -> {
            final PlayerFrame f = new PlayerFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭事件
            f.setVisible(true); // 显示窗体
        });
    }
}

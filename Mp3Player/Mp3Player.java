
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


class AudioPlayer {

    File file;
    AudioInputStream in; // 文件流
    AudioFormat audioFormat; // 文件格式
    SourceDataLine sourceDataLine; // 输出设备

    public AudioPlayer(final File file) {
        this.file = file;
    }

    public void play() {
        try {
            // 取得文件输入流
            in = AudioSystem.getAudioInputStream(file);
            audioFormat = in.getFormat();
            // 转换 mp3 文件编码
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16,
                        audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false);
                in = AudioSystem.getAudioInputStream(audioFormat, in);
            }

            getProperties();
            // 播放
            rawplay(audioFormat, in);

            in.close();

        } catch (final Exception e) {
            // System.out.printf("file %s not found\n", file.toURI().toString());
            // }catch(JavaLayerException e){
            System.out.printf("Failed to get sources\n");
        }
    }

    /**
     * 原始播放
     */
    private void rawplay(final AudioFormat targetFormat, final AudioInputStream din) throws IOException, LineUnavailableException {
        final byte[] data = new byte[4096];
        final SourceDataLine line = getLine(targetFormat);
        if (line != null) {
            // Start
            line.start();
            int nBytesRead = 0;
            while (nBytesRead != -1) {
                nBytesRead = din.read(data, 0, data.length);
                if (nBytesRead != -1)
                    line.write(data, 0, nBytesRead);
            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }
    }

    /**
     * 
     * @param audioFormat
     * @return
     * @throws LineUnavailableException
     */
    private SourceDataLine getLine(final AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res = null;
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }

    /**
     * 获取属性
     */
    private Map getProperties() throws UnsupportedAudioFileException, IOException {
        AudioFileFormat baseFileFormat = null;
        baseFileFormat = AudioSystem.getAudioFileFormat(file);

        Map properties = null;
        // TAudioFileFormat properties
        if (baseFileFormat instanceof TAudioFileFormat) {
            properties = ((TAudioFileFormat) baseFileFormat).properties();
            System.out.println(properties);
        }
        return properties;
    }

    // private String
}

class PlayerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
// 窗口位置
Point position = new Point(10, 10);
    public PlayerFrame() {
        add(new PlayerPanel(this));
        //setUndecorated(true); // 关闭所有框架装饰
        pack();
    }
}
    
class PlayerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 150;
    // 显示框
    JTextArea showBox;
    String showHelpMsg = "[/h] 帮助\n[/q] 退出\n[/move x y] 移动窗口位置到x,y\n[/c] 清屏\n[open xxx] 打开歌曲目录xxx\n/[play id] 播放编号为id的歌曲";
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
        MatteBorder showBoxField = new MatteBorder(0, 1, 0, 0, Color.BLACK);
        showBox.setBorder(showBoxField);
        System.out.println(showBox.getWrapStyleWord());
        showBox.setFocusable(false); // 不获取焦点
        
       
        // add(showBox);
        // 添加滚动条
        JScrollPane js = new JScrollPane(showBox);
        js.setBounds(0, 0, 350, 120);
        js.setBorder(showBoxField);
        //分别设置水平和垂直滚动条总是隐藏
        js.setHorizontalScrollBarPolicy(
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        js.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_NEVER);
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

        // 回车
        final Action enterAction = new EnterAction();
        final InputMap imap = commandField.getInputMap(JComponent.WHEN_FOCUSED);
        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "command.enter");
        final ActionMap amap = commandField.getActionMap();
        amap.put("command.enter", enterAction);

    }

    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * 回车动作
     */
    private class EnterAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent e) {
            final String commandStr = commandField.getText();
            commandField.setText("");
            // 执行命令
            parseCommand(commandStr);
            repaint();
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
                newMsg = commandKey + " " + commandValue + "<br/>" + r;
                
            }
        }catch(final ArrayIndexOutOfBoundsException e){
            newMsg = "命令错误";
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
        if(key.equals("open")){
            //try{
                File path = new File(value);

                if(!path.exists()){
                   return path.toURI().toString() + " 目录未找到."; 
                }
                
                getMusicFiles(path);
            
        }
        return "sss";
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
            //f.setTitle("MP3播放器");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭事件
            f.setVisible(true); // 显示窗体
        });

        //File f = new File("123.mp3");
        //AudioPlayer p = new AudioPlayer(f);
        //p.play();
    }
}

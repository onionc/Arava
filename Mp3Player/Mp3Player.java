
import java.io.File;
import java.io.IOException;
import java.util.Map;

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
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.KeyStroke;
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


class AudioPlayer {

    File file;
    AudioInputStream in; // 文件流
    AudioFormat audioFormat; // 文件格式
    SourceDataLine sourceDataLine; // 输出设备

    public AudioPlayer(File file) {
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

        } catch (Exception e) {
            // System.out.printf("file %s not found\n", file.toURI().toString());
            // }catch(JavaLayerException e){
            System.out.printf("Failed to get sources\n");
        }
    }

    /**
     * 原始播放
     */
    private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException {
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
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
    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
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
        System.out.println("sdsdf");
        pack();

    }
}
    
class PlayerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 150;
    // 显示框
    JLabel showBox;
    String showHelpMsg = "<html>/h 帮助<br/>/q 退出<br/>/c 清屏<br/></html>";
    String showMsg = showHelpMsg;
    // 命令框
    JTextField commandField;

    // 绿色
    final Color GREEN = new Color(88, 124, 12); 

PlayerFrame pf;

    public PlayerPanel(PlayerFrame pf) {
        this.pf = pf;
        setBackground(Color.BLACK);
        // 布局方式：无
        setLayout(null);

        // 添加一个展示框
        showBox = new JLabel("", SwingConstants.LEFT);
        showBox.setBounds(0, 0, 350, 120);
        showBox.setForeground(GREEN);
        showBox.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(showBox);

        // 添加一个命令输入框
        commandField = new JTextField();
        commandField.setBounds(0, 130, 350, 20);
        commandField.setBackground(Color.BLACK);
        MatteBorder borderField = new MatteBorder(0, 1, 1, 0, GREEN);
        commandField.setBorder(borderField);
        commandField.setForeground(Color.GREEN);
        commandField.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        add(commandField);

        // 回车
        Action enterAction = new EnterAction();
        InputMap imap = commandField.getInputMap(JComponent.WHEN_FOCUSED);
        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "command.enter");
        ActionMap amap = commandField.getActionMap();
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
        public void actionPerformed(ActionEvent e) {
            String commandStr = commandField.getText();
            commandField.setText("");
            // 执行命令
            parseCommand(commandStr);
        }
        
    }

    /**
     * 解析命令
     * @param command
     */
    public void parseCommand(String commandStr){
        System.out.println(showBox.getText());
        String newMsg = "";
        String commandKey = "", commandValue = "";
        String[] t = commandStr.split("\\s+");
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
                        showMsg = "<html></html>";
                        break;
    
                    case "/q":
                        System.exit(0);
                        break;
                    case "/position":
                        int x = Integer.valueOf(t[i++]);
                        int y = Integer.valueOf(t[i++]);
                        this.pf.setLocation(x,y);
                        newMsg = "窗口位置已重置";
                        break;
                }
            }else{
                // 命令值
                commandValue = t[i];
                newMsg = commandKey + " " + commandValue;
            }
           
            
            
        }catch(ArrayIndexOutOfBoundsException e){
            newMsg = "命令错误";
        }
        
        if(newMsg.length()>0){
            //showMsg = showMsg.substring(0, showMsg.length()-"</html>".length()) + addMsg + "<br/></html>";
            showMsg = "<html>"+ newMsg +"</html>";
        }
       
        showBox.setText(showMsg);
    }

    /**
     * 重绘界面
     */
    public void paintComponent(Graphics g){
        

        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        // 位置重置
        
        
        showBox.setText(showMsg);
    }
    
}

public class Mp3Player{
    public static void main(String[] args) throws IOException{
        EventQueue.invokeLater(() -> {
            PlayerFrame f = new PlayerFrame();
            //f.setTitle("MP3播放器");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭事件
            f.setVisible(true); // 显示窗体
        });

        //File f = new File("123.mp3");
        //AudioPlayer p = new AudioPlayer(f);
        //p.play();
    }
}

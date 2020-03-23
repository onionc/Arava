
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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.KeyStroke;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.KeyEvent;

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

    public PlayerFrame() {
        add(new PlayerPanel());
        // setUndecorated(true); // 关闭所有框架装饰

        pack();

    }
}

class PlayerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 150;

    // 命令框
    JTextField command;

    public PlayerPanel() {
        setBackground(Color.BLACK);
        // 布局方式：无
        setLayout(null);

        // 添加一个命令输入框
        command = new JTextField();
        command.setBounds(0, 130, 350, 20);
        command.setBackground(Color.BLACK);
        MatteBorder border = new MatteBorder(0, 2, 0, 0, Color.GREEN);
        command.setBorder(border);
        command.setForeground(Color.GREEN);
        add(command);

        // 回车
        Action enterAction = new EnterAction();
        InputMap imap = command.getInputMap(JComponent.WHEN_FOCUSED);
        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "command.enter");
        ActionMap amap = command.getActionMap();
        amap.put("command.enter", enterAction);

    }

    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    private class EnterAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            String a = command.getText();
            command.setText("");
            System.out.println(a);
        }
        
    }
    
}

public class Mp3Player{
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            PlayerFrame f = new PlayerFrame();
            //f.setTitle("MP3播放器");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭事件
            
            f.setVisible(true); // 显示窗体
        });
        File f = new File("123.mp3");
        AudioPlayer p = new AudioPlayer(f);
        p.play();
    }
}


import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.KeyStroke;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.Graphics;

class PlayerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    public PlayerFrame() {
        add(new PlayerPanel(this));
        setUndecorated(true); // 关闭所有框架装饰
        pack();
    }
}
    
class PlayerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 150;
    // 显示框
    JTextArea showBox;
    String showHelpMsg = "[/h] 帮助\n[/q] 退出\n[/move x y] 移动窗口位置到x,y\n[/c] 清屏\n[open xxx] 打开歌曲目录xxx\n[play id] 播放编号为id的歌曲";
    String showMsg = showHelpMsg; // 显示信息
    String musicInfoMsg = "";
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

        showBox.setFocusable(false); // 不获取焦点
        
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

        // 使用线程显示播放进度条
        Runnable r = () -> {
            try{
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(MusicFile.getInstance().play == 1){
                            MusicFile.getInstance().generateProgressBar();
                            MusicFile.getInstance().currentMusic.progressTimerCount++;
                        }
                        repaint();
                    }
                }, 1000, 1000);
            }catch(Exception e){
                System.out.println("progress bar thread error");
            }finally{
                System.out.println("progress bar thread finally");
            }
        };
        MusicFile.getInstance().barThread = new Thread(r);
        MusicFile.getInstance().barThread.start();

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
            switch(this.name){
                case "enter":
                    final String commandStr = commandField.getText();
                    commandField.setText("");
                    // 执行命令
                    parseCommand(commandStr);
                    repaint();
                    break;
                case "next":
                    commandField.setText("");
                    // 执行命令
                    parseCommand("next");
                    repaint();
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

        String newMsg = "";
        String commandKey = "", commandValue = "";
        final String[] commandArr = commandStr.split("\\s+");
        try{
            int i=0;
            // 获取命令字符串，有空值则跳过
            if(commandArr[i].equals("")){
                i++;
            }
            commandKey = commandArr[i++]; 
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
                        final int x = Integer.valueOf(commandArr[i++]);
                        final int y = Integer.valueOf(commandArr[i++]);
                        this.pf.setLocation(x,y);
                        newMsg = "窗口位置已重置";
                        break;
                }
            }else{
                // 普通命令值
                String rMsg = "";
                switch(commandKey){
                    case "open":
                        commandValue = commandArr[i];
                        rMsg = Command.runCommand(commandKey, commandValue);
                        break;
                    case "play":
                    case "pause":
                    case "next":
                        rMsg = Command.runCommand(commandKey, "");
                        break;
                    default:
                        break;
                }
                
                
                newMsg = commandKey + " " + commandValue + "\n" + rMsg;
                
            }
        }catch(final ArrayIndexOutOfBoundsException e){
            // 命令错误
            newMsg = "命令不能解析";
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
        
        // 显示框刷新, 加上歌曲信息
        String shwoAllMsg = String.format(
            "%s\n%s%s",
            showMsg, 
            MusicFile.getInstance().currentMusic,
            MusicFile.getInstance().progressBar
        );
        showBox.setText(shwoAllMsg);

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
        switch(key){
            case "open":
                File path = new File(value);
                String dir = path.toURI().toString();
                if(!path.exists()){
                   msg = dir + " 目录未找到."; 
                }else{
                    int count = MusicFile.getInstance().getMusicFiles(path);
                    if(count > 0){
                        msg = String.format("%s 目录下共 %d 首歌曲", dir, count);

                    }else{
                        msg = String.format("%s 目录下无 mp3 后缀歌曲", dir);
                    }
                }
                break;
            case "play":
                if(MusicFile.getInstance().play == 1){
                    // 播放中无操作
                    break;
                }else if(MusicFile.getInstance().play == 2){
                    // 暂停中的，继续播放
                    synchronized (MusicFile.getInstance().object) {
                        MusicFile.getInstance().object.notify();
                        MusicFile.getInstance().play=1;
                    }
                  
                }else if(MusicFile.getInstance().play == 0){
                    if(MusicFile.getInstance().getCount() > 0){
                        // 使用线程播放歌曲
                        if(MusicFile.getInstance().playThread == null){
                            System.out.println("play thread null");

                            Runnable r = () -> {
                                try{
                                    // 如果状态为3, 表示下一曲。(上一次播放结束之后重新开始play())
                                    do{
                                        MusicFile.getInstance().play = 1;
                                        AudioPlay.getInstance().play();
                                    }while(MusicFile.getInstance().play == 3);
                                }catch(Exception e){
                                    System.out.println(e);
                                    System.out.println("play thread error");
                                    MusicFile.getInstance().playThread = null;
                                    MusicFile.getInstance().play = 0;
                                }finally{
                                    MusicFile.getInstance().play = 0;
                                }
                            };
                            MusicFile.getInstance().playThread = new Thread(r);
                            MusicFile.getInstance().playThread.start();
                        }
                        msg = "播放歌曲中";
                    }else{
                        msg = "此目录下无歌曲，请先指定目录 [open xxx]";
                    }
                }
                break;
            case "pause":
                if(MusicFile.getInstance().play == 1)
                    MusicFile.getInstance().play = 2;
                break;
            case "next":
                // 下一首步骤：播放（避免暂停中），状态改变，当前歌曲更新
                if(MusicFile.getInstance().play!=0){
                    runCommand("play", "");
                    MusicFile.getInstance().play = 3;
                    MusicFile.getInstance().nextMusic();
                }
                break;
            default:
                break;
        }
        return msg;
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

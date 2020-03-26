
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

class AudioPlay {

    File file;
    AudioInputStream in; // 文件流
    AudioFormat audioFormat; // 文件格式
    SourceDataLine sourceDataLine; // 输出设备

    public AudioPlay(File file) {
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
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Java技术栈线程被中断，程序退出。");
                    
                        return;
                    }
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



//File f = new File("123.mp3");
//AudioPlayer p = new AudioPlayer(f);
//p.play();

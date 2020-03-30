
import java.io.File;
import java.io.FileNotFoundException;
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
    private static AudioPlay instance;

    private AudioPlay() {}
    public static synchronized AudioPlay getInstance() {
        if(instance == null) {
            instance = new AudioPlay();
        }

        return instance;
    }
    
    public void play() throws InterruptedException{
        try {
            this.file = MusicFile.getInstance().getMusic();

            if(this.file == null){
                throw new FileNotFoundException("music file not found: " + this.file.toString());
            }else{
                // 获取文件信息
                getMusicInfo();
            }

            // 取得文件输入流
            in = AudioSystem.getAudioInputStream(this.file);
            audioFormat = in.getFormat();
            // 转换 mp3 文件编码
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16,
                        audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false);
                in = AudioSystem.getAudioInputStream(audioFormat, in);
            }
            // 播放
            rawplay(audioFormat, in);

            in.close();

        }catch(InterruptedException e){
            throw e;
        }catch (Exception e) {
            System.out.printf("Failed to get sources\n");
        }


        // !=3表示是正常播放结束，切换下一首
        if(MusicFile.getInstance().play!=3){
            MusicFile.getInstance().nextMusic();
            MusicFile.getInstance().play = 3;
        }
        
    }

    /**
     * 原始播放
     */
    private void  rawplay(final AudioFormat targetFormat, final AudioInputStream din) throws IOException, LineUnavailableException,InterruptedException {
        final byte[] data = new byte[4096];
        final SourceDataLine line = getLine(targetFormat);

        if (line != null) {
            // Start
            line.start();
            int nBytesRead = 0;
            while (nBytesRead != -1) {
                if(MusicFile.getInstance().play==1){
                    // 正常播放逻辑
                    nBytesRead = din.read(data, 0, data.length);
                    if (nBytesRead != -1)
                        line.write(data, 0, nBytesRead);
                }else if(MusicFile.getInstance().play==2){
                    // 阻塞暂停
                    synchronized (MusicFile.getInstance().object) {
                        MusicFile.getInstance().object.wait();
                    }

                }else if(MusicFile.getInstance().play==3){
                    // play 状态为3，则表示下一首，退出
                    break;
                }else{
                    ;
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
    private Map<?,?> getProperties() throws UnsupportedAudioFileException, IOException {
        AudioFileFormat baseFileFormat = null;
        baseFileFormat = AudioSystem.getAudioFileFormat(file);

        Map<?,?> properties = null;
        // TAudioFileFormat properties
        if (baseFileFormat instanceof TAudioFileFormat) {
            properties = ((TAudioFileFormat) baseFileFormat).properties();
        }
        return properties;
    }

    /**
     * 获取音乐文件信息
     */
    public void getMusicInfo(){
        try{
            Map<?,?> properties = this.getProperties();
            MusicFile.getInstance().setMusicInfo(
                (String) properties.get("title"), 
                (String) properties.get("author"),
                (long) properties.get("duration")
            );

            // 重置进度条
            MusicFile.getInstance().progressBar = "";
            MusicFile.getInstance().currentMusic.progressTimerCount = 0;
            
            
        }catch(Exception e){
            ;
        }
    }
}

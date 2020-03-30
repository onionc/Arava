import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class FileStruct{
    File file;
    String name;
    String author;
    int durationSeconds; // 时长
    String durationFormat; // 时长格式化
    int progressTimerCount = 0; // 当前播放到的秒数

    @Override
    public String toString(){
        if(this.file==null){
            return "";
        }
        String s = String.format(
            "♪ %s - %s\n",
            this.name, this.author
        );
        return s;
    }
}
/**
 * 存储音乐文件信息
 */
public class MusicFile{
    private static MusicFile singletonInstance;
    private File dir; // 目录
    private List<File> musics; // 文件
    public FileStruct currentMusic = new FileStruct(); // 当前的音乐文件
    private Iterator<?> interator; // 文件迭代器
    volatile Thread playThread; // 播放线程
    volatile Thread barThread; // 进度条线程
    int play = 0; // 0 初始,1 播放，2暂停
    Object object = new Object(); // 阻塞使用

    public String progressBar = ""; // 动态生成进度条
    
    private MusicFile() {}

    public static synchronized MusicFile getInstance() {
        if(singletonInstance == null) {
            singletonInstance = new MusicFile();
        }
        return singletonInstance;
    }

    public void setDir(File dir){
        this.dir = dir;
        this.musics = new LinkedList<File>();
    }

    public void addMusicFile(File file){
        if(this.musics == null) return;
        musics.add(file);
    }

    /**
     * 歌曲条数
     * @return
     */
    public int getCount(){
        if(this.musics == null) return 0;
        return musics.size();
    }

    /**
     * 获取目录下的mp3文件, 返回歌曲条数
     * @param path
     * @return int 
     */
    public int getMusicFiles(File dir){

        if(!dir.exists()) return 0;
        this.setDir(dir);
        for(File f : dir.listFiles()){
            if(f.toString().endsWith(".mp3")){
                MusicFile.getInstance().addMusicFile(f);
            }
        }

        return this.getCount();
    }

    /**
     * 获取当前或者第一首音乐文件
     * @return
     */
    public File getMusic(){
        if(this.currentMusic.file != null){

        }else if(this.interatorValid()){
            this.currentMusic.file = this.nextMusic();
        }else{
            return null;
        }
        return this.currentMusic.file;
    }

    /**
     * 下一首
     * @return
     */
    public File nextMusic(){
        // 有下一首取下一首，没有的话，取第一首
        if(!this.interatorValid()){
            return null;
        }
        if(this.interator.hasNext()){
            this.currentMusic.file = (File) this.interator.next();
        }else{
            this.interator = this.musics.iterator();
            this.currentMusic.file = (File) this.interator.next();
        }
        return this.currentMusic.file;

    }

    /**
     * 迭代器是否有效
     * @return boolean
     */
    private boolean interatorValid(){
        if(this.interator == null){
            if(this.getCount() <= 0){
                return false;
            }
            this.interator = this.musics.iterator();
        }
        return true;
    }

    
    /**
     * 设置音乐名、作者和时长
     * @param name
     * @param author
     * @param duration
     */
    public void setMusicInfo(String name, String author, long duration){
        this.currentMusic.name = name;
        this.currentMusic.author = author;
        this.currentMusic.durationSeconds = (int) duration/1000/1000;
        this.currentMusic.durationFormat = getDuration(this.currentMusic.durationSeconds);
    }

    
    /**
     * 获取格式化的时长
     * @param ds
     * @return
     */
    private String getDuration(int ds){
        int h = (int) ds/3600;
        int m = (int) (ds % 3600) / 60;
        int s = ds % 60;
        return String.format("%02d:%02d:%02d", h,m,s);
    }

    /**
     * 生成进度条
     */
    public void generateProgressBar(){
        final int LENGTH = 30; // 进度条长度

        // 使用毫秒可以避免进度条的问题，比如：歌曲长度小于LENGTH时 per 为0
        int msec = this.currentMusic.durationSeconds*1000; // 毫秒
        int per = (int) msec/LENGTH; // 每个符号多少毫秒

        // 进度条长度内，播放和未播放的数量（*表示播放，_表示未播放的占位）
        int starCount = (this.currentMusic.progressTimerCount*1000 / per);
        starCount = starCount>LENGTH ? LENGTH : (starCount<0 ? 0 : starCount); // 避免 java.lang.NegativeArraySizeException:
        int oCount = LENGTH - starCount;
        
        String startStr = new String(new char[starCount]).replace("\0", "*");
        String oStr = new String(new char[oCount]).replace("\0", "_");
        
        String currentTime = getDuration(this.currentMusic.progressTimerCount);
        this.progressBar = String.format("%s%s [%s/%s]", startStr, oStr, currentTime, this.currentMusic.durationFormat);
    }

}
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 存储音乐文件信息
 */
public class MusicFile{
    private static MusicFile singletonInstance;
    private File dir; // 目录
    private List<File> musics; // 文件
    private File currentMusic = null; // 当前的音乐文件
    private Iterator interator; // 文件迭代器
    volatile Thread playThread; // 播放线程
    volatile  int play = 0; // 0 初始,1 播放，2暂停
    Object object = new Object();
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
     * 获取当前或者第一首音乐的文件
     * @return
     */
    public File getMusic(){
        if(this.currentMusic != null){

        }else if(this.interatorValid()){
            this.currentMusic = this.nextMusic();
        }else{
            return null;
        }
        return this.currentMusic;
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
            this.currentMusic = (File) this.interator.next();
        }else{
            this.currentMusic = (File) this.interator.next();
        }
        return this.currentMusic;

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





}
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
    private Iterator currentMusic;// 当前的音乐文件 = linkedList.iterator();
    Thread t;
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
     * 获取下一首音乐文件，如果第一次或者到末尾则取第一首
     * @return
     */
    public File getMusic(){
        if(this.currentMusic == null){
            if(this.getCount() <= 0) return null;
            this.currentMusic = this.musics.iterator();
        }
        // 有下一首取下一首，没有的话，取第一首
        if(this.currentMusic.hasNext()){
            return (File) this.currentMusic.next();
        }else{
            return (File) this.musics.iterator().next();
        }
    }




}
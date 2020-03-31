## Mp3播放器

来写一个控制台版的mp3播放器。以前很喜欢 cmd.fm 这种控制台风格的播放器。

播放mp3使用 [mp3spi](http://www.javazoom.net/mp3spi/documents.html) 库：下载[mp3spi库](http://www.javazoom.net/mp3spi/sources.html)文件，解压，拿出根目录下的 `mp3spi1.9.5.jar` 和 lib 目录下的 `jl1.0.1.jar`、 `tritonus_share.jar` 。将这三个文件放到项目 lib 目录下。

>   Tips: 使用外部 jar 包，需要在编译时指定 classpath。例如，使用当前目录下的 jl1.0.jar，命令： `javac -cp ./jl1.0.jar  Mp3Player.java`。如果在 vscode 下开发，引入第三方包的方式是：添加 jar 包至根目录下lib文件夹，（有一些教程还要添加jar路径到.classpath 文件，我没有用到）。



#### 播放中的画面

 ![1585497394618](http://image.acfuu.com/mdImages/202003/1585497394618.png)

支持命令：

-   **/c** 清屏
-   **/q** 退出
-   **/move x y** 移动窗口到屏幕的 (x, y) 位置
-   **open xxx** 打开xxx目录，查找下面的mp3文件个数
-   **play** 播放
-   **pause/→** 暂停
-   **next** 下一首



#### 使用

如果使用vscode，直接打开目录，编译即可。

命令行编译：

```
javac  -encoding utf-8  -cp "./lib/jl1.0.1.jar;./lib/tritonus_share.jar;./lib/mp3spi1.9.5.jar" Mp3Player.java AudioPlay.java MusicFile.java
```



#### 遇到问题（未解决）

在 vscode 中可以编译运行，但是在命令行(cmd / powershell)中编译后会读取音频错误，错误信息如下：

```
javax.sound.sampled.UnsupportedAudioFileException: File of unsupported format
        at java.desktop/javax.sound.sampled.AudioSystem.getAudioInputStream(AudioSystem.java:1066)
        at AudioPlay.play(AudioPlay.java:46)
```

AudioPlay.java:46 的代码是：`in = AudioSystem.getAudioInputStream(this.file);`

库中报错代码：

 ![1585622285760](C:\chad\java\Arava\Mp3Player\README.assets\1585622285760.png)



1. 想把报错的类复制过来，发现如果使用 `import com.sun.media.sound.JDK13Services;` ，是属于模块未导出的包

   ` (程序包 com.sun.media.sound 已在模块 java.desktop 中声明, 但该模块未导出它)` 

2. 得考虑其他方法。以后再解决
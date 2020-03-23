## Mp3播放器

来写一个控制台版的mp3播放器。以前很喜欢 cmd.fm 这种控制台风格的播放器。
播放mp3使用 [mp3spi](http://www.javazoom.net/mp3spi/documents.html) 库。参考[使用Java实现MP3音乐播放器](https://www.cnblogs.com/haoxia/archive/2009/06/03/1495419.html)。



### 界面和命令解析

// todo

### 播放 mp3，获取时长

下载[mp3spi库](http://www.javazoom.net/mp3spi/sources.html)文件，解压，拿出根目录下的 `mp3spi1.9.5.jar` 和 lib 目录下的 `jl1.0.1.jar`、 `tritonus_share.jar` 。将这三个文件放到项目 lib 目录下。

>   Tips: 使用外部 jar 包，需要在编译时指定 classpath。例如，使用当前目录下的 jl1.0.jar，命令： `javac -cp ./jl1.0.jar  Mp3Player.java`。如果在 vscode 下开发，引入第三方包的方式是：添加 jar 包至根目录下lib文件夹，（有一些教程还要添加jar路径到.classpath 文件，我没有用到）。


package Unit;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 文件日志记录类
 */
public class Log {
    private static Logger logger;
    private static FileHandler fileHandler;

    private Log(){};
    public synchronized static Logger getLogger(){
        if(logger == null){
            logger = Logger.getLogger("QrCode");
            try{
                fileHandler = new FileHandler("a.log", true);
            }catch(IOException ignore){};
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new LogFormatter());
            
            logger.addHandler(fileHandler);
            
            // 不使用父类 handler 写日志到控制台
            logger.setUseParentHandlers(false);
        }
        return logger;
    }

    /**
     * 输出格式
     */
    public static class LogFormatter extends Formatter {
        public static final DateFormat df = new SimpleDateFormat("YYYY/MM/dd hh:mm:ss.SSS");

        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append(df.format(new Date(record.getMillis())));
            builder.append(" [").append(record.getLevel()).append("] ");
            builder.append(formatMessage(record));
            builder.append("\n");
            return builder.toString();
        }

    }
}
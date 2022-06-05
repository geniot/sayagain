package ua.i0xhex.vpartsextractor;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ua.i0xhex.vpartsextractor.util.FileUtils;

public class Terminal {
    
    private static boolean WRITE_TO_FILE_ENABLED = true;
    private static Path ROOT_PATH = Paths.get("").toAbsolutePath();
    private static FileWriter LOG_FILE_WRITER;
    private static ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    
    private static DateTimeFormatter DATE_FILE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    static {
        initFileWriter();
    }
    
    public static void info(String message, Object... args) {
        println(format("INFO", message, args));
    }
    
    public static void warn(String message, Object... args) {
        println(format("WARN", message, args));
    }
    
    public static void error(String message, Object... args) {
        println(format("ERROR", message, args));
    }
    
    public static <T> T exception(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        warn(sw.toString());
        return null;
    }
    
    public static void print(String message, Object... args) {
        write(String.format(message, args));
    }
    
    public static void println(String message, Object... args) {
        write(String.format(message + "\n", args));
    }
    
    // private
    
    private static String format(String type, String message, Object... args) {
        String prefix = "[" + TIME_FORMATTER.format(ZonedDateTime.now()) + "] [" + type + "] ";
        return String.format(prefix + message, args);
    }
    
    private static synchronized void write(String message) {
        try {
            System.out.print(message);
            if (LOG_FILE_WRITER != null) {
                LOG_FILE_WRITER.write(message);
                LOG_FILE_WRITER.flush();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static synchronized void initFileWriter() {
        // check : disabled
        if (!WRITE_TO_FILE_ENABLED)
            return;
        
        try {
            // previous log file writer not closed yet
            if (LOG_FILE_WRITER != null)
                LOG_FILE_WRITER.close();
            
            // create "logs" directory
            var root = Paths.get("").toAbsolutePath();
            var dir = root.resolve("logs");
            FileUtils.createDirectoryIfNotExists(dir);
            
            // rename latest file
            var fileLatest = dir.resolve("latest.log");
            if (Files.isRegularFile(fileLatest)) {
                // rename to date formatted file 
                var timeNow = ZonedDateTime.now();
                var dateFormattedNow = DATE_FILE_FORMATTER.format(timeNow);
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    var fileName = String.format("%s-%03d.log", dateFormattedNow, i);
                    var file = dir.resolve(fileName);
                    if (!Files.isRegularFile(file)) {
                        Files.move(fileLatest, file);
                        break;
                    }
                }
            }
            
            // init writer
            LOG_FILE_WRITER = new FileWriter(fileLatest.toFile());
            
            // schedule init writer for next day
            var timeNow = ZonedDateTime.now();
            var timeNextDay = timeNow
                    .plusDays(1)
                    .withHour(0).withMinute(0).withSecond(1);
            
            long seconds = Duration.between(timeNow, timeNextDay).toSeconds();
            SCHEDULED_EXECUTOR.schedule(Terminal::initFileWriter, seconds, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to init file writer.", ex);
        }
    }
}

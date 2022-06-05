package ua.i0xhex.vpartsextractor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import ua.i0xhex.vpartsextractor.model.Part;
import ua.i0xhex.vpartsextractor.util.FileUtils;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class VideoPartsExtractor {
    
    private Path ffmpegFile;
    private Path ffprobeFile;
    private Path inputVideoFile;
    private Path inputSubtitlesFile;
    private Path outputDirAudio;
    private Path outputDirScreenshots;
    
    private int audioTrack = 0;
    private int fadeIn = 200;
    private int fadeOut = 200;
    
    public void run(String[] args) {
        try {
            Terminal.info("Initialize..");
            parseArguments(args);
            FileUtils.createDirectoryIfNotExists(outputDirAudio);
            FileUtils.createDirectoryIfNotExists(outputDirScreenshots);
        
            var ffmpeg = new FFmpeg(ffmpegFile.toString());
            var ffprobe = new FFprobe(ffprobeFile.toString());
            var executor = new FFmpegExecutor(ffmpeg, ffprobe);
        
            Terminal.info("Parse subtitles...");
            var parts = new LinkedList<Part>();
            try (Stream<String> stream = Files.lines(inputSubtitlesFile)) {
                var id = new AtomicInteger();
                stream
                        .filter(line -> line.contains("-->"))
                        .forEach(line -> {
                            String[] arr = line.split(" --> ");
                            int start = parseDuration(arr[0]);
                            int end = parseDuration(arr[1]);
                            parts.add(new Part(id.getAndIncrement(), start, end));
                        });
            }
        
            Terminal.info("Selected %d parts.", parts.size());
            var counter = new AtomicInteger();
            
            Terminal.info("Extract audio parts...");
            counter.set(0);
            executeBulk(parts, chunk -> {
                var builder = buildAudioExtraction(chunk);
                //Terminal.info(String.join(" ", builder.build())); // print arguments built
                executor.createJob(builder).run();
                Terminal.info("Done %d/%d", counter.addAndGet(chunk.size()), parts.size());
            }, 50);
    
            Terminal.info("Extract screenshots...");
            counter.set(0);
            executeBulk(parts, chunk -> {
                var builder = buildScreenshotExtraction(chunk);
                //Terminal.info(String.join(" ", builder.build())); // print arguments built
                executor.createJob(builder).run();
                Terminal.info("Done %d/%d", counter.addAndGet(chunk.size()), parts.size());
            }, 50);
            
            Terminal.info("Done!");
            System.exit(0);
        } catch (Exception ex) {
            Terminal.exception(ex);
            System.exit(1);
        }
    }
    
    /**
     * Build FFmpegBuilder to extract audio from given parts.
     *
     * @param parts parts
     * @return builder
     */
    private FFmpegBuilder buildAudioExtraction(List<Part> parts) {
        var builder = new FFmpegBuilder()
                .setInput(inputVideoFile.toString());
        
        for (var part : parts) {
            int id = part.getId();
            int start = part.getStart();
            int end = part.getEnd();
    
            int sectionDuration = end - start;
            int fadeInDuration = Math.min(sectionDuration / 2, this.fadeIn);
            int fadeInStart = start;
            int fadeOutDuration = Math.min(sectionDuration / 2, this.fadeOut);
            int fadeOutStart = start + (sectionDuration - fadeOutDuration);
    
            var output = String.format("audio_%04d.mp3", id + 1);
            
            builder
                    .addOutput(outputDirAudio.resolve(output).toString())
                    .addExtraArgs("-map", "0:a:" + audioTrack)
                    .setAudioCodec("libmp3lame")
                    .addExtraArgs("-ss", start + "ms")
                    .addExtraArgs("-to", end + "ms")
                    .addExtraArgs("-af",
                            String.format(
                                    Locale.US, // To ensure '.' as decimal separator
                                    "\"afade=t=in:st=%dms:d=%dms,afade=t=out:st=%dms:d=%dms\"",
                                    fadeInStart, fadeInDuration, fadeOutStart, fadeOutDuration))
                    .done();
        }
        
        return builder;
    }
    
    /**
     * Build FFmpegBuilder to extract screenshots from given parts.
     * 
     * @param parts parts
     * @return builder
     */
    private FFmpegBuilder buildScreenshotExtraction(List<Part> parts) {
        var builder = new FFmpegBuilder();
        int i = 0;
        int size = parts.size();
        
        for (var part : parts) {
            int id = part.getId();
            int start = part.getStart();
            int end = part.getEnd();
            int timestamp = start + ((end - start) / 2);
            var output = String.format("screenshot_%04d.png", id + 1);
    
            builder
                    .addExtraArgs("-ss", timestamp + "ms")
                    .addOutput(outputDirScreenshots.resolve(output).toString())
                        .addExtraArgs("-map", i + ":v")
                        .addExtraArgs("-frames", "1")
                        .done();
    
            /*
             * Workaround to make multiple inputs with arguments in proper order.
             * 
             * By default library will generate args like that:
             * -ss 1 -ss 2 -ss 3 -i input.mp4 -i input.mp4 -i input.mp4
             * 
             * We need:
             * -ss 1 -i input.mp4 -ss 2 -i input.mp4 -ss 3 -i input.mp4
             */
            if (i + 1 < size) {
                builder.addExtraArgs("-i", inputVideoFile.toString());
            } else {
                builder.addInput(inputVideoFile.toString());
            }
            
            i++;
        }
        
        return builder;
    }
    
    /**
     * Split elements to chunks, where each has {@code elementsPerTime} size and execute them.
     * 
     * @param elements elements
     * @param consumer executor of elements chunk
     * @param elementsPerTime max elements per chunk
     * @param <E> element type
     */
    private <E> void executeBulk(List<E> elements, Consumer<List<E>> consumer, int elementsPerTime) {
        var elementsLeft = new LinkedList<E>(elements);
        while (!elementsLeft.isEmpty()) {
            var elementsChunk = new LinkedList<E>();
            for (int i = 0; i < elementsPerTime && !elementsLeft.isEmpty(); i++)
                elementsChunk.add(elementsLeft.remove());
            consumer.accept(elementsChunk);
        }
    }
    
    /**
     * Parse formatted duration:
     * <pre>HH:mm:ss.SSS</pre>
     * 
     * @param str input string
     * @return duration in millis
     */
    private int parseDuration(String str) {
        String[] arr = str.split(":");
        int hours = Integer.parseInt(arr[0]);
        int minutes = Integer.parseInt(arr[1]);
        arr = arr[2].split("\\.");
        int seconds = Integer.parseInt(arr[0]);
        int millis = Integer.parseInt(arr[1]);
        
        millis += Duration.ofSeconds(seconds).toMillis();
        millis += Duration.ofMinutes(minutes).toMillis();
        millis += Duration.ofHours(hours).toMillis();
        return millis;
    }
    
    /**
     * Parse arguments given.
     * 
     * @param args arguments
     */
    private void parseArguments(String[] args) {
        // read
        var argumentMap = new HashMap<String, String>();
        for (var argument : args) {
            try {
                var argumentArr = argument.split("=");
                argumentMap.put(argumentArr[0].toLowerCase(), argumentArr[1]);
            } catch (Exception ex) {
                // ignore
            }
        }
        
        // get
        try {
            var value = (String) null;
            
            value = argumentMap.get("ffmpeg");
            if (value == null)
                throw new IllegalStateException("Missing 'ffmpeg' argument.");
            ffmpegFile = Paths.get(value).toAbsolutePath();
    
            value = argumentMap.get("ffprobe");
            if (value == null)
                throw new IllegalStateException("Missing 'ffprobe' argument.");
            ffprobeFile = Paths.get(value).toAbsolutePath();
            
            value = argumentMap.get("input-video");
            if (value == null)
                throw new IllegalStateException("Missing 'input-video' argument.");
            inputVideoFile = Paths.get(value);
    
            value = argumentMap.get("input-subtitles");
            if (value == null)
                throw new IllegalStateException("Missing 'input-subtitles' argument.");
            inputSubtitlesFile = Paths.get(value);
    
            value = argumentMap.get("output-dir");
            if (value == null)
                throw new IllegalStateException("Missing 'output-dir' argument.");
            var outputDir = Paths.get(value);
            outputDirAudio = outputDir.resolve("audio");
            outputDirScreenshots = outputDir.resolve("screenshots");
            
            value = argumentMap.get("audiotrack");
            if (value != null)
                audioTrack = Integer.parseInt(value);
            
            value = argumentMap.get("fadein");
            if (value != null)
                fadeIn = Integer.parseInt(value);
            
            value = argumentMap.get("fadeout");
            if (value != null)
                fadeOut = Integer.parseInt(value);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse arguments.", ex);
        }
    }
    
    // static
    
    public static void main(String[] args) {
        new VideoPartsExtractor().run(args);
    }
}

package ua.i0xhex.vpartsextractor.util;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    
    /**
     * Create file if not exists
     *
     * @param file file to create
     * @return same given file (for inlining)
     */
    public static Path createFileIfNotExists(Path file) {
        try {
            if (!Files.isRegularFile(file))
                Files.createFile(file);
            return file;
        } catch (Exception ex) {
            String path = getPathName(file);
            throw new RuntimeException("Failed to create file: " + path, ex);
        }
    }
    
    /**
     * Create directory if not exists
     *
     * @param dir directory to create
     * @return same given directory (for inlining)
     */
    public static Path createDirectoryIfNotExists(Path dir) {
        try {
            if (!Files.isDirectory(dir))
                Files.createDirectories(dir);
            return dir;
        } catch (Exception ex) {
            String path = getPathName(dir);
            throw new RuntimeException("Failed to create directory: " + path, ex);
        }
    }
    
    /**
     * Create symbolic link to a directory or file.
     *
     * @param link path to link
     * @param target path to target
     * @param replace delete old file if exists
     */
    public static Path createSymbolicLink(Path link, Path target, boolean replace) {
        try {
            link = link.toAbsolutePath();
            target = link.toAbsolutePath();
            
            // delete existing file or directory
            if (replace) {
                if (Files.isRegularFile(link)) {
                    deleteFile(link);
                } else if (Files.isDirectory(link)) {
                    deleteDirectory(link);
                }
            }
            
            // create
            return Files.createSymbolicLink(link, target);
        } catch (Exception ex) {
            String path = getPathName(link);
            throw new RuntimeException("Failed to create symbolic link: " + path, ex);
        }
    }
    
    /**
     * Delete file
     *
     * @param file file to delete
     */
    public static void deleteFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (Exception ex) {
            String path = getPathName(file);
            throw new RuntimeException("Failed to delete file: " + path, ex);
        }
    }
    
    /**
     * Delete directory recursively.
     *
     * @param dir directory to delete
     */
    public static void deleteDirectory(Path dir) {
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            String path = getPathName(dir);
            throw new RuntimeException("Failed to delete directory: " + path, ex);
        }
    }
    
    /**
     * Copy directory recursively
     *
     * @param source source directory
     * @param target destination directory
     * @param options options
     */
    public static void copyDirectory(Path source, Path target, CopyOption... options) {
        try {
            Files.walkFileTree(source, EnumSet.of(FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(target.resolve(source.relativize(dir)));
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(source.relativize(file)), options);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            String pathSource = getPathName(source);
            String pathTarget = getPathName(target);
            throw new RuntimeException(
                    String.format("Failed to copy directory '%s' to '%s'",
                            pathSource, pathTarget), ex);
        }
    }
    
    /**
     * Copy file
     *
     * @param source source file
     * @param target target file
     * @param options options
     */
    public static void copyFile(Path source, Path target, CopyOption... options) {
        try {
            Files.copy(source, target, options);
        } catch (Exception ex) {
            String pathSource = getPathName(source);
            String pathTarget = getPathName(target);
            throw new RuntimeException(
                    String.format("Failed to copy file '%s' to '%s'",
                            pathSource, pathTarget), ex);
        }
    }
    
    /**
     * Move directory recursively
     *
     * @param source source directory
     * @param target destination directory
     * @param options options
     */
    public static void moveDirectory(Path source, Path target, CopyOption... options) {
        try {
            Files.walkFileTree(source, EnumSet.of(FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(target.resolve(source.relativize(dir)));
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.move(file, target.resolve(source.relativize(file)), options);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            String pathSource = getPathName(source);
            String pathTarget = getPathName(target);
            throw new RuntimeException(
                    String.format("Failed to copy directory '%s' to '%s'",
                            pathSource, pathTarget), ex);
        }
    }
    
    /**
     * Move file
     *
     * @param source source file
     * @param target target file
     * @param options options
     */
    public static void moveFile(Path source, Path target, CopyOption... options) {
        try {
            Files.move(source, target, options);
        } catch (Exception ex) {
            String pathSource = getPathName(source);
            String pathTarget = getPathName(target);
            throw new RuntimeException(
                    String.format("Failed to copy file '%s' to '%s'",
                            pathSource, pathTarget), ex);
        }
    }
    
    /**
     * Check if given directory is empty
     *
     * @param dir directory to check
     * @return true if empty, false otherwise
     */
    public static boolean isDirectoryEmpty(Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            return !stream.iterator().hasNext();
        } catch (Exception ex) {
            String path = getPathName(dir);
            throw new RuntimeException("Failed to check if directory is empty: " + path, ex);
        }
    }
    
    /**
     * Get a list of files in given directory
     *
     * @param dir directory
     * @return list of files
     */
    public static List<Path> getFiles(Path dir) {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream.collect(Collectors.toCollection(LinkedList::new));
        } catch (Exception ex) {
            String path = getPathName(dir);
            throw new RuntimeException("Failed to get file list in directory: " + path, ex);
        }
    }
    
    /**
     * Get a list of files in given directory using filter
     *
     * @param dir directory
     * @param filter filter
     * @return list of files
     */
    public static List<Path> getFiles(Path dir, Predicate<Path> filter) {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(filter)
                    .collect(Collectors.toCollection(LinkedList::new));
        } catch (Exception ex) {
            String path = getPathName(dir);
            throw new RuntimeException("Failed to get file list in directory: " + path, ex);
        }
    }
    
    /**
     * Returns path name replacing Windows path separator to default '/' one.
     *
     * @param path path (file or directory)
     * @return path name
     */
    public static String getPathName(Path path) {
        return path.toString().replace('\\', '/');
    }
}


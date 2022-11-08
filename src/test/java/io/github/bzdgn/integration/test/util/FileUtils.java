package io.github.bzdgn.integration.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public static void copyDirectoryTo(String src, String dst) throws IOException {
        File srcFile = new File(src);
        File dstFile = new File(dst);

        copyDirectoryCompatibityMode(srcFile, dstFile);
    }

    private static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : sourceDirectory.list()) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }

    private static void copyFile(File sourceFile, File destinationFile)
            throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
                OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    public static void deletePath(String path) throws IOException {
        File directoryToClean = new File(path);
        if (directoryToClean.exists()) {
            for (File subFile : directoryToClean.listFiles()) {
                String subFilePath = subFile.getPath();
                Files.walk(Paths.get(subFilePath))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }

    public static String readFileContent(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static byte[] readFileContentInBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public static List<String> getFolderFileNames(String path) {
        return Arrays.stream(new File(path).listFiles())
                .map(f -> f.getName())
                .collect(Collectors.toList());
    }

    public static List<String> getFolderFilePaths(String path) {
        return Arrays.stream(new File(path).listFiles())
                .map(f -> f.getPath())
                .collect(Collectors.toList());
    }

    public static File[] getFolderFiles(String path) {
        return new File(path).listFiles();
    }

    public static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return null;
        }

        return fileName.substring(lastIndexOf + 1).toLowerCase();
    }

    public static List<String> getFolderSubdirectories(String path) {
        return Arrays.stream(new File(path).listFiles())
                .filter(f -> f.isDirectory())
                .map(f -> f.getName())
                .collect(Collectors.toList());
    }

}

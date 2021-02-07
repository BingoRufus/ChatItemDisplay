package com.github.bingorufus.chatitemdisplay.util.updater;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Scanner;

public class UpdateDownloader {
    String newFileName;


    public File download(File newFile) throws IOException {
        FileOutputStream downloadPath = new FileOutputStream(newFile);
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(
                new URL("https://gist.githubusercontent.com/BingoRufus/a3714f3e6afb400122ec5ffefe6c430c/raw/?version="
                        + Math.random()).openStream()));
        Scanner scanner = new Scanner(inputStream);
        String link = scanner.next();

        BufferedInputStream in = new BufferedInputStream(new URL(link).openStream());

        byte[] dataBuffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            downloadPath.write(dataBuffer, 0, bytesRead);

        }
        newFileName = newFile.getName();
        downloadPath.close();
        return newFile;


    }

    public void deletePlugin(Object plugin) {
        try {
            File f = Paths.get((plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()))
                    .toFile();
            if (f.getName().equals(newFileName))
                return;
            f.delete();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}

package me.bingorufus.chatitemdisplay.util.updater;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Scanner;

public class UpdateDownloader {
	String version;

	public UpdateDownloader(String ver) {
		version = ver;
	}

	public String download(FileOutputStream downloadPath) {

		try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(
				new URL("https://gist.githubusercontent.com/BingoRufus/a3714f3e6afb400122ec5ffefe6c430c/raw/?version="
						+ Math.random()).openStream()));
				Scanner scanner = new Scanner(inputStream)) {
			String link = scanner.next();

			try (BufferedInputStream in = new BufferedInputStream(new URL(link).openStream())) {

				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					downloadPath.write(dataBuffer, 0, bytesRead);

				}

				downloadPath.close();
				return null;

			}

		} catch (IOException e) {
			e.printStackTrace();
			return "Unable to download the newest version of ChatItemDisplay (" + e.getMessage() + ")";
		}
	}

	public void deletePlugin(Object plugin) {
		Paths.get((plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile().replaceAll("%20",
				" "))).toFile().delete();
	}

}

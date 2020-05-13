package me.BingoRufus.ChatDisplay.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Scanner;

import org.bukkit.Bukkit;

import me.BingoRufus.ChatDisplay.Main;

public class UpdateDownloader {
	Main main;
	String version;

	public UpdateDownloader(Main m, String ver) {
		main = m;
		version = ver;
	}

	public void download() {
		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
			try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(new URL(
					"https://gist.githubusercontent.com/BingoRufus/a3714f3e6afb400122ec5ffefe6c430c/raw/?version="
							+ Math.random()).openStream()));
					Scanner scanner = new Scanner(inputStream)) {
				String link = scanner.next();

				try (BufferedInputStream in = new BufferedInputStream(new URL(link).openStream())) {
					FileOutputStream download = new FileOutputStream("plugins/ChatItemDisplay " + version + ".jar");
					byte dataBuffer[] = new byte[1024];
					int bytesRead;
					while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
						download.write(dataBuffer, 0, bytesRead);

					}
					Bukkit.getLogger().info(
							"The newest version of ChatItemDisplay has been downloaded automatically, it will be loaded upon startup");
					download.close();

					Paths.get((Bukkit.getPluginManager().getPlugin("ChatItemDisplay").getClass().getProtectionDomain()
							.getCodeSource().getLocation().getFile().replaceAll("%20", " "))).toFile().delete();

				}

			} catch (IOException e) {

				e.printStackTrace();
			}
		});
	}
}

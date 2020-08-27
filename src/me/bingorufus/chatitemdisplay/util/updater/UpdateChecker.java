package me.bingorufus.chatitemdisplay.util.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

public class UpdateChecker {

	private Integer id;

	public UpdateChecker(Integer i) {
		this.id = i;
	}

	public String getLatestVersion(Consumer<String> version) {

			try (InputStream inputStream = new URL(
					"https://api.spigotmc.org/legacy/update.php?resource=" + this.id + "?" + UUID.randomUUID())
					.openStream(); Scanner scanner = new Scanner(inputStream)) {
				if (scanner.hasNext()) {
					version.accept(scanner.next());
				}
			return null;

			} catch (IOException e) {
				return("Unable to connect to Spigot to check for updates " + e.getMessage());
			}



	}
}

package com.bingorufus.common.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

public class UpdateChecker {

    private final int id;

    public UpdateChecker(int i) {
        this.id = i;
    }

    public void getLatestVersion(Consumer<String> version) throws IOException {

        InputStream inputStream = new URL(
                "https://api.spigotmc.org/legacy/update.php?resource=" + this.id + "?" + UUID.randomUUID())
                .openStream();
        Scanner scanner = new Scanner(inputStream);
        if (scanner.hasNext()) {
            version.accept(scanner.next());
        }


    }
}

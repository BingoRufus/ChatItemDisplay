package io.github.bingorufus.chatitemdisplay.util.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

public class UpdateChecker {

    private final Integer id;

    public UpdateChecker(Integer i) {
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

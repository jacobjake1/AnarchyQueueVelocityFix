package org.zeroBzeroT.anarchyqueue;

import com.moandjiezana.toml.Toml;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public static String serverMain = null;
    public static String serverQueue = null;
    public static String name = null;
    public static int maxPlayers = 0;
    public static String messagePosition = null;
    public static String messageConnecting = null;
    public static String messageFull = null;
    public static String messageOffline = null;
    public static boolean kick = true;
    public static int waitOnKick = 0;
    public static boolean sendTitle = true;
    public static String titleMessage = null;
    public static String subtitleMessage = null;

    static void loadConfig(Path path) throws IOException {
        File file = new File(path.toFile(), "config.toml");
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) throw new IllegalStateException("unable to create data dir!");
        }

        if (!file.exists()) {
            try (InputStream input = Config.class.getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    if (!file.createNewFile()) throw new IllegalStateException("unable to load default config!");
                }
            } catch (IOException exception) {
                Main.getInstance().log.warn(exception.getMessage());
                return;
            }
        }

        Toml toml = new Toml().read(file);
        serverMain = toml.getString("server-main", "main");
        serverQueue = toml.getString("server-queue", "queue");
        name = toml.getString("name", "0b0t");
        maxPlayers = toml.getLong("max-players", 420L).intValue();
        messagePosition = toml.getString("message-position", "&ePosition in queue: ");
        messageConnecting = toml.getString("message-connecting", "&aConnecting to the server...");
        messageFull = toml.getString("message-full", "&cServer is currently full!");
        messageOffline = toml.getString("message-offline", "&6&lWaiting for server to come online");
        kick = toml.getBoolean("kick", true);
        waitOnKick = toml.getLong("wait-on-kick", 16L).intValue();
        sendTitle = toml.getBoolean("send-title", true);
        titleMessage = toml.getString("title-message", "&6Queue Status");
        subtitleMessage = toml.getString("subtitle-message", "&ePosition: %position%/%total%");
    }

    public static Component parseColor(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}

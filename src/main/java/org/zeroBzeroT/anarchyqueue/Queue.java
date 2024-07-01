package org.zeroBzeroT.anarchyqueue;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public class Queue {
    private final Logger log;
    private final ProxyServer proxyServer;
    private final List<Player> queuedPlayers = new CopyOnWriteArrayList<>();

    public Queue(ProxyServer proxyServer) {
        this.log = Main.getInstance().log;
        this.proxyServer = proxyServer;

        proxyServer.getScheduler()
                .buildTask(Main.getInstance(), this::process)
                .delay(Duration.ofSeconds(1))
                .repeat(Duration.ofSeconds(1))
                .schedule();
    }

    @Subscribe
    public void onServerConnectedEvent(ServerConnectedEvent e) {
        if (e.getServer().getServerInfo().getName().equals(Config.serverQueue)) {
            log.info("queuing " + e.getPlayer().getUsername() + " (" + e.getPlayer().getUniqueId().toString() + ")");
            queuedPlayers.add(e.getPlayer());
        } else if (e.getServer().getServerInfo().getName().equals(Config.serverMain)) {
            queuedPlayers.remove(e.getPlayer());
        }
    }

    public void process() {
        final RegisteredServer serverQueue;
        try {
            serverQueue = getServer(Config.serverQueue);
        } catch (ServerNotReachableException e) {
            log.warn(e.getMessage());
            return;
        }

        queuedPlayers.removeIf(player -> !serverQueue.getPlayersConnected().contains(player));

        if (queuedPlayers.size() == 0) return;

        RegisteredServer serverMain = null;
        boolean isServerDown = true;
        try {
            serverMain = getServer(Config.serverMain);
            isServerDown = false;
        } catch (ServerNotReachableException e) {
            // Server is down, isServerDown remains true
        }

        final boolean full = !isServerDown && serverMain.getPlayersConnected().size() >= Config.maxPlayers;

        // Send title messages constantly
        sendInfos(serverQueue, full, isServerDown);

        // Send chat messages every 10 seconds
        if (Instant.now().getEpochSecond() % 10 == 0) {
            sendChatInfos(serverQueue, full, isServerDown);
        }

        if (full || isServerDown) return;

        UUID uuid = queuedPlayers.get(0).getUniqueId();
        log.info("processing " + uuid.toString());

        final RegisteredServer finalServerMain = serverMain;
        serverQueue.getPlayersConnected().stream()
                .filter(p -> p.getUniqueId().equals(uuid))
                .findAny().ifPresent(p -> {
                    clearChat(p);
                    p.sendMessage(Identity.nil(), Config.parseColor(Config.messageConnecting));
                    try {
                        if (p.createConnectionRequest(finalServerMain).connect().get().isSuccessful()) queuedPlayers.remove(0);
                    } catch (InterruptedException | ExecutionException e) {
                        log.error("unable to connect " + p.getUsername() + "(" + p.getUniqueId().toString() + ") to " + Config.serverMain + ": " + e.getMessage());
                    }
                });
    }

    private void sendInfos(RegisteredServer serverQueue, boolean full, boolean isServerDown) {
        for (int i = 0; i < queuedPlayers.size(); i++) {
            Player player = queuedPlayers.get(i);
            if (isServerDown) {
                sendTitle(player, -1, -1, Config.messageOffline);
            } else if (full) {
                sendTitle(player, -1, -1, Config.messageFull);
            } else {
                sendTitle(player, i + 1, queuedPlayers.size(), null);
            }
        }
    }

    private void sendChatInfos(RegisteredServer serverQueue, boolean full, boolean isServerDown) {
        for (int i = 0; i < queuedPlayers.size(); i++) {
            Player player = queuedPlayers.get(i);
            clearChat(player);
            if (isServerDown) {
                player.sendMessage(Identity.nil(), Config.parseColor(Config.messageOffline));
            } else if (full) {
                player.sendMessage(Identity.nil(), Config.parseColor(Config.messageFull));
            } else {
                String message = Config.messagePosition + (i + 1) + "/" + queuedPlayers.size();
                player.sendMessage(Identity.nil(), Config.parseColor(message));
            }
        }
    }

    private void sendTitle(Player player, int position, int total, String overrideSubtitle) {
        Component title = Config.parseColor(Config.titleMessage);
        Component subtitle;
        if (overrideSubtitle != null) {
            subtitle = Config.parseColor(overrideSubtitle);
        } else {
            subtitle = Config.parseColor(Config.subtitleMessage
                    .replace("%position%", String.valueOf(position))
                    .replace("%total%", String.valueOf(total)));
        }

        player.showTitle(Title.title(
                title,
                subtitle,
                Title.Times.of(Duration.ZERO, Duration.ofSeconds(20), Duration.ofSeconds(1))
        ));
    }

    private void clearChat(Player player) {
        for (int i = 0; i < 100; i++) {
            player.sendMessage(Component.empty());
        }
    }

    private RegisteredServer getServer(String name) throws ServerNotReachableException {
        Optional<RegisteredServer> serverOpt = proxyServer.getServer(name);
        if (!serverOpt.isPresent()) {
            throw new ServerNotReachableException("Server " + name + " is not configured!");
        }

        final RegisteredServer server = serverOpt.get();
        try {
            server.ping().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerNotReachableException("Server " + name + " is not reachable: " + e.getMessage());
        }

        return server;
    }

    private static class ServerNotReachableException extends Exception {
        public ServerNotReachableException(String message) {
            super(message);
        }
    }

    public List<Player> getQueuedPlayers() {
        return queuedPlayers;
    }
}

package org.zeroBzeroT.anarchyqueue;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public class SlotsCommand implements SimpleCommand {

    private final Queue queue;

    public SlotsCommand(Queue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(Component.text("This command can only be executed by players."));
            return;
        }

        Player player = (Player) invocation.source();
        int queueSize = queue.getQueuedPlayers().size();
        int maxPlayers = Config.maxPlayers;

        player.sendMessage(Component.text("Queue size: " + queueSize));
        player.sendMessage(Component.text("Max players: " + maxPlayers));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true; // Allow all players to use this command
    }
}

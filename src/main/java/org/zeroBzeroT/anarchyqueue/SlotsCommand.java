package org.zeroBzeroT.anarchyqueue;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * SlotsCommand
 */
public class SlotsCommand extends Command {

    public SlotsCommand() {
        super("maxplayers");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender != ProxyServer.getInstance().getConsole()) {
            sender.sendMessage(new TextComponent("§cUnknown command§r"));
            return;
        } else if (args.length != 1) {
            sender.sendMessage(new TextComponent("§3Current maximum player capacity is " + Config.maxPlayers + ".§r"));
            return;
        }

        int maxPlayers = Integer.parseInt(args[0]);
        sender.sendMessage(new TextComponent("§3Changed maximum player capacity to " + maxPlayers + ".§r"));
        Config.maxPlayers = maxPlayers;
    }
}
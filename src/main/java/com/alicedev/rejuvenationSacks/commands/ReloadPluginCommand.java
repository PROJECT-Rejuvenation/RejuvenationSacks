package com.alicedev.rejuvenationSacks.commands;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import com.alicedev.rejuvenationSacks.utils.MiniMessageUtils;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadPluginCommand implements CommandExecutor {

    private RejuvenationSacks plugin;

    public ReloadPluginCommand(RejuvenationSacks plugin) {
        this.plugin = plugin;
    }

    /**
     * Sends a formatted message to the command sender.
     *
     * @param sender  Command sender
     * @param message Message to send
     */
    private void commandFeedback(CommandSender sender, String message) {
        sender.sendMessage(MiniMessageUtils.mmFormat(message)); // Use GREEN for success feedback
    }

    /**
     * Executes the /prsacks reload command.
     *
     * @param sender Command sender
     * @param cmd    Command instance
     * @param label  Command label
     * @param args   Command arguments
     * @return true if the command executed successfully, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("rejuvenationsacks.reload")) {
            commandFeedback(sender, "<#ff7744>You do not have permissions to run this command.");
            return true;
        }

        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            commandFeedback(sender, "<#ff7744>Usage: /prsacks reload");
            return true;
        }

        commandFeedback(sender, "<#aaffbb>Reloading plugin...");
        plugin.loadPlugin();
        commandFeedback(sender, "<#aaffbb>Plugin reloaded successfully.");
        return true;
    }


}

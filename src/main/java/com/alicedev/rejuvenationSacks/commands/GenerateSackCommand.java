package com.alicedev.rejuvenationSacks.commands;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import com.alicedev.rejuvenationSacks.data.SackDataManager;
import com.alicedev.rejuvenationSacks.data.SackTemplate;
import com.alicedev.rejuvenationSacks.utils.MiniMessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GenerateSackCommand implements CommandExecutor {

    private RejuvenationSacks plugin;
    private SackDataManager sackData;

    public GenerateSackCommand(RejuvenationSacks plugin) {
        this.plugin = plugin;
        this.sackData = plugin.getSackDataManager();
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
     * Executes the /gensack command.
     *
     * @param sender Command sender
     * @param cmd    Command instance
     * @param label  Command label
     * @param args   Command arguments
     * @return true if the command executed successfully, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("rejuvenationsacks.generate")) {
            commandFeedback(sender, "<#ff7744>You do not have permissions to run this command.");
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            commandFeedback(sender, "<#ff7744>Usage: /gensack <template_id> [target_player]");
            return true;
        }

        if (args.length == 1 && !(sender instanceof Player)){
            commandFeedback(sender, "<#ff7744>Usage for console: /gensack <template_id> <target_player>");
            return true;
        }

        commandFeedback(sender, "<#aaffbb>Attempting to generate sack...");
        Player target = null;
        if(sender instanceof Player) target = (Player) sender;
        if(args.length == 2){
            if(Bukkit.getPlayer(args[1]) != null) target = Bukkit.getPlayer(args[1]);
        }

        SackTemplate template = null;
        try {
            template = sackData.loadFromConfig(args[0]);
        } catch (Exception e) {

            commandFeedback(sender, "<#aaffbb>Template identifier not found.");
            return true;
        }
        try {
            target.give(SackTemplate.createSack(plugin,template));
        } catch (Exception e) {
            e.printStackTrace();
        }

        commandFeedback(sender, "<#aaffbb>Sack generated successfully.");
        return true;
    }

}

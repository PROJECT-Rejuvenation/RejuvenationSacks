package com.alicedev.rejuvenationSacks.commands.completions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateSackCompletions implements TabCompleter {

    private final String[] OPTIONS;

    public GenerateSackCompletions(String[] options) {
        this.OPTIONS = options;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String [] strings) {
        List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(strings[0], Arrays.asList(OPTIONS), completions);
        return completions;
    }
}

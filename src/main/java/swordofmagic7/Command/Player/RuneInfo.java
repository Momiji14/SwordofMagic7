package swordofmagic7.Command.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import swordofmagic7.Command.SomCommand;
import swordofmagic7.Command.SomTabComplete;
import swordofmagic7.Data.PlayerData;
import swordofmagic7.Item.RuneParameter;

import java.util.ArrayList;
import java.util.List;

import static swordofmagic7.Data.DataBase.RuneList;
import static swordofmagic7.Data.DataBase.getRuneParameter;
import static swordofmagic7.Function.sendMessage;

public class RuneInfo implements SomCommand, SomTabComplete {
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("Amount")) {
                sendMessage(player, "§aRuneListSize: " + RuneList.size());
            } else if (RuneList.containsKey(args[0])) {
                RuneParameter rune = getRuneParameter(args[0]);
                try {
                    if (args[1] != null) rune.Level = Math.min(Math.max(Integer.parseInt(args[1]), 1), PlayerData.MaxLevel);
                    if (args[2] != null) rune.Quality = Math.min(Math.max(Double.parseDouble(args[2])/100f, 0), 200);
                } catch (Exception ignore) {}
                ItemStack itemStack = rune.viewRune(playerData.ViewFormat(), rune.isLoreHide);
                List<String> list = new ArrayList<>();
                list.add(itemStack.getItemMeta().getDisplayName());
                list.addAll(itemStack.getLore());
                sendMessage(player, list);
            } else player.sendMessage("§a存在しない§eルーン§aです");
        } else {
            player.sendMessage("§e/runeInfo <RuneID> [<Level>] [<0~200>]");
        }
        return true;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> PlayerTabComplete(Player player, PlayerData playerData, Command command, String[] args) {
        return null;
    }

    @Override
    public List<String> TabComplete(CommandSender sender, Command command, String[] args) {
        List<String> complete = new ArrayList<>();
        if (args.length == 1) for (RuneParameter rune : RuneList.values()) {
            if (!rune.isHide) complete.add(rune.Id);
        }
        return complete;
    }
}

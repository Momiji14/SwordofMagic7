package swordofmagic7.Command.Developer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import swordofmagic7.Command.SomCommand;
import swordofmagic7.Data.PlayerData;

import static swordofmagic7.Data.PlayerData.playerData;

public class SetNick implements SomCommand {
    @Override
    public boolean PlayerCommand(Player player, PlayerData playerData, String[] args) {
        return false;
    }

    @Override
    public boolean Command(CommandSender sender, String[] args) {
        Player target = null;
        if (sender instanceof Player player) target = player;
        if (args.length == 2) {
            target = Bukkit.getPlayer(args[1]);
        }
        if (target != null && target.isOnline()) {
            PlayerData targetData = playerData(target);
            targetData.Nick = args[0];
            sender.sendMessage(targetData.Nick);
            targetData.Status.StatusUpdate();
        } else {
            sender.sendMessage("§c無効なプレイヤーです");
        }
        return true;
    }
}

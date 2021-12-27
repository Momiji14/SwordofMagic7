package swordofmagic7;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class Function {

    public static void Log(String str) {
        Log(str, false);
    }
    public static void Log(String str, boolean stackTrace) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) player.sendMessage(str);
        }
        if (stackTrace) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String colored(String str, String def) {
        if (str.contains("&")) {
            return str.replace("&", "§");
        } else {
            return def.replace("&", "§") + str;
        }
    }

    public static String unColored(String string) {
        return string
                .replace("§0", "")
                .replace("§1", "")
                .replace("§2", "")
                .replace("§3", "")
                .replace("§4", "")
                .replace("§5", "")
                .replace("§6", "")
                .replace("§7", "")
                .replace("§8", "")
                .replace("§9", "")
                .replace("§a", "")
                .replace("§b", "")
                .replace("§c", "")
                .replace("§d", "")
                .replace("§e", "")
                .replace("§f", "")
                .replace("§l", "")
                .replace("§m", "")
                .replace("§n", "")
                .replace("§k", "")
                .replace("§r", "")
                ;

    }
    public static String decoText(String str) {
        int flame = 6 - Math.round(str.length() / 2f);
        StringBuilder deco = new StringBuilder("===");
        deco.append("=".repeat(Math.max(0, flame)));
        return "§6§l§m" + deco + "§6§l[[|§r " + colored(str, "§e§l") + "§r §6§l|]]§m" + deco;
    }

    public static List<String> loreText(List<String> list) {
        List<String> lore = new ArrayList<>();
        for (String str : list) {
            lore.add("§a§l" + str);
        }
        return lore;
    }

    public static String decoLore(String str) {
        return "§7・" + colored(str, "§e§l") + "§7: §a§l";
    }

    public static List<String> removeChar() {
        List<String> list = new ArrayList<>();
        list.add("=");
        list.add("|");
        list.add(" ");
        list.add("[");
        list.add("]");
        return list;
    }
    public static String unDecoText(String str) {
        if (str == null) return "";
        for (String rev : removeChar()) {
            if (str.contains(rev)) str = str.replace(rev, "");
        }
        return unColored(str);
    }

    public static void BroadCast(String str) {
        for (Player player : PlayerList.get()) {
            player.sendMessage(str);
        }
    }

    public static Inventory decoInv(String name, int size) {
        return Bukkit.createInventory(null, size*9, name);
    }

    public static boolean inAir(Player player) {
        return !player.isOnGround();
    }

    public static boolean isAlive(Player player) {
        return player.getGameMode() == GameMode.SURVIVAL;
    }

    public static boolean equalInv(InventoryView view, String name) {
        return view.getTitle().equalsIgnoreCase(name);
    }

    public static boolean equalItem(ItemStack item, ItemStack item2) {
        return unColored(item.getItemMeta().getDisplayName()).equals(unColored(item2.getItemMeta().getDisplayName()));
    }

    public static boolean isZero(int a) {
        return a != 0;
    }

    public static boolean isZero(double a) {
        return a != 0;
    }

}

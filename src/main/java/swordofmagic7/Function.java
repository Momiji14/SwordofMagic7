package swordofmagic7;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import swordofmagic7.Effect.EffectManager;
import swordofmagic7.Effect.EffectType;
import swordofmagic7.Equipment.EquipmentSlot;
import swordofmagic7.Mob.EnemyData;
import swordofmagic7.Mob.MobManager;
import swordofmagic7.MultiThread.MultiThread;
import swordofmagic7.Pet.PetManager;
import swordofmagic7.Pet.PetParameter;
import swordofmagic7.RayTrace.RayTrace;
import swordofmagic7.Sound.SoundList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static swordofmagic7.Data.DataBase.*;
import static swordofmagic7.Data.PlayerData.playerData;
import static swordofmagic7.Sound.CustomSound.playSound;
import static swordofmagic7.System.plugin;
import static swordofmagic7.System.random;

public final class Function {

    public static void Log(String str) {
        Log(str, false);
    }
    public static void Log(String str, boolean stackTrace) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("som7.log")) player.sendMessage(str);
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

    public static boolean ignoreEntity(Entity entity) {
        return (entity instanceof ItemFrame || entity.getType() == EntityType.ARMOR_STAND || entity instanceof Minecart || CitizensAPI.getNPCRegistry().isNPC(entity));
    }

    public static String decoLore(String str) {
        return "§7・" + colored(str, "§e§l") + "§7: §a§l";
    }

    public static String decoBossBar(String str) {
        return colored(str, "§e§l") + "§7: §a§l";
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
        BroadCast(str, null);
    }

    public static void BroadCast(String str, SoundList sound) {
        for (Player player : PlayerList.get()) {
            player.sendMessage(str);
            if (sound != null) playSound(player, sound);
        }
    }

    public static Inventory decoInv(String name, int size) {
        return Bukkit.createInventory(null, size*9, name);
    }

    public static Inventory decoAnvil(String name) {
        Inventory inv = Bukkit.createInventory(null, 9, name);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, NoneFlame);
        }
        for (int i : AnvilUISlot) {
            inv.setItem(i, AirItem);
        }
        inv.setItem(8, AnvilUIFlame);
        return inv;
    }

    public static Vector getRightDirection(Location location) {
        Vector direction = location.getDirection().normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }

    public static Vector getLeftDirection(Location location) {
        Vector direction = location.getDirection().normalize();
        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
    }

    public static boolean inAir(Player player) {
        return !player.isOnGround();
    }

    public static boolean isAlive(Player player) {
        return player.isOnline() && player.getGameMode() == GameMode.SURVIVAL;
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

    public static final Vector VectorUp = new Vector(0, 1, 0);
    public static final Vector VectorDown = new Vector(0, -1, 0);

    public static Location playerHandLocation(Player player) {
        Location location = player.getEyeLocation().clone();
        location.setYaw(location.getYaw()+90);
        location.setPitch(60);
        location = location.add(location.getDirection());
        location.setDirection(player.getLocation().getDirection());
        return location;
    }

    public static Location playerEyeLocation(Player player, double length) {
        return RayTrace.rayLocationBlock(player.getEyeLocation(), length, true).HitPosition;
    }

    public static void CloseInventory(Player player) {
        if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING)  {
            MultiThread.TaskRunSynchronized(player::closeInventory, "CloseInventory: " + player.getName());
        }
    }

    public static Object GetRandom(Set<?> list) {
        if (list.size() > 0) {
            int a = random.nextInt(list.size());
            int i = 0;
            for (Object obj : list) {
                if (i == a) return obj;
                i++;
            }
        }
        return null;
    }

    public static void Push(LivingEntity entity, Vector vector) {
        EffectManager manager = EffectManager.getEffectManager(entity);
        if (!manager.hasEffect(EffectType.PainBarrier)) {
            entity.setVelocity(vector);
        }
    }

    public static Set<LivingEntity> NearLivingEntity(Location location, double radius, Predicate<LivingEntity> predicate) {
        Set<LivingEntity> entities = new HashSet<>();
        for (Player player : PlayerList.PlayerList) {
            if (location.distance(player.getLocation()) < radius && predicate.test(player)) {
                entities.add(player);
            }
        }
        try {
            for (EnemyData enemyData : MobManager.getEnemyList()) {
                if (enemyData.entity != null && location.distance(enemyData.entity.getLocation()) < radius && predicate.test(enemyData.entity)) {
                    entities.add(enemyData.entity);
                }
            }
        } catch (Exception ignored) {}
        for (PetParameter petParameter : PetManager.PetSummonedList.values()) {
            if (petParameter.entity != null && location.distance(petParameter.entity.getLocation()) < radius && predicate.test(petParameter.entity)) {
                entities.add(petParameter.entity);
            }
        }
        return entities;
    }

    public static List<LivingEntity> NearLivingEntityAtList(Location location, double radius, Predicate<LivingEntity> predicate) {
        return new ArrayList<>(NearLivingEntity(location, radius, predicate));
    }

    public static boolean isHoldFishingRod(Player player) {
        return playerData(player).Equipment.getEquip(EquipmentSlot.MainHand).Icon == Material.FISHING_ROD;
    }

    public static String decoDoubleToString(double i, String format) {
        String text = String.format(format, i);
        if (i >= 0) return text;
        else return "-" + text;
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(message);
    }

    public static void sendMessage(Player player, List<String> message) {
        for (String str : message) {
            player.sendMessage(str);
        }
    }

    public static void sendMessage(Player player, String message, SoundList sound) {
        sendMessage(player, message);
        playSound(player, sound);
    }

    public static void sendMessage(Player player, List<String> message, SoundList sound) {
        sendMessage(player, message);
        playSound(player, sound);
    }

    public static void teleportServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}

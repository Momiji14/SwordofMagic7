package swordofmagic7.Shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import swordofmagic7.Data.PlayerData;
import swordofmagic7.Data.Type.ViewInventoryType;
import swordofmagic7.Item.ItemParameter;
import swordofmagic7.Item.ItemStackData;
import swordofmagic7.Item.RuneParameter;
import swordofmagic7.Sound.SoundList;

import java.util.ArrayList;
import java.util.List;

import static swordofmagic7.Data.DataBase.*;
import static swordofmagic7.Function.*;
import static swordofmagic7.Menu.Menu.ignoreSlot;
import static swordofmagic7.Sound.CustomSound.playSound;

public class RuneShop {
    public static final String RuneShopMenuDisplay = "§lルーン職人";
    private static final String RuneCrashDisplay = "§lルーン粉砕";
    public static final String RuneEquipDisplay = "§lルーン装着";
    private static final String RuneUpgradeDisplay = "§lルーン強化";
    private static final String RunePolishDisplay = "§lルーン研磨";
    private static final ItemStack RuneShopMenu_RuneCrash = new ItemStackData(Material.GUNPOWDER, decoText("ルーン粉砕"), "§a§lルーンを砕いて粉に変えます").view();
    private static final ItemStack RuneShopMenu_RuneEquip = new ItemStackData(Material.CHAINMAIL_CHESTPLATE, decoText("ルーン装着"), "§a§l武具にルーンを装着できます").view();
    private static final ItemStack RuneShopMenu_RuneUpgrade = new ItemStackData(Material.ANVIL, decoText("ルーン強化"), "§a§l同名・同レベルのルーンを合成して\n§a§lルーンのレベルを上げます\n§a§l品質は合計の55%の値になります").view();
    private static final ItemStack RuneShopMenu_RunePolish = new ItemStackData(Material.GRINDSTONE, decoText("ルーン研磨"), "§a§lルーンの粉末を使用して\n§a§lルーンの品質を上げます\n§a§l品質は最大の70%まで上がります").view();
    private final Player player;
    private final PlayerData playerData;

    public RuneShop(Player player, PlayerData playerData) {
        this.player = player;
        this.playerData = playerData;
    }

    public void RuneShopMenu() {

    }

    static List<Integer> RuneSlotIndex() {
        List<Integer> List = new ArrayList<>();
        List.add(14);
        List.add(15);
        List.add(16);
        List.add(23);
        List.add(24);
        List.add(25);
        List.add(32);
        List.add(33);
        List.add(34);
        return List;
    }

    public void RuneMenuView() {
        Inventory inv = decoInv(RuneShopMenuDisplay, 1);
        inv.setItem(0, RuneShopMenu_RuneCrash);
        inv.setItem(1, RuneShopMenu_RuneEquip);
        inv.setItem(2, RuneShopMenu_RuneUpgrade);
        inv.setItem(3, RuneShopMenu_RunePolish);
        player.openInventory(inv);
        playSound(player, SoundList.MenuOpen);
    }

    private final List<RuneParameter> RuneCrashed = new ArrayList<>();
    public void RuneCrashView() {
        player.openInventory(RuneCrashInv());
    }

    public Inventory RuneCrashInv() {
        Inventory inv = decoInv(RuneCrashDisplay, 5);
        playerData.Menu.ViewInventoryCache = playerData.ViewInventory;
        playerData.setView(ViewInventoryType.RuneInventory, false);
        int slot = 0;
        for (RuneParameter rune : RuneCrashed) {
            inv.setItem(slot, rune.viewRune(playerData.ViewFormat()));
            slot++;
        }
        return inv;
    }

    public void RuneEquipView() {
        Inventory inv = decoInv(RuneEquipDisplay, 5);
        playerData.Menu.ViewInventoryCache = playerData.ViewInventory;
        playerData.setView(ViewInventoryType.ItemInventory, false);
        for (int slot = 0; slot < 45; slot++) {
            if (slot != 20 && !RuneSlotIndex().contains(slot)) {
                inv.setItem(slot, RuneEquipFlame);
            }
        }
        player.openInventory(inv);
    }

    public void RuneUpgradeView() {
        Inventory inv = decoAnvil(RuneUpgradeDisplay);
        playerData.Menu.ViewInventoryCache = playerData.ViewInventory;
        playerData.setView(ViewInventoryType.RuneInventory, false);
        player.openInventory(inv);
    }

    public void RunePolishView() {
        Inventory inv = decoAnvil(RunePolishDisplay);
        playerData.Menu.ViewInventoryCache = playerData.ViewInventory;
        playerData.setView(ViewInventoryType.RuneInventory, false);
        player.openInventory(inv);
    }

    private ItemParameter RuneCache;
    private final RuneParameter[] RuneUpgradeCache = new RuneParameter[3];
    private final ItemParameter RunePowder = getItemParameter("ルーンの粉");
    public void RuneMenuClick(InventoryView view, Inventory ClickInventory, ItemStack currentItem, int index, int Slot) {
        String format = playerData.ViewFormat();
        if (equalInv(view, RuneShopMenuDisplay)) {
            if (equalItem(currentItem, RuneShopMenu_RuneCrash)) {
                RuneCrashView();
            } else if (equalItem(currentItem, RuneShopMenu_RuneEquip)) {
                RuneEquipView();
            } else if (equalItem(currentItem, RuneShopMenu_RuneUpgrade)) {
                RuneUpgradeView();
            } else if (equalItem(currentItem, RuneShopMenu_RunePolish)) {
                RunePolishView();
            }
            playSound(player, SoundList.Click);
        } else if (equalInv(view, RuneCrashDisplay) && playerData.ViewInventory.isRune()) {
            if (ClickInventory == view.getTopInventory()) {
                if (playerData.ItemInventory.hasItemParameter(RunePowder, 1)) {
                    RuneParameter rune = RuneCrashed.get(Slot).clone();
                    RuneCrashed.remove(Slot);
                    playerData.RuneInventory.addRuneParameter(rune);
                    playerData.ItemInventory.removeItemParameter(RunePowder, 1);
                    player.sendMessage("§e[ルーン]§aを§b復元§aしました");
                    playSound(player, SoundList.LevelUp);
                } else {
                    player.sendMessage("§e[ルーンの粉]§aが必要です");
                    playSound(player, SoundList.Nope);
                }
            } else if (index > -1) {
                RuneParameter rune = playerData.RuneInventory.getRuneParameter(index).clone();
                RuneCrashed.add(rune);
                playerData.RuneInventory.removeRuneParameter(index);
                playerData.ItemInventory.addItemParameter(RunePowder, 1);
                if (RuneCrashed.size() > 45) {
                    RuneCrashed.remove(0);
                }
                player.sendMessage("§e[ルーン]§aを§c粉砕§aしました");
                playSound(player, SoundList.LevelUp);
            }
            view.getTopInventory().setContents(RuneCrashInv().getStorageContents());
        } else if (equalInv(view, RuneEquipDisplay)) {
            if (ClickInventory == player.getInventory() && ignoreSlot(Slot)) {
                if (index > -1) {
                    if (playerData.ViewInventory.isItem()) {
                        if (RuneCache != null) {
                            playerData.ItemInventory.addItemParameter(RuneCache, 1);
                        }
                        RuneCache = playerData.ItemInventory.getItemParameter(index);
                        playerData.ItemInventory.removeItemParameter(RuneCache, 1);
                        playerData.setView(ViewInventoryType.RuneInventory, false);
                        playSound(player, SoundList.Click);
                    } else if (playerData.ViewInventory.isRune()) {
                        if (RuneCache != null) {
                            if (RuneCache.itemEquipmentData.getRuneSize() < RuneCache.itemEquipmentData.RuneSlot) {
                                RuneParameter runeParameter = playerData.RuneInventory.getRuneParameter(index);
                                playerData.RuneInventory.removeRuneParameter(index);
                                RuneCache.itemEquipmentData.addRune(runeParameter);
                                playSound(player, SoundList.Click);
                            } else {
                                player.sendMessage("§eルーンスロット§aに空きがありません");
                                playSound(player, SoundList.Nope);
                            }
                        } else {
                            player.sendMessage("§e装備§aを§eセット§aしてください");
                            playSound(player, SoundList.Nope);
                        }
                    }
                }
            } else if (view.getTopInventory() == ClickInventory) {
                if (Slot == 20) {
                    playerData.ItemInventory.addItemParameter(RuneCache, 1);
                    RuneCache = null;
                    playerData.setView(ViewInventoryType.ItemInventory, false);
                    playSound(player, SoundList.Click);
                } else if ((14 <= Slot && Slot <= 16) || (23 <= Slot && Slot <= 25) || (32 <= Slot && Slot <= 34)) {
                    int RuneIndex;
                    if (Slot <= 16) {
                        RuneIndex = Slot - 14;
                    } else if (Slot <= 25) {
                        RuneIndex = Slot - 20;
                    } else {
                        RuneIndex = Slot - 26;
                    }
                    if (RuneIndex < RuneCache.itemEquipmentData.getRuneSize()) {
                        playerData.RuneInventory.addRuneParameter(RuneCache.itemEquipmentData.getRune(RuneIndex));
                        RuneCache.itemEquipmentData.removeRune(RuneIndex);
                        playSound(player, SoundList.Click);
                    }
                }
            }
            if (RuneCache != null) {
                view.getTopInventory().setItem(20, RuneCache.viewItem(1, format));
                int i = 0;
                for (int slot : RuneSlotIndex()) {
                    if (i < RuneCache.itemEquipmentData.RuneSlot) {
                        if (i < RuneCache.itemEquipmentData.getRuneSize()) {
                            view.getTopInventory().setItem(slot, RuneCache.itemEquipmentData.getRune(i).viewRune(format));
                        } else {
                            view.getTopInventory().setItem(slot, AirItem);
                        }
                    } else view.getTopInventory().setItem(slot, new ItemStack(Material.RED_STAINED_GLASS_PANE));
                    i++;
                }
            } else {
                view.getTopInventory().setItem(20, AirItem);
                for (int slot : RuneSlotIndex()) {
                    view.getTopInventory().setItem(slot, AirItem);
                }
            }
        } else if (equalInv(view, RuneUpgradeDisplay) && playerData.ViewInventory.isRune()) {
            Inventory inv = view.getTopInventory();
            if (ClickInventory == inv) {
                if (Slot == AnvilUISlot[0] || Slot == AnvilUISlot[1]) {
                    for (int i = 0; i < 2; i++) {
                        if (RuneUpgradeCache[i] != null) {
                            playerData.RuneInventory.addRuneParameter(RuneUpgradeCache[i]);
                            RuneUpgradeCache[i] = null;
                        }
                    }
                    playSound(player, SoundList.Click);
                } else if (Slot == AnvilUISlot[2] && RuneUpgradeCache[2] != null) {
                    playerData.RuneInventory.addRuneParameter(RuneUpgradeCache[2]);
                    RuneUpgradeCache[2] = null;
                    for (int i = 0; i < 2; i++) {
                        RuneUpgradeCache[i] = null;
                    }
                    player.sendMessage("§e[ルーン]§aを§b合成§aしました");
                    playSound(player, SoundList.LevelUp);
                }
            } else if (index > -1) {
                if (RuneUpgradeCache[0] == null) {
                    RuneUpgradeCache[0] = playerData.RuneInventory.getRuneParameter(index).clone();
                    playerData.RuneInventory.removeRuneParameter(index);
                    playSound(player, SoundList.Click);
                } else if (RuneUpgradeCache[1] == null) {
                    RuneParameter rune = playerData.RuneInventory.getRuneParameter(index);
                    if (RuneUpgradeCache[0].Id.equals(rune.Id) && RuneUpgradeCache[0].Level == rune.Level) {
                        RuneUpgradeCache[1] = rune.clone();
                        playerData.RuneInventory.removeRuneParameter(index);
                        playSound(player, SoundList.Click);
                    } else {
                        player.sendMessage("§e[同名・同レベル]§aの§e[ルーン]§aを選択してください");
                        playSound(player, SoundList.Nope);
                    }
                }
            }
            for (int i = 0; i < 2; i++) {
                if (RuneUpgradeCache[i] != null) {
                    inv.setItem(AnvilUISlot[i], RuneUpgradeCache[i].viewRune(format));
                } else {
                    inv.setItem(AnvilUISlot[i], AirItem);
                }
            }
            if (RuneUpgradeCache[0] != null && RuneUpgradeCache[1] != null) {
                RuneUpgradeCache[2] = getRuneParameter(RuneUpgradeCache[0].Id);
                RuneUpgradeCache[2].Level = RuneUpgradeCache[0].Level+1;
                RuneUpgradeCache[2].Quality = Math.min(2, (RuneUpgradeCache[0].Quality + RuneUpgradeCache[1].Quality)*0.55);
                inv.setItem(AnvilUISlot[2], RuneUpgradeCache[2].viewRune(format));
            } else {
                inv.setItem(AnvilUISlot[2], AirItem);
                RuneUpgradeCache[2] = null;
            }
        } else if (equalInv(view, RunePolishDisplay) && playerData.ViewInventory.isRune()) {
            Inventory inv = view.getTopInventory();
            if (ClickInventory == inv) {
                if (Slot == AnvilUISlot[2]) {
                    if (playerData.ItemInventory.hasItemParameter(RunePowder, 1)) {
                        playerData.RuneInventory.addRuneParameter(RuneUpgradeCache[2]);
                        playerData.ItemInventory.removeItemParameter(RunePowder, 1);
                        RuneUpgradeCache[0] = null;
                        player.sendMessage("§e[ルーン]§aを§b研磨§aしました");
                        playSound(player, SoundList.LevelUp);
                    } else {
                        player.sendMessage("§e[" + RunePowder.Display + "]§aが足りません");
                        playSound(player, SoundList.Nope);
                    }
                } else if (RuneUpgradeCache[0] != null) {
                    playerData.RuneInventory.addRuneParameter(RuneUpgradeCache[0]);
                    RuneUpgradeCache[0] = null;
                    playSound(player, SoundList.Click);
                }
            } else if (index > -1) {
                if (RuneUpgradeCache[0] != null) playerData.RuneInventory.addRuneParameter(RuneUpgradeCache[0]);
                RuneUpgradeCache[0] = playerData.RuneInventory.getRuneParameter(index).clone();
                if (RuneUpgradeCache[0].Quality < 0.7) {
                    playerData.RuneInventory.removeRuneParameter(index);
                    playSound(player, SoundList.Click);
                } else {
                    RuneUpgradeCache[0] = null;
                    player.sendMessage("§e[品質]§aが§e[70%以上]§aです");
                    playSound(player, SoundList.Nope);
                }
            }
            if (RuneUpgradeCache[0] != null) {
                RuneUpgradeCache[2] = RuneUpgradeCache[0].clone();
                RuneUpgradeCache[2].Quality = Math.min(0.7, RuneUpgradeCache[2].Quality*1.2);
                inv.setItem(AnvilUISlot[0], RuneUpgradeCache[0].viewRune(format));
                inv.setItem(AnvilUISlot[1], RunePowder.viewItem(1, format));
                inv.setItem(AnvilUISlot[2], RuneUpgradeCache[2].viewRune(format));
            } else {
                inv.setItem(AnvilUISlot[0], AirItem);
                inv.setItem(AnvilUISlot[1], AirItem);
                inv.setItem(AnvilUISlot[2], AirItem);
                RuneUpgradeCache[2] = null;
            }
        }
    }

    public void RuneMenuClose(InventoryView view) {
        player.setItemOnCursor(AirItem);
        if (equalInv(view, RuneEquipDisplay)) {
            if (RuneCache != null) {
                playerData.ItemInventory.addItemParameter(RuneCache, 1);
                RuneCache = null;
            }
        } else if (equalInv(view, RuneUpgradeDisplay) || equalInv(view, RunePolishDisplay)) {
            for (int i = 0; i < 2; i++) {
                if (RuneUpgradeCache[i] != null) {
                    playerData.RuneInventory.addRuneParameter(RuneUpgradeCache[i]);
                    RuneUpgradeCache[i] = null;
                }
            }
        }
    }
}

package swordofmagic7.Inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import swordofmagic7.Data.PlayerData;
import swordofmagic7.Data.Type.ViewInventoryType;
import swordofmagic7.MultiThread.MultiThread;
import swordofmagic7.Pet.PetParameter;
import swordofmagic7.Sound.SoundList;

import java.util.*;

import static swordofmagic7.Data.DataBase.AirItem;
import static swordofmagic7.Function.sendMessage;
import static swordofmagic7.SomCore.plugin;
import static swordofmagic7.SomCore.spawnPlayer;
import static swordofmagic7.Sound.CustomSound.playSound;

public class PetInventory extends BasicInventory {
    public final int MaxSlot = 500;
    private final List<PetParameter> List = new ArrayList<>();
    private final HashMap<UUID, PetParameter> HashMap = new HashMap<>();
    public PetInventory(Player player, PlayerData playerData) {
        super(player, playerData);
    }
    public PetSortType Sort = PetSortType.Name;
    public boolean SortReverse = false;

    public void start() {
        MultiThread.TaskRun(() -> {
            while (player.isOnline() && plugin.isEnabled()) {
                if (List.size() > 0) {
                    for (PetParameter pet : List) {
                        if (!pet.Summoned) {
                            pet.changeStamina(1);
                        }
                        pet.Health += pet.HealthRegen / 5;
                        pet.Mana += pet.ManaRegen / 5;
                        if (pet.Health > pet.MaxHealth) pet.Health = pet.MaxHealth;
                        if (pet.Mana > pet.MaxMana) pet.Mana = pet.MaxMana;
                    }
                    if (playerData.ViewInventory.isPet()) {
                        viewPet();
                    }
                }
                MultiThread.sleepTick(20);
            }
        }, "PetInventory");
    }

    public List<PetParameter> getList() {
        return List;
    }

    public HashMap<UUID, PetParameter> getHashMap() {
        return HashMap;
    }

    public void clear() {
        List.clear();
    }

    public void addPetParameter(PetParameter pet) {
        if (List.size() < MaxSlot) {
            if (List.size() >= MaxSlot-10) {
                sendMessage(player, "§e[ペットケージ]§aが§c残り" + (MaxSlot - List.size()) +"スロット§aです", SoundList.Tick);
            }
        } else {
            sendMessage(player, "§e[ペットケージ]§aが§c満杯§aです", SoundList.Nope);
            spawnPlayer(player);
        }
        HashMap.put(pet.petUUID, pet);
        List.add(pet);
    }
    public PetParameter getPetParameter(int i) {
        if (i < List.size()) {
            return List.get(i);
        }
        return null;
    }

    public void removePetParameter(int i) {
        List.remove(i);
    }

    public boolean hasPetParameter(String name) {
        for (PetParameter pet : List) {
            if (pet.petData.Id.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void PetInventorySort() {
        switch (Sort) {
            case Name -> Sort = PetSortType.Level;
            case Level -> Sort = PetSortType.GrowthRate;
            case GrowthRate -> Sort = PetSortType.Name;
        }
        player.sendMessage("§e[ペットケージ]§aの§e[ソート方法]§aを§e[" + Sort.Display + "]§aにしました");
        playSound(player, SoundList.Click);
        playerData.viewUpdate();
    }

    public void PetInventorySortReverse() {
        SortReverse = !SortReverse;
        String msg = "§e[ペットケージ]§aの§e[ソート順]§aを";
        if (!SortReverse) msg += "§b[昇順]";
        else msg += "§c[降順]";
        msg += "§aにしました";
        player.sendMessage(msg);
        playSound(player, SoundList.Click);
        playerData.viewUpdate();
    }

    public void viewPet() {
        playerData.ViewInventory = ViewInventoryType.PetInventory;
        int index = ScrollTick*8;
        int slot = 9;
        Comparator<PetParameter> comparator = null;
        try {
            if (List.size() > 0) switch (Sort) {
                case Name -> comparator = new PetSortName();
                case Level -> comparator = new PetSortLevel();
                case GrowthRate -> comparator = new PetSortGrowthRate();
            }
            if (comparator != null) List.sort(comparator);
            if (SortReverse) Collections.reverse(List);
        } catch (Exception e) {
            sendMessage(player, "§eソート処理中§aに§cエラー§aが発生したため§eソート処理§aを§e中断§aしました §c" + e.getMessage());
        }
        for (int i = index; i < index+24; i++) {
            if (i < List.size()) {
                ItemStack item = List.get(i).viewPet(playerData.ViewFormat());
                ItemMeta meta = item.getItemMeta();
                List<String> Lore = new ArrayList<>(meta.getLore());
                Lore.add("§8" + i);
                meta.setLore(Lore);
                item.setItemMeta(meta);
                player.getInventory().setItem(slot, item);
            } else {
                player.getInventory().setItem(slot, AirItem);
            }
            slot++;
            if (slot == 17 || slot == 26) slot++;
        }
    }
}

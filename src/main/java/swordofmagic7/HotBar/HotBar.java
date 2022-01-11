package swordofmagic7.HotBar;

import org.bukkit.entity.Player;
import swordofmagic7.Data.PlayerData;
import swordofmagic7.Data.Type.ViewInventoryType;
import swordofmagic7.Item.ItemParameter;
import swordofmagic7.Sound.SoundList;

import java.util.UUID;

import static swordofmagic7.Data.DataBase.getItemParameter;
import static swordofmagic7.Data.DataBase.getSkillData;
import static swordofmagic7.Sound.CustomSound.playSound;

public class HotBar {
    private final Player player;
    private final PlayerData playerData;
    private int SelectSlot = -1;
    private HotBarData[] HotBarData = new HotBarData[32];

    public HotBar(Player player, PlayerData playerData) {
        this.player = player;
        this.playerData = playerData;

        for(int i = 0; i < 32; i++) {
            HotBarData[i] = new HotBarData();
        }
    }

    public void use(int index) {
        switch (HotBarData[index].category) {
            case Skill -> {
                if (playerData.Skill.isCastReady()) {
                    playerData.Skill.CastSkill(getSkillData(HotBarData[index].Icon));
                }
            }
            case Pet -> {
                if (playerData.PetInventory.getHashMap().containsKey(UUID.fromString(HotBarData[index].Icon))) {
                    playerData.PetInventory.getHashMap().get(UUID.fromString(HotBarData[index].Icon)).spawn();
                } else {
                    player.sendMessage("§e[ペットケージ]§aに居ません");
                    playSound(player, SoundList.Nope);
                }
            }
            case Item -> {
                ItemParameter item = getItemParameter(HotBarData[index].Icon);
                if (playerData.ItemInventory.hasItemParameter(item, 1)) {
                    item.itemPotion.usePotion(player, item);
                } else {
                    player.sendMessage("§e[アイテム]§aがありません");
                    playSound(player, SoundList.Nope);
                }
            }
            default -> player.sendMessage("§e[ホットバー" + (index+1) + "]§aは§eセット§aされていません");
        }
        UpdateHotBar();
    }

    public void UpdateHotBar() {
        if (playerData.ViewInventory.isHotBar()) viewTop();
        viewBottom();
    }

    public void setHotBar(HotBarData[] HotBarData) {
        this.HotBarData = HotBarData.clone();
    }

    public void setHotBar(int index, HotBarData HotBarData) {
        this.HotBarData[index] = HotBarData.clone();
    }

    public void setSelectSlot(int slot) {
        SelectSlot = slot;
    }

    public int getSelectSlot() {
        return SelectSlot;
    }

    public void unSelectSlot() {
        SelectSlot = -1;
    }

    public void addHotbar(HotBarData hotBarData) {
        for (int i = 0; i < 32; i++) {
            if (HotBarData[i] != null) {
                if (HotBarData[i].isEmpty()) {
                    HotBarData[i] = hotBarData.clone();
                    return;
                }
            } else {
                HotBarData[i] = hotBarData.clone();
                return;
            }
        }
        player.sendMessage("§e[ホットバー]§aに空きがありません");
    }

    public HotBarData[] getHotBar() {
        return HotBarData;
    }

    HotBarData getHotBar(int index) {
        return HotBarData[index];
    }

    public void viewBottom() {
        for (int i = 0; i < 8; i++) {
            if (HotBarData[i] == null) HotBarData[i] = new HotBarData();
            player.getInventory().setItem(i, HotBarData[i].view(playerData, SelectSlot == i));
        }
    }

    public void viewTop() {
        playerData.ViewInventory = ViewInventoryType.HotBar;
        int slot = 9;
        for (int i = 8; i < 32; i++) {
            player.getInventory().setItem(slot, HotBarData[i].view(playerData, SelectSlot == i));
            slot++;
            if (slot == 17 || slot == 26 || slot == 35) slot++;
        }
    }
}
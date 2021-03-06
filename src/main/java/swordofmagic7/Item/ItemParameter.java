package swordofmagic7.Item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import swordofmagic7.Function;
import swordofmagic7.Item.ItemExtend.*;
import swordofmagic7.Item.ItemUseList.RewardBox;
import swordofmagic7.Item.ItemUseList.RewardBoxData;
import swordofmagic7.Status.StatusParameter;
import swordofmagic7.TextView.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static swordofmagic7.Data.DataBase.*;
import static swordofmagic7.Function.*;

public class ItemParameter implements Cloneable {
    public String Id;
    public String Display = "Display";
    public List<String> Lore = new ArrayList<>();
    public Material Icon = Material.BARRIER;
    public String IconData;
    public Color color = Color.BLACK;
    public ItemCategory Category = ItemCategory.None;
    public int CustomModelData = 0;
    public int Sell = 0;
    public ItemEquipmentData itemEquipmentData = new ItemEquipmentData();
    public ItemPetEgg itemPetEgg = new ItemPetEgg();
    public ItemPotion itemPotion = new ItemPotion();
    public ItemPetFood itemPetFood = new ItemPetFood();
    public ItemCook itemCook = new ItemCook();
    public String Materialization;
    public boolean isHide = false;
    public boolean isLoreHide = false;
    public boolean isNonTrade = false;
    public java.io.File File;

    Material getIcon() {
        if (Icon == null) Icon = Material.BARRIER;
        return Icon;
    }

    public ItemParameter() {
        for (StatusParameter param : StatusParameter.values()) {
            itemEquipmentData.Parameter.put(param, 0d);
        }
    }

    public boolean isEmpty() {
        return this.Icon == Material.BARRIER || Icon == null;
    }

    public ItemStack viewItem(int amount, String format) {
        return viewItem(amount, format, true);
    }

    public ItemStack viewItem(int amount, String format, boolean isLoreHide) {
        final HashMap<StatusParameter, Double> Parameter = itemEquipmentData.Parameter();
        final ItemStack item = new ItemStack(getIcon());
        final ItemMeta meta = item.getItemMeta();
        if (item.getType() == Material.POTION) {
            PotionMeta potion = (PotionMeta) meta;
            potion.setColor(color);
        }
        meta.setDisplayName(decoText(Display));
        meta.setCustomModelData(CustomModelData);
        List<String> Lore = new ArrayList<>();
        if (isLoreHide && this.isLoreHide) Lore.add("??c??l??????????????????????????????????????????????????????");
        else Lore.addAll(loreText(this.Lore));
        Lore.add(itemInformation);
        Lore.add(decoLore("????????????") + Category.Display);
        Lore.add(decoLore("??????") + Sell);
        if (Category.isPotion()) {
            Lore.add(decoText("??3??l???????????????"));
            Lore.add(decoLore("?????????") + itemPotion.PotionType.Display);
            int i = 1;
            String suffix = "";
            if (itemPotion.PotionType.isElixir()) suffix = "%";
            for (double d : itemPotion.Value) {
                if (d != 0) {
                    Lore.add(decoLore("?????????[" + i + "]") + d + suffix);
                }
                i++;
            }
            Lore.add(decoLore("???????????????") + itemPotion.CoolTime + "???");
        }
        if (Category.isCook()) {
            Lore.add(decoText("??3??l????????????"));
            if (itemCook.Health > 0) Lore.add(decoLore("????????????") + itemCook.Health);
            if (itemCook.Mana > 0) Lore.add(decoLore("????????????") + itemCook.Mana);
            for (StatusParameter param : StatusParameter.values()) {
                if (itemCook.Fixed.containsKey(param)) {
                    Lore.add(decoLore(param.Display) + "+" + Function.decoDoubleToString(Math.round(itemCook.Fixed.get(param)), format));
                }
                if (itemCook.Multiply.containsKey(param)) {
                    Lore.add(decoLore(param.Display) + "+" + Function.decoDoubleToString(Math.round(itemCook.Multiply.get(param)*100), format) + "%");
                }
            }
            if (itemCook.isBuff) Lore.add(decoLore("????????????") + itemCook.BuffTime + "???");
            Lore.add(decoLore("???????????????") + itemCook.CoolTime + "???");
        }
        if (Category.isEquipment()) {
            Lore.add(itemParameter);
            Lore.add(decoLore("????????????") + itemEquipmentData.EquipmentSlot.Display);
            Lore.add(decoLore("?????????") + itemEquipmentData.equipmentCategory.Display);
            for (StatusParameter param : StatusParameter.values()) {
                if (isZero(Parameter.get(param))) {
                    if (itemEquipmentData.isAccessory()) {
                        Lore.add(param.DecoDisplay + String.format(format, Parameter.get(param)) + " (" + String.format(format, itemEquipmentData.itemAccessory.Base.get(param)) + "??" + String.format("%.0f", itemEquipmentData.itemAccessory.Range.get(param)*100) + "%)");
                    } else {
                        Lore.add(param.DecoDisplay + String.format(format, Parameter.get(param)) + " (" +String.format(format, itemEquipmentData.Parameter.get(param)) + ")");
                    }
                }
            }
            //if (itemEquipmentData.RuneMultiply != 1)
            Lore.add(decoLore("???????????????") + String.format("%.0f", itemEquipmentData.RuneMultiply*100) + "%");
            Lore.add(decoLore("?????????") + itemEquipmentData.Plus);
            Lore.add(decoLore("???????????????") + itemEquipmentData.ReqLevel);
            Lore.add(itemRune);
            for (int i = 0; i < itemEquipmentData.RuneSlot; i++) {
                if (i < itemEquipmentData.Rune.size()) {
                    RuneParameter runeParameter = itemEquipmentData.Rune.get(i);
                    Lore.add("??7?????e??l" + runeParameter.Display + " Lv" + runeParameter.Level + " (" + String.format(format, runeParameter.Quality*100) + "%)");
                } else {
                    Lore.add("??7?????l??????????????????");
                }
            }
        }
        if (RewardBoxList.containsKey(Id)) {
            Lore.add(decoText("??3??l?????????"));
            RewardBox rewardBox = RewardBoxList.get(Id);
            for (RewardBoxData rewardBoxData : rewardBox.List) {
                Lore.add("??7?????e??l" + rewardBoxData.id + "??ax" + rewardBoxData.amount + " ??b??l-> ??a??l" + String.format(format, rewardBoxData.percent*100) + "%");
            }
            Lore.add(rewardBox.isPartition ? "??b??l??????" : "??b??l????????????");
        }
        meta.setUnbreakable(true);
        meta.setLore(Lore);
        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        if (Icon == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) meta;
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", IconData));
            try {
                Field field = skullMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(skullMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log("????????????????????????????????????????????????????????????????????? -> " + Display);
            }

            item.setItemMeta(skullMeta);
        } else {
            item.setItemMeta(meta);
        }
        if (Category.isTool()) {
            //NBTItem nbtItem = new NBTItem(item);
        }

        if (amount > MaxStackAmount) {
            amount = MaxStackAmount;
        }

        item.setAmount(amount);
        return item;
    }

    public TextView getTextView(int amount, String format) {
        return getTextView(amount, format, true);
    }

    public TextView getTextView(int amount, String format, boolean isLoreHide) {
        ItemStack item = viewItem(amount, format, isLoreHide);
        String suffix = "";
        if (amount > 1) suffix = "??ax" + amount;
        if (Category.isEquipment()) suffix = "??b+" + itemEquipmentData.Plus;
        StringBuilder hoverText = new StringBuilder(item.getItemMeta().getDisplayName());
        for (String str : item.getLore()) {
            hoverText.append("\n").append(str);
        }
        return new TextView().addText("??e[" + Display + suffix + "??e]").addHover(hoverText.toString()).reset();
    }

    @Override
    public ItemParameter clone() {
        try {
            ItemParameter clone = (ItemParameter) super.clone();
            clone.itemEquipmentData = this.itemEquipmentData.clone();
            clone.itemPotion = this.itemPotion.clone();
            clone.itemPetEgg = this.itemPetEgg.clone();
            clone.itemPetFood = this.itemPetFood.clone();
            // TODO: ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
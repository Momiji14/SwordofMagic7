package swordofmagic7.Data;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import swordofmagic7.Function;
import swordofmagic7.Item.ItemStackData;

import java.util.ArrayList;
import java.util.List;

import static swordofmagic7.Data.DataBase.AirItem;
import static swordofmagic7.Function.Log;

public class TitleData {
    public final String Id;
    public final Material Icon;
    public final int Amount;
    public final String[] Display;
    public final List<String> Lore;
    public final int[] waitTick;
    public final int flame;

    TitleData(String Id, Material icon, int amount, List<String> Data, List<String> Lore) {
        this.Id = Id;
        Icon = icon;
        Amount = amount;
        this.Lore = Lore;
        flame = Data.size();
        Display = new String[flame];
        waitTick = new int[flame];
        int i = 0;
        for (String data : Data) {
            String[] split = data.split(",");
            Display[i] = split[0];
            waitTick[i] = Integer.parseInt(split[1]);
            i++;
        }
    }

    public ItemStack view(boolean has) {
        try {
            List<String> lore = new ArrayList<>();
            for (String str : Lore) {
                lore.add("§a§l" + str);
            }
            lore.add(Function.decoText("プレビュー"));
            lore.addAll(List.of(Display));
            ItemStack item = new ItemStackData(Icon, Function.decoText(Id), lore).view();
            if (has) item.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
            item.setAmount(Amount);
            return item;
        } catch (Exception e) {
            Log("TitleError -> " + Id);
        }
        return AirItem;
    }
}

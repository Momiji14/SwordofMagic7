package swordofmagic7.Life.Smith;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import swordofmagic7.Function;
import swordofmagic7.Inventory.ItemParameterStack;
import swordofmagic7.Item.ItemParameter;
import swordofmagic7.Shop.ItemRecipe;

import java.util.ArrayList;
import java.util.List;

import static swordofmagic7.Function.decoLore;

public class SmeltData {
    public ItemParameter itemParameter;
    public int Amount;
    public int ReqLevel;
    int Exp;
    public ItemRecipe itemRecipe;

    public SmeltData(ItemParameter item, int amount, int reqLevel, int exp, ItemRecipe itemRecipe) {
        itemParameter = item;
        Amount = amount;
        ReqLevel = reqLevel;
        Exp = exp;
        this.itemRecipe = itemRecipe;
    }

    public ItemStack view(String format) {
        ItemStack item = itemParameter.viewItem(Amount, format);
        ItemMeta meta = item.getItemMeta();
        List<String> Lore = new ArrayList<>(meta.getLore());
        Lore.add(Function.decoText("§3§l精錬情報"));
        Lore.add(Function.decoLore("必要鍛冶レベル") + ReqLevel);
        Lore.add(Function.decoLore("鍛冶経験値") + Exp);
        if (Amount >= 100) Lore.add(Function.decoLore("精錬個数") + Amount);
        for (ItemParameterStack stack : itemRecipe.ReqStack) {
            Lore.add(decoLore(stack.itemParameter.Id) + stack.Amount + "個");
        }
        meta.setLore(Lore);
        item.setItemMeta(meta);
        return item;
    }
}

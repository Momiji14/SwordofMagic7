package swordofmagic7.Market;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import swordofmagic7.Function;
import swordofmagic7.Inventory.ItemParameterStack;
import swordofmagic7.Item.ItemParameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static swordofmagic7.Data.DataBase.DataBasePath;
import static swordofmagic7.Data.DataBase.MaxStackAmount;

public class MarketData {
    public static final String MarketDataSplit = "@Market@";
    ItemParameterStack itemParameterStack;
    UUID uuid;
    int Mel;
    UUID Owner;
    long timeStamp;

    public MarketData(ItemParameterStack itemParameterStack, int Mel, UUID Owner, long timeStamp) {
        this.itemParameterStack = itemParameterStack;
        this.Mel = Mel;
        this.Owner = Owner;
        this.timeStamp = timeStamp;
        uuid = UUID.randomUUID();
    }

    public MarketData(String data) {
        String[] split = data.split(MarketDataSplit);
        itemParameterStack = ItemParameterStack.fromString(split[0]);
        Mel = Integer.parseInt(split[1]);
        Owner = UUID.fromString(split[2]);
        timeStamp = Long.parseLong(split[3]);
        uuid = UUID.fromString(split[4]);
    }

    public String toString() {
        return itemParameterStack.toString() + MarketDataSplit
                + Mel + MarketDataSplit
                + Owner + MarketDataSplit
                + timeStamp + MarketDataSplit
                + uuid;
    }

    public ItemStack view(String format) {
        ItemParameter itemPram = itemParameterStack.itemParameter;
        ItemStack item = itemPram.viewItem(itemParameterStack.Amount, format);
        ItemMeta meta = item.getItemMeta();
        List<String> Lore = new ArrayList<>(meta.getLore());
        File file = new File(DataBasePath, "Market/" + Market.MarketPriceYml);
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        Lore.add(Function.decoText("??3??l????????????"));
        try {
            Lore.add(Function.decoLore("????????????") + (data.isSet(itemPram.Id) ? data.getInt(itemPram.Id) : "??7??l??????????????????"));
        } catch (Exception e) {
            Lore.add(Function.decoLore("????????????") + "??c??l?????????????????????????????????");
        }
        Lore.add(Function.decoLore("?????????") + MarketContainer.getOwnerNick(Owner));
        Lore.add(Function.decoLore("????????????") + Mel + "??????/???");
        Lore.add(Function.decoLore("?????????") + itemParameterStack.Amount + "???");
        Lore.add(Function.decoLore("??????") + (int) Math.floor((System.currentTimeMillis() - timeStamp)/60000f) + "???");
        meta.setLore(Lore);
        item.setItemMeta(meta);
        item.setAmount(Math.min(MaxStackAmount, itemParameterStack.Amount));
        return item;
    }
}

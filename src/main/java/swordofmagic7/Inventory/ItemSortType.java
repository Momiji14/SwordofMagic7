package swordofmagic7.Inventory;

import swordofmagic7.Item.RuneParameter;

import java.util.Comparator;

public enum ItemSortType {
    Name("名前順"),
    Category("カテゴリ順"),
    Amount("個数順"),
    ;

    public String Display;

    ItemSortType(String Display) {
        this.Display = Display;
    }
}

class ItemSortName implements Comparator<ItemParameterStack> {
    public int compare(ItemParameterStack item, ItemParameterStack item2) {
        return item.itemParameter.Id.compareTo(item2.itemParameter.Id);
    }
}

class ItemSortCategory implements Comparator<ItemParameterStack> {
    public int compare(ItemParameterStack item, ItemParameterStack item2) {
        return item.itemParameter.Category.compareTo(item2.itemParameter.Category);
    }
}

class ItemSortAmount implements Comparator<ItemParameterStack> {
    public int compare(ItemParameterStack item, ItemParameterStack item2) {
        return item.Amount - item2.Amount;
    }
}
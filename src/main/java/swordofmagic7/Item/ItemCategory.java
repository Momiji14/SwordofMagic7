package swordofmagic7.Item;

public enum ItemCategory {
    Item("アイテム"),
    Potion("ポーション"),
    Material("素材"),
    PetEgg("ペットエッグ"),
    PetFood("ペットフード"),
    Equipment("装備"),
    Tool("ツール"),
    ;
    String Display;

    ItemCategory(String Display) {
        this.Display = Display;
    }

    public ItemCategory getItemCategory(String str) {
        for (ItemCategory loop : ItemCategory.values()) {
            if (loop.toString().equalsIgnoreCase(str)) {
                return loop;
            }
        }
        return ItemCategory.Item;
    }

    public boolean isItem() {
        return this == Item;
    }

    public boolean isPotion() {
        return this == Potion;
    }

    public boolean isMaterial() {
        return this == Material;
    }

    public boolean isPetEgg() {
        return this == PetEgg;
    }

    public boolean isPetFood() {
        return this == PetFood;
    }

    public boolean isEquipment() {
        return this == Equipment;
    }

    public boolean isTool() {
        return this == Tool;
    }
}
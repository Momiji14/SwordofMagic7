package swordofmagic7.ViewBar.SideBarToDo;

import org.bukkit.entity.Player;
import swordofmagic7.Classes.ClassData;
import swordofmagic7.Data.DataBase;
import swordofmagic7.Data.PlayerData;
import swordofmagic7.Item.ItemParameter;
import swordofmagic7.Life.LifeStatus;
import swordofmagic7.Life.LifeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static swordofmagic7.Function.decoLore;
import static swordofmagic7.Function.decoText;

public class SideBarToDo {

    private final Player player;
    private final PlayerData playerData;

    public List<SideBarToDoData> list = new ArrayList<>();

    public SideBarToDo(PlayerData playerData) {
        this.playerData = playerData;
        player = playerData.player;
    }

    public void SideBarToDoCommand(String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("itemAmount") || args[0].equalsIgnoreCase("iA")) {
                if (args.length == 2) {
                    ItemParameter item = null;
                    if (DataBase.ItemList.containsKey(args[1])) {
                        item = DataBase.getItemParameter(args[1]);
                    } else {
                        try {
                            item = playerData.ItemInventory.getItemParameter(Integer.parseInt(args[1]));
                        } catch (Exception ignored) {
                        }
                    }
                    if (item != null) {
                        SideBarToDoData data = new SideBarToDoData();
                        data.type = SideBarToDoType.ItemAmount;
                        data.key = item;
                        list.add(data);
                    } else {
                        player.sendMessage("§a存在しない§eアイテム§aです");
                    }
                }
            } else if (args[0].equalsIgnoreCase("lifeInfo") || args[0].equalsIgnoreCase("lI")) {
                SideBarToDoData data = new SideBarToDoData();
                data.type = SideBarToDoType.LifeInfo;
                data.key = LifeType.getData(args[1]);
                if (data.key != null) {
                    list.add(data);
                } else {
                    viewHelp();
                }
            } else if (args[0].equalsIgnoreCase("classInfo") || args[0].equalsIgnoreCase("cI")) {
                SideBarToDoData data = new SideBarToDoData();
                data.type = SideBarToDoType.ClassInfo;
                data.key = DataBase.getClassData(args[1]);
                if (data.key != null) {
                    list.add(data);
                } else {
                    viewHelp();
                }
            } else if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("c")) {
                if (args.length >= 2) {
                    try {
                        list.remove(Integer.parseInt(args[1]));
                    } catch (Exception e) {
                        viewHelp();
                    }
                } else {
                    list.clear();
                }
            } else {
                viewHelp();
            }
        } else {
            viewHelp();
        }
    }

    public void refresh() {
        if (list.size() > 0) {
            List<String> textData = new ArrayList<>();
            textData.add(decoText("SideBarToDo"));
            for (SideBarToDoData data : list) {
                if (data.type.isItemAmount()) {
                    ItemParameter item = (ItemParameter) data.key;
                    int amount = playerData.ItemInventory.getItemParameterStack(item).Amount;
                    textData.add(decoLore("アイテム数[" + item.Display + "]") + amount + "個");
                } else if (data.type.isLifeInfo()) {
                    LifeType type = (LifeType) data.key;
                    textData.add("§7・§e§l" + type.Display + " Lv" + playerData.LifeStatus.getLevel(type) + " " + playerData.LifeStatus.viewExpPercent(type));
                } else if (data.type.isClassInfo()) {
                    ClassData classData = (ClassData) data.key;
                    textData.add("§7・" + classData.Color + "§l" + classData.Display + " Lv" + playerData.Classes.getClassLevel(classData) + " " + playerData.Classes.viewExpPercent(classData) + "%");
                }
            }
            playerData.ViewBar.setSideBar("SideBarToDo", textData);
        } else {
            playerData.ViewBar.resetSideBar("SideBarToDo");
        }
    }

    void viewHelp() {
        player.sendMessage(decoText("SideBarToDo Commands"));
        player.sendMessage("§e/sideBarToDo itemAmount <ItemName>");
        player.sendMessage("§e/sideBarToDo LifeInfo <LifeID>");
        player.sendMessage("§e/sideBarToDo ClassInfo <ClassID>");
        player.sendMessage("§e/sideBarToDo clear [<index>]");
    }
}

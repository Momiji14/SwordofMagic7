package swordofmagic7.Item.ItemUseList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import swordofmagic7.Client;
import swordofmagic7.Data.DataBase;
import swordofmagic7.Data.PlayerData;
import swordofmagic7.Item.ItemParameter;
import swordofmagic7.Mob.EnemyData;
import swordofmagic7.Mob.MobManager;
import swordofmagic7.MultiThread.MultiThread;
import swordofmagic7.PlayerList;
import swordofmagic7.SomCore;
import swordofmagic7.Sound.SoundList;
import swordofmagic7.TextView.TextView;
import swordofmagic7.ViewBar.ViewBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static swordofmagic7.Data.DataBase.ServerId;
import static swordofmagic7.Dungeon.Dungeon.*;
import static swordofmagic7.Function.*;
import static swordofmagic7.SomCore.plugin;

public class KingSlime {

    public static final String SummonQuestFailed = "§c§l《召喚クエスト失敗》";

    private static final Location EventLocation = new Location(world, 4178, 99, 617);
    private static EnemyData Enemy;
    public static int Time;
    public static int StartTime = 1800;
    public static Set<Player> Players = new HashSet<>();

    public static void trigger(PlayerData playerData, ItemParameter item) {
        if (SomCore.isDevEventServer()) {
            if (playerData.player.getLocation().distance(EventLocation) < 32) {
                if (Enemy == null || !Enemy.isAlive()) {
                    Time = StartTime;
                    playerData.ItemInventory.removeItemParameter(item, 1);
                    TextView textView = new TextView("§b[" + ServerId + "] " + playerData.getNick() + "§aさんが");
                    textView.addView(item.getTextView(1, playerData.ViewFormat())).addText("§aを使用しました");
                    textView.setSound(SoundList.DungeonTrigger);
                    Client.BroadCast(textView);
                    Enemy = MobManager.mobSpawn(DataBase.getMobData("キングスライム"), 50, EventLocation);
                    MultiThread.TaskRun(() -> {
                        Set<Player> list = PlayerList.getNear(EventLocation, Radius);
                        while (Time > 0 && Enemy.isAlive() && list.size() > 0 && plugin.isEnabled()) {
                            list = PlayerList.getNear(EventLocation, Radius);
                            Players.addAll(list);
                            Time--;
                            List<String> textData = new ArrayList<>();
                            textData.add(decoText("§c§l召喚ボスクエスト"));
                            textData.add(decoLore("ボス体力") + String.format("%.0f", Enemy.Health));
                            textData.add(decoLore("残り時間") + Time + "秒");
                            ViewBar.setSideBar(Players, "KingSlime", textData);
                            MultiThread.sleepTick(20);
                        }
                        ViewBar.resetSideBar(Players, "KingSlime");
                        if (Enemy.isDead()) {
                            Client.BroadCast(new TextView("§b[" + ServerId + "] §cキングスライム§aが§c討伐§aされました").setSound(SoundList.DungeonTrigger));
                        } else {
                            Enemy.delete();
                            Message(Players, SummonQuestFailed, "", null, SoundList.DungeonTrigger);
                        }
                        Players.clear();
                    }, "KingSlime");
                } else {
                    sendMessage(playerData.player, "§aすでに開始されています", SoundList.Nope);
                }
            } else {
                sendMessage(playerData.player, "§e[ベア湧水地]§aの§e魔結晶§aの付近で使用できます", SoundList.Nope);
            }
        } else {
            sendMessage(playerData.player, "§e[Eventチャンネル]§aでのみ使用できます", SoundList.Nope);
        }
    }
}

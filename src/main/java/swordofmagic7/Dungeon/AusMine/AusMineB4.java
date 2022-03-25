package swordofmagic7.Dungeon.AusMine;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import swordofmagic7.Dungeon.Dungeon;
import swordofmagic7.Mob.EnemyData;
import swordofmagic7.Mob.MobManager;
import swordofmagic7.MultiThread.MultiThread;
import swordofmagic7.PlayerList;
import swordofmagic7.Sound.SoundList;
import swordofmagic7.ViewBar.ViewBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static swordofmagic7.Data.DataBase.getMobData;
import static swordofmagic7.Data.DataBase.getWarpGate;
import static swordofmagic7.Dungeon.Dungeon.*;
import static swordofmagic7.Function.decoLore;
import static swordofmagic7.Function.decoText;

public class AusMineB4 {
    private static final Location EventLocation = new Location(world,704, 119, 1979);
    private static boolean Start = false;
    public static int Time;
    public static int StartTime = 500;
    private static EnemyData Enemy;
    private static Set<Player> Players = new HashSet<>();
    private static final double Radius = Dungeon.Radius*2;
    public static float SkillTime = -1;
    private static final String[] ClearText = new String[]{
            "§cグリフィア§aを討伐しました！",
            "§eスニーク§aを続けると§e退場§aします"};
    public static boolean Start() {
        if (!Start) {
            Start = true;
            MultiThread.TaskRunSynchronized(() -> {
                Enemy = MobManager.mobSpawn(getMobData("グリフィア"), 25, EventLocation);
                MultiThread.TaskRun(() -> {
                    Time = StartTime;
                    Players = PlayerList.getNear(EventLocation, Radius);
                    Set<Player> list = PlayerList.getNear(EventLocation, Radius);
                    Message(Players, DungeonQuestTrigger, "§cグリフィア§aを討伐せよ", null, SoundList.DungeonTrigger);
                    while (Time > 0 && Enemy.isAlive() && list.size() > 0) {
                        list = PlayerList.getNear(EventLocation, Radius);
                        Players.addAll(list);
                        Time--;
                        for (int i = 0; i < 10; i++) {
                            List<String> textData = new ArrayList<>();
                            textData.add(decoText("§c§lダンジョンクエスト"));
                            textData.add(decoLore("ボス体力") + String.format("%.0f", Enemy.Health) + " (" + String.format("%.0f", Enemy.Health / Enemy.MaxHealth *100) + "%)");
                            if (SkillTime > -1)
                                textData.add(decoLore("スキル詠唱") + String.format("%.0f", SkillTime * 100) + "%");
                            textData.add(decoLore("残り時間") + Time + "秒");
                            ViewBar.setSideBar(Players, "AusMineB4", textData);
                            MultiThread.sleepTick(2);
                        }
                    }
                    ViewBar.resetSideBar(Players, "AusMineB4");
                    if (Enemy.isDead()) {
                        Message(Players, DungeonQuestClear, ClearText[1], ClearText, SoundList.LevelUp);
                        MultiThread.sleepTick(100);
                        MultiThread.TaskRunSynchronized(() -> {
                            for (Player player : PlayerList.getNear(EventLocation, Radius)) {
                                if (!player.isSneaking()) {
                                    player.teleportAsync(getWarpGate("AusMineB4_to_AusMineB4Boss").Location);
                                } else {
                                    player.teleportAsync(getWarpGate("AusForest_to_AusMineB1").Location);
                                }
                            }
                        });
                    } else {
                        Enemy.delete();
                        Message(Players, DungeonQuestFailed, "", null, SoundList.DungeonTrigger);
                    }
                    Players.clear();
                    Start = false;
                }, "AusMineB4DungeonQuest");
            });
        }
        return false;
    }
}
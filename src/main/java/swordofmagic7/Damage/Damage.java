package swordofmagic7.Damage;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import swordofmagic7.Attribute.AttributeType;
import swordofmagic7.Data.PlayerData;
import swordofmagic7.Data.Type.DamageLogType;
import swordofmagic7.Mob.EnemyData;
import swordofmagic7.Mob.MobManager;
import swordofmagic7.Pet.PetManager;
import swordofmagic7.Pet.PetParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static swordofmagic7.Data.PlayerData.playerData;
import static swordofmagic7.Function.Log;
import static swordofmagic7.System.BTTSet;
import static swordofmagic7.System.plugin;

public final class Damage {

    private static final Random random = new Random();

    public static void makeHeal(Player healer, Player victim, double healMultiply) {
        PlayerData healerData = playerData(healer);
        PlayerData victimData = playerData(victim);
        double heal = healerData.Status.HLP * healMultiply;
        victimData.changeHealth(heal);
        String Text = "§b≫§e" + String.format("%.1f", heal);
        String M = " §f[M:" + String.format("%.0f", healMultiply*100) + "%]";
        String SPI = " §b[SPI:" + healerData.Attribute.getAttribute(AttributeType.SPI) + "]";
        String HLP = " §e[HPL:" + String.format("%.0f", healerData.Status.HLP) + "]";
        if (healerData.DamageLog.isDamageOnly()) {
            String healText = Text;
            if (healerData.DamageLog.isDetail()) {
                healText += M + SPI + HLP;
            }
            healer.sendMessage(healText + " -> " + victimData.Nick);
        }
        if (healer != victim && victimData.DamageLog.isDetail()) {
            String healText = Text;
            if (healerData.DamageLog.isDetail()) {
                healText += M + SPI + HLP;
            }
            victim.sendMessage(healText + " <- " + healerData.Nick);
        }
    }

    public static void makeDamage(LivingEntity attacker, List<LivingEntity> victims, DamageCause damageCause, String damageSource, double damageMultiply, int count, int wait) {
        BTTSet(new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i < victims.size()) {
                    makeDamage(attacker, victims.get(i), damageCause, damageSource, damageMultiply, count);
                }
                i++;
            }
        }.runTaskTimerAsynchronously(plugin, 0, wait), "makeDamage");
    }

    public static void makeDamage(LivingEntity attacker, LivingEntity victim, DamageCause damageCause, String damageSource, double damageMultiply, int count) {
        if (victim.isDead()) {
            return;
        }

        double ATK;
        double DEF;
        double ACC;
        double EVA;
        double CriticalRate;
        double CriticalResist;
        double baseDamage;
        double hitRate;
        double criRate;
        double damage = 0;

        double Attack;
        double Defence;
        double victimMaxHealth = 0;
        double victimHealth = 0;
        double Multiply = 1;
        double CriticalMultiply = 1.2;
        double Resistance = 1;

        LivingEntity finalVictim = victim;
        Bukkit.getScheduler().runTask(plugin, () -> {
            finalVictim.damage(0);
        });

        if (attacker instanceof Player player) {
            PlayerData playerData = playerData(player);
            ATK = playerData.Status.ATK;
            ACC = playerData.Status.ACC;
            CriticalRate = playerData.Status.CriticalRate;
            CriticalMultiply = playerData.Status.CriticalMultiply;
            Multiply = playerData.Status.DamageCauseMultiply.get(damageCause);
        } else if (MobManager.isEnemy(attacker)) {
            EnemyData enemyData = MobManager.EnemyTable(attacker.getUniqueId());
            ATK = enemyData.ATK;
            ACC = enemyData.ACC;
            CriticalRate = enemyData.CriticalRate;
        } else if (PetManager.isPet(attacker)) {
            PetParameter petParameter = PetManager.PetParameter(attacker);
            ATK = petParameter.ATK;
            ACC = petParameter.ACC;
            CriticalRate = petParameter.CriticalRate;
            petParameter.DecreaseStamina(1, 0.1);
        } else return;

        if (victim instanceof Player player) {
            if (!player.isOnline()) return;
            PlayerData playerData = playerData(player);
            DEF = playerData.Status.DEF;
            EVA = playerData.Status.EVA;
            CriticalResist = playerData.Status.CriticalResist;
            Resistance = playerData.Status.DamageCauseResistance.get(damageCause);
        } else if (MobManager.isEnemy(victim)) {
            EnemyData enemyData = MobManager.EnemyTable(victim.getUniqueId());
            DEF = enemyData.DEF;
            EVA = enemyData.EVA;
            CriticalResist = enemyData.CriticalResist;
        } else if (PetManager.isPet(victim)) {
            PetParameter petParameter = PetManager.PetParameter(victim);
            DEF = petParameter.DEF;
            EVA = petParameter.EVA;
            CriticalResist = petParameter.CriticalResist;
            petParameter.DecreaseStamina(1, 0.7);
        } else {
            return;
        }

        baseDamage = (Math.pow(ATK, 2) / (ATK + DEF * 2));
        baseDamage *= damageMultiply;
        baseDamage *= Multiply;
        baseDamage /= Resistance;
        hitRate = (Math.pow(ACC, 2) / (ACC + EVA/2)) / ACC;
        criRate = (Math.pow(CriticalRate, 2) / (CriticalRate + CriticalResist/2)) / CriticalRate;
        Attack = ATK;
        Defence = DEF;

        if ((attacker instanceof Player || PetManager.isPet(victim)) && victim instanceof Player) {
            baseDamage /= 2;
        }

        int hitCount = 0;
        int criCount = 0;
        for (int i = 0; i < count; i++) {
            if (random.nextDouble() <= hitRate) {
                if (random.nextDouble() <= criRate) {
                    criCount++;
                    damage += baseDamage * CriticalMultiply;
                    randomHologram("§b§l❤" + String.format("%.1f", baseDamage * CriticalMultiply), victim.getEyeLocation());
                } else {
                    hitCount++;
                    damage += baseDamage;
                    randomHologram("§c§l❤" + String.format("%.1f", baseDamage), victim.getEyeLocation());
                }
            } else {
                randomHologram("§7§lMiss [" + String.format("%.0f", hitRate * 100) + "%]", victim.getEyeLocation());
            }
        }

        String damageText;
        if (hitCount + criCount > 0) {
            damageText = "§e" + String.format("%.1f", baseDamage) + "§ax" + hitCount + " §b" + String.format("%.1f", baseDamage * CriticalMultiply) + "§ax" + criCount;
        } else damageText = "§7Miss";

        boolean victimDead = false;
        if (victim instanceof Player player) {
            PlayerData playerData = playerData(player);
            if (playerData.Status.Health - damage > 0) {
                playerData.Status.Health -= damage;
            } else {
                victimDead = true;
                playerData.dead();
                if (attacker instanceof Player player2) {
                    PlayerData playerData2 = playerData(player2);
                    playerData2.Status.Health += playerData.Status.MaxHealth/5;
                }
            }

            victimMaxHealth = playerData.Status.MaxHealth;
            victimHealth = playerData.Status.Health;
        } else if (MobManager.isEnemy(victim)) {
            EnemyData enemyData = MobManager.EnemyTable(victim.getUniqueId());
            enemyData.Health -= damage;
            enemyData.addPriority(attacker, damage);
            if (enemyData.Health > 0) {
                victim.setHealth(enemyData.Health);
            } else {
                victimDead = true;
                enemyData.dead();
            }
            victimMaxHealth = enemyData.MaxHealth;
            victimHealth = enemyData.Health;
        } else if (PetManager.isPet(victim)) {
            PetParameter petStatus = PetManager.PetParameter(victim);
            if (petStatus.Health - damage > 0) {
                petStatus.Health -= damage;
            } else {
                victimDead = true;
                petStatus.dead();
            }
            victimMaxHealth = petStatus.MaxHealth;
            victimHealth = petStatus.Health;
        }

        if (PetManager.isPet(attacker)) {
            PetParameter pet = PetManager.PetParameter(attacker);
            attacker = pet.player;
            if (victimDead) {
                pet.target = null;
            }
        }
        if (PetManager.isPet(victim)) {
            victim = PetManager.PetParameter(victim).player;
        }

        final String M = " §f[M:" + String.format("%.0f", damageMultiply * 100) + "%]";
        final String HP = " §c[HP:" + String.format("%.0f", victimHealth) + "/" + String.format("%.0f", victimMaxHealth) + "]";
        final String AD = " §e[AD:" + String.format("%.1f", Attack) + "/" + String.format("%.1f", Defence) + "]";
        final String HR = " §e[HR:" + String.format("%.0f", hitRate * 100) + "%]";
        final String CR = " §b[CR:" + String.format("%.0f", criRate * 100) + "%]";
        final String R = " §b[R:" + String.format("%.1f", (1 - (1 / Resistance)) * 100) + "%]";
        if (attacker instanceof Player player) {
            DamageLogType DamageLog = playerData(player).DamageLog;
            if (DamageLog.isDamageOnly()) {
                String damageLog = "§a≫" + damageText;
                if (DamageLog.isDetail()) {
                    damageLog += M + HP;
                    if (DamageLog.isAll()) {
                        damageLog += AD + HR + CR + R;
                    }
                }
                player.sendMessage(damageLog);
            }
        }

        if (victim instanceof Player player) {
            DamageLogType DamageLog = playerData(player).DamageLog;
            if (DamageLog.isDamageOnly()) {
                String damageLog = "§c≪" + damageText;
                if (DamageLog.isDetail()) {
                    damageLog += M + HP;
                    if (DamageLog.isAll()) {
                        damageLog += AD + HR + CR + R;
                    }
                }
                player.sendMessage(damageLog);
            }
        }
    }

    static void randomHologram(String string, Location loc) {
        randomHologram(string, loc, new ArrayList<>());
    }

    static void randomHologram(String string, Location loc, List<Player> players) {
        Random random = new Random();
        double x = random.nextDouble() * 2 - 1;
        double y = random.nextDouble() + 1;
        double z = random.nextDouble() * 2 - 1;
        Bukkit.getScheduler().runTask(plugin, () -> {
            Hologram hologram = HologramsAPI.createHologram(plugin, loc);
            loc.add(x, y, z);
            VisibilityManager visibilityManager = hologram.getVisibilityManager();
            if (players.size() > 0) {
                visibilityManager.setVisibleByDefault(false);
                for (Player player : players) {
                    visibilityManager.showTo(player);
                }
            }
            hologram.appendTextLine(string);
            hologram.teleport(loc);
            Bukkit.getScheduler().runTaskLater(plugin, hologram::delete, 20);
        });
    }
}

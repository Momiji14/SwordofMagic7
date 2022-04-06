package swordofmagic7.Mob;

import com.destroystokyo.paper.entity.Pathfinder;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import swordofmagic7.Classes.ClassData;
import swordofmagic7.Classes.Classes;
import swordofmagic7.Client;
import swordofmagic7.Damage.Damage;
import swordofmagic7.Damage.DamageCause;
import swordofmagic7.Data.PlayerData;
import swordofmagic7.Effect.*;
import swordofmagic7.Function;
import swordofmagic7.Item.RuneParameter;
import swordofmagic7.MultiThread.MultiThread;
import swordofmagic7.Particle.ParticleData;
import swordofmagic7.Particle.ParticleManager;
import swordofmagic7.Pet.PetData;
import swordofmagic7.Pet.PetParameter;
import swordofmagic7.PlayerList;
import swordofmagic7.Quest.QuestData;
import swordofmagic7.Quest.QuestProcess;
import swordofmagic7.Quest.QuestReqContentKey;
import swordofmagic7.Sound.SoundList;
import swordofmagic7.Status.StatusParameter;
import swordofmagic7.TextView.TextView;

import java.util.*;

import static swordofmagic7.Data.DataBase.*;
import static swordofmagic7.Data.PlayerData.playerData;
import static swordofmagic7.Function.*;
import static swordofmagic7.SomCore.*;
import static swordofmagic7.Sound.CustomSound.playSound;

public class EnemyData {
    public UUID uuid;
    public LivingEntity entity;
    public MobData mobData;
    public int Level;
    public double MaxHealth;
    public double Health;
    public double ATK;
    public double DEF;
    public double ACC;
    public double EVA;
    public double CriticalRate;
    public double CriticalResist;
    public double Exp;
    public double ClassExp;

    public HashMap<StatusParameter, Double> MultiplyStatus = new HashMap<>();
    public HashMap<StatusParameter, Double> FixedStatus = new HashMap<>();
    public HashMap<DamageCause, Double> DamageCauseMultiply = new HashMap<>();
    public HashMap<DamageCause, Double> DamageCauseResistance = new HashMap<>();

    public final EnemySkillManager skillManager = new EnemySkillManager(this);
    public final EffectManager effectManager;
    public int HitCount = 0;

    private final Set<Player> Involved = new HashSet<>();
    private final HashMap<LivingEntity, Double> Priority = new HashMap<>();
    public Location SpawnLocation;
    public LivingEntity target;
    public Location overrideTargetLocation;
    public Location nonTargetLocation;
    public Location DefenseAI;
    public boolean isDefenseBattle = false;
    private boolean isDead = false;

    public boolean isDead() {
        return isDead || entity == null;
    }

    public boolean isAlive() {
        return !isDead && entity != null;
    }

    public EnemyData(LivingEntity entity, MobData baseData, int level) {
        this.entity = entity;
        mobData = baseData;
        Level = level;
        effectManager = new EffectManager(entity, EffectOwnerType.Enemy, this);

        statusUpdate();

        String DisplayName = getDecoDisplay();
        entity.setCustomNameVisible(true);
        entity.setCustomName(DisplayName);
        entity.setMaxHealth(MaxHealth);
        entity.setHealth(entity.getMaxHealth());
        if (mobData.disguise != null) {
            Disguise disguise = mobData.disguise.clone();
            disguise.setEntity(entity);
            disguise.setDisguiseName(DisplayName);
            disguise.setDynamicName(true);
            disguise.setCustomDisguiseName(true);
            disguise.startDisguise();
        }
        Health = MaxHealth;
        entity.setNoDamageTicks(0);
        uuid = entity.getUniqueId();
    }

    public static double StatusMultiply(int level) {
        return Math.pow(0.74+(level/3f), 1.45);
    }

    public void statusUpdate() {
        double multiply = StatusMultiply(Level);
        double multiply2 = Level >= 30 ? Math.pow(multiply, 1.1) : multiply;
        double multiply3 = Level >= 30 ? Math.pow(multiply, 1.04) : multiply;

        HashMap<StatusParameter, Double> baseMultiplyStatusRev = new HashMap<>();
        HashMap<StatusParameter, Double> multiplyStatusRev = new HashMap<>();
        for (StatusParameter param : StatusParameter.values()) {
            MultiplyStatus.put(param, 1d);
            FixedStatus.put(param, 0d);
            multiplyStatusRev.put(param, 1d);
            baseMultiplyStatusRev.put(param, 1d);
        }
        for (DamageCause cause : DamageCause.values()) {
            DamageCauseMultiply.put(cause, 1d);
            DamageCauseResistance.put(cause, 1d);
        }
        if (effectManager.Effect.size() > 0) {
            for (Map.Entry<EffectType, EffectData> data : effectManager.Effect.entrySet()) {
                EffectType effectType = data.getKey();
                EffectData effectData = data.getValue();
                for (StatusParameter param : StatusParameter.values()) {
                    double multiplyStatusAdd = EffectDataBase.EffectStatus(effectType).MultiplyStatus.getOrDefault(param, 0d) * effectData.stack;
                    double baseMultiplyStatusAdd = EffectDataBase.EffectStatus(effectType).BaseMultiplyStatus.getOrDefault(param, 0d) * effectData.stack;
                    if (multiplyStatusAdd >= 0) MultiplyStatus.merge(param, multiplyStatusAdd, Double::sum);
                    else {
                        multiplyStatusRev.put(param, multiplyStatusRev.get(param) * Math.pow(1 + multiplyStatusAdd, effectData.stack));
                    }
                    if (baseMultiplyStatusAdd >= 0) MultiplyStatus.merge(param, baseMultiplyStatusAdd, Double::sum);
                    else {
                        baseMultiplyStatusRev.put(param, baseMultiplyStatusRev.get(param) * Math.pow(1 + baseMultiplyStatusAdd, effectData.stack));
                    }
                }
                for (DamageCause cause : DamageCause.values()) {
                    DamageCauseMultiply.merge(cause, EffectDataBase.EffectStatus(effectType).DamageCauseMultiply.getOrDefault(cause, 0d) * effectData.stack, Double::sum);
                    DamageCauseResistance.merge(cause, EffectDataBase.EffectStatus(effectType).DamageCauseResistance.getOrDefault(cause, 0d) * effectData.stack, Double::sum);
                }
            }
            for (StatusParameter param : StatusParameter.values()) {
                MultiplyStatus.put(param, MultiplyStatus.get(param) * multiplyStatusRev.get(param));
                MultiplyStatus.put(param, MultiplyStatus.get(param) * baseMultiplyStatusRev.get(param));
            }
        }

        MaxHealth = mobData.Health * multiply2 * statusMultiply(StatusParameter.MaxHealth);
        ATK = mobData.ATK * multiply * statusMultiply(StatusParameter.ATK);
        DEF = mobData.DEF * multiply3 * statusMultiply(StatusParameter.DEF);
        ACC = mobData.ACC * multiply * statusMultiply(StatusParameter.ACC);
        EVA = mobData.EVA * multiply * statusMultiply(StatusParameter.EVA);
        CriticalRate = mobData.CriticalRate * multiply * statusMultiply(StatusParameter.CriticalRate);
        CriticalResist = mobData.CriticalResist * multiply3 * statusMultiply(StatusParameter.CriticalResist);
        Exp = mobData.Exp * multiply;
        ClassExp = mobData.Exp;
    }

    private double statusMultiply(StatusParameter status) {
        return MultiplyStatus.getOrDefault(status, 1d);
    }

    public String getDecoDisplay() {
        return "§c§l《" + mobData.Display + " Lv" + Level + "》";
    }

    private final java.util.function.Predicate<LivingEntity> Predicate = entity -> entity.getType() == EntityType.PLAYER;

    private boolean runAITask = true;

    void stopAI() {
        runAITask = false;
        if (asyncAITask != null) asyncAITask.cancel();
        if (syncAITask != null) syncAITask.cancel();
    }

    public boolean isRunnableAI() {
        return runAITask && isAlive() && plugin.isEnabled();
    }

    public Location LastLocation;
    private Location NextLocation;
    public BukkitTask asyncAITask;
    public BukkitTask syncAITask;
    void runAI() {
        stopAI();
        SpawnLocation = entity.getLocation();
        LastLocation = SpawnLocation;
        if (entity instanceof Mob mob) {
            Pathfinder pathfinder = mob.getPathfinder();
            runAITask = true;
            syncAITask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!isRunnableAI()) this.cancel();
                    if (NextLocation != null && entity.getLocation().distance(NextLocation) > mobData.Reach) {
                        mob.lookAt(NextLocation);
                        pathfinder.moveTo(NextLocation, mobData.Mov);
                        LastLocation = entity.getLocation();
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
            if (DefenseAI != null) {
                NextLocation = DefenseAI;
            } else {
                asyncAITask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!isRunnableAI()) this.cancel();
                        Location targetLocation = null;
                        if (overrideTargetLocation != null) {
                            targetLocation = overrideTargetLocation;
                        } else if (target != null) {
                            targetLocation = target.getLocation();
                        } else if (nonTargetLocation != null) {
                            targetLocation = nonTargetLocation;
                        }
                        if (targetLocation != null) {
                            NextLocation = targetLocation;
                        }

                        double topPriority = 0;
                        Priority.entrySet().removeIf(entry -> (
                                entry.getKey() instanceof Player player && !Function.isAlive(player))
                                || entry.getKey().getLocation().distance(entity.getLocation()) > mobData.Search
                                || entry.getKey().isDead());
                        for (Map.Entry<LivingEntity, Double> priority : Priority.entrySet()) {
                            double priorityValue = priority.getValue();
                            LivingEntity priorityTarget = priority.getKey();
                            if (priorityTarget instanceof Player player) {
                                PlayerData targetData = playerData(player);
                                if (targetData.EffectManager.hasEffect(EffectType.Teleportation)) {
                                    priorityValue = 0;
                                    Priority.put(priority.getKey(), 0d);
                                }
                                if (targetData.EffectManager.hasEffect(EffectType.Covert)) {
                                    priorityValue = 0;
                                    if (target == player) target = null;
                                }
                                if (targetData.EffectManager.hasEffect(EffectType.HatePriority)) {
                                    target = player;
                                    break;
                                }
                            }
                            if (topPriority < priorityValue) {
                                target = priorityTarget;
                                topPriority = priorityValue;
                            }
                        }

                        if (target == null && mobData.Hostile) {
                            Set<Player> Targets = PlayerList.getNear(entity.getLocation(), mobData.Search);
                            int topLevel = 0;
                            LivingEntity target = null;
                            for (Player player : Targets) {
                                PlayerData playerData = playerData(player);
                                if (playerData.Level > topLevel) {
                                    topLevel = playerData.Level;
                                    target = player;
                                }
                            }
                            if (target != null) Priority.put(target, 1d);
                        }

                        if (target != null) {
                            if (target instanceof Player player && !Function.isAlive(player)) {
                                Priority.remove(target);
                                target = null;
                            } else {
                                skillManager.tickSkillTrigger();
                                final Location TargetLocation = target.getLocation();
                                final Location EntityLocation = entity.getLocation();
                                double x1 = EntityLocation.getX();
                                double y1 = EntityLocation.getY();
                                double z1 = EntityLocation.getZ();
                                double x2 = TargetLocation.getX();
                                double y2 = TargetLocation.getY();
                                double z2 = TargetLocation.getZ();
                                if (Math.abs(x1 - x2) <= mobData.Reach && Math.abs(z1 - z2) <= mobData.Reach && Math.abs(y1 - y2) < 16) {
                                    Damage.makeDamage(entity, target, DamageCause.ATK, "attack", 1, 1);
                                }
                            }
                        }
                        if (!isDefenseBattle) {
                            if (PlayerList.getNear(entity.getLocation(), 48).size() == 0 || SpawnLocation.distance(entity.getLocation()) > mobData.Search + 64) {
                                delete();
                            }
                        }
                    }
                }.runTaskTimerAsynchronously(plugin, 0, 20);
            }
        }
    }

    private final Map<LivingEntity, Double> TotalDamageTable = new HashMap<>();
    public void addPriority(LivingEntity entity, double addPriority) {
        Priority.put(entity, Priority.getOrDefault(entity, 0d) + addPriority);
        TotalDamageTable.put(entity, TotalDamageTable.getOrDefault(entity, 0d) + addPriority);
    }

    public void resetPriority() {
        Priority.clear();
    }

    public void delete() {
        isDead = true;
        stopAI();
        if (entity != null) MobManager.EnemyTable.remove(entity.getUniqueId().toString());
        MultiThread.TaskRunSynchronized(() -> {
            entity.setHealth(0);
            entity.remove();
        }, "EnemyDelete");
    }

    public static int decayExp(int exp, int playerLevel, int mobLevel) {
        if (playerLevel < mobLevel + 20) {
            return exp;
        } else if (playerLevel > mobLevel + 20 && mobLevel + 40 < playerLevel) {
            double decay = ((mobLevel + 40) - playerLevel)/20f;
            return (int) Math.max(Math.round(exp * decay), 1);
        } else {
            return 1;
        }
    }

    public synchronized void dead() {
        if (isDead) return;
        if (entity != null) {
            ParticleManager.RandomVectorParticle(new ParticleData(Particle.FIREWORKS_SPARK, 0.22f), entity.getLocation(), 100);
            playSound(entity.getLocation(), SoundList.Death);
            Involved.addAll(PlayerList.getNear(entity.getLocation(), 32));
        }
        delete();

        if (mobData.enemyType.isBoss()) {
            List<Map.Entry<LivingEntity, Double>> entries = new ArrayList<>(TotalDamageTable.entrySet());
            entries.sort((obj1, obj2) -> obj2.getValue().compareTo(obj1.getValue()));
            List<String> message = new ArrayList<>();
            message.add(decoText("ダメージランキング"));
            int i = 1;
            for (Map.Entry<LivingEntity, Double> entry : entries) {
                message.add("§7・§e" + i + "位§7: §e" + entry.getKey().getName() + " §b-> §c" + String.format("%.0f", entry.getValue()));
                i++;
                if (i > 5) break;
            }
            for (Player player : PlayerList.getNear(entity.getLocation(), 32)) {
                if (player.isOnline()) {
                    sendMessage(player, message, SoundList.Tick);
                }
            }
        }

        int exp = (int) Math.floor(Exp);
        int classExp = (int) Math.floor(ClassExp);
        List<DropItemData> DropItemTable = new ArrayList<>(mobData.DropItemTable);
        DropItemTable.add(new DropItemData(getItemParameter("生命の雫"), 0.0001));
        DropItemTable.add(new DropItemData(getItemParameter("強化石"), 0.05));
        for (Player player : Involved) {
            if (player.isOnline()) {
                PlayerData playerData = playerData(player);
                playerData.statistics.enemyKill(mobData);
                Classes classes = playerData.Classes;
                List<ClassData> classList = new ArrayList<>();
                for (ClassData classData : classes.classSlot) {
                    if (classData != null && !classData.ProductionClass) {
                        classList.add(classData);
                    }
                }
                playerData.addPlayerExp(decayExp(exp, playerData.Level, Level));
                for (ClassData classData : classList) {
                    classes.addClassExp(classData, classExp);
                }
                for (PetParameter pet : playerData.PetSummon) {
                    pet.addExp(decayExp(exp, pet.Level, Level));
                }
                List<String> Holo = new ArrayList<>();
                Holo.add("§e§lEXP §a§l+" + exp);
                if (!isDefenseBattle) {
                    for (DropItemData dropData : DropItemTable) {
                        if ((dropData.MinLevel == 0 && dropData.MaxLevel == 0) || (dropData.MinLevel <= Level && Level <= dropData.MaxLevel)) {
                            if (random.nextDouble() <= dropData.Percent) {
                                int amount;
                                if (dropData.MaxAmount != dropData.MinAmount) {
                                    amount = random.nextInt(dropData.MaxAmount - dropData.MinAmount) + dropData.MinAmount;
                                } else {
                                    amount = dropData.MinAmount;
                                }
                                playerData.ItemInventory.addItemParameter(dropData.itemParameter.clone(), amount);
                                Holo.add("§b§l[+]§e§l" + dropData.itemParameter.Display + "§a§lx" + amount);
                                if (playerData.DropLog.isItem()) ItemGetLog(player, dropData.itemParameter, amount);
                                if ((dropData.Percent <= 0.01 && mobData.enemyType.isBoss()) || (dropData.Percent <= 0.001 && mobData.enemyType.isNormal())) {
                                    TextView text = new TextView(playerData.getNick() + "§aさんが");
                                    text.addView(dropData.itemParameter.getTextView(amount, playerData.ViewFormat()));
                                    text.addText("§aを§e獲得§aしました");
                                    text.setSound(SoundList.Tick);
                                    Client.BroadCast(text);
                                }
                            }
                        }
                    }
                    for (DropRuneData dropData : mobData.DropRuneTable) {
                        if ((dropData.MinLevel == 0 && dropData.MaxLevel == 0) || (dropData.MinLevel <= Level && Level <= dropData.MaxLevel)) {
                            if (random.nextDouble() <= dropData.Percent) {
                                RuneParameter runeParameter = dropData.runeParameter.clone();
                                runeParameter.Quality = random.nextDouble();
                                runeParameter.Level = Level;
                                if (playerData.RuneQualityFilter <= runeParameter.Quality) {
                                    playerData.RuneInventory.addRuneParameter(runeParameter);
                                    Holo.add("§b§l[+]§e§l" + runeParameter.Display);
                                    if (playerData.DropLog.isRune()) {
                                        player.sendMessage("§b[+]§e" + runeParameter.Display + " §e[レベル:" + Level + "] [品質:" + String.format(playerData.ViewFormat(), runeParameter.Quality * 100) + "%]");
                                    }
                                } else {
                                    playerData.ItemInventory.addItemParameter(playerData.RuneShop.RunePowder, 1);
                                    Holo.add("§b§l[+]§e§l" + playerData.RuneShop.RunePowder.Display);
                                    if (playerData.DropLog.isItem()) {
                                        ItemGetLog(player, playerData.RuneShop.RunePowder, 1);
                                    }
                                }
                            }
                        }
                    }
                    for (Map.Entry<QuestData, QuestProcess> data : playerData.QuestManager.QuestList.entrySet()) {
                        QuestData questData = data.getKey();
                        if (questData.type.isEnemy()) {
                            for (Map.Entry<QuestReqContentKey, Integer> reqContent : questData.ReqContent.entrySet()) {
                                QuestReqContentKey key = reqContent.getKey();
                                if (key.mainKey.equalsIgnoreCase(mobData.Id) && Level >= key.intKey[0]) {
                                    playerData.QuestManager.processQuest(questData, key, 1);
                                }
                            }
                        }
                    }
                    if (playerData.Skill.hasSkill("Pleasure") && getPetList().containsKey(mobData.Id)) {
                        PetData petData = getPetData(mobData.Id);
                        if (petData.BossPet) {
                            if (random.nextDouble() <= 0.0005) {
                                PetParameter pet = new PetParameter(player, playerData, petData, Level, PlayerData.MaxLevel, 0, 2);
                                playerData.PetInventory.addPetParameter(pet);
                                TextView text = new TextView(playerData.getNick() + "§aさんが§e[" + mobData.Id + "]§aを§b懐柔§aしました");
                                text.setSound(SoundList.Tick);
                                Client.BroadCast(text);
                            }
                        } else {
                            if (random.nextDouble() <= 0.01) {
                                PetParameter pet = new PetParameter(player, playerData, petData, Level, Math.min(Level + 10, PlayerData.MaxLevel), 0, random.nextDouble() + 0.5);
                                playerData.PetInventory.addPetParameter(pet);
                                Function.sendMessage(player, "§e[" + mobData.Id + "]§aを§b懐柔§aしました", SoundList.Tick);
                            }
                        }
                    }
                    Location loc = entity.getLocation().clone().add(0, 1 + Holo.size() * 0.25, 0);
                    MultiThread.TaskRunSynchronized(() -> {
                        Hologram hologram = createHologram("DropHologram:" + UUID.randomUUID(), loc);
                        VisibilityManager visibilityManager = hologram.getVisibilityManager();
                        visibilityManager.setVisibleByDefault(false);
                        visibilityManager.showTo(player);
                        for (String holo : Holo) {
                            hologram.appendTextLine(holo);
                        }
                        MultiThread.TaskRunSynchronizedLater(hologram::delete, 50, "EnemyKillRewardHoloDelete");
                        playerData.viewUpdate();
                    }, "EnemyKillRewardHolo");
                }
            }
        }
    }
}

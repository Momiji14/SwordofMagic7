package swordofmagic7.Data;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import swordofmagic7.Attribute.Attribute;
import swordofmagic7.Attribute.AttributeType;
import swordofmagic7.Classes.ClassData;
import swordofmagic7.Classes.Classes;
import swordofmagic7.Data.Type.DamageLogType;
import swordofmagic7.Data.Type.DropLogType;
import swordofmagic7.Data.Type.StrafeType;
import swordofmagic7.Data.Type.ViewInventoryType;
import swordofmagic7.Effect.EffectManager;
import swordofmagic7.Effect.EffectOwnerType;
import swordofmagic7.Equipment.Equipment;
import swordofmagic7.Equipment.EquipmentSlot;
import swordofmagic7.HotBar.HotBar;
import swordofmagic7.HotBar.HotBarData;
import swordofmagic7.InstantBuff.InstantBuff;
import swordofmagic7.Inventory.*;
import swordofmagic7.Item.ItemExtend.ItemPotionType;
import swordofmagic7.Item.ItemParameter;
import swordofmagic7.Item.ItemStackData;
import swordofmagic7.Item.RuneParameter;
import swordofmagic7.Item.Upgrade;
import swordofmagic7.Life.Gathering;
import swordofmagic7.Life.LifeStatus;
import swordofmagic7.Life.LifeType;
import swordofmagic7.Map.MapData;
import swordofmagic7.Map.MapManager;
import swordofmagic7.Menu.Menu;
import swordofmagic7.MultiThread.MultiThread;
import swordofmagic7.Party.PartyData;
import swordofmagic7.Pet.PetEvolution;
import swordofmagic7.Pet.PetManager;
import swordofmagic7.Pet.PetParameter;
import swordofmagic7.Quest.QuestManager;
import swordofmagic7.Shop.PetShop;
import swordofmagic7.Shop.RuneShop;
import swordofmagic7.Shop.Shop;
import swordofmagic7.Skill.CastType;
import swordofmagic7.Skill.Skill;
import swordofmagic7.Sound.SoundList;
import swordofmagic7.Status.Status;
import swordofmagic7.Title.TitleManager;
import swordofmagic7.Tutorial;
import swordofmagic7.ViewBar.SideBarToDo.SideBarToDo;
import swordofmagic7.ViewBar.ViewBar;

import java.io.File;
import java.util.*;

import static swordofmagic7.Classes.Classes.MaxSlot;
import static swordofmagic7.Classes.Classes.ReqExp;
import static swordofmagic7.Data.DataBase.*;
import static swordofmagic7.Function.*;
import static swordofmagic7.Sound.CustomSound.playSound;
import static swordofmagic7.System.createHologram;
import static swordofmagic7.System.plugin;
import static swordofmagic7.Title.TitleManager.DefaultTitle;

public class PlayerData {
    private static final HashMap<UUID, PlayerData> playerData = new HashMap<>();
    public static PlayerData playerData(Player player) {
        if (player.isOnline()) {
            if (!playerData.containsKey(player.getUniqueId())) {
                playerData.put(player.getUniqueId(), new PlayerData(player));
            }
            return playerData.get(player.getUniqueId());
        }
        Log("§c" + player.getName() + "§c, " + player.getUniqueId() + " is Offline or Npc", true);
        return new PlayerData(null);
    }
    public static HashMap<UUID, PlayerData> playerDataList() {
        return playerData;
    }


    public final Player player;
    public swordofmagic7.Inventory.ItemInventory ItemInventory;
    public HotBar HotBar;
    public swordofmagic7.Inventory.RuneInventory RuneInventory;
    public swordofmagic7.Inventory.PetInventory PetInventory;
    public Equipment Equipment;
    public Status Status;
    public Classes Classes;
    public Skill Skill;
    public Menu Menu;
    public Attribute Attribute;
    public EffectManager EffectManager;
    public Upgrade Upgrade;
    public Shop Shop;
    public RuneShop RuneShop;
    public LifeStatus LifeStatus;
    public PetManager PetManager;
    public PetShop PetShop;
    public PetEvolution PetEvolution;
    public MapManager MapManager;
    public Gathering Gathering;
    public QuestManager QuestManager;
    public ViewBar ViewBar;
    public SideBarToDo SideBarToDo;
    public Statistics statistics;
    public TitleManager titleManager;

    public String Nick;

    public int Level = 1;
    public int Exp = 0;

    public DamageLogType DamageLog = DamageLogType.None;
    public boolean ExpLog = false;
    public DropLogType DropLog = DropLogType.None;
    public boolean PvPMode = false;
    public boolean PlayMode = true;
    public StrafeType StrafeMode = StrafeType.DoubleJump;
    public CastType CastMode = CastType.Hold;
    public int Mel = 10000;
    public int ViewFormat = 0;
    public int Strafe = 2;
    public MapData Map = MapList.get("Alden");
    public boolean WallKicked = false;
    public BukkitTask WallKickedTask;
    public List<PetParameter> PetSummon = new ArrayList<>();
    public List<String> ActiveTeleportGate = new ArrayList<>();
    public HashMap<ItemPotionType, Integer> PotionCoolTime = new HashMap<>();
    public int useCookCoolTime = 0;
    public PetParameter PetSelect;
    public boolean isDead = false;
    public boolean RevivalReady = false;
    public boolean FishingDisplayNum = false;
    public boolean HoloSelfView = false;
    public PartyData Party;
    public InstantBuff instantBuff;
    public boolean isLoaded = false;

    public ViewInventoryType ViewInventory = ViewInventoryType.ItemInventory;

    PlayerData(Player player) {
        this.player = player;
        if (player == null) return;
        ItemInventory = new ItemInventory(player, this);
        HotBar = new HotBar(player, this);
        RuneInventory = new RuneInventory(player, this);
        PetInventory = new PetInventory(player, this);
        Equipment = new Equipment(player, this);
        Classes = new Classes(player, this);
        Skill = new Skill(player, this, plugin);
        Status = new Status(player, this, Classes, Skill);
        Menu = new Menu(player, this);
        Attribute = new Attribute(player, this);
        EffectManager = new EffectManager(player, EffectOwnerType.Player, this);
        Upgrade = new Upgrade(player, this);
        Shop = new Shop(player, this);
        RuneShop = new RuneShop(player, this);
        LifeStatus = new LifeStatus(player, this);
        PetManager = new PetManager(player, this);
        PetShop = new PetShop(player, this);
        PetEvolution = new PetEvolution(player, this);
        MapManager = new MapManager(player, this);
        Gathering = new Gathering(player, this);
        QuestManager = new QuestManager(player, this);
        ViewBar = new ViewBar(player, this, Status);
        SideBarToDo = new SideBarToDo(this);
        titleManager = new TitleManager(this);
        statistics = new Statistics(player, this);
        instantBuff = new InstantBuff(this);

        Nick = player.getName();

        PetInventory.start();

        InitializeHologram();
    }

    public Hologram hologram;
    public VisibilityManager visibilityManager;
    public TextLine[] hologramLine = new TextLine[3];
    public String holoTitle;
    public int HoloWait = 0;
    public int HoloAnim = 0;
    public void InitializeHologram() {
        MultiThread.TaskRunSynchronized(() -> {
            if (hologram != null && !hologram.isDeleted()) hologram.delete();
            hologram = createHologram(player.getName(), playerHoloLocation());
            visibilityManager = hologram.getVisibilityManager();
            if (!HoloSelfView) visibilityManager.hideTo(player);
            hologramLine[2] = hologram.appendTextLine(DefaultTitle.Display[0]);
            hologramLine[0] = hologram.appendTextLine("NameTag");
            hologramLine[1] = hologram.appendTextLine("HealthBar");
            MultiThread.TaskRun(() -> {
                while (plugin.isEnabled() && player.isOnline()) {
                    if (titleManager.Title.flame > 1) {
                        if (titleManager.Title.flame - 1 > HoloAnim) {
                            HoloWait++;
                            if (HoloWait > titleManager.Title.waitTick[HoloAnim]) {
                                HoloWait = 0;
                                HoloAnim++;
                            }
                        } else {
                            HoloWait++;
                            if (HoloWait > titleManager.Title.waitTick[HoloAnim]) {
                                HoloWait = 0;
                                HoloAnim = 0;
                            }
                        }
                        holoTitle = titleManager.Title.Display[HoloAnim];
                    } else {
                        holoTitle = titleManager.Title.Display[0];
                    }
                    MultiThread.sleepTick(1);
                }
            }, "PlayerHolo: " + player.getName());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        hologramLine[2].setText(holoTitle);
                        hologram.teleport(playerHoloLocation());
                    } else {
                        if (!hologram.isDeleted()) hologram.delete();
                        hologram = null;
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 1);
        }, "HologramInitialize: " + player.getName());
    }

    public Location playerHoloLocation() {
        Location loc = player.getEyeLocation().clone();
        loc.setY(loc.getY()+1.1);
        return loc;
    }

    public String getNick() {
        return getNick(false);
    }

    public String getNick(boolean bold) {
        String prefix = "§e";
        if (bold) prefix += "§l";
        return prefix + Nick;
    }

    public void changeViewFormat() {
        if (ViewFormat < 3) setViewFormat(ViewFormat+1);
        else setViewFormat(0);
    }

    public void DamageLog() {
        switch (DamageLog) {
            case None -> DamageLog(DamageLogType.DamageOnly);
            case DamageOnly -> DamageLog(DamageLogType.Detail);
            case Detail -> DamageLog(DamageLogType.All);
            case All -> DamageLog(DamageLogType.None);
        }
    }

    void DamageLog(DamageLogType bool) {
        DamageLog = bool;
        String msg = "§c[ダメージログ]§aを";
        msg += "§b[" + DamageLog.Display + "]";
        msg += "§aにしました";
        player.sendMessage(msg);
        playSound(player, SoundList.Click);
    }

    public void ExpLog() {
        ExpLog(!ExpLog);
    }

    void ExpLog(boolean bool) {
        ExpLog = bool;
        String msg = "§e[経験値ログ]§aを";
        if (bool) msg += "§b[有効]";
        else msg += "§c[無効]";
        msg += "§aにしました";
        player.sendMessage(msg);
        playSound(player, SoundList.Click);
    }

    public void DropLog() {
        switch (DropLog) {
            case None -> DropLog(DropLogType.All);
            case All -> DropLog(DropLogType.Item);
            case Item -> DropLog(DropLogType.Rune);
            case Rune -> DropLog(DropLogType.None);
        }
    }

    void DropLog(DropLogType bool) {
        DropLog = bool;
        String msg = "§e[ドロップログ]§aを";
        msg += "§b[" + DropLog.Display + "]";
        msg += "§aにしました";
        player.sendMessage(msg);
        playSound(player, SoundList.Click);
    }

    public void PvPMode() {
        PvPMode(!PvPMode);
    }

    void PvPMode(boolean bool) {
        PvPMode = bool;
        String msg = "§e[PvPモード]§aを";
        if (bool) msg += "§b[有効]";
        else msg += "§c[無効]";
        msg += "§aにしました";
        player.sendMessage(msg);
        playSound(player, SoundList.Click);
    }

    public void StrafeMode() {
        switch (StrafeMode) {
            case DoubleJump -> StrafeMode(StrafeType.AirDash);
            case AirDash -> StrafeMode(StrafeType.All);
            case All -> StrafeMode(StrafeType.DoubleJump);
        }
    }

    void StrafeMode(StrafeType mode) {
        StrafeMode = mode;
        String msg = "§e[ストレイフ条件]§aを";
        msg += "§b[" + StrafeMode.Display + "]";
        msg += "§aにしました";
        player.sendMessage(msg);
        playSound(player, SoundList.Click);
    }

    public void CastMode() {
        switch (CastMode) {
            case Renewed -> CastMode(CastType.Legacy);
            case Legacy -> CastMode(CastType.Hold);
            case Hold -> CastMode(CastType.Renewed);
        }
    }

    void CastMode(CastType bool) {
        CastMode = bool;
        String msg = "§e[キャストモード]§aを";
        msg += "§b[" + CastMode.Display + "]";
        msg += "§aにしました";
        player.sendMessage(msg);
        playSound(player, SoundList.Click);
    }

    public void FishingDisplayNum() {
        FishingDisplayNum(!FishingDisplayNum);
    }

    void FishingDisplayNum(boolean bool) {
        FishingDisplayNum = bool;
        String msg = "§e[釣獲コンボ表記]§aを";
        if (bool) msg += "§b[数字]";
        else msg += "§c[アルファベット]";
        msg += "§aにしました";
        player.sendMessage(msg);
        playSound(player, SoundList.Click);
    }

    public void FishingUseCombo() {
        FishingUseCombo(!Gathering.FishingUseCombo);
    }

    void FishingUseCombo(boolean bool) {
        if (Gathering.FishingInProgress) {
            player.sendMessage("§e釣獲中§aは切り替えできません");
            playSound(player, SoundList.Nope);
            return;
        }
        Gathering.FishingUseCombo = bool;
        String msg = "§e[釣獲モード]§aを";
        if (bool) msg += "§b[エンドレス]";
        else msg += "§c[タイムアタック]";
        msg += "§aにしました";
        player.sendMessage(msg);
        playSound(player, SoundList.Click);
    }

    public void HoloSelfView() {
        HoloSelfView(!HoloSelfView, true);
    }

    void HoloSelfView(boolean bool, boolean message) {
        HoloSelfView = bool;
        if (bool) visibilityManager.showTo(player);
        else visibilityManager.hideTo(player);
        if (message) {
            String msg = "§e[自視点ステータスバー]§aを";
            if (bool) msg += "§b[表示]";
            else msg += "§c[非表示]";
            msg += "§aにしました";
            player.sendMessage(msg);
            playSound(player, SoundList.Click);
        }
    }

    public String ViewFormat() {
        return "%." + ViewFormat + "f";
    }

    public void setViewFormat(int ViewFormat) {
        this.ViewFormat = ViewFormat;
        player.sendMessage("§e表記小数桁数§aを§e[" + ViewFormat + "桁]§aに§e設定§aしました");
        playSound(player, SoundList.Click);
        viewUpdate();
    }

    public String viewExpPercent() {
        return String.format("%.3f", (float)Exp/ ReqExp(Level) * 100);
    }

    public void remove() {
        playerData.remove(player.getUniqueId());
    }

    public void saveCloseInventory() {
        CloseInventory(player);
        Bukkit.getScheduler().runTaskLater(plugin, this::save, 1);
    }

    public void addPlayerLevel(int addLevel) {
        Level += addLevel;
        if (Level > MaxLevel) {
            Level = MaxLevel;
            Exp = 0;
        } else {
            BroadCast(getNick() + "§aさんが§eLv" + Level + "§aになりました");
            Attribute.addPoint(addLevel * 5);
            if (Level == MaxLevel) Exp = 0;
            playSound(player, SoundList.LevelUp);
        }
    }

    public static final int MaxLevel = 50;

    public void addPlayerExp(int addExp) {
        if (Level >= MaxLevel) {
            Exp = 0;
            addExp = 0;
        } else {
            Exp += addExp;
        }
        if (ReqExp(Level) <= Exp) {
            int addLevel = 0;
            while (ReqExp(Level) <= Exp) {
                Exp -= ReqExp(Level);
                addLevel++;
            }
            addPlayerLevel(addLevel);
        }
        if (ExpLog) player.sendMessage("§e経験値§7: §a+" + addExp);
    }

    public void save() {
        if (Tutorial.TutorialProcess.containsKey(player)) {
            player.sendMessage(Tutorial.TutorialNonSave);
            return;
        }
        if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING)  {
            player.closeInventory();
        }
        File playerFile = new File(DataBasePath, "PlayerData/" + player.getUniqueId() + ".yml");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error creating " + playerFile.getName() + "!");
            }
        }
        FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);

        Set<String> rollback = new HashSet<>();
        for (Map.Entry<String, ClassData> classData : getClassList().entrySet()) {
            if (Classes.getClassLevel(classData.getValue()) < swordofmagic7.Classes.Classes.MaxLevel) {
                if (Classes.getClassLevel(classData.getValue()) == data.getInt("ClassData." + classData.getKey() + ".Level", 0)) {
                    if (Classes.getClassExp(classData.getValue()) < data.getInt("ClassData." + classData.getKey() + ".Exp", 0)) {
                        rollback.add("クラス経験値[" + classData.getKey() + "]");
                    }
                } else if (Classes.getClassLevel(classData.getValue()) < data.getInt("ClassData." + classData.getKey() + ".Level", 0)) {
                    rollback.add("クラスレベル[" + classData.getKey() + "]");
                }
            }
        }
        if (Level < MaxLevel) {
            if (Level == data.getInt("Level")) {
                if (Exp < data.getInt("Exp")) {
                    rollback.add("プレイヤー経験値");
                }
            } else if (Level < data.getInt("Level")) {
                rollback.add("プレイヤーレベル");
            }
        }

        if (rollback.size() > 0) {
            player.sendMessage("§eロールバック§aを検知したため§bセーブ§aを中断しました");
            Log("§cロールバック検知: §f" + player.getName() + ", " + player.getUniqueId() + ", " + rollback);
            return;
        }

        data.set("Location.x", player.getLocation().getX());
        data.set("Location.y", player.getLocation().getY());
        data.set("Location.z", player.getLocation().getZ());
        data.set("Location.yaw", player.getLocation().getYaw());
        data.set("Location.pitch", player.getLocation().getPitch());

        data.set("Mel", Mel);
        data.set("Health", Status.Health);
        data.set("Mana", Status.Mana);
        data.set("Map", Map.Id);
        data.set("Nick", Nick);

        data.set("Setting.DamageLog", DamageLog.toString());
        data.set("Setting.ExpLog", ExpLog);
        data.set("Setting.DropLog", DropLog.toString());
        data.set("Setting.PvPMode", PvPMode);
        data.set("Setting.CastMode", CastMode.toString());
        data.set("Setting.StrafeMode", StrafeMode.toString());
        data.set("Setting.ShopAmountReset", Shop.AmountReset);
        data.set("Setting.ViewFormat", ViewFormat);
        data.set("Setting.FishingDisplayNum", FishingDisplayNum);
        data.set("Setting.FishingUseCombo", Gathering.FishingUseCombo);
        if (HoloSelfView) data.set("Setting.HoloSelfView", "VISIBLE");
        else data.set("Setting.HoloSelfView", "HIDDEN");
        data.set("Others.FishingCombo", Gathering.FishingComboBoost);
        data.set("Others.FishingSetCombo", Gathering.FishingSetCombo);
        data.set("Setting.PlayMode", PlayMode);
        data.set("Setting.ViewFormat", ViewFormat);
        data.set("Setting.Inventory.ViewInventory", ViewInventory.toString());
        data.set("Setting.Inventory.ItemInventorySort", ItemInventory.Sort.toString());
        data.set("Setting.Inventory.RuneInventorySort", RuneInventory.Sort.toString());
        data.set("Setting.Inventory.PetInventorySort", PetInventory.Sort.toString());
        data.set("Setting.Inventory.ItemInventorySortReverse", ItemInventory.SortReverse);
        data.set("Setting.Inventory.RuneInventorySortReverse", RuneInventory.SortReverse);
        data.set("Setting.Inventory.PetInventorySortReverse", PetInventory.SortReverse);

        data.set("Title.List", new ArrayList<>(titleManager.TitleList));
        data.set("Title.Select", titleManager.Title.Id);

        data.set("ActiveTeleportGate", ActiveTeleportGate);

        for (LifeType type : LifeType.values()) {
            data.set("Life." + type + "Level", LifeStatus.getLevel(type));
            data.set("Life." + type + "Exp", LifeStatus.getExp(type));
        }

        data.set("Level", Level);
        data.set("Exp", Exp);

        for (Map.Entry<String, ClassData> classData : getClassList().entrySet()) {
            data.set("ClassData." + classData.getKey() + ".Level", Classes.getClassLevel(classData.getValue()));
            data.set("ClassData." + classData.getKey() + ".Exp", Classes.getClassExp(classData.getValue()));
        }

        for (int i = 0; i <= MaxSlot-1; i++) {
            if (Classes.classSlot[i] != null) {
                data.set("Class.Slot" + i, Classes.classSlot[i].Id);
            } else {
                data.set("Class.Slot" + i, "None");
            }
        }

        data.set("Attribute.Point", Attribute.getAttributePoint());
        for (AttributeType attr : AttributeType.values()) {
            data.set("Attribute." + attr.toString(), Attribute.getAttribute(attr));
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            data.set("Inventory." + slot.toString(), new ItemParameterStack(Equipment.getEquip(slot)).toString());
        }
        List<String> itemList = new ArrayList<>();
        for (ItemParameterStack stack : ItemInventory.getList()) {
            itemList.add(stack.toString());
        }
        data.set("Inventory.ItemList", itemList);

        List<String> runeList = new ArrayList<>();
        for (RuneParameter rune : RuneInventory.getList()) {
            runeList.add(rune.toString());
        }
        data.set("Inventory.RuneList", runeList);

        List<String> petList = new ArrayList<>();
        for (PetParameter pet : PetInventory.getList()) {
            petList.add(pet.toString());
        }
        data.set("Inventory.PetList", petList);

        List<String> hotBarList = new ArrayList<>();
        for (HotBarData hotBarData : HotBar.getHotBar()) {
            if (hotBarData != null) {
                hotBarList.add(hotBarData.toString());
            } else {
                hotBarList.add("None");
            }
        }
        data.set("Inventory.HotBar", hotBarList);

        statistics.save(data);

        try {
            data.save(playerFile);
            player.sendMessage("§eプレイヤデータ§aの§bセーブ§aが完了しました");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File playerFile = new File(DataBasePath, "PlayerData/" + player.getUniqueId() + ".yml");
        if (playerFile.exists()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);

            Mel = data.getInt("Mel", 10000);
            Status.Health = data.getDouble("Health", -1);
            Status.Mana = data.getDouble("Mana", -1);
            Map = getMapData(data.getString("Map", "Alden"));
            Nick = data.getString("Nick", player.getName());

            DamageLog = DamageLogType.fromString(data.getString("Setting.DamageLog"));
            ExpLog = data.getBoolean("Setting.ExpLog", false);
            DropLog = DropLogType.fromString(data.getString("Setting.DropLog"));
            CastMode = CastType.valueOf(data.getString("Setting.CastMode", "Renewed"));
            StrafeMode = StrafeType.fromString(data.getString("Setting.StrafeMode"));
            Shop.AmountReset = data.getBoolean("Setting.ShopAmountReset");
            PvPMode = data.getBoolean("Setting.PvPMode", false);
            FishingDisplayNum = data.getBoolean("Setting.FishingDisplayNum", false);
            HoloSelfView = data.getString("Setting.HoloSelfView", "HIDDEN").equals("VISIBLE");
            Gathering.FishingComboBoost = data.getInt("Others.FishingCombo", 0);
            Gathering.FishingSetCombo = data.getInt("Others.FishingSetCombo", 0);
            Gathering.FishingUseCombo = data.getBoolean("Setting.FishingUseCombo", true);
            PlayMode = data.getBoolean("Setting.PlayMode", true);
            ViewFormat = data.getInt("Setting.ViewFormat",0);
            ViewInventory = ViewInventoryType.valueOf(data.getString("Setting.Inventory.ViewInventory","ItemInventory"));
            ItemInventory.Sort = ItemSortType.valueOf(data.getString("Setting.Inventory.ItemInventorySort","Name"));
            RuneInventory.Sort = RuneSortType.valueOf(data.getString("Setting.Inventory.RuneInventorySort","Name"));
            PetInventory.Sort = PetSortType.valueOf(data.getString("Setting.Inventory.PetInventorySort","Name"));
            ItemInventory.SortReverse = data.getBoolean("Setting.Inventory.ItemInventorySortReverse",false);
            RuneInventory.SortReverse = data.getBoolean("Setting.Inventory.RuneInventorySortReverse",false);
            PetInventory.SortReverse = data.getBoolean("Setting.Inventory.PetInventorySortReverse",false);

            titleManager.TitleList = new HashSet<>(data.getStringList("Title.List"));
            titleManager.Title = TitleDataList.getOrDefault(data.getString("Title.Select"), DefaultTitle);

            ActiveTeleportGate = data.getStringList("ActiveTeleportGate");

            for (LifeType type : LifeType.values()) {
                LifeStatus.setLevel(type, data.getInt("Life." + type + "Level", 1));
                LifeStatus.setExp(type, data.getInt("Life." + type + "Exp", 0));
            }

            Level = data.getInt("Level", 1);
            Exp = data.getInt("Exp", 1);

            for (Map.Entry<String, ClassData> classData : getClassList().entrySet()) {
                Classes.setClassLevel(classData.getValue(), data.getInt("ClassData." + classData.getKey() + ".Level"));
                Classes.setClassExp(classData.getValue(), data.getInt("ClassData." + classData.getKey() + ".Exp"));
            }

            for (int i = 0; i <= MaxSlot-1; i++) {
                String id = data.getString("Class.Slot" + i, "None");
                if (!id.equalsIgnoreCase("None"))
                    Classes.classSlot[i] = getClassData(id);
            }

            Attribute.setPoint(data.getInt("Attribute.Point"));
            for (AttributeType attr : AttributeType.values()) {
                Attribute.setAttribute(attr, data.getInt("Attribute." + attr.toString()));
            }

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemParameter param = ItemParameterStack.fromString(data.getString("Inventory." + slot.toString(), "None")).itemParameter;
                if (!param.isEmpty()) Equipment.Equip(slot, param);
            }

            List<String> itemList = data.getStringList("Inventory.ItemList");
            ItemInventory.clear();
            for (String itemData : itemList) {
                ItemParameterStack stack = ItemParameterStack.fromString(itemData);
                if (!stack.isEmpty()) ItemInventory.addItemParameter(stack);
            }

            List<String> runeList = data.getStringList("Inventory.RuneList");
            RuneInventory.clear();
            for (String runeData : runeList) {
                RuneParameter rune = RuneParameter.fromString(runeData);
                if (!rune.isEmpty()) RuneInventory.addRuneParameter(rune);
            }

            List<String> petList = data.getStringList("Inventory.PetList");
            PetInventory.clear();
            for (String petData : petList) {
                PetParameter pet = new PetParameter(player, this, petData);
                PetInventory.addPetParameter(pet);
            }

            List<String> hotBarList = data.getStringList("Inventory.HotBar");
            int i = 0;
            HotBarData[] hotBarData = new HotBarData[32];
            for (String hotBar : hotBarList) {
                hotBarData[i] = HotBarData.fromString(hotBar);
                i++;
            }
            HotBar.setHotBar(hotBarData);

            if (PlayMode) {
                viewUpdate();
                World world = player.getWorld();
                double x = data.getDouble("Location.x", SpawnLocation.getX());
                double y = data.getDouble("Location.y", SpawnLocation.getY());
                double z = data.getDouble("Location.z", SpawnLocation.getZ());
                float yaw = (float) data.getDouble("Location.yaw", SpawnLocation.getYaw());
                float pitch = (float) data.getDouble("Location.pitch", SpawnLocation.getPitch());
                Location loc = new Location(world, x, y, z, yaw, pitch);
                player.teleportAsync(loc);
                player.setGameMode(GameMode.SURVIVAL);
            } else {
                player.setGameMode(GameMode.CREATIVE);
            }

            statistics.load(data);
        } else {
            MultiThread.TaskRunSynchronizedLater(() -> {
                Status.Health = Status.MaxHealth;
                Status.Mana = Status.MaxMana;
                ItemInventory.addItemParameter(DataBase.getItemParameter("ノービスブレード"), 1);
                ItemInventory.addItemParameter(DataBase.getItemParameter("ノービスメイス"), 1);
                ItemInventory.addItemParameter(DataBase.getItemParameter("ノービスロッド"), 1);
                ItemInventory.addItemParameter(DataBase.getItemParameter("ノービスアクトガン"), 1);
                ItemInventory.addItemParameter(DataBase.getItemParameter("ノービスシールド"), 1);
                ItemInventory.addItemParameter(DataBase.getItemParameter("ノービストリンケット"), 1);
                ItemInventory.addItemParameter(DataBase.getItemParameter("ノービスアーマー"), 1);
                Tutorial.tutorialTrigger(player, 0);
            }, 10, "TutorialTrigger: " + player.getName());
        }
        Status.StatusUpdate();
        ViewBar.tickUpdate();
        MultiThread.TaskRunSynchronizedLater(() -> {
            isLoaded = true;
        }, 5);
    }

    public static String booleanToTextOrder(boolean bool) {
        if (bool) return "降順";
        else return "昇順";
    }

    ItemStack UserMenuIcon() {
        List<String> Lore = new ArrayList<>();
        Lore.add("§a§lユーザーメニューを開きます");
        Lore.add("§a§lシフトクリックでインベントリ表示を");
        Lore.add("§a§l瞬時に切り替えることが出来ます");
        Lore.add(decoText("§3§lインベントリ表示"));
        Lore.add(decoLore("§e§lインベントリ表示") + ViewInventory.Display);
        if (ViewInventory.isItem()) {
            Lore.add(decoLore("§e§lインベントリ容量") + ItemInventory.getList().size() + "/" + ItemInventory.MaxSlot);
            Lore.add(decoLore("§e§lソート方法/順") + ItemInventory.Sort.Display + "/" + booleanToTextOrder(ItemInventory.SortReverse));
        } else if (ViewInventory.isRune()) {
            Lore.add(decoLore("§e§lインベントリ容量") + RuneInventory.getList().size() + "/" + RuneInventory.MaxSlot);
            Lore.add(decoLore("§e§lソート方法/順") + RuneInventory.Sort.Display + "/" + booleanToTextOrder(RuneInventory.SortReverse));
        } else if (ViewInventory.isPet()) {
            Lore.add(decoLore("§e§lペットケージ容量") + PetInventory.getList().size() + "/" + PetInventory.MaxSlot);
            Lore.add(decoLore("§e§lソート方法/順") + PetInventory.Sort.Display + "/" + booleanToTextOrder(PetInventory.SortReverse));
        }
        Lore.add("§c§l※BE勢は選択した後インベントリを閉じるとメニューが開きます");
        return new ItemStackData(Material.BOOK, decoText("§e§lユーザーメニュー"), Lore).view();
    }

    public void viewUpdate() {
        if (PlayMode) {
            switch (ViewInventory) {
                case ItemInventory -> ItemInventory.viewInventory();
                case RuneInventory -> RuneInventory.viewRune();
                case PetInventory -> PetInventory.viewPet();
                case HotBar -> HotBar.viewTop();
            }
            HotBar.viewBottom();
            Equipment.viewEquip();
            player.getInventory().setItem(26, UserMenuIcon());
            player.getInventory().setItem(17, UpScrollItem);
            player.getInventory().setItem(35, DownScrollItem);
        }
    }

    public void setView(ViewInventoryType ViewInventory) {
        setView(ViewInventory, true);
    }

    public void setView(ViewInventoryType ViewInventory, boolean log) {
        this.ViewInventory = ViewInventory;
        if (log) player.sendMessage("§eインベントリ表示§aを§e[" + ViewInventory.Display + "]§aに切り替えました");
        viewUpdate();
        Tutorial.tutorialTrigger(player, 1);
    }

    public void changeHealth(double health) {
        if (Status.Health+health > Status.MaxHealth) {
            Status.Health = Status.MaxHealth;
        } else if (Status.Health+health < 0) {
            dead();
        } else {
            Status.Health += health;
        }
    }

    public void setHealth(double health) {
        Status.Health = health;
        changeHealth(0);
    }

    public void changeMana(double mana) {
        if (Status.Mana+mana > Status.MaxMana) {
            Status.Mana = Status.MaxMana;
        } else if (Status.Mana+mana < 0) {
            Status.Mana = 0;
        } else {
            Status.Mana += mana;
        }
    }

    public void setMana(double mana) {
        Status.Mana = mana;
        changeMana(0);
    }

    private boolean RightClickHold = false;
    private BukkitTask RightClickHoldTask;

    public void setRightClickHold() {
        RightClickHold = true;
        if (CastMode.isRenewed()) HotBar.UpdateHotBar();
        setRightClickHoldTask();
    }

    void setRightClickHoldTask() {
        if (RightClickHoldTask != null) RightClickHoldTask.cancel();
        RightClickHoldTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isHandRaised() || player.isBlocking()) {
                setRightClickHoldTask();
            } else {
                RightClickHold = false;
                if (CastMode.isRenewed()) HotBar.UpdateHotBar();
            }
        }, 5);
    }

    public boolean isRightClickHold() {
        return RightClickHold || player.isHandRaised() || player.isBlocking();
    }

    public void revival() {
        if (isDead) {
            RevivalReady = true;
        }
    }

    public int deadTime = 0;
    public void dead() {
        final Location LastDeadLocation = player.getLocation();
        MultiThread.TaskRunSynchronized(() -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle("§4§lYou Are Dead", "", 20, 200, 20);
            isDead = true;
            deadTime = 1200;
            Hologram hologram = createHologram("DeadHologram:" + player.getName(), player.getEyeLocation());
            hologram.appendTextLine(Nick);
            ItemStack head = ItemStackPlayerHead(player);
            head.setAmount(1);
            hologram.appendItemLine(head);
            new BukkitRunnable() {
                @Override
                public void run() {
                    deadTime -= 10;
                    if (deadTime <= 0) {
                        this.cancel();
                        player.teleportAsync(player.getWorld().getSpawnLocation());
                        player.setGameMode(GameMode.SURVIVAL);
                        Status.Health = Status.MaxHealth;
                        Status.Mana = Status.MaxMana;
                        player.resetTitle();
                        Map = getMapData("Alden");
                        hologram.delete();
                    } else if (RevivalReady) {
                        this.cancel();
                        RevivalReady = false;
                        player.teleportAsync(LastDeadLocation);
                        player.setGameMode(GameMode.SURVIVAL);
                        Status.Health = Status.MaxHealth/2;
                        player.resetTitle();
                        hologram.delete();
                    } else {
                        LastDeadLocation.setPitch(player.getLocation().getPitch());
                        LastDeadLocation.setYaw(player.getLocation().getYaw());
                        player.teleportAsync(LastDeadLocation);
                        if (deadTime < 1100) player.sendTitle("§4§lYou Are Dead", "§e§lスニークでリスポーン", 0, 20, 0);
                    }
                }
            }.runTaskTimer(plugin, 0, 10);
        }, "PlayerDead: " + player.getName());
    }
}

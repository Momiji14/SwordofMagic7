package swordofmagic7.Data;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import swordofmagic7.Classes.ClassData;
import swordofmagic7.Equipment.EquipmentCategory;
import swordofmagic7.Equipment.EquipmentSlot;
import swordofmagic7.Inventory.ItemParameterStack;
import swordofmagic7.Item.ItemCategory;
import swordofmagic7.Item.ItemExtend.ItemPotionType;
import swordofmagic7.Item.ItemParameter;
import swordofmagic7.Item.ItemStackData;
import swordofmagic7.Item.RuneParameter;
import swordofmagic7.Life.Angler.AnglerData;
import swordofmagic7.Life.Harvest.HarvestData;
import swordofmagic7.Life.Harvest.HarvestItemData;
import swordofmagic7.Life.Lumber.LumberData;
import swordofmagic7.Life.Lumber.LumberItemData;
import swordofmagic7.Life.Mine.MineData;
import swordofmagic7.Life.Mine.MineItemData;
import swordofmagic7.Map.MapData;
import swordofmagic7.Map.TeleportGateParameter;
import swordofmagic7.Map.WarpGateParameter;
import swordofmagic7.Mob.*;
import swordofmagic7.Npc.NpcData;
import swordofmagic7.Pet.PetData;
import swordofmagic7.Shop.ItemRecipe;
import swordofmagic7.Shop.ShopData;
import swordofmagic7.Shop.ShopSlot;
import swordofmagic7.Skill.SkillData;
import swordofmagic7.Skill.SkillParameter;
import swordofmagic7.Skill.SkillType;
import swordofmagic7.Status.StatusParameter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static swordofmagic7.Function.Log;
import static swordofmagic7.Function.decoText;

public final class DataBase {
    public static final String DataBasePath = "M:\\Minecraft\\Server\\SwordofMagic7\\DataBase\\";
    public static final String format = "%.3f";
    public static final Location SpawnLocation = new Location(Bukkit.getWorld("world"), 1200.5, 100, 0.5, 0, 0);
    public static final ItemStack AirItem = new ItemStack(Material.AIR);
    public static final ItemStack FlameItem = new ItemStackData(Material.GRAY_STAINED_GLASS_PANE, "§7§l空スロット").view();
    public static final ItemStack ShopFlame = new ItemStackData(Material.BROWN_STAINED_GLASS_PANE, " ").view();
    public static final ItemStack UpScrollItem = UpScrollItem();
    public static final ItemStack DownScrollItem = DownScrollItem();
    public static final String itemInformation = decoText("§3§lアイテム情報");
    public static final String itemParameter = decoText("§3§lパラメーター");
    public static final String itemRune = decoText("§3§lルーン");
    public static final HashMap<String, ItemParameter> ItemList = new HashMap<>();
    public static final HashMap<String, RuneParameter> RuneList = new HashMap<>();
    public static final HashMap<String, ClassData> ClassList = new HashMap<>();
    public static final HashMap<String, SkillData> SkillDataList = new HashMap<>();
    public static final HashMap<String, SkillData> SkillDataDisplayList = new HashMap<>();
    public static final HashMap<String, MobData> MobList = new HashMap<>();
    public static final HashMap<String, MobSpawnerData> MobSpawnerList = new HashMap<>();
    public static final HashMap<String, ShopData> ShopList = new HashMap<>();
    public static final HashMap<String, ItemRecipe> ItemRecipeList = new HashMap<>();
    public static final HashMap<String, WarpGateParameter> WarpGateList = new HashMap<>();
    public static final HashMap<String, TeleportGateParameter> TeleportGateList = new HashMap<>();
    public static final HashMap<String, MapData> MapList = new HashMap<>();
    public static final HashMap<String, PetData> PetList = new HashMap<>();
    public static final HashMap<Integer, NpcData> NpcList = new HashMap<>();
    public static final HashMap<Integer, String> TeleportGateMenu = new HashMap<>();
    public static final HashMap<String, MineData> MineDataList = new HashMap<>();
    public static final HashMap<String, LumberData> LumberDataList = new HashMap<>();
    public static final HashMap<String, HarvestData> HarvestDataList = new HashMap<>();
    public static final HashMap<String, AnglerData> AnglerDataList = new HashMap<>();

    public static ItemStack ItemStackPlayerHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);
        return item;
    }

    static ItemStack UpScrollItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner("MHF_ArrowUp");
        meta.setDisplayName("§e§l上にスクロール");
        item.setItemMeta(meta);
        return item;
    }

    static ItemStack DownScrollItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner("MHF_ArrowDown");
        meta.setDisplayName("§e§l下にスクロール");
        item.setItemMeta(meta);
        return item;
    }

    private static List<File> dumpFile(File file) {
        List<File> list = new ArrayList<>();
        File[] files = file.listFiles();
        for (File tmpFile : files) {
            if (tmpFile.isDirectory()) {
                list.addAll(dumpFile(tmpFile));
            } else {
                list.add(tmpFile);
            }
        }
        return list;
    }

    public static void DataLoad() {
        File npcDirectories = new File(DataBasePath, "Npc");
        List<File> npcFiles = dumpFile(npcDirectories);
        for (File file : npcFiles) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            NpcData npcData = new NpcData();
            npcData.Message = data.getStringList("Message");
            NpcList.put(Integer.valueOf(fileName), npcData);
        }

        File itemDirectories = new File(DataBasePath, "ItemData");
        List<File> itemFiles = dumpFile(itemDirectories);
        for (File file : itemFiles) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            ItemParameter itemParameter = new ItemParameter();
            itemParameter.Id = fileName;
            itemParameter.Display = data.getString("Display");
            itemParameter.Lore = data.getStringList("Lore");
            itemParameter.Sell = data.getInt("Sell");
            itemParameter.Category = ItemCategory.Item.getItemCategory(data.getString("Category"));
            if (data.isSet("Color.R")) {
                itemParameter.color = Color.fromRGB(data.getInt("Color.R"), data.getInt("Color.G"), data.getInt("Color.B"));
            }
            if (itemParameter.Category.isPetEgg()) {
                itemParameter.itemPetEgg.PetId = data.getString("PetId");
                itemParameter.itemPetEgg.PetMaxLevel = data.getInt("PetMaxLevel");
                itemParameter.itemPetEgg.PetLevel = data.getInt("PetLevel");
            } else if (itemParameter.Category.isPotion()) {
                itemParameter.itemPotion.PotionType = ItemPotionType.valueOf(data.getString("Potion.Type"));
                itemParameter.itemPotion.CoolTime = data.getInt("Potion.CoolTime");
                for (int i = 0; i < 4; i++) {
                    itemParameter.itemPotion.Value[i] = data.getDouble("Potion.Value." + i);
                }
            } else if (itemParameter.Category.isPetFood()) {
                itemParameter.itemPetFood.Stamina = data.getInt("PetFood.Stamina");
            }
            if (data.isSet("Material")) {
                itemParameter.Icon = Material.getMaterial(data.getString("Material", "BARRIER"));
            } else if (itemParameter.Category == ItemCategory.Equipment) {
                itemParameter.itemEquipmentData.EquipmentCategory = EquipmentCategory.getEquipmentCategory(data.getString("EquipmentCategory"));
                itemParameter.Icon = itemParameter.itemEquipmentData.EquipmentCategory.material;
                itemParameter.itemEquipmentData.ReqLevel = data.getInt("ReqLevel");
                itemParameter.itemEquipmentData.RuneSlot = data.getInt("RuneSlot");
                itemParameter.itemEquipmentData.UpgradeCost = data.getInt("UpgradeCost");
                itemParameter.itemEquipmentData.Durable = data.getInt("Durable");
                itemParameter.itemEquipmentData.MaxDurable = itemParameter.itemEquipmentData.Durable;
                itemParameter.itemEquipmentData.EquipmentSlot = EquipmentSlot.MainHand.getEquipmentSlot(data.getString("EquipmentSlot"));
                for (StatusParameter param : StatusParameter.values()) {
                    if (data.isSet(param.toString())) {
                        itemParameter.itemEquipmentData.Parameter.put(param, data.getDouble(param.toString()));
                    }
                }
            }
            ItemList.put(itemParameter.Id, itemParameter);
        }

        File runeDirectories = new File(DataBasePath, "RuneData/");
        File[] runeFile = runeDirectories.listFiles();
        for (File file : runeFile) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            RuneParameter runeData = new RuneParameter();
            runeData.Id = fileName;
            runeData.Icon = Material.getMaterial(data.getString("Icon"));
            runeData.Display = data.getString("Display");
            runeData.Lore = data.getStringList("Lore");
            for (StatusParameter param : StatusParameter.values()) {
                if (data.isSet(param.toString())) {
                    runeData.Parameter.put(param, data.getDouble(param.toString()));
                } else {
                    runeData.Parameter.put(param, 0d);
                }
            }
            RuneList.put(runeData.Id, runeData);
        }

        File petDirectories = new File(DataBasePath, "PetData/");
        List<File> petFile = dumpFile(petDirectories);
        for (File file : petFile) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            PetData petData = new PetData();
            petData.Id = fileName;
            petData.Display = data.getString("Display");
            petData.Lore = data.getStringList("Lore");
            petData.entityType = EntityType.fromName(data.getString("Type").toUpperCase());
            if (data.isSet("Disguise.Type")) {
                petData.disguise = new MobDisguise(DisguiseType.valueOf(data.getString("Disguise.Type").toUpperCase()));
                if (petData.disguise.getType() == DisguiseType.SLIME) {
                    SlimeWatcher slimeWatcher = new SlimeWatcher(petData.disguise);
                    slimeWatcher.setSize(data.getInt("Disguise.Size"));
                    petData.disguise.setWatcher(slimeWatcher);
                }
            }
            petData.Icon = Material.getMaterial(data.getString("Icon", "BARRIER"));
            petData.MaxStamina = data.getDouble("MaxStamina");
            petData.MaxHealth = data.getDouble("MaxHealth");
            petData.HealthRegen = data.getDouble("HealthRegen");
            petData.MaxMana = data.getDouble("MaxMana");
            petData.ManaRegen = data.getDouble("ManaRegen");
            petData.ATK = data.getDouble("ATK");
            petData.DEF = data.getDouble("DEF");
            petData.HLP = data.getDouble("HLP");
            petData.ACC = data.getDouble("ACC");
            petData.EVA = data.getDouble("EVA");
            petData.CriticalRate = data.getDouble("CriticalRate");
            petData.CriticalResist = data.getDouble("CriticalResist");
            PetList.put(fileName, petData);
        }

        File mapDirectories = new File(DataBasePath, "MapData/");
        File[] mapFile = mapDirectories.listFiles();
        for (File file : mapFile) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            String fileName = file.getName().replace(".yml", "");
            MapData mapData = new MapData();
            mapData.Id = fileName;
            mapData.Display = data.getString("Display");
            mapData.Color = data.getString("Color");
            mapData.Level = data.getInt("Level");
            mapData.Safe = data.getBoolean("Safe");
            if (data.isSet("Life.Mine")) {
                for (String str : data.getStringList("Life.Mine")) {
                    String[] split = str.split(",");
                    mapData.GatheringData.put(Material.getMaterial(split[0]), split[1]);
                }
            }
            if (data.isSet("Life.Lumber")) {
                for (String str : data.getStringList("Life.Lumber")) {
                    String[] split = str.split(",");
                    mapData.GatheringData.put(Material.getMaterial(split[0]), split[1]);
                }
            }
            if (data.isSet("Life.Harvest")) {
                for (String str : data.getStringList("Life.Harvest")) {
                    String[] split = str.split(",");
                    mapData.GatheringData.put(Material.getMaterial(split[0]), split[1]);
                }
            }
            MapList.put(fileName, mapData);
        }

        File lifeMineDirectories = new File(DataBasePath, "Life/Mine");
        File[] lifeMineFile = lifeMineDirectories.listFiles();
        for (File file : lifeMineFile) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            String fileName = file.getName().replace(".yml", "");
            int CoolTime = data.getInt("CoolTime");
            int Exp = data.getInt("Exp");
            int ReqLevel = data.getInt("ReqLevel");
            MineData lifeData = new MineData(CoolTime, Exp, ReqLevel);
            for (String str : data.getStringList("Item")) {
                String[] split = str.split(":");
                MineItemData itemData = new MineItemData();
                itemData.itemParameter = getItemParameter(split[0]);
                itemData.Percent = Double.parseDouble(split[1]);
                lifeData.itemData.add(itemData);
            }
            MineDataList.put(fileName, lifeData);
        }

        File lifeLumberDirectories = new File(DataBasePath, "Life/Lumber");
        File[] lifeLumberFile = lifeLumberDirectories.listFiles();
        for (File file : lifeLumberFile) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            String fileName = file.getName().replace(".yml", "");
            int CoolTime = data.getInt("CoolTime");
            int Exp = data.getInt("Exp");
            int ReqLevel = data.getInt("ReqLevel");
            LumberData lifeData = new LumberData(CoolTime, Exp, ReqLevel);
            for (String str : data.getStringList("Item")) {
                String[] split = str.split(":");
                LumberItemData itemData = new LumberItemData();
                itemData.itemParameter = getItemParameter(split[0]);
                itemData.Percent = Double.parseDouble(split[1]);
                lifeData.itemData.add(itemData);
            }
            LumberDataList.put(fileName, lifeData);
        }

        File lifeHarvestDirectories = new File(DataBasePath, "Life/Harvest");
        File[] lifeHarvestFile = lifeHarvestDirectories.listFiles();
        for (File file : lifeHarvestFile) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            String fileName = file.getName().replace(".yml", "");
            int CoolTime = data.getInt("CoolTime");
            int Exp = data.getInt("Exp");
            int ReqLevel = data.getInt("ReqLevel");
            HarvestData lifeData = new HarvestData(CoolTime, Exp, ReqLevel);
            for (String str : data.getStringList("Item")) {
                String[] split = str.split(":");
                HarvestItemData itemData = new HarvestItemData();
                itemData.itemParameter = getItemParameter(split[0]);
                itemData.Percent = Double.parseDouble(split[1]);
                lifeData.itemData.add(itemData);
            }
            HarvestDataList.put(fileName, lifeData);
        }

        File warpDirectories = new File(DataBasePath, "WarpGateData/");
        File[] warpFile = warpDirectories.listFiles();
        for (File file : warpFile) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            String fileName = file.getName().replace(".yml", "");
            World world = Bukkit.getWorld(data.getString("Location.w", "world"));
            double x = data.getDouble("Location.x");
            double y = data.getDouble("Location.y");
            double z = data.getDouble("Location.z");
            float yaw = (float) data.getDouble("Location.yaw");
            float pitch = (float) data.getDouble("Location.pitch");
            Location loc = new Location(world, x, y, z, yaw, pitch);
            WarpGateParameter warp = new WarpGateParameter();
            warp.Id = fileName;
            warp.Location = loc;
            if (data.isSet("Target")) {
                warp.Target = data.getString("Target");
            } else {
                double xT = data.getDouble("TargetLocation.x");
                double yT = data.getDouble("TargetLocation.y");
                double zT = data.getDouble("TargetLocation.z");
                float yawT = (float) data.getDouble("TargetLocation.yaw");
                float pitchT = (float) data.getDouble("TargetLocation.pitch");
                warp.TargetLocation = new Location(world, xT, yT, zT, yawT, pitchT);
            }
            warp.NextMap = MapList.get(data.getString("NextMap"));
            warp.Trigger = data.getString("Trigger");
            warp.start();
            if (data.getBoolean("Default", true)) {
                warp.Active();
            } else {
                warp.Disable();
            }
            WarpGateList.put(fileName, warp);
        }

        File teleportDirectories = new File(DataBasePath, "TeleportGateData/");
        File[] teleportFile = teleportDirectories.listFiles();
        for (File file : teleportFile) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            String fileName = file.getName().replace(".yml", "");
            if (!fileName.equalsIgnoreCase("GUI")) {
                World world = Bukkit.getWorld(data.getString("Location.w", "world"));
                double x = data.getDouble("Location.x");
                double y = data.getDouble("Location.y");
                double z = data.getDouble("Location.z");
                float yaw = (float) data.getDouble("Location.yaw");
                float pitch = (float) data.getDouble("Location.pitch");
                Location loc = new Location(world, x, y, z, yaw, pitch);
                TeleportGateParameter teleport = new TeleportGateParameter();
                teleport.Id = fileName;
                teleport.Display = data.getString("Display");
                teleport.Icon = Material.getMaterial(data.getString("Icon"));
                teleport.Title = data.getString("Title");
                teleport.Subtitle = data.getString("Subtitle");
                teleport.Location = loc;
                teleport.DefaultActive = data.getBoolean("DefaultActive");
                teleport.Map = getMapData(data.getString("Map"));
                TeleportGateList.put(fileName, teleport);
                teleport.start();
            } else {
                for (int i = 0; i < 54; i++) {
                    if (data.isSet("TeleportGateMenu." + i)) {
                        TeleportGateMenu.put(i, data.getString("TeleportGateMenu." + i));
                    }
                }
            }
        }

        File skillDirectories = new File(DataBasePath, "SkillData/");
        List<File> skillFile = dumpFile(skillDirectories);
        for (File file : skillFile) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            SkillData skillData = new SkillData();
            skillData.Id = fileName;
            skillData.Icon = Material.getMaterial(data.getString("Icon", "END_CRYSTAL"));
            skillData.Display = data.getString("Display");
            List<String> Lore = new ArrayList<>();
            for (String str : data.getStringList("Lore")) {
                Lore.add("§a§l" + str);
            }
            skillData.Lore = Lore;
            skillData.SkillType = SkillType.valueOf(data.getString("SkillType"));
            skillData.ReqLevel = data.getInt("ReqLevel");
            int i = 0;
            while (data.isSet("Parameter-" + i + ".Display")) {
                SkillParameter param = new SkillParameter();
                param.Display = data.getString("Parameter-" + i + ".Display");
                param.Value = data.getDouble("Parameter-" + i + ".Value");
                param.Increase = data.getDouble("Parameter-" + i + ".Increase");
                param.Prefix = data.getString("Parameter-" + i + ".Prefix");
                param.Suffix = data.getString("Parameter-" + i + ".Suffix");
                param.Format = "%." + data.getInt("Parameter-" + i + ".Format") + "f";
                skillData.Parameter.add(param);
                i++;
            }
            if (skillData.SkillType.isActive()) {
                for (String str : data.getStringList("ReqMainHand")) {
                    skillData.ReqMainHand.add(EquipmentCategory.getEquipmentCategory(str));
                }
                skillData.Mana = data.getInt("Mana");
                skillData.CastTime = data.getInt("CastTime");
                skillData.RigidTime = data.getInt("RigidTime");
                skillData.CoolTime = data.getInt("CoolTime");
            }
            SkillDataList.put(skillData.Id, skillData);
            SkillDataDisplayList.put(skillData.Display, skillData);
        }

        File classDirectories = new File(DataBasePath, "ClassData/");
        List<File> classFile = dumpFile(classDirectories);
        for (File file : classFile) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            ClassData classData = new ClassData();
            classData.Id = fileName;
            classData.Color = data.getString("Color");
            classData.Icon = Material.getMaterial(data.getString("Icon", "BARRIER"));
            classData.Display = data.getString("Display");
            classData.Lore = data.getStringList("Lore");
            classData.Nick = data.getString("Nick");
            classData.Tier = data.getInt("Tier");
            List<SkillData> Skills = new ArrayList<>();
            for (String str : data.getStringList("SkillList")) {
                if (SkillDataList.containsKey(str)) {
                    Skills.add(SkillDataList.get(str));
                }
            }
            classData.SkillList = Skills;
            ClassList.put(fileName, classData);
        }
        for (File file : classFile) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            if (data.isSet("ReqClass")) {
                for (String str : data.getStringList("ReqClass")) {
                    String[] split = str.split(":");
                    ClassList.get(fileName).ReqClass.put(getClassData(split[0]), Integer.valueOf(split[1]));
                }
            }
        }

        File mobDirectories = new File(DataBasePath, "MobData/");
        List<File> mobFile = dumpFile(mobDirectories);
        for (File file : mobFile) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            MobData mobData = new MobData();
            mobData.Id = fileName;
            mobData.Display = data.getString("Display");
            String entityType = data.getString("Type").toUpperCase();
            if (EntityType.fromName(entityType) != null) {
                mobData.entityType = EntityType.valueOf(entityType);
            } else {
                mobData.entityType = EntityType.SKELETON;
                Log("§cError Non-EntityType: " + fileName);
            }
            if (data.isSet("Disguise.Type")) {
                mobData.disguise = new MobDisguise(DisguiseType.valueOf(data.getString("Disguise.Type").toUpperCase()));
                if (mobData.disguise.getType() == DisguiseType.SLIME) {
                    SlimeWatcher slimeWatcher = new SlimeWatcher(mobData.disguise);
                    slimeWatcher.setSize(data.getInt("Disguise.Size"));
                    mobData.disguise.setWatcher(slimeWatcher);
                }
            }
            mobData.Health = data.getDouble("Health");
            mobData.ATK = data.getDouble("ATK");
            mobData.DEF = data.getDouble("DEF");
            mobData.ACC = data.getDouble("ACC");
            mobData.EVA = data.getDouble("EVA");
            mobData.CriticalRate = data.getDouble("CriticalRate");
            mobData.CriticalResist = data.getDouble("CriticalResist");
            mobData.Exp = data.getDouble("Exp");
            mobData.Mov = data.getDouble("Mov");
            mobData.Reach = data.getDouble("Reach");
            if (data.isSet("Skill")) {
                List<MobSkillData> SkillList = new ArrayList<>();
                for (String str : data.getStringList("Skill")) {
                    try {
                        String[] split = str.split(",");
                        MobSkillData mobSkillData = new MobSkillData();
                        mobSkillData.Skill = split[0];
                        for(String skillData : split) {
                            if (skillData.contains("Percent:")) {
                                mobSkillData.Percent = Double.parseDouble(skillData.replace("Percent:", ""));
                            } else if (skillData.contains("CoolTime:")) {
                                mobSkillData.CoolTime = Integer.parseInt(skillData.replace("CoolTime:", ""));
                            } else if (skillData.contains("Health:")) {
                                mobSkillData.Health = Double.parseDouble(skillData.replace("Health:", ""));
                            } else if (skillData.contains("Available:")) {
                                mobSkillData.Available = Integer.parseInt(skillData.replace("Available:", ""));
                            }
                        }
                        SkillList.add(mobSkillData);
                    } catch (Exception ignored) {
                        Log("MobData Format Error -> " + fileName);
                    }

                }
                mobData.SkillList = SkillList;
            }
            if (data.isSet("Hostile")) {
                mobData.Hostile = data.getBoolean("Hostile");
            }
            if (data.isSet("HPStop")) {
                mobData.HPStop = data.getDoubleList("HPStop");
            }

            List<DropItemData> DropItemTable = new ArrayList<>();
            for (String dropStr : data.getStringList("DropItem")) {
                String[] dropData = dropStr.split(",");
                DropItemData dropItemData = new DropItemData(getItemParameter(dropData[0]));
                for (String str : dropData) {
                    if (str.contains("Amount:")) {
                        str = str.replace("Amount:", "");
                        if (str.contains("-")) {
                            String[] Amount = str.split("-");
                            dropItemData.MinAmount = Integer.parseInt(Amount[0]);
                            dropItemData.MaxAmount = Integer.parseInt(Amount[1]);
                        } else {
                            dropItemData.MinAmount = Integer.parseInt(str);
                            dropItemData.MaxAmount = Integer.parseInt(str);
                        }
                    } else if (str.contains("Level:")) {
                        str = str.replace("Level:", "");
                        if (str.contains("-")) {
                            String[] Level = str.split("-");
                            dropItemData.MinLevel = Integer.parseInt(Level[0]);
                            dropItemData.MaxLevel = Integer.parseInt(Level[1]);
                        } else {
                            dropItemData.MinLevel = Integer.parseInt(str);
                            dropItemData.MaxLevel = Integer.parseInt(str);
                        }
                    } else if (str.contains("Percent")) {
                        dropItemData.Percent = Double.parseDouble(str.replace("Percent:", ""));
                    }
                }
                DropItemTable.add(dropItemData);
            }

            List<DropRuneData> DropRuneTable = new ArrayList<>();
            for (String dropStr : data.getStringList("DropRune")) {
                String[] dropData = dropStr.split(",");
                DropRuneData dropRuneData = new DropRuneData(getRuneParameter(dropData[0]));
                for (String str : dropData) {
                    if (str.contains("Level:")) {
                        str = str.replace("Level:", "");
                        if (str.contains("-")) {
                            String[] Level = str.split("-");
                            dropRuneData.MinLevel = Integer.parseInt(Level[0]);
                            dropRuneData.MaxLevel = Integer.parseInt(Level[1]);
                        } else {
                            dropRuneData.MinLevel = Integer.parseInt(str);
                            dropRuneData.MaxLevel = Integer.parseInt(str);
                        }
                    } else if (str.contains("Percent")) {
                        dropRuneData.Percent = Double.parseDouble(str.replace("Percent:", ""));
                    }
                }
                DropRuneTable.add(dropRuneData);
            }

            mobData.DropItemTable = DropItemTable;
            mobData.DropRuneTable = DropRuneTable;

            MobList.put(fileName, mobData);
        }

        File mobSpawnerDirectories = new File(DataBasePath, "MobSpawner/");
        List<File> mobSpawner = dumpFile(mobSpawnerDirectories);
        for (File file : mobSpawner) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            MobSpawnerData mobSpawnerData = new MobSpawnerData();
            mobSpawnerData.mobData = getMobData(data.getString("MobData"));
            mobSpawnerData.Level = data.getInt("Level");
            mobSpawnerData.Radius = data.getInt("Radius");
            mobSpawnerData.RadiusY = data.getInt("RadiusY");
            mobSpawnerData.MaxMob = data.getInt("MaxMob");
            mobSpawnerData.PerSpawn = data.getInt("PerSpawn");
            double x = data.getDouble("Location.x");
            double y = data.getDouble("Location.y");
            double z = data.getDouble("Location.z");
            mobSpawnerData.location = new Location(Bukkit.getWorld(data.getString("Location.w", "world")), x, y, z);
            mobSpawnerData.start();
            MobSpawnerList.put(fileName, mobSpawnerData);
        }

        File recipeDirectories = new File(DataBasePath, "Recipe/");
        File[] recipeFile = recipeDirectories.listFiles();
        for (File file : recipeFile) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            ItemRecipe itemRecipe = new ItemRecipe();
            for (String str : data.getStringList("ReqStack")) {
                String[] split = str.split(",");
                ItemParameter itemParameter = getItemParameter(split[0]);
                ItemParameterStack stack = new ItemParameterStack(itemParameter);
                for (String str2 : split) {
                    if (str2.contains("Amount:")) {
                        stack.Amount = Integer.parseInt(str2.replace("Amount:", ""));
                    }
                }
                itemRecipe.ReqStack.add(stack);
            }
            ItemRecipeList.put(fileName, itemRecipe);
        }

        File shopDirectories = new File(DataBasePath, "ShopData/");
        File[] shopFile = shopDirectories.listFiles();
        for (File file : shopFile) {
            String fileName = file.getName().replace(".yml", "");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            ShopData shopData = new ShopData();
            shopData.Display = fileName;
            int slot = 0;
            for (String str : data.getStringList("Data")) {
                ShopSlot shopSlot = new ShopSlot();
                String[] split = str.split(",");
                shopSlot.itemParameter = getItemParameter(split[0]);
                for (String str2 : split) {
                    if (str2.contains("Mel:")) {
                        shopSlot.Mel = Integer.parseInt(str2.replace("Mel:", ""));
                    } else if (str2.contains("Slot:")) {
                        slot = Integer.parseInt(str2.replace("Slot:", ""));
                    } else if (str2.contains("Recipe:")) {
                        shopSlot.itemRecipe = getItemRecipe(str2.replace("Recipe:", ""));
                    }
                }
                shopData.Data.put(slot, shopSlot);
                slot++;
            }
            ShopList.put(fileName, shopData);
        }
    }

    public static HashMap<String, ItemParameter> getItemList() {
        return ItemList;
    }

    public static HashMap<String, RuneParameter> getRuneList() {
        return RuneList;
    }

    public static HashMap<String, ClassData> getClassList() {
        return ClassList;
    }

    public static HashMap<String, SkillData> getSkillList() {
        return SkillDataList;
    }

    public static HashMap<String, MobData> getMobList() {
        return MobList;
    }

    public static HashMap<String, PetData> getPetList() {
        return PetList;
    }

    public static HashMap<String, ItemRecipe> getItemRecipeList() {
        return ItemRecipeList;
    }

    public static NpcData getNpcData(int id) {
        if (NpcList.containsKey(id)) {
            return NpcList.get(id);
        } else {
            Log("§cNon-NpcData: " + id, true);
            return new NpcData();
        }
    }

    public static ItemParameter getItemParameter(String str) {
        if (ItemList.containsKey(str)) {
            return ItemList.get(str).clone();
        } else {
            Log("§cNon-ItemParameter: " + str, true);
            return new ItemParameter();
        }
    }

    public static RuneParameter getRuneParameter(String str) {
        if (RuneList.containsKey(str)) {
            return RuneList.get(str).clone();
        } else {
            Log("§cNon-RuneParameter: " + str, true);
            return new RuneParameter();
        }
    }

    public static ClassData getClassData(String str) {
        if (ClassList.containsKey(str)) {
            return ClassList.get(str);
        } else {
            Log("§cNon-ClassData: " + str, true);
            return new ClassData();
        }
    }

    public static SkillData getSkillData(String skill) {
        if (SkillDataList.containsKey(skill)) {
            return SkillDataList.get(skill);
        } else if (SkillDataDisplayList.containsKey(skill)) {
            return SkillDataDisplayList.get(skill);
        } else {
            Log("§cNon-SkillData: " + skill, true);
            return new SkillData();
        }
    }

    public static MobData getMobData(String str) {
        if (MobList.containsKey(str)) {
            return MobList.get(str);
        } else {
            Log("§cNon-MobData: " + str, true);
            return new MobData();
        }
    }

    public static ItemRecipe getItemRecipe(String str) {
        if (ItemRecipeList.containsKey(str)) {
            return ItemRecipeList.get(str);
        } else {
            Log("§cNon-ItemRecipe: " + str, true);
            return new ItemRecipe();
        }
    }

    static MapData getMapData(String str) {
        if (MapList.containsKey(str)) {
            return MapList.get(str);
        } else {
            Log("§cNon-MapData: " + str, true);
            return new MapData();
        }
    }

    public static PetData getPetData(String str) {
        if (PetList.containsKey(str)) {
            return PetList.get(str);
        } else {
            Log("§cNon-PetData: " + str, true);
            return new PetData();
        }
    }

    public static ShopData getShopData(String str) {
        if (ShopList.containsKey(str)) {
            return ShopList.get(str);
        } else {
            Log("§cNon-ShopList: " + str, true);
            return new ShopData();
        }
    }

    public static WarpGateParameter getWarpGate(String str) {
        if (WarpGateList.containsKey(str)) {
            return WarpGateList.get(str);
        } else {
            Log("§cNon-WarpGate: " + str, true);
            return new WarpGateParameter();
        }
    }
}

package swordofmagic7.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import swordofmagic7.Item.ItemStackData;
import swordofmagic7.Particle.ParticleData;

import static swordofmagic7.Function.VectorUp;
import static swordofmagic7.Function.decoText;
import static swordofmagic7.Particle.ParticleManager.spawnParticle;
import static swordofmagic7.System.BTTSet;
import static swordofmagic7.System.plugin;

public class TeleportGateParameter {
    public String Id;
    public String Display;
    public Material Icon;
    public String Title;
    public String Subtitle;
    public Location Location;
    public boolean DefaultActive;
    public MapData Map;

    public ItemStack view() {
        return new ItemStackData(Icon, decoText(Display)).view();
    }

    private World world;
    private final ParticleData particleData = new ParticleData(Particle.FIREWORKS_SPARK, 0.1f, VectorUp);
    public void start() {
        world = Location.getWorld();
        BTTSet(new BukkitRunnable() {
            int i = 0;
            final double increment = (2 * Math.PI) / 90;
            final double radius = 1.5;
            @Override
            public void run() {
                for (int loop = 0; loop < 3; loop++) {
                    i++;
                    double angle = i * increment;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    Location nLoc = new Location(world, Location.getX() + x, Location.getY(), Location.getZ() + z);
                    Location nLoc2 = new Location(world, Location.getX() - x, Location.getY(), Location.getZ() - z);
                    spawnParticle(particleData, nLoc);
                    spawnParticle(particleData, nLoc2);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0 , 1), "TeleportGate:" + Id);
    }
}
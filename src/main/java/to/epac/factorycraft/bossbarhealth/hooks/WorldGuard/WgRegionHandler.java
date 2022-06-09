package to.epac.factorycraft.bossbarhealth.hooks.WorldGuard;

import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar.BarType;

public class WgRegionHandler implements Listener {

    @EventHandler
    public void onRegionEntered(RegionEnteredEvent event) {
        if (!BossBarHealth.inst().getConfigManager().isWgEnabled()) return;

        Bukkit.getScheduler().runTask(BossBarHealth.inst(), () -> {

            Player player = event.getPlayer();
            HealthBar bar = HealthBar.bars.get(player);

            if (BossBarHealth.inst().getConfigManager().getWorldsHidden().contains(player.getWorld())) return;

            if (bar == null) {
                bar = new HealthBar();
                bar.update(player, BarType.NORMAL, 0.0, null, true);
            } else
                bar.update(player, bar.getType(), bar.getLostgain(), bar.getCause(), false);
        });
    }

    @EventHandler
    public void onRegionLeft(RegionLeftEvent event) {
        if (!BossBarHealth.inst().getConfigManager().isWgEnabled()) return;

        Bukkit.getScheduler().runTask(BossBarHealth.inst(), () -> {

            Player player = event.getPlayer();
            HealthBar bar = HealthBar.bars.get(player);

            if (BossBarHealth.inst().getConfigManager().getWorldsHidden().contains(player.getWorld())) return;

            if (bar == null) {
                bar = new HealthBar();
                bar.update(player, BarType.NORMAL, 0.0, null, true);
            } else
                bar.update(player, bar.getType(), bar.getLostgain(), bar.getCause(), false);
        });
    }
}

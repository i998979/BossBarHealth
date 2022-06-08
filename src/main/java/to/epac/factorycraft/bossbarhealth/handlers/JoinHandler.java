package to.epac.factorycraft.bossbarhealth.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;

public class JoinHandler implements Listener {

    private BossBarHealth plugin = BossBarHealth.inst();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = event.getPlayer();
            HealthBar bar = HealthBar.bars.get(player);

            if (plugin.getConfigManager().getWorldsHidden().contains(player.getWorld())) return;

            if (bar == null) {
                bar = new HealthBar();
                bar.update(player, null, 0.0, null, true);
            } else
                bar.update(player, null, 0.0, null, false);

        });
    }
}

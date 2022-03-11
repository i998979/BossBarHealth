package to.epac.factorycraft.bossbarhealth.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;

public class RespawnHandler implements Listener {

    private BossBarHealth plugin = BossBarHealth.inst();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!plugin.getConfigManager().isSelfEnabled()) return;

        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> {

            HealthBar bar = HealthBar.bars.get(player);

            if (bar == null) {
                bar = new HealthBar();
                bar.update(player, null, 0.0, null, true);
            } else
                bar.update(player, null, 0.0, null, false);
        });
    }
}

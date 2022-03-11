package to.epac.factorycraft.bossbarhealth.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar.BarType;

public class PlayerMoveHandler {

    private static BossBarHealth plugin = BossBarHealth.inst();

    public static void start() {

        BukkitRunnable self = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    HealthBar bar = HealthBar.bars.get(player);

                    if (bar == null) {
                        bar = new HealthBar();
                        bar.update(player, BarType.NORMAL, 0.0, null, true);
                    } else
                        bar.update(player, bar.getType(), bar.getLostgain(), bar.getCause(), false);
                }
            }
        };
        self.runTaskTimer(plugin, 0, plugin.getConfigManager().getFacingRefresh());


        BukkitRunnable enemy = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    HealthBar bar = HealthBar.bars.get(player);

                    if (bar != null) {
                        if (bar.getTarget() != null)
                            bar.updateEnemy(player, bar.getTarget(), bar.getEnemyType(), bar.getEnemyLostgain(), bar.getEnemyCause(), false);
                    }
                }
            }
        };
        enemy.runTaskTimer(plugin, 0, plugin.getConfigManager().getEnemyFacingRefresh());
    }
}

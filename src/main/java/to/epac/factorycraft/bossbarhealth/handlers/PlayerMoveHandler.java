package to.epac.factorycraft.bossbarhealth.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar.BarType;

public class PlayerMoveHandler {

    public static void start() {

        BukkitRunnable self = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    HealthBar bar = HealthBar.bars.get(player);

                    if (bar != null) {
                        if (!BossBarHealth.inst().getConfigManager().getWorldsHidden().contains(player.getWorld()))
                            bar.update(player, bar.getType(), bar.getLostgain(), bar.getCause(), false);
                        else {
                            bar.remove();
                            HealthBar.bars.remove(player);
                        }
                    } else {
                        if (!BossBarHealth.inst().getConfigManager().getWorldsHidden().contains(player.getWorld())) {
                            bar = new HealthBar();
                            bar.update(player, BarType.NORMAL, 0.0, null, true);
                        }
                    }
                }
            }
        };
        self.runTaskTimer(BossBarHealth.inst(), 0, BossBarHealth.inst().getConfigManager().getFacingRefresh());


        BukkitRunnable enemy = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    HealthBar bar = HealthBar.bars.get(player);

                    if (bar != null) {
                        if (bar.getTarget() != null) {
                            if (!BossBarHealth.inst().getConfigManager().getWorldsHidden().contains(player.getWorld()))
                                bar.updateEnemy(player, bar.getTarget(), bar.getEnemyType(), bar.getEnemyLostgain(), bar.getEnemyCause(), false);
                            else {
                                bar.remove();
                                HealthBar.bars.remove(player);
                            }
                        }
                    }
                }
            }
        };
        enemy.runTaskTimer(BossBarHealth.inst(), 0, BossBarHealth.inst().getConfigManager().getEnemyFacingRefresh());
    }
}

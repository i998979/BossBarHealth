package to.epac.factorycraft.bossbarhealth.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.scheduler.BukkitRunnable;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar.BarType;

import java.util.Map;

public class RegainHealthHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegainHealth(EntityRegainHealthEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();

        Bukkit.getScheduler().runTask(BossBarHealth.inst(), () -> {

            // If the entity regaining health is player
            if (entity instanceof Player) {
                Player player = (Player) entity;

                // If SelfBar is not enabled
                if (!BossBarHealth.inst().getConfigManager().isSelfEnabled()) return;

                HealthBar bar = HealthBar.bars.get(player);

                if (bar != null) {
                    // Update with HpGain format
                    bar.update(player, BarType.HPGAIN, event.getAmount(), null, false);
                    bar.setLastUpdate(System.currentTimeMillis());

                    // Attempt to update SelfBar after fade-out time
                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (bar.attemptUpdate(player)) {
                                this.cancel();
                            }
                        }
                    };
                    runnable.runTaskTimer(BossBarHealth.inst(), BossBarHealth.inst().getConfigManager().getDurationNormal(), 0);
                }
            }


            // If EnemyBar is not enabled
            if (!BossBarHealth.inst().getConfigManager().isEnemyEnabled()) return;
            // If entity's type is not in blacklist
            if (BossBarHealth.inst().getConfigManager().getBlacklist().contains(entity.getType().toString())) return;

            for (Map.Entry<Player, HealthBar> entry : HealthBar.bars.entrySet()) {
                Player player = entry.getKey();
                HealthBar bar = entry.getValue();

                if (bar.getTarget() != null && bar.getTarget().equals(entity)) {
                    bar.updateEnemy(player, entity, BarType.HPGAIN, event.getAmount(), null, false);

                    int delay = BossBarHealth.inst().getConfigManager().getEnemyDurNormal();

                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (bar.attemptRemove(delay)) {
                                if (BossBarHealth.inst().getConfigManager().isSelfEnabled()) {
                                    if (!HealthBar.hide.contains(player.getUniqueId())) {
                                        bar.getSelfBar().addPlayer(player);
                                    }
                                }
                                this.cancel();
                            }
                        }
                    };
                    runnable.runTaskTimer(BossBarHealth.inst(), delay, 0);
                }
            }
        });
    }
}

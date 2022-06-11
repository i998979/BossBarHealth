package to.epac.factorycraft.bossbarhealth.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar.BarType;

import java.util.Map;

public class DamageHandler implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity victim = event.getEntity();

        if (event.isCancelled()) return;
        // If victim is not LivingEntity (eg. Ender Crystal)
        if (!(victim instanceof LivingEntity)) return;


        // If the victim is any player, update his bar
        if (victim instanceof Player) {

            Player player = (Player) victim;
            HealthBar bar = HealthBar.bars.get(player);

            if (bar != null) {
                bar.update(player, BarType.HPLOST, event.getFinalDamage() * -1, event.getCause(), false);
                bar.setLastUpdate(System.currentTimeMillis());


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
        if (BossBarHealth.inst().getConfigManager().getBlacklist().contains(victim.getType().toString())) return;
        if (BossBarHealth.inst().getConfigManager().getWorldsHidden().contains(victim.getWorld())) return;


        // If Entity Damage By Entity
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;

            Player damager = null;

            // If entity damaged by player
            if (edbeEvent.getDamager() instanceof Player) {
                // If damager isn't victim himself
                if (!edbeEvent.getDamager().equals(victim))
                    damager = (Player) edbeEvent.getDamager();
            }
            // If player damaged by entity projectile
            else if (edbeEvent.getDamager() instanceof Projectile) {
                Projectile proj = (Projectile) edbeEvent.getDamager();
                // If shooter is player
                if (proj.getShooter() instanceof Player)
                    // If shooter is not damaging himself
                    if (!proj.getShooter().equals(victim))
                        damager = (Player) proj.getShooter();
            }

            if (damager != null) {
                HealthBar bar = HealthBar.bars.get(damager);

                if (bar != null) {
                    boolean create = bar.getTarget() == null;
                    bar.updateEnemy(damager, (LivingEntity) victim, BarType.HPLOST, event.getFinalDamage() * -1, event.getCause(), create);
                    bar.setEnemyLastUpdate(System.currentTimeMillis());
                }
            }
        }


        Bukkit.getScheduler().runTask(BossBarHealth.inst(), () -> {

            final int delay = (((LivingEntity) victim).getHealth() <= 0)
                    ? BossBarHealth.inst().getConfigManager().getEnemyDurZero()
                    : BossBarHealth.inst().getConfigManager().getEnemyDurNormal();

            // Update everyone's EnemyBar if their target is the victim
            for (Map.Entry<Player, HealthBar> entry : HealthBar.bars.entrySet()) {
                Player player = entry.getKey();
                HealthBar bar = entry.getValue();

                if (bar.getTarget() != null && bar.getTarget().equals(victim)) {

                    bar.updateEnemy(player, (LivingEntity) victim, BarType.HPLOST, event.getFinalDamage() * -1, event.getCause(), false);

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
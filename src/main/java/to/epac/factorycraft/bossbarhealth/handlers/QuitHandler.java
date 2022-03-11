package to.epac.factorycraft.bossbarhealth.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;

public class QuitHandler implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HealthBar bar = HealthBar.bars.get(player);

        if (bar != null) {
            bar.remove();
            HealthBar.bars.remove(player);
        }
    }
}

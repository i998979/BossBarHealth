package to.epac.factorycraft.bossbarhealth.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.hpbar.HealthBar;
import to.epac.factorycraft.bossbarhealth.utils.ChatColor;

import java.util.UUID;

public class Commands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            helpPage(sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("BossBarHealth.Admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
                return false;
            }

            BossBarHealth.inst().getConfigManager().load();
            BossBarHealth.inst().getLangManager().load();

            HealthBar.removeAll();

            if (BossBarHealth.inst().getConfigManager().isSelfEnabled() || BossBarHealth.inst().getConfigManager().isEnemyEnabled())
                HealthBar.updateAll();

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7BossBarHealth &8➽ &fConfiguration reloaded."));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute command.");
            return false;
        }


        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (args[0].equalsIgnoreCase("help")) {
            helpPage(player);
        } else if (args[0].equalsIgnoreCase("show")) {
            if (!player.hasPermission("BossBarHealth.Admin") && !player.hasPermission("BossBarHealth.Show")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
                return false;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7BossBarHealth &8➽ &aShowing health bar."));

            HealthBar.hide.remove(uuid);

            HealthBar bar = HealthBar.bars.get(player);
            if (bar != null) {
                if (BossBarHealth.inst().getConfigManager().isSelfEnabled())
                    if (!bar.getSelfBar().getPlayers().contains(player))
                        bar.getSelfBar().addPlayer(player);
            } else {
                bar = new HealthBar();
                bar.update(player, null, 0, null, true);
            }

        } else if (args[0].equalsIgnoreCase("hide")) {
            if (!player.hasPermission("BossBarHealth.Admin") && !player.hasPermission("BossBarHealth.Show")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
                return false;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7BossBarHealth &8➽ &cHiding health bar."));

            if (!HealthBar.hide.contains(uuid))
                HealthBar.hide.add(uuid);

            HealthBar bar = HealthBar.bars.get(player);
            if (bar != null) {
                bar.remove();
                bar.removeEnemy();
            }
        }
        return true;
    }

    public void helpPage(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8➽-----➽ &7BossBarHealth &8➽-----➽"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8➽ &c<>: Required &d[]: Optional"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8➽ &b/bbh Show&b: &3Show BossBarHealth to player."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8➽ &b/bbh Hide&b: &3Hide BossBarHealth from player."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8➽ &b/bbh Help&b: &3Show the help page."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8➽-----➽ &7BossBarHealth &8➽-----➽"));
    }
}

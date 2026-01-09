package to.epac.factorycraft.bossbarhealth.hpbar;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.raidstone.wgevents.WorldGuardEvents;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import to.epac.factorycraft.bossbarhealth.BossBarHealth;
import to.epac.factorycraft.bossbarhealth.utils.ChatColor;
import to.epac.factorycraft.bossbarhealth.utils.Utils;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HealthBar {

    public enum BarType {
        NORMAL, HPLOST, HPGAIN
    }

    private BossBarHealth plugin = BossBarHealth.inst();

    public static HashMap<Player, HealthBar> bars = new HashMap<>();
    public static List<UUID> hide = new ArrayList<>();

    private BossBar self;
    private BossBar enemy;
    private LivingEntity target;
    private long lastUpdate;
    private long e_lastUpdate;

    private double lostgain;
    private BarType type;
    private DamageCause cause;
    private double e_lostgain;
    private BarType e_type;
    private DamageCause e_cause;

    public HealthBar() {
    }

    public HealthBar(BossBar self) {
        this.self = self;
    }

    public HealthBar(BossBar self, BossBar enemy, LivingEntity target) {
        this.self = self;
        this.enemy = enemy;
        this.target = target;
    }


    public static void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!hide.contains(player.getUniqueId())) {

                HealthBar bar = bars.get(player);

                if (BossBarHealth.inst().getConfigManager().getWorldsHidden().contains(player.getWorld())) continue;

                if (bar == null) {
                    bar = new HealthBar();
                    bar.update(player, null, 0.0, null, true);
                } else
                    bar.update(player, null, 0.0, null, false);
            }
        }
    }

    public static void removeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!hide.contains(player.getUniqueId())) {

                HealthBar bar = bars.get(player);

                if (bar != null) {
                    bar.remove();
                    bar.removeEnemy();

                    bars.remove(player);
                }
            }
        }
    }


    /**
     * Update SelfBar when it is expired (elapsed time - expire time >= -20)
     *
     * @param player Player to update
     */
    public boolean attemptUpdate(Player player) {
        long elapsedTime = System.currentTimeMillis() - lastUpdate;
        long confVal = plugin.getConfigManager().getEnemyDurNormal() / 20 * 1000L;

        if (elapsedTime - confVal >= 0) {
            update(player, null, 0.0, null, false);
            return true;
        }

        return false;
    }


    /**
     * Update/create SelfBar for player
     *
     * @param player   Player to update/create
     * @param type     Bar type to apply, eg. HPLOST when health lost, null when it depends on lostgain
     * @param lostgain Hp lost/gained
     * @param cause    Cause of the damage
     * @param create   Create a new boss bar object or not
     */
    public void update(Player player, @Nullable BarType type, double lostgain, @Nullable DamageCause cause, boolean create) {
        double hp = player.getHealth() * plugin.getConfigManager().getScale();
        double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getScale();
        double change = Math.abs(lostgain) * plugin.getConfigManager().getScale();


        // Apply decimal format
        String pattern = "#";
        for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++)
            pattern += (i == 0 ? "." : "#");
        DecimalFormat df = new DecimalFormat(pattern);


        String title = "";

        if (hp <= 0 && !plugin.getConfigManager().getFormatDead().isEmpty())
            title = plugin.getConfigManager().getFormatDead();
        else if ((type == null && lostgain < 0.0) || type == BarType.HPLOST)
            title = plugin.getConfigManager().getFormatHpLost();
        else if ((type == null && lostgain > 0.0) || type == BarType.HPGAIN)
            title = plugin.getConfigManager().getFormatHpGain();
        else if ((type == null && lostgain == 0.0) || type == BarType.NORMAL)
            title = plugin.getConfigManager().getFormatNormal();


        this.type = type;
        this.lostgain = lostgain;
        this.cause = cause;


        // Update placeholders
        title = title
                // Change
                .replaceAll("%change%", df.format(change))
                .replaceAll("%change_percent%", df.format(change / max * 100))

                .replaceAll("%name%", player.getName())
                .replaceAll("%displayname%", player.getDisplayName())
                .replaceAll("%hp%", df.format(hp))
                .replaceAll("%max%", df.format(max))

                // Affected by decimal places and scale
                .replaceAll("%hp_percent%", df.format(hp / max * 100))
                .replaceAll("%max_percent%", df.format(100))

                .replaceAll("%hp_int%", (int) Math.ceil(hp) + "")
                .replaceAll("%max_int%", (int) Math.ceil(max) + "")

                .replaceAll("%direction_cardinalfull%", Utils.getDirection(player.getLocation().getYaw(), "CARDINAL_FULL"))
                .replaceAll("%direction_ordinalfull%", Utils.getDirection(player.getLocation().getYaw(), "ORDINAL_FULL"))
                .replaceAll("%direction_cardinal%", Utils.getDirection(player.getLocation().getYaw(), "CARDINAL"))
                .replaceAll("%direction_ordinal%", Utils.getDirection(player.getLocation().getYaw(), "ORDINAL"))
                .replaceAll("%direction_number%", Utils.getDirection(player.getLocation().getYaw(), "NUMBER"));
        title = ChatColor.translateAlternateColorCodes('&', title);
        // If PlaceholderAPI is installed
        if (plugin.usePapi())
            title = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, title);


        // ######################### //
        // BarColor, BarStyle
        // ######################### //
        // Update BarColor and BarStyle based on DamageCause
        BarColor color = plugin.getConfigManager().getColor();
        BarStyle style = plugin.getConfigManager().getStyle();

        // If WorldGuard is installed and hook enabled
        if (plugin.useWorldGuard() && plugin.getConfigManager().isWgEnabled()) {
            // Loop through all regions player is at
            for (String region : WorldGuardEvents.getRegionsNames(player.getUniqueId())) {
                // Get the setting of the region
                BarSetting wgSetting = plugin.getConfigManager().getWgSetting().get(region);
                // If everything is not null
                if (wgSetting != null && wgSetting.getColor() != null && wgSetting.getStyle() != null) {
                    // Apply settings
                    color = wgSetting.getColor();
                    style = wgSetting.getStyle();
                    break;
                }
            }
        }

        // If damage cause is not null
        if (cause != null) {
            // Get settings of the damage cause
            BarSetting setting = plugin.getConfigManager().getCauseSetting().get(cause.name());
            // If the setting exists
            if (setting != null) {
                color = setting.getColor();
                style = setting.getStyle();
            } else {
                color = plugin.getConfigManager().getCauseSetting().get("Default").getColor();
                style = plugin.getConfigManager().getCauseSetting().get("Default").getStyle();
            }
        }
        // If creating a new boss bar, apply settings along with the bar
        if (create)
            self = Bukkit.createBossBar(title, color, style, new BarFlag[0]);
            // Otherwise, set color and style only
        else {
            self.setColor(color);
            self.setStyle(style);
        }


        // ######################### //
        // Bar progress
        // ######################### //
        // Update SelfBar progress
        if (hp / max > 1.0)
            self.setProgress(1.0);
        else if (hp / max < 0.0)
            self.setProgress(0.0);
        else
            self.setProgress(hp / max);


        // ######################### //
        // Bar title
        // ######################### //
        // If we are creating a new HealthBar
        if (create) {
            // If SelfBar is enabled
            if (plugin.getConfigManager().isSelfEnabled())
                // If player didn't hide his own bar
                if (!hide.contains(player.getUniqueId()))
                    // Add him to BossBar display list
                    self.addPlayer(player);

            // Update lastUpdate
            lastUpdate = System.currentTimeMillis();

            // Put it in list
            bars.put(player, this);
        }
        // If we are not creating a new one, then just update the title
        else {
            self.setTitle(title);
        }
    }


    /**
     * Update/create EnemyBar for player
     *
     * @param player   Player to update/create
     * @param target   Player's target
     * @param type     Bar type to apply, eg. HPLOST when health lost, null when it depends on lostgain
     * @param lostgain Hp lost/gained
     * @param cause    Cause of the damage
     * @param create   Create a new boss bar object or not
     */
    public void updateEnemy(Player player, LivingEntity target, @Nullable BarType type, double lostgain, @Nullable DamageCause cause, boolean create) {
        if (plugin.getConfigManager().getOverride())
            self.removePlayer(player);

        double hp = player.getHealth() * plugin.getConfigManager().getScale();
        double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getScale();

        double e_hp = target.getHealth() * plugin.getConfigManager().getEnemyScale();
        double e_max = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * plugin.getConfigManager().getEnemyScale();
        double e_change = Math.abs(lostgain) * plugin.getConfigManager().getEnemyScale();


        String pattern = "#";
        for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++)
            pattern += (i == 0 ? "." : "#");
        DecimalFormat df = new DecimalFormat(pattern);


        String title = "";
        if (hp <= 0 && !plugin.getConfigManager().getEnemyFormatSelfDead().isEmpty())
            title = plugin.getConfigManager().getEnemyFormatSelfDead();
        else if (e_hp <= 0 && !plugin.getConfigManager().getEnemyFormatDead().isEmpty())
            title = plugin.getConfigManager().getEnemyFormatDead();
        else if ((type == null && lostgain < 0.0) || type == BarType.HPLOST)
            title = plugin.getConfigManager().getEnemyFormatHpLost();
        else if ((type == null && lostgain >= 0.0) || type == BarType.HPGAIN)
            title = plugin.getConfigManager().getEnemyFormatHpGain();


        this.e_lostgain = lostgain;
        this.e_type = type;
        this.e_cause = cause;


        title = title
                // Change
                .replaceAll("%e_change%", df.format(e_change))
                .replaceAll("%e_change_percent%", df.format(e_change / e_max * 100))


                // Self
                .replaceAll("%name%", player.getName())
                .replaceAll("%displayname%", player.getDisplayName())
                .replaceAll("%hp%", df.format(hp))
                .replaceAll("%max%", df.format(max))

                // Affected by decimal places and scale
                .replaceAll("%hp_percent%", df.format(hp / max * 100))
                .replaceAll("%max_percent%", df.format(100))

                .replaceAll("%hp_int%", (int) Math.ceil(hp) + "")
                .replaceAll("%max_int%", (int) Math.ceil(max) + "")

                .replaceAll("%direction_cardinalfull%", Utils.getDirection(player.getLocation().getYaw(), "CARDINAL_FULL"))
                .replaceAll("%direction_ordinalfull%", Utils.getDirection(player.getLocation().getYaw(), "ORDINAL_FULL"))
                .replaceAll("%direction_cardinal%", Utils.getDirection(player.getLocation().getYaw(), "CARDINAL"))
                .replaceAll("%direction_ordinal%", Utils.getDirection(player.getLocation().getYaw(), "ORDINAL"))
                .replaceAll("%direction_number%", Utils.getDirection(player.getLocation().getYaw(), "NUMBER"))


                // Enemy
                .replaceAll("%e_hp%", df.format(e_hp))
                .replaceAll("%e_max%", df.format(e_max))

                // Affected by decimal places and scale
                .replaceAll("%e_hp_percent%", df.format(e_hp / e_max * 100))
                .replaceAll("%e_max_percent%", df.format(100))

                .replaceAll("%e_hp_int%", (int) Math.ceil(e_hp) + "")
                .replaceAll("%e_max_int%", (int) Math.ceil(e_max) + "")
                .replaceAll("%e_type%", target.getType() + "")

                .replaceAll("%e_direction_cardinalfull%", Utils.getDirection(target.getLocation().getYaw(), "CARDINAL_FULL"))
                .replaceAll("%e_direction_ordinalfull%", Utils.getDirection(target.getLocation().getYaw(), "ORDINAL_FULL"))
                .replaceAll("%e_direction_cardinal%", Utils.getDirection(target.getLocation().getYaw(), "CARDINAL"))
                .replaceAll("%e_direction_ordinal%", Utils.getDirection(target.getLocation().getYaw(), "ORDINAL"))
                .replaceAll("%e_direction_number%", Utils.getDirection(target.getLocation().getYaw(), "NUMBER"));

        if (target instanceof Player) {
            // If Citizens is installed and hook enabled
            if (plugin.useCitizens() && plugin.getConfigManager().isCitizensEnabled()
                    && CitizensAPI.getNPCRegistry().isNPC(target))
                title = title
                        .replaceAll("%e_name%", CitizensAPI.getNPCRegistry().getNPC(target).getName())
                        .replaceAll("%e_displayname%", CitizensAPI.getNPCRegistry().getNPC(target).getFullName());
            else
                title = title
                        .replaceAll("%e_name%", target.getName())
                        .replaceAll("%e_displayname%", ((Player) target).getDisplayName());

        }
        // If MythicMobs is installed and hook enabled
        else if (plugin.useMythicMobs() && plugin.getConfigManager().isMythicMobsEnabled()
                && MythicBukkit.inst().getAPIHelper().isMythicMob(target))
            title = title
                    .replaceAll("%e_name%", MythicBukkit.inst().getAPIHelper().getMythicMobInstance(target).getType().getInternalName())
                    .replaceAll("%e_displayname%", MythicBukkit.inst().getAPIHelper().getMythicMobInstance(target).getDisplayName() == null ? ""
                            : MythicBukkit.inst().getAPIHelper().getMythicMobInstance(target).getDisplayName());
        else
            title = title
                    .replaceAll("%e_name%", plugin.langManager.getText(target))
                    .replaceAll("%e_displayname%", target.getCustomName() != null ?
                            target.getCustomName() : plugin.langManager.getText(target));


        title = ChatColor.translateAlternateColorCodes('&', title);

        if (plugin.usePapi())
            title = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, title);


        BarColor color = plugin.getConfigManager().getEnemyColor();
        BarStyle style = plugin.getConfigManager().getEnemyStyle();

        if (create)
            enemy = Bukkit.createBossBar(title, color, style, new BarFlag[0]);
        else {
            enemy.setColor(color);
            enemy.setStyle(style);
        }

        if (e_hp / e_max > 1.0)
            enemy.setProgress(1.0);
        else if (e_hp / e_max < 0.0)
            enemy.setProgress(0.0);
        else
            enemy.setProgress(e_hp / e_max);


        this.target = target;


        if (create) {
            if (plugin.getConfigManager().isEnemyEnabled())
                if (!hide.contains(player.getUniqueId()))
                    enemy.addPlayer(player);

            e_lastUpdate = System.currentTimeMillis();

            bars.put(player, this);
        } else {
            enemy.setTitle(title);
        }

    }


    public boolean attemptRemove(int delay) {
        double elapsedTime = System.currentTimeMillis() - e_lastUpdate + 1;
        double confVal = delay / 20.0 * 1000L;
        if (elapsedTime - confVal >= 0) {
            removeEnemy();
            return true;
        }
        return false;
    }

    public void remove() {
        this.lostgain = 0;
        this.type = null;
        this.cause = null;

        if (self != null)
            self.removeAll();
    }

    public void removeEnemy() {
        this.lostgain = 0;
        this.type = null;
        this.e_cause = null;

        if (enemy != null) {
            enemy.removeAll();
            enemy = null;
            target = null;
        }
    }


    public BossBar getSelfBar() {
        return self;
    }

    public BossBar getEnemyBar() {
        return enemy;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getEnemyLastUpdate() {
        return e_lastUpdate;
    }

    public void setEnemyLastUpdate(long lastUpdate) {
        this.e_lastUpdate = lastUpdate;
    }


    public double getLostgain() {
        return lostgain;
    }

    public void setLostgain(double lostgain) {
        this.lostgain = lostgain;
    }

    public BarType getType() {
        return type;
    }

    public void setType(BarType type) {
        this.type = type;
    }

    public DamageCause getCause() {
        return cause;
    }

    public void setCause(DamageCause cause) {
        this.cause = cause;
    }


    public double getEnemyLostgain() {
        return e_lostgain;
    }

    public void setEnemyLostgain(double lostgain) {
        this.e_lostgain = lostgain;
    }

    public BarType getEnemyType() {
        return e_type;
    }

    public void setEnemyType(BarType type) {
        this.e_type = type;
    }

    public DamageCause getEnemyCause() {
        return e_cause;
    }

    public void setEnemyCause(DamageCause cause) {
        this.e_cause = cause;
    }
}

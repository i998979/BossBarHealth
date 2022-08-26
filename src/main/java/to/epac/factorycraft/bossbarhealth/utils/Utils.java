package to.epac.factorycraft.bossbarhealth.utils;

import to.epac.factorycraft.bossbarhealth.BossBarHealth;

import java.text.DecimalFormat;

import static to.epac.factorycraft.bossbarhealth.config.ConfigManager.*;

public class Utils {

    private static BossBarHealth plugin = BossBarHealth.inst();

    /**
     * Convert yaw to direction string
     * CARDINAL_FULL: North, East, South, West
     * ORDINAL_FULL: CARDINAL_FULL and NorthEast, SouthEast, etc...
     * CARDINAL: N, E, S, W
     * ORDINAL: CARDINAL and NE, SE, etc...
     * NUMBER: 0-360 degrees
     *
     * @param yaw  Yaw to convert
     * @param type Output type
     * @return String of the direction
     */
    public static String getDirection(double yaw, String type) {

        yaw = (yaw + 360) % 360;

        if (type.equals("NUMBER")) {
            String pattern = "#";
            for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++)
                pattern += (i == 0 ? "." : "#");
            DecimalFormat df = new DecimalFormat(pattern);

            return df.format(yaw);
        }

        String direction = "";

        // ORDINAL (E, S, W, N)
        if (type.startsWith("ORDINAL")) {
            if (yaw >= 0 && yaw < 45) {
                if (type.equals("ORDINAL_FULL")) direction = full_s;
                else if (type.equals("ORDINAL")) direction = short_s;
            }
            if (45 <= yaw && yaw < 135) {
                if (type.equals("ORDINAL_FULL")) direction = full_w;
                else if (type.equals("ORDINAL")) direction = short_w;
            }
            if (135 <= yaw && yaw < 225) {
                if (type.equals("ORDINAL_FULL")) direction = full_n;
                else if (type.equals("ORDINAL")) direction = short_n;
            }
            if (225 <= yaw && yaw < 315) {
                if (type.equals("ORDINAL_FULL")) direction = full_e;
                else if (type.equals("ORDINAL")) direction = short_e;
            }
            if (315 <= yaw && yaw <= 360) {
                if (type.equals("ORDINAL_FULL")) direction = full_s;
                else if (type.equals("ORDINAL")) direction = short_s;
            }
        }


        // CARDINAL and NE, SE, etc...
        else if (type.startsWith("CARDINAL")) {
            if (0 <= yaw && yaw < 22.5) {
                if (type.equals("CARDINAL_FULL")) direction = full_s;
                else if (type.equals("CARDINAL")) direction = short_s;
            }
            if (22.5 <= yaw && yaw < 67.5) {
                if (type.equals("CARDINAL_FULL")) direction = full_sw;
                else if (type.equals("CARDINAL")) direction = short_sw;
            }
            if (67.5 <= yaw && yaw < 112.5) {
                if (type.equals("CARDINAL_FULL")) direction = full_w;
                else if (type.equals("CARDINAL")) direction = short_w;
            }
            if (112.5 <= yaw && yaw < 157.5) {
                if (type.equals("CARDINAL_FULL")) direction = full_nw;
                else if (type.equals("CARDINAL")) direction = short_nw;
            }
            if (157.5 <= yaw && yaw < 202.5) {
                if (type.equals("CARDINAL_FULL")) direction = full_n;
                else if (type.equals("CARDINAL")) direction = short_n;
            }
            if (202.5 <= yaw && yaw < 247.5) {
                if (type.equals("CARDINAL_FULL")) direction = full_ne;
                else if (type.equals("CARDINAL")) direction = short_ne;
            }
            if (247.5 <= yaw && yaw < 292.5) {
                if (type.equals("CARDINAL_FULL")) direction = full_e;
                else if (type.equals("CARDINAL")) direction = short_e;
            }
            if (292.5 <= yaw && yaw < 337.5) {
                if (type.equals("CARDINAL_FULL")) direction = full_se;
                else if (type.equals("CARDINAL")) direction = short_se;
            }
            if (337.5 <= yaw && yaw <= 360) {
                if (type.equals("CARDINAL_FULL")) direction = full_s;
                else if (type.equals("CARDINAL")) direction = short_s;
            }
        }


        return direction;
    }
}

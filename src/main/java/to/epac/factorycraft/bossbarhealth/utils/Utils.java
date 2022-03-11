package to.epac.factorycraft.bossbarhealth.utils;

import to.epac.factorycraft.bossbarhealth.BossBarHealth;

import java.text.DecimalFormat;

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
                if (type.equals("ORDINAL_FULL")) direction = "South";
                else if (type.equals("ORDINAL")) direction = "S";
            }
            if (45 <= yaw && yaw < 135) {
                if (type.equals("ORDINAL_FULL")) direction = "West";
                else if (type.equals("ORDINAL")) direction = "W";
            }
            if (135 <= yaw && yaw < 225) {
                if (type.equals("ORDINAL_FULL")) direction = "North";
                else if (type.equals("ORDINAL")) direction = "N";
            }
            if (225 <= yaw && yaw < 315) {
                if (type.equals("ORDINAL_FULL")) direction = "East";
                else if (type.equals("ORDINAL")) direction = "E";
            }
            if (315 <= yaw && yaw <= 360) {
                if (type.equals("ORDINAL_FULL")) direction = "South";
                else if (type.equals("ORDINAL")) direction = "S";
            }
        }


        // CARDINAL and NE, SE, etc...
        else if (type.startsWith("CARDINAL")) {
            if (0 <= yaw && yaw < 22.5) {
                if (type.equals("CARDINAL_FULL")) direction = "South";
                else if (type.equals("CARDINAL")) direction = "S";
            }
            if (22.5 <= yaw && yaw < 67.5) {
                if (type.equals("CARDINAL_FULL")) direction = "SouthWest";
                else if (type.equals("CARDINAL")) direction = "SW";
            }
            if (67.5 <= yaw && yaw < 112.5) {
                if (type.equals("CARDINAL_FULL")) direction = "West";
                else if (type.equals("CARDINAL")) direction = "W";
            }
            if (112.5 <= yaw && yaw < 157.5) {
                if (type.equals("CARDINAL_FULL")) direction = "NorthWest";
                else if (type.equals("CARDINAL")) direction = "NW";
            }
            if (157.5 <= yaw && yaw < 202.5) {
                if (type.equals("CARDINAL_FULL")) direction = "North";
                else if (type.equals("CARDINAL")) direction = "N";
            }
            if (202.5 <= yaw && yaw < 247.5) {
                if (type.equals("CARDINAL_FULL")) direction = "NorthEast";
                else if (type.equals("CARDINAL")) direction = "NE";
            }
            if (247.5 <= yaw && yaw < 292.5) {
                if (type.equals("CARDINAL_FULL")) direction = "East";
                else if (type.equals("CARDINAL")) direction = "E";
            }
            if (292.5 <= yaw && yaw < 337.5) {
                if (type.equals("CARDINAL_FULL")) direction = "SouthEast";
                else if (type.equals("CARDINAL")) direction = "SE";
            }
            if (337.5 <= yaw && yaw <= 360) {
                if (type.equals("CARDINAL_FULL")) direction = "South";
                else if (type.equals("CARDINAL")) direction = "S";
            }
        }


        return direction;
    }
}

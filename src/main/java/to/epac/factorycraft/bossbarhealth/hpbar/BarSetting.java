package to.epac.factorycraft.bossbarhealth.hpbar;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class BarSetting {

    private BarColor color;
    private BarStyle style;

    public BarSetting() {
    }

    public BarSetting(BarColor color, BarStyle style) {
        this.color = color;
        this.style = style;
    }


    public BarColor getColor() {
        return color;
    }

    public void setColor(BarColor color) {
        this.color = color;
    }

    public BarStyle getStyle() {
        return style;
    }

    public void setStyle(BarStyle style) {
        this.style = style;
    }
}

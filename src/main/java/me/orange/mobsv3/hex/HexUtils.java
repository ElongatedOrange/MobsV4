package me.orange.mobsv3.hex;

import me.orange.mobsv3.MobsV3;
import net.md_5.bungee.api.ChatColor;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtils {
    public static final Pattern HEX_PATTERN = Pattern.compile(Objects.requireNonNull(MobsV3.MOBS.getConfig().getString("pattern")));

    public static String format(String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        while (matcher.find()) {
            string = string.replace(matcher.group(), "" + ChatColor.of(matcher.group()));
        }

        return string;
    }

}
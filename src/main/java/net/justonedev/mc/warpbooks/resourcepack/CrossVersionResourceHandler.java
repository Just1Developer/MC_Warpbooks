package net.justonedev.mc.warpbooks.resourcepack;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrossVersionResourceHandler implements ResourceHandler {

    private static final List<Integer> LEGACY_MODELS_VERSION_CUTOFF = List.of(1, 21, 3);
    private static final String DEFAULT_RESOURCE_DOMAIN = "warpbooks";

    private Resourcepack currentResourcePack;

    private final boolean isNewModelVersion;
    private final String resourceDomain;
    private final Plugin plugin;

    public CrossVersionResourceHandler(Plugin plugin) {
        this.plugin = plugin;
        this.isNewModelVersion = isNewModelVersion();
        this.resourceDomain = DEFAULT_RESOURCE_DOMAIN;
    }

    public boolean shouldUseNewModels() {
        return isNewModelVersion;
    }

    public String getResourceDomain() {
        return resourceDomain;
    }

    private static boolean isNewModelVersion() {
        String versionString = Bukkit.getVersion();
        Matcher matcher = Pattern.compile("^(\\d+(?:\\.\\d+)+).*").matcher(versionString);
        if (!matcher.matches()) return true;
        String parsedVersion = matcher.group(1);

        List<Integer> version = Arrays.stream(parsedVersion
                        .split("\\."))
                .map(Integer::parseInt)
                .toList();
        for (int i = 0; i < Math.min(version.size(), LEGACY_MODELS_VERSION_CUTOFF.size()); i++) {
            if (LEGACY_MODELS_VERSION_CUTOFF.get(i) < version.get(i)) {
                return true;
            } else if (LEGACY_MODELS_VERSION_CUTOFF.get(i) > version.get(i)) {
                return false;
            }
        }
        return false;
    }

}

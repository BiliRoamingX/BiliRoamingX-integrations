package app.revanced.bilibili.patches;

import android.net.Uri;

import java.util.regex.Pattern;

import app.revanced.bilibili.settings.Settings;
import app.revanced.bilibili.utils.LogHelper;

public class BLRoutePatch {
    private static final String STORY_ROUTER_PARAM = "&-Arouter=story";
    private static final Pattern playerPreloadRegex = Pattern.compile("&player_preload=[^&]*");

    public static Uri intercept(Uri uri) {
        String scheme = uri.getScheme();
        String url;
        if ("bilibili".equals(scheme)) {
            if (Settings.REPLACE_STORY_VIDEO.getBoolean()) {
                if ("story".equals(uri.getAuthority()))
                    return uri.buildUpon().authority("video").build();
                else if ("video".equals(uri.getAuthority()) && (url = uri.toString()).contains(STORY_ROUTER_PARAM))
                    return Uri.parse(url.replace(STORY_ROUTER_PARAM, ""));
            }
        } else if ("https".equals(scheme)) {
            boolean needHook = VideoQualityPatch.halfScreenQuality() != 0 || VideoQualityPatch.fullScreenQuality() != 0;
            if (needHook && (url = uri.toString()).startsWith("https://www.bilibili.com/bangumi/play"))
                return Uri.parse(playerPreloadRegex.matcher(url).replaceAll(""));
        }
        LogHelper.debug(() -> "Route uri: " + uri);
        return uri;
    }
}

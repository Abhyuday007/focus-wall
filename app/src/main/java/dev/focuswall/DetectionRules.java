package dev.focuswall;

final class DetectionRules {
    static final String RULESET_VERSION = "2026.11.21-1";
    static final String INSTAGRAM = "com.instagram.android";
    static final String YOUTUBE = "com.google.android.youtube";

    static final Profile INSTAGRAM_PROFILE = new Profile(
            INSTAGRAM,
            "455.0.0.51.72 · 457.0.0.0.60",
            new long[]{386113969L, 386305570L},
            INSTAGRAM + ":id/clips_tab",
            new String[]{
                    INSTAGRAM + ":id/feed_tab",
                    INSTAGRAM + ":id/direct_tab",
                    INSTAGRAM + ":id/search_tab",
                    INSTAGRAM + ":id/profile_tab"
            });

    static final Profile YOUTUBE_PROFILE = new Profile(
            YOUTUBE,
            "21.45.83 · 21.46.522",
            new long[]{1561311006L, 1561314006L},
            YOUTUBE + ":id/reel_watch_fragment_root",
            new String[0]);

    static Profile forPackage(String packageName) {
        if (INSTAGRAM.equals(packageName)) return INSTAGRAM_PROFILE;
        if (YOUTUBE.equals(packageName)) return YOUTUBE_PROFILE;
        return null;
    }

    static final class Profile {
        final String packageName;
        final String testedVersionName;
        final long[] supportedVersionCodes;
        final String feedResourceId;
        final String[] safeResourceIds;

        Profile(String packageName, String testedVersionName, long[] supportedVersionCodes,
                String feedResourceId, String[] safeResourceIds) {
            this.packageName = packageName;
            this.testedVersionName = testedVersionName;
            this.supportedVersionCodes = supportedVersionCodes;
            this.feedResourceId = feedResourceId;
            this.safeResourceIds = safeResourceIds;
        }

        boolean supports(long versionCode) {
            for (long supported : supportedVersionCodes) {
                if (versionCode == supported) return true;
            }
            return false;
        }
    }

    private DetectionRules() {}
}

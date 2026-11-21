package dev.focuswall;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

final class InstalledAppVersion {
    final String name;
    final long code;

    private InstalledAppVersion(String name, long code) {
        this.name = name;
        this.code = code;
    }

    static InstalledAppVersion read(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return new InstalledAppVersion(info.versionName == null ? "unknown" : info.versionName,
                    info.getLongVersionCode());
        } catch (PackageManager.NameNotFoundException ignored) {
            return new InstalledAppVersion("not installed", -1);
        }
    }
}

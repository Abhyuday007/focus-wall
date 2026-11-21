package dev.focuswall;

final class SurfaceSnapshot {
    final boolean feedVisible;
    final boolean knownSafeVisible;
    final boolean rootBelongsToPackage;
    final boolean instagramSearchSelected;
    final boolean pagerVisible;

    SurfaceSnapshot(boolean feedVisible, boolean knownSafeVisible, boolean rootBelongsToPackage,
            boolean instagramSearchSelected, boolean pagerVisible) {
        this.feedVisible = feedVisible;
        this.knownSafeVisible = knownSafeVisible;
        this.rootBelongsToPackage = rootBelongsToPackage;
        this.instagramSearchSelected = instagramSearchSelected;
        this.pagerVisible = pagerVisible;
    }
}

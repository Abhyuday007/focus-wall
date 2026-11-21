package dev.focuswall;

final class LimitExitPolicy {
    enum Destination { INSTAGRAM_HOME, YOUTUBE_HOME }

    static Destination destination(boolean youtubeShorts) {
        return youtubeShorts ? Destination.YOUTUBE_HOME : Destination.INSTAGRAM_HOME;
    }

    static String buttonLabel(boolean youtubeShorts) {
        return youtubeShorts ? "Go to YouTube Home" : "Back to Instagram Home";
    }

    private LimitExitPolicy() {}
}

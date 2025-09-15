package GameLogic;

import com.badlogic.gdx.utils.TimeUtils;

public final class Time {

    private long startedMs = 0L;
    private long accumulatedMs = 0L;
    private boolean running = false;

    public synchronized void start() {
        accumulatedMs = 0L;
        startedMs = TimeUtils.millis();
        running = true;
    }

    public synchronized void pause() {
        if (!running) {
            return;
        }
        accumulatedMs += TimeUtils.millis() - startedMs;
        running = false;
    }

    public synchronized void resume() {
        if (running) {
            return;
        }
        startedMs = TimeUtils.millis();
        running = true;
    }

    public synchronized void reset() {
        accumulatedMs = 0L;
        running = false;
    }

    public synchronized long elapsedMs() {
        return running ? accumulatedMs + (TimeUtils.millis() - startedMs) : accumulatedMs;
    }

    public synchronized String mmss() {
        long ms = elapsedMs();
        long s = ms / 1000;
        long mm = s / 60;
        long ss = s % 60;
        return String.format("%02d:%02d", mm, ss);
    }

    public synchronized boolean isRunning() {
        return running;
    }
}

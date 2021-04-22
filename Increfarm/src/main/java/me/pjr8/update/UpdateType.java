package me.pjr8.update;

/**
 * The types of update delays to provide in the {@link UpdateEvent}.
 *
 * @author Thortex
 */
public enum UpdateType {
    MIN_64(3840000L),
    MIN_32(1920000L),
    MIN_16(960000L),
    MIN_08(480000L),
    MIN_04(240000L),
    MIN_02(120000L),
    MIN_01(60000L),
    SLOWEST(32000L),
    SLOWER(16000L),
    SLOW(4000L),
    SEC(1000L),
    FAST(500L),
    FASTER(250L),
    FASTEST(125L),
    TICK(49L);

    private long time;
    private long last;
    private long timeCount;
    private long timeSpent;

    UpdateType(long time) {
        this.time = time;
        this.last = System.currentTimeMillis();
    }



    /**
     * Check if the time between the last update and
     * current time has elapsed the given delay time.
     *
     * @return if it has elapsed the given delay time
     */
    public boolean elapsed() {
        if (System.currentTimeMillis() - last > time) {
            last = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     * Start a stopwatch.
     */
    public void startStopwatch() {
        this.timeCount = System.currentTimeMillis();
    }

    /**
     * Stop a stopwatch.
     */
    public void stopStopwatch() {
        this.timeSpent += System.currentTimeMillis() - timeCount;
    }
}

package org.samcrow.antrecorder;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

/**
 * Counts up in seconds
 * <p>
 * @author Sam Crow
 */
public class SubSecondTimer {

    private long startTime = 0;

    private long accumulatedTime = 0;

    /**
     * Starts counting
     */
    public void start() {
        startTime = System.currentTimeMillis();
        startNotifying();
    }

    /**
     * Pauses counting
     */
    public void pause() {
        accumulatedTime += System.currentTimeMillis() - startTime;
        startTime = 0;
        stopNotifying();
    }

    /**
     * Resets time to 0 seconds
     */
    public void reset() {
        accumulatedTime = 0;
        startTime = 0;
    }

    /**
     * Returns the elapsed time in seconds
     * <p>
     * @return
     */
    public final double getTime() {
        if(startTime != 0) {
            return (accumulatedTime + System.currentTimeMillis() - startTime) / 1000d;
        }
        else {
            return accumulatedTime;
        }
    }

    // Change notification infrastructure
    private Timer timer;

    public interface TimeChangeListener {

        public void onTimeChanged(double newTime);

    }

    private TimeChangeListener listener;

    public void setTimeChangeListener(TimeChangeListener listener) {
        this.listener = listener;
    }

    private void startNotifying() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (listener != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            listener.onTimeChanged(getTime());
                        }
                    });
                }
            }
        }, 100, 100);
    }

    private void stopNotifying() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}

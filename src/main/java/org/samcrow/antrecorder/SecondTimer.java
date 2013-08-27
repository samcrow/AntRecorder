package org.samcrow.antrecorder;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Counts up in seconds
 * @author Sam Crow
 */
public class SecondTimer {
    
    /**
     * Elapsed time in seconds, rounded down
     */
    private final IntegerProperty time = new SimpleIntegerProperty(0);
    
    private Timer timer;
    
    private static final long MILLISECONDS_PER_SECOND = 1000;
    
    /**
     * Starts counting
     */
    public void start() {
        resetTimer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setTime(getTime() + 1);
                    }
                });
            }
        }, MILLISECONDS_PER_SECOND, MILLISECONDS_PER_SECOND);
    }
    
    /**
     * Pauses counting
     */
    public void pause() {
        resetTimer();
    }
    
    /**
     * Resets time to 0 seconds
     */
    public void reset() {
        setTime(0);
    }
    
    /**
     * Ensures that the timer is not running anything and is ready
     * to use
     */
    private void resetTimer() {
        //Cancel and remove a timer that exists
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        
        timer = new Timer("Second counter");
    }
    
    public final int getTime() {
        return time.get();
    }
    
    public final void setTime(int newTime) {
        time.set(newTime);
    }
    
    public final IntegerProperty timeProperty() {
        return time;
    }
}

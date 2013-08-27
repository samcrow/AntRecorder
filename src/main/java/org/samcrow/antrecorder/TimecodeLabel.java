package org.samcrow.antrecorder;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;


/**
 * A label that displays a timecode in minutes and seconds.
 * 
 * Bind {@link #timeProperty()} to another property and the displayed time
 * will be updated
 * 
 * @author Sam Crow
 */
public class TimecodeLabel extends Label {
    
    /**
     * Elapsed time, in seconds
     */
    private final IntegerProperty time = new SimpleIntegerProperty();
    
    
    public TimecodeLabel() {
        time.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setText(formatTime(newValue.intValue()));
            }
        });
        setText(formatTime(0));
    }
    
    
    static String formatTime(int seconds) {
        
        final int SECONDS_PER_MINUTE = 60;
        
        int minutes = 0;
        
        while (seconds >= SECONDS_PER_MINUTE) {
            seconds -= SECONDS_PER_MINUTE;
            minutes++;
        }
        
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public final IntegerProperty timeProperty() {
        return time;
    }
}

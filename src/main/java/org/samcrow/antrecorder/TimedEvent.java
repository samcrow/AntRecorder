package org.samcrow.antrecorder;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Stores a time and an event type
 * @author Sam Crow
 */
public class TimedEvent {
    
    /**
     * The time of the event, in seconds
     */
    private final int time;
    
    private final Event event;

    public TimedEvent(int time, Event event) {
        this.time = time;
        this.event = event;
    }

    /**
     * 
     * @return The time of this event, in seconds
     */
    public int getTime() {
        return time;
    }

    public Event getEvent() {
        return event;
    }
    
    
    
    public static enum Event {
        AntIn ("Ant in", new KeyCodeCombination(KeyCode.I)),
        AntOut ("Ant out", new KeyCodeCombination(KeyCode.O)),
        ;
        
        private final String humanFriendlyName;
        
        /**
         * The key combination that can be used to trigger this button
         */
        private final KeyCombination key;

        private Event(String humanFriendlyName) {
            this.humanFriendlyName = humanFriendlyName;
            key = null;
        }

        private Event(String humanFriendlyName, KeyCombination key) {
            this.humanFriendlyName = humanFriendlyName;
            this.key = key;
        }

        public KeyCombination getKey() {
            return key;
        }

        public String getHumanFriendlyName() {
            return humanFriendlyName;
        }
    }
}

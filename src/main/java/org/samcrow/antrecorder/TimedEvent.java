package org.samcrow.antrecorder;

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
        AntIn ("Ant in"),
        AntOut ("Ant out"),
        ;
        
        private final String humanFriendlyName;

        private Event(String humanFriendlyName) {
            this.humanFriendlyName = humanFriendlyName;
        }

        public String getHumanFriendlyName() {
            return humanFriendlyName;
        }
    }
}

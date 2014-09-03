package org.samcrow.antrecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jfxtras.labs.dialogs.MonologFX;
import org.samcrow.antrecorder.TimedEvent.Event;

/**
 * 
 *
 */
public class App extends Application {
    
    private SubSecondTimer timer = new SubSecondTimer();
    
    private Stage stage;
    
    private List<TimedEvent> events = new LinkedList<>();
    
    /**
     * A label that provides status information
     */
    private Label statusLabel = new Label("Press start to begin");

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        
        final Insets MARGIN = new Insets(10);
        
        //Layout
        final VBox root = new VBox();
        
        MenuBar bar = createMenuBar();
        bar.setUseSystemMenuBar(true);
        root.getChildren().add(bar);
        
        final HBox topBox = new HBox();
        topBox.setAlignment(Pos.BASELINE_CENTER);
        {
            final Button playButton = new Button("Start");
            playButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    timer.start();
                    statusLabel.setText("Started");
                }
            });
            topBox.getChildren().add(playButton);
            HBox.setMargin(playButton, MARGIN);
            
            final Button pauseButton = new Button("Pause");
            pauseButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    timer.pause();
                    statusLabel.setText("Paused");
                }
            });
            topBox.getChildren().add(pauseButton);
            HBox.setMargin(pauseButton, MARGIN);
            
            final Button resetButton = new Button("Clear");
            resetButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    timer.pause();
                    timer.reset();
                    //Remove the recorded events
                    events.clear();
                    statusLabel.setText("Events cleared");
                }
            });
            topBox.getChildren().add(resetButton);
            HBox.setMargin(resetButton, MARGIN);
            
            final TimecodeLabel timeLabel = new TimecodeLabel();
            timer.setTimeChangeListener(new SubSecondTimer.TimeChangeListener() {
                @Override
                public void onTimeChanged(double newTime) {
                    timeLabel.setTime((int) newTime);
                }
            });
            topBox.getChildren().add(timeLabel);
            HBox.setMargin(timeLabel, MARGIN);
        }
        root.getChildren().add(topBox);
        
        root.getChildren().add(new Separator(Orientation.HORIZONTAL));
        
        final HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.BASELINE_CENTER);
        {
            final Label recordLabel = new Label("Record:");
            bottomBox.getChildren().add(recordLabel);
            HBox.setMargin(recordLabel, MARGIN);
            
            //Create buttons for the event types
            for(final Event type : Event.values()) {
                
                String buttonText = type.getHumanFriendlyName();
                if(type.getKey() != null) {
                    buttonText += " ("+type.getKey().toString()+")";
                }
                
                final Button button = new Button(buttonText);
                
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        recordEvent(type);
                    }
                });
                
                bottomBox.getChildren().add(button);
                HBox.setMargin(button, MARGIN);
            }
        }
        root.getChildren().add(bottomBox);
        
        root.getChildren().add(statusLabel);
        VBox.setMargin(statusLabel, MARGIN);
        
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        
        //Add accelerators for event keyboard shortcuts
        for(final Event type : Event.values()) {
            if(type.getKey() != null) {
                scene.getAccelerators().put(type.getKey(), new Runnable() {

                    @Override
                    public void run() {
                        recordEvent(type);
                    }
                });
            }
        }
        
        stage.setScene(scene);
        stage.setTitle("Ant counter");
        stage.show();
    }
    
    /**
     * Records an event of the given type at the timer's current time
     * @param type 
     */
    private void recordEvent(Event type) {
        TimedEvent event = new TimedEvent(timer.getTime(), type);
        events.add(event);
        
        statusLabel.setText(TimecodeLabel.formatTime((int) timer.getTime()) + ": Recorded " + type.getHumanFriendlyName());
    }
    
    private MenuBar createMenuBar() {
        MenuBar bar = new MenuBar();
        
        final Menu fileMenu = new Menu("File");

        final MenuItem saveItem = new MenuItem("Save as...");
        saveItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        saveItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    saveFile();
                }
                catch (IOException ex) {
                    
                }
            }
        });

        fileMenu.getItems().add(saveItem);
        
        bar.getMenus().add(fileMenu);
        
        return bar;
    }
    
    private void saveFile() throws IOException {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        final File saveFile = chooser.showSaveDialog(stage);
        
        try(PrintStream out = new PrintStream(new FileOutputStream(saveFile))) {
            
            //Headers
            out.println("Time,Event");
            
            for(TimedEvent event : events) {
                out.print(TimecodeLabel.formatTime(event.getTime()));
                out.print(',');
                out.print(event.getEvent().name());
                
                out.println();
            }
            
            statusLabel.setText("Saved file");
        }
        catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            MonologFX errDialog = new MonologFX(MonologFX.Type.ERROR);
            errDialog.setModal(true);
            errDialog.initOwner(stage);
            errDialog.setTitleText("Save failed");
            errDialog.setMessage(ex.getLocalizedMessage());

            errDialog.showDialog();
            
            throw ex;
        }
    }
    
    @Override
    public void stop() {
        System.exit(0);
    }
    
    public static void main( String[] args )
    {
        launch(args);
    }
}

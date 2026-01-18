package com.tainttracker.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tainttracker.config.ConfigManager;

import java.io.IOException;

public class MainApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize ConfigManager
            ConfigManager.getInstance();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1024, 768);
            // scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            primaryStage.setTitle("TaintTracker - Deserialization Audit Tool");
            primaryStage.setScene(scene);
            primaryStage.show();

            logger.info("Application started successfully");
        } catch (IOException e) {
            logger.error("Failed to start application", e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

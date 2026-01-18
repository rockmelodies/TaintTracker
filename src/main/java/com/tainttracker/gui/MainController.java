package com.tainttracker.gui;

import com.tainttracker.analyzer.BytecodeWalker;
import com.tainttracker.scanner.ProjectScanner;
import com.tainttracker.scanner.ScanContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private MenuItem menuOpenProject;
    @FXML
    private TreeView<String> projectTree;
    @FXML
    private TableView<BytecodeWalker.VulnerabilityFitting> vulnTable;
    @FXML
    private TableColumn<BytecodeWalker.VulnerabilityFitting, String> colType;
    @FXML
    private TableColumn<BytecodeWalker.VulnerabilityFitting, String> colRisk;
    @FXML
    private TableColumn<BytecodeWalker.VulnerabilityFitting, String> colLocation;
    @FXML
    private TextArea consoleArea;
    @FXML
    private Label statusLabel;

    private final ProjectScanner scanner = new ProjectScanner();
    private final BytecodeWalker analyzer = new BytecodeWalker();
    private final ObservableList<BytecodeWalker.VulnerabilityFitting> vulnerabilities = FXCollections
            .observableArrayList();

    @FXML
    public void initialize() {
        // Initialize Table Columns
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().sink().description()));

        colRisk.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().sink().riskLevel())));

        colLocation.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().sourceClass() + "." + cellData.getValue().sourceMethod()));

        vulnTable.setItems(vulnerabilities);
    }

    @FXML
    private void handleOpenProject() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Project");
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        if (selectedDirectory != null) {
            log("Selected project: " + selectedDirectory.getAbsolutePath());
            runScan(selectedDirectory);
        }
    }

    private void runScan(File projectRoot) {
        Task<Void> scanTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Scanning project structure...");
                ScanContext context = scanner.scan(projectRoot);

                updateMessage("Found " + context.getClassFiles().size() + " class files. Starting analysis...");

                int totalProxy = context.getClassFiles().size();
                int current = 0;

                for (File classFile : context.getClassFiles()) {
                    List<BytecodeWalker.VulnerabilityFitting> vulns = analyzer.analyzeProperties(classFile);
                    if (!vulns.isEmpty()) {
                        javafx.application.Platform.runLater(() -> vulnerabilities.addAll(vulns));
                    }
                    current++;
                    updateProgress(current, totalProxy);
                    updateMessage("Analyzing: " + classFile.getName());
                }

                return null;
            }
        };

        scanTask.messageProperty().addListener((obs, oldVal, newVal) -> {
            statusLabel.setText(newVal);
            log(newVal);
        });

        scanTask.setOnSucceeded(e -> {
            statusLabel.setText("Scan Complete. Found " + vulnerabilities.size() + " vulnerabilities.");
            log("Scan Complete.");
        });

        scanTask.setOnFailed(e -> {
            log("Scan Failed: " + scanTask.getException().getMessage());
            logger.error("Scan failed", scanTask.getException());
        });

        new Thread(scanTask).start();
    }

    private void log(String message) {
        // Simple append to text area
        if (consoleArea != null) {
            consoleArea.appendText(message + "\n");
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}

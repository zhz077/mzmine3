/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Logger;

import org.controlsfx.control.TaskProgressView;
import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

import io.github.mzmine.datamodel.impl.MZmineObjectBuilder;
import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.main.MZmineCore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.concurrent.Task;

/**
 * MZmine JavaFX Application class
 */
public final class MZmineGUI extends Application {

    private static final File MENU_FILE = new File("conf/MainMenu.fxml");

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static MainWindowController mainWindowController;
    private static MZmineProject currentProject;

    public void start(Stage stage) {

        // Create an empty project
        currentProject = MZmineObjectBuilder.getMZmineProject();

        try {
            // Load the main window
            URL mainFXML = getClass().getResource("MainWindowDockFX.fxml"); //MainWindow.fxml
            FXMLLoader loader = new FXMLLoader(mainFXML);
            BorderPane rootPane = (BorderPane) loader.load();
            mainWindowController = loader.getController();
            Scene scene = new Scene(rootPane, 800, 600, Color.WHITE);
            stage.setScene(scene);

            // Load menu
            MenuBar menu = (MenuBar) FXMLLoader.load(MENU_FILE.toURI().toURL());
            rootPane.setTop(menu);

        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Error loading MZmine GUI from FXML: " + e);
            Platform.exit();
        }

        // Configure DockFX - currently does not support FXML
        DockPane dockPane = mainWindowController.getMainDockPane();
        TreeView<?> rawDataTree = mainWindowController.getRawDataTree();
        TreeView<?> peakListTree = mainWindowController.getPeakListTree();
        TaskProgressView<?> tasksView = mainWindowController.getTaskTable();

        // Add raw data file and peak lists trees to tabs
        TabPane tabs = new TabPane();
        Tab fileTab = new Tab("Raw Data Files", rawDataTree);
        fileTab.setClosable(false);
        Tab peakTab = new Tab("Peak Lists", peakListTree);
        peakTab.setClosable(false);
        tabs.getTabs().addAll(fileTab, peakTab);
        DockNode tabsDock = new DockNode(tabs);
        tabsDock.setDockTitleBar(null); // Disable undocking
        tabsDock.setPrefSize(200, 400);
        tabsDock.setMinHeight(400);
        tabsDock.setVisible(true);
        tabsDock.dock(dockPane, DockPos.LEFT);

        // Add empty dock for visualizers
        DockNode visualizerDock = new DockNode(tasksView);
        visualizerDock.setPrefSize(600, 400);
        visualizerDock.dock(dockPane, DockPos.RIGHT);

        // Add task table
        DockNode taskDock = new DockNode(tasksView);
        taskDock.setPrefHeight(100);
        taskDock.setVisible(true);
        taskDock.setClosable(false);
        taskDock.dock(dockPane, DockPos.BOTTOM);



        stage.setTitle("MZmine " + MZmineCore.getMZmineVersion());

        stage.setMinWidth(300);
        stage.setMinHeight(300);

        // Set application icon
        final Image mzMineIcon = new Image(
                "file:lib" + File.separator + "mzmine-icon.png");
        stage.getIcons().setAll(mzMineIcon);

        stage.setOnCloseRequest(e -> {
            requestQuit();
            e.consume();
        });

        stage.show();
        DockPane.initializeDefaultUserAgentStylesheet();

    }

    public static void requestQuit() {
        Dialog<ButtonType> dialog = new Dialog<>();
        // dialog.initOwner(stage);
        dialog.setTitle("Confirmation");
        dialog.setContentText("Are you sure you want to exit?");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES,
                ButtonType.NO);
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public static void displayMessage(String msg) {
        Dialog<ButtonType> dialog = new Dialog<>();
        // dialog.initOwner(stage);
        dialog.setTitle("Message");
        dialog.setContentText(msg);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    static MZmineProject getCurrentProject() {
        return currentProject;
    }

    static void submitTasks(Collection<Task> tasks) {
        for (Task task : tasks) {
            mainWindowController.addTask(task);
        }

    }

}

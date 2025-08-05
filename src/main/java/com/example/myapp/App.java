package com.example.myapp;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends Application {

    private TabPane fileTabPane;
    private TreeView<String> projectExplorer;
    private TextArea instructionPanel;
    private VBox scriptDataPanel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Slugger's CS2 Editor");

        // Create main layout
        BorderPane root = new BorderPane();

        // Create menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create main content area
        SplitPane mainSplitPane = new SplitPane();
        mainSplitPane.setOrientation(Orientation.HORIZONTAL);

        // Left panel - Project Explorer
        VBox leftPanel = createLeftPanel();

        // Center panel - Code Editor
        VBox centerPanel = createCenterPanel();

        // Right panel - Script Data
        VBox rightPanel = createRightPanel();

        mainSplitPane.getItems().addAll(leftPanel, centerPanel, rightPanel);
        mainSplitPane.setDividerPositions(0.2, 0.8);

        root.setCenter(mainSplitPane);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/editor-styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        Menu viewMenu = new Menu("View");
        Menu extensionMenu = new Menu("Extension");
        Menu helpMenu = new Menu("Help");

        // File menu items
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem saveAsFile = new MenuItem("Save As");

        fileMenu.getItems().addAll(newFile, openFile, new SeparatorMenuItem(),
                saveFile, saveAsFile);

        menuBar.getMenus().addAll(fileMenu, viewMenu, extensionMenu, helpMenu);
        return menuBar;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(5);
        leftPanel.setPadding(new Insets(5));
        leftPanel.setPrefWidth(200);
        leftPanel.setStyle("-fx-background-color: #f0f0f0;");

        // Scripts Explorer
        Label scriptsLabel = new Label("▼ Scripts Explorer");
        scriptsLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        TreeItem<String> rootItem = new TreeItem<>("▼ 718 Stats");
        rootItem.setExpanded(true);

        TreeItem<String> s683Item = new TreeItem<>("S683 - override");
        TreeItem<String> expandingItem = new TreeItem<>("▶ Expanding Notification");
        TreeItem<String> modifyingItem = new TreeItem<>("▶ Modifying Widgets");
        TreeItem<String> randomItem = new TreeItem<>("▼ Random");
        randomItem.setExpanded(true);

        TreeItem<String> testLogicItem = new TreeItem<>("10049 - test logic");
        TreeItem<String> startScrollItem = new TreeItem<>("6251 - start scroll");
        TreeItem<String> loopScrollItem = new TreeItem<>("6252 - loop scroll");
        TreeItem<String> testItem = new TreeItem<>("6253 - test");

        randomItem.getChildren().addAll(testLogicItem, startScrollItem,
                loopScrollItem, testItem);

        rootItem.getChildren().addAll(s683Item, expandingItem, modifyingItem, randomItem);

        TreeItem<String> tooltipItem = new TreeItem<>("▶ Tooltip");

        TreeItem<String> mainRoot = new TreeItem<>("");
        mainRoot.getChildren().addAll(rootItem, tooltipItem);

        projectExplorer = new TreeView<>(mainRoot);
        projectExplorer.setShowRoot(false);
        projectExplorer.setPrefHeight(300);

        leftPanel.getChildren().addAll(scriptsLabel, projectExplorer);
        return leftPanel;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox();

        // File tabs
        fileTabPane = new TabPane();

        // Create sample tabs
        Tab tab1 = createCodeTab("6253 - test.X", getSampleCode());
        Tab tab2 = createCodeTab("S683 - override.X", "// Another file content");

        fileTabPane.getTabs().addAll(tab1, tab2);
        fileTabPane.getSelectionModel().select(tab1);

        centerPanel.getChildren().add(fileTabPane);
        VBox.setVgrow(fileTabPane, Priority.ALWAYS);

        return centerPanel;
    }

    private Tab createCodeTab(String fileName, String content) {
        Tab tab = new Tab(fileName);

        HBox codeContainer = new HBox();

        // Line numbers
        TextArea lineNumbers = new TextArea();
        lineNumbers.setEditable(false);
        lineNumbers.setPrefWidth(50);
        lineNumbers.setStyle("-fx-control-inner-background: #f5f5f5; " +
                "-fx-text-fill: #666666; " +
                "-fx-font-family: 'Courier New'; " +
                "-fx-font-size: 12px;");

        // Code editor
        CodeTextArea codeEditor = new CodeTextArea();
        codeEditor.setText(content);
        codeEditor.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        // Update line numbers when text changes
        updateLineNumbers(lineNumbers, codeEditor.getText());
        codeEditor.textProperty().addListener((obs, oldText, newText) -> {
            updateLineNumbers(lineNumbers, newText);
        });

        // Sync scrolling
        lineNumbers.scrollTopProperty().bind(codeEditor.scrollTopProperty());

        codeContainer.getChildren().addAll(lineNumbers, codeEditor);
        HBox.setHgrow(codeEditor, Priority.ALWAYS);

        tab.setContent(codeContainer);
        return tab;
    }

    private void updateLineNumbers(TextArea lineNumbers, String text) {
        String[] lines = text.split("\n");
        StringBuilder lineNumberText = new StringBuilder();
        for (int i = 1; i <= lines.length; i++) {
            lineNumberText.append(i).append("\n");
        }
        lineNumbers.setText(lineNumberText.toString());
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(5);
        rightPanel.setPadding(new Insets(5));
        rightPanel.setPrefWidth(200);
        rightPanel.setStyle("-fx-background-color: #f0f0f0;");

        Label scriptDataLabel = new Label("Script S683 Data");
        scriptDataLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        // Create data fields
        VBox dataFields = new VBox(2);

        dataFields.getChildren().addAll(
                createDataField("Instructions:", "483"),
                createDataField("Int Params:", "1"),
                createDataField("String Params:", "0"),
                createDataField("Long Params:", "0"),
                createDataField("Int Vars:", "8"),
                createDataField("String Vars:", "0"),
                createDataField("Long Vars:", "0"),
                createDataField("Switch Count:", "0"),
                createDataField("Name:", "null")
        );

        Label instructionLabel = new Label("Instruction Assembly");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        instructionLabel.setPadding(new Insets(10, 0, 5, 0));

        // Instruction assembly text area
        instructionPanel = new TextArea();
        instructionPanel.setEditable(false);
        instructionPanel.setPrefHeight(400);
        instructionPanel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;");
        instructionPanel.setText(getInstructionAssembly());

        rightPanel.getChildren().addAll(scriptDataLabel, dataFields, instructionLabel, instructionPanel);
        VBox.setVgrow(instructionPanel, Priority.ALWAYS);

        return rightPanel;
    }

    private HBox createDataField(String label, String value) {
        HBox field = new HBox(5);
        Label labelNode = new Label(label);
        labelNode.setMinWidth(100);
        labelNode.setStyle("-fx-font-size: 11px;");

        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-font-size: 11px; -fx-text-fill: #0066cc;");

        field.getChildren().addAll(labelNode, valueNode);
        return field;
    }

    private String getSampleCode() {
        return """
            // a modified S683 for 718 to include added skills
            // validated with
            widget(1218, 30).clearChildren();
            widget(1218, 70).clearChildren();
            
            VAR[1753] = arg0;
            VAR[1754] = 1;
            
            DataMap datamap = null;
            int int1 = 0;
            int int2 = 0;
            int int3 = 0;
            int int4 = 30;
            int int5 = 30;
            int int6 = 0;
            
            widget(1218, 172).setText("Milestones");
            widget(1218, 1).setIsHidden(false);
            if (arg0 > 0) {
                widget(1218, 58).setIsHidden(false);
                widget(1218, 7).setIsHidden(false);
                widget(1218, 85).setText(String.valueOf(getLevel(arg0, , , 681, arg0)));
                widget(1218, 161).clearChildren();
                datamap2 = getDragMap(, , , 680, arg0);
                int1 = getDragSize(datamap2);
                widget(1218, 161).resize(int5, int1, 0, 0);
                while (int1 < int5) {
                    createChild(widget(1218, 161), 1, int1, 0);
                    CHILD.setScriptCallOnMouseEntered(5700, int4, "");
                    CHILD.setScriptCallOnMouseExited(5700, int4, 0);
                    CHILD.setScriptCallOnClick(5700, int4, datamap.get(), int1);
                    int4 = int4 + 1;
                    CHILD.resizePosition(0, int5, 0, 0);
                    CHILD.setPosition(4004);
                    CHILD.setTextShadowed(true);
                    }
                    CHILD.setRGB(9169788);
                }
                else {
                    CHILD.setRGB(15122830);
                }
                CHILD.setText(datamap.get(, , datamap2, int3));
                CHILD.setTextAlignment(1, 0, 0);
                int4 = int4 + 15, 1, 0);
                int3 = int3 + 1;
            }
            
            widget(1218, 31).setSprite(7844);
            widget(1218, 83).setSprite(7844);
            widget(1218, 112).setSprite(7844);
            widget(1218, 106).setSprite(7844);
            """;
    }

    private String getInstructionAssembly() {
        return """
            [00100]Opc 558 -> LOAD_INT_VALUE
            [00101]Int: 798222878
            [00103]Opc 784 -> clearChildren()
            [00104]Int: 0
            [00105]Opc 558 -> LOAD_INT_VALUE
            [00106]Int: 798227070
            [00108]Opc 784 -> clearChildren()
            [00109]Int: 0
            [00110]Opc 517 -> LOAD_INT_VAR
            [00111]Int: 0 -> arg0
            [00112]Opc 825 -> ASSIGN_VARC
            [00113]Int: 1753 -> VARC[1753]
            [00114]Opc 558 -> LOAD_INT_VALUE
            [00115]Int: 1
            [00116]Opc 825 -> ASSIGN_VARC
            [00117]Int: 1754 -> VARC[1754]
            [00118]Opc 558 -> LOAD_INT_VALUE
            [00119]Int: 1
            [00120]Opc 605 -> ASSIGN_INT_VAR
            [00121]Int: 1 -> datamap2
            [00122]Opc 558 -> LOAD_INT_VALUE
            [00123]Int: 0
            [00124]Opc 605 -> ASSIGN_INT_VAR
            [00125]Int: 2 -> int1
            [00126]Opc 558 -> LOAD_INT_VALUE
            [00127]Int: 0
            [00128]Opc 605 -> ASSIGN_INT_VAR
            [00129]Int: 3 -> int3
            """;
    }

    // Custom TextArea with basic syntax highlighting
    private static class CodeTextArea extends TextArea {
        public CodeTextArea() {
            super();
            // Basic setup - real syntax highlighting would require more complex implementation
            this.setWrapText(false);

            // Handle tab key for indentation
            this.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.TAB) {
                    this.insertText(this.getCaretPosition(), "    ");
                    event.consume();
                }
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
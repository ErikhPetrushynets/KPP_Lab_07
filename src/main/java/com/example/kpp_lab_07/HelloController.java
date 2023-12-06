package com.example.kpp_lab_07;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class HelloController {
    ThreadManager threadManager;
    @FXML
    private TextArea console;
    @FXML
    private TextField inputNumOfThreads;
    @FXML
    private TextField inputNumOfBooks;
    @FXML
    private TableView<ThreadInfo> threadsTable;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn priorityColumn;
    @FXML
    private TableColumn statusColumn;
    @FXML
    private TableColumn changeTimeColumn;

    @FXML
    protected void StartThreads() {
        Library library = new Library(Integer.valueOf(inputNumOfBooks.getText()));
        threadsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        library.setConsoleTextArea(console);

        if (threadManager != null) {
            for (var thread : threadManager.getThreads()) {
                if (!thread.isInterrupted()) thread.interrupt();
            }
            console.clear();
        }

        threadManager = new ThreadManager(Integer.valueOf(inputNumOfThreads.getText()), library, this.threadsTable);

    }

    @FXML
    protected void KillSelectedThread() throws InterruptedException {
        threadManager.KillSelectedThread();
    }

    @FXML
    public void ResumeSelectedThread() throws InterruptedException {
        threadManager.ResumeSelectedThread();
    }

    @FXML
    public void SuspendSelectedThread() throws InterruptedException {
        threadManager.SuspendSelectedThread();
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<ThreadInfo, String>("name"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<ThreadInfo, String>("priority"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<ThreadInfo, String>("status"));
        changeTimeColumn.setCellValueFactory(new PropertyValueFactory<ThreadInfo, String>("lastStatusChangeTime"));

    }
}
package com.example.kpp_lab_07;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

import java.util.Date;
import java.util.Objects;
import java.util.Random;


import java.util.LinkedList;

public class ThreadManager {
    private final Object lock = new Object();

    LinkedList<Thread> threads;
    LinkedList<Reader> readers;

    @FXML
    TableView<ThreadInfo> threadsTable;
    ObservableList<ThreadInfo> threadInfoList;

    public LinkedList<Reader> getReaders() {
        return readers;
    }

    public void setReaders(LinkedList<Reader> readers) {
        this.readers = readers;
    }

    public LinkedList<Thread> getThreads() {
        return threads;
    }

    public void setThreads(LinkedList<Thread> threads) {
        this.threads = threads;
    }

    ThreadManager(Integer numOfThreads, Library library, TableView<ThreadInfo> threadsTable){
        this.threadsTable = threadsTable;
        this.threads = new LinkedList<>();
        this.readers = new LinkedList<>();

        this.threadInfoList = FXCollections.observableArrayList();
        Random random = new Random();

        for(int i = 0; i < numOfThreads; i++){
            Reader newReader = new Reader(library, i + 1, this, this.threadsTable);
            readers.add(newReader);
            Thread thread = new Thread(newReader) ;
            int randomPriority = random.nextInt(Thread.MAX_PRIORITY - Thread.MIN_PRIORITY + 1) + Thread.MIN_PRIORITY;
            thread.setPriority(randomPriority);
            thread.setName(String.valueOf(i + 1));
            threads.add(thread);
            ThreadInfo threadInfo = new ThreadInfo(
                    thread.getName(),
                    "Running",
                    thread.getPriority(),
                    (new Date()).toString()
            );
            threadInfoList.add(threadInfo);
        }
        threadsTable.setItems(threadInfoList);
        for (var thread: threads) {
            thread.start();
        }
    }
    public void ResumeSelectedThread(){
        ObservableList<ThreadInfo> selectedThreadsInfo = threadsTable.getSelectionModel().getSelectedItems();
        for (ThreadInfo selectedThreadInfo : selectedThreadsInfo) {
            Reader selectedReader = this.getReaders().stream()
                    .filter(reader -> String.valueOf(reader.getReaderId()).equals(selectedThreadInfo.getName()))
                    .findFirst()
                    .orElse(null);
            if (selectedReader != null) {
                synchronized (selectedReader.getLock()) {
                    selectedReader.getLock().notify();
                }
                selectedThreadInfo.setStatus("Running");
                threadsTable.refresh();
            }else{
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ooops!");
                    alert.setHeaderText(null);
                    alert.setContentText("One of selected threads is null(");
                    alert.showAndWait();
                });
            }
        }
    }
    public void SuspendSelectedThread() throws InterruptedException {
        ObservableList<ThreadInfo> selectedThreadsInfo = threadsTable.getSelectionModel().getSelectedItems();
        for (ThreadInfo selectedThreadInfo : selectedThreadsInfo) {
            Reader selectedReader = this.getReaders().stream()
                    .filter(reader -> String.valueOf(reader.getReaderId()).equals(selectedThreadInfo.getName()))
                    .findFirst()
                    .orElse(null);

            if (selectedReader != null) {
                selectedThreadInfo.setStatus("Paused");
                threadsTable.refresh();
            }else{
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ooops!");
                    alert.setHeaderText(null);
                    alert.setContentText("One of selected threads is null(");
                    alert.showAndWait();
                });
            }
        }
    }
    protected void KillSelectedThread() {
        ObservableList<ThreadInfo> selectedThreadsInfo = threadsTable.getSelectionModel().getSelectedItems();

        for (ThreadInfo selectedThreadInfo : selectedThreadsInfo) {
            Thread selectedThread = this.getThreads().stream()
                    .filter(thread -> Objects.equals(thread.getName(), selectedThreadInfo.getName()))
                    .findFirst()
                    .orElse(null);

            if (selectedThread != null) {
                selectedThread.interrupt();
                selectedThreadInfo.setStatus("ENDED");
                threadsTable.refresh();
            }else{
                Platform.runLater(() -> {Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ooops!");
                    alert.setHeaderText(null);
                    alert.setContentText("One of selected threads is null(");
                    alert.showAndWait();
                });
            }
        }

        threadsTable.refresh();
    }

    void RefreshTable(){
        ObservableList<ThreadInfo> selectedThreadsInfo = threadsTable.getItems();
        for (int i = 0; i < getReaders().size(); i++){
            selectedThreadsInfo.get(i).setStatus(threads.get(i).getState().name());
        }
    }

}

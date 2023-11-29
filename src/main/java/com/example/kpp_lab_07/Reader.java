package com.example.kpp_lab_07;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

class Reader implements Runnable {
    private final Object lock = new Object();
    boolean stopMe ;
    private final Library library;
    private final int readerId;
    Object lockForLibrary;
    private int numOfBooks;
    private final Random random = new Random();
    ThreadManager threadManager;
    TableView<ThreadInfo> threadsTable;

    public Reader(Library library, int readerId, ThreadManager threadManager, TableView<ThreadInfo> threadsTable) {
        this.library = library;
        this.readerId = readerId;
        this.numOfBooks = 0;
        this.threadManager = threadManager;
        this.threadsTable = threadsTable;
        this.stopMe = false;
    }

    public boolean isStopMe() {
        return stopMe;
    }

    public void setStopMe(boolean stopMe) {
        this.stopMe = stopMe;
    }

    public Object getLock() {
        return lock;
    }

    public int getReaderId() {
        return readerId;
    }

    public int getNumOfBooks() {
        return numOfBooks;
    }
    public void setNumOfBooks(int numOfBooks) {
        this.numOfBooks = numOfBooks;
    }
    @Override
    public void run() {
        try {
            while (true) {
                this.StopMe();
                library.borrowBook(this);
                this.StopMeIfNoBooks();
                threadManager.RefreshTable();
                Thread.sleep(random.nextInt(1000, 3000));
                library.returnBooks(this);
                Thread.sleep(random.nextInt(1000, 3000));
            }


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        updateLastStatusChangeTime(Thread.currentThread().getName(), new Date());
    }
    private void updateLastStatusChangeTime(String threadName, Date time) {
        for (ThreadInfo threadInfo : threadManager.threadInfoList) {
            if (threadInfo.getName().equals(threadName)) {
                threadInfo.setLastStatusChangeTime(time.toString());
                break;
            }
        }
        Platform.runLater(() -> threadManager.threadsTable.refresh());
    }
    public void StopMe() {
        TableView.TableViewSelectionModel<ThreadInfo> selectionModel = threadsTable.getSelectionModel();
        if (!selectionModel.isEmpty()) {
            synchronized (this) {
                if (Objects.equals(threadsTable.getItems().get(this.getReaderId() - 1).getStatus(), "Paused")) {
                    try {
                        this.library.lockForLibrary.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void StopMeIfNoBooks() {
                if (this.stopMe && this.getNumOfBooks() == 0) {
                    synchronized (this.library.lockForLibrary) {
                        this.stopMe = false;
                        System.out.println(this.getReaderId());
                        try {
                            this.library.lockForLibrary.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
    }



}
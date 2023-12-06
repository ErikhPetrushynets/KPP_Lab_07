package com.example.kpp_lab_07;

import javafx.scene.control.TableView;

import java.util.Objects;
import java.util.Random;

class Reader implements Runnable {
    private final Object lock = new Object();
    private final Library library;
    private final int readerId;
    private final Random random = new Random();
    ThreadManager threadManager;
    TableView<ThreadInfo> threadsTable;
    private int numOfBooks;

    public Reader(Library library, int readerId, ThreadManager threadManager, TableView<ThreadInfo> threadsTable) {
        this.library = library;
        this.readerId = readerId;
        this.numOfBooks = 0;
        this.threadManager = threadManager;
        this.threadsTable = threadsTable;
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
                StopMe();
                threadManager.RefreshTable();
                if (this.getNumOfBooks() == 0 && library.getTotalBooks() != 0 || this.getNumOfBooks() != 0 && library.getTotalBooks() != 0) {
                    library.borrowBook(this);
                } else if (this.getNumOfBooks() == 0 && library.getTotalBooks() == 0) {
                    this.StopBooks();
                } else {
                    int booksToReturn = random.nextInt(1, numOfBooks + 1);
                    this.numOfBooks -= booksToReturn;
                    library.returnBooks(readerId, booksToReturn);
                }
                Thread.sleep(random.nextInt(1000, 5000));
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void StopMe() {
        TableView.TableViewSelectionModel<ThreadInfo> selectionModel = threadsTable.getSelectionModel();
        if (!selectionModel.isEmpty()) {
            synchronized (this) {
                if (Objects.equals(threadsTable.getItems().get(this.getReaderId() - 1).getStatus(), "SUSPENDED")) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void StopBooks() {
        synchronized (library) {
            try {
                System.out.println("Stopping: " + this.readerId);
                library.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
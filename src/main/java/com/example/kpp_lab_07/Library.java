package com.example.kpp_lab_07;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.concurrent.Semaphore;

class Library {
    private final Semaphore semaphore = new Semaphore(1, true);
    @FXML
    TextArea console;
    private volatile int totalBooks;

    public Library(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setConsoleTextArea(TextArea console) {
        this.console = console;
    }

    public synchronized void borrowBook(Reader reader) throws InterruptedException {
        try {
            semaphore.acquire();
            if (totalBooks > 0) {
                totalBooks--;
                reader.setNumOfBooks(reader.getNumOfBooks() + 1);
                appendToConsole("Reader " + reader.getReaderId() + ": Borrowed 1 book. Remaining books: " + totalBooks);
            }
        } finally {
            semaphore.release();
        }
    }

    public synchronized void returnBooks(int readerId, int numOfBooks) throws InterruptedException {
        try {
            semaphore.acquire();
            this.totalBooks += numOfBooks;
            this.notify();
            appendToConsole("Reader " + readerId + ": Returned " + numOfBooks + " books. Remaining books: " + totalBooks);
        } finally {
            semaphore.release();
        }
    }

    private synchronized void appendToConsole(String text) throws InterruptedException {
        if (console.getText().length() > 8000) {
            String newText = console.getText().substring(console.getText().length() - 4000);
            console.setText(newText);
        }
        Platform.runLater(() -> {
            console.appendText(text + "\n");
        });
    }
}

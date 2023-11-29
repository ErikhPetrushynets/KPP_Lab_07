package com.example.kpp_lab_07;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.util.concurrent.Semaphore;

class Library {
    @FXML
    TextArea console;
    private int totalBooks;
    private final Semaphore semaphore = new Semaphore(1, true);


    public void setConsoleTextArea(TextArea console) {
        this.console = console;
    }

    public Library(int totalBooks) {
        this.totalBooks = totalBooks;
    }


    public synchronized void borrowBook(Reader reader) throws InterruptedException {
        semaphore.acquire();
        if (totalBooks <= 0) {
            appendToConsole("Reader " + reader.getReaderId() + ": There are 0 available books to borrow. Will return later");
        } else {
            totalBooks--;
            reader.setNumOfBooks(reader.getNumOfBooks() + 1);
            appendToConsole("Reader " + reader.getReaderId() + ": Borrowed 1 book. Remaining books: " + totalBooks);
        }
        semaphore.release();
    }

    public void returnBooks(int readerId, int numOfBooks) throws InterruptedException {
        semaphore.acquireUninterruptibly();
        this.totalBooks += numOfBooks;
        appendToConsole("Reader " + readerId + ": Returned " + numOfBooks +
                " books. Remaining books: " + totalBooks);

        semaphore.release();
    }
    private void appendToConsole(String text) throws InterruptedException {
        if (console.getText().length() > 8000) {
            String newText = console.getText().substring(console.getText().length() - 4000);
            console.setText(newText);
        }
        Platform.runLater(() -> {
            console.appendText(text + "\n");
        });

    }
}

package com.example.kpp_lab_07;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.util.Objects;
import java.util.concurrent.Semaphore;

class Library{
    @FXML
    TextArea console;
    private volatile int totalBooks;
    private Semaphore semaphore ;

    public Object lockForLibrary;
    public  int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public void setConsoleTextArea(TextArea console) {
        this.console = console;
    }

    public Library(int totalBooks) {
        this.totalBooks = totalBooks;
        this.lockForLibrary = new Object();
        this.semaphore = new Semaphore(totalBooks, true);
    }


    public synchronized void borrowBook(Reader reader) throws InterruptedException {
        if(this.totalBooks == 0){
            reader.setStopMe(true);
        }
        else {
            semaphore.acquire();
            totalBooks--;
            reader.setNumOfBooks(1);
            appendToConsole("Reader " + reader.getReaderId() + ": Borrowed 1 book. Remaining books: " + totalBooks);
        }
    }

    public void returnBooks(Reader reader) throws InterruptedException {
        if(reader.getNumOfBooks() != 0){
            synchronized (this.lockForLibrary) {
                this.totalBooks += 1;
                reader.setNumOfBooks(0);
                this.lockForLibrary.notify();
            }
            appendToConsole("Reader " + reader.getReaderId() + ": Returned 1 books. Remaining books: " + totalBooks);
            semaphore.release();
        }
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

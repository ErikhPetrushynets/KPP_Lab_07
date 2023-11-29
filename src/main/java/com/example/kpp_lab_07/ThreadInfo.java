package com.example.kpp_lab_07;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ThreadInfo {
    private final SimpleStringProperty name;
    private final SimpleStringProperty status;
    private final SimpleIntegerProperty priority;
    private final SimpleStringProperty lastStatusChangeTime;

    public ThreadInfo(String name, String status, int priority, String lastStatusChangeTime) {
        this.name = new SimpleStringProperty(name);
        this.status = new SimpleStringProperty(status);
        this.priority = new SimpleIntegerProperty(priority);
        this.lastStatusChangeTime = new SimpleStringProperty(lastStatusChangeTime);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public int getPriority() {
        return priority.get();
    }

    public SimpleIntegerProperty priorityProperty() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    public String getLastStatusChangeTime() {
        return lastStatusChangeTime.get();
    }

    public SimpleStringProperty lastStatusChangeTimeProperty() {
        return lastStatusChangeTime;
    }

    public void setLastStatusChangeTime(String lastStatusChangeTime) {
        this.lastStatusChangeTime.set(lastStatusChangeTime);
    }
}
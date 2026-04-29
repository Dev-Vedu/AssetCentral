package com.itasset.management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String description;
    private String status;
    private String type;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;





    @ManyToOne
    private User worker;


    private String workerMessage;

    private LocalDateTime createdAt;

    private boolean escalated = false;

    // GETTERS & SETTERS


    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }


    public String getStatus() {
        return status;
    }
    public String getWorkerMessage() {
        return workerMessage;
    }


    public String getType() {
        return type;
    }

    public User getEmployee() {
        return employee;
    }



    public User getWorker() {
        return worker;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setWorkerMessage(String workerMessage) {
        this.workerMessage = workerMessage;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }


    public void setWorker(User worker) {
        this.worker = worker;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public void setEscalated(boolean escalated) {
        this.escalated = escalated;
    }
}
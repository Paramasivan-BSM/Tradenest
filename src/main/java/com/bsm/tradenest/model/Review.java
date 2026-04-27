package com.bsm.tradenest.model;

import java.time.Instant;

public class Review {

    private String id;
    private String workerId;
    private String userEmail;
    private String userName;
    private int rating; // 1-5
    private String comment;
    private String createdAt;
    private String workerReply;
    private String repliedAt;

    public Review() {
        this.createdAt = Instant.now().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getWorkerReply() {
        return workerReply;
    }

    public void setWorkerReply(String workerReply) {
        this.workerReply = workerReply;
    }

    public String getRepliedAt() {
        return repliedAt;
    }

    public void setRepliedAt(String repliedAt) {
        this.repliedAt = repliedAt;
    }
}

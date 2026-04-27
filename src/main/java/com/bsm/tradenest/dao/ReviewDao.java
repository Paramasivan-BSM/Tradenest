package com.bsm.tradenest.dao;

import com.bsm.tradenest.model.Review;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewDao {

    private final MongoCollection<Document> collection;

    public ReviewDao(MongoDatabase db) {
        this.collection = db.getCollection("reviews");
    }

    public void save(Review review) {
        Document doc = new Document()
                .append("workerId", review.getWorkerId())
                .append("userEmail", review.getUserEmail())
                .append("userName", review.getUserName())
                .append("rating", review.getRating())
                .append("comment", review.getComment())
                .append("createdAt", review.getCreatedAt())
                .append("workerReply", review.getWorkerReply())
                .append("repliedAt", review.getRepliedAt());
        collection.insertOne(doc);
    }

    public List<Review> findByWorkerId(String workerId) {
        List<Review> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("workerId", workerId))) {
            list.add(docToReview(doc));
        }
        return list;
    }

    /** Returns true if this user has already reviewed this worker */
    public boolean hasReviewed(String workerId, String userEmail) {
        return collection.find(Filters.and(
                Filters.eq("workerId", workerId),
                Filters.eq("userEmail", userEmail))).first() != null;
    }

    /**
     * Persist a worker's reply to a specific review.
     * Returns false if the reviewId is invalid.
     */
    public boolean addReply(String reviewId, String reply) {
        try {
            ObjectId oid = new ObjectId(reviewId);
            var result = collection.updateOne(
                    Filters.eq("_id", oid),
                    Updates.combine(
                            Updates.set("workerReply", reply),
                            Updates.set("repliedAt", Instant.now().toString())));
            return result.getMatchedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Review docToReview(Document doc) {
        Review r = new Review();
        r.setId(doc.getObjectId("_id").toHexString());
        r.setWorkerId(doc.getString("workerId"));
        r.setUserEmail(doc.getString("userEmail"));
        r.setUserName(doc.getString("userName"));
        r.setRating(doc.getInteger("rating", 0));
        r.setComment(doc.getString("comment"));
        r.setCreatedAt(doc.getString("createdAt"));
        r.setWorkerReply(doc.getString("workerReply"));
        r.setRepliedAt(doc.getString("repliedAt"));
        return r;
    }
}

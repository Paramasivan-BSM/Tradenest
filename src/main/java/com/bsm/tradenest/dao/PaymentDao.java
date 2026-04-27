package com.bsm.tradenest.dao;

import com.bsm.tradenest.model.Payment;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao {

    private final MongoCollection<Document> collection;

    public PaymentDao(MongoDatabase db) {
        this.collection = db.getCollection("payments");
    }

    public String save(Payment payment) {
        Document doc = new Document()
                .append("bookingId", payment.getBookingId())
                .append("orderId", payment.getOrderId())
                .append("paymentId", payment.getPaymentId())
                .append("amount", payment.getAmount())
                .append("workerAmount", payment.getWorkerAmount())
                .append("adminAmount", payment.getAdminAmount())
                .append("status", payment.getStatus())
                .append("createdAt", payment.getCreatedAt());
        collection.insertOne(doc);
        return doc.getObjectId("_id").toHexString();
    }

    public Payment findByOrderId(String orderId) {
        Document doc = collection.find(Filters.eq("orderId", orderId)).first();
        if (doc == null)
            return null;
        return docToPayment(doc);
    }

    public Payment findByBookingId(String bookingId) {
        Document doc = collection.find(Filters.eq("bookingId", bookingId)).first();
        if (doc == null)
            return null;
        return docToPayment(doc);
    }

    public void updateStatusAndPaymentId(String orderId, String paymentId) {
        collection.updateOne(
                Filters.eq("orderId", orderId),
                new Document("$set", new Document("status", "SUCCESS")
                        .append("paymentId", paymentId)));
    }

    private Payment docToPayment(Document doc) {
        Payment p = new Payment();
        p.setId(doc.getObjectId("_id").toHexString());
        p.setBookingId(doc.getString("bookingId"));
        p.setOrderId(doc.getString("orderId"));
        p.setPaymentId(doc.getString("paymentId"));
        p.setAmount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0);
        p.setWorkerAmount(doc.getDouble("workerAmount") != null ? doc.getDouble("workerAmount") : 0.0);
        p.setAdminAmount(doc.getDouble("adminAmount") != null ? doc.getDouble("adminAmount") : 0.0);
        p.setStatus(doc.getString("status"));
        p.setCreatedAt(doc.getString("createdAt"));
        return p;
    }
}

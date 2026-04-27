package com.bsm.tradenest.dao;

import com.bsm.tradenest.enums.BookingStatus;
import com.bsm.tradenest.model.Booking;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookingDao {

    private final MongoCollection<Document> collection;

    public BookingDao(MongoDatabase db) {
        this.collection = db.getCollection("bookings");
    }

    public String save(Booking booking) {
        Document doc = new Document()
                .append("userEmail", booking.getUserEmail())
                .append("workerEmail", booking.getWorkerEmail())
                .append("workerName", booking.getWorkerName())
                .append("workerId", booking.getWorkerId())
                .append("description", booking.getDescription())
                .append("amount", booking.getAmount())
                .append("status", booking.getStatus().name())
                .append("scheduledDate", booking.getScheduledDate())
                .append("createdAt", booking.getCreatedAt());
        collection.insertOne(doc);
        return doc.getObjectId("_id").toHexString();
    }

    public Booking findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        if (doc == null)
            return null;
        return docToBooking(doc);
    }

    public List<Booking> findByUserEmail(String email) {
        List<Booking> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("userEmail", email))) {
            list.add(docToBooking(doc));
        }
        return list;
    }

    public List<Booking> findByWorkerEmail(String email) {
        List<Booking> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("workerEmail", email))) {
            list.add(docToBooking(doc));
        }
        return list;
    }

    public List<Booking> findByWorkerId(String workerId) {
        List<Booking> list = new ArrayList<>();
        if (workerId == null || workerId.isBlank())
            return list;
        for (Document doc : collection.find(Filters.eq("workerId", workerId))) {
            list.add(docToBooking(doc));
        }
        return list;
    }

    public List<Booking> findByWorkerName(String workerName) {
        List<Booking> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("workerName", workerName))) {
            list.add(docToBooking(doc));
        }
        return list;
    }

    public void updateStatus(String id, BookingStatus status) {
        collection.updateOne(
                Filters.eq("_id", new ObjectId(id)),
                Updates.set("status", status.name()));
    }

    private Booking docToBooking(Document doc) {
        Booking b = new Booking();
        b.setId(doc.getObjectId("_id").toHexString());
        b.setUserEmail(doc.getString("userEmail"));
        b.setWorkerEmail(doc.getString("workerEmail"));
        b.setWorkerName(doc.getString("workerName"));
        b.setWorkerId(doc.getString("workerId"));
        b.setDescription(doc.getString("description"));
        b.setAmount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0);
        b.setStatus(BookingStatus.valueOf(doc.getString("status")));
        b.setScheduledDate(doc.getString("scheduledDate"));
        b.setCreatedAt(doc.getString("createdAt"));
        return b;
    }
}

package com.bsm.tradenest.dao;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class WorkerDao {

    private final MongoCollection<Document> collection;

    public WorkerDao(MongoDatabase db) {
        this.collection = db.getCollection("workersTree");
    }

    public Document findByEmail(String email) {
        return collection.find(Filters.eq("email", email)).first();
    }

    public Document findByName(String name) {
        return collection.find(Filters.eq("name", name)).first();
    }

    public void save(Document doc) {
        collection.insertOne(doc);
    }

    /**
     * Update an existing worker profile — supports all editable fields.
     */
    public void update(String id, String name, String skill, double lat, double lng,
            double radiusKm, String email, double ratePerHour, int experienceYears,
            String bio, boolean available) {
        collection.updateOne(
                Filters.eq("_id", new ObjectId(id)),
                Updates.combine(
                        Updates.set("name", name),
                        Updates.set("skill", skill.trim()),
                        Updates.set("serviceRadiusKm", radiusKm),
                        Updates.set("email", email),
                        Updates.set("ratePerHour", ratePerHour),
                        Updates.set("experienceYears", experienceYears),
                        Updates.set("bio", bio != null ? bio.trim() : ""),
                        Updates.set("available", available),
                        Updates.set("location", new Document()
                                .append("type", "Point")
                                .append("coordinates", List.of(lng, lat)))));
    }

    /** Convenience overload for legacy callers that don't have extra fields */
    public void update(String id, String name, String skill, double lat, double lng,
            double radiusKm, String email) {
        update(id, name, skill, lat, lng, radiusKm, email, 0, 0, "", true);
    }

    /** Toggle availability only */
    public void setAvailability(String email, boolean available) {
        collection.updateOne(
                Filters.eq("email", email),
                Updates.set("available", available));
    }

    public List<Document> findNearbyWorkers(double lat, double lng, String skill) {
        skill = skill.trim();

        Bson geoFilter = Filters.nearSphere(
                "location",
                new Point(new Position(lng, lat)),
                10000.0,
                null);

        Bson filter = Filters.and(
                geoFilter,
                Filters.eq("available", true),
                Filters.eq("skill", skill));

        List<Document> results = collection.find(filter).into(new ArrayList<>());
        for (Document doc : results) {
            if (doc.getObjectId("_id") != null) {
                doc.append("id", doc.getObjectId("_id").toHexString());
            }
        }
        return results;
    }

    public List<String> fetchAllSkills() {
        List<String> skills = new ArrayList<>();
        collection.distinct("skill", String.class).into(skills);
        return skills;
    }

    public void addPortfolioImage(String email, String imageUrl) {
        collection.updateOne(
                Filters.eq("email", email),
                Updates.push("portfolioImages", imageUrl));
    }

    public List<String> getPortfolioImages(String workerId) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(workerId))).first();
        if (doc == null)
            return List.of();
        @SuppressWarnings("unchecked")
        List<String> images = (List<String>) doc.get("portfolioImages");
        return images != null ? images : List.of();
    }
}

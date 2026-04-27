package com.bsm.tradenest.services;

import com.bsm.tradenest.dao.WorkerDao;
import com.bsm.tradenest.exception.BadRequestException;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {

    private final WorkerDao dao;

    public WorkerService(WorkerDao dao) {
        this.dao = dao;
    }

    public void registerWorker(
            String name, String skill, double lat, double lng, double radiusKm,
            String email, double ratePerHour, int experienceYears, String bio,
            boolean available) {

        if (radiusKm <= 0)
            throw new BadRequestException("Radius must be greater than zero");

        // Block re-registration for same email
        if (email != null) {
            Document existing = dao.findByEmail(email);
            if (existing != null) {
                throw new BadRequestException(
                        "A worker profile already exists for this account. You cannot register twice.");
            }
        }

        // Legacy upsert: link email to nameless profile
        Document legacyProfile = dao.findByName(name);
        if (legacyProfile != null && legacyProfile.getString("email") == null) {
            dao.update(legacyProfile.getObjectId("_id").toHexString(), name, skill,
                    lat, lng, radiusKm, email, ratePerHour, experienceYears, bio, available);
            return;
        }

        // Brand-new profile
        Document doc = new Document()
                .append("name", name)
                .append("skill", skill.trim())
                .append("available", available)
                .append("serviceRadiusKm", radiusKm)
                .append("ratePerHour", ratePerHour)
                .append("experienceYears", experienceYears)
                .append("bio", bio != null ? bio.trim() : "")
                .append("email", email)
                .append("location", new Document()
                        .append("type", "Point")
                        .append("coordinates", List.of(lng, lat)));
        dao.save(doc);
    }

    public void updateWorker(
            String email, String name, String skill, double lat, double lng,
            double radiusKm, double ratePerHour, int experienceYears, String bio,
            boolean available) {

        if (radiusKm <= 0)
            throw new BadRequestException("Radius must be greater than zero");

        Document existing = dao.findByEmail(email);
        if (existing == null)
            throw new BadRequestException("No worker profile found for this account");

        dao.update(existing.getObjectId("_id").toHexString(), name, skill, lat, lng,
                radiusKm, email, ratePerHour, experienceYears, bio, available);
    }
}

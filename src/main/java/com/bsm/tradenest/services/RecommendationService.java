package com.bsm.tradenest.services;

import com.bsm.tradenest.dao.WorkerDao;
import com.mongodb.client.DistinctIterable;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private final WorkerDao dao;

    public RecommendationService(WorkerDao dao) {
        this.dao = dao;
    }

    public List<Document> recommendWorkers(
            double lat,
            double lng,
            String skill
    ) {
        List<Document> workers = dao.findNearbyWorkers(lat, lng, skill);
        // Return empty list — frontend handles the empty state gracefully
        return workers;
    }


    public List<String> getAllSkills() {
        return dao.fetchAllSkills();
    }


}

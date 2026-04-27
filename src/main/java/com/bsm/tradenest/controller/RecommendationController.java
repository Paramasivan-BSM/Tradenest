package com.bsm.tradenest.controller;

import com.bsm.tradenest.dao.WorkerDao;
import com.bsm.tradenest.services.RecommendationService;
import com.mongodb.client.DistinctIterable;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Document> recommend(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam String skill
    ) {
        return service.recommendWorkers(lat, lng, skill);
    }






        @GetMapping("/skills")
        public List<String> getWorkerSkills() {
            return service.getAllSkills();
        }
}

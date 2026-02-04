package com.bsm.tradenest.dao;

import com.bsm.tradenest.enums.Role;
import com.bsm.tradenest.model.Usermodel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class Userdao {

   private  MongoCollection<Document> collection;

   Userdao(MongoDatabase db){
       this.collection = db.getCollection("Users");
   }


   public void save(Usermodel model){

       Document doc = new Document("username",model.getUsername())
               .append("email",model.getEmail())
               .append("password",model.getPassword())
               .append("role",model.getRole());

       collection.insertOne(doc);






   }

    public boolean adminExists() {
        return collection.countDocuments(
                Filters.eq("role", "ROLE_ADMIN")
        ) > 0;
    }

    public Usermodel findByEmail(String email) {

        Document doc = collection.find(
                Filters.eq("email", email)
        ).first();

        if (doc == null) return null;

        Usermodel user = new Usermodel();
        user.setEmail(doc.getString("email"));
        user.setPassword(doc.getString("password"));
        user.setRole(Role.valueOf(doc.getString("role")));
        user.setEnabled(doc.getBoolean("enabled", true));

        return user;
    }
















}

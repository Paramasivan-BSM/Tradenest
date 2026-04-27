package com.bsm.tradenest.dao;

import com.bsm.tradenest.model.Message;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class MessageDao {

    private final MongoCollection<Document> collection;

    public MessageDao(MongoDatabase db) {
        this.collection = db.getCollection("messages");
    }

    public void save(Message message) {
        Document doc = new Document()
                .append("senderId", message.getSenderId())
                .append("receiverId", message.getReceiverId())
                .append("content", message.getContent())
                .append("conversationId", message.getConversationId())
                .append("createdAt", message.getCreatedAt());
        collection.insertOne(doc);
    }

    /**
     * Returns all messages for the conversation between two users, ordered
     * oldest-first
     */
    public List<Message> findConversation(String userA, String userB) {
        String convId = buildConversationId(userA, userB);
        List<Message> list = new ArrayList<>();
        for (Document doc : collection
                .find(Filters.eq("conversationId", convId))
                .sort(Sorts.ascending("createdAt"))) {
            list.add(docToMessage(doc));
        }
        return list;
    }

    /** Stable conversation ID regardless of who initiated */
    public static String buildConversationId(String a, String b) {
        String[] parts = { a, b };
        Arrays.sort(parts);
        return parts[0] + "||" + parts[1];
    }

    /**
     * Returns the latest message from each conversation that involves myEmail.
     * Used to render the inbox conversation list.
     */
    public List<Message> findInbox(String myEmail) {
        // Find all conversations involving this user (as sender OR receiver)
        List<Message> latest = new ArrayList<>();
        java.util.Set<String> seen = new java.util.LinkedHashSet<>();

        for (Document doc : collection
                .find(Filters.or(
                        Filters.eq("senderId", myEmail),
                        Filters.eq("receiverId", myEmail)))
                .sort(Sorts.descending("createdAt"))) {
            String convId = doc.getString("conversationId");
            if (seen.add(convId)) { // first occurrence = latest message for that conv
                latest.add(docToMessage(doc));
            }
        }
        return latest;
    }

    private Message docToMessage(Document doc) {
        Message m = new Message();
        m.setId(doc.getObjectId("_id").toHexString());
        m.setSenderId(doc.getString("senderId"));
        m.setReceiverId(doc.getString("receiverId"));
        m.setContent(doc.getString("content"));
        m.setConversationId(doc.getString("conversationId"));
        m.setCreatedAt(doc.getString("createdAt"));
        return m;
    }
}

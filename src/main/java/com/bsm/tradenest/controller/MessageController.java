package com.bsm.tradenest.controller;

import com.bsm.tradenest.dao.MessageDao;
import com.bsm.tradenest.model.Message;
import com.bsm.tradenest.services.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageDao messageDao;
    private final BookingService bookingService;

    public MessageController(MessageDao messageDao, BookingService bookingService) {
        this.messageDao = messageDao;
        this.bookingService = bookingService;
    }

    /**
     * POST /api/messages/send
     * Body: { "receiverId": "worker@email.com", "content": "Hello!" }
     */
    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody Map<String, String> req,
            HttpServletRequest request) {
        String senderEmail = bookingService.extractEmailFromRequest(request);
        if (senderEmail == null)
            return ResponseEntity.status(401).body("Unauthorized");

        String receiverId = req.get("receiverId");
        String content = req.get("content");

        if (receiverId == null || receiverId.isBlank())
            return ResponseEntity.badRequest().body("receiverId is required");
        if (content == null || content.isBlank())
            return ResponseEntity.badRequest().body("content is required");

        Message msg = new Message();
        msg.setSenderId(senderEmail);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setConversationId(MessageDao.buildConversationId(senderEmail, receiverId));

        messageDao.save(msg);
        return ResponseEntity.ok(Map.of("message", "Sent"));
    }

    /**
     * GET /api/messages/conversation/{otherEmail}
     * Returns the full conversation thread between the caller and otherEmail
     */
    @GetMapping("/conversation/{otherEmail}")
    public ResponseEntity<?> getConversation(@PathVariable String otherEmail,
            HttpServletRequest request) {
        String myEmail = bookingService.extractEmailFromRequest(request);
        if (myEmail == null)
            return ResponseEntity.status(401).body("Unauthorized");

        List<Message> messages = messageDao.findConversation(myEmail, otherEmail);
        return ResponseEntity.ok(messages);
    }

    /**
     * GET /api/messages/inbox
     * Returns the latest message per unique conversation involving the caller.
     */
    @GetMapping("/inbox")
    public ResponseEntity<?> getInbox(HttpServletRequest request) {
        String myEmail = bookingService.extractEmailFromRequest(request);
        if (myEmail == null)
            return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(messageDao.findInbox(myEmail));
    }
}

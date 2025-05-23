package com.app.web.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
	private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;

    // Constructor, getters y setters
    public ChatMessage(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    @Override
    public String toString() {
        return timestamp.toString() + " " + senderId + "->" + receiverId + " " + content;
    }
}

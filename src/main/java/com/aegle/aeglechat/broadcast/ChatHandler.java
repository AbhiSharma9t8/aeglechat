package com.aegle.aeglechat.broadcast;

import com.aegle.aeglechat.user.UserRepository;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatHandler extends TextWebSocketHandler {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        userRepository.addUser(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        System.out.printf("\n[%s] %s%n",session.getId(), message.getPayload());
        for (WebSocketSession s: userRepository.activeUsers()){
            if (s.isOpen() && !s.getId().equals(session.getId())){
                s.sendMessage(new TextMessage(userRepository.user(session.getId())+" : "+message.getPayload()));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        userRepository.removeUser(session.getId());
        System.out.println("Connection closed: "+ session.getId());
    }
}


package com.aegle.aeglechat.user;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    static class User{
        private final WebSocketSession session;
        private final String name;
        User(WebSocketSession session, String name) {
            this.session = session;
            this.name = name;
        }
        public WebSocketSession getSession() {
            return session;
        }
        public String getName() {
            return name;
        }
    }

    private final ConcurrentHashMap<String,User> activeUserMap = new ConcurrentHashMap<>();

    public void addUser(WebSocketSession session) throws IOException {
        String name = session.getUri().getQuery().split("=")[1];
        if(activeUserName().contains(name)){
            System.out.println("existing user found:"+name);
            session.sendMessage(new TextMessage("existing user found:"+name));
            session.close();
            return;
        }
        activeUserMap.put(session.getId(),new User(session, name));
        System.out.printf("\nNew Connection established from %s ",name);
        session.sendMessage(new TextMessage("Welcome to aeglechat "+name+" active user: "+ activeUserName()));
    }

    public void removeUser(String UUID){
       activeUserMap.remove(UUID);
    }

    public Collection<WebSocketSession> activeUsers(){
        return activeUserMap.values().stream().map(User::getSession).toList();
    }

    public List<String> activeUserName(){
        return activeUserMap.values().stream().map(User::getName).toList();
    }

    public String user(String uuid){
        return activeUserMap.get(uuid).getName();
    }

}


package com.ido.qna.websocket;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.ido.qna.entity.UserMessage;
import com.ido.qna.service.UserMessageService;
import com.rainful.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.HashMap;


/**
 * 微信小程序WebSocket
 *
 * @author Shanqinag Ke
 * @since 2016-10-15
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final static List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<WebSocketSession>());
    @Autowired
    UserMessageService userMessageService;

    /**
     * 处理接收文本
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //todo test code
//        session.sendMessage(new TextMessage(new Gson().toJson(Arrays.asList(UserMessage.builder()
//                .title("new message")
//                .content("message content")
//        .build()))));
//
//        session.close();

//        //TODO find user message
        List<UserMessage> userMsg = userMessageService.findAll();
        if (userMsg == null || userMsg.size() == 0) {
            session.close();
            logger.info("no message to user , closing session");
            super.handleTextMessage(session, message);
            return;
        }
        String outMsg = new Gson().toJson(userMsg);

        session.sendMessage(new TextMessage(outMsg));
        super.handleTextMessage(session, message);

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("connect to the websocket chat success......");
        sessions.add(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        logger.info("websocket chat connection closed......");
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.info("websocket chat connection closed......");
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 判断用户是否在线
     *
     * @param userName 登录用户名
     * @return
     */
    public boolean isUserConnected(String userName) {
        if (org.springframework.util.StringUtils.isEmpty(userName)) {
            return false;
        }
        for (WebSocketSession user : sessions) {
            if (user.getAttributes().get("user").equals(userName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 发送消息
     *
     * @param userName 昵称
     * @param content  发送内容
     */
    public static void sendChatMessage(String userName, String content) {
        if (StringUtils.isEmpty(userName)) {
            return;
        }
        if (content == null) {
            return;
        }
        for (WebSocketSession session : sessions) {
            if (!session.getAttributes().get("user").equals(userName)) {
                if (session.isOpen()) {
                    try {
                        Map<String, Object> retMap = new HashMap<String, Object>();
                        retMap.put("user", userName);
                        retMap.put("content", content);
                        session.sendMessage(new TextMessage(JSON.toJSONString(retMap)));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}
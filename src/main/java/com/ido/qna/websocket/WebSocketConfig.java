package com.ido.qna.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer{
    @Autowired
    ChatWebSocketHandler handler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //注册通道
        registry.addHandler(handler,"/websocket").setAllowedOrigins("*").addInterceptors(myInterceptor());
        registry.addHandler(handler, "/sockjs/websocket").setAllowedOrigins("*").addInterceptors(myInterceptor()).withSockJS();
    }
    //消息处理Handler
//    @Bean
//    public ChatWebSocketHandler chatWebSocketHandler() {
//        return new ChatWebSocketHandler();
//    }

    //websocket拦截器
    @Bean
    public WebSocketHandshakeInterceptor myInterceptor(){
        return new WebSocketHandshakeInterceptor();
    }
}
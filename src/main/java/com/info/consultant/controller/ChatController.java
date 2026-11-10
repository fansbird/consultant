package com.info.consultant.controller;

import com.info.consultant.aiservice.ConsultantService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ConsultantService consultantService;

    //用于阻塞聊天的方法
//    @GetMapping
//    public String chat(String message) {//浏览器用户问题
//        return consultantService.chat(message);
//    }

    //用于流式聊天的方法
    @GetMapping(produces = "text/html;charset=utf-8")
    public Flux<String> chatStream(String memoryId, String message) {//浏览器用户问题
        return consultantService.chatStream(memoryId, message);
    }

}

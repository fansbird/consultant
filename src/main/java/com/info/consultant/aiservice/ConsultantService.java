package com.info.consultant.aiservice;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,//手动注入
        chatModel = "openAiChatModel",//注入的bean名称，用于阻塞聊天
        streamingChatModel = "openAiStreamingChatModel",//注入的bean名称，用于流式聊天
        chatMemoryProvider = "chatMemoryProvider",//注入的bean名称，记忆工厂
        contentRetriever = "contentRetriever",//注入的bean名称，RAG检索器
        tools="reservationTool"//注入的bean名称，ai识别参数并传入数据库
)
public interface ConsultantService {
    //用于聊天的方法
    //public String chat(String message);
    //用于流式聊天的方法
    @SystemMessage(fromResource = "system.txt")
    Flux<String> chatStream(@MemoryId String id, @UserMessage String message);
}

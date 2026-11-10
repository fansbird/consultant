package com.info.consultant.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {
    //注入redisTemplate
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        //从redis中获取会话记忆json字符串
        String json = redisTemplate.opsForValue().get(memoryId.toString());
        // 判空处理
        if (json == null || json.isEmpty()) {
            return new java.util.ArrayList<>(); // 第一次来，返回空列表
        }
        // 将json字符串转换为会话记忆列表
        return ChatMessageDeserializer.messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        //更新redis中的会话记忆
        //将会话记忆转换为json字符串
        String json = ChatMessageSerializer.messagesToJson(messages);
        //将json字符串存储到redis中
        redisTemplate.opsForValue().set(memoryId.toString(), json, Duration.ofDays(1));

    }

    @Override
    public void deleteMessages(Object memoryId) {
        //从redis中删除会话记忆
        redisTemplate.delete(memoryId.toString());

    }
}

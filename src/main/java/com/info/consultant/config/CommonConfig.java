package com.info.consultant.config;

import com.info.consultant.aiservice.ConsultantService;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.JedisClientConfig;

import java.util.List;

@Configuration
public class CommonConfig {
    @Autowired
    private ChatMemoryStore redisChatMemoryStore;
    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private RedisEmbeddingStore redisEmbeddingStore;



    //构建会话记忆对象
//    @Bean
//    public ChatMemory chatMemory(){
//        return MessageWindowChatMemory.builder()
//                .maxMessages(20)
//                .build();
//    }

    //创建chatMemoryProvider对象
    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        return new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(redisChatMemoryStore)
                        .build();
            }
        };

    }


    //构建向量数据库操作对象
    //@Bean//加载一次放入到redis向量数据库后，就不需要重复加载了，可注释掉
    public EmbeddingStore store() {
        // 1. 加载文档进内存（去找 resources/content 目录下的文件）
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content");

        // 2. 构建向量数据库操作对象（这里是内存版，重启就清空,通常用redis版）
       // InMemoryEmbeddingStore store = new InMemoryEmbeddingStore();

        // 3. 构建一个 EmbeddingStoreIngestor 对象，完成文本数据切割、向量化、存储
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(redisEmbeddingStore)
                .embeddingModel(embeddingModel)
                .build();

        // 4. 执行！把刚才加载的文档，切碎、变成向量，存进数据库里
        ingestor.ingest(documents);

        // 5. 把这个装满资料的数据库交出去
        return redisEmbeddingStore;
    }

    // ==========================================
    // 第二部分：检索（给 AI 配一个聪明的“图书管理员”）
    // ==========================================
    @Bean
    public ContentRetriever contentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(redisEmbeddingStore)  // 告诉管理员去哪个数据库找资料
                .minScore(0.5)          // 防火墙：相似度必须大于 0.5（50%）才能拿给 AI，防止误导
                .maxResults(3)          // 限制：每次最多只拿 3 段资料，防止把 AI 的脑容量（Token）撑爆
                .embeddingModel(embeddingModel) // 告诉管理员用哪个模型做向量化
                .build();
    }
}

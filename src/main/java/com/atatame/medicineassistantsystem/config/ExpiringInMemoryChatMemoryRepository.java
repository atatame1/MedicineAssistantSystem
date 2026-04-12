package com.atatame.medicineassistantsystem.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 支持自动清理过期会话的内存存储库
 * 通过 TTL（Time-To-Live）机制限制内存增长
 */
@Slf4j
public class ExpiringInMemoryChatMemoryRepository implements ChatMemoryRepository {

    private final ConcurrentHashMap<String, List<Message>> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastAccessTime = new ConcurrentHashMap<>();
    
    private final long ttlMillis; // 会话存活时间（毫秒）
    private final long cleanupIntervalMillis; // 清理间隔
    private volatile long lastCleanupTime;

    /**
     * 构造函数
     * @param ttlMinutes 会话存活时间（分钟），超过此时间未访问的会话将被清理
     */
    public ExpiringInMemoryChatMemoryRepository(long ttlMinutes) {
        this.ttlMillis = ttlMinutes * 60 * 1000L;
        this.cleanupIntervalMillis = 5 * 60 * 1000L; // 每5分钟清理一次
        this.lastCleanupTime = System.currentTimeMillis();
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        cleanupIfNeeded();
        store.put(conversationId, new ArrayList<>(messages));
        lastAccessTime.put(conversationId, System.currentTimeMillis());
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        cleanupIfNeeded();
        List<Message> messages = store.get(conversationId);
        if (messages != null) {
            lastAccessTime.put(conversationId, System.currentTimeMillis());
            return new ArrayList<>(messages);
        }
        return List.of();
    }

    @Override
    public List<String> findConversationIds() {
        cleanupIfNeeded();
        return new ArrayList<>(lastAccessTime.keySet());
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        store.remove(conversationId);
        lastAccessTime.remove(conversationId);
    }

    /**
     * 检查是否需要执行清理操作
     */
    private void cleanupIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastCleanupTime > cleanupIntervalMillis) {
            synchronized (this) {
                if (now - lastCleanupTime > cleanupIntervalMillis) {
                    performCleanup(now);
                    lastCleanupTime = now;
                }
            }
        }
    }

    /**
     * 执行清理操作，删除过期的会话
     */
    private void performCleanup(long now) {
        Set<String> expiredKeys = lastAccessTime.entrySet().stream()
                .filter(entry -> now - entry.getValue() > ttlMillis)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        for (String key : expiredKeys) {
            store.remove(key);
            lastAccessTime.remove(key);
        }

        if (!expiredKeys.isEmpty()) {
            log.info("[ExpiringInMemoryChatMemoryRepository] 清理了 " + expiredKeys.size()
                    + " 个过期会话，当前会话数: " + store.size());
        }
    }

    /**
     * 获取当前存储的会话数量
     */
    public int size() {
        return store.size();
    }
}

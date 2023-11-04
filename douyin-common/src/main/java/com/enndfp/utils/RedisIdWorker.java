package com.enndfp.utils;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 基于 Redis 实现全局唯一 id 的工具类
 *
 * @author Enndfp
 */
@Component
public class RedisIdWorker {

    @Resource
    private RedisUtils redisUtils;

    // 开始时间戳 2008.08.08 20:00:00
    private static final long BEGIN_TIMESTAMP = 1218225600000L;
    // 序列号的位数
    private static final int SEQUENCE_NUMBER = 16;
    // 同一毫秒内能生成的最大序列号
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_NUMBER) - 1;

    // 上一次获取的时间戳
    private long lastTimestamp = -1L;

    // 缓存的序列号范围
    private long sequenceCache = 0L;
    private long sequenceCacheEnd = 0L;
    // 从Redis一次获取的序列号数量
    private static final int CACHE_SIZE = 1000;


    public synchronized long nextId(String keyPrefix) {
        // 1. 生成毫秒级时间戳
        long nowMillis = System.currentTimeMillis();
        long timestamp = nowMillis - BEGIN_TIMESTAMP;

        // 处理时间回拨问题
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset < 100) {
                // 如果时间回拨小于100ms，暂停当前线程等待时间回拨结束
                try {
                    Thread.sleep(offset + 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                nowMillis = System.currentTimeMillis();
                timestamp = nowMillis - BEGIN_TIMESTAMP;
            } else if (offset < 1000) {
                // 如果时间回拨在100ms~1s之间，使用上一个毫秒的最大序列号并递增
                if (sequenceCache >= sequenceCacheEnd) {
                    // 如果缓存的序列号已用完，从Redis获取新的序列号
                    refreshSequenceCache(keyPrefix);
                }
                // 使用上一个毫秒的最大序列号并递增
                sequenceCache++;
            } else if (offset < 5000) {
                // 如果时间回拨在1s~5s之间，建议使用轮询机制
                throw new RuntimeException("时钟回拨了 " + offset + "ms。建议使用轮询机制处理");
            } else {
                // 如果时间回拨超过5s，建议人工介入
                throw new RuntimeException("时钟回拨超过5秒。建议人工介入处理");
            }
        }

        // 2. 如果缓存的序列号用完，重新从Redis获取
        if (sequenceCache >= sequenceCacheEnd) {
            refreshSequenceCache(keyPrefix);
        }

        // 3. 获取序列号
        long sequence = sequenceCache++;

        // 4. 更新上一次的时间戳
        lastTimestamp = timestamp;

        // 5. 拼接并返回
        return timestamp << SEQUENCE_NUMBER | sequence;
    }

    /**
     * 刷新本地缓存的序列号
     *
     * @param keyPrefix
     */
    private void refreshSequenceCache(String keyPrefix) {
        // 1. 获取当前日期，精确到天
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        // 2. 从 Redis 一次获取 1000 个序列号，并缓存
        long start = redisUtils.increment("icr:" + keyPrefix + ":" + date, CACHE_SIZE);
        sequenceCache = start;
        sequenceCacheEnd = start + CACHE_SIZE;
        if (sequenceCacheEnd > MAX_SEQUENCE) {
            // 序列号已超出最大值
            throw new RuntimeException("从Redis获取的序列号超出了最大限制");
        }
    }

}

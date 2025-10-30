package org.example.PCOI.Service.Impl;

import org.example.PCOI.Service.Inter.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

    @Override
    public void logMethodExecution(String username, String operationType, String ipAddress) {
        // 目前仅输出到应用日志；后续可接入数据库/消息队列
        LOGGER.info("operation={}, user={}, ip={}", operationType, username, ipAddress);
    }
}


package org.example.PCOI.Service.Impl;

import org.example.PCOI.Mapper.OperationLogsMapper;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {
    private final OperationLogsMapper operationlogsmapper;

    public LogServiceImpl(OperationLogsMapper operationlogsmapper) {
        this.operationlogsmapper = operationlogsmapper;
    }

    public void logMethodExecution(String username,String operationType,String ipAddress) {

        operationlogsmapper.insertLog(username, operationType, ipAddress);
    }
}

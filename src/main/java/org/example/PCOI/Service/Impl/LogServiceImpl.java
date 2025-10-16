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

    public void logMethodExecution(String username) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String operationType = stack.length > 2 ? stack[2].getMethodName() : "defaultOperation";


        String ipAddress = "192.168.80.16";

        operationlogsmapper.insertLog(username, operationType, ipAddress);
    }
}

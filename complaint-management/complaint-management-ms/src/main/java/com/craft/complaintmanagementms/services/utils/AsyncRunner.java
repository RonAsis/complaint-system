package com.craft.complaintmanagementms.services.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
@Slf4j
public class AsyncRunner {

    @Autowired
    private TaskExecutor taskExecutor;

    public Future<Boolean> asynTaskNoPool(Runnable runable) {
        return taskExecutor.asynTaskNoPool(runable, RequestContext.getContext().getId());
    }
    ////////////////////////////////////////////////// Inner class to run async //////////////////////////////////////////////////

    @Service
    class TaskExecutor {

        @Async
        public Future<Boolean> asynTaskNoPool(Runnable runable, String xReqId) {
            try {
                RequestContext.getContext().setId(xReqId);
                runable.run();
                return new AsyncResult<Boolean>(true);
            } catch (Exception e) {
                log.error("Async Task exception:", e);
                throw e;
            }
        }

    }
}

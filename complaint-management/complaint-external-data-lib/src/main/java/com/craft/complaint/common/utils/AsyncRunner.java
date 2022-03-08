package com.craft.complaint.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.Future;

@Service
@Slf4j
public class AsyncRunner {

    @Autowired
    private TaskExecutor taskExecutor;

    public Future<Boolean> asynTaskNoPool(Runnable runable) {
        return taskExecutor.asynTaskNoPool(runable, RequestContext.getContext().getId());
    }

    public Future<Boolean> asynTaskNoPool(Runnable runable, JobCounter... counters) {
        return taskExecutor.asynTaskNoPool(runable, RequestContext.getContext().getId(), counters);
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

        @Async
        public Future<Boolean> asynTaskNoPool(Runnable runable, String xReqId, JobCounter... counters) {
            try {
                RequestContext.getContext().setId(xReqId);
                runable.run();
                updateCounters(counters);
                return new AsyncResult<Boolean>(true);
            } catch (Exception e) {
                log.error("Async Task exception:", e);
                updateCounters(counters);
                throw e;
            }

        }

        private void updateCounters(JobCounter[] counters) {
            if (counters != null) {
                for (JobCounter counter : counters) {
                    counter.jobFinished();
                }
            }
        }

    }

    /////////////////////////////////////////////////////////////

    public static class JobCounter {

        final Logger logger = LoggerFactory.getLogger(JobCounter.class);

        private int totalJobs = 0;
        private boolean killJob = false;
        private String jobName;
        private ArrayList<Future<Boolean>> alljobs = new ArrayList<>();

        public JobCounter() {
            this.jobName = "";
        }

        public JobCounter(String jobName) {
            this.jobName = jobName;
        }

        public synchronized void jobFinished() {
            totalJobs++;
            notifyAll();
        }

        public boolean isKillJob() {
            return killJob;
        }

        public synchronized int getTotalJobs() {
            return totalJobs;
        }

        public synchronized void stopWaiting() {
            killJob = true;
            logger.error("Stop job {} was called", this.jobName);
            for (Future<Boolean> job : alljobs) {
                //				if(!job.isDone()){
                if (job != null) {
                    job.cancel(true);
                }
                //				}
            }
            notifyAll();
        }

        public boolean waitForAllJobsDone(int expectedTotalJobs) {
            return waitForAllJobsDone(expectedTotalJobs, 1, false);
        }

        public boolean waitForAllJobsDone(int expectedTotalJobs, boolean logInDebug) {
            return waitForAllJobsDone(expectedTotalJobs, 1, logInDebug);
        }

        public boolean waitForAllJobsDone(int expectedTotalJobs, int batchLogPrintsSize) {
            return waitForAllJobsDone(expectedTotalJobs, batchLogPrintsSize, false);
        }

        public synchronized boolean waitForAllJobsDone(int expectedTotalJobs, int batchLogPrintsSize, boolean logInDebug) {
            if (batchLogPrintsSize < 1) {
                batchLogPrintsSize = 1;
            }
            boolean firstTime = true;
            int currTotalJobs = getTotalJobs();
            while (currTotalJobs < expectedTotalJobs) {
                try {
                    if (killJob) {
                        logger.info("job {} was interupted", this.jobName);
                        totalJobs = 0;
                        return false;
                    }
                    wait();
                    currTotalJobs = getTotalJobs();
                    if (firstTime || currTotalJobs % batchLogPrintsSize == 0) {
                        firstTime = false;
                        if (logInDebug) {
                            logger.debug("job {} done: {} of {}", this.jobName, currTotalJobs, expectedTotalJobs);
                        } else {
                            logger.info("job {} done: {} of {}", this.jobName, currTotalJobs, expectedTotalJobs);
                        }
                    }
                } catch (InterruptedException e) {
                    logger.error("Got interrupt", e);
                }
            }

            if (logInDebug) {
                logger.debug("all jobs {} ended: {} of {}", this.jobName, getTotalJobs(), expectedTotalJobs);
            } else {
                logger.info("all jobs {} ended: {} of {}", this.jobName, getTotalJobs(), expectedTotalJobs);
            }
            totalJobs = 0;
            return true;
        }

        public void addProcces(Future<Boolean> result) {
            if (killJob) {
                logger.error("Job {} was already killed, stopping new process", this.jobName);
                result.cancel(true);
                return;
            }
            alljobs.add(result);
        }

        public boolean isAllJobsEnded() {
            for (Future<Boolean> job : alljobs) {
                if (!job.isDone()) {
                    return false;
                }
            }
            return true;
        }
    }
}

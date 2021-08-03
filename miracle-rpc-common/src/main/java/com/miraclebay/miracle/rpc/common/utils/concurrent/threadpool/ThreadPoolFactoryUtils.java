package com.miraclebay.miracle.rpc.common.utils.concurrent.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public final class ThreadPoolFactoryUtils {
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();
    private ThreadPoolFactoryUtils(){}

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix){
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix, CustomThreadPoolConfig customThreadPoolConfig){
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon){
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k-> createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon));
        //如果线程池被shutdown的话就重新创建一个
        if (threadPool.isShutdown() || threadPool.isTerminated()){
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    /**
     * 关闭所有线程池
     */
    public static void shutDownAllThreadPool(){
        log.info("call shutDownAllThreadPool method");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry->{
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("shut down thread pool [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            }catch (InterruptedException e){
                log.error("Thread pool nerver terminated");
                executorService.shutdown();
            }
        });
    }

    /**
     * 创建线程池
     * @param customThreadPoolConfig
     * @param threadNamePrefix
     * @param daemon
     * @return
     */
    public static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, boolean daemon){
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaximumPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(), customThreadPoolConfig.getUnit(), customThreadPoolConfig.getWorkQueue(), threadFactory);
    }

    /*
     创建线程工厂
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon){
        if (threadNamePrefix != null ){
            if (daemon !=  null){
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d")
                                                .setDaemon(daemon).build();
            }else{
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool){
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        //设置定时任务，每个1s打印一次线程池的信息
        scheduledExecutorService.scheduleAtFixedRate(()->{
           log.info("========ThreadPool Status=======");
           log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
           log.info("Active Threads: [{}]", threadPool.getActiveCount());
           log.info("Number of Tasks: [{}]", threadPool.getCompletedTaskCount());
           log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
           log.info("================================");
        },0, 1, TimeUnit.SECONDS);
    }
}

package com.ido.qna.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @param <K> key
 */
@Slf4j
public class CacheMap<K> implements FunctionInterface.BeforeCleanUp<K> {
    private Map<K, Object> map;
    /**
     * for name the work thread
     */
    private static int THREAD_COUNT = 0;
    /**
     * make the time out configurable
     */
    private long TIMEOUT = 60 * 1;
    //put key to store a object with create time
    //this map can be clean up after a key was first create after a defined time


    @Override
    public void beforeCleanUp(Map<K, Object> toRemove) {

    }

    public CacheMap(Map<K, Object> t) {
        this.map = t;
        CleanWorker worker = new CleanWorker(map, this, "cache-clean-up" + THREAD_COUNT++);
        worker.start();


    }

    @Slf4j
    public static class CacheMapBuilder<K> {
        private static CacheMapBuilder<?> builder;
        private Map<K, Object> map = null;
        private int timeout = 60;
        private FunctionInterface.BeforeCleanUp<K> defaultCleanUp = (toRemove -> {
            log.warn("default clean up ");
        });

        public static <K> CacheMapBuilder builder() {
            builder = new CacheMapBuilder<K>();
            return builder;
        }

        public CacheMapBuilder map(Map<K, Object> m) {
            Objects.requireNonNull(m);
            this.map = m;
            return this;
        }

        /**
         * how many time after put into the map to remove, second base  time unit
         *
         * @param t second
         * @return
         */
        public CacheMapBuilder timeout(int t) {
            if (t == 0) {
                throw new IllegalArgumentException("time out must greater than 0");
            }
            this.timeout = t;
            return this;
        }

        public CacheMapBuilder beforeCleanUpCallBack(FunctionInterface.BeforeCleanUp<K> callback) {
            if (callback != null) {
                this.defaultCleanUp = callback;
            }
            return this;
        }

        public CacheMap<K> build() {
            CacheMap<K> map = new CacheMap<>(this.map, this.defaultCleanUp, this.timeout);
            return map;

        }
    }


    public CacheMap(Map<K, Object> t, FunctionInterface.BeforeCleanUp<K> cleanUp) {
        this.map = t;
        CleanWorker<K> worker = new CleanWorker(map, cleanUp, "cache-clean-up" + THREAD_COUNT++);
        worker.start();


    }

    public CacheMap(Map<K, Object> t, FunctionInterface.BeforeCleanUp<K> cleanUp, int timeout) {
        this.map = t;
        this.TIMEOUT = timeout;
        CleanWorker worker = new CleanWorker(map, cleanUp, "cache-clean-up" + THREAD_COUNT++);
        worker.start();


    }

    /**
     *
     * @param t table to store
     * @param cleanUp before remove listener
     * @param timeout second base
     * @param threadName cleaner work thread name
     */
    public CacheMap(Map<K, Object> t, FunctionInterface.BeforeCleanUp<K> cleanUp, int timeout, String threadName) {
        this.map = t;
        this.TIMEOUT = timeout;
        CleanWorker worker = new CleanWorker(map, cleanUp, threadName);
        worker.start();


    }

    public CacheMap(Map<K, Object> t, FunctionInterface.BeforeCleanUp<K> cleanUp, String threadName) {
        this.map = t;
        CleanWorker worker = new CleanWorker(map, cleanUp, threadName);
        worker.start();


    }


    public void put(K k, Object v) {
        this.map.put(k, new TimeBase(v));
    }

    public Object get(K k) {
        return this.map.get(k) == null ? null : ((TimeBase) this.map.get(k)).getV();
    }


    static class TimeBase extends Object {
        private LocalDateTime createTime;
        private Object v;

        public TimeBase(Object v) {
            this.v = v;
            this.createTime = LocalDateTime.now();
        }

        public Object getV() {
            return v;
        }
    }


    private class CleanWorker<K> extends Thread {
        private Map<K, Object> table;
        FunctionInterface.BeforeCleanUp cleanUp;

        public CleanWorker(Map<K, Object> t, FunctionInterface.BeforeCleanUp<K> cleanUp, String name) {
            super(name);
            this.table = t;
            this.cleanUp = cleanUp;
        }


        @Override
        public void run() {
            while (true) {
                try {
                    sleep(1000 * 60);
                    Map<K, Object> toRemove = new HashMap<>(table.size() / 2);
                    List<K> keysToRemove = new ArrayList<>(table.size() / 2);

                    for (Map.Entry<K, Object> entry : table.entrySet()) {
                        TimeBase tb = (TimeBase) entry.getValue();
                        boolean timeout = tb.createTime.plusSeconds(TIMEOUT).isBefore(LocalDateTime.now());
                        if (timeout) {
                            //store what to remove for next action
                            toRemove.put(entry.getKey(), tb.getV());
                            keysToRemove.add(entry.getKey());
                        }
                    }

                    this.cleanUp.beforeCleanUp(toRemove);
                    //do clean up
                    keysToRemove.stream().forEach(k -> table.remove(k));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

package com.ido.qna.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @param <K> key
 */
@Slf4j
public class CacheMap<K> implements FunctionInterface.BeforeCleanUp<K> {
    private Map<K, Object> map;
    //TODO make the time out configurable
    private static int THREAD_COUNT = 0;
    private  static   long TIMEOUT = 60 * 1;
    //put key to store a object with create time
    //this map can be clean up after a key was first create after a defined time


    @Override
    public void beforeCleanUp(Map<K, Object> toRemove) {

    }

    public CacheMap(Map<K, Object> t) {
        this.map = t;
        CleanWorker worker = new CleanWorker(map, this, "cache-clean-up"+THREAD_COUNT++);
        worker.start();


    }

    public CacheMap(Map<K, Object> t, FunctionInterface.BeforeCleanUp<K> cleanUp) {
        this.map = t;
        CleanWorker worker = new CleanWorker(map, cleanUp, "cache-clean-up"+THREAD_COUNT++);
        worker.start();


    }

    public CacheMap(Map<K, Object> t, FunctionInterface.BeforeCleanUp<K> cleanUp,String threadName) {
        this.map = t;
        CleanWorker worker = new CleanWorker(map, cleanUp, threadName);
        worker.start();


    }





    public void put(K k, Object v) {
        this.map.put(k, new TimeBase(v));
    }

    public Object get(K k) {
        return  this.map.get(k)==null ? null:((TimeBase) this.map.get(k)).getV();
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


    private static class CleanWorker<K> extends Thread {
        private Map<K, Object> table;
        FunctionInterface.BeforeCleanUp cleanUp;

        public CleanWorker(Map<K, Object> t, FunctionInterface.BeforeCleanUp cleanUp, String name) {
            super(name);
            this.table = t;
            this.cleanUp = cleanUp;
        }


        @Override
        public void run() {
            while (true) {
                try {
                    sleep(1000 * 60);
                    Map<K,Object> toRemove = new HashMap<>(table.size()/2);
                    List<K> keysToRemove = new ArrayList<>(table.size()/2);

                    for (Map.Entry<K,Object> entry : table.entrySet()) {
                        TimeBase tb = (TimeBase) entry.getValue();
                        boolean timeout = tb.createTime.plusSeconds(TIMEOUT).isBefore(LocalDateTime.now());
                        if (timeout) {
                            //store what to remove for next action
                            toRemove.put( entry.getKey(),tb.getV());
                            keysToRemove.add(entry.getKey());
                        }
                    }

                    this.cleanUp.beforeCleanUp(toRemove);
                    //do clean up
                    keysToRemove.stream().forEach(k->table.remove(k));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

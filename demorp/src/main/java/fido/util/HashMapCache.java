package fido.util;

/*****************************************************************************************
 * Copyright (c) 2014. by Samsung Security Center. All rights reserved.
 * This software is the confidential and proprietary information of Samsung SDS, Inc.
 * ("Confidential information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you
 * entered into with Samsung SDS.
 ****************************************************************************************/

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Singleton;

import com.google.common.collect.Maps;

/**
 * Simply implemented Cache Class using HashMap.
 */
@Singleton
public class HashMapCache implements Closeable {

    private final Map<String, String> storage = Maps.newConcurrentMap();

    private final Timer timer = new Timer();
    
    public void flush() {
        storage.clear();
    }

    public Map<String, String> getList(){
    	return storage;
    }
    
    public String get(String key) {
        return storage.get(key);
    }

    public void put(final String key, String value, long timeout) throws IllegalStateException {
        put(key, value);

        class DataRemovalTask extends TimerTask {

            @Override
            public void run() {
                storage.remove(key);
            }
        }
        timer.schedule(new DataRemovalTask(), timeout * 1000);
    }

    public void put(String key, String value) throws IllegalStateException {
        storage.put(key, value);
    }

    public String remove(String key) {
        return storage.remove(key);
    }

    public void close() throws IOException {

    }
    
    public static void main(String[] args) throws InterruptedException {
    	HashMapCache tmp = new HashMapCache();
    	tmp.put("test","test");
    	tmp.put("test1", "test1", 1);
    	
    	System.out.println(tmp.get("test"));
    	System.out.println(tmp.get("test1"));
    	
    	Thread.sleep(2000);
    	
    	System.out.println(tmp.get("test"));
    	System.out.println(tmp.get("test1"));
    	
    	tmp.put("test1", "test1", 1);
    	System.out.println(tmp.get("test1"));
    	tmp.put("test1", "test2");
    	System.out.println(tmp.get("test1"));
    	
    	System.out.println("===============");
    	System.out.println(tmp.get("test"));
    	System.out.println(tmp.get("test1"));
    	
    	Thread.sleep(100);
    	
    	System.out.println(tmp.get("test"));
    	System.out.println(tmp.get("test1"));
    	
    	Thread.sleep(1000);
    	
    	System.out.println(tmp.get("test"));
    	System.out.println(tmp.get("test1"));
    	
	}
}
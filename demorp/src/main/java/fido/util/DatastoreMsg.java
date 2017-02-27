/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fido.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Simple implementation of a data store using standard Java collections.
 * <p>
 * This class is thread-safe but not persistent (it will lost the data when the
 * app is restarted) - it is meant just as an example.
 */
public final class DatastoreMsg {

//  private static final Map<String,String> toekns = new HashMap<String,String>();
  private static final Map<String,String> fidoMysingle = new HashMap<String,String>();
  private static final List<String> regIds = new ArrayList<String>();
  private static final Logger logger = Logger.getLogger(DatastoreMsg.class.getName());
  private static final HashMapCache sessionIdPool = new HashMapCache();

  private DatastoreMsg() {
    throw new UnsupportedOperationException();
  }

  public static void registerSessionIdPool(String key, String value, long timeout) {
	  logger.info("Registering " + key);
	  synchronized (sessionIdPool) {
		  sessionIdPool.put(key, value, timeout);
	  }
  }
  
  public static void updateSessionIdPool(String key, String value) {
	  logger.info("Registering " + key);
	  if(sessionIdPool.get(key) != null){
		  synchronized (sessionIdPool) {
			  sessionIdPool.put(key, value);
		  }
	  }
  }
  
  public static void unregisterSessionIdPool(String key) {
	  logger.info("Unregistering " + key);
	  synchronized (sessionIdPool) {
		  sessionIdPool.remove(key);
	  }
  }
  
  public static HashMapCache getSessionIdPool() {
	    synchronized (sessionIdPool) {
	      return sessionIdPool;
	    }
  }
  
  public static Map<String, String> getSessionIdPoolList(){
	  return sessionIdPool.getList();
  }
  
  
  /**
   * Registers a fidoMysingles.
   */
  public static void registerFidoMySingle(String loginId, String msg) {
    logger.info("Registering " + loginId);
    synchronized (fidoMysingle) {
    	fidoMysingle.put(loginId, msg);
    }
  }

  /**
   * Unregisters a fidoMysingles.
   */
  public static void unregisterFidoMySingle(String loginId) {
    logger.info("Unregistering " + loginId);
    synchronized (fidoMysingle) {
    	fidoMysingle.remove(loginId);
    }
  }

  /**
   * Gets all registered fidoMysingles.
   */
  public static Map<String, String> getFidoMySingle() {
    synchronized (fidoMysingle) {
      return new HashMap<String, String>(fidoMysingle);
    }
  }

  public static void register(String regId) {
    logger.info("Registering " + regId);
    synchronized (regIds) {
      regIds.add(regId);
    }
  }
  public static void unregister(String regId) {
    logger.info("Unregistering " + regId);
    synchronized (regIds) {
      regIds.remove(regId);
    }
  }
  public static void updateRegistration(String oldId, String newId) {
    logger.info("Updating " + oldId + " to " + newId);
    synchronized (regIds) {
      regIds.remove(oldId);
      regIds.add(newId);
    }
  }
  public static List<String> getDevices() {
    synchronized (regIds) {
      return new ArrayList<String>(regIds);
    }
  }
}
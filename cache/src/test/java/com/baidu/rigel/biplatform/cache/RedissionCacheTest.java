/**
 * Copyright (c) 2014 Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.rigel.biplatform.cache;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;
import org.redisson.core.Predicate;
import org.redisson.core.RMap;
import org.springframework.cache.Cache.ValueWrapper;

/**
 *Description:
 * @author david.wang
 *
 */
public class RedissionCacheTest {
    
    @Test
    public void testGetWithNull () {
        RedissonCache cache = genCache ();
        ValueWrapper value = cache.get ("");
        Assert.assertNull (value);
    }

    @Test
    public void testGetWithExistKey () {
        RedissonCache cache = genCache ();
        ValueWrapper value = cache.get ("test");
        Assert.assertNull (value);
    }
    
    @Test
    public void testGet () {
        RedissonCache cache = genCache ();
        cache.put ("test", "test");
        ValueWrapper value = cache.get ("test");
        Assert.assertNotNull (value);
    }
    
    @Test
    public void testClear () {
        RedissonCache cache = genCache ();
        cache.put ("test", "test");
        cache.clear ();
        ValueWrapper value = cache.get ("test");
        Assert.assertNull (value);
    }
    
    @Test
    public void testDelete () {
        RedissonCache cache = genCache ();
        cache.put ("test", "test");
        cache.evict ("");
        Assert.assertNotNull (cache.get ("test"));
        cache.evict ("test");
        ValueWrapper value = cache.get ("test");
        Assert.assertNull (value);
    }
    
    @Test
    public void testPutIfAbsent() {
        RedissonCache cache = genCache ();
        Assert.assertNull (cache.putIfAbsent ("test", "test"));
        Assert.assertNotNull (cache.putIfAbsent ("test", "test"));
    }
    
    @Test
    public void testGetWithType () {
        RedissonCache cache = genCache();
        cache.put ("test", "test");
        String value = cache.get ("test", String.class);
        Assert.assertNotNull (value);
        Assert.assertEquals ("test", value);
        
        Assert.assertNull (cache.get ("", String.class));
    }
    
    private RedissonCache genCache() {
        RMap<Object, Object> map = new RMap<Object, Object>() {
            ConcurrentHashMap<Object, Object> tmp = new ConcurrentHashMap<Object, Object>();
            @Override
            public Object putIfAbsent(Object key, Object value) {
                return tmp.putIfAbsent (key, value);
            }

            @Override
            public boolean remove(Object key, Object value) {
                return tmp.remove (key, value);
            }

            @Override
            public boolean replace(Object key, Object oldValue, Object newValue) {
                tmp.put (key, newValue);
                return true;
            }

            @Override
            public Object replace(Object key, Object value) {
                return tmp.put (key, value);
            }

            @Override
            public int size() {
                return tmp.size ();
            }

            @Override
            public boolean isEmpty() {
                return tmp.isEmpty ();
            }

            @Override
            public boolean containsKey(Object key) {
                return tmp.containsKey (key);
            }

            @Override
            public boolean containsValue(Object value) {
                return tmp.containsValue (value);
            }

            @Override
            public Object get(Object key) {
                return tmp.get (key);
            }

            @Override
            public Object put(Object key, Object value) {
                return tmp.put (key, value);
            }

            @Override
            public Object remove(Object key) {
                return tmp.remove (key);
            }

            @Override
            public void putAll(Map<? extends Object, ? extends Object> m) {
                tmp.putAll (m);
            }

            @Override
            public void clear() {
                tmp.clear ();
            }

            @Override
            public Set<Object> keySet() {
                return tmp.keySet ();
            }

            @Override
            public Collection<Object> values() {
                return tmp.values ();
            }

            @Override
            public Set<java.util.Map.Entry<Object, Object>> entrySet() {
                return tmp.entrySet ();
            }

            @Override
            public boolean expire(long timeToLive, TimeUnit timeUnit) {
                return false;
            }

            @Override
            public boolean expireAt(long timestamp) {
                return false;
            }

            @Override
            public boolean expireAt(Date timestamp) {
                return false;
            }

            @Override
            public boolean clearExpire() {
                return false;
            }

            @Override
            public long remainTimeToLive() {
                return 0;
            }

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public void delete() {
                tmp.clear ();
            }

            @Override
            public Object addAndGet(Object key, Object delta) {
                return tmp.putIfAbsent (key, delta);
            }

            @Override
            public Map<Object, Object> getAll(Set<Object> keys) {
                return null;
            }

            @Override
            public Map<Object, Object> filterEntries(
                    Predicate<java.util.Map.Entry<Object, Object>> predicate) {
                return null;
            }

            @Override
            public Map<Object, Object> filterValues(Predicate<Object> predicate) {
                return null;
            }

            @Override
            public Map<Object, Object> filterKeys(Predicate<Object> predicate) {
                return null;
            }

            @Override
            public long fastRemove(Object... keys) {
                return 0;
            }

            @Override
            public Future<Long> fastRemoveAsync(Object... keys) {
                return null;
            }

            @Override
            public Future<Boolean> fastPutAsync(Object key, Object value) {
                return null;
            }

            @Override
            public boolean fastPut(Object key, Object value) {
                return false;
            }

            @Override
            public Future<Object> getAsync(Object key) {
                return null;
            }

            @Override
            public Future<Object> putAsync(Object key, Object value) {
                return null;
            }

            @Override
            public Future<Object> removeAsync(Object key) {
                this.tmp.remove (key);
                return new Future<Object>() {

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public boolean isDone() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public Object get() throws InterruptedException,
                            ExecutionException {
                        return tmp.remove (key);
                    }

                    @Override
                    public Object get(long timeout, TimeUnit unit)
                            throws InterruptedException, ExecutionException,
                            TimeoutException {
                        return get();
                    }

                    @Override
                    public boolean isSuccess() {
                        return true;
                    }

                    @Override
                    public boolean isCancellable() {
                        return false;
                    }

                    @Override
                    public Throwable cause() {
                        return null;
                    }

                    @Override
                    public Future<Object> addListener(
                            GenericFutureListener<? extends Future<? super Object>> listener) {
                        return null;
                    }

                    @Override
                    public Future<Object> addListeners(
                            GenericFutureListener<? extends Future<? super Object>>... listeners) {
                        return null;
                    }

                    @Override
                    public Future<Object> removeListener(
                            GenericFutureListener<? extends Future<? super Object>> listener) {
                        return null;
                    }

                    @Override
                    public Future<Object> removeListeners(
                            GenericFutureListener<? extends Future<? super Object>>... listeners) {
                        return null;
                    }

                    @Override
                    public Future<Object> sync() throws InterruptedException {
                        return null;
                    }

                    @Override
                    public Future<Object> syncUninterruptibly() {
                        return null;
                    }

                    @Override
                    public Future<Object> await() throws InterruptedException {
                        return null;
                    }

                    @Override
                    public Future<Object> awaitUninterruptibly() {
                        return null;
                    }

                    @Override
                    public boolean await(long timeout, TimeUnit unit)
                            throws InterruptedException {
                        return false;
                    }

                    @Override
                    public boolean await(long timeoutMillis)
                            throws InterruptedException {
                        return false;
                    }

                    @Override
                    public boolean awaitUninterruptibly(long timeout,
                            TimeUnit unit) {
                        return false;
                    }

                    @Override
                    public boolean awaitUninterruptibly(long timeoutMillis) {
                        return false;
                    }

                    @Override
                    public Object getNow() {
                        try {
                            return get();
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    public boolean cancel(boolean mayInterruptIfRunning) {
                        return false;
                    }
                };
            }
            
        };
        RedissonCache cache = new RedissonCache (map, "test");
        return cache;
    }
}

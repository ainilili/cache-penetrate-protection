package org.smallnico.demo.component;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.smallnico.demo.domain.User;
import org.smallnico.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

@Component
public class CacheComponent implements Map<Integer, User>, SmartLifecycle{

    @Autowired
    private UserMapper userMapper;
    
    //Map代替缓存中间件
    private Map<Integer, User> cache = new ConcurrentHashMap<Integer, User>();

    private BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), 1000 * 10000, 0.0001F);
    
    public boolean mightContain(Integer userId) {
        return bloomFilter.mightContain(userId);
    }
    
    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    @Override
    public User get(Object key) {
        return cache.get(key);
    }

    @Override
    public User put(Integer key, User value) {
        return cache.put(key, value);
    }

    @Override
    public User remove(Object key) {
        return cache.remove(key);
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends User> m) {
        cache.putAll(m);        
    }

    @Override
    public void clear() {
        cache.clear();        
    }

    @Override
    public Set<Integer> keySet() {
        return cache.keySet();
    }

    @Override
    public Collection<User> values() {
        return cache.values();
    }

    @Override
    public Set<Entry<Integer, User>> entrySet() {
        return cache.entrySet();
    }
    
    /**
     * 初始化缓存及初始化BloomFilter
     */
    @Override
    public void start() {
        List<User> users = userMapper.selectList(null);
        if(! CollectionUtils.isEmpty(users)) {
            users.forEach(u -> {
                bloomFilter.put(u.getId());
                put(u.getId(), u);
            });
        }
    }

    @Override
    public void stop() {
        
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void stop(Runnable callback) {
    }
	
	
}
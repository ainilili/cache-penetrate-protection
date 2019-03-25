package org.smallnico.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.smallnico.demo.component.CacheComponent;
import org.smallnico.demo.domain.User;
import org.smallnico.demo.mapper.UserMapper;
import org.smallnico.demo.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/test")
public class DemoController {

    @Autowired
    private CacheComponent cacheComponent;

    @Autowired
    private UserMapper userMapper;

    @ApiOperation(value = "存在缓存穿透缺陷")
    @GetMapping("/penetrate")
    public ResultVo penetrate() {
        return test(false);
    }
    
    @ApiOperation(value = "不存在缓存穿透缺陷")
    @GetMapping("/protection")
    public ResultVo protection() {
        return test(true);
    }
    
    public ResultVo test(boolean prot) {
        List<Integer> userIds = getUserIds();
        
        final AtomicLong hitCounter = new AtomicLong();
        final AtomicLong missCounter = new AtomicLong();
        
        long start = System.nanoTime();
        
        userIds.forEach(id -> {
            User u = cacheComponent.get(id);
            if(u != null) {
                hitCounter.incrementAndGet();
            }else {
                boolean exist = true;
                
                if(prot) {
                    exist = cacheComponent.mightContain(id);
                }
                
                if(exist) {
                    missCounter.incrementAndGet();
                    
                    User user = userMapper.selectById(id);
                    if(user != null) {
                        cacheComponent.put(user.getId(), user);
                    }
                }
            }
        });
        
        long spend = (System.nanoTime() - start) / 1000000;
        
        return new ResultVo(hitCounter, missCounter, spend);
    }

    private List<Integer> getUserIds(){
        List<Integer> userIds = new ArrayList<Integer>();

        Integer start = Integer.MAX_VALUE;
        while(start > Integer.MAX_VALUE - 1000000) {
            userIds.add(start --);
        }
        return userIds;
    }

}

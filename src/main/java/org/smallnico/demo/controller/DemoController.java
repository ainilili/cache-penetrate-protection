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
    
    /**
     * 重现缓存穿透场景
     * 
     * @param prot 是否开启防护，true则开启
     * @return {@link ResultVo}
     */
    public ResultVo test(boolean prot) {
        List<Integer> userIds = getUserIds();
        
        //计数
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
                    //判断该id是否存在
                    //Bloom Filter不能确定数据一定存在，但是能确定数据一定不存在
                    //需要在容错率和性能两方面权衡
                    exist = cacheComponent.mightContain(id);
                }
                
                if(exist) {
                    missCounter.incrementAndGet();
                    
                    //db操作
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

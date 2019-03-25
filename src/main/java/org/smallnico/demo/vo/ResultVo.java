package org.smallnico.demo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ResultVo {

    @ApiModelProperty(value = "命中缓存")
    private Object hit;
    
    @ApiModelProperty(value = "未命中缓存并且执行了数据库同步缓存的逻辑")
    private Object miss;
    
    @ApiModelProperty(value = "消耗时长(毫秒)")
    private Long expend;

    public ResultVo(Object hit, Object miss, Long expend) {
        super();
        this.hit = hit;
        this.miss = miss;
        this.expend = expend;
    }

    public Long getExpend() {
        return expend;
    }

    public void setExpend(Long expend) {
        this.expend = expend;
    }

    public Object getHit() {
        return hit;
    }

    public void setHit(Object hit) {
        this.hit = hit;
    }

    public Object getMiss() {
        return miss;
    }

    public void setMiss(Object miss) {
        this.miss = miss;
    }
    
}

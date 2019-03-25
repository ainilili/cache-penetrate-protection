# 介绍
这是一个演示缓存穿透以及如何防止缓存穿透的Demo.
# 何为缓存穿透
业务场景中一般使用类似``redis``、``memcached``等缓存中间件来减少db的压力，伪代码如下：
```java
Obj o = cache.get(id);
if(o != null){
    return o;
}else{
    o = db.get(id);
    if(o != null){
        cache.put(o.id, o);   
    }
    return o;
}
```
以上是一个根据id查询信息，通过cache做缓存的典型场景，前提是请求的id对应的数据都是在db真实存在的，如果模拟大量冷且无效的数据，那么cache的作用形同摆设。

简单来说，上述场景中db的数据id范围是0~65536，但是大量的查询请求的id所在范围大于65536，那么将会导致穿透过cache，一直走db查询的逻辑，这将会导致db的压力剧增，这就是所谓的**缓存穿透**！
# 如何防止
## 无效数据也放于缓存
改动一下上述伪代码：
```java
Obj o = cache.get(id);
if(o != null){
    return o;
}else{
    o = db.get(id);
    cache.put(o.id, o);   
    return o;
}
```
## BloomFilter
这里不过多阐述，详情可以百度百科一波：[https://baike.baidu.com/item/bloom%20filter/6630926?fr=aladdin](https://baike.baidu.com/item/bloom%20filter/6630926?fr=aladdin)

演示中使用``guava``自带的``Bloom Filter``!
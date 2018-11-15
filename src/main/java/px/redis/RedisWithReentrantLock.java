package px.redis;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * redis可重入锁  copy to 掘金小册 Redis深度历险
 */
public class RedisWithReentrantLock {



    private ThreadLocal<Map<String,Integer>> lockers=new ThreadLocal<>();

    private Jedis jedis;

    public RedisWithReentrantLock(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 使用set key value ex time nx让set和expire成原子操作
     * @param key
     * @return
     */
    public boolean _lock(String key){
        return jedis.set(key,"","nx","ex",5L)!=null;
    }

    public void  _unlock(String key){
        jedis.del(key);
    }


    private Map<String,Integer> currentLockers(){
        /**
         * key  获取锁次数
         */
        Map<String,Integer> refs=lockers.get();
        if(refs!=null){
            return refs;
        }
        lockers.set(new HashMap<>());
        return lockers.get();
    }

    public boolean lock(String key){
        Map<String,Integer> refs=currentLockers();
        Integer refCnt=refs.get(key);
        if(refCnt!=null){
            refs.put(key,refCnt+1);
            return true;
        }
        boolean ok=this._lock(key);
        if(!ok){
            return false;
        }
        refs.put(key,1);
        return true;
    }

    public boolean unlock(String key){
        Map<String,Integer> refs=currentLockers();
        Integer refCnt=refs.get(key);
        if(refCnt==null){
            return false;
        }
        refCnt-=1;
        if(refCnt>0){
            refs.put(key,refCnt);
        }else{
            refs.remove(key);
            this._unlock(key);
        }
        return true;
    }

}

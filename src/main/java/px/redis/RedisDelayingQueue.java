package px.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

/**
 * redis延时队列  copy to 掘金小册 Redis深度历险
 */
public class RedisDelayingQueue<T> {


    static class TaskItem<T>{
        public String id;
        public T msg;
    }

    /**
     * fastjson序列化generic类型
      */
   private Type taskType=new TypeReference<TaskItem<T>>(){}.getType();

   private Jedis jedis;

   private String queueKey;

    public RedisDelayingQueue(Jedis jedis, String queueKey) {
        this.jedis = jedis;
        this.queueKey = queueKey;
    }


    public void delay(T msg){
        TaskItem<T> task=new TaskItem<>();
        task.id= UUID.randomUUID().toString();//唯一id
        task.msg=msg;
        String s= JSON.toJSONString(task);
        jedis.zadd(queueKey,System.currentTimeMillis()+5000,s);//放入延时队列，5s后再试
    }

    public void loop(){
        while (!Thread.interrupted()){
            //只取一条,0到当前时间的意思 是把5秒以前进队列的任务取出来
            Set<String> values = jedis.zrangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
            if(values.isEmpty()){
                try {
                    Thread.sleep(500);
                }catch (InterruptedException e){
                    break;
                }
                continue;
            }
            String s=values.iterator().next();
            if(jedis.zrem(queueKey,s)>0){//抢到，但是如果这个时候抛出异常，任务就丢失了，缺乏ack机制
                TaskItem<T> task=JSON.parseObject(s,taskType);
                System.out.println(task.msg);
            }
        }
    }

    public static void main(String[] args) {
        Jedis jedis=new Jedis();
        RedisDelayingQueue<String> stringRedisDelayingQueue=new RedisDelayingQueue<>(jedis,"q-demo");
        Thread producer= new Thread(() -> {
            for(int i=0;i<10;i++){
                stringRedisDelayingQueue.delay("codehole"+i);
            }
        });
        Thread consumer= new Thread(() -> stringRedisDelayingQueue.loop());
        producer.start();
        consumer.start();
        try {
            producer.join();
            Thread.sleep(6000);
            consumer.interrupt();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

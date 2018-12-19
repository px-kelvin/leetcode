package px.limit;

import com.google.common.util.concurrent.RateLimiter;

public class SmoothBursty {


    public static void main(String[] args) {
        //一秒创建5个令牌，每隔200毫秒一个
        RateLimiter rateLimiter=RateLimiter.create(5);
        //当前桶中有足够令牌，则返回成功0，没有则暂停一下，比如说这里差不多都是返回200ms
        System.out.println(rateLimiter.acquire());
        System.out.println(rateLimiter.acquire());
        System.out.println(rateLimiter.acquire());
        System.out.println(rateLimiter.acquire());
        System.out.println(rateLimiter.acquire());
        System.out.println(rateLimiter.acquire());

    }

}

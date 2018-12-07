package px.netty;

import java.util.concurrent.TimeUnit;

public class DaemonThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        long startTime=System.nanoTime();
        Thread t=new Thread(()->{
            try {
                TimeUnit.DAYS.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"Daemon-T");
        t.setDaemon(true);//如果daemon=true，那么5秒后jvm就会退出，如果=fasle，即便超过5秒，main线程执行完毕，也不会退出
        t.start();
        TimeUnit.SECONDS.sleep(5);
        System.out.println("系统退出，执行"+(System.nanoTime()-startTime)/1000/1000/1000+"s");
    }
}

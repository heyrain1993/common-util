package com.heyu.common.utils;

import com.heyu.common.enums.DateEnum;
import org.junit.Test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class DateUtilTest {

    /**
     * 线程池：创建线程池的参数
     * 1.corePoolSize:核心线程池大小
     * 2.maximumPoolSize:线程池最大允许线程数
     * 3.keepAliveTime:线程允许空闲时间
     * 4.TimeUnit:时间单位
     * 5.workQueue:队列
     * 6.RejectedExecutionHandler:拒绝策略。常见:AbortPolicy(抛异常)、CallerRunsPolicy(有提交任务者执行)、DiscardPolicy(直接丢弃任务)和DiscardOldestPolicy(丢弃最老的任务)
     * 当先线程池添加任务时，线程池先创建线程处理；如果当前线程池线程数量达到corePoolSize，则将任务丢到任务队列；
     * 当任务队列满的时候还有任务过来时，则创建新的线程解决，知道线程池线程数量达到maximumPoolSize；
     * 如果达到maximumPoolSize还有任务过来时，则根据创建线程时设置的拒绝策略拒绝任务。
     *
     * 当任务减少后，如果线程池线程数量大于corePoolSize，且有线程的空闲时间超过keepAliveTime，则终止该线程，知道线程池数量不大于corePoolSize
     */

    /**
     * 创建线程池几种方法：
     * 1.Executors.newCachedThreadPool():该种线程池线程数量最小为0，最大不限；造成线程数量不可控
     * 2.Executors.newFixedThreadPool(int nThreads):创建固定大小线程时，但使用无界队列作为任务队列，可能造成OOM
     * 3.Executors.newScheduledThreadExecutor():创建能够完成定时任务的线程池
     * 4.Executors.newSingleThreadExecutor():创建单个线程的线程池，会导致线程耗尽
     * 注:可以通过ThreadPoolExecutor构造方法来自己构造线程池
     */

    /**
     * 1.execute(Runnable run):向线程池提交不需要返回值的任务
     * 2.Future<T> submit(Callable<T> call):向线程池提交需要返回值的任务。通过返回的future对象可以判断任务是否执行成功；通过get()方法能够获取任务的返回值，
     * 该方法会阻塞当前线程，直到任务执行完毕。如果call()方法执行抛出异常，则在调用get()方法时，会将该异常抛出
     */
    ExecutorService executorService = Executors.newCachedThreadPool();
    /**
     * 测试SimpleDateFormat的线程不安全性
     */
    @Test
    public void testSimpleDateFormatParse() throws Exception {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] strs = {"2019-01-02 12:23:56","2019-01-03 11:15:53","2019-01-12 05:06:02","2019-01-05 08:12:36","2019-01-06 02:08:09"};
        for(final String str:strs){
            Future<String> future = executorService.submit(new Callable<String>() {
                public String call() throws Exception {
                    String result = null;
                    try {
                        result = Thread.currentThread().getName() + " " +simpleDateFormat.parse(str);
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return result;

                }
            });
            System.out.println(Thread.currentThread().getName() + " " + future.get());//get()会阻塞，直到任务执行完
        }
    }

    @Test
    public void testSimpleDateFormatFormat() throws Exception {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date[] dates = {simpleDateFormat.parse("2019-01-02 12:23:56"),
                simpleDateFormat.parse("2019-01-03 11:15:53"),
                simpleDateFormat.parse("2019-01-12 05:06:02"),
                simpleDateFormat.parse("2019-01-05 08:12:36"),
                simpleDateFormat.parse("2019-01-06 02:08:09")};

        for(final Date date:dates){
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        //System.out.println(Thread.currentThread().getName()+ " " + simpleDateFormat.format(date));
                        System.out.println(Thread.currentThread().getName()+ " " + DateUtil.formatDateToStr(date,DateEnum.FORMAT_TIME));
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //Thread.sleep(3000);
    }

    @Test
    public void testFormat(){
        System.out.println(DateUtil.formatDateToStr(new Date(), DateEnum.FORMAT_TIME));
        System.out.println(DateUtil.parseStrToDate("2019-01-02 12:23:56",DateEnum.FORMAT_TIME));
    }
}

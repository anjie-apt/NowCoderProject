package com.company.toutiao;


import java.util.concurrent.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends Thread {
    //自定义内集成Thread类，重载run()方法
    private int tid;

    public MyThread(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                System.out.println(String.format("%d:%d", tid, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue<String> queue;
    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(Thread.currentThread().getName() + ":" +queue.take());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Producer implements Runnable {
    private BlockingQueue<String> queue;
    public Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(10);
                queue.put(String.valueOf(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class MultiThreadTests {
    public static void testThread() {
//        for (int i = 0; i < 10; i++){
//            new MyThread(i).run();
//        }

        for (int i = 0; i < 10; i++){
            final int finalI = i;
            //implements Runnable(),实现run()方法
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 10; j++) {
                            Thread.sleep(100);
                            System.out.println(String.format("T2 %d: %d:", finalI, j));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static Object object = new Object();

    public static void testSynchronized1() {
        synchronized (object) {
            try {
                for (int j = 0; j < 10; j++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3 %d:", j));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized2() {
        synchronized (object) {
            try {
                for (int j = 0; j < 10; j++) {
                    Thread.sleep(100);
                    System.out.println(String.format("T4 %d:", j));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSychronized() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }

    public static void testBlockingQueue() {
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue), "Consumer1").start();
        new Thread(new Consumer(queue), "Consumer2").start();
    }
    /**
     * ThreadLocal:
     * 1.线程局部变量，即使是一个static成员，每个线程访问的变量是不同的
     * 2.常见于web中存储当前用户到一个静态工具类中，在线程的任何地方都可以访问到当前线程的用户
     * 3.参考hostholder.java中的users
     */
    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();
    private static int userId;

    public static void testThreadLocal() {
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        threadLocalUserIds.set(finalI);
                        userId = finalI;
                        Thread.sleep(1000);
                        System.out.println("UserId:" + userId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * Executor:
     * 1.提供一个运行任务的框架
     * 2.将任务和如何运行任务解耦
     * 3.常用于提供线程池或者定时任务服务
     */
    public static void testExecutor() {

//        ExecutorService service = Executors.newSingleThreadExecutor();
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1:" + i);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2:" + i);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        service.shutdown();
        while (!service.isTerminated()) {
            try {
                Thread.sleep(1000);
                System.out.println("Wait for termination");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int counter = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void testWithoutAtomic() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; ++j) {
                            counter++;
                            System.out.println("counter:" + counter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    public static void testWithAtomic() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; ++j) {
                            System.out.println("counter:" + atomicInteger.incrementAndGet());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void testAtomic() {
        //testWithoutAtomic();
        testWithAtomic();
    }

    /**
     * Future:
     * 1.返回异步结果
     * 2.阻塞等待返回结果
     * 3.设置timeout
     * 4.获取线程中的Exception
     */
    public static void testFuture() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                throw new IllegalArgumentException("异常");
                //return 1;
            }
        });

        service.shutdown();
        try {
            System.out.println(future.get());
            //System.out.println(future.get(100, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        //testThread();

        //Synchronized内置锁
        //放在方法上会锁住所有synchronized方法
        //synchronized(obj)锁住相关的代码段
        //testSychronized();

        //testBlockingQueue();

        //testThreadLocal();
        //testExecutor();
        //testAtomic();
        testFuture();
    }
}

package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {

    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);
        // 一个生产者不断生产数据，三个消费者不断消费数据(并发消费)
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

class Producer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            // 生产者最多只能生产 100 个数，每次把数放入队列中
            for (int i = 0; i < 100; ++i) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "produce:" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class Consumer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            // 只要有数据就一直消费，所以使用 while 循环
            while (true) {
                // 用户消费数据的时间是不确定的，有时快有时慢
                // 注意这里随机到的数大概率比 20 要大
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "consume:" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

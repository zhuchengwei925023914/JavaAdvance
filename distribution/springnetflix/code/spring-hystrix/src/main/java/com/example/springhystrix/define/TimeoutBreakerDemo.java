package com.example.springhystrix.define;

import java.util.Random;
import java.util.concurrent.*;

public class TimeoutBreakerDemo {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        RandomCommand randomCommand = new RandomCommand();

        Future<String> future = executorService.submit(() -> randomCommand.run());

        String result = "";
        try {
            result = future.get(100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            result = randomCommand.fallback();
        }
        System.out.println(result);
        executorService.shutdown();
    }


    public static class RandomCommand implements Command<String> {

        private Random random = new Random();

        @Override
        public String run() {
            int time = random.nextInt(150);
            System.out.println("休眠时间: " + time);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "正常返回";
        }

        @Override
        public String fallback() {
            return "降级";
        }
    }
}


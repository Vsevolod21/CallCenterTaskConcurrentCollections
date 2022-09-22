import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallCenterTask {
    private Queue<String> callsTotal = null;
    private volatile boolean cycle = true;
    private volatile boolean cycleOperator = true;


    public CallCenterTask() {
        callsTotal = new ConcurrentLinkedQueue<>();
        Thread calls = new Thread(new Calls());
        Operators operators = new Operators();
        ExecutorService operatorsPool = Executors.newFixedThreadPool(4);
//        Runnable runnable = operators1::run;
        operatorsPool.submit(operators::run);
        calls.start();
        while (cycleOperator) {
            try {
                int TIMESLEEPMAINTHREAD = 1000;
                Thread.sleep(TIMESLEEPMAINTHREAD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        operatorsPool.shutdown();
        calls.interrupt();
        System.exit(0);
    }

    class Calls implements Runnable {
        public void run() {
            System.out.println("Пошли звонки");
            try {
                for (int i = 1; i <= 10; i++) {
                    String call = "Звонок " + i;
                    callsTotal.add(call);
                    System.out.println("Поступил " + call +
                            ". Всего звонков в очереди: " + callsTotal.size());
                    int TIMESLEEPCALLSTHREAD = 1000;
                    Thread.sleep(TIMESLEEPCALLSTHREAD);
                }
                cycle = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Operators implements Runnable {
        @Override
        public void run() {
            String inputCall;
            System.out.println("Операторы готовы к работе");
            while (cycle || callsTotal.size() > 0) {
                if ((inputCall = callsTotal.poll()) != null)
                    System.out.println("    Принят " + inputCall +
                            ". Осталось звонков: " + callsTotal.size());
                try {
                    int TIMESLEEPOPERATORSTHREAD = 1000;
                    Thread.sleep(TIMESLEEPOPERATORSTHREAD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cycleOperator = false;
        }
    }

    public static void main(String[] args) {
        new CallCenterTask();
    }
}


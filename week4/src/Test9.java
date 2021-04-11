import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.LongAdder;

public class Test9 {


  static class WorkThread implements Runnable {

    volatile String result;
    Thread thread;

    public String getResult() {
      return result;
    }

    public WorkThread(Thread condition) {
      this.thread = condition;
    }

    @Override
    public void run() {
      try {
        System.out.println("the child thread");
        Thread.sleep(5000);
        result = "sss";
        thread.notifyAll();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {

//    LongAdder adder = new LongAdder();
//    for (int i = 0;i < 100;i++) {
//      Thread t = new Thread(new Runnable() {
//        @Override
//        public void run() {
//          adder.increment();
//        }
//      });
//      t.start();
//    }
    Callable<String> callable = new Callable<String>() {
      @Override
      public String call() throws Exception {
        System.out.println("the child thread");
        Thread.sleep(5000);
        return "sss";
      }
    };

    Thread thread = new Thread(callable);
    thread.start();

//    Thread mainThread = Thread.currentThread();
//    WorkThread workThread = new WorkThread(mainThread);
//    Thread thread = new Thread(workThread);
//    try {
//        thread.start();
//        mainThread.wait();
//       System.out.println(workThread.getResult());
//    } catch (Exception e) {
//
//    } finally {
//
//    }
  }
}

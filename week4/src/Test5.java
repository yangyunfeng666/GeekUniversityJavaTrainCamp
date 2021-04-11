import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Test5 {

  /**
   * 使用阻塞线程队列，主线程从队列中读取值，当没有值的时候，阻塞，子线程往线程里面写值，待子线程写值成功，主线程就能读取到值
   * @param args
   */
  public static void main(String[] args) {
    BlockingQueue<String> blockingQueue = new LinkedBlockingDeque(1);
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println("the child thread");
          Thread.sleep(5000);
          blockingQueue.put("sss");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
    try {
      String result = blockingQueue.take();
      System.out.println(result);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

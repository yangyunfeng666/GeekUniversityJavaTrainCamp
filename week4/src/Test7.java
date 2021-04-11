import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Test7 {


  /**
   * 使用FutureTask 单线程
   * @param args
   */
  public static void main(String[] args) {
    FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
      @Override
      public String call() throws Exception {
        System.out.println("the child thread");
        Thread.sleep(5000);
        return "dsds";
      }
    });
    Thread thread = new Thread(task);
    thread.start();
    try {
      System.out.println(task.get());
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

  }

}

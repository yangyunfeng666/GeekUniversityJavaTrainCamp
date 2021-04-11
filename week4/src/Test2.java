import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Test2 {


  /**
   * 使用Future 在线程池中执行，异步调用结果
   * @param args
   */
  public static void main(String[] args) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    Future<String> future = executorService.submit(new Callable<String>() {
     @Override
     public String call() throws Exception {
       System.out.println("the child thread");
       Thread.sleep(5000);
       return "result";
     }
   });
    try {
      String result =  future.get();
      System.out.println(result);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }
}

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Test8 {

  /**
   * 使用FutureTask 和 线程池
   * @param args
   */
  public static void main(String[] args) {
    FutureTask<String> task = new FutureTask<>(new Callable<String>() {
      @Override
      public String call() throws Exception {
        System.out.println("the child thread");
        Thread.sleep(5000);
        return "dsds";
      }
    });
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(task);
    try {
      executorService.shutdown();
      System.out.println(task.get());
      CompletableFuture.supplyAsync(() -> "Hello, java course.")
          .thenApply(String::toUpperCase).thenCompose(s -> CompletableFuture.supplyAsync(s::toLowerCase))
          .thenAccept(v -> { System.out.println("thenCompose:"+v);});
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }
}

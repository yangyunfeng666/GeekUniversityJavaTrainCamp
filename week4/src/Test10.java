import java.util.concurrent.CompletableFuture;

public class Test10 {

  /**
   * 使用ComplatableFuture 异步执行
   * @param args
   */
  public static void main(String[] args) {
   String result =  CompletableFuture.supplyAsync(() -> {
     System.out.println("the child thread");
     try {
       Thread.sleep(5000);
     } catch (InterruptedException e) {
       e.printStackTrace();
     }
     return "dsds";
    }).join();

   System.out.println(result);
  }
}

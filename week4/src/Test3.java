import java.util.concurrent.Semaphore;

public class Test3 {

  static class WorkThread implements Runnable {

    volatile String result;

    public String getResult() {
      return result;
    }
    Semaphore a;

    public WorkThread(Semaphore a) {
      this.a = a;
    }

    @Override
    public void run() {
      try {
        System.out.println("the child thread");
        Thread.sleep(5000);
        result = "sss";
        a.release();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  /**
   * 使用信号量，在资源为0，当主线程执行的时候，申请资源，没有资源等待，待子线程给值赋值的时候才释放信号量，然后主线程唤醒，读取可见性值
   * @param args
   */
  public static void main(String[] args) {
    Semaphore semaphore = new Semaphore(0);
    try {
      WorkThread workThread = new WorkThread(semaphore);
      Thread thread = new Thread(workThread);
      thread.start();
      semaphore.acquire();
      System.out.println(workThread.getResult());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

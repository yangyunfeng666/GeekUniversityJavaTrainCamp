import java.util.concurrent.CountDownLatch;

public class Test4 {


  static class WorkThread implements Runnable {

    volatile String result;
    CountDownLatch thread;

    public String getResult() {
      return result;
    }

    public WorkThread(CountDownLatch condition) {
      this.thread = condition;
    }

    @Override
    public void run() {
      try {
        System.out.println("the child thread");
        Thread.sleep(5000);
        result = "sss";
        thread.countDown();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 使用CountDownLatch ,先让主线程暂停，让子线程执行赋值，让volatile保证值的可见性，然后子线程发送countDown到0，然后就是主线程进行执行，读取值
   * @param args
   */
  public static void main(String[] args) {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    try {
     WorkThread workThread = new WorkThread(countDownLatch);
      Thread thread = new Thread(workThread);
      thread.start();
      countDownLatch.await();
      System.out.println(workThread.getResult());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

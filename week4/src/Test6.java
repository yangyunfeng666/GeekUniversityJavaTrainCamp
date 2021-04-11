import java.util.concurrent.locks.LockSupport;

public class Test6 {

  /**
   * 使用LockSupport的，先主线程暂停，让子线程执行赋值，使用volatile 修改的值有可见性，然后唤醒主线程
   */
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
        LockSupport.unpark(thread);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    Thread mianThread = Thread.currentThread();
    WorkThread workThread = new WorkThread(mianThread);
    Thread thread = new Thread(workThread);
    thread.start();
    try {
      LockSupport.park(mianThread);
      System.out.println(workThread.getResult());
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}

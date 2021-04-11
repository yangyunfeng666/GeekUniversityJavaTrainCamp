import java.util.concurrent.locks.LockSupport;

class WorkThread implements Runnable {

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

public class Test1 {


  static class WorkThread implements Runnable {

    volatile String result;

    public String getResult() {
      return result;
    }

    public WorkThread() {
    }

    @Override
    public void run() {
      try {
        System.out.println("the child thread");
        Thread.sleep(5000);
        result = "sss";
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 这里只是使用了子线程执行，然后主线程，让子线程join,先让子线程执行完，进行值的赋值操作，然后使用volatile保证值的可见性。
   * @param args
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InterruptedException {
    WorkThread workThread = new WorkThread();
    Thread thread = new Thread(workThread);
    thread.start();
    thread.join();
    System.out.println(workThread.result);
  }

}

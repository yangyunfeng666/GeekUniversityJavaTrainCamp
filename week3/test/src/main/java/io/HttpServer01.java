package io;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer01 {

  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(8080);
      while (true) {
        Socket socket = serverSocket.accept();
        service(socket);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void service(Socket socket) throws IOException {
    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
    printWriter.println("HTTP/1.1 200 OK");
    printWriter.println("Content-Type:text/html;charset:utf-8");
    String body = "hello nio1\n";
    printWriter.println("Content-Length:"+body.getBytes().length);
    printWriter.println();
    printWriter.write(body);
    printWriter.close();
    socket.close();
  }

}

package network;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Completed by Quan Nguyen, Dzung Dinh
 */
public final class Server {
  public static void main(String argv[]) throws Exception {
    int port = 6789;
    ServerSocket socket = new ServerSocket(port);

    while (true) {
      Socket connection = socket.accept();
      // Construct an object to process the HTTP request message.
      Request request = new Request(connection);

      // Create a new thread to process the request.
      Thread thread = new Thread(request);

      // Start the thread.
      thread.start();
    }
  }
}
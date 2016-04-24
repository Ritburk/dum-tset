package net.cmacpherson.mud;

public class MUD {
  
  private static final int PORT = 3333;
  
  public static void main(String[] args) {
    new ServerThread(PORT).start();
  }
}

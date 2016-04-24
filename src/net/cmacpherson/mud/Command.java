package net.cmacpherson.mud;

import java.io.IOException;

public interface Command {
  public boolean execute(Character c, String cmd, String line, Output o);
  public String getHelp();

  public static Integer readInt(ClientThread client, String prompt) {
    Integer i = null;
    try {
      client.o2(prompt, false);
      client.flush();
      String s = client.getFromQueue();
      while (i == null) {
        try {
          i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
          client.o2("|r|Error reading int.", true);
          client.o2("Try again: ", false);
          client.flush();
          s = client.getFromQueue();
          if (s == null) return null;
        }
      }
    } catch (IOException e) {
      return null;
    }
    return i;
  }
  
  public static Long readLong(ClientThread client, String prompt) {
    Long l = null;
    try {
      client.o2(prompt, false);
      client.flush();
      String s = client.getFromQueue();
      while (l == null) {
        try {
          l = Long.parseLong(s);
        } catch (NumberFormatException e) {
          client.o2("|r|Error reading long.", true);
          client.o2("Try again: ", false);
          client.flush();
          s = client.getFromQueue();
          if (s == null) return null;
        }
      }
    } catch (IOException e) {
      return null;
    }
    return l;
  }
  
  public static Double readDouble(ClientThread client, String prompt) {
    Double d = null;
    try {
      client.o2(prompt, false);
      client.flush();
      String s = client.getFromQueue();
      while (d == null) {
        try {
          d = Double.parseDouble(s);
        } catch (NumberFormatException e) {
          client.o2("|r|Error reading double.", true);
          client.o2("Try again: ", false);
          client.flush();
          s = client.getFromQueue();
          if (s == null) return null;
        }
      }
    } catch (IOException e) {
      return null;
    }
    return d;
  } 
  
  public static String readString(ClientThread client, String prompt) {
    String s = null;
    try {
      client.o2(prompt, false);
      client.flush();
      s = client.getFromQueue();
    } catch (IOException e) {
      return null;
    }
    return s.trim();
  }
  
  public static Dice readDice(ClientThread client, String prompt) {
    Dice d = null;
    client.o2(prompt,  false);
    while (d == null) {
      try {
        client.flush();
        String s = client.getFromQueue();
        if (s == null) return null;
        d = new Dice(s);
      } catch (IOException e) {
        return null;
      } catch (InvalidDiceFormatException e) {
        System.out.println(e.getMessage());
        client.o2("|r|Error processing dice. (ex. 1d8+1)", true);
        client.o2("Try again: ", false);
      }
    }
    return d;
  }
}

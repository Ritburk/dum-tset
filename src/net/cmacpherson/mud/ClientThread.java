package net.cmacpherson.mud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.transform.TransformerException;

public class ClientThread extends Thread {
    
  public boolean connected;
  public boolean linkdead;
  public boolean logout;
  private ServerThread server;
  public Socket socket;
  public PC player;
  private BufferedReader in;
  private PrintWriter out;
  public Output output;
  private InputThread inputThread;
  public LinkedList<String> queue = new LinkedList<String>();
  
  public ClientThread(ServerThread server,
                      Socket socket) {
    connected = false;
    linkdead = true;
    this.server = server;
    this.socket = socket;
    output = new Output(server);
    inputThread = new InputThread();
  }
  
  public class InputThread extends Thread {
    private String prevInput = "";
    public IOException e = null;
    @Override
    public void run() {
      try {
        while (true) {
          String s = readInput();
          if (s == null)
            throw new IOException();
          if (s.equals("!"))
            queue.add(prevInput);
          else {
            prevInput = s;
            queue.add(s);
          }
          synchronized(this) {
            this.notify();
          }
        }
      } catch (IOException e) {
        this.e = e;
        synchronized(this) {
          this.notify();
        }
      }
    }
  }
  
  public void notifyQueue() {
    synchronized (inputThread) {
      inputThread.notify();
    }
  }
  
  public String getFromQueue() throws IOException {
    while (queue.isEmpty()) {
      try {
        synchronized (inputThread) {
          inputThread.wait(100);
        }
      } catch (InterruptedException e) {}
      if (!inputThread.isAlive())
        break;
    }
    if (inputThread.e != null ||
        queue.size() == 0)
      throw inputThread.e;
    return queue.pop();
  }
  
  @Override
  public void run() {
    try {
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream());
      if ((player = login()) != null) {
        server.connect(this);
        connected = true;
        linkdead = false;
        if (player.linkdead)
          player.linkdead = false;
        else if (player.level == 0) {
          //TODO newbie info
          o("|p|TODO: Put newbie info here. :)", true);
          flush();
          try {
            XML.INSTANCE.build(player);
          } catch (TransformerException e) {
            server.print("Unable to save PC: " + player.getFilePath());
          }
        } else if (player.status == Character.Status.SLEEPING)
          queue.add("");
        else
          queue.add("look");
        inputThread.start();
        while (connected)
          player.doCommand(getFromQueue());
      }
      socket.close();
    } catch (IOException e) {
      connected = false;
      linkdead = true;
    }
    if (logout)
      return;
    if (linkdead && player != null)
      server.linkdead(player);
    server.disconnect(this);
  }
  
  public void logout() {
    logout = true;
    server.logout(player);
  }
  
  private PC login() throws IOException {
    showLoginScreen();
    boolean loggedIn = false;
    PC player = null;
    o("|w|What is your name: ", false);
    while (!loggedIn) {
      flush();
      String name = readInput();
      if (name == null) return null;
      if (name.equals("")) {
        o("|w|No name entered. Goodbye.", false);
        return null;
      }
      if (!meetsNameRequirements(name)) {
        outputNameRequirements();
        o("", true);
        o("|w|What is your name: ", false);
        continue;
      }
      if ((player = server.retrievePlayer(name)) != null) {
        o("|w|Existing Character.", true);
        o("|w|Password: ", false);
        flush();
        String password = readPassword();
        if (password == null) return null;
        if (!MD5.encode(password).equals(player.password)) {
          o("|br|Password Incorrect.", true);
          flush();
          return null;
        }
        loggedIn = true;
      } else {
        if (!server.registerName(name)) {
          o("|r|Name is locked.", true);
          o("", true);
          o("|w|What is your name: ", false);
          continue;
        }
        o("|w|Are you new here? (Y/N) ", false);
        flush();
        String answer = readInput();
        if (answer == null) return null;
        while (!answer.regionMatches(true, 0, "YES", 0, answer.length()) &&
               !answer.regionMatches(true, 0, "NO", 0, answer.length())) {
          o("|w|Sorry, what was that? (Y/N) ", false);
          flush();
          answer = readInput();
          if (answer == null) return null;
        }
        if (answer.regionMatches(true, 0, "NO", 0, answer.length())) {
          o("|w|Then what was your name? ", false);
          server.unregisterName(name);
          player = null;
          continue;
        }
        String password = "password";
        String ensurePassword = "!password";
        while (!password.equals(ensurePassword)) {
          o("", true);
          o("|w|Please enter a password: ", false);
          flush();
          password = readPassword();
          if (password == null) return null;
          if (!meetsPasswordRequirements(password)) {
            outputPasswordRequirements();
            continue;
          }
          o("|w|Please re-enter your password: ", false);
          flush();
          ensurePassword = readPassword();
          if (ensurePassword == null) return null;
          if (!password.equals(ensurePassword))
            o("|r|Passwords did not match. Please try again.", true);
        }
        password = MD5.encode(password);
        ensurePassword = null;
        Character.Sex sex = null;
        o("", true);
        while (sex == null) {
          o("Please select your gender (M/F/N): ", false);
          flush();
          String line = readInput().trim();
          if (line.equals(""))
            continue;
          else if (line.regionMatches(true, 0, "male", 0, line.length()))
            sex = Character.Sex.MALE;
          else if (line.regionMatches(true, 0, "female", 0, line.length()))
            sex = Character.Sex.FEMALE;
          else if (line.regionMatches(true, 0, "neutral", 0, line.length()))
            sex = Character.Sex.NEUTRAL;
          else
            o("|r|Invalid selection. Please try again.", true);
        }
        Race.Abr race = null;
        String[] choices = new String[Race.Name.values().length];
        int maxWidth = -1;
        for (int i = 0; i < choices.length; i++) {
          choices[i] = "(" + Race.Abr.values()[i].name() + ") " + Race.Name.values()[i].name().substring(0, 1) + Race.Name.values()[i].name().substring(1).toLowerCase();
          if (choices[i].length() > maxWidth)
            maxWidth = choices[i].length();
        }
        o("", true);
        o(Utils.createColumns(maxWidth, choices), true);
        o("", true);
        while (race == null) {
          o("Please select your race: ", false);
          flush();
          String line = readInput().trim();
          if (line.equals("")) {
            o("", true);
            o(Utils.createColumns(maxWidth, choices), true);
            o("", true);
            continue;
          }
          if ("help ".regionMatches(true, 0, line, 0, "help ".length())) {
            //display help for race
            continue;
          }
          for (int i = 0; i < Race.Abr.values().length; i++)
            if (line.equalsIgnoreCase(Race.Abr.values()[i].name()) ||
                line.equalsIgnoreCase(Race.Name.values()[i].name()))
              race = Race.Abr.values()[i];
          if (race == null)
            o("|r|Invalid selection. Please try again.", true);
        }
        player = new PC(server);
        player.client = this;
        player.linkdead = false;
        player.name(name.substring(0, 1).toUpperCase() + name.substring(1));
        player.title = ": a player.";
        player.displayName = player.name + player.title;
        player.password = password;
        player.prompt = Globals.STARTING_PROMPT;
        player.title = ": a player.";
        player.location = server.rooms.get(Globals.STARTING_LOCATION_ID).location;
        player.sex = sex;
        player.race = new Race(race);
        player.skills = new Skills();
        player.prof = new Proficiencies();
        player.status = Character.Status.SLEEPING;
        player.gold = Globals.STARTING_GOLD;
        player.level = 0;
        int[] stats = Race.STATS[player.race.abr.ordinal()];
        player._str = stats[0];
        player.cSTR = player._str;
        player._dex = stats[1];
        player.cDEX = player._dex;
        player._con = stats[2];
        player.cCON = player._con;
        player._int = stats[3];
        player.cINT = player._int;
        player._wis = stats[4];
        player.cWIS = player._wis;
        player._cha = stats[5];
        player.cCHA = player._cha;
        player.mhp = Globals.STARTING_HP;
        player.hp = player.mhp;
        player.mm = Globals.STARTING_MANA;
        player.m = player.mm;
        player.mmv = Globals.STARTING_MOVES;
        player.mv = player.mmv;
        player.hr = Utils.baseHR(player.cSTR);
        player.dr = Utils.baseDR(player.cDEX);
        player.shr = Utils.baseSHR(player.cINT);
        player.sdr = Utils.baseSDR(player.cINT);
        player.fort = Utils.baseFort(player.cCON);
        player.ref = Utils.baseFort(player.cDEX);
        player.will = Utils.baseWill(player.cWIS);
        player.align = Globals.STARTING_ALIGNMENT;
        player.ac = Utils.baseAC(player.cDEX);
        //starting equipment
        
        loggedIn = true;
      }
      if (server.loggedPCs.contains(player)) {
        if (player.linkdead) {
          return server.link(player, this);
        } else {
          o("|r|Character already playing.", true);
          o("|w|Reconnect? (Y/N)", false);
          flush();
          String ans = readInput();
          if (ans.regionMatches(true, 0, "YES", 0, ans.length())) {
            server.linkdead(player);
            server.disconnect(player.client);
            return server.link(player, this);
          }
        }
      }
      if (!showDailyMessage()) {return null;}
    }
    player.client = this;
    return server.login(player);
  }
  
  private void showLoginScreen() {
    o("", true);
    o("|bk|------------------------------ |bg|  mMUD   |bk|------------------------------", true);
    o("", true);
  }
  private boolean meetsNameRequirements(String name) {
    return name.length() >= 3 &&
           name.length() <= 15 &&
           Utils.justLetters(name);
  }
  private void outputNameRequirements() {
    o("|r|Name must be between 3 and 15 alphabetic characters.", true);
  }
  private boolean meetsPasswordRequirements(String pw) {
    return pw.length() >= 5 &&
           pw.length() <= 24 &&
           Utils.justAlphaNumeric(pw);
  }
  private void outputPasswordRequirements() {
    o("|r|Password must be only alpha-numeric characters and", true);
    o("|r|between 5 and 24 characters in length.", true);
  }
  private boolean showDailyMessage() throws IOException {
    o("", true);
    o("|bk|------------------------------ |bg|Welcome! |bk|------------------------------", true);
    o("", true);
    o("|bk|                      Press enter to continue...", true);
    flush();
    return readInput() != null;
  }
  
  public boolean blockOutput = false;
  private final ArrayList<OutputData> missedOutput = new ArrayList<OutputData>();
  private final ArrayList<OutputData> ol = new ArrayList<OutputData>();
  public void o2(String line, boolean newline) {output(new OutputData(line, newline), true);}
  public void o(String line, boolean newline) {output(new OutputData(line, newline), false);}
  public synchronized void output(OutputData o, boolean bypass) {
    if (blockOutput && !bypass)
      missedOutput.add(o);
    else
      ol.add(o);
  }
  public synchronized void flush() {
    for (int i = 0; i < ol.size(); i++) {
      out.print(parseColor(ol.get(i).LINE));
      if (ol.get(i).NEWLINE)
        out.println();
    }
    ol.clear();
    out.flush();
  }
  public synchronized void unblockOutput(boolean show) {
    blockOutput = false;
    if (show)
      ol.addAll(missedOutput);
    missedOutput.clear();
    player.prompt();
  }
  
  private final int BACKSPACE = 8;
  private final int LINE_FEED = 10;
  private final int CARRIAGE_RETURN = 13;
  private final int ESC = 27;
  private String readInput() throws IOException {
    boolean stop = false;
    String line = "";
    while (!stop) {
      int next = in.read();
      if (next == -1) return null;
      if (next == LINE_FEED || next == CARRIAGE_RETURN) {
        in.read();
        stop = true;
      } else {
        if (next == BACKSPACE) {
          try {
            line = line.substring(0, line.length() - 1);
          } catch (StringIndexOutOfBoundsException e) {
            line = "";
          }
        } else if (next == ESC) {
          in.read();
          in.read();
        }
        if (next < 32)
          continue;
        line += (char)next;
      }
    }
    return line.trim();
  }
  
  public String parseColor(String line) {
    for (int i = 0; i <= Color.values().length; i++) {
      int index = line.indexOf(Globals.COLOR_CODES[i]);
      while (index != -1) {
        line = line.substring(0, index) + Globals.ANSI_CODES[i % 16] +
               line.substring(index + Globals.COLOR_CODES[i].length());
        index = line.indexOf(Globals.COLOR_CODES[i]);
      }
    }
    return Globals.ANSI_CODES[Globals.COLOR] +
           line +
           Globals.ANSI_CODES[Globals.INPUT_COLOR];
  }
  
  public String readPassword() throws IOException {
    out.print(Globals.ANSI_CODES[Color.BLACK.ordinal()]);
    out.flush();
    return readInput();
  }
}

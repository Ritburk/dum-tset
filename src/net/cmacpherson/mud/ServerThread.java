package net.cmacpherson.mud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.transform.TransformerException;

public class ServerThread extends Thread {

  private static final int TIME_OUT_INTERVAL = 100;
  private final int PORT;
  
  private boolean reboot;
  private boolean stopServer;
  
  private ServerSocket server;
  private ArrayList<Thread> threads;
  private ArrayList<ClientThread> clients;
  private Output output;
  public ArrayList<String> registeredNames;
  public ArrayList<Mob> mobs;
  public ArrayList<PC> loggedPCs;
  private ArrayList<PC> savedPCs;
  public ArrayList<Plane> universe;
  public Hashtable<Long, Room> rooms;
  public Hashtable<Long, ItemProto> itemBank;
  public Hashtable<Long, MobProto> mobBank;
  public Generator generator;
  public TicThread tic;
  
  public ServerThread(int port) {
    PORT = port;
  }
  
  public void run() {
    do {
      print("-= Server Started =-");
      try {
        if (!init()) {
          print("Server failed to load resources.");
          print("-= Server Terminated =-");
          return;
        }
        stopServer = false;
        tic.start();
        try {
          server = new ServerSocket(PORT);
        } catch (IOException e) {
          print("Error Opening Port: " + PORT);
          print("-= Server Terminated =-");
          tic.shutdown = true;
          tic.interrupt();
          return;
        }
        server.setSoTimeout(TIME_OUT_INTERVAL);
        print("-= Port " + PORT + " Opened =-");
        try {
          do {
            try {
              Socket socket = server.accept();
              print("Socket Opened: " + socket.getRemoteSocketAddress());
              ClientThread client = new ClientThread(this, socket);
              threads.add(client);
              client.start();
            } catch (SocketTimeoutException e) {}
          } while (!stopServer);
        } catch (SocketException e) {}
        noFight(true);
        tic.shutdown = true;
        tic.interrupt();
        while (tic.isAlive())
          try {
            sleep(100);
          } catch (InterruptedException e) {}
        synchronized (loggedPCs) {
          for (PC pc : loggedPCs)
            pc.forceOnThread("quit", null);
        }
        print("Waiting for PCs to logout...");
        while (loggedPCs.size() != 0) 
          try {
            sleep(1000);
          } catch (InterruptedException e) {}
        _finalize();
      } catch (IOException e) {}
    } while (reboot);
  }
  
  public void noFight(boolean noFight) {
    if (noFight) {
      //interrupt all actions
      //make everyone > SLEEPING = STANDING
    }
    tic.noFight = noFight;
  }
  
  public boolean init() {
    print("Initializing variables...");
    output = new Output(this);
    reboot = false;
    threads = new ArrayList<Thread>();
    clients = new ArrayList<ClientThread>();
    if (XML.INSTANCE == null) {
      print("Error creating xml hook.");
      return false;
    }
    mobs = new ArrayList<Mob>();
    loggedPCs = new ArrayList<PC>();
    savedPCs = new ArrayList<PC>();
    registeredNames = new ArrayList<String>();
    universe = new ArrayList<Plane>();
    rooms = new Hashtable<Long, Room>();
    itemBank = new Hashtable<Long, ItemProto>();
    mobBank = new Hashtable<Long, MobProto>();
    tic = new TicThread(this);
    print("Loading configuration...");
    try {
      if (Globals.CONFIG_FILE.exists()) {
        long[] config = XML.INSTANCE.parseConfig(Globals.CONFIG_FILE);
        Globals.LAST_ROOM_ID = config[0];
        Globals.LAST_ITEM_ID = config[1];
        Globals.LAST_KEY_ID = config[2];
        Globals.LAST_PROTO_ITEM_ID = config[3];
        Globals.LAST_PROTO_MOB_ID = config[4];
      } else
        print("No configuration found.  Using default configuration...");
    } catch (Exception e) {
      e.printStackTrace();
      print("Error processing configutation file.");
      return false;
    }
    print("Loading generic items...");
    File[] files = new File(Globals.ITEM_PATH).listFiles();
    for (File file : files) {
      try {
        ItemProto proto = new ItemProto();
        proto.load(XML.INSTANCE.parse(file));
        itemBank.put(proto.vnum, proto);
      } catch (Exception e) {
        print("Error loading prototype item: " + file.getAbsolutePath());
        e.printStackTrace();
      }
    }
    print("Loading generic mobs...");
    files = new File(Globals.MOB_PATH).listFiles();
    for (File file : files) {
      try {
        MobProto proto = new MobProto();
        proto.load(XML.INSTANCE.parse(file));
        mobBank.put(proto.vnum, proto);
      } catch (Exception e) {
        print("Error loading prototype mob: " + file.getAbsolutePath());
        e.printStackTrace();
      }
    }
    print("Initializing generator...");
    generator = new Generator(this);
    if (generator.server == null)
      return false;
    print("Loading universe...");
    generator.recoverMap();
    if (universe.isEmpty()) {
//      print("No universe data found. Loading default plane...");
//      universe.add(Globals.generateDefaultPlane());
      print("No universe data found. Loading random plane...");
      
      //TODO load random plane
      //when making options for generator, use biome to describe algorithm
    }
    print("Building associations...");
    int areaCount = 0;
    int roomCount = 0;
    for (Plane plane : universe) 
      for (Area area : plane.areas) {
        areaCount++;
        for (Room room : area.rooms) {
          roomCount++;
          room.location = new Location(plane, area, room, room.id);
          rooms.put(room.id, room);
        }
      }
    for (Plane plane : universe)
      for (Area area : plane.areas)
        for (Room room : area.rooms)
          for (Exit exit : room.exits) {
            if (exit != null)
              exit.target = rooms.get(exit.targetID).location;
            //anything else in the room that needs to have associations built
            //gateways? items? mobs? etc...
          }
    print("Universe loaded:");
    print("Planes:  " + universe.size());
    print("Areas:   " + areaCount);
    print("Rooms:   " + roomCount);
    print("Loading PCs...");
    files = new File(Globals.PC_PATH).listFiles();
    for (File file : files) {
      try {
        PC pc = new PC(this);
        pc.load(XML.INSTANCE.parse(file));
        savedPCs.add(pc);
      } catch (Exception e) {
        print("Error loading PC from file: " + file.getAbsolutePath());
        e.printStackTrace();
      }
    }
    print("PCs:     " + savedPCs.size());
    print("Loading socials...");
    try {
      ArrayList<String[]> socials = new ArrayList<String[]>();
      BufferedReader in = new BufferedReader(new FileReader(Globals.SOCIALS_FILE));
      String line = null;
      while ((line = in.readLine()) != null) {
        String[] sep = new String[] {"", "", "", "", "", "", "", ""};
        String[] split = line.split("\\|");
        System.arraycopy(split, 0, sep, 0, split.length);
        socials.add(sep);
      }
      in.close();
      Globals.SOCIALS = socials.toArray(new String[][] {});
    } catch (FileNotFoundException e) {
      print("Socials not found: " + Globals.SOCIALS_FILE.getAbsolutePath());
      return false;
    } catch (IOException e) {
      print("Error reading file: " + Globals.SOCIALS_FILE.getAbsolutePath());
      return false;
    }
    print("Socials: " + Globals.SOCIALS.length);
    return true;
  }
  
  public void shutdown() {stopServer = true;}
  public void reboot() {stopServer = reboot = true;}
  
  private void _finalize() {
    try {
      server.close();
      print("-= Port " + PORT + " Closed =-");
    } catch (IOException e) {
      print("Error closing ServerSocket:");
      e.printStackTrace();
    }
    print("-= Data Backup Started =-");
    print("Saving PCs...");
    for (PC pc : savedPCs)
      try {
        XML.INSTANCE.build(pc);
      } catch (TransformerException e) {
        print("Error saving PC: " + pc.getFilePath());
        e.printStackTrace();
      }
    print("Saving universe...");
    for (Plane plane : universe)
      try {
        XML.INSTANCE.build(plane);
      } catch (TransformerException e) {
        print("Error saving Plane: " + plane.getFilePath());
        e.printStackTrace();
      }
    print("Saving prototype items...");
    for (long l : itemBank.keySet())
      try {
        XML.INSTANCE.build(itemBank.get(l));
      } catch (TransformerException e) {
        print("Error saving prototype item: " + itemBank.get(l).getFilePath());
        e.printStackTrace();
      }
    print("Saving prototype mobs...");
    for (long l : mobBank.keySet())
      try {
        XML.INSTANCE.build(mobBank.get(l));
      } catch (TransformerException e) {
        print("Error saving prototype mob: " + mobBank.get(l).getFilePath());
        e.printStackTrace();
      }
    print("Saving configuration...");
    try {
      long[] config = new long[] {
        Globals.LAST_ROOM_ID,
        Globals.LAST_ITEM_ID,
        Globals.LAST_KEY_ID,
        Globals.LAST_PROTO_ITEM_ID,
        Globals.LAST_PROTO_MOB_ID
      };
      XML.INSTANCE.build(config);
    } catch (TransformerException e) {
      print("Error saving configuration. (Uh-oh)");
      print("LAST_ROOM_ID:       " + Globals.LAST_ROOM_ID);
      print("LAST_ITEM_ID:       " + Globals.LAST_ITEM_ID);
      print("LAST_KEY_ID:        " + Globals.LAST_KEY_ID);
      print("LAST_PROTO_ITEM_ID: " + Globals.LAST_PROTO_ITEM_ID);
      print("LAST_PROTO_MOB_ID:  " + Globals.LAST_PROTO_MOB_ID);
      e.printStackTrace();
    }
    print("-= Data Backup Complete =-");
    print("-= Server Terminated =-");
  }
  
  public void print(String str) {
    System.out.println(str);
  }

  public synchronized PC retrievePlayer(String name) {
    for (int i = 0; i < loggedPCs.size(); i++) {
      if (loggedPCs.get(i).name.equalsIgnoreCase(name))
        return loggedPCs.get(i);
    }
    for (int i = 0; i < savedPCs.size(); i++) {
      if (savedPCs.get(i).name.equalsIgnoreCase(name))
        return savedPCs.get(i);
    }
    return null;
  }
  
  public PC login(PC player) {
    if (player.location.room == null) {
      output.toChar("Whoops, seems like the room you were in isn't there anymore.", player);
      output.toChar("Let's teleport you to a safe room...", player);
      player.location = rooms.get(Globals.STARTING_LOCATION_ID).location;
    }
    output.toRoom(player.location.room, "%N|y|With a flash of brilliant light, $n has just entered the room" +
                                        "%N|y|  through a tear in reality.", null, player);
    output.flush();
    player.location.room.chars.add(player);
    if (loggedPCs.contains(player))
      print("E[ServerThread.login]: loggedPCs already contains player[" + player.name + "]");
    else
      loggedPCs.add(player);
    print("Login: " + player.name);
    return player;
  }

  public void connect(ClientThread client) {
    threads.remove(client);
    clients.add(client);
    print("Client Connected: " + client.player.name + " {" + client.socket.getRemoteSocketAddress() + "}");
  }
  
  public void logout(PC player) {
    // TODO search variables to remove connections to PC (i.e. nullify people following this PC)
    output.toChar("Good bye.", player);
    output.flush();
    if (!loggedPCs.remove(player))
      print("E[ServerThread.logout]: loggedPCs does not contain player[" + player.name + "]");
    else
      print("Logout: " + player.name);
    disconnect(player.client);
  }
  
  public void disconnect(ClientThread client) {
    threads.remove(client);
    if (clients.remove(client))
      print("Client Disconnect: " + client.socket.getRemoteSocketAddress());
    else
      print("Connection Dropped: " + client.socket.getRemoteSocketAddress());
    try {
      client.socket.close();
      print("Socket Closed: " + client.socket.getRemoteSocketAddress());
    } catch (IOException e) {}
  }
  
  public void linkdead(PC player) {
    player.linkdead = true;
    print("Linkdead Player: " + player.name);
    output.toRoom(player.location.room, "%N$n has lost their link.", null, player);
    output.flush();
  }
  
  public PC link(PC player, 
                 ClientThread client) {
    output.toRoom(player.location.room, "%N$n has reconnected.", player);
    player.client = client;
    client.player = player;
    output.toChar("Reconnecting...", player);
    output.flush();
    print("Linked: " + player.name);
    return player;
  }
  
  public synchronized boolean registerName(String name) {
    if (registeredNames.contains(name))
      return false;
    registeredNames.add(name);
    return true;
  }
  
  public synchronized void unregisterName(String name) {
    registeredNames.remove(name);
  }
}

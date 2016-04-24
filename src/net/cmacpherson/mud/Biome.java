package net.cmacpherson.mud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Biome {

  public String name;
  public int version;
  
  //version 1
  public String[] roomNames;
  public String[][] roomDescriptions;
  public long[] itemVNUMs;
  public long[] mobVNUMs;
  public double[] diminishingReturnsForExitChances;
  public double[] directionalChances;

  /*
   * Biome Format (version 1):
   * 
   * Each line has a descriptor followed by a ':' and finally the data required
   * 
   * Descriptors:
   * V = version = int (absolutely must have and must be first line)
   * N = room name = String (must have at least 1, can be entered multiple times)
   * D = room description = String (must have at least 1, can be entered multiple times, '|' denotes line separator)
   * M = mob proto vnum = long (can be entered multiple times)
   * I = item proto vnum = long (can be entered multiple times)
   * R = diminishing returns for exit chances = list of 6 doubles
   * C = directional chances for exits = list of 6 doubles (must add to 1, '|' denotes separator)
   * 
   */
  public static Biome load(ServerThread server, File file) throws IOException {
    server.print("Loading biome frome file: " + file.getName());
    Biome biome = new Biome();
    biome.name = file.getName().split("\\.")[0].toUpperCase().replaceAll(" ", "_");
    BufferedReader in = new BufferedReader(new FileReader(file));
    String line = in.readLine();
    boolean error = false;
    if (line == null)
      error |= error("", "File Error", "File has no data.", server);
    else {
      String[] args = line.split(":");
      if (args.length < 2)
        error |= error(line, "Invalid Syntax", "Wrong number of arguments.", server);
      else {
        if (args[0].charAt(0) != 'V' ||
            args[0].charAt(0) != 'v')
          error |= error(line, "Descriptor Error", "First line must describe version.", server);
        else if (args.length != 2)
          error |= error(line, "Invalid Argument Length", args[0], server);
        else {
          try {
            biome.version = Integer.parseInt(args[1]);
          } catch (NumberFormatException e) {
            error |= error(line, "Value Error", "Expected integer.", server);
          }
        }
      }
    }
    switch (biome.version) {
    case 1:
      error |= version1(biome, file.getName(), in, server);
      break;
    }
    in.close();
    if (error) {
      server.print("Errors encountered while loading biome.");
      return null;
    }
    server.print("Biome loaded successfully.");
    return biome;
  }
  
  private static boolean error(String line,
                               String desc,
                               String arg,
                               ServerThread server) {
    server.print("Error on line: " + line + "\n" +
                 "  " + desc + ": " + arg);
    return true;
  }
  
  private static boolean version1(Biome biome,
                                  String filename,
                                  BufferedReader in, 
                                  ServerThread server) throws IOException {
    boolean error = false;
    String line = null;
    while ((line = in.readLine()) != null) {
      String[] args = line.split(":");
      if (args.length < 2)
        error |= error(line, "Invalid Syntax", "Wrong number of arguments.", server);
      else {
        switch (args[0].charAt(0)) {
        case 'V':
        case 'v':
          //already taken care of
          break;
        case 'N':
        case 'n':
          String[] names = new String[biome.roomNames.length + 1];
          System.arraycopy(biome.roomNames, 0, names, 0, biome.roomNames.length);
          biome.roomNames = names;
          break;
        case 'D':
        case 'd':
          String[][] desc = new String[biome.roomDescriptions.length + 1][];
          for (int i = 0; i < biome.roomDescriptions.length; i++)
            System.arraycopy(biome.roomDescriptions[i], 0, desc[i], 0, biome.roomDescriptions[i].length);
          desc[desc.length - 1] = args[1].split("|");
          break;
        case 'M':
        case 'm':
          long[] mobs = new long[biome.mobVNUMs.length + 1];
          System.arraycopy(biome.mobVNUMs, 0, mobs, 0, biome.mobVNUMs.length);
          try {
            mobs[mobs.length - 1] = Long.parseLong(args[1]);
            biome.mobVNUMs = mobs;
          } catch (NumberFormatException e) {
            error |= error(line, "Value Error", "Expected long.", server);
          }
          break;
        case 'I':
        case 'i':
          long[] items = new long[biome.itemVNUMs.length + 1];
          System.arraycopy(biome.itemVNUMs, 0, items, 0, biome.itemVNUMs.length);
          try {
            items[items.length - 1] = Long.parseLong(args[1]);
            biome.itemVNUMs = items;
          } catch (NumberFormatException e) {
            error |= error(line, "Value Error", "Expected long.", server);
          }
          break;
        case 'R':
        case 'r':
          String[] list = args[1].split("|");
          if (list.length != 6)
            error |= error(line, "Value Error", "Expected 6 doubles.", server);
          else {
            biome.diminishingReturnsForExitChances = new double[6];
            try {
              for (int i = 0; i < 6; i++)
                biome.diminishingReturnsForExitChances[i] = Double.parseDouble(list[i].trim());
            } catch (NumberFormatException e) {
              biome.diminishingReturnsForExitChances = null;
              error |= error(line, "Value Error", "Expected 6 doubles.", server);
            }
          }
          break;
        case 'C':
        case 'c':
          list = args[1].split("|");
          if (list.length != 6)
            error |= error(line, "Value Error", "Expected 6 doubles.", server);
          else {
            biome.directionalChances = new double[6];
            try {
              for (int i = 0; i < 6; i++)
                biome.directionalChances[i] = Double.parseDouble(list[i].trim());
              double total = 0;
              for (double d : biome.directionalChances)
                total += d;
              if (total != 1.0) {
                error |= error(line, "Value Error", "Values must add up to 1.", server);
                biome.directionalChances = null;
              }
            } catch (NumberFormatException e) {
              biome.directionalChances = null;
              error |= error(line, "Value Error", "Expected 6 doubles.", server);
            }
          }
          break;
        default:
          error |= error(line, "Unknown Descriptor", args[0], server);
        }
      }
    }
    if (!error) {
      if (biome.diminishingReturnsForExitChances == null)
        biome.diminishingReturnsForExitChances = new double[] {0.9, 0.7, 0.5, 0.3, 0.1, 0.1};
      if (biome.directionalChances == null)
        biome.directionalChances = new double[] {0.2, 0.2, 0.2, 0.2, 0.1, 0.1};
    }
    return error;
  }
}

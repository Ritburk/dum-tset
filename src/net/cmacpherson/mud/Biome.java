package net.cmacpherson.mud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Biome {
  
  /*   VERSION 2
   * 
   * need to have this be more structual
   * we are thinking that information needs to be more organized
   * version 1 takes into account entry order, but may be too confusing
   * should use xml or a similar structure
   * 
   * rooms need to have a description of what type of space they need
   *   -this could be like it's a spread area (i.e. 2x2 or >)
   *   -weather it requires a vertical component like tree or flying in the air
   *   -needs to be underground
   *   (these determine that the generator needs to keep track of what the
   *    ground level is across the area (if it's mountainous it goes up but
   *    is still considered ground level)
   * also need room titles/description that correspond to each other
   * also if certain rooms require specific items located in it
   * maybe an "area" room description has several interlocking different
   *  rooms that could relate (i.e. a house with several rooms)
   * 
   * have biome hold a map containing vars and values that are called based on
   * specific algorithms when generating areas
   * 
   */
  
  public HashMap<String, Object> vars = new HashMap<String, Object>();
  
  @SuppressWarnings("unchecked")
  public boolean load(ServerThread server, File file) throws IOException {
    server.print("Loading biome from file: " + file.getName());
    BufferedReader in = new BufferedReader(new FileReader(file));
    LinkedList<String> lines = new LinkedList<String>();
    String line = null;
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.charAt(0) != '#' &&
          line.equals(""))
        lines.add(line);
    }
    in.close();
    while (!lines.isEmpty()) {
      line = lines.removeFirst();
      if (line.equalsIgnoreCase("ROOM")) {
        RoomInfo room = parseRoom(lines);
        if (room == null)
          return false;
        ArrayList<RoomInfo> rooms = (ArrayList<RoomInfo>)vars.get("rooms");
        if (rooms == null) {
          rooms = new ArrayList<RoomInfo>();
          vars.put("rooms", rooms);
        }
        rooms.add(room);
      } else if (line.equalsIgnoreCase("REGION")) {
        RegionInfo region = parseRegion(lines);
        if (region == null)
          return false;
        ArrayList<RegionInfo> regions = (ArrayList<RegionInfo>)vars.get("regions");
        if (regions == null) {
          regions = new ArrayList<RegionInfo>();
          vars.put("regions", regions);
        }
        regions.add(region);
      } else if (line.equalsIgnoreCase("ITEM")) {
        ItemInfo item = parseItem(lines);
        if (item == null)
          return false;
        ArrayList<ItemInfo> items = (ArrayList<ItemInfo>)vars.get("items");
        if (items == null) {
          items = new ArrayList<ItemInfo>();
          vars.put("items", items);
        }
        items.add(item);
      } else if (line.equalsIgnoreCase("MOB")) {
        MobInfo mob = parseMob(lines);
        if (mob == null)
          return false;
        ArrayList<MobInfo> mobs = (ArrayList<MobInfo>)vars.get("mobs");
        if (mobs == null) {
          mobs = new ArrayList<MobInfo>();
          vars.put("mobs", mobs);
        }
        mobs.add(mob);
      } else {
        Object[] var = parseVar(line);
        if (var == null) {
          server.print("Unrecognized variable entry: " + line);
          return false;
        }
        vars.put((String)var[0], var[1]);
      }
    }
    return true;
  }
  
  private Object[] parseVar(String line) {
    Object[] var = line.split("=");
    if (var.length != 2)
      return null;
    var[0] = ((String)var[0]).trim();
    String s = ((String)var[1]).trim();
    var[0] = null;
    if (s.equalsIgnoreCase("true"))
      var[1] = true;
    else if (s.equalsIgnoreCase("false"))
      var[1] = false;
    if (var[1] != null)
      return var;
    try {
      var[1] = Integer.parseInt(s);
      return var;
    } catch (NumberFormatException e) {}
    try {
      var[1] = Long.parseLong(s);
      return var;
    } catch (NumberFormatException e) {}
    try {
      var[1] = Double.parseDouble(s);
      return var;
    } catch (NumberFormatException e) {}
    var[1] = s.toUpperCase();
    return var;
  }
  
  private RoomInfo parseRoom(LinkedList<String> lines) {
    RoomInfo room = new RoomInfo();
    
    return room;
  }
  
  private RegionInfo parseRegion(LinkedList<String> lines) {
    RegionInfo region = new RegionInfo();
    
    return region;
  }
  
  private ItemInfo parseItem(LinkedList<String> lines) {
    ItemInfo item = new ItemInfo();
    
    return item;
  }
  
  private MobInfo parseMob(LinkedList<String> lines) {
    MobInfo mob = new MobInfo();
    
    return mob;
  }
  
  public class RoomInfo {
    public boolean unique = false;
    public String name;
    public String[] description;
    public ItemInfo[] items;
    public MobInfo[] mobs;
  }
  
  public class RegionInfo {
    public boolean unique = false;
    public HashMap<String, Object> vars = new HashMap<String, Object>();
    public RoomInfo[] rooms;
    public ItemInfo[] items;
    public MobInfo[] mobs;
  }
  
  public class ItemInfo {
    public long vnum;
    public ItemInfo[] contents;
  }
  
  public class MobInfo {
    public long vnum;
    public ItemInfo[] contents;
    public ItemInfo[] equiped;
  }
}

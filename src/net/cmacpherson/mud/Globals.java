package net.cmacpherson.mud;

import java.io.File;

public final class Globals {

  public static final String WORLD_PATH = "data/universe/";
  public static final String BIOMES_PATH = "data/biomes/";
  public static final String PC_PATH = "data/pcs/";
  public static final String ITEM_PATH = "data/items/";
  public static final String MOB_PATH = "data/mobs/";
  public static final File CONFIG_FILE = new File("data/config.xml");
  public static final File SOCIALS_FILE = new File("data/socials.txt");
  
  public static final String STARTING_PROMPT = "|y|<|br|%h/%Hhp |bc|%m/%Mm |bp|%v/%Vmv |bw|%T|y|>";
  public static final long STARTING_LOCATION_ID = 5;
  public static final long STARTING_GOLD = 100;
  public static final long STARTING_HP = 30;
  public static final long STARTING_MANA = 100;
  public static final long STARTING_MOVES = 50;
  public static final int STARTING_ALIGNMENT = 0;
  
  public static final double DEFAULT_HP_REGEN = .20;
  public static final double DEFAULT_M_REGEN = .20;
  public static final double DEFAULT_MV_REGEN = .30;
  
  public static final int IMM_LEVEL = 1;
 
  public static long LAST_ROOM_ID = 0;
  public static long LAST_ITEM_ID = 0;
  public static long LAST_PROTO_ITEM_ID = 0;
  public static long LAST_PROTO_MOB_ID = 0;
  public static long LAST_KEY_ID = 0;
  
  public static final String COLOR_CODES[] =
    {"|w|",  "|k|",  "|r|",  "|g|",
     "|y|",  "|b|",  "|p|",  "|c|",
     "|bw|", "|bk|", "|br|", "|bg|",
     "|by|", "|bb|", "|bp|", "|bc|",
     "|W|",  "|K|",  "|R|",  "|G|",
     "|Y|",  "|B|",  "|P|",  "|C|",
     "|BW|", "|BK|", "|BR|", "|BG|",
     "|BY|", "|BB|", "|BP|", "|BC|"};
  public static final String ANSI_CODES[] = 
    {"\033[0m",    "\033[0;30m", "\033[0;31m", "\033[0;32m",
     "\033[0;33m", "\033[0;34m", "\033[0;35m", "\033[0;36m",
     "\033[1;37m", "\033[1;30m", "\033[1;31m", "\033[1;32m",
     "\033[1;33m", "\033[1;34m", "\033[1;35m", "\033[1;36m"};
  public static final int INPUT_COLOR = Color.WHITE.ordinal();
  public static final int COLOR = Color.GREEN.ordinal();
  public static final String COLOR_STR = "|g|";
  public static final String COLOR_ROOM_NAME = "|by|";
  public static final String COLOR_DESCRIPTION = "|y|";
  public static final String COLOR_EXITS = "|c|";
  public static final String COLOR_ITEMS = "|w|";
  public static final String COLOR_CHARS = "|p|";
  public static final Color COLOR_SAY = Color.YELLOW;
  public static final Color COLOR_YELL = Color.RED;
  
  public static final long GOLD_VNUM = -1;
  public static final double WEIGHT_PER_GOLD = 1.0 / 100.0;
  
  public static String[][] SOCIALS;
  
  public static final String[] EQ_SLOT_DESC = new String[] {
    "   <used as light> ",
    "  <on left finger> ",
    " <on right finger> ",
    "<worn around neck> ",
    "    <worn on body> ",
    "    <worn on head> ",
    "    <worn on feet> ",
    "    <worn on legs> ",
    "   <worn on hands> ",
    "    <worn on arms> ",
    " <worn about body> ",
    "   <worn on waist> ",
    "   <on left wrist> ",
    "  <on right wrist> ",
    " <used in offhand> ",
    "   <used as wield> "
  };
  
  public static final String[] HEALTH_STATUS_LINES = new String[] {
    "perfectly healthy",   // 95%
    "bruised",             // 75%
    "injured",             // 60%
    "bloodied",            // 40%
    "very badly wounded",  // 25%
    "in aweful condition", // 5%
    "on death's door"      // 0%
  };
  public static final String[] EXHAUSTION_STATUS_LINES = new String[] {
    "perfectly rested",             // 95%
    "breathing heavily",            // 75%
    "trying to catch their breath", // 60%
    "panting",                      // 40%
    "exhausted",                    // 25%
    "struggling to breath",         // 5%
    "beyond exhausted"              // 0%
  };
 
//  public static Plane generateDefaultPlane() {
//    Plane nowhere = new Plane();
//    nowhere.name = "No Where";
//    {
//      Area limbo = new Area();
//      limbo.name = "Limbo";
//      {
//        Room nw = new Room(); //indoors closed
//        {
//          nw.id = 1;
//          nw.name = "Northwest";
//          nw.environment = Environment.INDOORS;
//          nw.setFlag(Room.LIGHT, true);
//          nw.setFlag(Room.SAFE, true);
//          Door ee = new Door();
//          ee.isLinked = true;
//          ee.targetID = 2;
//          ee.setFlag(Door.CLOSED, true);
//          ee.setFlag(Door.LOCKED, true);
//          ee.setFlag(Door.PASSABLE, true);
//          ee.keyID = 1;
//          ee.direction = Direction.EAST;
//          nw.exits[Direction.EAST.ordinal()] = ee;
//          Door es = new Door();
//          es.isLinked = true;
//          es.targetID = 4;
//          es.setFlag(Door.CLOSED, true);
//          es.isHidden = true;
//          es.direction = Direction.SOUTH;
//          nw.exits[Direction.SOUTH.ordinal()] = es;
//        }
//        limbo.rooms.add(nw);
//        Room n = new Room(); //city
//        {
//          n.id = 2;
//          n.name = "North";
//          n.environment = Environment.CITY;
//          n.setFlag(Room.LIGHT, true);
//          n.setFlag(Room.SAFE, true);
//          Exit ee = new Exit();
//          ee.isLinked = true;
//          ee.targetID = 3;
//          ee.direction = Direction.EAST;
//          n.exits[Direction.EAST.ordinal()] = ee;
//          Exit es = new Exit();
//          es.isLinked = true;
//          es.targetID = 5;
//          es.direction = Direction.SOUTH;
//          n.exits[Direction.SOUTH.ordinal()] = es;
//          Door ew = new Door();
//          ew.isLinked = true;
//          ew.targetID = 1;
//          ew.setFlag(Door.CLOSED, true);
//          ew.setFlag(Door.LOCKED, true);
//          ew.setFlag(Door.PASSABLE, true);
//          ew.keyID = 1;
//          ew.direction = Direction.WEST;
//          n.exits[Direction.WEST.ordinal()] = ew;
//        }
//        limbo.rooms.add(n);
//        Room ne = new Room(); //mountain
//        {
//          ne.id = 3;
//          ne.name = "Northeast";
//          ne.environment = Environment.MOUNTAINS;
//          ne.setFlag(Room.LIGHT, true);
//          ne.setFlag(Room.SAFE, true);
//          Exit es = new Exit();
//          es.isLinked = true;
//          es.targetID = 8;
//          es.direction = Direction.SOUTH;
//          ne.exits[Direction.SOUTH.ordinal()] = es;
//          Exit ew = new Exit();
//          ew.isLinked = true;
//          ew.targetID = 2;
//          ew.direction = Direction.WEST;
//          ne.exits[Direction.WEST.ordinal()] = ew;
//        }
//        limbo.rooms.add(ne);
//        Room w = new Room(); //city
//        {
//          w.id = 4;
//          w.name = "West";
//          w.environment = Environment.CITY;
//          w.setFlag(Room.LIGHT, true);
//          w.setFlag(Room.SAFE, true);
//          Door en = new Door();
//          en.isLinked = true;
//          en.targetID = 1;
//          en.setFlag(Door.CLOSED, true);
//          en.isHidden = true;
//          en.direction = Direction.NORTH;
//          w.exits[Direction.NORTH.ordinal()] = en;
//          Exit ee = new Exit();
//          ee.isLinked = true;
//          ee.targetID = 5;
//          ee.direction = Direction.EAST;
//          w.exits[Direction.EAST.ordinal()] = ee;
//          Exit es = new Exit();
//          es.isLinked = true;
//          es.targetID = 9;
//          es.direction = Direction.SOUTH;
//          w.exits[Direction.SOUTH.ordinal()] = es;
//        }
//        limbo.rooms.add(w);
//        Room c = new Room(); //plains
//        {
//          c.id = 5;
//          c.name = "Center";
//          c.environment = Environment.PLAINS;
//          c.setFlag(Room.LIGHT, true);
//          c.setFlag(Room.SAFE, true);
//          Exit en = new Exit();
//          en.isLinked = true;
//          en.targetID = 2;
//          en.direction = Direction.NORTH;
//          c.exits[Direction.NORTH.ordinal()] = en;
//          Exit ee = new Exit();
//          ee.isLinked = true;
//          ee.targetID = 8;
//          ee.direction = Direction.EAST;
//          c.exits[Direction.EAST.ordinal()] = ee;
//          Exit es = new Exit();
//          es.isLinked = true;
//          es.targetID = 10;
//          es.direction = Direction.SOUTH;
//          c.exits[Direction.SOUTH.ordinal()] = es;
//          Exit ew = new Exit();
//          ew.isLinked = true;
//          ew.targetID = 4;
//          ew.direction = Direction.WEST;
//          c.exits[Direction.WEST.ordinal()] = ew;
//          Exit eu = new Exit();
//          eu.isLinked = true;
//          eu.targetID = 6;
//          eu.direction = Direction.UP;
//          c.exits[Direction.UP.ordinal()] = eu;
//          Exit ed = new Exit();
//          ed.isLinked = true;
//          ed.targetID = 7;
//          ed.direction = Direction.DOWN;
//          c.exits[Direction.DOWN.ordinal()] = ed;
//        }
//        limbo.rooms.add(c);
//        Room u = new Room(); //air
//        {
//          u.id = 6;
//          u.name = "Above Center";
//          u.environment = Environment.AIR;
//          u.setFlag(Room.LIGHT, true);
//          u.setFlag(Room.SAFE, true);
//          Exit ed = new Exit();
//          ed.isLinked = true;
//          ed.targetID = 5;
//          ed.direction = Direction.DOWN;
//          u.exits[Direction.DOWN.ordinal()] = ed;
//        }
//        limbo.rooms.add(u);
//        Room d = new Room(); //not safe
//        {
//          d.id = 7;
//          d.name = "Below Center";
//          d.environment = Environment.UNDERGROUND;
//          d.setFlag(Room.LIGHT, true);
//          Exit eu = new Exit();
//          eu.isLinked = true;
//          eu.targetID = 5;
//          eu.direction = Direction.UP;
//          d.exits[Direction.UP.ordinal()] = eu;
//        }
//        limbo.rooms.add(d);
//        Room e = new Room(); //hills
//        {
//          e.id = 8;
//          e.name = "East";
//          e.environment = Environment.HILLS;
//          e.setFlag(Room.LIGHT, true);
//          e.setFlag(Room.SAFE, true);
//          Exit en = new Exit();
//          en.isLinked = true;
//          en.targetID = 3;
//          en.direction = Direction.NORTH;
//          e.exits[Direction.NORTH.ordinal()] = en;
//          Exit es = new Exit();
//          es.isLinked = true;
//          es.targetID = 11;
//          es.direction = Direction.SOUTH;
//          e.exits[Direction.SOUTH.ordinal()] = es;
//          Exit ew = new Exit();
//          ew.isLinked = true;
//          ew.targetID = 5;
//          ew.direction = Direction.WEST;
//          e.exits[Direction.WEST.ordinal()] = ew;
//        }
//        limbo.rooms.add(e);
//        Room sw = new Room(); //forest
//        {
//          sw.id = 9;
//          sw.name = "Southwest";
//          sw.environment = Environment.FOREST;
//          sw.setFlag(Room.SAFE, true);
//          Exit en = new Exit();
//          en.isLinked = true;
//          en.targetID = 4;
//          en.direction = Direction.NORTH;
//          sw.exits[Direction.NORTH.ordinal()] = en;
//          Exit ee = new Exit();
//          ee.isLinked = true;
//          ee.targetID = 10;
//          ee.direction = Direction.EAST;
//          sw.exits[Direction.EAST.ordinal()] = ee;
//        }
//        limbo.rooms.add(sw);
//        Room s = new Room(); //hills
//        {
//          s.id = 10;
//          s.name = "South";
//          s.environment = Environment.HILLS;
//          s.setFlag(Room.LIGHT, true);
//          s.setFlag(Room.SAFE, true);
//          Exit en = new Exit();
//          en.isLinked = true;
//          en.targetID = 5;
//          en.direction = Direction.NORTH;
//          s.exits[Direction.NORTH.ordinal()] = en;
//          Exit ee = new Exit();
//          ee.isLinked = true;
//          ee.targetID = 11;
//          ee.direction = Direction.EAST;
//          s.exits[Direction.EAST.ordinal()] = ee;
//          Exit ew = new Exit();
//          ew.isLinked = true;
//          ew.targetID = 9;
//          ew.direction = Direction.WEST;
//          s.exits[Direction.WEST.ordinal()] = ew;
//        }
//        limbo.rooms.add(s);
//        Room se = new Room(); //water surface
//        {
//          se.id = 11;
//          se.name = "Southeast";
//          se.environment = Environment.WATER_SURFACE;
//          se.setFlag(Room.LIGHT, true);
//          se.setFlag(Room.SAFE, true);
//          Exit en = new Exit();
//          en.isLinked = true;
//          en.targetID = 8;
//          en.direction = Direction.NORTH;
//          se.exits[Direction.NORTH.ordinal()] = en;
//          Exit ew = new Exit();
//          ew.isLinked = true;
//          ew.targetID = 10;
//          ew.direction = Direction.WEST;
//          se.exits[Direction.WEST.ordinal()] = ew;
//          Exit ed = new Exit();
//          ed.isLinked = true;
//          ed.targetID = 12;
//          ed.direction = Direction.DOWN;
//          se.exits[Direction.DOWN.ordinal()] = ed;
//        }
//        limbo.rooms.add(se);
//        Room sed = new Room(); //under water
//        {
//          sed.id = 12;
//          sed.name = "Under Southeast";
//          sed.environment = Environment.UNDER_WATER;
//          sed.setFlag(Room.LIGHT, true);
//          sed.setFlag(Room.SAFE, true);
//          Exit eu = new Exit();
//          eu.isLinked = true;
//          eu.targetID = 11;
//          eu.direction = Direction.UP;
//          sed.exits[Direction.UP.ordinal()] = eu;
//        }
//        limbo.rooms.add(sed);
//      }
//      nowhere.areas.add(limbo);
//    }
//    LAST_ROOM_ID = 12;
//    return nowhere;
//  }
}

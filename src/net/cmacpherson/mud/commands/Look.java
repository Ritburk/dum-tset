package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Look implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    String[] args = new String[0];
    if (line != null)
      args = Utils.splitInput(line);
    if (args.length == 0) {
      //look at room
      if (c.location.room.hasLight()) {
        o.toChar(Globals.COLOR_ROOM_NAME + c.location.room.name, c);
        for (String s : c.location.room.description)
          o.toChar(Globals.COLOR_DESCRIPTION + s, c);
        o.toChar(Globals.COLOR_EXITS + c.location.room.displayExits(), c);
        Object[] objs = Utils.condenseItems(c.location.room.items, c);
        int[] counts = (int[])objs[0];
        Item[] items = (Item[])objs[1];
        for (int i = 0; i < items.length; i++) {
          if (Utils.testVisibility(c, items[i]))
            if (counts[i] > 1) {
              String prefix = "(" + counts[i] + ") ";
              while (prefix.length() < 8)
                prefix = " " + prefix;
              o.toChar(Globals.COLOR_ITEMS + prefix + items[i].longName, c);
            } else
              o.toChar(Globals.COLOR_ITEMS + "        " + items[i].longName, c);
        }
        for (Character ch : c.location.room.chars) {
          if (!c.equals(ch) &&
              Utils.testVisibility(c, ch))
            o.toChar(Globals.COLOR_CHARS + ch.displayName(), c);
        }
      } else {
        o.toChar("You can't see anything!", c);
      }
    } else if (args[0].equalsIgnoreCase("at")) {
      try {
        boolean showDesc = true;
        boolean showHealth = true;
        boolean showExhaust = true;
        boolean showEQ = true;
        if (args.length > 2) {
          showDesc = args[2].regionMatches(true, 0, "description", 0, args[2].length());
          showHealth = args[2].regionMatches(true, 0, "health", 0, args[2].length());
          showExhaust = args[2].regionMatches(true, 0, "exhaustion", 0, args[2].length());
          showEQ = args[2].regionMatches(true, 0, "equipment", 0, args[2].length());
        }
        if (args[1].regionMatches(true, 0, "self", 0, args[1].length())) {
          o.toChar("You look at yourself,", c);
          String prefix = "You are ";
          if (showDesc) o.toChar(lookAtDescription(c), c);
          if (showHealth) o.toChar(lookAtHealth(c, prefix), c);
          if (showExhaust) o.toChar(lookAtExhaustion(c, prefix), c);
          if (showEQ) o.toChar(Utils.showEQ(c, c), c);
        } else {
          ArrayList<Keywords> keywords = new ArrayList<Keywords>();
          for (Character ch : c.location.room.chars)
            if (Utils.testVisibility(c, ch))
              keywords.add(ch);
          for (Item item : c.location.room.items)
            if (Utils.testVisibility(c, item))
              keywords.add(item);
          for (Item item : c.inv)
            if (Utils.testVisibility(c, item) &&
                !item.isEquipped())
              keywords.add(item);
          for (Item item : c.eq)
            if (item != null &&
                Utils.testVisibility(c, item))
              keywords.add(item);
          Object[] arg = Utils.parseInput(args[1]);
          Keywords obj = Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
          if (obj == null)
            o.toChar("Look at what?", c);
          else if (obj instanceof Character) {
            if (obj.equals(c)) {
              c.force("look at self", null, false);
              return true;
            }
            Character ch = (Character)obj;
            o.toChar("%N$n looks at you.", ch, c);
            o.toChar("You look at $n,", c, ch);
            if (showDesc) o.toChar(lookAtDescription(ch), c, ch);
            if (showHealth) o.toChar(lookAtHealth(ch, null), c, ch);
            if (showExhaust) o.toChar(lookAtExhaustion(ch, null), c, ch);
            if (showEQ) o.toChar(Utils.showEQ(c, ch), c, ch);
          } else if (obj instanceof Item) {
            Item item = (Item)obj;
            o.toChar("You look at " + item.name + ",", c);
            o.toChar("  " + item.longName, c);
            for (String s : item.description)
              o.toChar(s, c);
          }
        }
      } catch (ArrayIndexOutOfBoundsException e) {
        o.toChar("Look at what?", c);
      }
    } else if (args[0].equalsIgnoreCase("in")) {
      //look in container
      try {
        ArrayList<Keywords> keywords = new ArrayList<Keywords>();
        for (Item item : c.inv)
          if (Utils.testVisibility(c, item) &&
              !item.isEquipped())
            keywords.add(item);
        for (Item item : c.eq)
          if (Utils.testVisibility(c, item))
            keywords.add(item);
        for (Item item : c.location.room.items)
          if (Utils.testVisibility(c, item))
            keywords.add(item);
        Object[] arg = Utils.parseInput(args[1]);
        Keywords obj = Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
        if (obj == null)
          o.toChar("You can't seem to find it.", c);
        else {
          if (obj instanceof Item) {
            Item item = (Item)obj;
            if (item.isType(Item.CONTAINER)) {
              if (item.status == Item.Status.CLOSED)
                o.toChar("It's closed.", c);
              else {
                o.toChar("You look inside " + item.name + ":", c);
                Object[] objs = Utils.condenseItems(c.inv, c);
                int[] counts = (int[])objs[0];
                Item[] items = (Item[])objs[1];
                if (items.length == 0)
                  o.toChar("      (nothing)", c);
                else
                  for (int i = 0; i < items.length; i++)
                    if (counts[i] > 1) {
                      String prefix = "(" + counts[i] + ")";
                      while (prefix.length() < 6)
                        prefix = " " + prefix;
                      o.toChar(prefix + items[i].name, c);
                    } else
                      o.toChar("      " + items[i].name, c);
              }
            } else
              o.toChar("You can't look inside that.", c);
          }
        }
      } catch (ArrayIndexOutOfBoundsException e) {
        o.toChar("Look in what?", c);
      }
    } else {
      for (Direction dir : Direction.values()) {
        //Test for directions
        if (args[0].regionMatches(true, 0, dir.name(), 0, args[0].length())) {
          Exit exit = c.location.room.exits[dir.ordinal()];
          if (exit != null) {
            if (exit instanceof Door) {
              Door door = (Door)exit;
              if (door.isHidden)
                o.toChar("You find a hidden door.", c);
              if (door.isClosed())
                o.toChar("You look at the door, it is closed.", c);
              else {
                o.toChar("You look through the open door:", c);
                boolean something = false;;
                for (Character ch : exit.target.room.chars)
                  if (Utils.testVisibility(c, ch)) {
                    o.toChar("  " + ch.displayName(), c);
                    something = true;
                  }
                if (!something)
                  o.toChar("  nothing is there.", c);
              }
            } else {
              if (exit.isHidden) {
                o.toChar("You sense a hidden path leading that direction.", c);
                o.toChar("  You can't seem to make anything else out.", c);
              } else {
                o.toChar("You look " + dir.name().toLowerCase() + ":", c);
                boolean something = false;;
                for (Character ch : exit.target.room.chars)
                  if (Utils.testVisibility(c, ch)) {
                    o.toChar("  " + ch.displayName(), c);
                    something = true;
                  }
                if (!something)
                  o.toChar("  nothing is there.", c);
              }
            }
          } else
            o.toChar("There is nothing to see that direction.", c);
          return true;
        }
      }
      o.toChar("Look where?", c);
    }
    return true;
  }
  
  private String lookAtDescription(Character c) {
    if (c.description.size() == 0) return "<no descripton>";
    String s = "";
    for (String str : c.description)
      s += str + "%N";
    return s.substring(0, s.length() - 2);
  }
  
  private String lookAtHealth(Character c, String prefix) {
    String s = "";
    double percent = (double)c.hp / (double)c.mhp;
    if (percent > .95)
      s += Globals.HEALTH_STATUS_LINES[0];
    else if (percent > .75)
      s += Globals.HEALTH_STATUS_LINES[1];
    else if (percent > .60)
      s += Globals.HEALTH_STATUS_LINES[2];
    else if (percent > .40)
      s += Globals.HEALTH_STATUS_LINES[3];
    else if (percent > .25)
      s += Globals.HEALTH_STATUS_LINES[4];
    else if (percent > .05)
      s += Globals.HEALTH_STATUS_LINES[5];
    else
      s += Globals.HEALTH_STATUS_LINES[6];
    s = (prefix == null ? "$n looks like they are " : prefix) + s + ".";
    return s;
  }
  
  private String lookAtExhaustion(Character c, String prefix) {
    String s = "";
    double percent = (double)c.mv / (double)c.mmv;
    if (percent > .95)
      s += Globals.EXHAUSTION_STATUS_LINES[0];
    else if (percent > .75)
      s += Globals.EXHAUSTION_STATUS_LINES[1];
    else if (percent > .60)
      s += Globals.EXHAUSTION_STATUS_LINES[2];
    else if (percent > .40)
      s += Globals.EXHAUSTION_STATUS_LINES[3];
    else if (percent > .25)
      s += Globals.EXHAUSTION_STATUS_LINES[4];
    else if (percent > .05)
      s += Globals.EXHAUSTION_STATUS_LINES[5];
    else
      s += Globals.EXHAUSTION_STATUS_LINES[6];
    s = (prefix == null ? "$n looks like they are " : prefix) + s + ".";
    return s;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: look%N" +
           "        look at <char>%N" +
           "        look in <container>%N" +
           "        look <dir>";
  }
}
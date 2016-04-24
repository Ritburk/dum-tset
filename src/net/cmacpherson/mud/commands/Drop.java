package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Drop implements Command {
  /*
   * examples:
   *   drop hat
   *   drop all.hat
   *   drop 2.hat
   *   drop 10 gold
   *   drop all gold
   */
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Drop what?", c);
      return true;
    }
    String[] args = Utils.splitInput(line);
    Object[] arg = Utils.parseInput(args[0]);
    ArrayList<Keywords> keywords = new ArrayList<Keywords>();
    for (Item item : c.inv)
      if (Utils.testVisibility(c, item) &&
          !item.isEquipped())
        keywords.add(item);
    if (((int)arg[0]) == 0) {
      Item[] items = Utils.cast(Utils.findMatches((String)arg[1], keywords.toArray(new Keywords[] {})), Item.class);
      if (items.length == 0)
        o.toChar("You can't seem to find them.", c);
      else {
        for (Item item : items)
          if (item.is(Item.CURSED))
            o.toChar("You can't seem to let go of it!", c);
          else {
            c.inv.remove(item);
            c.location.room.items.add(item);
            o.toChar("You drop $i.", c, item);
          }
        o.toRoom(c.location.room, "%N$n drops several items.", c);
      }
    } else if (args.length > 1 &&
               args[1].equalsIgnoreCase("gold")) {
      if (args[0].equalsIgnoreCase("all")) {
        o.toChar("You drop " + c.gold + " gold.", c);
        o.toRoom(c.location.room, "%N$n drops some gold.", c);
        c.location.room.items.add(ItemProto.generateGold(c.gold));
        c.gold = 0;
      } else
        try {
          int gold = Integer.parseInt(args[0]);
          if (gold > c.gold)
            o.toChar("You don't have that much gold.", c);
          else {
            o.toChar("You drop " + gold + " gold.", c);
            o.toRoom(c.location.room, "%N$n drops some gold.", c);
            c.location.room.items.add(ItemProto.generateGold(gold));
            c.gold -= gold;
          }
        } catch (NumberFormatException e) {
          o.toChar("You can't seem to figure out how much gold to drop.", c);
        }
    } else {
      Item item = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (item == null) {
        o.toChar("You can't seem to find it.", c);
      } else {
        if (item.is(Item.CURSED))
          o.toChar("You can't seem to let go of it!", c);
        else {
          c.inv.remove(item);
          c.location.room.items.add(item);
          o.toChar("You drop $i.", c, item);
          o.toRoom(c.location.room, "%N$n drops $i.", c, item);
        }
      }
    }
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: drop <item>%N" +
           "        drop <amount> gold";
  }
}

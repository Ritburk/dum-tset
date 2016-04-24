package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Remove implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Remove what?", c);
      return true;
    }
    Object[] arg = Utils.parseInput(Utils.readArg(line)[0]);
    ArrayList<Keywords> keywords = new ArrayList<Keywords>();
    for (Item item : c.eq)
      if (item != null &&
          Utils.testVisibility(c, item))
        keywords.add(item);
    if ((int)arg[0] == 0) {
      Item[] items = Utils.cast(Utils.findMatches((String)arg[1], keywords.toArray(new Keywords[] {})), Item.class);
      if (items.length == 0)
        o.toChar("You can't remove something you aren't wearing.", c);
      else {
        for (Item item : items)
          try {
            c.remove(item);
            o.toChar("You remove $i.", c, item);
          } catch (CursedItemException e) {
            o.toChar("You try to take off $i, but it won't let go! CURSED!", c, item);
          }
        o.toRoom(c.location.room, "%N$n removes some items.", c);
      }
    } else {
      Item item = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (item == null)
        o.toChar("You can't remove something you aren't wearing.", c);
      else {
        try {
          c.remove(item);
          o.toChar("You remove $i.", c, item);
          o.toRoom(c.location.room, "%N$n removes $i.", c, item);
        } catch (CursedItemException e) {
          o.toChar("You try to take off $i, but it won't let go! CURSED!", c, item);
        }
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: remove <item>";
  }
}

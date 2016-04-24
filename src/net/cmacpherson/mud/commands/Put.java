package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Put implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Put what in where?", c);
      return true;
    }
    String[] args = Utils.splitInput(line);
    if (args.length == 1) {
      o.toChar("Put it in where?", c);
    } else {
      if (args[1].equalsIgnoreCase("in")) {
        if (args.length > 2) {
          ArrayList<Keywords> keywords = new ArrayList<Keywords>();
          for (Item item : c.inv)
            if (!item.isEquipped() &&
                Utils.testVisibility(c, item))
              keywords.add(item);
          for (Item item : c.inv)
            if (item.isEquipped() &&
                Utils.testVisibility(c, item))
              keywords.add(item);
          for (Item item : c.location.room.items)
            if (Utils.testVisibility(c, item))
              keywords.add(item);
          Object[] arg = Utils.parseInput(args[2]);
          if ((int)arg[0] == 0)
            o.toChar("You can't put something into multiple containers.", c);
          else {
            Item container = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
            if (container == null)
              o.toChar("You can't find what you want to put that into.", c);
            else {
              keywords.clear();
              for (Item item : c.inv)
                if (!item.isEquipped() &&
                    Utils.testVisibility(c, item))
                  keywords.add(item);
              arg = Utils.parseInput(args[0]);
              if ((int)arg[0] == 0) {
                Item[] items = Utils.cast(Utils.findMatches((String)arg[1], keywords.toArray(new Keywords[] {})), Item.class);
                if (items.length == 0)
                  o.toChar("You can't find what you are tryin gto put in there.", c);
                else {
                  for (Item item : items)
                    try {
                      container.addToContents(item);
                      c.inv.remove(item);
                      o.toChar("You put $i into $I.", c, item, container);
                    } catch (TooHeavyException e) {
                      o.toChar("You try to put $i into $I, but it would break from the weight.", c, item, container);
                    } catch (NoSpaceException e) {
                      o.toChar("You try to put $i into $I, but it is too full.", c, item, container);
                    }
                  o.toRoom(c.location.room, "%N$n puts several things into $i.", c, container);
                }
              } else {
                Item item = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
                if (item == null)
                  o.toChar("You can't find what you are trying to put in there.", c);
                else {
                  try {
                    container.addToContents(item);
                    c.inv.remove(item);
                    o.toChar("You put $i into $I.", c, item, container);
                    o.toRoom(c.location.room, "%N$n puts $i into $I.", c, item, container);
                  } catch (TooHeavyException e) {
                    o.toChar("You try to put $i into $I, but it would break from the weight.", c, item, container);
                  } catch (NoSpaceException e) {
                    o.toChar("You try to put $i into $I, but it is too full.", c, item, container);
                  }
                }
              }
            }
          }
        } else
          o.toChar("Put it in where?", c);
      } else
        o.toChar(getHelp(), c);
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: put <item> in <container>";
  }
}

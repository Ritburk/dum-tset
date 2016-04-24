package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Get implements Command {
  
  private String verb = "get";
  
  public Get() {}
  
  public Get(String verb) {
    this.verb = verb;
  }
  
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar(verb.substring(0, 1).toUpperCase() + verb.substring(1) + " what?", c);
      return true;
    }
    String[] args = Utils.splitInput(line);
    Object[] arg = Utils.parseInput(args[0]);
    if (args.length == 1) {
      ArrayList<Keywords> keywords = new ArrayList<Keywords>();
      for (Item item : c.location.room.items)
        if (Utils.testVisibility(c, item))
          keywords.add(item);
      if ((int)arg[0] == 0) {
        Item[] items = Utils.cast(Utils.findMatches((String)arg[0], keywords.toArray(new Keywords[] {})), Item.class);
        if (items.length == 0)
          o.toChar("You don't see them here.", c);
        else {
          for (Item item : items) {
            try {
              c.addToInv(item);
              o.toChar("You pick up $i.",  c, item);
              c.location.room.items.remove(item);
            } catch (TooHeavyException e) {
              o.toChar("You try to pick $i up, but you can't hold the weight.", c, item);
            } catch (NoSpaceException e) {
              o.toChar("You try to pick $i up, but your hands are full.", c, item);
            }
          }
          o.toRoom(c.location.room, "%N$n picks up several items.", c);
        }
      } else {
        Item item = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
        if (item == null)
          o.toChar("You don't see it here.", c);
        else {
          try {
            c.addToInv(item);
            o.toChar("You pick up $i.", c, item);
            o.toRoom(c.location.room, "%N$n picks up $i.", c, item);
            c.location.room.items.remove(item);
          } catch (TooHeavyException e) {
            o.toChar("You try to pick $i up, but you can't hold the weight.", c, item);
          } catch (NoSpaceException e) {
            o.toChar("You try to pick $i up, but your hands are full.", c, item);
          }
        }
      }
    } else {
      if (args[1].equalsIgnoreCase("from")) {
        if (args.length > 2) {
          Object[] arg2 = Utils.parseInput(args[2]);
          if ((int)arg2[0] == 0)
            o.toChar("You can't manage to search several containers at the same time.", c);
          else {
            ArrayList<Keywords> keywords = new ArrayList<Keywords>();
            for (Item item : c.inv)
              if (Utils.testVisibility(c, item) &&
                  !item.isEquipped())
                keywords.add(item);
            for (Item item : c.inv)
              if (Utils.testVisibility(c, item) &&
                  item.isEquipped())
                keywords.add(item);
            for (Item item : c.location.room.items)
              if (Utils.testVisibility(c, item))
                keywords.add(item);
            Item container = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
            if (container == null)
              o.toChar("You can't find what you want to look in.", c);
            else if (!container.isType(Item.CONTAINER))
              o.toChar("That's not a container.", c);
            else if (container.status == Item.Status.CLOSED ||
                     container.status == Item.Status.LOCKED)
              o.toChar("It's closed.", c);
            else {
              keywords.clear();
              for (Item item : container.contents)
                if (Utils.testVisibility(c, item))
                  keywords.add(item);
              if ((int)arg[0] == 0) {
                Item[] items = (Item[])Utils.findMatches((String)arg[1], keywords.toArray(new Keywords[] {}));
                if (items.length == 0)
                  o.toChar("You don't see them in $i.", c, container);
                else {
                  for (Item item : items) {
                    try {
                      if (c.inv.contains(container))
                        c.addToInv(item, true);
                      else
                        c.addToInv(item);
                      container.contents.remove(item);
                      o.toChar("You " + verb + " $i from $I.", c, item, container);
                    } catch (TooHeavyException e) {
                      o.toChar("You try to " + verb + " $i from $I, but it's too heavy.", c, item, container);
                    } catch (NoSpaceException e) {
                      o.toChar("You try to " + verb + " $i from $I, but your hands are full,", c, item, container);
                    }
                  }
                  o.toRoom(c.location.room, "%N$n " + verb + "s several things from $i.", c, container);
                }
              } else {
                Item item = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
                if (item == null)
                  o.toChar("You don't see it in $i.", c);
                else {
                  try {
                    if (c.inv.contains(container))
                      c.addToInv(item, true);
                    else
                      c.addToInv(item);
                    container.contents.remove(item);
                    o.toChar("You " + verb + " $i from $I.", c, item, container);
                    o.toRoom(c.location.room, "%N$n " + verb + "s $i from $I.", c, item, container);
                  } catch (TooHeavyException e) {
                    o.toChar("You try to " + verb + " $i from $I, but it's too heavy.", c, item, container);
                  } catch (NoSpaceException e) {
                    o.toChar("You try to " + verb + " $i from $I, but your hands are full.", c, item, container);
                  }
                }
              }
            }
          }
        } else
          o.toChar("From where did you want to " + verb + " that item?", c);
      } else
        o.toChar("You aren't sure where you're trying to " + verb + " that item from.", c);
    }
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: " + verb + " <item>%N" +
           "        " + verb + " <item> from <container>";
  }
}
package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Give implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Give what to whom?", c);
      return true;
    }
    String[] args = Utils.splitInput(line);
    if (args.length == 1)
      o.toChar("To whom did you want to give that to?", c);
    else if (args.length == 2 ||
             (args.length == 3 &&
              !args[1].equalsIgnoreCase("to")))
        o.toChar(getHelp(), c);
    else {
      Object[] arg = Utils.parseInput(args[2]);
      ArrayList<Keywords> keywords = new ArrayList<Keywords>();
      for (Character ch : c.location.room.chars)
        if (Utils.testVisibility(c, ch))
          keywords.add(ch);
      Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (ch == null) {
        o.toChar("They aren't here.", c);
        return true;
      }
      arg = Utils.parseInput(args[0]);
      keywords.clear();
      for (Item item : c.inv)
        if (!item.isEquipped() &&
            Utils.testVisibility(c, item))
          keywords.add(item);
      if ((int)arg[0] == 0) {
        Item[] items = Utils.cast(Utils.findMatches((String)arg[1], keywords.toArray(new Keywords[] {})), Item.class);
        if (items.length == 0)
          o.toChar("You can't give them something you can't find.", c);
        else {
          if (!Utils.testVisibility(ch, c))
            o.toChar("They can't see you.", c);
          else {
            o.toChar("", ch);
            for (Item item : items)
              if (!Utils.testVisibility(ch, item)) {
                o.toChar("They can't see $i.", c, item);
                o.toChar("$n holds out $s hand to give you something, but you don't see anything.", ch, c);
              } else if (item.is(Item.CURSED)) {
                o.toChar("You attempt to give $i to $n, but you can't let go of it.", c, ch, item);
                o.toChar("$n tries to give you $i, but $e can't let go of it.", ch, c, item);
              } else
                try {
                  ch.addToInv(item);
                  c.inv.remove(item);
                  o.toChar("You give $i to $n.", c, ch, item);
                  o.toChar("$n gives you $i.", ch, c, item);
                } catch (TooHeavyException e) {
                  o.toChar("You try to give $i to $N, but it's too heavy for them.",  c, ch, item);
                  o.toChar("$n tries to give you $i, but it's too heavy for you.", ch, c, item);
                } catch (NoSpaceException e) {
                  o.toChar("You try to give $i to $N, but their hands are full.",  c, ch, item);
                  o.toChar("$n tries to give you $i, but your hands are full.", ch, c, item);
                }
            o.toRoom(c.location.room, "%N$n gives several items to $N.", ch, c);
          }
        }
      } else {
        Item item = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[]{}));
        if (item == null)
          o.toChar("You can't give them something you can't find.", c);
        else {
          if (!Utils.testVisibility(ch, c))
            o.toChar("They can't see you.", c);
          else if (!Utils.testVisibility(ch, item)) {
            o.toChar("They can't see it.", c);
            o.toChar("%N$n holds out $s hand to give you something, but you don't see anything.", ch, c);
          } else if (item.is(Item.CURSED)) {
            o.toChar("You attempt to give $i to, $n, but you can't let go of it.", c, ch, item);
            o.toChar("%N$n tries to give you $i, but $e can't let go of it.", ch, c, item);
          } else
            try {
              ch.addToInv(item);
              c.inv.remove(item);
              o.toChar("You give $i to $n.", c, ch, item);
              o.toChar("%N$n gives you $i.", ch, c, item);
              o.toRoom(c.location.room, "%N$n gives $N $i.", ch, c, item);
            } catch (TooHeavyException e) {
              o.toChar("You try to give $i to $N, but it's too heavy for them.", c, ch, item);
              o.toChar("%N$n tries to give you $i, but it's too heavy for you.", ch, c, item);
            } catch (NoSpaceException e) {
              o.toChar("You try to give $i to $N, but their hands are full.", c, ch, item);
              o.toChar("%N$n tries to give you $i, but your hands are full.", ch, c, item);
            }
        }
      }
    }
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: give <item> to <char>";
  }
}

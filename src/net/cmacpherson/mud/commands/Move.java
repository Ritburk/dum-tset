package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Move implements Command {

  private Direction dir;

  private static final String[] oppStrings = new String[] {
    "the south",
    "the north",
    "the west",
    "the east",
    "above",
    "below"
  };
  
  public Move(Direction dir) {
    this.dir = dir;
  }
  
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (dir != null) {
      Exit e = c.location.room.exits[dir.ordinal()];
      Door d = null;
      if (e instanceof Door)
        d = (Door)e;
      if (e == null)
        o.toChar("You can't go that direction.", c);
      else
        if (d == null)
          move(c, c.location.room, e.target.room, o);
        else
          if (d.isClosed())
            //if player is translucent then test passdoor
            o.toChar("The door is closed.", c);
          else
            move(c, c.location.room, d.target.room, o);
    } else {
      if (line == null) {
        o.toChar("Move which direction?", c);
        return true;
      }
      for (Direction d : Direction.values())
        if (line.regionMatches(true, 0, d.name(), 0, line.length()))
          dir = d;
      if (dir == null) {
        o.toChar("Move which direction?", c);
        return true;
      }
      execute(c, cmd, line, o);
      dir = null;
    }
    return true;
  }
  
  private void move(Character c, Room from, Room to, Output o) {
    from.chars.remove(c);
    o.toChar("You leave " + dir.name().toLowerCase() + ".", c);
    o.toRoom(from, "%N$n leaves " + dir.name().toLowerCase() + ".", c);
    o.toRoom(to, "%N$n arrives from " + oppStrings[dir.ordinal()] + ".", c);
    to.chars.add(c);
    c.location = to.location;
    c.force("look", o, false);
    checkFollowers(from.chars.toArray(new Character[] {}), c, from, to, o);
  }
  
  private void checkFollowers(Character[] check, Character c, Room from, Room to, Output o) {
    for (Character ch : check) {
      if (c.followers.contains(ch)) {
        if (ch.status == Character.Status.STANDING) {
          from.chars.remove(ch);
          o.toChar("You follow $n " + dir.name().toLowerCase() + ".", ch, c);
          o.toRoom(from, "$n follows $N " + dir.name().toLowerCase() + ".", c, ch);
          o.toRoom(to, "$n arrives from " + oppStrings[dir.ordinal()] + ".", ch);
          to.chars.add(ch);
          ch.location = to.location;
          ch.force("look", o, false);
          checkFollowers(check, ch, from, to, o);
        } else {
          o.toChar("$n fails to follow you.", c, ch);
          if (ch.status != Character.Status.SLEEPING)
            o.toChar("You are unable to follow $n " + dir.name().toLowerCase() + ".", ch, c);
        }
      }
    }
  }
  
  @Override
  public String getHelp() {
    return "Syntax: move <direction>%N" +
           "        <direction>";
  }
}

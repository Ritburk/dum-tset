package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Scan implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    Direction[] dirs = null;
    String dir = "";
    if (line != null)
      dir = Utils.readArg(line)[0];
    if (dir.equals(""))
      dirs = Direction.values();
    else {
      for (Direction d : Direction.values())
        if (dir.regionMatches(true,  0,  d.name(),  0, dir.length()))
          dirs = new Direction[] {d};
      if (dirs == null) {
        o.toChar("Scan which direction?", c);
        return true;
      }
      boolean hasExits = false;
      for (int i = 0; i < dirs.length; i++)
        if (c.location.room.exits[dirs[i].ordinal()] != null &&
            !c.location.room.exits[dirs[i].ordinal()].isHidden)
          hasExits = true;
      if (!hasExits) {
        o.toChar("There is no exit that direction.", c);
        return true;
      }
    }
    o.toChar("You scan your surroundings:", c);
    for (Direction d : dirs) {
      Exit e = c.location.room.exits[d.ordinal()];
      if (e != null &&
          !e.isHidden) {
        o.toChar(d.name() + ":", c);
        if (e instanceof Door &&
            ((Door)e).isClosed()) {
          o.toChar("  the door is closed.", c);
          continue;
        }
        boolean something = false;
        for (Character ch : e.target.room.chars)
          if (Utils.testVisibility(c, ch)) {
            o.toChar("  " + ch.displayName(), c);
            something = true;
          }
        if (!something)
          o.toChar("  nothing is there.", c);
      }
    }
    o.toRoom(c.location.room, "%N$n scans his surrounds.", c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: scan%N" +
           "        scan <direction";
  }
}

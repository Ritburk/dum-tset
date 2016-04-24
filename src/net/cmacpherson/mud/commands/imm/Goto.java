package net.cmacpherson.mud.commands.imm;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Goto implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Where did you want to go?", c);
      return true;
    }
    try {
      long vnum = Long.parseLong(Utils.readArg(line)[0]);
      Room r = o.SERVER.rooms.get(vnum);
      if (r == null)
        o.toChar("Unable to find that room.", c);
      else {
        c.location.room.chars.remove(c);
        o.toRoom(c.location.room, "%N$n steps into a portal and vanishes.", c);
        c.location = r.location;
        o.toRoom(c.location.room, "%N$n steps out of a portal.", c);
        c.location.room.chars.add(c);
      }
    } catch (NumberFormatException e) {
      Object[] arg = Utils.parseInput(Utils.readArg(line)[0]);
      ArrayList<Keywords> keywords = new ArrayList<Keywords>();
      keywords.addAll(o.SERVER.loggedPCs);
      keywords.addAll(o.SERVER.mobs);
      Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (ch == null)
        o.toChar("Unable to find that character.", c);
      else {
        c.location.room.chars.remove(c);
        o.toRoom(c.location.room, "%N$n steps into a portal and vanishes.", c);
        c.location = ch.location;
        o.toRoom(c.location.room, "%N$n steps out of a portal.", c);
        c.location.room.chars.add(c);
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: goto <vnum>%N" +
           "        goto <char>";
  }
}

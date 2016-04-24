package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.*;
import net.cmacpherson.mud.Character;

public class InvalidCommand implements Command {
  @Override
  public boolean execute(Character c, String cmd, String args, Output o) {
    for (String[] social : Globals.SOCIALS) {
      if (cmd.regionMatches(true, 0, social[0], 0, cmd.length())) {
      //{social, from_no_arg, room_no_arg, from_one_arg, room_one_arg, to_one_arg, from_self, room_self}
        if (args == null) {
          o.toChar(social[1], c);
          if (!social[2].equals(""))
            o.toRoom(c.location.room, "%N" + social[2], c);
        } else {
          Object[] arg = Utils.parseInput(Utils.readArg(args)[0]);
          Character to = null;
          if (((String)arg[1]).regionMatches(true, 0, "self", 0, ((String)arg[1]).length()))
            arg[1] = c.name;
          ArrayList<Keywords> keywords = new ArrayList<Keywords>();
          for (Character ch : c.location.room.chars)
            if (Utils.testVisibility(c, ch))
              keywords.add(ch);
          if ((to = (Character)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}))) != null) {
            if (to.equals(c)) {
              o.toChar(social[6], c);
              o.toRoom(c.location.room, "%N" + social[7], c);
            } else {
              o.toChar(social[3], c);
              o.toRoom(c.location.room, "%N" + social[4], to, c);
              o.toChar("%N" + social[5], to, c);
            }
          } else
            o.toChar("To whom?", c);
        }
        return true;
      }
    }
    o.toChar("Invalid command.", c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return null;
  }
}

package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Wake implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      c.force("stand", null, false);
      return true;
    }
    ArrayList<Keywords> keywords = new ArrayList<Keywords>();
    for (Character ch : c.location.room.chars)
      if (Utils.testVisibility(c, ch))
        keywords.add(ch);
    Object[] arg = Utils.parseInput(Utils.readArg(line)[0]);
    Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
    if (ch == null)
      o.toChar("They aren't here.", c);
    else {
      if (ch.status != Character.Status.SLEEPING)
        o.toChar("They are already awake.", c);
      else {
        //TODO test target for no wake config
        ch.status = Character.Status.STANDING;
        o.toChar("You wake $n.", c, ch);
        o.toChar("%N$n wakes you.", ch, c);
        o.toRoom(c.location.room, "%N$n wakes $N.", ch, c);
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: wake%N" +
           "        wake <char>";
  }
}

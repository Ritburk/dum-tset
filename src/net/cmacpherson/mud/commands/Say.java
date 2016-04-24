package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Say implements Command {

  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null)
      o.toChar("Say what?", c);
    else {
      if (c.status == Character.Status.SLEEPING) {
        o.toChar("You mumble while you sleep, 'MmmmMmmm'", c);
        o.toRoom(c.location.room, "%N$N mumbles in their sleep, 'MmmMmm'", c);
      } else {
        o.toChar("You say '" + line + "'", c);
        o.toRoom(c.location.room, Globals.COLOR_SAY + "%N$n says '" + line + "'", c);
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: say <msg>";
  }
}

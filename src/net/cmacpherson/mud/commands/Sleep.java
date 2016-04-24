package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Sleep implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (c.status == Character.Status.SLEEPING)
      o.toChar("You are already sleeping.", c);
    else if (c.status == Character.Status.STANDING ||
               c.status == Character.Status.RESTING) {
      c.status = Character.Status.SLEEPING;
      o.toChar("You sleep.", c);
      o.toRoom(c.location.room, "%N$n lays down to sleep.", c);
    } else
      //shouldn't reach here but just in case
      o.toChar("You can't do that right now.", c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: sleep";
  }
}

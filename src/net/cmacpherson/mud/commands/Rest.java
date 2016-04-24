package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Rest implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (c.status == Character.Status.RESTING)
      o.toChar("You are already resting.", c);
    else if (c.status == Character.Status.SLEEPING) {
      c.status = Character.Status.RESTING;
      o.toChar("You wake up and rest.", c);
      o.toRoom(c.location.room, "%N$n wakes up and rests.", c);
    } else if (c.status == Character.Status.STANDING) {
      c.status = Character.Status.RESTING;
      o.toChar("You rest.", c);
      o.toRoom(c.location.room, "%N$n rests.", c);
    } else
      //shouldn't reach here but just in case
      o.toChar("You can't do that right now.", c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: rest";
  }
}

package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Stand implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (c.status == Character.Status.STANDING)
      o.toChar("You are already standing.", c);
    else if (c.status == Character.Status.SLEEPING) {
      c.status = Character.Status.STANDING;
      o.toChar("You wake and stand up.", c);
      o.toRoom(c.location.room, "%N$n wakes up and stands up.", c);
    } else if (c.status == Character.Status.RESTING) {
      c.status = Character.Status.STANDING;
      o.toChar("You stand up.", c);
      o.toRoom(c.location.room, "%N$n stands up.", c);
    } else
      //shouldn't reach here but just in case
      o.toChar("You can't do that right now.", c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: stand";
  }
}

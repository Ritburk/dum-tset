package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Yell implements Command {

  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null)
      o.toChar("Yell what?", c);
    else {
      o.toChar(Globals.COLOR_YELL + "You yell '" + line + "'", c);
      for (Character ch : c.location.area.chars())
        if (ch instanceof PC &&
            c != ch)
          o.toChar(Globals.COLOR_YELL + "%N$n yells '" + line + "'", ch, c);
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: yell <msg>";
  }
}

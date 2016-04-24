package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class GTell implements Command {

  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null)
      o.toChar("Tell the group what?", c);
    else {
      o.toChar(Globals.COLOR_CODES[((Color)c.config.channels[Config.Channel.GTELL.ordinal()][2]).ordinal()] + "You tell the group '" + line + "'", c);
      o.toGroup(c.group, Globals.COLOR_CODES[((Color)c.config.channels[Config.Channel.GTELL.ordinal()][2]).ordinal()] + "%N$n tells the group '" + line + "'", c);
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: gtell <msg>";
  }
}

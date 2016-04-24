package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Chat implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null)
      o.toChar("Chat what?", c);
    else {
      c.config.channels[Config.Channel.CHAT.ordinal()][1] = true;
      o.toChar(Globals.COLOR_CODES[((Color)c.config.channels[Config.Channel.CHAT.ordinal()][2]).ordinal()] + "You chat '" + line + "'", c);
      for (PC to : o.SERVER.loggedPCs)
        if ((boolean)to.config.channels[Config.Channel.CHAT.ordinal()][1] &&
            !to.equals(c))
          o.toChar(Globals.COLOR_CODES[((Color)to.config.channels[Config.Channel.CHAT.ordinal()][2]).ordinal()] + "%N$n chats '" + line + "'", to, c);
    }
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: chat <msg>";
  }
}

package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Reply implements Command {

  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Tell them what?", c);
      return true;
    }
    if (c.reply == null)
      o.toChar("You don't know who to reply to.", c);
    else if (c.reply instanceof PC &&
             ((PC)c.reply).linkdead)
      o.toChar("They are linkdead.", c);
    // TODO test channels
    else {
      String from = "You tell $n '" + line + "'";
      String to = "%N$n tells you '" + line + "'";
      if (c.reply.status == Character.Status.SLEEPING) {
        from = "$n is sleeping, but you tell them '" + line + "'";
        to = "%NYou dream of $n telling you '" + line + "'";
      }
      o.toChar(Globals.COLOR_CODES[((Color)c.config.channels[Config.Channel.TELL.ordinal()][2]).ordinal()] + from, c, c.reply);
      o.toChar(Globals.COLOR_CODES[((Color)c.reply.config.channels[Config.Channel.TELL.ordinal()][2]).ordinal()] + to, c.reply, c);
      c.reply.reply = c;
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: reply <msg>";
  }
}

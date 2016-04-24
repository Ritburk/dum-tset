package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Tell implements Command {

  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Tell who what?", c);
      return true;
    }
    String[] args = Utils.readArg(line);
    Object[] arg = Utils.parseInput(args[0]);
    ArrayList<Keywords> keywords = new ArrayList<Keywords>();
    for (PC ch : o.SERVER.loggedPCs)
      if (Utils.testVisibility(c, ch))
        keywords.add(ch);
    if ((int)arg[0] == 0)
      o.toChar("You can't send a tell to more than one person.", c);
    else {
      PC to = (PC)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (to == null)
        o.toChar("They aren't here.", c);
      else if (to.linkdead)
        o.toChar("They are linkdead.", c);
      else if (args[1].equals(""))
        o.toChar("What did you want to tell them?", c);
      // TODO test channels
      else {
        String from = "You tell $n '" + args[1] + "'";
        String str = "%N$n tells you '" + args[1] + "'";
        if (to.status == Character.Status.SLEEPING) {
          from = "$n is sleeping, but you tell them '" + args[1] + "'";
          str = "%NYou dream of $n telling you '" + args[1] + "'";
        }
        o.toChar(Globals.COLOR_CODES[((Color)c.config.channels[Config.Channel.TELL.ordinal()][2]).ordinal()] + from, c, to);
        o.toChar(Globals.COLOR_CODES[((Color)to.config.channels[Config.Channel.TELL.ordinal()][2]).ordinal()] + str, to, c);
        to.reply = c;
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: tell <char> <msg>";
  }
}

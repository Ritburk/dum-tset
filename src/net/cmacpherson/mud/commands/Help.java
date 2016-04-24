package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Help implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      //generic help output
      o.toChar(getHelp(), c);
      return true;
    }
    String help = null;
    for (Object[] obj : c.COMMAND_MAP) {
      if (c.level <= (int)obj[2] &&
          line.regionMatches(true, 0, (String)obj[0], 0, line.length()))
        help = ((Command)obj[1]).getHelp();
    }
    if (help == null) {
      //check topics
      
      //if nothing found
      o.toChar("Unable to find help for {" + line + "}.", c);
    } else
      o.toChar(help, c);
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: help <command>%N" +
           "        help <topic>";
  }
}

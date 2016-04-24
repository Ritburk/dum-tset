package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.*;
import net.cmacpherson.mud.Character;

public class Commands implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    ArrayList<String> commands = new ArrayList<String>();
    int maxLength = 0;
    for (Object[] command : c.COMMAND_MAP) {
      if (c.level >= (int)command[2]) {
        commands.add((String)command[0]);
        if (((String)command[0]).length() > maxLength)
          maxLength = ((String)command[0]).length();
      }
    }
    String chart = Utils.createColumns(maxLength, commands.toArray(new String[] {}));
    o.toChar(chart, c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: commands";
  }
}

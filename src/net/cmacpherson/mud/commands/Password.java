package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Password implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar(getHelp(), c);
      return true;
    }
    String[] args = Utils.splitInput(line);
    if (args.length == 3) {
      if (args[1].equals(args[2]) &&
          ((PC)c).password.equals(MD5.encode(args[0]))) {
        ((PC)c).password = MD5.encode(args[0]);
        o.toChar("Password changed.", c);
      } else
        o.toChar("|r|Unable to change password.", c);
    } else
      o.toChar("|r|Unable to change password.", c);
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: password <old> <new> <new>";
  }
}

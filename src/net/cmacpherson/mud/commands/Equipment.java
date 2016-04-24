package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Equipment implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    o.toChar("You are wearing:", c);
    o.toChar(Utils.showEQ(c, c), c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: equipment";
  }
}

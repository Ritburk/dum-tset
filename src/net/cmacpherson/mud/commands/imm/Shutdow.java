package net.cmacpherson.mud.commands.imm;

import net.cmacpherson.mud.*;
import net.cmacpherson.mud.Character;

public class Shutdow implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    o.toChar("|r|You must type the whole word.", c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: shutdown";
  }
}

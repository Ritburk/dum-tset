package net.cmacpherson.mud.commands.imm;

import net.cmacpherson.mud.*;
import net.cmacpherson.mud.Character;

public class Shutdown implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    o.toChar("Shutdown process initiated...", c);
    o.SERVER.shutdown();
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: shutdown";
  }
}

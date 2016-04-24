package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Qui implements Command {
  @Override
  public boolean execute(Character c, String cmd, String args, Output o) {
    o.toChar("|r|You must type it out if you intend to leave this world.", c);
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: quit";
  }
}

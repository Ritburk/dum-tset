package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Close implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    //TODO close
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: close";
  }
}

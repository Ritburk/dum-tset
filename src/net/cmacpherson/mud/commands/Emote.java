package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Emote implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Emote what?", c);
      return true;
    }
    if (Utils.justLetters(line.substring(0, 1)))
      line = " " + line;
    o.toChar("$n" + line, c);
    o.toRoom(c.location.room, "%N$n" + line, c);
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: emote <msg>";
  }
}

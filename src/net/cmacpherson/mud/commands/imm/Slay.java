package net.cmacpherson.mud.commands.imm;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Slay implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Who did you want to slay?", c);
      return true;
    }
    Object[] arg = Utils.parseInput(Utils.readArg(line)[0]);
    ArrayList<Keywords> keywords = new ArrayList<Keywords>();
    keywords.addAll(c.location.room.chars);
    keywords.addAll(o.SERVER.loggedPCs);
    keywords.addAll(o.SERVER.mobs);
    Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
    if (ch == null)
      o.toChar("Unable to find that character.", ch);
    else {
      //TODO kill char/remove from combat
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: slay <char>";
  }
}

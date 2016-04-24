package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Where implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("You are in " + c.location.area.toString(), c);
      o.toChar("Players near you:", c);
      for (Character ch : c.location.area.chars())
        if (ch instanceof PC) {
          String s = "  " + ch.name;
          while (s.length() < 15)
            s += " ";
          o.toChar(s + "in " + ch.location.room.name, c, ch);
        }
    } else {
      ArrayList<Keywords> keywords = new ArrayList<Keywords>();
      for (Character ch : c.location.area.chars())
        if (Utils.testVisibility(c, ch))
          keywords.add(ch);
      Object[] arg = Utils.parseInput(line);
      Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (ch == null)
        o.toChar("You find no one.", c);
      else {
        o.toChar("You sense them in your surroundings:", c);
        o.toChar("  $n in " + ch.location.room.name, c, ch);
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: where%N" +
           "        where <char>";
  }
}

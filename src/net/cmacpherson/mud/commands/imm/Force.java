package net.cmacpherson.mud.commands.imm;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Force implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Force who to do what?", c);
      return true;
    }
    String[] args = Utils.readArg(line);
    Object[] arg = Utils.parseInput(args[0]);
    if ((int)arg[0] == 0) {
    	if (args[1].equals(""))
    	  o.toChar("What did you want them to do ?", c);
    	else {
    	  Character[] chars = Utils.filter(o.SERVER.loggedPCs.toArray(new Character[] {}), new Filter[] {new Filter(-1, Globals.IMM_LEVEL - 1)});
    	  for (Character ch : chars)
    	    ch.forceOnThread(args[1], c);
    	  o.toChar("Done.", c);
    	}
    } else {
      ArrayList<Keywords> keywords = new ArrayList<Keywords>();
      for (Character ch : c.location.room.chars)
        if (c instanceof Mob)
          keywords.add(ch);
      for (Character ch : o.SERVER.loggedPCs)
        keywords.add(ch);
      Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (ch != null) {
        if (args[1].equals(""))
          o.toChar("What did you want them to do?", c);
        else {
          if (ch.level < c.level) {
            ch.forceOnThread(args[1], c);
            o.toChar("Done.", c);
          } else
            o.toChar("You are not high enough in level to force them to do that.", c);
        } 
      } else
        o.toChar("You can't find them.", c);
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: force <char> <command>";
  }
}

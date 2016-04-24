package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Follow implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Follow who?", c);
      return true;
    }
    Object[] arg = Utils.parseInput(Utils.readArg(line)[0]);
    if (((String)arg[1]).regionMatches(true, 0, "self", 0, ((String)arg[1]).length())) {
      ungroup(c, o);
      c.follow(c);
      return true;
    }
    Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], c.location.room.chars.toArray(new Keywords[] {}));
    if (ch == null) {
      o.toChar("You can't follow someone who isn't here.", c);
      return true;
    } else if (c.equals(ch)) {
      c.force("follow self", null, false);
      return true;
    } else {
      ungroup(c, o);
      o.toChar("%N$n is now following you.", ch, c);
      o.toChar("You are now following $n.", c, ch);
      c.follow(ch);
    }
    return true;
  }
  
  private void ungroup(Character c, Output o) {
    if (c.following != null) {
      boolean grouped = false;
      if (grouped = c.group.members.remove(c)) {
        o.toGroup(c.group, "%N$n leaves the group.", c);
        o.toChar("You leave $n's group.", c, c.group.leader);
        c.group = c.myGroup;
      }
      o.toChar((grouped ? "" : "%N") + "$n stops following you.", c.following, c);
      o.toChar("You stop following $n.", c, c.following);
    }
  }
  
  @Override
  public String getHelp() {
    return "Syntax: follow <char>";
  }
}

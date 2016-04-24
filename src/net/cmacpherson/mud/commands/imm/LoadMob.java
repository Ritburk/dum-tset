package net.cmacpherson.mud.commands.imm;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class LoadMob implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Load what vnum?", c);
      return true;
    }
    String str = Utils.readArg(line)[0];
    try {
      long vnum = Long.parseLong(str);
      MobProto proto = o.SERVER.mobBank.get(vnum);
      if (proto == null)
        o.toChar("|r|Unable to find proto mob.", c);
      else {
        Mob mob = proto.generate(-1);
        c.location.room.chars.add(mob);
        o.SERVER.mobs.add(mob);
        o.toChar("Success.", c);
      }
    } catch (NumberFormatException e) {
      o.toChar("|r|Invalid vnum format.", c);
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: loadmob <vnum>";
  }
}

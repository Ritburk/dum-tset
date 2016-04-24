package net.cmacpherson.mud.commands.imm;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class LoadItem implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null)
      o.toChar("Load what vnum?", c);
    else {
      String str = Utils.readArg(line)[0];
      try {
        long vnum = Long.parseLong(str);
        ItemProto proto = o.SERVER.itemBank.get(vnum);
        if (proto == null)
          o.toChar("|r|Unable to find proto item.", c);
        else {
          Item item = proto.generate(-1);
          try {
            c.addToInv(item, true);
          } catch (TooHeavyException e) {            
          } catch (NoSpaceException e) {}
          o.toChar("Success.", c);
        }
      } catch (NumberFormatException e) {
        o.toChar("|r|Invalid vnum format.", c);
      }
    }
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: loaditem <vnum>";
  }
}

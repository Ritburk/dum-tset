package net.cmacpherson.mud.commands.imm;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Gift implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Give a gift of gold to who?", c);
      return true;
    }
    String[] args = Utils.splitInput(line);
    Object[] arg = Utils.parseInput(args[0]);
    int n = 1000;
    if (args.length > 1) {
      try {
        n = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
        o.toChar("How much gold did you want to gift to them?", c);
        return true;
      }
    }
    Item gold = ItemProto.generateGold(n);
    if ((int)arg[0] == 0) {
      Character[] chars = Utils.cast(Utils.findMatches((String)arg[1], c.location.room.chars.toArray(new Keywords[] {})), Character.class);
      if (chars.length == 0)
        o.toChar("You don't see them here.", c);
      else {
        for (Character ch : chars) {
          try {
            ch.addToInv(gold);
            o.toChar("%N$n has gifted to you $i.", ch, c, gold);
          } catch (TooHeavyException e) {
            o.toChar("$n can't hold the weight.", c, ch);
            o.toChar("%N$n tries to give you $i, but you can't hold the weight.", ch, ch, gold);
            } catch (NoSpaceException e) {}
        }
        o.toChar("You have been very generous.", c);
      }
      return true;
    }
    Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], c.location.room.chars.toArray(new Keywords[] {}));
    if (ch == null)
      o.toChar("You don't see them here.", c);
    else {
      try {
        ch.addToInv(gold);
        o.toChar("You gift $i to $n.", c, ch, gold);
        o.toChar("%N$n has gifted to you $i.", ch, c, gold);
        o.toRoom(c.location.room, "%N$n gives some gold to $N.", ch, c);
      } catch (TooHeavyException e) {
        o.toChar("They can't hold the weight.", c);
        o.toChar("%N$n tries to give you $i, but you can't hold the weight.", ch, c, gold);
        o.toRoom(c.location.room, "%N$n tries to give $N some gold, but they can't hold the weight.", ch, c);
      } catch (NoSpaceException e) {}
    }
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: gift <char>%N" +
           "        gift <char> <amount>";
  }
}

package net.cmacpherson.mud.commands.imm;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Transfer implements Command {
  //TODO update this to pull guys out of combat
  //TODO update to accept all
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Who did you want to transfer?", c);
      return true;
    }
    String[] args = Utils.splitInput(line);
    Object[] arg = Utils.parseInput(Utils.readArg(line)[0]);
    ArrayList<Keywords> keywords = new ArrayList<Keywords>();
    keywords.addAll(o.SERVER.loggedPCs);
    keywords.addAll(o.SERVER.mobs);
    Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
    if (ch == null)
      o.toChar("Unable to find that character.", c);
    else {
      if (args.length > 1) {
        if (args[1].equalsIgnoreCase("to")) {
          if (args.length > 2) {
            try {
              long vnum = Long.parseLong(args[2]);
              Room r = o.SERVER.rooms.get(vnum);
              if (r == null)
                o.toChar("Unable to find that room.", c);
              else {
                ch.location.room.chars.remove(ch);
                o.toRoom(ch.location.room,  "%N$n is sucked into a portal.", ch);
                ch.location = r.location;
                o.toRoom(ch.location.room, "%N$n is spit out of a portal.", ch);
                ch.location.room.chars.add(ch);
              }
            } catch (NumberFormatException e) {
              Object[] arg2 = Utils.parseInput(args[2]);
              Character ch2 = (Character)Utils.findMatch((int)arg2[0], (String)arg2[1], keywords.toArray(new Keywords[] {}));
              if (ch2 == null)
                o.toChar("Unable to find the target location.", c);
              else {
                ch.location.room.chars.remove(ch);
                o.toRoom(ch.location.room, "%N$n is sucked into a portal.", ch);
                ch.location = ch2.location;
                o.toRoom(ch.location.room, "%N$n is spit out of a portal.", ch);
                ch.location.room.chars.add(ch);
              }
            }
          } else
            o.toChar("Where did you want to send them?", c);
        } else
          o.toChar(getHelp(), c);
      } else {
        ch.location.room.chars.remove(ch);
        o.toRoom(ch.location.room, "%N$n is sucked into a portal.", ch);
        ch.location = c.location;
        o.toRoom(ch.location.room, "%N$n is spit out of a portal.", ch);
        ch.location.room.chars.add(ch);
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: transfer <char>%N" +
           "        transfer <char> to <vnum>%N" +
           "        transfer <char> to <char>";
  }
}

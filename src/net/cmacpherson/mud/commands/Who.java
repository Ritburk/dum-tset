package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Who implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      display(Utils.order(Utils.filter(o.SERVER.loggedPCs.toArray(new Character[] {}), new Filter[] {new Filter(c)})), c, o);
    } else {
      String[] args = Utils.splitInput(line);
      if (args[0].equalsIgnoreCase("is")) {
        if (args.length == 1)
          o.toChar("Who are you looking for?", c);
        else
          display(Utils.order(Utils.filter(o.SERVER.loggedPCs.toArray(new Character[] {}), new Filter[] {new Filter(args[1])})), c, o);
      } else {
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter(c));
        Race.Abr race = null;
        int min = -1;
        int max = -1;
        for (String arg : args) {
          race = Race.valueOf(arg);
          if (min == -1)
            try {
              min = Integer.parseInt(arg);
            } catch (NumberFormatException e) {}
          else if (max == -1)
            try {
              max = Integer.parseInt(arg);
            } catch (NumberFormatException e) {}
        }
        if (race == null)
          filters.add(new Filter(race));
        if (min != -1)
          filters.add(new Filter(min, max));
        display(Utils.order(Utils.filter(o.SERVER.loggedPCs.toArray(new Character[] {}), filters.toArray(new Filter[] {}))), c, o);
      }
    }
    return true;
  }
  
  private void display(Character[] chars, Character c, Output o) {
    o.toChar("You search the universe looking for people...", c);
    if (chars.length == 0)
      o.toChar("You find no one.", c);
    else {
      //[### RAC CLA] ch.displayName()
      for (Character ch : chars) {
        String s = "[";
        String str = "" + ch.level;
        while (str.length() < 3)
          str = " " + str;
        s += str + " " + ch.race.abr.name() + " " + ch.skills.getClassAbr() + "] ";
        if (ch instanceof PC)
          s += ((PC)ch).displayName();
        else
          s += ch.displayName();
        o.toChar(s, c);
      }
    }
  }

  @Override
  public String getHelp() {
    return "Syntax: who%N" +
           "        who [<race>] [<level range>]%N" +
           "        who is <name>%N" +
           "   level range syntax: <min> [<max>]";
  }
}

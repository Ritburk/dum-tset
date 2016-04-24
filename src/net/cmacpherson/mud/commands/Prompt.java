package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Prompt implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("What did you want to do with your prompt?", c);
      return true;
    }
    if (c instanceof PC) {
      PC pc = (PC)c;
      String arg = Utils.readArg(line)[0];
      if (arg.equalsIgnoreCase("reset")) {
        pc.prompt = Globals.STARTING_PROMPT;
        o.toChar("Prompt reset.", c);
      } else if (arg.equalsIgnoreCase("on")) {
        pc.config.showPrompt = true;
        o.toChar("Prompt turned on.", c);
      } else if (arg.equalsIgnoreCase("off")) {
        pc.config.showPrompt = false;
        o.toChar("Prompt turned off.", c);
      } else {
        pc.prompt = line;
        o.toChar("Prompt set.", c);
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: prompt <prompt>%N" +
           "        prompt reset%N" +
           "        prompt <on/off>%N" +
           "  codes: (place a % before the code)%N" +
           "h - hp      H - max hp%N" +
           "m - mana    M - max mana%N" + 
           "v - moves   V - max moves%N" +
           "T - tnl     N - newline%N" + 
           "R - room";
  }
}

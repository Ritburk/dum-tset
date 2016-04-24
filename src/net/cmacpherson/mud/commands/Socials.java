package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Socials implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    ArrayList<String> socials = new ArrayList<String>();
    int maxLength = 0;
    for (String[] social : Globals.SOCIALS) {
      socials.add(social[0]);
      if (social[0].length() > maxLength)
        maxLength = social[0].length();
    }
    String chart = Utils.createColumns(maxLength, socials.toArray(new String[] {}));
    o.toChar(chart, c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: socials%N" +
           "        <social>%N" +
           "        <social> <char>";
  }
}

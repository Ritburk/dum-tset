package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Areas implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    ArrayList<String> list = new ArrayList<String>();
    int maxWidth = 0;
    if (line == null) {
      for (Area area : c.location.plane.areas) {
        String s = area.toString();
        if (s.length() > maxWidth)
          maxWidth = s.length();
        list.add(s);
      }
    } else {
      try {
        int level = Integer.parseInt(line);
        for (Area area : c.location.plane.areas)
          if (level >= area.levelRange[0] &&
              level <= area.levelRange[1]) {
            String s = "{ " + (area.levelRange[0] < 9 ? " " : "") +
                       area.levelRange[0] +
                       "  " + (area.levelRange[1] < 9 ? " " : "") +
                       area.levelRange[1] + " } " + area.name;
            if (s.length() > maxWidth)
              maxWidth = s.length();
            list.add(s);
          }
      } catch (NumberFormatException e) {
        if (line.regionMatches(true, 0, "common", 0, line.length())) {
          for (Area area : c.location.plane.areas)
            if (area.isCommon()) {
              String s = area.toString();
              list.add(s);
              if (s.length() > maxWidth)
                maxWidth = s.length();
            }
        } else {
          o.toChar("Which areas did you want to list?", c);
          return true;
        }
      }
    }
    String chart = Utils.createColumns(maxWidth, list.toArray(new String[] {}));
    o.toChar(chart, c);
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: areas%N" +
           "        areas <level>%N" +
           "        areas common";
  }
}

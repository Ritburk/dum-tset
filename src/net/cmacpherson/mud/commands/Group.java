package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Group implements Command {

  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
/*
===<Leader's name> Group=======================================================
LEVEL    NAME         STATUS            HP          MANA         MOVES      TNL
===============================================================================
### CLE  Creighton    CONCENTRATING #####/#####  #####/#####  #####/#####  ####
===============================================================================
*/
    if (line == null) {
      String s = "=== " + c.group.leader.name + "'s Group ";
      while (s.length() < 79)
        s += "=";
      s += "%NLEVEL    NAME         STATUS            HP          MANA         MOVES      TNL%N";
      s += "===============================================================================%N";
      for (Character ch : c.group.members) {
        String str = "";
        String st = "" + ch.level;
        while (st.length() < 3)
          st = " " + st;
        str += st + " " + ch.skills.getClassAbr() + " " + (ch == c.group.leader ? "*" : " ") + ch.name;
        while (str.length() < 22)
          str += " ";
        str += ch.status.name();
        while (str.length() < 36)
          str += " ";
        st = "" + ch.hp;
        while (st.length() < 5)
          st = " " + st;
        str += st + "/";
        st = "" + ch.mhp;
        while (st.length() < 5)
          st += " ";
        str += st + "  ";
        st = "" + ch.m;
        while (st.length() < 5)
          st = " " + st;
        str += st + "/";
        st = "" + ch.mm;
        while (st.length() < 5)
          st += " ";
        str += st + "  ";
        st = "" + ch.mv;
        while (st.length() < 5)
          st = " " + st;
        str += st + "/";
        st = "" + ch.mmv;
        while (st.length() < 5)
          st += " ";
        str += st + " ";
        if (ch instanceof PC) {
          st = "" + ((PC)c).tnl();
          while (st.length() < 5)
            st = " " + st;
          str += st;
        }
        s += str + "%N";
      }
      s += "===============================================================================";
      o.toChar(s, c);
    } else {
      ArrayList<Keywords> keywords = new ArrayList<Keywords>();
      for (Character ch : c.location.room.chars)
        if (Utils.testVisibility(c, ch))
          keywords.add(ch);
      Object[] arg = Utils.parseInput(Utils.readArg(line)[0]);
      Character ch = (Character)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (ch == null)
        o.toChar("They aren't here.", c);
      else if (ch.following != c)
        o.toChar("They aren't following you.", c);
      else if (c.following != null)
        o.toChar("You can't add people to a group you aren't leading.", c);
      else {
        o.toChar("%NYou join $n's group.", ch, c);
        o.toChar("$n joins the group.", c, ch);
        o.toGroup(c.group, "%N$n joins the group.", c, ch);
        c.group.members.add(ch);
        ch.group = c.group;
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: group%N" +
           "        group <char>";
  }
}

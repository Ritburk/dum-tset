package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Score implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
/*
===============================================================================
Creighton the 1st level Human Peasant
===============================================================================
 STR: 10(10)    FORT:     0    HP:       30/   30
 DEX: 10(10)    REFLEX:   0    MANA:    100/  100
 CON: 10(10)    WILL:     0    MOVES:    50
 INT: 10(10)    ===============================================================
 WIS: 10(10)    AC:       0    ALIGNMENT: #####    DEATHS: 0
 CHA: 10(10)    BLOCK:   0%    GOLD:      100
===============================================================================
  HR:   0     SPELL HR:  10    EXP:    0
  DR:   0     SPELL DR:   0    TNL:    1000
===============================================================================
*/
    String s = "===============================================================================%N";
    s += " " + c.name + " the " + c.level;
    if (c.level == 11 ||
        c.level == 12 ||
        c.level == 13)
      s += "th";
    else
      switch (c.level % 10) {
      case 1:
        s += "st";
        break;
      case 2:
        s += "nd";
        break;
      case 3:
        s += "rd";
      default:
        s += "th";
      }
    String race = c.race.name.name();
    s += " level " + race.substring(0, 1) + race.substring(1).toLowerCase() + " " + c.skills.getClassName() + "%N";
    s += "===============================================================================%N";
    s += " STR: " + (c._str < 10 ? " " : "") + c._str + "(" + (c.cSTR < 10 ? " " : "") + c.cSTR + ")    FORT:   ";
    String str = "" + c.fort;
    while (str.length() < 3)
      str = " " + str;
    s += str + "    HP:    ";
    str = "" + c.hp;
    while (str.length() < 5)
      str = " " + str;
    s += str + "/";
    str = "" + c.mhp;
    while (str.length() < 5)
      str = " " + str;
    s += str + "%N DEX: " + (c._dex < 10 ? " " : "") + c._dex + "(" + (c.cDEX < 10 ? " " : "") + c.cDEX + ")    REFLEX: ";
    str = "" + c.ref;
    while (str.length() < 3)
      str = " " + str;
    s += str + "    MANA:  ";
    str = "" + c.m;
    while (str.length() < 5)
      str = " " + str;
    s += str + "/";
    str = "" + c.mm;
    while (str.length() < 5)
      str = " " + str;
    s += str + "%N CON: " + (c._con < 10 ? " " : "") + c._con + "(" + (c.cCON < 10 ? " " : "") + c.cCON + ")    WILL:   ";
    str = "" + c.will;
    while (str.length() < 3)
      str = " " + str;
    s += str + "    MOVES: ";
    str = "" + c.mv;
    while (str.length() < 5)
      str = " " + str;
    s += str + "%N INT: " + (c._int < 10 ? " " : "") + c._int + "(" + (c.cINT < 10 ? " " : "") + c.cINT + ")    ";
    s += "===============================================================%N";
    s += " WIS: " + (c._wis < 10 ? " " : "") + c._wis + "(" + (c.cWIS < 10 ? " " : "") + c.cWIS + ")    AC:     ";
    str = "" + c.ac;
    while (str.length() < 3)
      str = " " + str;
    s += str + "    ALIGNMENT: ";
    str = "" + c.align;
    while (str.length() < 5)
      str = " " + str;
    s += str + "    DEATHS: ";
    if (c instanceof PC)
      s += ((PC)c).deaths;
    s += "%N CHA: " + (c._cha < 10 ? " " : "") + c._cha + "(" + (c.cCHA < 10 ? " " : "") + c.cCHA + ")    BLOCK:  ";
    str = "" + (int)(c.blockPercent() * 100) + "%";
    while (str.length() < 3)
      str = " " + str;
    s += str + "    GOLD:      " + c.gold + "%N";
    s += "===============================================================================%N";
    s += "  HR: ";
    str = "" + c.hr;
    while (str.length() < 3)
      str = " " + str;
    s += str + "     SPELL HR: ";
    str = "" + c.shr;
    while (str.length() < 3)
      str = " " + str;
    s += str + "    EXP:    ";
    if (c instanceof PC)
      s += ((PC)c).exp;
    s += "%N  DR: ";
    str = "" + c.dr;
    while (str.length() < 3)
      str = " " + str;
    s += str + "     SPELL DR: ";
    str = "" + c.sdr;
    while (str.length() < 3)
      str = " " + str;
    s += str + "    TNL:    ";
    if (c instanceof PC)
      s += ((PC)c).tnl();
    s += "%N===============================================================================";
    o.toChar(s, c);
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: score";
  }
}

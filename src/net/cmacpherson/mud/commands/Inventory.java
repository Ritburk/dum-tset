package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.*;
import net.cmacpherson.mud.Character;

public class Inventory implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    Object[] obj = Utils.condenseItems(c.inv, c);
    int[] counts = (int[])obj[0];
    Item[] items = (Item[])obj[1];
    o.toChar("You are carrying:", c);
    if (items.length == 0)
      o.toChar("      (nothing)", c);
    else
      for (int i = 0; i < items.length; i++)
        if (counts[i] > 1) {
          String prefix = "(" + counts[i] + ") ";
          while (prefix.length() < 6)
            prefix = " " + prefix;
          o.toChar(prefix + items[i].name, c);
        } else
          o.toChar("      " + items[i].name, c);
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: inventory";
  }
}

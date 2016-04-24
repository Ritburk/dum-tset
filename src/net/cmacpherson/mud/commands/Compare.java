package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Compare implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Compare what?", c);
      return true;
    }
    String[] args = Utils.splitInput(line);
    Object[] arg = Utils.parseInput(args[0]);
    ArrayList<Keywords> keywords = new ArrayList<Keywords>();
    for (Item item : c.inv)
      if (!item.isEquipped() &&
          Utils.testVisibility(c, item))
        keywords.add(item);
    Item item1 = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
    if (item1 == null)
      o.toChar("What did you want to compare?", c);
    else if (args.length == 1) {
      Item item2 = null;
      switch (item1.slot) {
      case RING:
        item2 = c.eq[Character.EQSlot.RING1.ordinal()];
        break;
      case WRIST:
        item2 = c.eq[Character.EQSlot.WRIST1.ordinal()];
        break;
      default:
        item2 = c.eq[Character.EQSlot.valueOf(item1.slot.name()).ordinal()];
      }
      if (item2 == null)
        o.toChar("You aren't wearing anything to compare that to.", c);
      else {
        if (item1.slot != item2.slot)
          o.toChar("You aren't able to compare those items.", c);
        else
          o.toChar(compare(c, item1, item2), c);
      }
    } else if (args.length == 3 &&
               args[1].equalsIgnoreCase("to")) {
      keywords.clear();
      for (Item item : c.inv)
        if (item.isEquipped() &&
            Utils.testVisibility(c, item))
          keywords.add(item);
      for (Item item : c.inv)
        if (!item.isEquipped() &&
            Utils.testVisibility(c, item))
          keywords.add(item);
      arg = Utils.parseInput(args[2]);
      Item item2 = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (item2 == null)
        o.toChar("You can't find what you wanted to compare that to.", c);
      else if (item1.slot != item2.slot)
        o.toChar("You aren't able to compare those items.", c);
      else
        o.toChar(compare(c, item1, item2), c, item1, item2);
    } else
      o.toChar("Syntax: compare <item> to <item>", c);
    return true;
  }
  
  public String compare(Character c, Item item1, Item item2) {
    String str = "You compare $i in your inventory%N" +
                 "to $I " + (item2.isEquipped() ? "that you are wearing:" : "in your inventory:");
    String s = "";
    if (item1.is(Item.WEAPON) &&
        item2.is(Item.WEAPON)) {
      int min1 = (int)(item1.damage.min() * Utils.mod(c.cSTR)) + item1.modDR + item1.modEDR;
      int min2 = (int)(item2.damage.min() * Utils.mod(c.cSTR)) + item2.modDR + item2.modEDR;
      int max1 = (int)(item1.damage.max() * Utils.mod(c.cSTR)) + item1.modDR + item1.modEDR;
      int max2 = (int)(item2.damage.max() * Utils.mod(c.cSTR)) + item2.modDR + item2.modEDR;
      if (item1.is(Item.BOW) &&
          item2.is(Item.BOW)) {
        min1 = (int)(item1.damage.min() * Utils.mod(c.cDEX)) + item1.modDR + item1.modEDR;
        min2 = (int)(item2.damage.min() * Utils.mod(c.cDEX)) + item2.modDR + item2.modEDR;
        max1 = (int)(item1.damage.max() * Utils.mod(c.cDEX)) + item1.modDR + item1.modEDR;
        max2 = (int)(item2.damage.max() * Utils.mod(c.cDEX)) + item2.modDR + item2.modEDR;
        if (item1.drawSTR > item2.drawSTR)
          s += "%N  $i will be more difficult to draw than $I,";
        else if (item1.drawSTR == item2.drawSTR)
          s += "%N  $i and $I will feel the same while drawing,";
        else
          s += "%N  $i will be less difficult to draw than $I,";
      } else {
        if (item1.is(Item.TWO_HANDED)) {
          min1 = (int)(item1.damage.min() * (Utils.mod(c.cSTR) + 0.2)) + item1.modDR + item1.modEDR;
          max1 = (int)(item1.damage.max() * (Utils.mod(c.cSTR) + 0.2)) + item1.modDR + item1.modEDR;
        } 
        if (item2.is(Item.TWO_HANDED)) {
          min2 = (int)(item2.damage.min() * (Utils.mod(c.cSTR) + 0.2)) + item2.modDR + item2.modEDR;
          max2 = (int)(item2.damage.max() * (Utils.mod(c.cSTR) + 0.2)) + item2.modDR + item2.modEDR;
        }
      }
      if ((min1 + max1) / 2 > (min2 / max2) / 2)
        s += "%N  $i will do more damage than $I,";
      else if ((min1 + max1) / 2 == (min2 / max2) / 2)
        s += "%N  $i and $I will do about the same damage,";
      else
        s += "%N  $i will do less damage than $I,";
      int hr1 = item1.modHR + item1.modEHR;
      int hr2 = item2.modHR + item2.modEHR;
      if (hr1 > hr2)
        s += "%N  $i will hit more often than $I,";
      else if (hr1 == hr2)
        s += "%N  $i and $I will hit about as often,";
      else
        s += "%N  $i will hit less often than $I,";
    }
    if (item1.is(Item.WEARABLE) &&
        item2.is(Item.WEARABLE) &&
        (item1.modSHR != 0 ||
         item2.modSDR != 0)) {
      int shr1 = item1.modSHR + item1.modESHR;
      int shr2 = item2.modSHR + item2.modESHR;
      int sdr1 = item1.modSDR + item1.modESDR;
      int sdr2 = item2.modSDR + item2.modESDR;
      if (shr1 > shr2)
        s += "%N  $i will help spells hit more often than $I,";
      else if (shr1 == shr2)
        s += "%N  $i and $I will help spells hit about the same,";
      else
        s += "%N  $i will help spells hit less often than $I,";
      if (sdr1 > sdr2)
        s += "%N  $i will help spells do more damage than $I,";
      else if (sdr1 == sdr2)
        s += "%N  $i and $I will help spells do similar damage,";
      else
        s += "%N  $i will help spells do less damage than $I,";
    }
    if (item1.is(Item.ARMOR) &&
        item2.is(Item.ARMOR)) {
      int ac1 = item1.ac + item1.modAC + item1.modEAC;
      int ac2 = item2.ac + item2.modAC + item2.modEAC;
      if (ac1 > ac2)
        s += "%N  $i will protect you more than $I,";
      else if (ac1 == ac2)
        s += "%N  $i and $I will protect you about the same,";
      else
        s += "%N  $i will protect you less than $I,";
    }
    if (item1.is(Item.CONTAINER) &&
        item2.is(Item.CONTAINER)) {
      if (item1.mCapacity > item2.mCapacity)
        s += "%N  $i can hold more than $I,";
      else if (item1.mCapacity == item2.mCapacity)
        s += "%N  $i can't hold as much as $I,";
      else
        s += "%N  $i and $I will hold the same amout,";
      if (item1.mContainerWeight > item2.mContainerWeight)
        s += "%N  $i can hold more weight than $I,";
      else if (item1.mContainerWeight == item2.mContainerWeight)
        s += "%N  $i can't hold as much weight as $I,";
      else
        s += "%N  $i and $I will hold about the same amount of weight,";
    }
    if (s.equals(""))
      s += "%N  theres nothing comparable about these items.";
    else
      s = s.substring(0, s.length() - 1) + ".";
    return str + s;
  }

  @Override
  public String getHelp() {
    return "Syntax: compare <item>%N" +
           "        compare <item> to <item>";
  }
}

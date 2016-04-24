package net.cmacpherson.mud.commands;

import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Wield implements Command {

  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      o.toChar("Wield what?", c);
      return true;
    }
    Object[] arg = Utils.parseInput(Utils.readArg(line)[0]);
    ArrayList<Keywords> keywords = new ArrayList<Keywords>();
    for (Item item : c.inv)
      if (!item.isEquipped() &&
          Utils.testVisibility(c, item))
        keywords.add(item);
    if ((int)arg[0] == 0)
      o.toChar("You can't wield multiple items.", c);
    else {
      Item item = (Item)Utils.findMatch((int)arg[0], (String)arg[1], keywords.toArray(new Keywords[] {}));
      if (item == null)
        o.toChar("You can't find what you're trying to wield.", c);
      else if (item.slot != Item.Slot.WIELD)
        o.toChar("You can't wield that item.", c);
      else {
        Item[] remove = null;
        try {
          remove = c.wear(item, Character.EQSlot.WIELD);
          if (remove != null)
            for (Item i : remove) {
              o.toChar("You remove $i.", c, i);
              o.toRoom(c.location.room, "%N$n removes $i.", c, i);
            }
          o.toChar("You wield $i.", c, item);
          if (item.is(Item.CURSED))
            o.toChar("  A cold feeling runs through your body as you put it on.", c);
          o.toRoom(c.location.room, (remove == null ? "%N" : "") + "$n wields $i.", c, item);
        } catch (InsufficientLevelException e) {
          o.toChar("You need to be level " + item.level + " to use this item.", c);
        } catch (InsufficientSkillException e) {
          o.toChar("You need to have a " + item.reqProficiency.name().toLowerCase() + " skill level of " + item.reqProficiencyLevel + " to use this item.", c);
        } catch (CursedItemException e) {
          o.toChar("You try to take off $i, but it won't let go! CURSED!", c, e.cursed);
        } catch (WrongAlignmentException e) {
          o.toChar("You wear $i.", c, item);
          o.toChar("As $i touches your body, an extreme pain crawls through your body and%N" +
                   "  you drop it.", c, item);
          o.toRoom(c.location.room, (remove == null ? "%N" : "") + "$n cringes from pain as $e puts on $i and drops it.", c, item);
          c.inv.remove(item);
          c.location.room.items.add(item);
        } catch (WeaponBalanceException e) {
          o.toChar("You try to use $i but it doesn't balance well with $I.", c, item, e.other);
        }
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: wield <item>";
  }
}

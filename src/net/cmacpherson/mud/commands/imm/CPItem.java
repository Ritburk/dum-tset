package net.cmacpherson.mud.commands.imm;

import java.io.IOException;
import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;
import static net.cmacpherson.mud.Command.*;

//CREATE PROTO ITEM
public class CPItem implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      if (c instanceof PC) {
        ClientThread client = ((PC)c).client;
        if (client != null) {
          try {
            ItemProto proto = new ItemProto();
            proto.vnum = Globals.LAST_PROTO_ITEM_ID++;
            client.blockOutput = true;
            client.o2("Would you like to use the wizard? ", false);
            client.flush();
            String ans = client.getFromQueue();
            while (!ans.regionMatches(true, 0, "YES", 0, ans.length()) &&
                   !ans.regionMatches(true, 0, "NO", 0, ans.length())) {
              client.o2("Sorry, what was that? (Y/N) ", false);
              client.flush();
              ans = client.getFromQueue();
            }
            if (ans.regionMatches(true, 0, "YES", 0, ans.length())) {
              int maxLength = 0;
              for (String str : Item.TYPE_NAMES)
                if (str.length() > maxLength)
                  maxLength = str.length();
              String chart = Utils.createColumns(maxLength, Item.TYPE_NAMES);
              client.o2(chart, true);
              while (true) {
                client.o2("List all types that apply: ", false);
                client.flush();
                String types = client.getFromQueue();
                if (types.equals("")) {
                  client.o2(chart, true);
                  continue;
                }
                String[] split = types.split("\\s");
                boolean all = true;
                for (String str : split) {
                  boolean match = false;
                  for (String str2 : Item.TYPE_NAMES)
                    match |= str.equalsIgnoreCase(str2);
                  all &= match;
                }
                if (all) {
                  for (String str : split)
                    for (int i = 0; i < Item.TYPE_NAMES.length; i++)
                      if (str.equalsIgnoreCase(Item.TYPE_NAMES[i]))
                        proto.setType(i, true);;
                  break;
                } else
                  client.o2("|r|Error processing types.", true);
              }
              //TODO create test list to see if there wasn't a type missed
              proto.name = readString(client, "name: ");
              proto.longName = readString(client, "long name: ");
              String list = readString(client, "keywords (list): ");
              proto.keywords = list.split("\\s");
              maxLength = 0;
              for (String str : Item.FLAG_NAMES)
                if (str.length() > maxLength)
                  maxLength = str.length();
              chart = Utils.createColumns(maxLength, Item.FLAG_NAMES);
              client.o2(chart, true);
              while (true) {
                client.o2("flags (list): ", false);
                client.flush();
                String flags = client.getFromQueue();
                if (flags.equals(""))
                  break;
                String[] split = flags.split("\\s");
                boolean all = true;
                for (String str : split) {
                  boolean match = false;
                  for (String str2 : Item.FLAG_NAMES)
                    match |= str.equalsIgnoreCase(str2);
                  all &= match;
                }
                if (all) {
                  for (String str : split)
                    for (int i = 0; i < Item.FLAG_NAMES.length; i++)
                      if (str.equalsIgnoreCase(Item.FLAG_NAMES[i]))
                        proto.setFlag(i, true);
                  break;
                } else
                  client.o2("|r|Error processing flags.", true);
              }
//              maxLength = 0;
//              ArrayList<String> mats = new ArrayList<String>();
//              for (Item.Material mat : Item.Material.values()) {
//                if (mat.name().length() > maxLength)
//                  maxLength = mat.name().length();
//                mats.add(mat.name().toLowerCase());
//              }
//              chart = Utils.createColumns(maxLength, mats.toArray(new String[] {}));
//              client.o2(chart, true);
//              while (true) {
//                list = readString(client, "material: ");
//                if (list.equals("")) {
//                  client.o2(chart, true);
//                  continue;
//                }
//                try {
//                  proto.material = Item.Material.valueOf(list.toUpperCase());
//                  break;
//                } catch (IllegalArgumentException e) {
//                  client.o2("|r|Error processing material.", true);
//                }
//              }
              if (proto.isType(Item.GATEWAY)) {
                while (true) {
                  try {
                    proto.targetID = readLong(client, "target id: ");
                  } catch (NullPointerException e) {
                    throw new IOException();
                  }
                  if (o.SERVER.rooms.get(proto.targetID) != null)
                    break;
                  else
                    client.o2("|r|Unable to find target room.", true);
                }
              }
              if (proto.isType(Item.TANGABLE)) {
                try {
                  proto.level = readInt(client, "level: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.weight = readDouble(client, "weight: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.mDur = readInt(client, "durability: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.value = readDice(client, "value: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
              }
              if (proto.isType(Item.KEY)) {
                proto.keyID = Globals.LAST_KEY_ID++;
                try {
                  proto.uses = readDice(client, "key uses dice (unlimited = 0d0-1): ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
              }
              if (proto.isType(Item.WEARABLE)) {
                maxLength = 0;
                String[] names = new String[Proficiencies.Name.values().length];
                for (int i = 0; i < names.length; i++) {
                  names[i] = Proficiencies.Name.values()[i].name().toLowerCase();
                  if (names[i].length() > maxLength)
                    maxLength = names[i].length();
                }
                chart = Utils.createColumns(maxLength, names);
                client.o2(chart, true);
                while (true) {
                  list = readString(client, "required proficiency: ");
                  if (list.equals(""))
                    break;
                  try {
                    proto.reqProficiency = Proficiencies.Name.valueOf(list.toUpperCase());
                    break;
                  } catch (IllegalArgumentException e) {
                    client.o2("|r|Error processing required proficiency.", true);
                  }
                }
                if (proto.reqProficiency != null) {
                  try {
                    proto.reqProficiencyLevel = readInt(client, "required proficiency level: ");
                  } catch (NullPointerException e) {
                    throw new IOException();
                  }
                }
                maxLength = 0;
                ArrayList<String> slots = new ArrayList<String>();
                for (Item.Slot slot : Item.Slot.values()) {
                  if (slot.name().length() > maxLength)
                    maxLength = slot.name().length();
                  slots.add(slot.name().toLowerCase());
                }
                chart = Utils.createColumns(maxLength, slots.toArray(new String[] {}));
                client.o2(chart, true);
                while (true) {
                  list = readString(client, "slot: ");
                  if (list.equals("")) {
                    client.o2(chart, true);
                    continue;
                  }
                  try {
                    proto.slot = Item.Slot.valueOf(list.toUpperCase());
                    break;
                  } catch (IllegalArgumentException e) {
                    client.o2("|r|Error processing slot.", true);
                  }
                }
                //TODO affects wizard
                try {
                  proto.modSTR = readInt(client, "mod str: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modDEX = readInt(client, "mod dex: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modCON = readInt(client, "mod con: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modINT = readInt(client, "mod int: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modWIS = readInt(client, "mod wis: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modCHA = readInt(client, "mod cha: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modAC = readInt(client, "mod ac: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modFORT = readInt(client, "mod fort: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modREF = readInt(client, "mod ref: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modWILL = readInt(client, "mod will: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modHP = readInt(client, "mod hp: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modM = readInt(client, "mod mana: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modMV = readInt(client, "mod moves: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modSTR = readInt(client, "mod hr: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modSTR = readInt(client, "mod dr: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modSHR = readInt(client, "mod spell hr: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modSDR = readInt(client, "mod spell dr: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.modARC = readInt(client, "mod arc: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
              }
              if (proto.isType(Item.ARMOR)) {
                try {
                  proto.ac = readDice(client, "ac dice: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
              }
              if (proto.isType(Item.WEAPON)) {
                try {
                  proto.damage = readDice(client, "damage dice: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
              }
              if (proto.isType(Item.BOW) ||
                  proto.isType(Item.CROSSBOW))
                try {
                  proto.drawSTR = readInt(client, "draw str: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
              if (proto.isType(Item.LIGHT)) {
                try {
                  proto.duration = readDice(client, "duration dice (unlimited = 0d0-1): ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
              }
              if (proto.isType(Item.CONSUMABLE)) {
                //TODO spell wizard
                //include charges for each spell (or no charges for scrolls)
              }
              if (proto.isType(Item.CONTAINER)) {
                maxLength = 0;
                ArrayList<String> statuses = new ArrayList<String>();
                for (Item.Status status : Item.Status.values()) {
                  if (status.name().length() > maxLength)
                    maxLength = status.name().length();
                  statuses.add(status.name().toLowerCase());
                }
                chart = Utils.createColumns(maxLength, statuses.toArray(new String[] {}));
                client.o2(chart, true);
                while (true) {
                  list = readString(client, "container status: ");
                  if (list.equals("")) {
                    client.o2(chart, true);
                    continue;
                  }
                  try {
                    proto.status = Item.Status.valueOf(list.toUpperCase());
                    break;
                  } catch (IllegalArgumentException e) {
                    client.o2("|r|Error processing container status.", true);
                  }
                }
                try {
                  proto.mContainerWeight = readDouble(client, "max container weight: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
                try {
                  proto.mCapacity = readInt(client, "max container capacity: ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
              }
              if (proto.isType(Item.CORPSE))
                try {
                  proto.duration = new Dice("0d0-1");
                } catch (InvalidDiceFormatException e) {}
              if (proto.isType(Item.FOUNTAIN) |
                  proto.isType(Item.DRINK_CONTAINER)) {
                maxLength = 0;
                ArrayList<String> liquids = new ArrayList<String>();
                for (Liquid.Type liquid : Liquid.Type.values()) {
                  if (liquid.name().length() > maxLength)
                    maxLength = liquid.name().length();
                  liquids.add(liquid.name().toLowerCase());
                }
                chart = Utils.createColumns(maxLength, liquids.toArray(new String[] {}));
                client.o2(chart, true);
                while (true) {
                  list = readString(client, "liquid: ");
                  if (list.equals("")) {
                    client.o2(chart, true);
                    continue;
                  }
                  try {
                    proto.liquid = Liquid.valueOf(list.toUpperCase());
                    break;
                  } catch (IllegalArgumentException e) {
                    client.o2("|r|Error processing liquid.", true);
                  }
                }
              }
              if (proto.isType(Item.DRINK_CONTAINER))
                try {
                  proto.uses = readDice(client, "uses dice (unlimited = 0d0-1): ");
                } catch (NullPointerException e) {
                  throw new IOException();
                }
              if (proto.isType(Item.POISON)) {
                maxLength = 0;
                ArrayList<String> poisons = new ArrayList<String>();
                for (Item.Poison poison : Item.Poison.values()) {
                  if (poison.name().length() > maxLength)
                    maxLength = poison.name().length();
                  poisons.add(poison.name().toLowerCase());
                }
                chart = Utils.createColumns(maxLength, poisons.toArray(new String[] {}));
                client.o2(chart, true);
                while (true) {
                  list = readString(client, "poison: ");
                  if (list.equals("")) {
                    client.o2(chart, true);
                    continue;
                  }
                  try {
                    proto.poison = Item.Poison.valueOf(list.toUpperCase());
                    break;
                  } catch (IllegalArgumentException e) {
                    client.o2("|r|Error processing poison.", true);
                  }
                }
              }
            }
            o.SERVER.itemBank.put(proto.vnum, proto);
            client.o2("Successful.", true);
            client.o2("|w|VNUM: " + proto.vnum, true);
            client.o2("", true);
            client.o2("Show missed lines? (yes/no) ", false);
            client.flush();
            client.unblockOutput((ans = client.getFromQueue()).regionMatches(true, 0, "YES", 0, ans.length()));            
          } catch (IOException e) {
            client.blockOutput = false;
            return false;
          }
        }
      }
    } else {
      long vnum = -1;
      try {
        vnum = Long.parseLong(Utils.readArg(line)[0]);
      } catch (NumberFormatException e) {
        o.toChar("|r|Invalid vnum.", c);
        return true;
      }
      ItemProto proto = o.SERVER.itemBank.get(vnum);
      if (proto == null) {
        o.toChar("|r|Item prototype not found.", c);
        return true;
      } else {
        ItemProto newProto = proto.clone();
        o.SERVER.itemBank.put(newProto.vnum, newProto);
        o.toChar("Successful.", c);
        o.toChar("|w|VNUM: " + newProto.vnum, c);
      }
    }
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: #cpitem%N" +
           "        #cpitem <vnum>";
  }
}

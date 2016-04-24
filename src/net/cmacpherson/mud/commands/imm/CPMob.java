package net.cmacpherson.mud.commands.imm;

import java.io.IOException;
import java.util.ArrayList;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;
import static net.cmacpherson.mud.Command.*;

public class CPMob implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      if (c instanceof PC) {
        ClientThread client = ((PC)c).client;
        if (client != null) {
          try {
            MobProto proto = new MobProto();
            proto.vnum = Globals.LAST_PROTO_MOB_ID++;
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
              proto.name = readString(client, "name: ");
              proto.displayName = readString(client, "display name: ");
              String list = readString(client, "keywords (list): ");
              proto.keywords = list.split("\\s");
              int maxLength = 0;
              ArrayList<String> data = new ArrayList<String>();
              for (Character.Sex sex : Character.Sex.values()) {
                if (sex.name().length() > maxLength)
                  maxLength = sex.name().length();
                data.add(sex.name().toLowerCase());
              }
              String chart = Utils.createColumns(maxLength, data.toArray(new String[] {}));
              client.o2(chart, true);
              while (true) {
                list = readString(client, "sex: ");
                if (list.equals("")) {
                  client.o2(chart, true);
                  continue;
                }
                try {
                  proto.sex = Character.Sex.valueOf(list.toUpperCase());
                  break;
                } catch (IllegalArgumentException e) {
                  client.o2("|r|Error processing sex.", true);
                }
              }
              try {
                proto.hpDice = readDice(client, "hp dice: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto.mDice = readDice(client, "mana dice: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto.mvDice = readDice(client, "moves dice: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto.level = readInt(client, "level: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto.align = readInt(client, "align [-1000, 1000]: ");
                if (proto.align < -1000)
                  proto.align = -1000;
                if (proto.align > 1000)
                  proto.align = 1000;
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto._str = readInt(client, "str: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto._dex = readInt(client, "dex: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto._con = readInt(client, "con: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto._int = readInt(client, "int: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto._wis = readInt(client, "wis: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto._cha = readInt(client, "cha: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              try {
                proto.goldDice = readDice(client, "gold dice: ");
              } catch (NullPointerException e) {
                throw new IOException();
              }
              maxLength = 0;
              data.clear();
              for (AISpec spec : AISpec.values()) {
                if (spec.name().length() > maxLength)
                  maxLength = spec.name().length();
                data.add(spec.name());
              }
              chart = Utils.createColumns(maxLength, data.toArray(new String[] {}));
              client.o2(chart, true);
              while (true) {
                list = readString(client, "ai specs (list): ");
                if (list.equals(""))
                  break;
                String[] split = list.split("\\s");
                boolean all = true;
                for (String s : split) {
                  boolean match = false;
                  for (String str : data)
                    match |= s.equalsIgnoreCase(str);
                  all &= match;
                }
                if (all) {
                  ArrayList<AISpec> specs = new ArrayList<AISpec>();
                  for (String s : split)
                    specs.add(AISpec.valueOf(s.toUpperCase()));
                  proto.specs = specs.toArray(new AISpec[] {});
                  break;
                } else
                  client.o2("|r|Error processing ai specs.", true);
              }
              o.SERVER.mobBank.put(proto.vnum, proto);
              client.o2("Successful.", true);
              client.o2("|w|VNUM: " + proto.vnum, true);
              client.o2("", true);
              client.o2("Show missed lines? (yes/no) ", false);
              client.flush();
              client.unblockOutput((list = client.getFromQueue()).regionMatches(true, 0, "YES", 0, list.length()));
            }
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
      MobProto proto = o.SERVER.mobBank.get(vnum);
      if (proto == null) {
        o.toChar("|r|Mob prototype not found.", c);
        return true;
      } else {
        MobProto newProto = proto.clone();
        o.SERVER.mobBank.put(newProto.vnum, newProto);
        o.toChar("Successful.", c);
        o.toChar("|w|VNUM: " + newProto.vnum, c);
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: cpmob%N" +
           "        cpmob <vnum>";
  }
}

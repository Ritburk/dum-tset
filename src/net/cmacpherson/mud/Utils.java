package net.cmacpherson.mud;

import java.lang.reflect.Array;
import java.util.ArrayList;

public final class Utils {
  
  public static int baseHR(int _dex) {return _dex - 10;}
  public static int baseDR(int _str) {return _str - 10;}
  public static int baseSHR(int _int) {return _int;}
  public static int baseSDR(int _int) {return _int - 10;}
  public static int baseFort(int _con) {return _con - 10;}
  public static int baseRef(int _dex) {return _dex - 10;}
  public static int baseWill(int _wis) {return _wis - 10;}
  public static int baseAC(int _dex) {return (_dex - 10) * 10;}
  public static double mod(int stat) {
    return  1.0 + (stat - 10) * 0.1;
  }
  
  public static boolean justLetters(String str) {
    boolean justLetters = true;
    for (int i = 0; i < str.length(); i++)
      if (((int)str.charAt(i) < 65 ||
           (int)str.charAt(i) > 90) &&
          ((int)str.charAt(i) < 97 ||
           (int)str.charAt(i) > 122))
        justLetters = false;
    return justLetters;
  }
  public static boolean justAlphaNumeric(String str) {
    boolean justAlphaNumeric = true;
    for (int i = 0; i < str.length(); i++) 
      if (((int)str.charAt(i) < 48 ||
           (int)str.charAt(i) > 57) &&
          ((int)str.charAt(i) < 65 ||
           (int)str.charAt(i) > 90) &&
          ((int)str.charAt(i) < 97 ||
           (int)str.charAt(i) > 122))
        justAlphaNumeric = false;
    return justAlphaNumeric;
  }
  public static boolean testVisibility(Character looking, Character at) {
    if (looking.isAffectedBy(Affect.Type.BLIND))
      return false;
    boolean visible = looking.location.room.hasLight() ||
                      looking.isAffectedBy(Affect.Type.INFRAVISION);
    if (at.isAffectedBy(Affect.Type.INVIS))
      visible &= looking.isAffectedBy(Affect.Type.DETECT_INVIS);
    if (at.isAffectedBy(Affect.Type.HIDDEN))
      visible &= looking.isAffectedBy(Affect.Type.DETECT_HIDDEN);
    return visible;
  }
  public static boolean testVisibility(Character looking, Item at) {
    if (looking.isAffectedBy(Affect.Type.BLIND))
      return false;
    boolean visible = looking.location.room.hasLight() ||
                      at.is(Item.GLOWING);
    if (at.is(Item.INVIS))
      visible &= looking.isAffectedBy(Affect.Type.DETECT_INVIS);
    return visible;
  }
  
  public static String showEQ(Character looking, Character at) {
    if (at.isWearingNothing())
      if (!looking.equals(at))
        return "$e is naked!";
    String line = "";
    for (int i = 0; i < at.eq.length; i++) {
      if (!looking.equals(at) &&
          at.eq[i] == null)
        continue;
      String s = Globals.COLOR_STR + Globals.EQ_SLOT_DESC[i];
      if (at.eq[i] == null)
        s += "<nothing>";
      else if (!testVisibility(looking, at.eq[i]))
        s += "You are unsure.";
      else
        s += at.eq[i].name;
      line += s + "\n\r";
    }
    return line;
  }
  public static Object[] condenseItems(ArrayList<Item> items, Character c) {
    int[] counts = new int[0];
    ArrayList<Item> counted = new ArrayList<Item>();
    for (Item item : items) {
      if (item.isEquipped() ||
          (c != null &&
          !testVisibility(c, item)))
        continue;
      boolean duplicate = false;
      for (int i = 0; i < counted.size(); i++)
        if (item.vnum == counted.get(i).vnum &&
            item.name.equals(counted.get(i).name)) {
          duplicate = true;
          counts[i]++;
          break;
        }
      if (!duplicate) {
        counted.add(item);
        int[] temp = new int[counts.length + 1];
        System.arraycopy(counts, 0, temp, 0, counts.length);
        temp[temp.length - 1] = 1;
        counts = temp;
      }
    }
    return new Object[] {counts, counted.toArray(new Item[] {})};
  }
  
  public static Keywords[] findMatches(String arg, Keywords[] objs) {
    ArrayList<Keywords> keywords = new ArrayList<Keywords>();
    for (Keywords k : objs)
      if (matches(arg, k.keywords()))
        keywords.add(k);
    return keywords.toArray(new Keywords[] {});
  }
  public static Keywords findMatch(int n, String arg, Keywords[] objs) {
    Keywords keyword = null;
    for (Keywords k : objs) {
      if (matches(arg, k.keywords()))
        if (n == 1) {
          keyword = k;
          break;
        } else
          n--;
    }
    return keyword;
  }
  public static String[] readArg(String str) {
    String[] args = new String[] {"", ""};
    boolean inQuotes = false;
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == '\'') {
        if (inQuotes) {
          args[0] += "'";
          break;
        } else {
          args[0] += "'";
          inQuotes = true;
        }
      } else if (str.charAt(i) == ' ') {
        if (inQuotes) {
          args[0] += " ";
        } else {
          break;
        }
      } else {
        args[0] += str.charAt(i);
      }
    }
    args[1] = str.substring(args[0].length()).trim();
    return args;
  }
  public static String[] splitInput(String str) {
    ArrayList<String> split = new ArrayList<String>();
    while (!str.equals("")) {
      String[] result = readArg(str);
      split.add(result[0]);
      str = result[1];
    }
    return split.toArray(new String[] {});
  }
  public static Object[] parseInput(String str) {
    String[] split = str.split("\\.");
    for (int i = 0; i < split.length; i++)
      split[i] = split[i].replaceAll("\\'", "");
    if (split.length == 1)
      if (split[0].equalsIgnoreCase("all"))
        return new Object[] {0, ""};
      else
        return new Object[] {1, split[0]};
    else {
      if (split[0].equalsIgnoreCase("all"))
        split[0] = "0";
      return new Object[] {Integer.parseInt(split[0]), split[1]};
    }
  }
  private static boolean matches(String str, String[] keywords) {
    String[] args = str.split("\\s");
    boolean matches = true;
    for (String arg : args) {
      boolean argMatch = false;
      for (String s : keywords)
        argMatch |= arg.regionMatches(true, 0, s, 0, arg.length());
      matches &= argMatch;
    }
    return matches;
  }
  
  private static final int DEFAULT_TELNET_WIDTH = 79;
  public static String createColumns(int maxWidth, String[] data) {
    int colCount = DEFAULT_TELNET_WIDTH / (maxWidth + 1);
    String output = "";
    try {
      for (int i = 0; i < data.length;) {
        for (int j = 0; j < colCount; j++) {
          String col = data[i];
          while (col.length() < maxWidth)
            col += " ";
          output += col + " ";
          i++;
        }
        output += "\n\r";
      }
    } catch (ArrayIndexOutOfBoundsException e) {}
    return output;
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[] cast(Keywords[] data, Class<?> to) {
    T[] copy = (T[])Array.newInstance(to, data.length);
    System.arraycopy(data, 0, copy, 0, data.length);
    return copy;
  }
  
  public static Character[] order(Character[] list) {
    ArrayList<Character> chars = new ArrayList<Character>();
    loop:
    for (Character c : list) {
      for (int i = 0; i < chars.size(); i++)
        if (c.level > chars.get(i).level) {
          chars.add(i, c);
          continue loop;
        }
      chars.add(c);
    }
    return chars.toArray(new Character[] {});
  }
  
  public static Character[] filter(Character[] chars, Filter[] filters) {
    ArrayList<Character> chars2 = new ArrayList<Character>();
    for (Character ch :chars) {
      boolean passes = true;
      for (Filter filter : filters)
        passes &= filter.pass(ch);
      if (passes)
        chars2.add(ch);
    }
    return chars2.toArray(new Character[] {});
  }
}

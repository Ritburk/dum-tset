package net.cmacpherson.mud;

import java.util.ArrayList;

public class Output {
  //$n = from's name
  //$N = target's name
  //$e $E
  private static final String[] HE_SHE = new String[] {"it", "he", "she"};
  //$m $M
  private static final String[] HIM_HER = new String[] {"it", "him", "her"};
  //$s $S
  private static final String[] HIS_HER = new String[] {"its", "his", "her"};
  
  public final ServerThread SERVER;
  private final ArrayList<PC> toPrompt = new ArrayList<PC>();

  public Output(ServerThread server) {
    SERVER = server;
  }
  
  public void prompt(Character c) {
    if (c instanceof PC)
      if (!toPrompt.contains((PC)c))
        toPrompt.add((PC)c);
  }
  
  private void toChar(String msg, Character to, Character from, Item item1, Item item2) {
    if (to instanceof PC) {
      ((PC)to).client.output(OutputData.line(supplyPronouns(msg, to, to, from, item1, item2)), false);
      if (!toPrompt.contains((PC)to))
        toPrompt.add((PC)to);
    }
  }
  
  public void toChar(String msg, Character to) {
    toChar(msg, to, null, null, null);
  }

  public void toChar(String msg, Character to, Character from) {
    toChar(msg, to, from, null, null);
  }
  
  public void toChar(String msg, Character to, Character from, Item item) {
    toChar(msg, to, from, item, null);
  }
  
  public void toChar(String msg, Character to, Item item) {
    toChar(msg, to, null, item, null);
  }
  
  public void toChar(String msg, Character to, Item item1, Item item2) {
    toChar(msg, to, null, item1, item2);
  }
  
  
  private void toRoom(Room room, String msg, Character target, Character from, Item item1, Item item2) {
    for (Character ch : room.chars)
      if (ch instanceof PC &&
          ((PC)ch).client != null &&
          !ch.equals(from) &&
          !ch.equals(target) &&
          ch.status != Character.Status.SLEEPING) {
        ((PC)ch).client.output(OutputData.line(supplyPronouns(msg, ch, target, from, item1, item2)), false);
        if (!toPrompt.contains((PC)ch))
          toPrompt.add((PC)ch);
      }
  }
  
  public void toRoom(Room room, String msg, Character from, Item item1, Item item2) {
    toRoom(room, msg, null, from, item1, item2);
  }
  
  public void toRoom(Room room, String msg, Character from, Item item) {
    toRoom(room, msg, null, from, item, null);
  }
  
  public void toRoom(Room room, String msg, Character target, Character from, Item item) {
    toRoom(room, msg, target, from, item, null);
  }

  public void toRoom(Room room, String msg, Character target, Character from) {
    toRoom(room, msg, target, from, null, null);
  }
  
  public void toRoom(Room room, String msg, Character from) {
    toRoom(room, msg, null, from, null, null);
  }
  
  public void toGroup(Group g, String msg, Character target, Character from) {
    for (Character ch : g.members)
      if (ch instanceof PC &&
          ((PC)ch).client != null &&
          !ch.equals(from) &&
          !ch.equals(target)) {
        ((PC)ch).client.output(OutputData.line(supplyPronouns(msg, ch, target, from, null, null)), false);
        if (!toPrompt.contains((PC)ch))
          toPrompt.add((PC)ch);      }
  }
  
  public void toGroup(Group g, String msg, Character from) {
    toGroup(g, msg, null, from);
  }

  public void toAll(String msg, Character from, boolean excludeFrom) {
    for (PC player : SERVER.loggedPCs) {
      if (player.client != null)
        if (!(excludeFrom && player.equals(from))) {
          player.client.output(OutputData.line(msg), false);
          if (!toPrompt.contains(player))
            toPrompt.add(player);
        }
    }
  }

  public synchronized void flush() {
    for (int i = 0; i < toPrompt.size(); i++)
      toPrompt.get(i).client.player.prompt();
    toPrompt.clear();
  }

  private String supplyPronouns(String msg, Character to, Character target, Character from, Item item1, Item item2) {
    msg = msg.replaceAll("%N", "\n\r");
    int i = -1;
    while ((i = msg.indexOf('$')) != -1) {
      switch (msg.charAt(i + 1)) {
      case 'n':
        if (!Utils.testVisibility(to, from))
          msg = msg.substring(0, i) + "someone" + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + from.name + msg.substring(i + 2);
        break;
      case 'N':
        if (!Utils.testVisibility(to, target))
          msg = msg.substring(0, i) + "someone" + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + target.name + msg.substring(i + 2);
        break;
      case 'e':
        if (!Utils.testVisibility(to, from))
          msg = msg.substring(0, i) + "someone" + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + HE_SHE[from.sex.ordinal()] + msg.substring(i + 2);
        break;
      case 'E':
        if (!Utils.testVisibility(to, target))
          msg = msg.substring(0, i) + "someone" + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + HE_SHE[target.sex.ordinal()] + msg.substring(i + 2);
        break;
      case 'm':
        if (!Utils.testVisibility(to, from))
          msg = msg.substring(0, i) + "someone" + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + HIM_HER[from.sex.ordinal()] + msg.substring(i + 2);
        break;
      case 'M':
        if (!Utils.testVisibility(to, target))
          msg = msg.substring(0, i) + "someone" + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + HIM_HER[target.sex.ordinal()] + msg.substring(i + 2);
        break;
      case 's':
        if (!Utils.testVisibility(to, from))
          msg = msg.substring(0, i) + "someones" + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + HIS_HER[from.sex.ordinal()] + msg.substring(i + 2);
        break;
      case 'S':
        if (!Utils.testVisibility(to, target))
          msg = msg.substring(0, i) + "someones" + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + HIS_HER[target.sex.ordinal()] + msg.substring(i + 2);
        break;
      case 'i':
        if (Utils.testVisibility(to, item1))
          msg = msg.substring(0, i) + item1.name + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + "something" + msg.substring(i + 2);
        break;
      case 'I':
        if (Utils.testVisibility(to, item2))
          msg = msg.substring(0, i) + item2.name + msg.substring(i + 2);
        else
          msg = msg.substring(0, i) + "something" + msg.substring(i + 2);
      default:
        SERVER.print("E[Output.supplyPronouns]: unknown argument following $: '" + msg.charAt(i + 1) + "'");
        break;
      }
    }
    return capitalize(msg);
  }
  
  public String capitalize(String s) {
    if (s == null ||
        s.equals(""))
      return s;
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }
}

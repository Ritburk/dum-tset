package net.cmacpherson.mud.commands;

import javax.xml.transform.TransformerException;

import net.cmacpherson.mud.*;
import net.cmacpherson.mud.Character;

public class Quit implements Command {
  @Override
  public boolean execute(Character c, String cmd, String args, Output o) {
    if (c instanceof PC) {
      try {
        XML.INSTANCE.build((PC)c);
        o.toChar("Character saved.", c);
      } catch (TransformerException e) {
        //dump char data into text file to rebuild manually if needed
        o.toChar("|br|Character could not be saved." +  
                 "%NA dump file will be created instead as a backup.", c);
      }
      if (c.group != null) {
        if (c.group == c.myGroup) {
          
        } else {
          o.toGroup(c.group, "%N$n has left the group.", c);
          c.follow(c);
        }
      }
      ((PC)c).client.logout();
      c.location.room.chars.remove(c);
      o.toRoom(c.location.room, "%N|y|$n steps into a tear in reality and is gone.", c);
    }
    return true;
  }
  
  @Override
  public String getHelp() {
    return "Syntax: quit";
  }
}

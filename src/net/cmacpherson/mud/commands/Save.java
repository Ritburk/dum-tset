package net.cmacpherson.mud.commands;

import javax.xml.transform.TransformerException;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Save implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (c instanceof PC) {
      try {
        XML.INSTANCE.build((PC)c);
        o.toChar("Character saved.", c);
      } catch (TransformerException e) {
        //dump char data into text file to rebuild manually if needed
        o.toChar("|br|Character could not be saved." +  
                 "%NA dump file will be created instead as a backup.", c);
      }
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: save";
  }
}

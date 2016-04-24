package net.cmacpherson.mud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Spell implements XML.Processable {

  //TODO Spell
  
  @Override
  public Element compile(Document doc) {
    Element spellE = doc.createElement("spell");
    
    return spellE;
  }

  @Override
  public void load(Element e) {
    
  }

  @Override
  public String getFilePath() {return null;}
}

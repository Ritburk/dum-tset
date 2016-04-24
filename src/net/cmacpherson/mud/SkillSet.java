package net.cmacpherson.mud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SkillSet implements XML.Processable {

  @Override
  public Element compile(Document doc) {
    Element skills = doc.createElement("skills");
    
    return skills;
  }

  @Override
  public void load(Element e) {
    
  }

  @Override
  public String getFilePath() {return null;}
}

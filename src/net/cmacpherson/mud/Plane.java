package net.cmacpherson.mud;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Plane implements XML.Processable {

  public String name;
  public ArrayList<Area> areas = new ArrayList<Area>();
  
  public ArrayList<Character> chars() {
    ArrayList<Character> chars = new ArrayList<Character>();
    for (Area area : areas)
      chars.addAll(area.chars());
    return chars;
  }
  
  public void organize() {
    ArrayList<Area> temp = new ArrayList<Area>();
    loop:
    for (Area area : areas) {
      if (temp.isEmpty()) {
        temp.add(area);
        continue;
      }
      for (int i = 0; i < temp.size(); i++) {
        if (area.levelRange[0] < temp.get(i).levelRange[0]) {
          temp.add(i, area);
          continue loop;
        }
        if (area.levelRange[0] == temp.get(i).levelRange[0] &&
            area.levelRange[1] < temp.get(i).levelRange[1]) {
          temp.add(i, area);
          continue loop;
        }
      }
      temp.add(area);
    }
    areas = temp;
  }

  @Override
  public Element compile(Document doc) {
    Element planeE = doc.createElement("plane");
    planeE.appendChild(XML.cC(doc, "name", name));
    for (Area area: areas)
      planeE.appendChild(area.compile(doc));
    return planeE;
  }

  @Override
  public void load(Element planeE) {
    NodeList children = planeE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("name"))
          name = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("area")) {
          Area area = new Area();
          area.load(e);
          areas.add(area);
        }
      }
    }
  }
  
  @Override
  public String getFilePath() {
    return Globals.WORLD_PATH + name + ".xml";
  }
}


// add starting location for plane? hardcode starting room in char creation screen?
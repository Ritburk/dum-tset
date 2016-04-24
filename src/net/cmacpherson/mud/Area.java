package net.cmacpherson.mud;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Area implements XML.Processable {
  
  public String name;
  public int[] levelRange = new int[] {-1, -1};
  public ArrayList<Room> rooms = new ArrayList<Room>();
  
  public ArrayList<Character> chars() {
    ArrayList<Character> chars = new ArrayList<Character>();
    for (Room room : rooms)
      chars.addAll(room.chars);
    return chars;
  }
  
  public boolean isCommon() {
    return levelRange[0] == -1 &&
           levelRange[1] == -1;
  }
  
  @Override
  public String toString() {
    String s = name;
    if (isCommon())
      s = "{ COMMON } " + s;
    else
      s = "{ " + (levelRange[0] < 9 ? " " : "") +
          levelRange[0] +
          "  " + (levelRange[1] < 9 ? " " : "") +
          levelRange[1] + " } " + s;
    return s;
  }
  
  //TODO reset data stored here
  //should this be lines of text that are commands to rebuild the area?
  //how would we track things that don't need to be re added?
  //should mobs and items have area reset id numbers that we can search
  //  for that will track if the item has changed from last reset?
  //should we never save exit data within rooms compile output and have
  //  some type of script loaded from the area reset command finish
  //  building everything that's needed in the area?

  @Override
  public Element compile(Document doc) {
    Element areaE = doc.createElement("area");
    areaE.appendChild(XML.cC(doc, "name", name));
    areaE.appendChild(XML.cC(doc, "level_range", levelRange[0] + " " + levelRange[1]));
    for (Room room : rooms)
      areaE.appendChild(room.compile(doc));
    return areaE;
  }

  @Override
  public void load(Element areaE) {
    NodeList children = areaE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("name"))
          name = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("level_range")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          levelRange[0] = Integer.parseInt(split[0]);
          levelRange[1] = Integer.parseInt(split[1]);
        } else if (e.getTagName().equals("room")) {
          Room room = new Room();
          room.load(e);
          rooms.add(room);
        }
      }
    }
  }
  
  @Override
  public String getFilePath() {return null;}
}

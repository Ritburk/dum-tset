package net.cmacpherson.mud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class PC extends Character {

  public int version;
  public ClientThread client;
  public boolean linkdead;
  public String password;
  public String prompt;
  public String title;
  public long exp;
  public int deaths;
  
  public PC(ServerThread server) {
    super(server);
  }

  @Override
  public void doCombatRound(Output o) {
    // TODO PC#doCombatRound()
  }

  @Override
  public void doRegen() {
    // TODO PC#doRegen()
  }
  
  public void name(String name) {
    this.name = name;
    this.keywords = new String[] {name};
  }
  
  public String displayName() {
    return ((linkdead) ? "(Linkdead)" : "") + super.displayName();
  }
  
  public void prompt() {
    String line = prompt;
    if (config.showPrompt) {
      int index = line.indexOf("%h");
      while (index != -1) {
        line = line.substring(0, index) + hp + line.substring(index + "%h".length());
        index = line.indexOf("%h");
      }
      index = line.indexOf("%H");
      while (index != -1) {
        line = line.substring(0, index) + mhp + line.substring(index + "%H".length());
        index = line.indexOf("%H");
      }
      index = line.indexOf("%m");
      while (index != -1) {
        line = line.substring(0, index) + m + line.substring(index + "%m".length());
        index = line.indexOf("%m");
      }
      index = line.indexOf("%M");
      while (index != -1) {
        line = line.substring(0, index) + mm + line.substring(index + "%M".length());
        index = line.indexOf("%M");
      }
      index = line.indexOf("%v");
      while (index != -1) {
        line = line.substring(0, index) + mv + line.substring(index + "%v".length());
        index = line.indexOf("%v");
      }
      index = line.indexOf("%V");
      while (index != -1) {
        line = line.substring(0, index) + mmv + line.substring(index + "%V".length());
        index = line.indexOf("%V");
      }
      index = line.indexOf("%T");
      while (index != -1) {
        line = line.substring(0, index) + tnl() + line.substring(index + "%T".length());
        index = line.indexOf("%T");
      }
      index = line.indexOf("%N");
      while (index != -1) {
        line = line.substring(0, index) + "\n\r" + line.substring(index + "%N".length());
        index = line.indexOf("%N");
      }
      index = line.indexOf("%R");
      while (index != -1) {
        line = line.substring(0, index) + location.id + line.substring(index + "%R".length());
        index = line.indexOf("%R");
      }
    } else
      line = "|w|>";
    client.output(new OutputData(line, false), false);
    client.flush();
  }
  
  public long tnl() {return race.tnl - (exp % race.tnl);}

  @Override
  public Element compile(Document doc) {
    Element pc = doc.createElement("pc");
    pc.appendChild(XML.cC(doc, "version", Integer.toString(version)));
    pc.appendChild(XML.cC(doc, "password", password));
    pc.appendChild(XML.cC(doc, "prompt", prompt));
    pc.appendChild(XML.cC(doc, "title", title));
    pc.appendChild(XML.cC(doc, "exp", Long.toString(exp)));
    pc.appendChild(XML.cC(doc, "deaths", Integer.toString(deaths)));
    pc.appendChild(config.compile(doc));
    pc.appendChild(super.compile(doc));
    return pc;
  }

  @Override
  public void load(Element pcE) {
    NodeList children = pcE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("version"))
          version = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("password"))
          password = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("prompt"))
          prompt = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("title"))
          title = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("exp"))
          exp = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("deaths"))
          deaths = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("config")) {
          config = new Config();
          config.load(e);
        } else if (e.getTagName().equals("character"))
          super.load(e);
      }
    }
  }
  
  @Override
  public String getFilePath() {
    return Globals.PC_PATH + name.toLowerCase() + ".xml";
  }
  
  public String toString() {
    return "[" + race.abr + "] " + name + title;
  }
}

package net.cmacpherson.mud;

import java.util.ArrayList;
import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class MobProto implements XML.Processable {

  public long vnum;
  public String name;
  public String displayName;
  public String[] keywords;
  public ArrayList<String> description = new ArrayList<String>(Character.MAX_DESCRIPTION_SIZE);
  public Character.Sex sex;
  public Dice hpDice;
  public Dice mDice;
  public Dice mvDice;
  public int level;
  public int align;
  public int _str;
  public int _dex;
  public int _con;
  public int _int;
  public int _wis;
  public int _cha;
  public Dice goldDice;
  public AISpec[] specs = new AISpec[0];
  
  @SuppressWarnings("unchecked")
  public MobProto clone() {
    MobProto proto = new MobProto();
    proto.vnum = Globals.LAST_PROTO_MOB_ID++;
    proto.name = name;
    proto.displayName = displayName;
    proto.keywords = Arrays.copyOf(keywords, keywords.length);
    proto.description = (ArrayList<String>)description.clone();
    proto.sex = sex;
    proto.hpDice = hpDice.clone();
    proto.mDice = mDice.clone();
    proto.mvDice = mvDice.clone();
    proto.level = level;
    proto.align = align;
    proto._str = _str;
    proto._dex = _dex;
    proto._con = _con;
    proto._int = _int;
    proto._wis = _wis;
    proto._cha = _cha;
    proto.goldDice = goldDice.clone();
    proto.specs = Arrays.copyOf(specs, specs.length);
    return proto;
  }
  
  public Mob generate(int resetID) {
    Mob mob = new Mob(vnum);
    mob.specs = Arrays.copyOf(specs, specs.length);
    mob.resetID = resetID;
    mob.name = name;
    mob.displayName = displayName;
    mob.keywords = Arrays.copyOf(keywords, keywords.length);
    mob.sex = sex;
    mob.mhp = genHP();
    mob.hp = mob.mhp;
    mob.mm = genM();
    mob.m = mob.mm;
    mob.mmv = genMV();
    mob.mv = mob.mmv;
    mob.hr = genHR();
    mob.dr = genDR();
    mob.shr = genSHR();
    mob.sdr = genSDR();
    mob.level = level;
    mob.align = align;
    mob.ac = genAC();
    mob.fort = genFORT();
    mob.ref = genREF();
    mob.will = genWILL();
    mob._str = _str;
    mob.cSTR = _str;
    mob._dex = _dex;
    mob.cDEX = _dex;
    mob._con = _con;
    mob.cCON = _con;
    mob._int = _int;
    mob.cINT = _int;
    mob._wis = _wis;
    mob.cWIS = _wis;
    mob._cha = _cha;
    mob.cCHA = _cha;
    mob.gold = genGold();
    return mob;
  }
  
  private long genHP() {
    long n = 0;
    for (int i = 0; i < level; i++)
      n += hpDice.roll();
    return n;
  }
  private long genM() {
    long n = 0;
    for (int i = 0; i < level; i++)
      n += mDice.roll();
    return n;
  }
  private long genMV() {
    long n = 0;
    for (int i = 0; i < level; i++)
      n += mvDice.roll();
    return n;
  }
  private int genHR() {return level + (int)(10 * Utils.mod(_dex));}
  private int genDR() {return level + (int)(10 * Utils.mod(_str));}
  private int genSHR() {return level + (int)(10 * Utils.mod(_cha));}
  private int genSDR() {return level + (int)(10 * Utils.mod(_int));}
  private int genAC() {return level + (int)(10 * Utils.mod(_dex));}
  private int genFORT() {return (int)(level / 2.0 + 10 * Utils.mod(_con));}
  private int genREF() {return (int)(level / 2.0 + 10 * Utils.mod(_dex));}
  private int genWILL() {return (int)(level / 2.0 + 10 * Utils.mod(_wis));}
  private long genGold() {
    long n = 0;
    for (int i = 0; i < level; i++)
      n += goldDice.roll();
    return n / 4;
  }
  
  @Override
  public Element compile(Document doc) {
    Element mobE = doc.createElement("mob");
    mobE.appendChild(XML.cC(doc, "vnum", Long.toString(vnum)));
    mobE.appendChild(XML.cC(doc, "name", name));
    mobE.appendChild(XML.cC(doc, "display_name", displayName));
    if (keywords.length > 0) {
      String line = "";
      for (String k : keywords)
        line += k + " ";
      mobE.appendChild(XML.cC(doc, "keywords", line.trim()));
    }
    Element descE = doc.createElement("description");
    for (String line : description)
      descE.appendChild(XML.cC(doc, "line", line));
    mobE.appendChild(descE);
    mobE.appendChild(XML.cC(doc, "sex", sex.name()));
    mobE.appendChild(XML.cC(doc, "hp_dice", hpDice.toString()));
    mobE.appendChild(XML.cC(doc, "m_dice", mDice.toString()));
    mobE.appendChild(XML.cC(doc, "mv_dice", mvDice.toString()));
    mobE.appendChild(XML.cC(doc, "level", Integer.toString(level)));
    mobE.appendChild(XML.cC(doc, "align", Integer.toString(align)));
    mobE.appendChild(XML.cC(doc, "str", Integer.toString(_str)));
    mobE.appendChild(XML.cC(doc, "dex", Integer.toString(_dex)));
    mobE.appendChild(XML.cC(doc, "con", Integer.toString(_con)));
    mobE.appendChild(XML.cC(doc, "int", Integer.toString(_int)));
    mobE.appendChild(XML.cC(doc, "wis", Integer.toString(_wis)));
    mobE.appendChild(XML.cC(doc, "cha", Integer.toString(_cha)));
    mobE.appendChild(XML.cC(doc, "gold_dice", goldDice.toString()));
    if (specs.length > 0) {
      String line = "";
      for (AISpec spec : specs)
        line += spec.name() + " ";
      mobE.appendChild(XML.cC(doc, "ai_specs", line.trim()));
    }
    return mobE;
  }

  @Override
  public void load(Element protoE) {
    NodeList children = protoE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("vnum"))
          vnum = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("name"))
          name = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("display_name"))
          displayName = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("keywords"))
          keywords = ((Text)e.getFirstChild()).getData().split("\\s");
        else if (e.getTagName().equals("description")) {
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("line"))
                description.add(((Text)e2.getFirstChild()).getData());
            }
          }
        } else if (e.getTagName().equals("sex"))
          sex = Character.Sex.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("hp_dice"))
          try {
            hpDice = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
        else if (e.getTagName().equals("m_dice"))
          try {
            mDice = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
        else if (e.getTagName().equals("mv_dice"))
          try {
            mvDice = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
        else if (e.getTagName().equals("level"))
          level = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("align"))
          align = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("str"))
          _str = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("dex"))
          _dex = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("con"))
          _con = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("int"))
          _int = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("wis"))
          _wis = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("cha"))
          _cha = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("gold_dice"))
          try {
            goldDice = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
        else if (e.getTagName().equals("ai_specs")) {
          ArrayList<AISpec> specs = new ArrayList<AISpec>();
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          for (String s : split)
            specs.add(AISpec.valueOf(s));
          this.specs = specs.toArray(new AISpec[] {});
        }
      }
    }
  }

  @Override
  public String getFilePath() {
    return Globals.MOB_PATH + vnum + ".xml";
  }
}

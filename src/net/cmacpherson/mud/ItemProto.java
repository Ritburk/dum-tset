package net.cmacpherson.mud;

import java.util.ArrayList;
import java.util.Arrays;

//import net.cmacpherson.mud.Item.Material;
import net.cmacpherson.mud.Item.Slot;
import net.cmacpherson.mud.Item.Status;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ItemProto implements XML.Processable {
  
  public long vnum;
  public String name;
  public String longName;
  public String[] keywords;
  public int type;
  public int flags;
  public int level;
  public double weight;
  public int mDur;
  public Dice value;
  //public Item.Material material;
  public Proficiencies.Name reqProficiency;
  public int reqProficiencyLevel;
  public Item.Slot slot;
  public ArrayList<Affect> affects = new ArrayList<Affect>();
  public int modSTR;
  public int modDEX;
  public int modCON;
  public int modINT;
  public int modWIS;
  public int modCHA;
  public int modAC;
  public int modFORT;
  public int modREF;
  public int modWILL;
  public int modHP;
  public int modM;
  public int modMV;
  public int modHR;
  public int modDR;
  public int modSHR;
  public int modSDR;
  public int modARC;
  public Dice ac;
  public Dice damage;
  public int drawSTR;
  public Spell[] spells = new Spell[0];
  public int[] charges = new int[0];
  public long keyID;
  public Dice uses;
  public Dice duration;
  public long targetID;
  public Item.Status status;
  public double mContainerWeight;
  public int mCapacity;
  public ItemProto[] contents;
  public Item.Poison poison;
  public Liquid liquid;
  
  public boolean isType(int flag) {return (type & (1 << flag)) != 0;}
  public void setType(int flag, boolean on) {
    if (on)
      type |= 1 << flag;
    else
      type &= ~(1 << flag);
  }
  public boolean is(int flag) {return (flags & (1 << flag)) != 0;}
  public void setFlag(int flag, boolean on) {
    if (on)
      flags |= flag;
    else
      flags &= ~( 1 << flag);
  }
  
  @SuppressWarnings("unchecked")
  public ItemProto clone() {
    ItemProto proto = new ItemProto();
    proto.vnum = Globals.LAST_PROTO_ITEM_ID++;
    proto.name = name;
    proto.longName = longName;
    proto.keywords = keywords.clone();
    proto.type = type;
    proto.flags = flags;
    proto.level = level;
    proto.weight = weight;
    proto.mDur = mDur;
    proto.value = value;
    //proto.material = material;
    proto.reqProficiency = reqProficiency;
    proto.reqProficiencyLevel = reqProficiencyLevel;
    proto.slot = slot;
    proto.affects = (ArrayList<Affect>)affects.clone();
    proto.modSTR = modSTR;
    proto.modDEX = modDEX;
    proto.modCON = modCON;
    proto.modINT = modINT;
    proto.modWIS = modWIS;
    proto.modCHA = modCHA;
    proto.modAC = modAC;
    proto.modFORT = modFORT;
    proto.modREF = modREF;
    proto.modWILL = modWILL;
    proto.modHP = modHP;
    proto.modM = modM;
    proto.modMV = modMV;
    proto.modHR = modHR;
    proto.modDR = modDR;
    proto.modSHR = modSHR;
    proto.modSDR = modSDR;
    proto.modARC = modARC;
    proto.ac = ac.clone();
    proto.damage = damage.clone();
    proto.drawSTR = drawSTR;
    proto.spells = spells.clone();
    proto.charges = charges.clone();
    proto.keyID = keyID;
    proto.uses = uses.clone();
    proto.duration = duration.clone();
    proto.targetID = targetID;
    proto.status = status;
    proto.mContainerWeight = mContainerWeight;
    proto.mCapacity = mCapacity;
    proto.contents = contents.clone();
    proto.poison = poison;
    proto.liquid = liquid;
    return proto;
  }
  
  public static Item generateGold(long n) {
    Item gold = new Item() {
      @Override
      public double weight() {
        return value * Globals.WEIGHT_PER_GOLD;
      }
    };
    gold.vnum = Globals.GOLD_VNUM;
    if (n == 1) {
      gold.name = "a gold coin";
      gold.longName = "You see a gold coin laying here.";
    } else {
      gold.name = n + " gold coins";
      gold.longName = "You see a pile of gold coins laying here.";
    }
    gold.keywords = new String[] {"gold", "coins"};
    gold.setType(Item.TREASURE, true);
    gold.value = n;
    return gold;
  }
  
  public Item generate(int resetID) {
    Item item = new Item();
    item.vnum = vnum;
    item.resetID = resetID;
    item.id = Globals.LAST_ITEM_ID++;
    item.name = name;
    item.longName = longName;
    item.keywords = keywords;
    item.type = type;
    item.flags = flags;
    item.level = level;
    item.weight = weight;
    item.mDur = mDur;
    item.value = value.roll();
    //item.material = material;
    item.slot = slot;
    for (Affect a : affects)
      item.affects.add(a);
    item.modSTR = modSTR;
    item.modDEX = modDEX;
    item.modCON = modCON;
    item.modINT = modINT;
    item.modWIS = modWIS;
    item.modCHA = modCHA;
    item.modAC = modAC;
    item.modFORT = modFORT;
    item.modREF = modREF;
    item.modWILL = modWILL;
    item.modHP = modHP;
    item.modM = modM;
    item.modMV = modMV;
    item.modHR = modHR;
    item.modDR = modDR;
    item.modSHR = modSHR;
    item.modSDR = modSDR;
    item.modARC = modARC;
    item.ac = (int)ac.roll();
    item.damage = damage;
    item.drawSTR = drawSTR;
    item.spells = Arrays.copyOf(spells, spells.length);
    if (charges.length > 0)
      item.charges = new int[][] {Arrays.copyOf(charges, charges.length), Arrays.copyOf(charges, charges.length)};
    item.keyID = keyID;
    item.uses = (int)uses.roll();
    item.duration = (int)duration.roll();
    item.targetID = targetID;
    item.status = status;
    item.mContainerWeight = mContainerWeight;
    item.mCapacity = mCapacity;
    if (contents.length > 0)
      for (ItemProto content : contents)
        item.contents.add(content.generate(resetID));
    item.liquid = liquid;
    item.poison = poison;
    return item;
  }

  @Override
  public Element compile(Document doc) {
    Element itemE = doc.createElement("item_proto");
    itemE.appendChild(XML.cC(doc, "vnum", Long.toString(vnum)));
    itemE.appendChild(XML.cC(doc, "name", name));
    itemE.appendChild(XML.cC(doc, "long_name", longName));
    if (keywords.length > 0) {
      String line = "";
      for (String k : keywords)
        line += k + " ";
      itemE.appendChild(XML.cC(doc, "keywords", line.trim()));
    }
    itemE.appendChild(XML.cC(doc, "type", Integer.toString(type)));
    itemE.appendChild(XML.cC(doc, "flags", Integer.toString(flags)));
    itemE.appendChild(XML.cC(doc, "level", Integer.toString(level)));
    itemE.appendChild(XML.cC(doc, "weight", Double.toString(weight)));
    itemE.appendChild(XML.cC(doc, "max_durability", Integer.toString(mDur)));
    if (value != null)
      itemE.appendChild(XML.cC(doc, "value", value.toString()));
//    if (material != null)
//      itemE.appendChild(XML.cC(doc, "material", material.name()));
    if (reqProficiency != null) {
      itemE.appendChild(XML.cC(doc, "required_proficiency", reqProficiency.name()));
      itemE.appendChild(XML.cC(doc, "required_proficiency_level", Integer.toString(reqProficiencyLevel)));
    }
    if (slot != null)
      itemE.appendChild(XML.cC(doc, "slot", slot.name()));
    if (affects.size() > 0) {
      Element e = doc.createElement("affects");
      for (Affect affect : affects)
        e.appendChild(affect.compile(doc));
      itemE.appendChild(e);
    }
    itemE.appendChild(XML.cC(doc, "mod_str", Integer.toString(modSTR)));
    itemE.appendChild(XML.cC(doc, "mod_con", Integer.toString(modCON)));
    itemE.appendChild(XML.cC(doc, "mod_dex", Integer.toString(modDEX)));
    itemE.appendChild(XML.cC(doc, "mod_int", Integer.toString(modINT)));
    itemE.appendChild(XML.cC(doc, "mod_wis", Integer.toString(modWIS)));
    itemE.appendChild(XML.cC(doc, "mod_cha", Integer.toString(modCHA)));
    itemE.appendChild(XML.cC(doc, "mod_ac", Integer.toString(modAC)));
    itemE.appendChild(XML.cC(doc, "mod_fortitude", Integer.toString(modFORT)));
    itemE.appendChild(XML.cC(doc, "mod_reflex", Integer.toString(modREF)));
    itemE.appendChild(XML.cC(doc, "mod_will", Integer.toString(modWILL)));
    itemE.appendChild(XML.cC(doc, "mod_hp", Integer.toString(modHP)));
    itemE.appendChild(XML.cC(doc, "mod_mana", Integer.toString(modM)));
    itemE.appendChild(XML.cC(doc, "mod_moves", Integer.toString(modMV)));
    itemE.appendChild(XML.cC(doc, "mod_hitroll", Integer.toString(modHR)));
    itemE.appendChild(XML.cC(doc, "mod_damroll", Integer.toString(modDR)));
    itemE.appendChild(XML.cC(doc, "mod_spell_hitroll", Integer.toString(modSHR)));
    itemE.appendChild(XML.cC(doc, "mod_spell_damroll", Integer.toString(modSDR)));
    itemE.appendChild(XML.cC(doc, "mod_archery", Integer.toString(modARC)));
    if (ac != null)
      itemE.appendChild(XML.cC(doc, "ac_dice", ac.toString()));
    if (damage != null)
      itemE.appendChild(XML.cC(doc, "damage_dice", damage.toString()));
    itemE.appendChild(XML.cC(doc, "draw_strength", Integer.toString(drawSTR)));
    if (spells.length > 0) {
      Element e = doc.createElement("spells");
      for (Spell spell : spells)
        e.appendChild(spell.compile(doc));
      itemE.appendChild(e);
    }
    if (charges.length > 0) {
      String line = "";
      for (int i : charges)
        line += i + " ";
      itemE.appendChild(XML.cC(doc, "charges", line));
    }
    itemE.appendChild(XML.cC(doc, "key_id", Long.toString(keyID)));
    if (uses != null)
      itemE.appendChild(XML.cC(doc, "uses_dice", uses.toString()));
    if (duration != null)
      itemE.appendChild(XML.cC(doc, "duration_dice", duration.toString()));
    itemE.appendChild(XML.cC(doc, "target_id", Long.toString(targetID)));
    if (status != null)
      itemE.appendChild(XML.cC(doc, "status", status.name()));
    itemE.appendChild(XML.cC(doc, "max_container_weight", Double.toString(mContainerWeight)));
    itemE.appendChild(XML.cC(doc, "max_capacity", Integer.toString(mCapacity)));
    if (contents.length > 0) {
      Element e = doc.createElement("contents");
      for (ItemProto proto : contents)
        e.appendChild(proto.compile(doc));
      itemE.appendChild(e);
    }
    if (poison != null)
      itemE.appendChild(XML.cC(doc, "poison", poison.name()));
    if (liquid != null)
      itemE.appendChild(XML.cC(doc, "liquid", liquid.type.name()));
    return itemE;
  }

  @Override
  public void load(Element itemE) {
    NodeList children = itemE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("vnum"))
          vnum = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("name"))
          name = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("long_name"))
          longName = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("keywords"))
          keywords = ((Text)e.getFirstChild()).getData().split("\\s");
        else if (e.getTagName().equals("type"))
          type = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("flags"))
          flags = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("level"))
          level = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("weight"))
          weight = Double.parseDouble(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("max_durability"))
          mDur = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("value"))
          try {
            value = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
//        else if (e.getTagName().equals("material"))
//          material = Material.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("required_proficiency"))
          reqProficiency = Proficiencies.Name.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("required_proficiency_level"))
          reqProficiencyLevel = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("slot"))
          slot = Slot.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("affects")) {
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("affect")) {
                Affect a = new Affect();
                a.load(e2);
                affects.add(a);
              }
            }
          }
        } else if (e.getTagName().equals("mod_str"))
          modSTR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_con"))
          modCON = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_dex"))
          modDEX = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_int"))
          modINT = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_wis"))
          modWIS = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_cha"))
          modCHA = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_ac"))
          modAC = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_fortitude"))
          modFORT = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_reflex"))
          modREF = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_will"))
          modWILL = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_hp"))
          modHP = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_mana"))
          modM = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_moves"))
          modMV = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_hitroll"))
          modHR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_damroll"))
          modDR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_spell_hitroll"))
          modSHR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_spell_damroll"))
          modSDR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_archery"))
          modARC = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("ac_dice"))
          try {
            ac = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
        else if (e.getTagName().equals("damage_dice"))
          try {
            damage = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
        else if (e.getTagName().equals("draw_strength"))
          drawSTR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("spells")) {
          ArrayList<Spell> temp = new ArrayList<Spell>();
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("spell")) {
                Spell spell = new Spell();
                spell.load(e2);
                temp.add(spell);
              }
            }
          }
          spells = temp.toArray(new Spell[] {});
        } else if (e.getTagName().equals("charges")) {
          String[] tempS = ((Text)e.getFirstChild()).getData().split("\\s");
          int[] tempI = new int[tempS.length];
          for (int j = 0; j < tempS.length; j++)
            tempI[j] = Integer.parseInt(tempS[j]);
          charges = tempI;
        } else if (e.getTagName().equals("key_id"))
          keyID = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("uses_dice"))
          try {
            uses = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
        else if (e.getTagName().equals("duration_dice"))
          try {
            duration = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
        else if (e.getTagName().equals("target_id"))
          targetID = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("status"))
          status = Status.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("max_container_weight"))
          mContainerWeight = Double.parseDouble(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("max_capacity"))
          mCapacity = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("contents")) {
          ArrayList<ItemProto> temp = new ArrayList<ItemProto>();
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("item_proto")) {
                ItemProto proto = new ItemProto();
                proto.load(e2);
                temp.add(proto);
              }
            }
          }
          contents = temp.toArray(new ItemProto[] {});
        } else if (e.getTagName().equals("poison"))
          poison = Item.Poison.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("liquid"))
          liquid = Liquid.valueOf(((Text)e.getFirstChild()).getData());
      }
    }
  }

  @Override
  public String getFilePath() {
    return Globals.ITEM_PATH + vnum + ".xml";
  }
}

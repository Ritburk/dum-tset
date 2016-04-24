package net.cmacpherson.mud;

import java.util.ArrayList;

import net.cmacpherson.mud.commands.*;
import net.cmacpherson.mud.commands.imm.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import static net.cmacpherson.mud.Globals.IMM_LEVEL;

public abstract class Character implements XML.Processable, Keywords {

  public Output output;
  public Config config = new Config();
  public Location location;
  public String name;
  public String displayName;
  public String[] keywords;
  public static final int MAX_DESCRIPTION_LINE_LENGTH = 79;
  public static final int MAX_DESCRIPTION_SIZE = 80;
  public ArrayList<String> description = new ArrayList<String>(MAX_DESCRIPTION_SIZE);
  public Sex sex;
  public Item[] eq = new Item[EQSlot.values().length];
  public ArrayList<Item> inv = new ArrayList<Item>();
  public ArrayList<Affect> affects = new ArrayList<Affect>();
  public Status status = Status.STANDING;
  public long hp;
  public long mhp;
  public long m;
  public long mm;
  public long mv;
  public long mmv;
  public double hpRegen;
  public double mRegen;
  public double mvRegen;
  public int hr;
  public int dr;
  public int shr; //spell hit roll
  public int sdr; //spell dam roll
  public int level;
  public int ac;
  public int fort;
  public int ref;
  public int will;
  public int align;
  public int _str;
  public int cSTR;
  public int _dex;
  public int cDEX;
  public int _con;
  public int cCON;
  public int _int;
  public int cINT;
  public int _wis;
  public int cWIS;
  public int _cha;
  public int cCHA;
  public Race race;
  public long gold;
  public Skills skills;
  public Proficiencies prof;
  public ArrayList<Character> followers = new ArrayList<Character>();
  public Character following;
  public Group myGroup = new Group(this);
  public Group group = myGroup;
  public Character reply;
  public double arc;
  public int initiative;
  public Character target;
  
  public Character(ServerThread server) {
    if (server != null)
      output = new Output(server);
  }
  
  public abstract void doCombatRound(Output o);
  public abstract void doRegen();
  
  public void follow(Character c) {
    if (c == this) {
      if (following != null)
        following.followers.remove(this);
      following = null;
    } else {
      if (following != null)
        following.followers.remove(this);
      c.followers.add(this);
      following = c;
    }
  }
  
  public String displayName() {
    return displayName;
  }
  
  public String[] keywords() {return keywords;}
  
  public String getDescription() {
    String desc = "";
    for (int i = 0; i < description.size(); i++)
      desc += description.get(i) + "\n\r";
    return desc;
  }
  
  public synchronized void addDescriptionLine(String line) throws DescriptionLengthException,
                                                                  DescriptionSizeException {
    addDescriptionLine(line, -1);
  }
  public synchronized void addDescriptionLine(String line, int n) throws DescriptionLengthException,
                                                                         DescriptionSizeException {
    if (line.length() > MAX_DESCRIPTION_LINE_LENGTH)
      throw new DescriptionLengthException();
    if (description.size() > MAX_DESCRIPTION_SIZE)
      throw new DescriptionSizeException();
    if (n == -1 ||
        n >= description.size())
      description.add(line);
    else
      description.add(n, line);
  }
  public synchronized void removeDescriptionLine(int n) throws DescriptionEmptyException,
                                                               DescriptionSizeException {
    if (description.size() == 0)
      throw new DescriptionEmptyException();
    try {
      description.remove(n);
    } catch (IndexOutOfBoundsException e) {
      throw new DescriptionSizeException();
    }
  }
  public synchronized void clearDescription() {
    description.clear();
  }
  
  public double weight() {
    double w = gold * Globals.WEIGHT_PER_GOLD;
    for (Item item : inv)
      w += item.weight();
    return w;
  }
  public double mWeight() {
    return (_str - 10) * 15 + 200;
  }
  
  public int capacity() {
    return inv.size();
  }
  public int mCapacity() {
    return level + 30;
  }
  
  public double blockPercent() {
    //TODO Character#blockPercent()
    return 0;
  }
  
  public boolean isProducingLight() {
    return eq[EQSlot.LIGHT.ordinal()] != null || 
           isAffectedBy(Affect.Type.LIGHT);
  }
  
  public boolean isAffectedBy(Affect.Type type) {
    for (Affect a : affects)
      if (type == a.type)
        return true;
    return false;
  }
  
  public void addToInv(Item item) throws TooHeavyException, NoSpaceException {
    addToInv(item, false);
  }
  
  public void addToInv(Item item, boolean override) throws TooHeavyException, NoSpaceException {
    if (!override) {
      if (weight() + item.weight() > mWeight())
        throw new TooHeavyException();
      if (item.vnum != Globals.GOLD_VNUM &&
          capacity() + 1 > mCapacity())
        throw new NoSpaceException();
    }
    if (item.vnum == Globals.GOLD_VNUM)
      gold += item.value;
    else
      inv.add(0, item);
  }
  
  @Override
  public Element compile(Document doc) {
    Element c = doc.createElement("character");
    c.appendChild(XML.cC(doc, "location_id", Long.toString(location.id)));
    c.appendChild(XML.cC(doc, "name", name));
    c.appendChild(XML.cC(doc, "display_name", displayName));
    String line = "";
    for (String k : keywords)
      line += k + " ";
    c.appendChild(XML.cC(doc, "keywords", line.trim()));
    Element descE = doc.createElement("description");
    for (String line2 : description)
      descE.appendChild(XML.cC(doc, "line", line2));
    c.appendChild(descE);
    c.appendChild(XML.cC(doc, "sex", sex.name()));
    Element invE = doc.createElement("inventory");
    for (Item item : inv)
      invE.appendChild(item.compile(doc));
    c.appendChild(invE);
    Element affE = doc.createElement("affects");
    for (Affect aff : affects)
      affE.appendChild(aff.compile(doc));
    c.appendChild(affE);
    c.appendChild(XML.cC(doc, "status", status.name()));
    c.appendChild(XML.cC(doc, "hit_points", hp + " " + mhp));
    c.appendChild(XML.cC(doc, "mana", m + " " + mm));
    c.appendChild(XML.cC(doc, "moves", mv + " " + mmv));
    c.appendChild(XML.cC(doc, "hp_regen", Double.toString(hpRegen)));
    c.appendChild(XML.cC(doc, "m_regen", Double.toString(mRegen)));
    c.appendChild(XML.cC(doc, "mv_regen", Double.toString(mvRegen)));
    c.appendChild(XML.cC(doc, "hit_roll", Integer.toString(hr)));
    c.appendChild(XML.cC(doc, "dam_roll", Integer.toString(dr)));
    c.appendChild(XML.cC(doc, "spell_hit_roll", Integer.toString(shr)));
    c.appendChild(XML.cC(doc, "spell_dam_roll", Integer.toString(sdr)));
    c.appendChild(XML.cC(doc, "level", Integer.toString(level)));
    c.appendChild(XML.cC(doc, "armor_class", Integer.toString(ac)));
    c.appendChild(XML.cC(doc, "saves", fort + " " + ref + " " + will));
    c.appendChild(XML.cC(doc, "alignment", Integer.toString(align)));
    c.appendChild(XML.cC(doc, "str", _str + " " + cSTR));
    c.appendChild(XML.cC(doc, "dex", _dex + " " + cDEX));
    c.appendChild(XML.cC(doc, "con", _con + " " + cCON));
    c.appendChild(XML.cC(doc, "int", _int + " " + cINT));
    c.appendChild(XML.cC(doc, "wis", _wis + " " + cWIS));
    c.appendChild(XML.cC(doc, "cha", _cha + " " + cCHA));
    c.appendChild(XML.cC(doc, "race", race.abr.name()));
    c.appendChild(XML.cC(doc, "gold", Long.toString(gold)));
    c.appendChild(skills.compile(doc));
    c.appendChild(prof.compile(doc));
    c.appendChild(XML.cC(doc, "arc", Double.toString(arc)));
    return c;
  }
  
  @Override
  public void load(Element charE) {
    NodeList children = charE.getChildNodes();
    for  (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("location_id"))
          location = output.SERVER.rooms.get(Long.parseLong(((Text)e.getFirstChild()).getData())).location;
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
          sex = Sex.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("inventory")) {
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("item")) {
                Item item = new Item();
                item.load(e2);
                inv.add(item);
                if (item.isEquipped())
                  eq[item.eqSlot.ordinal()] = item;
              }
            }
          }
        } else if (e.getTagName().equals("affects")) {
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("affect")) {
                Affect aff = new Affect();
                aff.load(e2);
                affects.add(aff);
              }
            }
          }
        } else if (e.getTagName().equals("status"))
          status = Status.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("hit_points")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          hp = Long.parseLong(split[0]);
          mhp = Long.parseLong(split[1]);
        } else if (e.getTagName().equals("mana")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          m = Long.parseLong(split[0]);
          mm = Long.parseLong(split[1]);
        } else if (e.getTagName().equals("moves")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          mv = Long.parseLong(split[0]);
          mmv = Long.parseLong(split[1]);
        } else if (e.getTagName().equals("hp_regen"))
          hpRegen = Double.parseDouble(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("m_regen"))
          mRegen = Double.parseDouble(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mv_Regen"))
          mvRegen = Double.parseDouble(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("hit_roll"))
          hr = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("dam_roll"))
          dr = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("spell_hit_roll"))
          shr = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("spell_dam_roll"))
          sdr = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("level"))
          level = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("armor_class"))
          ac = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("saves")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          fort = Integer.parseInt(split[0]);
          ref = Integer.parseInt(split[1]);
          will = Integer.parseInt(split[2]);
        } else if (e.getTagName().equals("alignment"))
          align = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("str")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          _str = Integer.parseInt(split[0]);
          cSTR = Integer.parseInt(split[1]);
        } else if (e.getTagName().equals("dex")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          _dex = Integer.parseInt(split[0]);
          cDEX = Integer.parseInt(split[1]);
        } else if (e.getTagName().equals("con")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          _con = Integer.parseInt(split[0]);
          cCON = Integer.parseInt(split[1]);
        } else if (e.getTagName().equals("int")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          _int = Integer.parseInt(split[0]);
          cINT = Integer.parseInt(split[1]);
        } else if (e.getTagName().equals("wis")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          _wis = Integer.parseInt(split[0]);
          cWIS = Integer.parseInt(split[1]);
        } else if (e.getTagName().equals("cha")) {
          String[] split = ((Text)e.getFirstChild()).getData().split("\\s");
          _cha = Integer.parseInt(split[0]);
          cCHA = Integer.parseInt(split[1]);
        } else if (e.getTagName().equals("race"))
          race = new Race(Race.Abr.valueOf(((Text)e.getFirstChild()).getData()));
        else if (e.getTagName().equals("gold"))
          gold = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("skills")) {
          skills = new Skills();
          skills.load(e);
        } else if (e.getTagName().equals("proficiencies")) {
          prof = new Proficiencies();
          prof.load(e);
        } else if (e.getTagName().equals("arc"))
          arc = Double.parseDouble(((Text)e.getFirstChild()).getData());
      }
    }
  }
  
  public enum Sex {
    NEUTRAL,
    MALE,
    FEMALE
  }
  
  public static final int STANDING      = 1 << 0;
  public static final int RESTING       = 1 << 1;
  public static final int FIGHTING      = 1 << 2;
  public static final int SLEEPING      = 1 << 3;
  public static final int CONCENTRATING = 1 << 4;
  public static final int ANY_STATUS    = STANDING | RESTING | FIGHTING | SLEEPING | CONCENTRATING;
  public enum Status {
    STANDING,
    RESTING,
    FIGHTING,
    SLEEPING,
    CONCENTRATING
  }
  
  public enum EQSlot {
    LIGHT,
    RING1,
    RING2,
    NECK,
    BODY,
    HEAD,
    LEGS,
    FEET,
    HANDS,
    ARMS,
    ABOUT,
    WAIST,
    WRIST1,
    WRIST2,
    OFFHAND,
    WIELD
  }
  
  public boolean isWearingNothing() {
    boolean nude = true;
    for (Item i : eq)
      nude &= i == null;
    return nude;
  }
  
  public Item[] wear(Item item) throws InsufficientLevelException,
                                       InsufficientSkillException,
                                       CursedItemException, 
                                       WrongAlignmentException,
                                       WeaponBalanceException {
    switch (item.slot) {
    case RING:
      if (eq[EQSlot.RING1.ordinal()] == null)
        equip(item, EQSlot.RING1);
      else if (eq[EQSlot.RING2.ordinal()] == null)
        equip(item, EQSlot.RING2);
      else
        return equip(item, EQSlot.RING1);
      break;
    case WRIST:
      if (eq[EQSlot.WRIST1.ordinal()] == null)
        equip(item, EQSlot.WRIST1);
      else if (eq[EQSlot.WRIST1.ordinal()] == null)
        equip(item, EQSlot.WRIST2);
      else
        return equip(item, EQSlot.WRIST1);
      break;
    case WIELD:
      if (isUsingTwoHandedItem())
        return equip(item, EQSlot.WIELD);
      if (eq[EQSlot.WIELD.ordinal()] == null)
        equip(item, EQSlot.WIELD);
      else if (eq[EQSlot.OFFHAND.ordinal()] == null)
        equip(item, EQSlot.OFFHAND);
      else {
        if (eq[EQSlot.WIELD.ordinal()].isType(Item.WEAPON) &&
            !eq[EQSlot.OFFHAND.ordinal()].isType(Item.WEAPON))
          return equip(item, EQSlot.WIELD);
        else if (!eq[EQSlot.WIELD.ordinal()].isType(Item.WEAPON) &&
                 eq[EQSlot.OFFHAND.ordinal()].isType(Item.WEAPON))
          return equip(item, EQSlot.OFFHAND);
        else if (!eq[EQSlot.WIELD.ordinal()].isType(Item.WEAPON) &&
                 !eq[EQSlot.OFFHAND.ordinal()].isType(Item.WEAPON))
          return equip(item, EQSlot.WIELD);
        else {
          if (item.weight > eq[EQSlot.WIELD.ordinal()].weight)
            return equip(item, EQSlot.OFFHAND);
          else
            return equip(item, EQSlot.WIELD);
        } 
      }
      break;
    default:
      return equip(item, EQSlot.valueOf(item.slot.name()));
    }
    return null;
  }
  
  public Item[] wear(Item item, EQSlot slot) throws InsufficientLevelException,
                                                    InsufficientSkillException,
                                                    CursedItemException, 
                                                    WrongAlignmentException,
                                                    WeaponBalanceException {
    return equip(item, slot);
  }
  
  private Item[] equip(Item item, EQSlot slot) throws InsufficientLevelException,
                                                      InsufficientSkillException,
                                                      CursedItemException, 
                                                      WrongAlignmentException,
                                                      WeaponBalanceException {
    if (item.level > level)
      throw new InsufficientLevelException();
    if (item.reqProficiency != null &&
        item.reqProficiencyLevel < prof.getLevel(item.reqProficiency))
      throw new InsufficientSkillException();
    Item[] remove = null;
    if (item.isType(Item.TWO_HANDED))
      remove = new Item[] {eq[EQSlot.WIELD.ordinal()], eq[EQSlot.OFFHAND.ordinal()]};
    else
      if (eq[slot.ordinal()] != null)
        remove = new Item[] {eq[slot.ordinal()]};
    if (slot == EQSlot.WIELD) {
      if (eq[EQSlot.OFFHAND.ordinal()] != null &&
          eq[EQSlot.OFFHAND.ordinal()].isType(Item.WEAPON) &&
          eq[EQSlot.OFFHAND.ordinal()].weight > item.weight)
        throw new WeaponBalanceException(eq[EQSlot.OFFHAND.ordinal()]);
    }
    if (slot == EQSlot.OFFHAND) {
      if (item.isType(Item.WEAPON) &&
          eq[EQSlot.WIELD.ordinal()] != null &&
          eq[EQSlot.WIELD.ordinal()].isType(Item.WEAPON) &&
          eq[EQSlot.WIELD.ordinal()].weight < item.weight)
        throw new WeaponBalanceException(eq[EQSlot.WIELD.ordinal()]);
      if (isUsingTwoHandedItem())
        remove = new Item[] {eq[EQSlot.WIELD.ordinal()]};
    }
    if (remove != null)
      for (int i = 0; i < remove.length; i++)
        if (remove[i].is(Item.CURSED))
          throw new CursedItemException(remove[i]);
    if (remove != null)
      for (int i = 0; i < remove.length; i++) {
        remove[i].eqSlot = null;
        modStats(item, -1);
      }
    if (remove != null &&
        remove.length > 1)
      eq[EQSlot.OFFHAND.ordinal()] = null;
    if ((item.is(Item.ANTI_EVIL) &&
         isEvil()) ||
        (item.is(Item.ANTI_GOOD) &&
         isGood()) ||
        (item.is(Item.ANTI_NEUTRAL) &&
         isNeutral()))
      throw new WrongAlignmentException();
    eq[slot.ordinal()] = item;
    item.eqSlot = slot;
    modStats(item, 1);
    return remove;
  }
  
  public void remove(Item item) throws CursedItemException {
    if (item.is(Item.CURSED))
      throw new CursedItemException(item);
    eq[item.eqSlot.ordinal()] = null;
    item.eqSlot = null;
    modStats(item, -1);
  }
  
  private void modStats(Item item, int sign) {
    if (!(sign == 1 || sign == -1))
      throw new IllegalArgumentException("sign must be 1 or -1");
    for (Affect affect : item.affects)
      if (sign == 1)
        affect.equipOn(this);
      else
        affect.equipOff(this);
    cSTR += item.modSTR * sign;
    cDEX += item.modDEX * sign;
    cCON += item.modCON * sign;
    cINT += item.modINT * sign;
    cWIS += item.modWIS * sign;
    cCHA += item.modCHA * sign;
    fort += item.modFORT * sign;
    ref += item.modREF * sign;
    will += item.modWILL * sign;
    mhp += item.modHP * sign;
    mm += item.modM * sign;
    mmv += item.modMV * sign;
    hr += item.modHR * sign;
    dr += item.modDR * sign;
    hr += item.modEHR * sign;
    dr += item.modEDR * sign;
    shr += item.modSHR * sign;
    sdr += item.modSDR * sign;
    shr += item.modESHR * sign;
    sdr += item.modESDR * sign;
    arc += item.modARC * sign;
    ac += item.modAC * sign;
    ac += item.modEAC * sign;
    ac += item.ac;
  }
  
  public boolean isUsingTwoHandedItem() {
    return eq[EQSlot.WIELD.ordinal()] != null &&
           eq[EQSlot.WIELD.ordinal()].isType(Item.TWO_HANDED);
  }
  
  public boolean isEvil() {return align < -333;}
  public boolean isGood() {return align > 333;}
  public boolean isNeutral() {return !isEvil() && !isGood();}
  
  public boolean doCommand(String line) {
    line = line.trim();
    if (line.equals(""))
      if (this instanceof PC) {
        ((PC)this).prompt();
        return true;
      }
    char sym = line.charAt(0);
    switch (sym) {
    case '.':
      line = "chat " + line.substring(1);
    case ';':
      line = "gtell " + line.substring(1);
    }
    String[] split = Utils.readArg(line);
    String cmd = split[0];
    String args = split[1];
    if (args.equals(""))
      args = null;
    for (Object[] command : COMMAND_MAP) {
      if (cmd.regionMatches(true, 0, (String)command[0], 0, cmd.length()) &&
          level >= (Integer)command[2]) {
        if (((1 << status.ordinal()) & (Integer)command[3]) == 0) {
          output.toChar("You can't do that right now.", this);
          output.flush();
          return true;
        } else {
          boolean temp = ((Command)command[1]).execute(this, cmd, args, output);
          output.flush();
          return temp;
        }
      }
    }
    boolean temp = INVALID_CMD.execute(this, cmd, args, output);
    output.flush();
    return temp;
  }
  
  public void forceOnThread(String line, Character c) {
    if (this instanceof PC) {
      if (c != null)
        output.toChar("%N$n forces you to '" + line + "'.", this, c);
      else
        output.toChar("%NThe server forces you to '" + line + "'.", this);
      if (((PC)this).client != null) {
        ((PC)this).client.queue.push(line);
        ((PC)this).client.notifyQueue();
      }
    }
  }
  
  public void force(String line, Output o, boolean newline) {
    if (o == null)
      o = output;
    String[] split = Utils.readArg(line);
    String cmd = split[0];
    String args = split[1];
    if (newline)
      o.toChar("", this);
    if (args.equals(""))
      args = null;
    for (Object[] command : COMMAND_MAP) {
      if (cmd.regionMatches(true, 0, (String)command[0], 0, cmd.length()))
        ((Command)command[1]).execute(this, cmd, args, o);
    }
    if (newline)
      o.flush();
  }
  
  private final InvalidCommand INVALID_CMD = new InvalidCommand();
  public Object[][] COMMAND_MAP = new Object[][] {
      //command       class          level required      min status

      //cast
      //kill

      //consider
      //examine
      //channels
      //configuration
      //title

      //auction
      //bid

      //close
      //drink
      //fill
      //hold
      //lock
      //open
      //pick
      //quaff
      //recite
      //sell
      //buy
      //sacrifice
      //unlock
      
      //flee
      //rescue

      //IMM_COMMANDS
      //force
      //hotboo
      //hotboot
      //return
      //immtalk
      
      {"north",       new Move(Direction.NORTH), 0,   STANDING},
      {"south",       new Move(Direction.SOUTH), 0,   STANDING},
      {"east",        new Move(Direction.EAST),  0,   STANDING},
      {"west",        new Move(Direction.WEST),  0,   STANDING},
      {"up",          new Move(Direction.UP),    0,   STANDING},
      {"down",        new Move(Direction.DOWN),  0,   STANDING},
      
      {"close",       new Close(),               0,   STANDING | RESTING | FIGHTING},
      {"drop",        new Drop(),                0,   STANDING | RESTING | FIGHTING},
      {"equipment",   new Equipment(),           0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"get",         new Get(),                 0,   STANDING | RESTING | FIGHTING},
      {"give",        new Give(),                0,   STANDING | RESTING | FIGHTING},
      {"group",       new net.cmacpherson.mud.commands.Group(), 0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"inventory",   new Inventory(),           0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"look",        new Look(),                0,   STANDING | RESTING | FIGHTING},
      {"move",        new Move(null),            0,   STANDING},
      {"remove",      new Remove(),              0,   STANDING | RESTING | FIGHTING},
      {"rest",        new Rest(),                0,   STANDING | RESTING | SLEEPING},
      {"put",         new Put(),                 0,   STANDING | RESTING | FIGHTING},
      {"scan",        new Scan(),                0,   STANDING | RESTING | FIGHTING},
      {"score",       new Score(),               0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"sleep",       new Sleep(),               0,   STANDING | RESTING | SLEEPING},
      {"stand",       new Stand(),               0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"statistics",  new Score(),               0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"stats",       new Score(),               0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"take",        new Get("take"),           0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"wake",        new Wake(),                0,   STANDING | RESTING | SLEEPING},
      {"wear",        new Wear(),                0,   STANDING | RESTING | FIGHTING},
      {"wield",       new Wield(),               0,   STANDING | RESTING | FIGHTING},
      
      {"answer",      new Answer(),              0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"ask",         new Ask(),                 0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"chat",        new Chat(),                0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"gtell",       new GTell(),               0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"question",    new Ask(),                 0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"reply",       new Reply(),               0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"say",         new Say(),                 0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"tell",        new Tell(),                0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"yell",        new Yell(),                0,   STANDING | RESTING | FIGHTING},
      
      {"areas",       new Areas(),               0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"compare",     new Compare(),             0,   STANDING | RESTING | FIGHTING},
      {"description", new Description(),         0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"emote",       new Emote(),               0,   STANDING | RESTING | FIGHTING},
      {"follow",      new Follow(),              0,   STANDING | RESTING | FIGHTING},
      {"prompt",      new Prompt(),              0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"qui",         new Qui(),                 0,   ANY_STATUS},
      {"quit",        new Quit(),                0,   STANDING | RESTING | SLEEPING},
      {"where",       new Where(),               0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"who",         new Who(),                 0,   STANDING | RESTING | SLEEPING | FIGHTING},
      
      {"commands",    new Commands(),            0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"help",        new Help(),                0,   STANDING | RESTING | SLEEPING | FIGHTING},
      {"socials",     new Socials(),             0,   STANDING | RESTING | SLEEPING | FIGHTING},
      
      {"force",       new Force(),               IMM_LEVEL,   ANY_STATUS},
      {"gift",        new Gift(),                IMM_LEVEL,   ANY_STATUS},
      {"goto",        new Goto(),                IMM_LEVEL,   ANY_STATUS},
      {"loaditem",    new LoadItem(),            IMM_LEVEL,   ANY_STATUS},
      {"loadmob",     new LoadMob(),             IMM_LEVEL,   ANY_STATUS},
      {"shutdow",     new Shutdow(),             IMM_LEVEL,   ANY_STATUS},
      {"shutdown",    new Shutdown(),            IMM_LEVEL,   ANY_STATUS},
      {"slay",        new Slay(),                IMM_LEVEL,   ANY_STATUS},
      {"transfer",    new Transfer(),            IMM_LEVEL,   ANY_STATUS},
      
      {"#cpitem",     new CPItem(),              IMM_LEVEL,   ANY_STATUS},
      {"#cpmob",      new CPMob(),               IMM_LEVEL,   ANY_STATUS},
  };
}

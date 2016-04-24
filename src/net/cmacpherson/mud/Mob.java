package net.cmacpherson.mud;

public class Mob extends Character {

  public long protoVNUM;
  public AISpec[] specs;
  public int resetID;
  
  public Mob(long protoVNUM) {
    super(null);
    this.protoVNUM = protoVNUM;
  }
  
  @Override
  public void doCombatRound(Output o) {
    //TODO Mob#doCombatRound()
  }
  
  @Override
  public void doRegen() {
    //TODO Mob#doRegen()
  }
  
  @Override
  public String getFilePath() {return null;}
}

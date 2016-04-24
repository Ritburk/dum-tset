package net.cmacpherson.mud;

public class ResetData {
  /*
   * just thoughts here mostly
   * 
   * maybe this will contain a list of strings that are used
   * as commands to generate only certain things within the
   * area and everything else that's default will just be
   * loaded normally.  this implies that we will not save
   * data on compile from rooms for data pertaining to mobs
   * and items and exits.
   * 
   * things we will need reset data for:
   * items (vnums, on floor, also contents of said items)
   * mobs  (vnums, also include items carried or equipped)
   * exits (this one will be trickier but if we have the
   *   room id and the exit direction it shouldn't be too
   *   difficult, we would need for both sides of the doors
   *   unless we are able to link them as in have one exit
   *   class being shared between the two rooms if they are
   *   loaded as linked?)
   *   
   * how will we build worlds from within the game? should we
   * create the world then edit a reset data with commands in
   * order to create the rest of the things needed for the
   * area? how would we edit this? how long would the reset
   * data files be? would this just require builders to keep
   * track of their own scripts? how would we build items?
   * generate a generic item under a vnum and edit the vnum
   * prototype? should ranges for weapon values be system
   * controlled or builder controlled?
   */
}

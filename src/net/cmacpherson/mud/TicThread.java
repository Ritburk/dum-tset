package net.cmacpherson.mud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

public class TicThread extends Thread {
  
  private Hashtable<Integer, ArrayList<Character>> fighting = new Hashtable<Integer, ArrayList<Character>>();
  public boolean noFight = true;
  public boolean shutdown = false;
  private ServerThread server;
  private Output o;
  private boolean resetComplete;
  
  public TicThread(ServerThread server) {
    this.server = server;
    o = new Output(server);
  }
  
  public void removeFromCombat(int initiative, Character c) {
    synchronized (fighting) {
      for (int i = 0; i < fighting.get(initiative).size(); i++)
        if (c == fighting.get(initiative).get(i)) {
          fighting.get(initiative).set(i, null);
          resetCombatStatsFor(c);
        }
    }
  }
  
  public void addToCombat(int initiative, Character c) {
    synchronized (fighting) {
      if (fighting.containsKey(initiative))
        fighting.get(initiative).add(c);
      else {
        ArrayList<Character> chars = new ArrayList<Character>();
        chars.add(c);
        fighting.put(initiative,  chars);
      }
    }
  }

  public void run() {
    ArrayList<int[]> removeChars = new ArrayList<int[]>();
    resetComplete = true;
    loop:
    while (!shutdown) {
      if (!resetComplete) {
        try {
          sleep(100);
        } catch (InterruptedException e) {}
        continue;
      }
      for (Character c : server.loggedPCs)
        c.doRegen();
      for (Character c : server.mobs)
        c.doRegen();
      for (int i = 0; i < 10; i++) {
        if (shutdown)
          break loop;
        if (!noFight) {
          ArrayList<Integer> keys = new ArrayList<Integer>(fighting.keySet());
          Collections.sort(keys, new Comparator<Integer>() {
            @Override
            public int compare(Integer int1, Integer int2) {
              if (int2 > int1)
                return -1;
              if (int1 > int2)
                return 1;
              return 0;
            }});
          for (int initiative : keys) { //should do initiative in descending order...
            ArrayList<Character> chars = fighting.get(initiative);
            for (int j = 0; j < fighting.size(); j++) {
              synchronized (chars) {
                Character c = chars.get(j);
                if (c == null)
                  removeChars.add(new int[] {initiative, j});
                else
                  c.doCombatRound(o);
              }
            }
          }
          for (int[] index : removeChars)
            fighting.get(index[0]).remove(index[1]);
          removeChars.clear();
        }
        resetComplete = false;
        new Thread() {
          @Override
          public void run() {
            resetCombatStats();
          }
        }.start();
        try {
          sleep(3000);
        } catch (InterruptedException e) {}
      }
    }
  }
  
  private void resetCombatStats() {
    synchronized (fighting) {
      Enumeration<Integer> e = fighting.keys();
      while (e.hasMoreElements()) {
        int i = e.nextElement();
        for (Character c : fighting.get(i))
          resetCombatStatsFor(c);
      }
    }
    resetComplete = true;
  }
  
  private void resetCombatStatsFor(Character c) {
    //TODO TicThread#resetCombatStatsFor(Character)
    //reset vars for tracking if block or parry things of that nature were used
  }
}

package com.MikeTheShadow.ShadowXP.Util;

public class CustomUser
{
    String name;
    String UID;
    int level;
    int currentXP;
    int totalXP;
    int lastHP;

    public CustomUser(String name, String UID, int level, int currentXP, int totalXP, int lastHP)
    {
        this.name = name;
        this.UID = UID;
        this.level = level;
        this.currentXP = currentXP;
        this.totalXP = totalXP;
        this.lastHP = lastHP;
    }

    //getters
    public String getName() {return name;}
    public String  getUID() {return UID;}
    public int getLevel(){return level;}
    public int getCurrentXP() {return currentXP;}
    public int getTotalXP() {return totalXP;}
    public int getLastHP() { return lastHP; }

    //setters
    public void setXP(int change) { this.currentXP = change; }
    public void setTotalXP(int change) { this.totalXP = change; }
    public void setLevel(int change) { this.level = change; }
    public void setLastHP(int lastHP) { this.lastHP = lastHP; }

    //additions
    public void addXP(int change){ this.currentXP += change; }
    public void addTotalXP(int change) { this.totalXP += change; }
    public void addLevel(int change) { this.level += change; }

}

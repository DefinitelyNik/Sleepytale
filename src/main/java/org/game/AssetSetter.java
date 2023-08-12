package org.game;

import entity.OldMan;
import monster.GreenSlime;
import object.Door;
import object.Key;

/**
 * Класс, создающий объекты и сущности и назначающий их координаты на карте
 */
public class AssetSetter {
    GamePanel gp;
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        gp.obj[0] = new Door(gp);
        gp.obj[0].worldX = gp.tileSize * 12;
        gp.obj[0].worldY = gp.tileSize * 23;

        gp.obj[1] = new Key(gp);
        gp.obj[1].worldX = gp.tileSize * 12;
        gp.obj[1].worldY = gp.tileSize * 20;

        gp.obj[2] = new Door(gp);
        gp.obj[2].worldX = gp.tileSize * 14;
        gp.obj[2].worldY = gp.tileSize * 26;
    }

    public void setNPC() {
        gp.npc[0] = new OldMan(gp);
        gp.npc[0].worldX = gp.tileSize * 21;
        gp.npc[0].worldY = gp.tileSize * 21;
    }

    public void setMonster() {
        int i = 0;

        gp.monster[i] = new GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 21;
        gp.monster[i].worldY = gp.tileSize * 38;
        i++;

        gp.monster[i] = new GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 23;
        gp.monster[i].worldY = gp.tileSize * 42;
        i++;

        gp.monster[i] = new GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 24;
        gp.monster[i].worldY = gp.tileSize * 37;
        i++;

        gp.monster[i] = new GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 34;
        gp.monster[i].worldY = gp.tileSize * 42;
        i++;

        gp.monster[i] = new GreenSlime(gp);
        gp.monster[i].worldX = gp.tileSize * 38;
        gp.monster[i].worldY = gp.tileSize * 42;
        i++;

    }
}

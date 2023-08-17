package object;

import entity.Entity;
import org.game.GamePanel;

public class WoodenShield extends Entity {

    public WoodenShield(GamePanel gp) {
        super(gp);

        name = "Wood Shield";
        down1 = setup("/objects/shield_wood", gp.tileSize, gp.tileSize);
        defenceValue = 1;
        description = "[" + name + "]\nAn old shield.\nIt has some holes in it.";
    }
}

package object;

import entity.Entity;
import org.game.GamePanel;

public class NormalSword extends Entity {

    public NormalSword(GamePanel gp) {
        super(gp);

        name = "Normal Sword";
        down1 = setup("/objects/sword_normal", gp.tileSize, gp.tileSize);
        attackValue = 1;
    }
}

package object;

import entity.Entity;
import org.game.GamePanel;

/**
 * Класс объекта "сердце"
 * Нужен для создания объектов этого класа
 * Устанавливает картинку объекта и масштабирует ее при помощи uTool'а
 * У здоровья есть 3 картинки(пустое сердце, половинка сердца, полное сердце)
 * По сути, это здоровье игрока, которое отображается на экране
 */
public class Heart extends Entity {

    public Heart(GamePanel gp) {

        super(gp);

        name = "Heart";
        image = setup("/objects/heart_full", gp.tileSize, gp.tileSize);
        image2 = setup("/objects/heart_half", gp.tileSize, gp.tileSize);
        image3 = setup("/objects/heart_blank", gp.tileSize, gp.tileSize);
    }
}

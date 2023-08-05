package entity;

import org.game.GamePanel;
import org.game.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Абстрактный класс для любый сущностей(игрок, монстр и т.д.)
 * Содержит данные о всех сущностях в игре, включая игрока
 */
public class Entity {
    GamePanel gp;
    public BufferedImage straight, up1, up2, up3, down1, down2, down3,
            left1, left2, left3, right1, right2, right3, // Переменные, отвечающие за направление игрока
            sleep1, sleep2, sleep3; // Переменнные, отвечающие за анимацию сна
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2,
            attackLeft1, attackLeft2, attackRight1, attackRight2;
    public BufferedImage image, image2, image3; // Картинки для отрисовки сердечек
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48); // Область коллизии сущности(Квадрат, размер которого меньше, чем размер модели сущности)
    public Rectangle attackArea = new Rectangle(0,0,0,0);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false; // Переменная, указывающая на то, есть ли коллизия или нет
    String[] dialogues = new String[20]; // Общее кол-во диалогов

    //Состояние сущности
    public int worldX, worldY; // Координаты сущности на карте
    public String direction = "straight"; // Направление движения сущности(по дефолту сущность смотрит прямо)
    public int spriteNum = 1; // Переменная для отрисовки анимации передвижения сущностей
    int dialogueIndex = 0; // Переменная, отвечающая за номер реплики в диалоге
    public boolean collisionOn = false; // Переменная, которая нужна для проверки коллизии
    public boolean invincible = false; // Переменная, указывающая на то, неузвим ли игрок или нет
    boolean attacking = false; // Переменная, указывающая на то, атакует ли игрок или нет

    //Счетчики
    public int spriteCounter = 0; // Переменная для отрисовки анимации передвижения сущностей
    public int actionLockCounter = 0; // Переменная, которая нужна для того, чтобы сущности не совершали миллион действий в секунду(что-то типа задержки между действиями)
    public int invincibleCounter = 0; // Переменная, которая нужна для отключения состояния неуязвимости игрока

    //Атрибуты сущностей
    public int type; // 0 = игрок, 1 = npc, 2 = монстр
    public String name; // Имя объекта или монстра, или npc
    public int speed; // Скорость передвижения игрока по карте
    public int maxLife; // Максимальное кол-во здоровья у монстра или игрока
    public int life; // Текущее кол-во здоровья

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void setAction() {

    }

    /**
     * Метод, позволяющий сущностям говорить
     * По сути, он просто поочередно меняет строки диалога
     * Также меняет направление сущности в сторону игрока
     */
    public void speak() {

        if(dialogues[dialogueIndex] == null) {
            dialogueIndex = 0;
        }

        gp.ui.currentDialogue = dialogues[dialogueIndex];
        dialogueIndex++;

        switch (gp.player.direction) {
            case "up" -> direction = "down";
            case "down" -> direction = "up";
            case "left" -> direction = "right";
            case "right" -> direction = "left";
            default -> direction = "straight";
        }
    }

    public void update() {
        setAction();
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkEntity(this, gp.npc);
        gp.cChecker.checkEntity(this, gp.monster);
        boolean contactPlayer = gp.cChecker.checkPlayer(this);

        if(this.type == 2 && contactPlayer) {
            if(!gp.player.invincible) {
                gp.player.life -= 1;
                gp.player.invincible = true;
            }
        }

        if(!collisionOn) {
            switch (direction) {
                case "up" -> worldY -= speed;
                case "down" -> worldY += speed;
                case "left" -> worldX -= speed;
                case "right" -> worldX += speed;
            }
        }

        spriteCounter++;
        if(spriteCounter > 12) {
            if(spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }

        if(invincible) {
            invincibleCounter++;
            if(invincibleCounter > 40) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            switch (direction) {
                case "up" -> {
                    if (spriteNum == 1) {image = up1;}
                    if (spriteNum == 2) {image = up2;}
                }
                case "down" -> {
                    if (spriteNum == 1) {image = down1;}
                    if (spriteNum == 2) {image = down2;}
                }
                case "left" -> {
                    if (spriteNum == 1) {image = left1;}
                    if (spriteNum == 2) {image = left2;}
                }
                case "right" -> {
                    if (spriteNum == 1) {image = right1;}
                    if (spriteNum == 2) {image = right2;}
                }
            }

            if(invincible) g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));

            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    /**
     * Настройка изображения модели сущности(масштабирование)
     */
    public BufferedImage setup(String imagePath, int width, int height) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath + ".png")));
            image = uTool.scaleImage(image, width, height);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}

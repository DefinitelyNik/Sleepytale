package entity;

import org.game.GamePanel;
import org.game.KeyHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Класс игрока
 * Является сущностью, поэтому наследует этот класс
 * Тут устанавливается положение игрока на карте, его скорость, коллизия и др.
 * Устанавливаются анимации его передвижений
 * Обрабатываются его передвижения по карте
 * Отрисовывается сам игрок и его анимации
 */
public class Player extends Entity{
    KeyHandler keyH;
    public final int screenX; // координата игрока по оси Х
    public final int screenY; // координата игрока по оси Y
    int hasKey = 0;
    private long startTime = 0;

    public Player(GamePanel gp, KeyHandler keyH) {

        super(gp);

        this.keyH = keyH;

        screenX = gp.screenWidth /2 - (gp.tileSize /2);
        screenY = gp.screenHeight /2 - (gp.tileSize /2);

        solidArea = new Rectangle(26, 32, 12, 24);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        attackArea.width = 36;
        attackArea.height = 36;

        setDefaultValues();
        getPlayerImage();
        getPlayerAttackImage();
    }


    /**
     * Метод, устанавливающий дефолтные параметры игрока
     */
    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "straight";

        // Статус игрока
        maxLife = 6; // 1 жизнь = 1 половина сердечка на экране
        life = maxLife;
    }

    /**
     * Метод, получающий картинки для анимации передвижения игрока
     */
    public void getPlayerImage() {
        straight = setup("/player/player_straight", gp.tileSize, gp.tileSize);
        up1 = setup("/player/player_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/player/player_up_2", gp.tileSize, gp.tileSize);
        //up3 = setup("/player/player_up_1", gp.tileSize, gp.tileSize);
        down1 = setup("/player/player_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/player/player_down_2", gp.tileSize, gp.tileSize);
        down3 = setup("/player/player_down_3", gp.tileSize, gp.tileSize);
        left1 = setup("/player/player_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/player/player_left_2", gp.tileSize, gp.tileSize);
        left3 = setup("/player/player_left_3", gp.tileSize, gp.tileSize);
        right1 = setup("/player/player_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/player/player_right_2", gp.tileSize, gp.tileSize);
        right3 = setup("/player/player_right_3", gp.tileSize, gp.tileSize);
        sleep1 = setup("/player/player_sleep_1", gp.tileSize, gp.tileSize);
        sleep2 = setup("/player/player_sleep_2", gp.tileSize, gp.tileSize);
        sleep3 = setup("/player/player_sleep_3", gp.tileSize, gp.tileSize);
    }

    public void getPlayerAttackImage() {
        attackUp1 = setup("/player/boy_attack_up_1", gp.tileSize, gp.tileSize * 2);
        attackUp2 = setup("/player/boy_attack_up_2", gp.tileSize, gp.tileSize * 2);
        attackDown1 = setup("/player/boy_attack_down_1", gp.tileSize, gp.tileSize * 2);
        attackDown2 = setup("/player/boy_attack_down_2", gp.tileSize, gp.tileSize * 2);
        attackLeft1 = setup("/player/boy_attack_left_1", gp.tileSize * 2, gp.tileSize);
        attackLeft2 = setup("/player/boy_attack_left_2", gp.tileSize * 2, gp.tileSize);
        attackRight1 = setup("/player/boy_attack_right_1", gp.tileSize * 2, gp.tileSize);
        attackRight2 = setup("/player/boy_attack_right_2", gp.tileSize * 2, gp.tileSize);
    }

    /**
     * Метод, обновляющий положение игрока на карте, его коллизию и анимации передвижения
     */
    public void update() {

        if(attacking) {
            attacking();
        }
        else if(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed || keyH.ePressed) {
            startTime = System.nanoTime();

            if(keyH.upPressed) {
                direction = "up";
            }
            if(keyH.downPressed) {
                direction = "down";
            }
            if(keyH.leftPressed) {
                direction = "left";
            }
            if(keyH.rightPressed) {
                direction = "right";
            }

            // Проверка коллизии плитки
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // Проверка коллизии объектов
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            // Проверка коллизии сущностей
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
            interactNPC(npcIndex);

            // Проверка коллизии монстров
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            contactMonster(monsterIndex);

            // Проверка ивентов
            gp.eHandler.checkEvent();

            // Если коллизии нет, игрок может двигаться
            if(!collisionOn && !keyH.ePressed) {
                switch (direction) {
                    case "up" -> worldY -= speed;
                    case "down" -> worldY += speed;
                    case "left" -> worldX -= speed;
                    case "right" -> worldX += speed;
                }
            }

            gp.keyH.ePressed = false;

            // Выбор анимации модельки игрока
            spriteCounter++;
            if(spriteCounter > 12) {
                if(spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 3;
                }
                else if (spriteNum == 3) {
                    spriteNum = 4;
                }
                else if (spriteNum == 4) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }

        if(invincible) {
            invincibleCounter++;
            if(invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    /**
     * Метод, реализующий атаки игрока.
     * Здесь прорисовывается анимация атаки(просто меняются модельки).
     * Также здесь изменяется положение игрока при атаке, так как модельки атаки имеют форму прямоугольника,
     * а не квадрата, как у обычных моделек.
     * Также здесь проверяется коллизия области атаки с монстром, чтобы правильно определять,
     * поподает ли игрок по монстру.
     */
    public void attacking() {
        spriteCounter++;

        if(spriteCounter <= 5) {
            spriteNum = 1;
        }
        if(spriteCounter > 5 && spriteCounter <= 25) {
            spriteNum = 2;

            // сохраняем текущее значение worldX, worldY, solidArea
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Изменяем значения worldX, worldY для зоны атаки
            switch (direction) {
                case "up" -> worldY -= gp.tileSize;
                case "down" -> worldY += gp.tileSize;
                case "left" -> worldX -= gp.tileSize;
                case "right" -> worldX += gp.tileSize;
            }

            // область атаки становится solidArea
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;

            // проверяем коллизию с монстром с учётом обновлённых worldX, worldY и solidArea
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            damageMonster(monsterIndex);

            // После проверки коллизии возвращаем оригинальные значения переменных
            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;
        }
        if(spriteCounter > 25) {
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
    }

    /**
     * Метод, позволяющий игроку поднимать предметы(объекты)
     */
    public void pickUpObject(int i) {
        if(i != 999) {
            //Тут будут новые объекты
            String objectName = gp.obj[i].name;

            switch (objectName) {
                case "Key" -> {
                    hasKey++;
                    gp.obj[i] = null;
                }
                case "Door" -> {
                    if (hasKey > 0) {
                        gp.obj[i] = null;
                        hasKey--;
                    }
                }
            }
        }
    }

    /**
     * Метод, позволяющий взаимодействовать с другими сущностями
     * Например, можно поговорить с кем-либо
     */
    public void interactNPC(int i) {

        if(gp.keyH.ePressed) {
            if(i != 999) {
                gp.gameState = gp.dialogueState;
                gp.npc[i].speak();
            } else {
                attacking = true;
            }
        }
    }

    /**
     * Метод для контактов игрока с монстрами.
     * Если игрок касается монстра и игрок не в состоянии неуязвимости, то ему наносится урон.
     */
    public void contactMonster(int i) {
        if(i != 999) {
            if(!invincible) {
                life -= 1;
                invincible = true;
            }
        }
    }


    /**
     * Метод для нанесения дамага монстру
     * Здесь учитывается временное состояние неуязвимости монстра
     */
    public void damageMonster(int i) {
        if(i != 999) {
            if(!gp.monster[i].invincible) {
                gp.monster[i].life -= 1;
                gp.monster[i].invincible = true;

                if(gp.monster[i].life <= 0) {
                    gp.monster[i].dying = true;
                }
            }
        }
    }

    /**
     * Метод, отрисовывающий всё, что связано с игроком
     */
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int tempScreenX = screenX;
        int tempScreenY = screenY;

        switch (direction) {
            case "straight" -> image = straight;
            case "up" -> {
                if(!attacking) {
                    if (spriteNum == 1) {image = up1;}
                    if (spriteNum == 2) {image = up2;}
                    if (spriteNum == 3) {image = up1;} // up3 do not exist right now
                    if (spriteNum == 4) {image = up2;}
                }
                if(attacking) {
                    tempScreenY = screenY - gp.tileSize;
                    if (spriteNum == 1) {image = attackUp1;}
                    if (spriteNum == 2) {image = attackUp2;}
                }
            }
            case "down" -> {
                if(!attacking) {
                    if (spriteNum == 1) {image = down1;}
                    if (spriteNum == 2) {image = down2;}
                    if (spriteNum == 3) {image = down3;}
                    if (spriteNum == 4) {image = down2;}
                }
                if(attacking) {
                    if (spriteNum == 1) {image = attackDown1;}
                    if (spriteNum == 2) {image = attackDown2;}
                }
            }
            case "left" -> {
                if(!attacking) {
                    if (spriteNum == 1) {image = left1;}
                    if (spriteNum == 2) {image = left2;}
                    if (spriteNum == 3) {image = left3;}
                    if (spriteNum == 4) {image = left2;}
                }
                if(attacking) {
                    tempScreenX = screenX - gp.tileSize;
                    if (spriteNum == 1) {image = attackLeft1;}
                    if (spriteNum == 2) {image = attackLeft2;}
                }
            }
            case "right" -> {
                if(!attacking) {
                    if (spriteNum == 1) {image = right1;}
                    if (spriteNum == 2) {image = right2;}
                    if (spriteNum == 3) {image = right3;}
                    if (spriteNum == 4) {image = right2;}
                }
                if(attacking) {
                    if (spriteNum == 1) {image = attackRight1;}
                    if (spriteNum == 2) {image = attackRight2;}
                }
            }
        }

        // afk sleep animation
        long endTime = 0;
        if(startTime > 0) {
            endTime = (System.nanoTime() - startTime) / 1000000000;
        }
        //System.out.println(endTime);

        if(endTime == 10) {
            image = straight;
        } else if (endTime == 11 || endTime == 12) {
            image = sleep1;
        } else if (endTime >= 13) {
            if(endTime % 2 == 1) {
                image = sleep2;
            } else {
                image = sleep3;
            }
        }

        // Визуальные эффекты получения урона
        if(invincible) g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2.drawImage(image, tempScreenX, tempScreenY, null);

        // reset alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
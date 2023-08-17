package org.game;

import entity.Entity;
import object.Heart;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Класс, полностью отрисовывающий пользовательский интерфейс
 * В интерфейс входят: худ, различные экраны, некоторые объекты, окна, шрифты и т.д
 */
public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font maruMonica;// Кастомный шрифт
    BufferedImage heart_full, heart_half, heart_blank; // 3 изображения объекта "сердце"(здоровье игрока)
    public boolean messageOn = false;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public int commandNum = 0;
    public int titleScreenState = 0; // 0: первый страница загрузочного экрана 1: вторая страница и т.д.
    public int slotCol = 0;
    public int slotRow = 0;


    public UI(GamePanel gp) {
        this.gp = gp;

        try {
            InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }

        // Создание hud'а
        Entity heart = new Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
    }

    /**
     * Метод, показывающий сообщение на экране
     */
    public void addMessage(String text) {
        message.add(text);
        messageCounter.add(0);
    }

    /**
     * Метод, отрисовывающий интерфейс и экраны
     */
    public void draw(Graphics2D g2) {
        this.g2 = g2;

        g2.setFont(maruMonica);
        g2.setColor(Color.white);

        // Загрузочный экран
        if(gp.gameState == gp.titleState) {
            drawTitleScreen();
        }

        // Состояние игры(идёт игра)
        if(gp.gameState == gp.playState) {
            drawPlayerLife();
            drawMessage();
        }
        // Состояние игры(пауза)
        if(gp.gameState == gp.pauseState) {
            drawPlayerLife();
            drawPauseScreen();
        }
        // Состояние игры(диалог)
        if(gp.gameState == gp.dialogueState) {
            drawPlayerLife();
            drawDialogueScreen();
        }
        // Состояние показа статов гг
        if(gp.gameState == gp.characterState) {
            drawCharacterScreen();
            drawInventory();
        }
    }

    /**
     * Метод, отрисовывающий здоровье игрока
     */
    public void drawPlayerLife() {
        int x = gp.tileSize/2;
        int y = gp.tileSize/2;
        int i = 0;

        // Прорисовка пустых сердец
        while(i < gp.player.maxLife/2) {
            g2.drawImage(heart_blank, x, y, null);
            i++;
            x += gp.tileSize;
        }

        // Возвращаем исходные значения
        x = gp.tileSize/2;
        y = gp.tileSize/2;
        i = 0;

        // Прорисовка текущего количества хп
        while(i < gp.player.life) {
            g2.drawImage(heart_half, x, y, null);
            i++;

            if(i < gp.player.life) {
                g2.drawImage(heart_full, x, y, null);
            }

            i++;
            x += gp.tileSize;
        }
    }

    public void drawMessage() {
        int messageX = gp.tileSize;
        int messageY = gp.tileSize * 4;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32));

        for(int i = 0; i < message.size(); i++) {
            if(message.get(i) != null) {
                g2.setColor(Color.black);
                g2.drawString(message.get(i), messageX + 2, messageY + 2);
                g2.setColor(Color.white);
                g2.drawString(message.get(i), messageX, messageY);

                int counter = messageCounter.get(i) + 1; // messageCounter++
                messageCounter.set(i, counter); // set the counter to the array
                messageY += 50;

                if(messageCounter.get(i) > 180) {
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }
    }

    /**
     * Метод, отрисовавающий начальный экран
     */
    public void drawTitleScreen() {

        if(titleScreenState == 0) {
            // Цвет фона
            g2.setColor(new Color(0,0,0));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            // Название игры на загрузочном экране
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            String text = "Sleepytale";
            int x = getXForCenteredText(text);
            int y = gp.tileSize * 3;

            // Тень текста
            g2.setColor(Color.darkGray);
            g2.drawString(text, x+5, y+5);

            // Основной цвет текста
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            // Картинка персонажа
            x = gp.screenWidth/2 - (gp.tileSize*2)/2;
            y += gp.tileSize*2;
            g2.drawImage(gp.player.straight, x, y, gp.tileSize*2, gp.tileSize*2, null);

            // Меню
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

            // Строка новой игры
            text = "NEW GAME";
            x = getXForCenteredText(text);
            y += gp.tileSize*3;
            g2.drawString(text, x, y);
            if(commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            // Строка загрузки сохранения
            text = "LOAD GAME";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if(commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            // Строка выхода из игры
            text = "QUIT";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if(commandNum == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        }
        else if(titleScreenState == 1) {

            // Второе меню с выбором класса персонажа
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(42F));

            // Строка выбора класса
            String text = "Select your class!";
            int x = getXForCenteredText(text);
            int y = gp.tileSize * 3;
            g2.drawString(text, x, y);

            // Строка класса "Fighter"
            text = "Fighter";
            x = getXForCenteredText(text);
            y += gp.tileSize * 3;
            g2.drawString(text, x, y);
            if(commandNum == 0) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            // Строка класса "Thief"
            text = "Thief";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if(commandNum == 1) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            // Строка класса "Sorcerer"
            text = "Sorcerer";
            x = getXForCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if(commandNum == 2) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            // Строка возврата на первый начальный экран
            text = "Back";
            x = getXForCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if(commandNum == 3) {
                g2.drawString(">", x-gp.tileSize, y);
            }
        }
    }

    /**
     * Метод, отрисовывающий экран паузы
     */
    public void drawPauseScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80));
        String text = "PAUSED";
        int x = getXForCenteredText(text);
        int y = gp.screenHeight/2;

        g2.drawString(text, x, y);
    }

    /**
     * Метод, отрисовывающий диалоги
     */
    public void drawDialogueScreen() {
        // Окно диалога
        int x = gp.tileSize;
        int y = gp.screenHeight - gp.tileSize * 5;
        int width = gp.screenWidth - (gp.tileSize * 2);
        int height = gp.tileSize * 4;

        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 38F));
        x += gp.tileSize;
        y += gp.tileSize;

        for(String line : currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawCharacterScreen() {
        // Создаём рамку
        final int frameX = gp.tileSize;
        final int frameY = gp.tileSize;
        final int frameWidth = gp.tileSize * 5;
        final int frameHeight = gp.tileSize * 10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // Текст
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(48F));

        int textX = frameX + 10;
        int textY = frameY + gp.tileSize;
        final int lineHeight = 48;

        // Названия статов
        g2.drawString("Level", textX, textY);
        textY += lineHeight;
        g2.drawString("Life", textX, textY);
        textY += lineHeight;
        g2.drawString("Strength", textX, textY);
        textY += lineHeight;
        g2.drawString("Attack", textX, textY);
        textY += lineHeight;
        g2.drawString("Defence", textX, textY);
        textY += lineHeight;
        g2.drawString("Exp", textX, textY);
        textY += lineHeight;
        g2.drawString("Next lvl exp", textX, textY);
        textY += lineHeight;
        g2.drawString("Gold", textX, textY);
        textY += lineHeight + 10;
        g2.drawString("Weapon", textX, textY);
        textY += lineHeight + 15;
        g2.drawString("Shield", textX, textY);

        // Значения статов
        int tailX = (frameX + frameWidth) - 30;
        textY = frameY + gp.tileSize; // Возвращаем изначальное значение переменной textY
        String value;

        value = String.valueOf(gp.player.level);
        textX = getXForAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.life + "/" + gp.player.maxLife);
        textX = getXForAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.strength);
        textX = getXForAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.attack);
        textX = getXForAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.defence);
        textX = getXForAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.exp);
        textX = getXForAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.nextLevelExp);
        textX = getXForAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.coin);
        textX = getXForAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

       g2.drawImage(gp.player.currentWeapon.down1, tailX - gp.tileSize, textY - 40, null);
       textY += gp.tileSize;
       g2.drawImage(gp.player.currentShield.down1, tailX - gp.tileSize, textY - 40, null);
    }

    public void drawInventory() {

        // отрисовка окна инвентаря
        int frameX = gp.tileSize * 9;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 6;
        int frameHeight = gp.tileSize * 5;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // отрисовка слота
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize+5;

        // отрисовка предметов игрока
        for(int i = 0; i < gp.player.inventory.size(); i++) {
            g2.drawImage(gp.player.inventory.get(i).down1, slotX, slotY, null);
            slotX += slotSize;

            if(i == 4 || i == 9 || i == 14) {
                slotX = slotXstart;
                slotY += slotSize;
            }
        }

        // курсор
        int cursorX = slotXstart + (slotSize * slotCol);
        int cursorY = slotYstart + (slotSize * slotRow);
        int cursorWidth = gp.tileSize;
        int cursorHeight = gp.tileSize;

        // отрисовка курсора
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

        // отрисовка окна описания предмета
        int descFrameX = frameX;
        int descFrameY = frameY + frameHeight;
        int descFrameWidth = frameWidth;
        int descFrameHeight = gp.tileSize * 3;
        drawSubWindow(descFrameX, descFrameY, descFrameWidth, descFrameHeight);

        // отрисовка текста описания предмета
        int textX = descFrameX + 20;
        int textY = descFrameY + gp.tileSize/2 + 10;
        g2.setFont(g2.getFont().deriveFont(28F));

        int itemIndex = getItemIndexOnSlot();

        if(itemIndex < gp.player.inventory.size()) {
            for(String line: gp.player.inventory.get(itemIndex).description.split("\n")) {
                g2.drawString(line, textX, textY);
                textY += 32;
            }
        }
    }

    public int getItemIndexOnSlot() {
        int itemIndex = slotCol + (slotRow*5);
        return itemIndex;
    }

    /**
     * Метод, отрисовывающий "подокна"(нужны для окон диалогов)
     */
    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0,0,0, 200);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }

    /**
     * Метод, позволяющий вычислить координату х,
     * чтобы правильно выровнять текст на экране посередине(в зависимости от длинны строки)
     */
    public int getXForCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth/2 - length/2;
    }

    public int getXForAlignToRightText(String text, int tailX) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = tailX - length;
        return x;
    }
}

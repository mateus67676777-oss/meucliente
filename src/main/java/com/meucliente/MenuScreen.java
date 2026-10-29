package com.meucliente.gui;

import com.meucliente.module.Module;
import com.meucliente.module.ModuleManager;
import net.minecraft.client.gui.Screen;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Menu compacto em tres colunas. F8 ou ESC fecha.
 * Clique na linha: liga/desliga. Clique na seta verde: configuracoes (bloco 2).
 *
 * ATENCAO: nomes de metodos/campos abaixo (font, fill, openScreen, LiteralText,
 * keyPressed, charTyped, mouseScrolled) sao os da yarn 1.14.x e podem variar
 * um pouco entre builds. Se o compilador reclamar, ajuste so o nome apontado.
 */
public class MenuScreen extends Screen {

    private static final int COLS = 3;
    private static final int ROW_H = 14;
    private static final int GAP = 4;
    private static final int SB_W = 6;
    private static final int SEARCH_H = 14;

    private String search = "";
    private int scroll = 0;

    // layout calculado a cada frame (adapta a resolucao)
    private int colW, panelX, panelY, panelW, panelH, listTop, visibleRows, rowsPerCol;
    private List<Module> shown;

    public MenuScreen() {
        super(new LiteralText("MeuCliente"));
    }

    @Override
    public boolean isPauseScreen() {
        return false; // mantem o mundo visivel e rodando atras do menu
    }

    private void layout() {
        shown = ModuleManager.search(search);
        colW = Math.max(80, Math.min(120, (width - 40 - SB_W - (COLS + 1) * GAP) / COLS));
        panelW = COLS * colW + (COLS + 1) * GAP + SB_W;
        panelH = Math.min(height - 20, 260);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        listTop = panelY + GAP + SEARCH_H + GAP;
        visibleRows = Math.max(1, (panelY + panelH - GAP - listTop) / ROW_H);
        rowsPerCol = Math.max(1, (shown.size() + COLS - 1) / COLS);
        int maxScroll = Math.max(0, rowsPerCol - visibleRows);
        if (scroll > maxScroll) scroll = maxScroll;
        if (scroll < 0) scroll = 0;
    }

    private int rowX(int col) {
        return panelX + GAP + col * (colW + GAP);
    }

    /** Indice do modulo sob o mouse na lista filtrada, ou -1. */
    private int indexAt(double mx, double my) {
        if (my < listTop || my >= listTop + visibleRows * ROW_H) return -1;
        int row = (int) ((my - listTop) / ROW_H) + scroll;
        for (int col = 0; col < COLS; col++) {
            int x = rowX(col);
            if (mx >= x && mx < x + colW) {
                int idx = col * rowsPerCol + row;
                if (row < rowsPerCol && idx < shown.size()) return idx;
                return -1;
            }
        }
        return -1;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        layout();

        // painel cinza-escuro semitransparente (sem renderBackground: mundo visivel)
        fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xA0181818);

        // barra de pesquisa
        int sx = panelX + GAP, sy = panelY + GAP;
        fill(sx, sy, panelX + panelW - GAP, sy + SEARCH_H, 0xC0000000);
        if (search.isEmpty()) {
            font.draw("Pesquisar...", sx + 4, sy + 3, 0xFF808080);
        } else {
            font.draw(search + "_", sx + 4, sy + 3, 0xFFFFFFFF);
        }

        int hover = indexAt(mouseX, mouseY);

        for (int i = 0; i < shown.size(); i++) {
            int col = i / rowsPerCol;
            int row = i % rowsPerCol;
            if (row < scroll || row >= scroll + visibleRows) continue;

            Module m = shown.get(i);
            int x = rowX(col);
            int y = listTop + (row - scroll) * ROW_H;

            int bg = m.isEnabled() ? 0xB02E6B2E : 0x80383838;
            if (i == hover) bg = m.isEnabled() ? 0xC03C8A3C : 0xA0505050;
            fill(x, y, x + colW, y + ROW_H - 1, bg);

            font.draw(m.getName(), x + 4, y + 3, m.isEnabled() ? 0xFFFFFFFF : 0xFFC8C8C8);
            if (m.hasSettings()) {
                font.draw(">", x + colW - 9, y + 3, 0xFF55FF55);
            }
        }

        // barra de rolagem
        int trackX = panelX + panelW - SB_W - 1;
        int trackTop = listTop;
        int trackH = visibleRows * ROW_H;
        fill(trackX, trackTop, trackX + SB_W, trackTop + trackH, 0x80000000);
        int maxScroll = rowsPerCol - visibleRows;
        if (maxScroll > 0) {
            int thumbH = Math.max(10, trackH * visibleRows / rowsPerCol);
            int thumbY = trackTop + (trackH - thumbH) * scroll / maxScroll;
            fill(trackX, thumbY, trackX + SB_W, thumbY + thumbH, 0xFF8A8A8A);
        }

        super.render(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        layout();
        int idx = indexAt(mx, my);
        if (idx >= 0 && button == 0) {
            Module m = shown.get(idx);
            int col = idx / rowsPerCol;
            boolean onArrow = m.hasSettings() && mx >= rowX(col) + colW - 14;
            if (onArrow) {
                // bloco 2: abrir painel de configuracoes do modulo
            } else {
                m.toggle();
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        scroll -= (int) Math.signum(amount);
        layout();
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_F8) {
            onClose();
            return true;
        }
        if (key == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
            search = search.substring(0, search.length() - 1);
            scroll = 0;
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers); // ESC fecha
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (c >= 32 && c != 127 && search.length() < 32) {
            search += c;
            scroll = 0;
            return true;
        }
        return super.charTyped(c, modifiers);
    }
}

package view;

import java.util.ArrayList;
import java.util.List;

import com.codingame.game.Action;
import com.codingame.game.Player;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Polygon;
import com.codingame.gameengine.module.entities.Text;
import com.codingame.gameengine.module.tooltip.TooltipModule;

import view.toggle.ToggleModule;

public class View {
    // Constants
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    private List<List<Polygon>> hexagonsList = new ArrayList<>();
    private static Text[] moves = new Text[2];

    private Polygon createHex(GraphicEntityModule graphics, double radius, int centerX, int centerY, int row) {
        Polygon hex = graphics.createPolygon();
        // Creates HexGrid
        for (int i = 0; i < 6; i++) {
            int x = (int)(radius * 1.75 * row + centerX + radius * Math.cos((1 + i * 2) * Math.PI / 6D));
            int y = (int)(centerY + radius * Math.sin((1 + i * 2) * Math.PI / 6D));
            hex.addPoint(x, y);
        }
        hex.setFillColor(0xE7BA7B).setLineWidth(1).setLineColor(0x000000).setZIndex(0);

        return hex;
    };

    public View(GraphicEntityModule graphics, TooltipModule tooltips, ToggleModule toggle, List<Player> players) {
        graphics.createRectangle().setWidth(WIDTH).setHeight(HEIGHT).setFillColor(0xababab);

        Group texts = graphics.createGroup();
        texts.setZIndex(2);

        double radius = 65;
        int centerX = 735;
        int centerY = 150;
        int counter = 5;

        // Generates hex grid
        for(int h = 0; h < 9; ++h) {
            hexagonsList.add(h, new ArrayList<>());
            for(int c = 0; c < counter; ++c) {
                hexagonsList.get(h).add(c, createHex(graphics, radius, centerX, centerY, c));
                tooltips.setTooltipText(hexagonsList.get(h).get(c), c + " " + h);
                texts.add(graphics.createText(c + "  " + h).setX(centerX + (int)(radius * 1.75 * c) - 20).setY(centerY - 12));
            }
            if(h < 4) {
                centerX -= radius * 0.875;
                centerY += radius * 1.5;
                counter++;
            } else {
                centerX += radius * 0.875;
                centerY += radius * 1.5;
                counter--;
            }
        }
        toggle.displayOnToggleState(texts, "debugToggle", true);

        DrawPlayers(graphics, players);
    }

    private void DrawPlayer(GraphicEntityModule graphics, Player player) {
        int centerX = 172;
        if(player.getIndex() == 1) {
            centerX = 1542;
        }
        graphics.createText(player.getNicknameToken())
                .setX(centerX)
                .setY(352)
                .setFontSize(60).setFontFamily("Roboto").setFontWeight(Text.FontWeight.BOLD);

        graphics.createSprite()
                .setImage(player.getAvatarToken())
                .setX(centerX)
                .setY(130)
                .setScale(1.75);

        Polygon poly = createHex(graphics, 200, 103 + centerX, 800, 0);
        poly.setFillColor(player.getColorToken()).setZIndex(1);

        moves[player.getIndex()] = graphics.createText("").setY(725).setX(centerX-12).setZIndex(2).setFontSize(150).setFontWeight(Text.FontWeight.BOLD);
    }

    private void DrawPlayers(GraphicEntityModule graphics, List<Player> players) {
        DrawPlayer(graphics, players.get(0));
        DrawPlayer(graphics, players.get(1));
    }

    public void PaintSquare(GraphicEntityModule graphics, Action action) {
        moves[action.player.getIndex()].setText(action.row+ "  " + action.col);
        hexagonsList.get(action.col).get(action.row).setFillColor(action.player.getColorToken());
    }

    public void EndGameView(GraphicEntityModule graphics, Action action, GameManager gameManager, Player winner, List<Action> neighbours) {
        if(action == null)
            return;
        PaintSquare(graphics ,action);
        gameManager.setFrameDuration(2000);
        if(winner == null) { // Its a tie
            return;
        }

        // If lastPlayed is the winner
        if(action.player.getIndex() == winner.getIndex()) {
            for(int h = 0; h < 9; ++h) {
                for(int i = 0; i < hexagonsList.get(h).size(); ++i) {
                    boolean found = false;
                    for(Action a : neighbours) {
                        if(h == a.col && i == a.row) {
                            hexagonsList.get(h).get(i).setZIndex(1);
                            found = true;
                        }
                    }
                    if(!found) {
                        hexagonsList.get(h).get(i).setAlpha(0.4).setLineAlpha(0.4);
                    }
                }
            }
        } else {
            for(Action a : neighbours) {
                hexagonsList.get(a.col).get(a.row).setAlpha(0.2);
            }
        }
    }
}

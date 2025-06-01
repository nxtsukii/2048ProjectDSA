package test01;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Tile {
    private int value;
    private final Rectangle rect;
    private final Text text;
    private final StackPane stack;
    private static final int TILE_RECT_SIZE = 120; 

    public Tile(int value) {
        this.value = value;
        this.rect = new Rectangle(TILE_RECT_SIZE, TILE_RECT_SIZE); 
        this.rect.setArcWidth(15);
        this.rect.setArcHeight(15);
        this.text = new Text();
        this.text.setFont(Font.font("Arial Bold", 36)); 
        this.stack = new StackPane();
        
        stack.getChildren().addAll(rect, text);
        updateUI(); 
    }

    public void setValue(int val) {
        this.value = val;
        updateUI();
    }

    public int getValue() {
        return value;
    }

    public StackPane getStackPane() {
        return stack;
    }

    public void updateUI() {
        if (value == 0) {
            text.setText(""); 
            //transparent if value = 0
            rect.setFill(Color.TRANSPARENT); 

            stack.setStyle("-fx-background-color: rgba(238, 228, 218, 0.35); -fx-border-radius: 5px; -fx-border-color: #bbada0; -fx-border-width: 1px;");

        } else {
            text.setText(String.valueOf(value));
            rect.setFill(getColor(value));
            
            stack.setStyle("-fx-background-color: transparent; -fx-border-radius: 5px;");

            if (value <= 4) {
                text.setFill(Color.rgb(119, 110, 101)); 
            } else {
                text.setFill(Color.WHITE); 
            }
            
            if (value >= 1000) {
                text.setFont(Font.font("Arial Bold", 28));
            } else if (value >= 100) {
                text.setFont(Font.font("Arial Bold", 32));
            } else {
                text.setFont(Font.font("Arial Bold", 36));
            }
        }
    }

    private Color getColor(int value) {
        switch (value) {
            case 2:    return Color.rgb(238, 228, 218);
            case 4:    return Color.rgb(237, 224, 200);
            case 8:    return Color.rgb(242, 177, 121);
            case 16:   return Color.rgb(245, 149, 99);
            case 32:   return Color.rgb(246, 124, 95);
            case 64:   return Color.rgb(246, 94, 59);
            case 128:  return Color.rgb(237, 207, 114);
            case 256:  return Color.rgb(237, 204, 97);
            case 512:  return Color.rgb(237, 200, 80);
            case 1024: return Color.rgb(237, 197, 63);
            case 2048: return Color.rgb(237, 194, 46);
            default:   return Color.rgb(205, 193, 180);
        }
    }
}
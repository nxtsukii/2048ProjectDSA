package test01;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Board extends GridPane {
    private final int size = 4;
    private final Tile[][] tiles = new Tile[size][size];
    private static final int TILE_SIZE = 120;
    private static final int GAP_SIZE = 10;
    private static final int CELL_TOTAL_SIZE = TILE_SIZE + GAP_SIZE;

    private Runnable onMoveSequenceCompleteCallback; 
    private Consumer<Integer> onScoreUpdateCallback;
    private int score = 0;

    public Board() {
        setHgap(GAP_SIZE);
        setVgap(GAP_SIZE);
        setStyle("-fx-background-color: #bbada0; -fx-padding: 10px; -fx-border-color: #bbada0; -fx-border-width: 5px;");
        initializeTiles();
        spawn();
        spawn();
    }
    
    public int getScore() {
    	return score;
    }

   
    public void setOnScoreUpdate(Consumer<Integer> callback) {
    	this.onScoreUpdateCallback = callback;
    }
    
    private void triggerScoreUpdate() {
    	if (onScoreUpdateCallback != null) {
    		javafx.application.Platform.runLater(() -> 
    		onScoreUpdateCallback.accept(this.score));
    	}
    }
    
    public void setOnMoveSequenceComplete(Runnable callback) {
        this.onMoveSequenceCompleteCallback = callback;
    }

    private void initializeTiles() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile tile = new Tile(0);
                tiles[row][col] = tile;
                tile.getStackPane().setStyle("-fx-background-color: rgba(238, 228, 218, 0.35); -fx-border-radius: 5px; -fx-border-color: #bbada0; -fx-border-width: 1px;");
                add(tile.getStackPane(), col, row);
            }
        }
    }

    public void spawn() {
        List<int[]> emptyTiles = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (tiles[row][col].getValue() == 0) {
                    emptyTiles.add(new int[]{row, col});
                }
            }
        }
        if (!emptyTiles.isEmpty()) {
            int[] pos = emptyTiles.get((int)(Math.random() * emptyTiles.size()));
            tiles[pos[0]][pos[1]].setValue(Math.random() < 0.9 ? 2 : 4);

            ScaleTransition spawnAnim = new ScaleTransition(Duration.millis(150), tiles[pos[0]][pos[1]].getStackPane());
            spawnAnim.setFromX(0);
            spawnAnim.setFromY(0);
            spawnAnim.setToX(1);
            spawnAnim.setToY(1);
            spawnAnim.play();
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void refreshUI() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                tiles[row][col].updateUI();
            }
        }
    }

    
    //Movement functions
    public boolean moveLeft() {
        boolean moved = false;
        List<TileMove> movesThisTurn = new ArrayList<>();
        boolean[][] merged = new boolean[size][size];
        int scoreThisMove = 0;

        for (int row = 0; row < size; row++) {
            for (int col = 1; col < size; col++) {
                if (tiles[row][col].getValue() == 0) continue;
                Tile currentTileData = tiles[row][col];
                int currentValue = currentTileData.getValue();
                StackPane currentPane = currentTileData.getStackPane();
                int furthestLeftPossible = col;
                for (int k = col - 1; k >= 0; k--) {
                    Tile targetCell = tiles[row][k];
                    if (targetCell.getValue() == 0) {
                        furthestLeftPossible = k;
                    } else if (targetCell.getValue() == currentValue && !merged[row][k]) {
                        furthestLeftPossible = k;
                        break;
                    } else {
                        break;
                    }
                }
                if (furthestLeftPossible != col) {
                    moved = true;
                    Tile targetTileDataAtDestination = tiles[row][furthestLeftPossible];
                    movesThisTurn.add(new TileMove(currentPane, row, col, row, furthestLeftPossible, targetTileDataAtDestination.getValue() != 0));
                    if (targetTileDataAtDestination.getValue() == 0) {
                        targetTileDataAtDestination.setValue(currentValue);
                    } else {
                    	int newValue = targetTileDataAtDestination.getValue() * 2;
                        targetTileDataAtDestination.setValue(newValue);
                        scoreThisMove += newValue;
                        
                        merged[row][furthestLeftPossible] = true;
                    }
                    currentTileData.setValue(0);
                }
            }
        }

        if (scoreThisMove > 0) {
        	this.score += scoreThisMove;
        	triggerScoreUpdate();
        }
        
        if (moved) {
            animateMovement(movesThisTurn);
        } else {
            triggerMoveSequenceCompleteCallback();
        }
        return moved;
    }

    public boolean moveRight() {
        boolean moved = false;
        List<TileMove> movesThisTurn = new ArrayList<>();
        boolean[][] merged = new boolean[size][size];
        int scoreThisMove = 0;

        for (int row = 0; row < size; row++) {
            for (int col = size - 2; col >= 0; col--) {
                if (tiles[row][col].getValue() == 0) continue;
                Tile currentTileData = tiles[row][col];
                int currentValue = currentTileData.getValue();
                StackPane currentPane = currentTileData.getStackPane();
                int furthestRightPossible = col;
                for (int k = col + 1; k < size; k++) {
                    Tile targetCell = tiles[row][k];
                    if (targetCell.getValue() == 0) {
                        furthestRightPossible = k;
                    } else if (targetCell.getValue() == currentValue && !merged[row][k]) {
                        furthestRightPossible = k;
                        break;
                    } else {
                        break;
                    }
                }
                if (furthestRightPossible != col) {
                    moved = true;
                    Tile targetTileDataAtDestination = tiles[row][furthestRightPossible];
                    movesThisTurn.add(new TileMove(currentPane, row, col, row, furthestRightPossible, targetTileDataAtDestination.getValue() != 0));
                    if (targetTileDataAtDestination.getValue() == 0) {
                        targetTileDataAtDestination.setValue(currentValue);
                    } else {
                        int newValue = targetTileDataAtDestination.getValue() * 2;
                        targetTileDataAtDestination.setValue(newValue);
                        scoreThisMove += newValue; // 
                        merged[row][furthestRightPossible] = true;
                    }
                    currentTileData.setValue(0);
                }
            }
        }
        if (scoreThisMove > 0) {
            this.score += scoreThisMove;
            triggerScoreUpdate();
        }
        if (moved) {
            animateMovement(movesThisTurn);
        } else {
            triggerMoveSequenceCompleteCallback();
        }
        return moved;
    }

    public boolean moveUp() {
        boolean moved = false;
        List<TileMove> movesThisTurn = new ArrayList<>();
        boolean[][] merged = new boolean[size][size];
        int scoreThisMove = 0;

        for (int col = 0; col < size; col++) {
            for (int row = 1; row < size; row++) {
                if (tiles[row][col].getValue() == 0) continue;
                Tile currentTileData = tiles[row][col];
                int currentValue = currentTileData.getValue();
                StackPane currentPane = currentTileData.getStackPane();
                int furthestUpPossible = row;
                for (int k = row - 1; k >= 0; k--) {
                    Tile targetCell = tiles[k][col];
                    if (targetCell.getValue() == 0) {
                        furthestUpPossible = k;
                    } else if (targetCell.getValue() == currentValue && !merged[k][col]) {
                        furthestUpPossible = k;
                        break;
                    } else {
                        break;
                    }
                }
                if (furthestUpPossible != row) {
                    moved = true;
                    Tile targetTileDataAtDestination = tiles[furthestUpPossible][col];
                    movesThisTurn.add(new TileMove(currentPane, row, col, furthestUpPossible, col, targetTileDataAtDestination.getValue() != 0));
                    if (targetTileDataAtDestination.getValue() == 0) {
                        targetTileDataAtDestination.setValue(currentValue);
                    } else {
                        int newValue = targetTileDataAtDestination.getValue() * 2;
                        targetTileDataAtDestination.setValue(newValue);
                        scoreThisMove += newValue; 
                        merged[furthestUpPossible][col] = true;
                    }
                    currentTileData.setValue(0);
                }
            }
        }
        if (scoreThisMove > 0) {
            this.score += scoreThisMove;
            triggerScoreUpdate();
        }
        if (moved) {
            animateMovement(movesThisTurn);
        } else {
            triggerMoveSequenceCompleteCallback();
        }
        return moved;
    }

    public boolean moveDown() {
        boolean moved = false;
        List<TileMove> movesThisTurn = new ArrayList<>();
        boolean[][] merged = new boolean[size][size];
        int scoreThisMove = 0;

        for (int col = 0; col < size; col++) {
            for (int row = size - 2; row >= 0; row--) {
                if (tiles[row][col].getValue() == 0) continue;
                Tile currentTileData = tiles[row][col];
                int currentValue = currentTileData.getValue();
                StackPane currentPane = currentTileData.getStackPane();
                int furthestDownPossible = row;
                for (int k = row + 1; k < size; k++) {
                    Tile targetCell = tiles[k][col];
                    if (targetCell.getValue() == 0) {
                        furthestDownPossible = k;
                    } else if (targetCell.getValue() == currentValue && !merged[k][col]) {
                        furthestDownPossible = k;
                        break;
                    } else {
                        break;
                    }
                }
                if (furthestDownPossible != row) {
                    moved = true;
                    Tile targetTileDataAtDestination = tiles[furthestDownPossible][col];
                    movesThisTurn.add(new TileMove(currentPane, row, col, furthestDownPossible, col, targetTileDataAtDestination.getValue() != 0));
                    if (targetTileDataAtDestination.getValue() == 0) {
                        targetTileDataAtDestination.setValue(currentValue);
                    } else {
                        int newValue = targetTileDataAtDestination.getValue() * 2;
                        targetTileDataAtDestination.setValue(newValue);
                        scoreThisMove += newValue; 
                        merged[furthestDownPossible][col] = true;
                    }
                    currentTileData.setValue(0);
                }
            }
        }
        if (scoreThisMove > 0) {
            this.score += scoreThisMove;
            triggerScoreUpdate();
        }
        if (moved) {
            animateMovement(movesThisTurn);
        } else {
            triggerMoveSequenceCompleteCallback();
        }
        return moved;
    }

    public boolean isGameOver() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (tiles[row][col].getValue() == 0) {
                    return false;
                }
            }
        }
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int currentValue = tiles[row][col].getValue();
                if (col < size - 1) {
                    if (currentValue == tiles[row][col + 1].getValue()) {
                        return false;
                    }
                }
                if (row < size - 1) {
                    if (currentValue == tiles[row + 1][col].getValue()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isGameWon() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (tiles[row][col].getValue() == 2048) {
                    return true;
                }
            }
        }
        return false;
    }

   //Animation
    private void animateMovement(List<TileMove> moves) {
        ParallelTransition allAnimations = new ParallelTransition();
        for (TileMove move : moves) {
            StackPane paneToAnimate = move.movingPaneVisual;
            TranslateTransition slide = new TranslateTransition(Duration.millis(100), paneToAnimate);
            int dx = (move.toCol - move.fromCol) * CELL_TOTAL_SIZE;
            int dy = (move.toRow - move.fromRow) * CELL_TOTAL_SIZE;
            slide.setByX(dx);
            slide.setByY(dy);
            slide.setOnFinished(event -> {
                paneToAnimate.setVisible(false);
            });
            allAnimations.getChildren().add(slide);

            if (move.merged) {
                StackPane targetCellPane = tiles[move.toRow][move.toCol].getStackPane();
                ScaleTransition pulse = new ScaleTransition(Duration.millis(100), targetCellPane);
                pulse.setDelay(Duration.millis(50));
                pulse.setFromX(1.0);
                pulse.setFromY(1.0);
                pulse.setToX(1.2);
                pulse.setToY(1.2);
                pulse.setAutoReverse(true);
                pulse.setCycleCount(2);
                allAnimations.getChildren().add(pulse);
            }
        }

        allAnimations.setOnFinished(e -> {
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    tiles[r][c].getStackPane().setTranslateX(0);
                    tiles[r][c].getStackPane().setTranslateY(0);
                    tiles[r][c].getStackPane().setVisible(true);
                }
            }
            refreshUI();
            spawn(); 
            triggerMoveSequenceCompleteCallback(); 
        });
        allAnimations.play();
    }

    // <-- ADDED: Helper to trigger callback safely -->
    private void triggerMoveSequenceCompleteCallback() {
        if (onMoveSequenceCompleteCallback != null) {
            // Ensure it runs on the JavaFX Application Thread
            // (though it's likely already on it, this is safer for generic callbacks)
            javafx.application.Platform.runLater(onMoveSequenceCompleteCallback);
        }
    }

    private static class TileMove {
        StackPane movingPaneVisual;
        int fromRow, fromCol;
        int toRow, toCol;
        boolean merged;

        public TileMove(StackPane movingPaneVisual, int fromRow, int fromCol, int toRow, int toCol, boolean merged) {
            this.movingPaneVisual = movingPaneVisual;
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.merged = merged;
        }
    }
}
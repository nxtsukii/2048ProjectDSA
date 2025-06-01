package test01;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Game extends Application {
    private Board board;
    private boolean gameInteractionAllowed = true; 
    private void showMessage(String title, String content) {
        gameInteractionAllowed = false; 
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();     
    }

    private void checkGameConditions() {
        if (!gameInteractionAllowed) return; 

        if (board.isGameWon()) {
            showMessage("Victory!", "You reached 2048! You Won!");
            
        } else if (board.isGameOver()) { 
            showMessage("Game Over", "No more valid moves");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Label scoreLabel = new Label("Score: 0");
        Button restartBtn = new Button("Restart");
        restartBtn.setFocusTraversable(false);

        board = new Board();
        board.setOnMoveSequenceComplete(this::checkGameConditions); 
        board.setOnScoreUpdate(newScore -> scoreLabel.setText("Score: "+ newScore));
        scoreLabel.setText("Score: "+ board.getScore());

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(scoreLabel, restartBtn, board); 

        Scene scene = new Scene(root);

        restartBtn.setOnAction(e -> {
            root.getChildren().remove(board);
            board = new Board();
            board.setOnMoveSequenceComplete(this::checkGameConditions);
            board.setOnScoreUpdate(newScore -> scoreLabel.setText("Score: "+ newScore));
            scoreLabel.setText("Score: "+ board.getScore());
            
            gameInteractionAllowed = true; 
            root.getChildren().add(board);
            board.requestFocus();
        });

        scene.setOnKeyPressed(event -> {
            if (!gameInteractionAllowed) { 
                return;
            }
            
            switch (event.getCode()) {
                case UP:    board.moveUp(); break;
                case DOWN:  board.moveDown(); break;
                case LEFT:  board.moveLeft(); break;
                case RIGHT: board.moveRight(); break;
                default:
                    return;
            }
           
        });

        primaryStage.setTitle("2048");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        board.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package BouncingBall;

/**
 * @author Leong Kar Man
 * @studentID CST2104044
 * @date 2 Dec 2022 12:12:09 am
 */
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BouncingBallGame extends Application{
	
	//create an interaction with an end-user interface
	private enum UserAction{
		NONE, LEFT, RIGHT
	}
	private UserAction action = UserAction.NONE;
	
	private static final int paneWidth = 500;
	private static final int paneHeight = 600;
	
	private static final int radius = 44;//create ball's radius with student id=44
	private static final int paddle_W = 100, paddle_H = 20;//create paddle's width and height
	
	private int x = radius, y = radius;//x and y is the current position of center of the ball
	private int dx = 2, dy = 2;//ball's velocity in x-direction and y-direction
	private Circle ball = new Circle(x, y, radius, Color.ORANGE);//create a ball with center(x,y), radius and color
	private Rectangle paddle = new Rectangle(paddle_W, paddle_H);//create a rectangle paddle with the width, height and color
		
	private Timeline timeline = new Timeline();
	private boolean running = true;
	
	private int playCount = 0;//to count the amount of stages of the game
	private static int score = 0;//to count score for 5 stages of the game
	int timeSeconds = 30;//the time for one stage of the game
	int frameCount =0;//for counting the amount of frame in duration of 0.01  
	
	private Parent Content() {
		Pane pane = new Pane();//create a pane
		pane.setPrefSize(paneWidth, paneHeight);//set width and height of the pane
		pane.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, null, null)));
		paddle.setX(paneWidth / 2);//set the position of most left corner of paddle in x-direction
		paddle.setY(paneHeight - paddle_H);//set the position of most left corner of paddle in y-direction
		paddle.setFill(Color.BLACK);//set the color of the pane
		
		//create 3 texts with setting the position of the texts
		Text text1 = new Text(10, 25, null);
		Text text2 = new Text(10, 55, null);
		Text text3 = new Text(10, 85, null);
		
		//run this event with 10 frames per seconds
		KeyFrame frame = new KeyFrame(Duration.seconds(0.01), event ->{
			if(!running)
				return;
				//User input handling, for moving the paddle
				switch(action) {
				case LEFT:
					if(paddle.getX() - 5 >= 0)
						paddle.setX(paddle.getX() - 5);
					break;
				case RIGHT:
					if(paddle.getX() + paddle_W + 5 <= paneWidth)
						paddle.setX(paddle.getX() + 5);
					break;
				case NONE:
					break;
			}
			
			//The situation when time stops
			if(timeSeconds == 0) {
				//when time's up, ball didn't drop
				if (ball.getCenterY() < paneHeight- radius) {
					score++;
					x = radius;
					y = radius;
					//playCount++;
					restartGame();
				}//when time's up, ball drops
				else {
					score--;
					restartGame();
					//playCount++;
				}
			}
			//the situation when time is running
			if(timeSeconds != 0) {
				if (x < radius) {//if the ball hits the left wall of pane, 	
					ball.setFill(Color.DARKORCHID);
					dx *= -1;//Change ball move direction
				}
				if(x > paneWidth - radius) {//if the ball hits the right wall of the pane
					ball.setFill(Color.INDIANRED);
					dx *= -1;//change ball move direction
				}
				//if the ball hits the paddle
				if(ball.intersects(paddle.getX(), paneHeight-paddle_H, paddle_W, paddle_H)){
					ball.setFill(Color.DARKBLUE);
					dy *= -1;//change ball's move in y-direction
				}				
				if (y < radius ) {//if the ball hits the top of pane
					ball.setFill(Color.OLIVEDRAB);
					dy *= -1;//
				}
				//Game Over, if the ball drops
				if(y > paneHeight) {
					score--;//score -1
					x = radius;//set the center position of the ball
					y = radius;
					restartGame();
			  	}
				//continuously change the center position of the ball, to let ball moves
			  	x += dx; 
				y += dy;
				ball.setCenterX(x);
				ball.setCenterY(y);
			}
			
			//To count how many frames run in 1 second
			frameCount++;
			if(frameCount % 100 == 0)//one frame in 0.01 seconds, therefore it has 10 frames in 1 second
				timeSeconds--;//Initially timeSeconds is 30
			
			
			text1.setFont(Font.font("Serif",FontPosture.REGULAR, 20));
			text1.setText("Playcounts: " + playCount);
			
			text2.setFont(Font.font("Serif",FontPosture.REGULAR, 20));
			text2.setText("LeftTime: " + timeSeconds);
			
			text3.setFont(Font.font("Serif",FontPosture.REGULAR, 20));
			text3.setText("Score: " + score);
		});
		
		timeline.getKeyFrames().add(frame);
		timeline.setCycleCount(Timeline.INDEFINITE);
		 
		pane.getChildren().addAll(ball, paddle,text1, text2, text3);
		return pane;//always refresh the pane
	}
	
	//Method to restart the game
	private void restartGame() {
		stopGame();
		if(playCount < 6) {
			startGame();
		}
	}
	
	//Method to stop the game
	private void stopGame() {
		running = false;
		timeline.stop();
		if((playCount == 5  && timeSeconds == 0) || (playCount == 5 && ball.getCenterY() > radius)) {
			popUpWindow();//After 5 stages of the game, pop up a window to show the score
		}
	}
	
	//Method to start the game
	private void startGame() {
		playCount++;
		if (playCount < 6) {
			timeline.play();
			running = true;
			timeSeconds = 30;//reset the timeSeconds after one stage
		}
	}
	
	@Override
	public void start(Stage primaryStage) {
		//create a scene for pane 
		Scene scene = new Scene(Content());
		
		//scene register the handler for the key-pressed event
		scene.setOnKeyPressed(event ->{
			switch(event.getCode()) {
			case LEFT:
				action = UserAction.LEFT;
				break;
			case RIGHT:
				action = UserAction.RIGHT;
				break;
			default:
				break;
			}
		});
		//if the user released the key, it will be none action
		scene.setOnKeyReleased(event -> {
			switch (event.getCode()) {
			case LEFT:
				action = UserAction.NONE;
				break;
			case RIGHT:
				action = UserAction.NONE;
				break;
			default:
				break;
			}
		});
		
		primaryStage.setTitle("Bouncing Ball");
		primaryStage.setScene(scene);
		primaryStage.show();//display stage
		startGame();
	}
	
	//Pop up window to show the final score 
	static void popUpWindow(){
		Stage popUpStage = new Stage();
		Pane root= new Pane();
		
		Text text4 = new Text(20, 150, null);
		text4.setText("Thank You for Playing the Game!\nYour final score is: " + score);
		text4.setFont(Font.font("Srif",FontWeight.BOLD, FontPosture.REGULAR, 20 ));
		text4.setFill(Color.CORNFLOWERBLUE);
		text4.setTextAlignment(TextAlignment.CENTER);
		
		root.getChildren().addAll(text4);
		
		Scene thankScene = new Scene(root, 350,350, Color.LIGHTPINK);
		popUpStage.setScene(thankScene);
		popUpStage.setTitle("Thank You and Show Score Scene");
		popUpStage.show();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
}

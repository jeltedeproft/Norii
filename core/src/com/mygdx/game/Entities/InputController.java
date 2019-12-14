package com.mygdx.game.Entities;

import java.util.EnumMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Entities.EntityAnimation.Direction;
import com.mygdx.game.Entities.EntityAnimation.State;


public class InputController implements InputProcessor {
	enum Keys {
		LEFT, RIGHT, UP, DOWN, QUIT
	}

	enum Mouse {
		SELECT, DOACTION
	}

	private static EnumMap<Keys, Boolean> keys = new EnumMap<InputController.Keys, Boolean>(Keys.class);
	private static EnumMap<Mouse, Boolean> mouseButtons = new EnumMap<InputController.Mouse, Boolean>(Mouse.class);
	private Vector3 lastMouseCoordinates;


	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.QUIT, false);
	}

	static {
		mouseButtons.put(Mouse.SELECT, false);
		mouseButtons.put(Mouse.DOACTION, false);
	}

	private Entity unit;

	public void changePlayer(Entity unit){
		this.unit = unit;
	}

	public InputController(Entity unit){
		this.lastMouseCoordinates = new Vector3();
		this.unit = unit;
	}

	@Override
	public boolean keyDown(int keycode) {
		if( keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
			this.leftPressed();
		}
		if( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D){
			this.rightPressed();
		}
		if( keycode == Input.Keys.UP || keycode == Input.Keys.W){
			this.upPressed();
		}
		if( keycode == Input.Keys.DOWN || keycode == Input.Keys.S){
			this.downPressed();
		}
		if( keycode == Input.Keys.Q){
			this.quitPressed();
		}

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if( keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
			this.leftReleased();
		}
		if( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D){
			this.rightReleased();
		}
		if( keycode == Input.Keys.UP || keycode == Input.Keys.W ){
			this.upReleased();
		}
		if( keycode == Input.Keys.DOWN || keycode == Input.Keys.S){
			this.downReleased();
		}
		if( keycode == Input.Keys.Q){
			this.quitReleased();
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if( button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT ){
			this.setClickedMouseCoordinates(screenX, screenY);
		}

		if( button == Input.Buttons.LEFT){
			this.selectMouseButtonPressed();
		}
		if( button == Input.Buttons.RIGHT){
			this.doActionMouseButtonPressed();
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if( button == Input.Buttons.LEFT){
			this.selectMouseButtonReleased();
		}
		if( button == Input.Buttons.RIGHT){
			this.doActionMouseButtonReleased();
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public void dispose(){
		
	}
	
	//Key presses
	public void leftPressed(){
		keys.put(Keys.LEFT, true);
	}
	
	public void rightPressed(){
		keys.put(Keys.RIGHT, true);
	}
	
	public void upPressed(){
		keys.put(Keys.UP, true);
	}
	
	public void downPressed(){
		keys.put(Keys.DOWN, true);
	}
	public void quitPressed(){
		keys.put(Keys.QUIT, true);
	}
	
	public void setClickedMouseCoordinates(int x,int y){
		lastMouseCoordinates.set(x, y, 0);
	}
	
	public void selectMouseButtonPressed(){
		mouseButtons.put(Mouse.SELECT, true);
	}
	
	public void doActionMouseButtonPressed(){
		mouseButtons.put(Mouse.DOACTION, true);
	}
	
	//Releases
	
	public void leftReleased(){
		keys.put(Keys.LEFT, false);
	}
	
	public void rightReleased(){
		keys.put(Keys.RIGHT, false);
	}
	
	public void upReleased(){
		keys.put(Keys.UP, false);
	}
	
	public void downReleased(){
		keys.put(Keys.DOWN, false);
	}
	
	public void quitReleased(){
		keys.put(Keys.QUIT, false);
	}
	
	public void selectMouseButtonReleased(){
		mouseButtons.put(Mouse.SELECT, false);
	}
	
	public void doActionMouseButtonReleased(){
		mouseButtons.put(Mouse.DOACTION, false);
	}
	
	
	public void update(){
		processInput();
	}
	
	public static void hide(){
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.QUIT, false);
	}
	
	private void processInput(){
		if( keys.get(Keys.LEFT)){
			unit.setState(State.WALKING);
			unit.setDirection(Direction.LEFT);
		}else if( keys.get(Keys.RIGHT)){
			unit.setState(State.WALKING);
			unit.setDirection(Direction.RIGHT);
		}else if( keys.get(Keys.UP)){
			unit.setState(State.WALKING);
			unit.setDirection(Direction.UP);
		}else if(keys.get(Keys.DOWN)){
			unit.setState(State.WALKING);
			unit.setDirection(Direction.DOWN);
		}else{
			unit.setState(State.IDLE);
		}
		
		if( mouseButtons.get(Mouse.SELECT)) {
			mouseButtons.put(Mouse.SELECT, false);
		}

	}

}

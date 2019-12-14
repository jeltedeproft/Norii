package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class BattleScreenInputProcessor implements InputProcessor{
	
    private boolean leftCameraMove;
    private boolean rightCameraMove;
    private boolean upCameraMove;
    private boolean downCameraMove;
    private boolean isPaused;
    private Screen battleScreen;
    private OrthographicCamera mapCamera;
    
    private static final int CAMERA_SPEED = 10;
    
    public BattleScreenInputProcessor(Screen battleScreen,OrthographicCamera camera) {
    	this.mapCamera = camera;
    	this.battleScreen = battleScreen;
    	isPaused = false;
    }
    
	public void update() {
		float x = mapCamera.position.x;
		float y = mapCamera.position.y;
		float z = mapCamera.position.z;
		
		
	    if (leftCameraMove)
	    {
	    	x -= CAMERA_SPEED * Gdx.graphics.getDeltaTime();
	    }
	    if (rightCameraMove)
	    {
	    	x += CAMERA_SPEED * Gdx.graphics.getDeltaTime();
	    }
	    if (upCameraMove)
	    {
	    	y += CAMERA_SPEED * Gdx.graphics.getDeltaTime();
	    }
	    if (downCameraMove)
	    {
	    	y -= CAMERA_SPEED * Gdx.graphics.getDeltaTime();
	    }
	
		mapCamera.position.set(x, y, z);
		
	}

	@Override
	public boolean keyDown(int keycode) {
	    switch (keycode){
			case Keys.LEFT:
				setLeftMove(true);
				break;
			case Keys.RIGHT:
				setRightMove(true);
				break;
			case Keys.UP:
				setUpMove(true);
				break;
			case Keys.DOWN:
				setDownMove(true);
				break;
			default:
				break;
		}
	    return true;
	}
	
	@Override
	public boolean keyUp(int keycode) {
	    switch (keycode){
			case Keys.LEFT:
				setLeftMove(false);
				break;
			case Keys.RIGHT:
				setRightMove(false);
				break;
			case Keys.UP:
				setUpMove(false);
				break;
			case Keys.DOWN:
				setDownMove(false);
				break;
			case Keys.ESCAPE:
				if(isPaused) {
					battleScreen.resume();
				}else {
					battleScreen.pause();
				}
				isPaused = !isPaused;
				break;
			default:
				break;
		}
	    return true;
	}
	
    public void setLeftMove(boolean t)
    {
	    if(rightCameraMove && t) rightCameraMove = false;
	    leftCameraMove = t;
    }
    public void setRightMove(boolean t)
    {
	    if(leftCameraMove && t) leftCameraMove = false;
	    rightCameraMove = t;
    }
    public void setUpMove(boolean t)
    {
    	if(downCameraMove && t) downCameraMove = false;
    	upCameraMove = t;
    }
    public void setDownMove(boolean t)
    {
    	if(upCameraMove && t) upCameraMove = false;
    	downCameraMove = t;
    }

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
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

}

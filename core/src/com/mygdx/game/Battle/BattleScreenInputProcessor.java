package com.mygdx.game.Battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.game.Screen.BattleScreen;

public class BattleScreenInputProcessor implements InputProcessor {

	private boolean leftCameraMove;
	private boolean rightCameraMove;
	private boolean upCameraMove;
	private boolean downCameraMove;
	private boolean isPaused;
	private final BattleScreen battleScreen;
	private final OrthographicCamera mapCamera;
	private BattleManager bm;

	private static final int CAMERA_SPEED = 10;

	public BattleScreenInputProcessor(BattleScreen battleScreen, OrthographicCamera camera) {
		this.mapCamera = camera;
		this.battleScreen = battleScreen;
		isPaused = false;
	}

	public void setBattleManager(BattleManager bm) {
		this.bm = bm;
	}

	public void update() {
		float x = mapCamera.position.x;
		float y = mapCamera.position.y;
		final float z = mapCamera.position.z;

		if (leftCameraMove) {
			x -= CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}
		if (rightCameraMove) {
			x += CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}
		if (upCameraMove) {
			y += CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}
		if (downCameraMove) {
			y -= CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}

		mapCamera.position.set(x, y, z);

	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
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
		switch (keycode) {
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
			if (isPaused && battleScreen.isPaused()) {
				battleScreen.resume();
			} else {
				battleScreen.pause();
			}
			isPaused = !isPaused;
			break;
		case Keys.Z:
			sendKeyToBattlestate(Keys.Z);
			break;
		case Keys.Q:
			sendKeyToBattlestate(Keys.Q);
			break;
		case Keys.S:
			sendKeyToBattlestate(Keys.S);
			break;
		case Keys.D:
			sendKeyToBattlestate(Keys.D);
			break;
		default:
			break;
		}
		return true;
	}

	private void setLeftMove(boolean t) {
		if (rightCameraMove && t)
			rightCameraMove = false;
		leftCameraMove = t;
	}

	private void setRightMove(boolean t) {
		if (leftCameraMove && t)
			leftCameraMove = false;
		rightCameraMove = t;
	}

	private void setUpMove(boolean t) {
		if (downCameraMove && t)
			downCameraMove = false;
		upCameraMove = t;
	}

	private void setDownMove(boolean t) {
		if (upCameraMove && t)
			upCameraMove = false;
		downCameraMove = t;
	}

	private void sendKeyToBattlestate(int key) {
		bm.getCurrentBattleState().keyPressed(key);
	}

	private void sendButtonToBattlestate(int button) {
		bm.getCurrentBattleState().buttonPressed(button);
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		sendButtonToBattlestate(button);
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
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

}

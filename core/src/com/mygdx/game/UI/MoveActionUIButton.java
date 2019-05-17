package com.mygdx.game.UI;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Battle.BattleManager;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Map.MyPathFinder;
import com.mygdx.game.Particles.ParticleMaker;
import com.mygdx.game.Particles.ParticleType;

import Utility.TiledMapPosition;

public class MoveActionUIButton extends ActionUIButton{
	private Entity linkedUnit;
	private BattleManager battlemanager;

	public MoveActionUIButton(String imageFileName, Action action, final Entity linkedUnit) {
		super(imageFileName, action);
		this.active = true;
		this.linkedUnit = linkedUnit;
		this.battlemanager = battlemanager;


		button.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y) {
		    	if(linkedUnit.canMove()) { 
		    		linkedUnit.setInMovementPhase(!linkedUnit.isInMovementPhase());

		    		if(linkedUnit.isInMovementPhase()) {
			    		List<GridCell> path = MyPathFinder.getCellsWithin(linkedUnit.getCurrentPosition().getTileX(), linkedUnit.getCurrentPosition().getTileY(), linkedUnit.getMp());
			    		for(GridCell cell : path) {
			    			TiledMapPosition positionToPutMoveParticle = new TiledMapPosition(cell.x,cell.y);
			    			ParticleMaker.addParticle(ParticleType.MOVE,positionToPutMoveParticle );
			    		}
		    		}
		    	}	
		    }
		});
	}



}

package com.mygdx.game.UI;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Profile.ProfileManager;
import com.mygdx.game.Profile.ProfileObserver;


public class PlayerBattleHUD implements Screen, ProfileObserver {
	private static final String TAG = PlayerBattleHUD.class.getSimpleName();
	
    private Stage _stage;
    private Viewport _viewport;
    private Entity[] SortedUnits;
    private PortraitsUI _portraits;
    private StatusUI[] _statusUIs;
//    private InventoryUI _inventoryUI;
    private Camera _camera;

    public PlayerBattleHUD(Camera camera,Entity[] SortedUnits) {
    	this.SortedUnits = SortedUnits;
    	_statusUIs = new StatusUI[SortedUnits.length];
        _camera = camera;
        _viewport = new ScreenViewport(_camera);
        _stage = new Stage(_viewport);
        _stage.setDebugAll(false);//!!!!!!!!!!!!!!!! on for debug
        
        //create a status window for every unit
        for (int i = 0; i < SortedUnits.length; i++) {
        	Entity entity = SortedUnits[i];
        	_statusUIs[i] = new StatusUI(entity);
        	StatusUI statusui = _statusUIs[i];
	        
	        //status window blocks input beneath
        	statusui.addListener(new InputListener() {
	        	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	         		return true;
	         	}
	        });
	        
	        _stage.addActor(statusui);
        }
        
        //add portraits
    	_portraits = new PortraitsUI(SortedUnits);
    	
        //status window blocks input beneath
    	_portraits.addListener(new InputListener() {
        	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
         		return true;
         	}
        });
    	
    	_stage.addActor(_portraits);
        

//        _inventoryUI = new InventoryUI();
//        _inventoryUI.setMovable(false);
//        _inventoryUI.setVisible(false);
//        _inventoryUI.setPosition(_stage.getWidth()/2, 0);
        
        
//        _stage.addActor(_inventoryUI);
        
        
        //add tooltips to the stage
//        Array<Actor> actors = _inventoryUI.getInventoryActors();
//        for(Actor actor : actors){
//            _stage.addActor(actor);
//        }

//        ImageButton inventoryButton = _statusUI.getInventoryButton();
//        inventoryButton.addListener(new ClickListener() {
//            public void clicked (InputEvent event, float x, float y) {
//                _inventoryUI.setVisible(_inventoryUI.isVisible()?false:true);
//            }
//        });
    }

    public Stage getStage() {
        return _stage;
    }

    @Override
    public void onNotify(ProfileManager profileManager, ProfileEvent event) {
//        switch(event){
//            case PROFILE_LOADED:
//                Array<InventoryItemLocation> inventory = profileManager.getProperty("playerInventory", Array.class);
//                if( inventory != null && inventory.size > 0 ){
//                    _inventoryUI.populateInventory(_inventoryUI.getInventorySlotTable(), inventory);
//                }else{
//                    //add default items if nothing is found
//                    Array<ItemTypeID> items = _player.getEntityConfig().getInventory();
//                    Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
//                    for( int i = 0; i < items.size; i++){
//                        itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1));
//                    }
//                    _inventoryUI.populateInventory(_inventoryUI.getInventorySlotTable(), itemLocations);
//                }
//
//                Array<InventoryItemLocation> equipInventory = profileManager.getProperty("playerEquipInventory", Array.class);
//                if( equipInventory != null && equipInventory.size > 0 ){
//                    _inventoryUI.populateInventory(_inventoryUI.getEquipSlotTable(), equipInventory);
//                }
//
//                break;
//            case SAVING_PROFILE:
//                profileManager.setProperty("playerInventory", _inventoryUI.getInventory(_inventoryUI.getInventorySlotTable()));
//                profileManager.setProperty("playerEquipInventory", _inventoryUI.getInventory(_inventoryUI.getEquipSlotTable()));
//                break;
//            default:
//                break;
//        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        _stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        _stage.dispose();
    }

}


package com.mygdx.game.Map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BattleMap extends Map{
    private static final String TAG = BattleMap.class.getSimpleName();

    private static String _mapPath = "maps/32x32 rpg battlemap.tmx";


    BattleMap(){
        super(MapFactory.MapType.BATTLE_MAP, _mapPath);

        for( Vector2 position: _npcStartPositions){
            _mapEntities.add(initEntity(Entity.getEntityConfig(_townGuardWalking), position));
        }

        //Special cases
        _mapEntities.add(initSpecialEntity(Entity.getEntityConfig(_townBlacksmith)));
        _mapEntities.add(initSpecialEntity(Entity.getEntityConfig(_townMage)));
        _mapEntities.add(initSpecialEntity(Entity.getEntityConfig(_townInnKeeper)));

        //When we have multiple configs in one file
        Array<EntityConfig> configs = Entity.getEntityConfigs(_townFolk);
        for(EntityConfig config: configs){
            _mapEntities.add(initSpecialEntity(config));
        }
    }

    @Override
    public void updateMapEntities(MapManager mapMgr, Batch batch, float delta){
        for( int i=0; i < _mapEntities.size; i++){
            _mapEntities.get(i).update(mapMgr, batch, delta);
        }
    }

    private Entity initEntity(EntityConfig entityConfig, Vector2 position){
        Entity entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC);
        entity.setEntityConfig(entityConfig);

        entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, _json.toJson(entity.getEntityConfig()));
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, _json.toJson(position));
        entity.sendMessage(Component.MESSAGE.INIT_STATE, _json.toJson(entity.getEntityConfig().getState()));
        entity.sendMessage(Component.MESSAGE.INIT_DIRECTION, _json.toJson(entity.getEntityConfig().getDirection()));

        return entity;
    }

    private Entity initSpecialEntity(EntityConfig entityConfig){
        Vector2 position = new Vector2(0,0);

        if( _specialNPCStartPositions.containsKey(entityConfig.getEntityID()) ) {
             position = _specialNPCStartPositions.get(entityConfig.getEntityID());
        }
        return initEntity(entityConfig, position);
    }
}


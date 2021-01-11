package com.jelte.norii.entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jelte.norii.battle.MessageToBattleScreen;

public class EntityClickListener extends ClickListener {
	private final EntityActor actor;

	public EntityClickListener(EntityActor actor) {
		this.actor = actor;
	}

	@Override
	public void clicked(InputEvent event, float x, float y) {
		actor.getEntity().getOwner().sendMessageToBattleManager(MessageToBattleScreen.CLICKED, actor.getEntity());
	}

	@Override
	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		actor.setIsHovering(true);
		actor.getEntity().getOwner().sendMessageToBattleManager(MessageToBattleScreen.SHOW_STATUS_UI, actor.getEntity());
	}

	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		actor.setIsHovering(false);
		actor.getEntity().getOwner().sendMessageToBattleManager(MessageToBattleScreen.HIDE_STATUS_UI, actor.getEntity());
	}
}

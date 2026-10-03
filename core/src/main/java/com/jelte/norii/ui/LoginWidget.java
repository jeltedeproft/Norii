package com.jelte.norii.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.jelte.norii.utility.AssetManagerUtility;

public class LoginWidget {
	private static final String USERNAME = "Username";
	private static final String PASSWORD = "Password";
	private static final String CREATE = "Create Account";
	private static final String TITLE = "Log In";
	private static final String LOGIN = "Log In";

	private Window window;
	private Table mainTable;

	private Label usernameLabel;
	private Label passwordLabel;
	private Label createAccountLabel;

	private TextField usernameField;
	private TextField passwordField;

	private TextButton loginTextButton;

	public LoginWidget() {
		initWidgets();
		fillWindow();
	}

	private void initWidgets() {
		window = new Window(TITLE, AssetManagerUtility.getSkin());
		mainTable = new Table();
		usernameLabel = new Label(USERNAME, AssetManagerUtility.getSkin());
		passwordLabel = new Label(PASSWORD, AssetManagerUtility.getSkin());
		createAccountLabel = new Label(CREATE, AssetManagerUtility.getSkin());
		usernameField = new TextField("", AssetManagerUtility.getSkin());
		passwordField = new TextField("", AssetManagerUtility.getSkin());
		loginTextButton = new TextButton(LOGIN, AssetManagerUtility.getSkin());
	}

	private void fillWindow() {
		mainTable.pad(8);
		mainTable.add(usernameLabel).left().padRight(8);
		mainTable.add(usernameField).width(160).height(28).row();
		mainTable.add(passwordLabel).left().padRight(8).padTop(6);
		mainTable.add(passwordField).width(160).height(28).padTop(6).row();
		mainTable.add(loginTextButton).colspan(2).width(140).height(30).padTop(10).row();
		mainTable.add(createAccountLabel).colspan(2).padTop(6);
		window.add(mainTable);
	}

	public Window getWindow() {
		return window;
	}

	public TextButton getLoginTextButton() {
		return loginTextButton;
	}

	public String getUsername() {
		return usernameField.getMessageText();
	}

	public String getPassword() {
		return passwordField.getMessageText();
	}
}

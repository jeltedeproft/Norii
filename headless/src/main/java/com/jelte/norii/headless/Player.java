package com.jelte.norii.headless;

import java.util.List;

import com.jelte.norii.entities.Entity;

public class Player
{
    private ConnectedClient connectedClient;
    private List<Entity> team;

    public Player(ConnectedClient r, List<Entity> team)
    {
    	connectedClient = r;
        this.team = team;
    }

    public ConnectedClient getConnectedClient()
    {
        return connectedClient;
    }

    public List<Entity> getTeam()
    {
        return team;
    }
}
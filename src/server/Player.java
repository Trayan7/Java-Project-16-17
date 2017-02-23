package server;

import client.ClientInterface;


public class Player {
	/*
	 * Current health of player
	 */
	private int health = 100;
	
	/**
	 * Current player position of the x axis
	 */
	private int x;
	
	/**
	 * Current player position on the y axis
	 */
	private int y;
	
	/**
	 * The name of the player
	 */
	private String name;
	
	/**
	 * The client object of this player
	 */
	private ClientInterface client;
	
	//TODO Some kind of reference to the client
	
	/**
	 * Player constructor
	 * @param x Start x coordinate of player
	 * @param y Start y coordinate of player 
	 */
	public Player(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets a new position of the player on the x axis
	 * @param x The new x coordinate of the player
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Sets a new position of the player on the y axis
	 * @param y The new y coordinate of the player
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getHealth() {
		return this.health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	/**
	 * Function to let the player take a hit and lose some life
	 * @param lifeloss The amount of life lost
	 */
	public void hit(int lifeloss) {
		if (this.health < lifeloss) {
			this.health = 0;
		} else {
			this.health -= lifeloss;
		}
	}
	
	/**
	 * Function to set the reference to the client
	 */
	public void setClient() {
		//TODO Some setter function to set the client reference
	}
	
	/**
	 * Sets the client of this player
	 * @param client The client of this player
	 */
	public void setClient(ClientInterface client) {
		this.client = client;
	}
	
	/**
	 * Returns the client of this player
	 * @return The client of this player
	 */
	public ClientInterface getClient() {
		return client;
	}
	
	/**
	 * Returns the name of the player
	 * @return The name of the player
	 */
	public String getName() {
		return name;
	}
}

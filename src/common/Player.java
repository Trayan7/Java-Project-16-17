package common;

import java.io.Serializable;
import java.util.UUID;

import client.ClientInterface;

public class Player implements Serializable {
	/**
	 * SerialVersionUID for serialization of object
	 */
	private static final long serialVersionUID = 1L;

	/**
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
	 * ID of the player
	 */
	private UUID id;

	/**
	 * The client object of this player
	 */
	private ClientInterface client;

	/**
	 * Player constructor
	 * 
	 * @param x
	 *            Start x coordinate of player
	 * @param y
	 *            Start y coordinate of player
	 */
	public Player(UUID id, String name, int x, int y) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets a new position of the player on the x axis
	 * 
	 * @param x
	 *            The new x coordinate of the player
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets a new position of the player on the y axis
	 * 
	 * @param y
	 *            The new y coordinate of the player
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets the position of the player on the x axis 
	 * @return The x coordinate of the player
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the position of the player on the y axis 
	 * @return The y coordinate of the player
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the amount of health the player has.
	 * @return Integer of the health of the player
	 */
	public int getHealth() {
		return this.health;
	}

	/**
	 * Sets the health value of the player
	 * @param health The integer value the player's health is set to
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * Function to let the player take a hit and lose some life
	 * 
	 * @param lifeloss
	 *            The amount of life lost
	 */
	public void hit(int lifeloss) {
		if (this.health < lifeloss) {
			this.health = 0;
		} else {
			this.health -= lifeloss;
		}
	}

	/**
	 * Sets the client of this player
	 * 
	 * @param client
	 *            The client of this player
	 */
	public void setClient(ClientInterface client) {
		this.client = client;
	}

	/**
	 * Returns the client of this player
	 * 
	 * @return The client of this player
	 */
	public ClientInterface getClient() {
		return client;
	}

	/**
	 * Returns the name of the player
	 * 
	 * @return The name of the player
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the unique user id of the player
	 * @return The UUID of the player
	 */
	public UUID getID() {
		return id;
	}
}
package it.unozerouno.givemetime.view.utilities;

/**
 * This class describes the information of the user in the nav drawer
 * @author Paolo
 *
 */

public class DrawerUserProfile {

	private int drawableResID;
	private String name;
	private String email;
	
	public DrawerUserProfile(int drawableResID, String name, String email) {
		super();
		this.drawableResID = drawableResID;
		this.name = name;
		this.email = email;
	}

	public int getDrawableResID() {
		return drawableResID;
	}

	public void setDrawableResID(int drawableResID) {
		this.drawableResID = drawableResID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}

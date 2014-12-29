package it.unozerouno.givemetime.view.utilities;

/**
 * This class contains the information regarding the elements inside the navigation drawer
 * @author Paolo
 *
 */
public class DrawerItem {

	private String itemName;
	private int imgResID;
	private String title;
	private boolean isSpinner;
	
	// constructor for spinner 
	public DrawerItem(boolean isSpinner){
		this(null, 0);
		this.isSpinner = isSpinner;
	}
	
	// constructor for headers
	public DrawerItem(String title){
		this(null, 0);
		this.title = title;
	}
	
	// constructor for items
	public DrawerItem(String itemName, int imgResID) {
		super();
		this.itemName = itemName;
		this.imgResID = imgResID;
	}
	
	public boolean isSpinner(){
		return isSpinner;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getImgResID() {
		return imgResID;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setImgResID(int imgResID) {
		this.imgResID = imgResID;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
}

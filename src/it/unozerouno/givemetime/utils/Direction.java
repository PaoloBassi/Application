package it.unozerouno.givemetime.utils;

/**
 * This enum is used to understand the direction in which the user is moving the finger
 * @author Paolo
 *
 */

public enum Direction {
	NONE(false), VERTICAL(false), LEFT(true), RIGHT(true);

    public boolean isHorizontal(){
        return this.isHorizontal;
    }

    Direction(boolean isHorizontal){
        this.isHorizontal = isHorizontal;
    }
    
    private boolean isHorizontal;
}

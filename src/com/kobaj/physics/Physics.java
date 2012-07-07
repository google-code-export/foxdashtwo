package com.kobaj.physics;

import android.graphics.Rect;

//broken down into three parts
//1. gravity/force application.
//2. Collision detection
//3. Collision handling
public class Physics
{
	//I honestly don't know the best way to tackle this.
	//half of me wants to make it abstract and have a quad extend it.
	//but I really want to keep it separate from quads...
	
	private final static double gravity = 2.9; //well thats a random number.
	
	public static void apply_force(){}
	
	public static void apply_gravity(){}
	
	public static Rect check_collision(){ return null;}
	
	public static void handle_collision(){}
}

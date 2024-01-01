package com.arittek.befiler_services.test;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 * ---
 * Hint: You can use the debug stream to print initialTX and initialTY, if Thor seems not follow your orders.
 **/
class Player {

    public static void main(String args[]) {
	Scanner in = new Scanner(System.in);
	int lightX = in.nextInt(); // the X position of the light of power
	int lightY = in.nextInt(); // the Y position of the light of power
	int initialTX = in.nextInt(); // Thor's starting X position
	int initialTY = in.nextInt(); // Thor's starting Y position

	// game loop
	while (initialTX != lightX || initialTY != lightY) {
	    /*int remainingTurns = in.nextInt(); // The remaining amount of turns Thor can move. Do not remove this line.*/

	    if (initialTX < lightX && initialTY < lightY)  {
		initialTX++;
		initialTY++;
		System.out.println("SE");
	    } else if (initialTX > lightX && initialTY > lightY)  {
		initialTX--;
		initialTY--;
		System.out.println("NW");
	    } else if (initialTX == lightX && initialTY < lightY)  {
		initialTY++;
		System.out.println("S");
	    } else if (initialTX == lightX && initialTY > lightY)  {
		initialTY--;
		System.out.println("N");
	    } else if (initialTX < lightX && initialTY == lightY)  {
		initialTX++;
		System.out.println("E");
	    } else if (initialTX > lightX && initialTY == lightY)  {
		initialTX--;
		System.out.println("W");
	    } else if (initialTX < lightX && initialTY > lightY)  {
		initialTX++;
		initialTY--;
		System.out.println("NE");
	    } else if (initialTX < lightX && initialTY > lightY)  {
		initialTX--;
		initialTY++;
		System.out.println("SW");
	    }

	}
    }
}

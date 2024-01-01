package com.arittek.befiler_services.test;

public class KhadimTest {

    public static void main(String args[]) {
        /*String[][] list = new String[3][2];

        list[0][0] = "A";
	list[0][1] = "B";

	list[1][0] = "B";
	list[1][1] = "C";

	list[2][0] = "C";
	list[2][1] = "A";*/

	String list[][]={
		{"D","E"},{"F","D"},{"R","F"}
	};

	String start = list[0][0];

	for (int i=1; i<list.length; i++) {
	    if (start == list[i][list[i].length-1]) {
	        start = list[i][0];
	    }
	}

	System.out.println("The starting place of the journey given by exampleTrips is \""+ start +"\".");


    }
}

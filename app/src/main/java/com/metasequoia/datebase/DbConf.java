package com.metasequoia.datebase;

public class DbConf {
	public static final int RADIO_VERSION = 1;   //radio table version
	public static final String RADIO_NAME = "radio.db";  //radio table name

	public static final String TAB_RADIO= "points";  //radio table

	public static class Freq {
		public static final String Points_ID="id";  //radio table id
		public static final String FREQUENCY="frequency";  //radio table frequency
		public static final String BAND="band";  //radio table band
		public static final String PSNAME="psname";
		public static final String INDEX="indexx";
		//public static final String AREA="area";  //radio table area
		public static final String UNINUNAME="uninuname";  //radio table uninuname
	}
}

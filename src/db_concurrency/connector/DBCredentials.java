package db_concurrency.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Preuss
 */
public class DBCredentials {
	private static String USERNAME = "DB_039";
	private static String PASSWORD = "db2015";
	private static String HOST_NAME = "datdb.cphbusiness.dk";
	private static String PORT = "1521";
	private static String SID = "dat";
	private static String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
	private static String JDBC_URL = "jdbc:oracle:thin:@" + HOST_NAME + ":" + PORT + ":" + SID;

	static {
		Path currentRelativePath = Paths.get("../db_concurrency_login.properties");
		Path credentialsProp = currentRelativePath.toAbsolutePath().normalize();
		if(credentialsProp.toFile().exists()) {
			loadCredentials(credentialsProp.toFile());
		}
		System.out.println("CurrentRelativePath: " + credentialsProp);
	}

	public static String getUSERNAME() {
		return USERNAME;
	}

	public static String getPASSWORD() {
		return PASSWORD;
	}

	public static String getHOST_NAME() {
		return HOST_NAME;
	}

	public static String getPORT() {
		return PORT;
	}

	public static String getSID() {
		return SID;
	}

	public static String getDRIVER_CLASS() {
		return DRIVER_CLASS;
	}

	public static String getJDBC_URL() {
		return JDBC_URL;
	}

	private static void loadCredentials(File credentials) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(credentials));
			USERNAME = props.getProperty("USERNAME", USERNAME);
			PASSWORD = props.getProperty("PASSWORD", PASSWORD);
			HOST_NAME = props.getProperty("HOST_NAME", HOST_NAME);
			PORT = props.getProperty("PORT", PORT);
			SID = props.getProperty("SID", SID);
			DRIVER_CLASS = props.getProperty("DRIVER_CLASS", DRIVER_CLASS);
			JDBC_URL = props.getProperty("JDBC_URL", JDBC_URL);
		} catch(IOException e) {
			System.out.println("Exception Occurred" + e.getMessage());
		}

	}
}

package com.datads;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.datads.XMLUtil;

import java.io.FileInputStream;

import java.nio.file.Path;
import java.nio.file.Paths;

//import org.springframework.stereotype.Component;

/**
 * 
 * @author 10719785
 * 
 *         DataDS_ParsingService class does the following:
 * 
 *         1. Establishes a connection to MySQL database 2. Reads XML input file
 *         3. Parse XML file into a map data structure 4. Extracts map data for
 *         CSV Parsing 5. Writes map data to CSV files 6. Creates MySQL tables
 *         according to XML files 7. Extracts map data for MySQL database 8.
 *         Inserts data into MySQL database
 * 
 * 
 *         DataDS_ParsingService class works with XML files in the DATA DS
 *         Folders
 *
 */

//@Component
public class DataDS_ParsingService {
	
	/*
	private static Connection connection;
	private Map<String, String> parse;
	private Workbook workbook;
	private Sheet g1Sheet;
	private Sheet g2Sheet;
	private Sheet g3Sheet;
	private Sheet details_BI_ServerSheet;
	private Sheet budget_EssbaseSheet;
	private Sheet sectionsSheet;
	private Sheet grid0_1Sheet;
	
	public DataDS_ParsingService6() {
	  
	  workbook = new XSSFWorkbook();
	  
	  g1Sheet = workbook.createSheet("G1"); g2Sheet = workbook.createSheet("G2");
	  g3Sheet = workbook.createSheet("G3"); details_BI_ServerSheet =
	  workbook.createSheet("Details_BI_Server"); budget_EssbaseSheet =
	  workbook.createSheet("Budget_Essbase"); sectionsSheet =
	  workbook.createSheet("Sections"); grid0_1Sheet =
	  workbook.createSheet("Grid0_1");
	  
	  }

	public void parse(Document document, String filePath, String outputFileLocation) {
		try {
			parse = parseXmlDoc(document);
			extractMapData(parse, filePath);
			Connection connection = SampleParsingService.databaseConnection();
			extractMapDataForDb(connection, parse);

		} catch (Exception e) {
			System.out.println("Exception occurred");
			System.out.println("Exception Message: " + e.getMessage());
		}

		for (Map.Entry<String, String> entry : parse.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
	}
	*/ 
	 
	public static void main(String args[]) throws Exception {

		// File file = new
		// File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\DataDSCombined.xml");

		// File file = new
		// File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\DataDSCombinedTest.xml");

		File file = new File(
				"C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\sfo+passenger+count+data+model%2exml");

		// File file = new
		// File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\sample%2exml3");

		// File file = new
		// File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\sample%2exml1");

		// File file = new
		// File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\sample%2exml5");

		// File file = new
		// File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\sample%2exml10");

		// File file = new
		// File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\sample%2exml7");

		// File file = new
		// File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\_xdo_local%2erev+budg+actual+and+details+dm%2exml");

		// File file = new
		// File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\sample%2exml14");

		String absolutePath = file.getAbsolutePath();

		// outputFileLocation is created based on the XML file that is being read
		String outputFileLocation = "C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\datads\\Data_DS\\DataDSCSV";

		System.out.println("Reading XML file...");
		Document doc = readXMLDocumentFromFile(absolutePath);

		System.out.println("Parsing XML file...");
		Map<String, String> parse = parseXmlDoc(doc);

		for (Map.Entry<String, String> entry : parse.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}

		System.out.println("Extracting XML data to CSV files...");
		extractMapData(parse, outputFileLocation);

		System.out.println("Establishing connection to MySQl Database...");
		Connection conn = databaseConnection();

		System.out.println("Saving Parsed XML data to MySQL Database...");
		extractMapDataForDb(conn, parse);

	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection databaseConnection() throws SQLException {

		// Please create "DataDSDB" prior to running code
		// Change password and username accordingly
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DataDSDB?useSSL=false", "root",
				"root123");

		createTables(conn);
		return conn;
	}

	/**
	 * 
	 * @param fileNameWithPath
	 * @return
	 * @throws Exception
	 */
	public static Document readXMLDocumentFromFile(String fileNameWithPath) throws Exception {

		// Get Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Build Document
		Document document = builder.parse(new File(fileNameWithPath));

		// Normalize the XML Structure
		document.getDocumentElement().normalize();
		return document;
	}

	/**
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> parseXmlDoc(Document doc) throws Exception {

		Map<String, String> map = new HashMap<>();

		// .replaceAll("\\s+", ""); removes all trailing whitespaces that may occur from

		// Reading XML data from Report child node
		Element dATA_DSElm = doc.getDocumentElement();
		// String contOfDATA_DSElm = dATA_DSElm.getTextContent().replaceAll("\\s+", "");

		// G1_Type1
		List<Element> g_1NdList = XMLUtil.getChildElements(dATA_DSElm, "G_1");

		StringBuilder g1Builder = new StringBuilder();

		// NodeList nodeList = doc.getElementsByTagName("ID");
		if (doc.getElementsByTagName("ID").getLength() > 0) {

			// StringBuilder g1Type1Builder = new StringBuilder();
			// Creating Column names for g1Type1 CSV file
			g1Builder.append("ID").append("|").append("YEAR").append("|").append("MONTH").append("|")
					.append("OPERATING_AIRLINE").append("|").append("OPERATING_AIRLINE_IATA_CODE").append("|")
					.append("GEO_SUMMARY").append("|").append("GEO_REGION").append("|").append("ACTIVITY_TYPE_CODE")
					.append("|").append("PRICE_CATEGORY_CODE").append("|").append("TERMINAL").append("|")
					.append("BOARDING_AREA").append("|").append("CURRENT_COUNT").append("|").append("PREVIOUS_COUNT")
					.append("|").append("CHANGE_COUNT").append("|").append("PERCENT_CHANGE").append("|").append(";\n");

			for (Element g_1Elm : g_1NdList) {

				// NodeList nodeList = doc.getElementsByTagName("ID");
				// if(nodeList.getLength() > 0) {

				// String isMulti = g_1Elm.getAttribute("IsMulti");
				String contOfIDElm = null;
				String contOfYEARElm = null;
				String contOfMONTHElm = null;
				String contOfOPERATING_AIRLINEElm = null;
				String contOfOPERATING_AIRLINE_IATA_CODEElm = null;
				String contOfGEO_SUMMARYElm = null;
				String contOfGEO_REGIONElm = null;
				String contOfACTIVITY_TYPE_CODEElm = null;
				String contOfPRICE_CATEGORY_CODEElm = null;
				String contOfTERMINALElm = null;
				String contOfBOARDING_AREAElm = null;
				String contOfCURRENT_COUNTElm = null;
				String contOfPREVIOUS_COUNTElm = null;
				String contOfCHANGE_COUNTElm = null;
				String contOfPERCENT_CHANGEElm = null;

				List<Element> iDNdList = XMLUtil.getChildElements(g_1Elm, "ID");
				for (Element iDElm : iDNdList) {

					contOfIDElm = iDElm.getTextContent().replaceAll("\\s+", "");
					if (contOfIDElm.isEmpty()) {
						contOfIDElm = null;
					}

				}

				List<Element> yEARNdList = XMLUtil.getChildElements(g_1Elm, "YEAR");
				for (Element yEARElm : yEARNdList) {

					contOfYEARElm = yEARElm.getTextContent().replaceAll("\\s+", "");
					if (contOfYEARElm.isEmpty()) {
						contOfYEARElm = null;
					}

				}

				List<Element> mONTHNdList = XMLUtil.getChildElements(g_1Elm, "MONTH");
				for (Element mONTHElm : mONTHNdList) {

					contOfMONTHElm = mONTHElm.getTextContent().replaceAll("\\s+", "");
					if (contOfMONTHElm.isEmpty()) {
						contOfMONTHElm = null;
					}

				}

				List<Element> oPERATING_AIRLINENdList = XMLUtil.getChildElements(g_1Elm, "OPERATING_AIRLINE");
				for (Element oPERATING_AIRLINEElm : oPERATING_AIRLINENdList) {

					contOfOPERATING_AIRLINEElm = oPERATING_AIRLINEElm.getTextContent().replaceAll("\\s+", "");
					if (contOfOPERATING_AIRLINEElm.isEmpty()) {
						contOfOPERATING_AIRLINEElm = null;
					}

				}

				List<Element> oPERATING_AIRLINE_IATA_CODENdList = XMLUtil.getChildElements(g_1Elm,
						"OPERATING_AIRLINE_IATA_CODE");
				for (Element oPERATING_AIRLINE_IATA_CODEElm : oPERATING_AIRLINE_IATA_CODENdList) {

					contOfOPERATING_AIRLINE_IATA_CODEElm = oPERATING_AIRLINE_IATA_CODEElm.getTextContent()
							.replaceAll("\\s+", "");
					if (contOfOPERATING_AIRLINE_IATA_CODEElm.isEmpty()) {
						contOfOPERATING_AIRLINE_IATA_CODEElm = null;
					}

				}

				List<Element> gEO_SUMMARYNdList = XMLUtil.getChildElements(g_1Elm, "GEO_SUMMARY");
				for (Element gEO_SUMMARYElm : gEO_SUMMARYNdList) {

					contOfGEO_SUMMARYElm = gEO_SUMMARYElm.getTextContent().replaceAll("\\s+", "");
					if (contOfGEO_SUMMARYElm.isEmpty()) {
						contOfGEO_SUMMARYElm = null;
					}

				}

				List<Element> gEO_REGIONNdList = XMLUtil.getChildElements(g_1Elm, "GEO_REGION");
				for (Element gEO_REGIONElm : gEO_REGIONNdList) {

					contOfGEO_REGIONElm = gEO_REGIONElm.getTextContent().replaceAll("\\s+", "");
					if (contOfGEO_REGIONElm.isEmpty()) {
						contOfGEO_REGIONElm = null;
					}

				}

				List<Element> aCTIVITY_TYPE_CODENdList = XMLUtil.getChildElements(g_1Elm, "ACTIVITY_TYPE_CODE");
				for (Element aCTIVITY_TYPE_CODEElm : aCTIVITY_TYPE_CODENdList) {

					contOfACTIVITY_TYPE_CODEElm = aCTIVITY_TYPE_CODEElm.getTextContent().replaceAll("\\s+", "");
					if (contOfACTIVITY_TYPE_CODEElm.isEmpty()) {
						contOfACTIVITY_TYPE_CODEElm = null;
					}

				}

				List<Element> pRICE_CATEGORY_CODENdList = XMLUtil.getChildElements(g_1Elm, "PRICE_CATEGORY_CODE");
				for (Element pRICE_CATEGORY_CODEElm : pRICE_CATEGORY_CODENdList) {

					contOfPRICE_CATEGORY_CODEElm = pRICE_CATEGORY_CODEElm.getTextContent().replaceAll("\\s+", "");
					if (contOfPRICE_CATEGORY_CODEElm.isEmpty()) {
						contOfPRICE_CATEGORY_CODEElm = null;
					}

				}

				List<Element> tERMINALNdList = XMLUtil.getChildElements(g_1Elm, "TERMINAL");
				for (Element tERMINALElm : tERMINALNdList) {

					contOfTERMINALElm = tERMINALElm.getTextContent().replaceAll("\\s+", "");
					if (contOfTERMINALElm.isEmpty()) {
						contOfTERMINALElm = null;
					}

				}

				List<Element> bOARDING_AREANdList = XMLUtil.getChildElements(g_1Elm, "BOARDING_AREA");
				for (Element bOARDING_AREAElm : bOARDING_AREANdList) {

					contOfBOARDING_AREAElm = bOARDING_AREAElm.getTextContent().replaceAll("\\s+", "");
					if (contOfBOARDING_AREAElm.isEmpty()) {
						contOfBOARDING_AREAElm = null;
					}

				}

				List<Element> cURRENT_COUNTNdList = XMLUtil.getChildElements(g_1Elm, "CURRENT_COUNT");
				for (Element cURRENT_COUNTElm : cURRENT_COUNTNdList) {

					contOfCURRENT_COUNTElm = cURRENT_COUNTElm.getTextContent().replaceAll("\\s+", "");
					if (contOfCURRENT_COUNTElm.isEmpty()) {
						contOfCURRENT_COUNTElm = null;
					}

				}

				List<Element> pREVIOUS_COUNTNdList = XMLUtil.getChildElements(g_1Elm, "PREVIOUS_COUNT");
				for (Element pREVIOUS_COUNTElm : pREVIOUS_COUNTNdList) {

					contOfPREVIOUS_COUNTElm = pREVIOUS_COUNTElm.getTextContent().replaceAll("\\s+", "");
					if (contOfPREVIOUS_COUNTElm.isEmpty()) {
						contOfPREVIOUS_COUNTElm = null;
					}

				}

				List<Element> cHANGE_COUNTNdList = XMLUtil.getChildElements(g_1Elm, "CHANGE_COUNT");
				for (Element cHANGE_COUNTElm : cHANGE_COUNTNdList) {

					contOfCHANGE_COUNTElm = cHANGE_COUNTElm.getTextContent().replaceAll("\\s+", "");
					if (contOfCHANGE_COUNTElm.isEmpty()) {
						contOfCHANGE_COUNTElm = null;
					}

				}

				List<Element> pERCENT_CHANGENdList = XMLUtil.getChildElements(g_1Elm, "PERCENT_CHANGE");
				for (Element pERCENT_CHANGEElm : pERCENT_CHANGENdList) {

					contOfPERCENT_CHANGEElm = pERCENT_CHANGEElm.getTextContent().replaceAll("\\s+", "");
					if (contOfPERCENT_CHANGEElm.isEmpty()) {
						contOfPERCENT_CHANGEElm = null;
					}

				}

				// g1Type1 Values in CSV
				g1Builder.append(contOfIDElm).append("|").append(contOfYEARElm).append("|").append(contOfMONTHElm)
						.append("|").append(contOfOPERATING_AIRLINEElm).append("|")
						.append(contOfOPERATING_AIRLINE_IATA_CODEElm).append("|").append(contOfGEO_SUMMARYElm)
						.append("|").append(contOfGEO_REGIONElm).append("|").append(contOfACTIVITY_TYPE_CODEElm)
						.append("|").append(contOfPRICE_CATEGORY_CODEElm).append("|").append(contOfTERMINALElm)
						.append("|").append(contOfBOARDING_AREAElm).append("|").append(contOfCURRENT_COUNTElm)
						.append("|").append(contOfPREVIOUS_COUNTElm).append("|").append(contOfCHANGE_COUNTElm)
						.append("|").append(contOfPERCENT_CHANGEElm).append("|").append(";\n");
			}

			String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" + "ID VARCHAR(100), " + "YEAR VARCHAR(100), "
					+ "MONTH VARCHAR(100), " + "OPERATING_AIRLINE VARCHAR(100), "
					+ "OPERATING_AIRLINE_IATA_CODE VARCHAR(100), " + "GEO_SUMMARY VARCHAR(100), "
					+ "GEO_REGION VARCHAR(100), " + "ACTIVITY_TYPE_CODE VARCHAR(100), "
					+ "PRICE_CATEGORY_CODE VARCHAR(100), " + "TERMINAL VARCHAR(100), " + "BOARDING_AREA VARCHAR(100), "
					+ "CURRENT_COUNT VARCHAR(100), " + "PREVIOUS_COUNT VARCHAR(100), " + "CHANGE_COUNT VARCHAR(100), "
					+ "PERCENT_CHANGE VARCHAR(100) " + ")";

			try (PreparedStatement g1Stmt = databaseConnection().prepareStatement(g1TableQuery)) {
				g1Stmt.executeUpdate();
			}

		}

		// G1_Type2
		Element p_CURRElm = XMLUtil.getChildElement(dATA_DSElm, "P_CURR");
		String contOfP_CURRElm = p_CURRElm.getTextContent();
		if (contOfP_CURRElm.isEmpty()) {
			contOfP_CURRElm = null;
		}

		Element p_YEARElm = XMLUtil.getChildElement(dATA_DSElm, "P_YEAR");
		String contOfP_YEARElm = p_YEARElm.getTextContent();
		if (contOfP_YEARElm.isEmpty()) {
			contOfP_YEARElm = null;
		}

		Element p_COMPANYElm = XMLUtil.getChildElement(dATA_DSElm, "P_COMPANY");
		String contOfP_COMPANYElm = p_COMPANYElm.getTextContent();
		if (contOfP_COMPANYElm.isEmpty()) {
			contOfP_COMPANYElm = null;
		}

		Element p_ORGElm = XMLUtil.getChildElement(dATA_DSElm, "P_ORG");
		String contOfP_ORGElm = p_ORGElm.getTextContent();
		if (contOfP_ORGElm.isEmpty()) {
			contOfP_ORGElm = null;
		}

		List<Element> g_1NdList2 = XMLUtil.getChildElements(dATA_DSElm, "G_1");

		// NodeList nodeList = doc.getElementsByTagName("REVENUE");
		// nodeList = doc.getElementsByTagName("REVENUE").getLength();
		if (doc.getElementsByTagName("USD_REVENUE").getLength() > 0) {

			// StringBuilder g1Type2Builder = new StringBuilder();
			// Creating Column names for g1Type2 CSV file
			g1Builder.append("P_CURR").append("|").append("P_YEAR").append("|").append("P_COMPANY").append("|")
					.append("P_ORG").append("|").append("COMPANY").append("|").append("ORGANIZATION").append("|")
					.append("DEPARTMENT").append("|").append("OFFICE").append("|").append("PER_NAME_YEAR").append("|")
					.append("PER_NAME_QTR").append("|").append("USD_REVENUE").append("|").append(";\n");

			for (Element g_1Elm : g_1NdList2) {

				// NodeList nodeList = doc.getElementsByTagName("REVENUE");
				// if(nodeList.getLength() > 0) {

				// String isMulti = g_1Elm.getAttribute("IsMulti");
				String contOfREVENUEElm = null;
				String contOfTARGET_REVENUEElm = null;
				String contOfCOMPANYElm = null;
				String contOfORGANIZATIONElm = null;
				String contOfDEPARTMENTElm = null;
				String contOfOFFICEElm = null;
				String contOfBRANDElm = null;
				String contOfPER_NAME_YEARElm = null;
				String contOfPER_NAME_QTRElm = null;
				String contOfUSD_REVENUEElm = null;
				String contOfLOBElm = null;

				List<Element> rEVENUENdList = XMLUtil.getChildElements(g_1Elm, "REVENUE");
				for (Element rEVENUEElm : rEVENUENdList) {

					contOfREVENUEElm = rEVENUEElm.getTextContent();
					if (contOfREVENUEElm.isEmpty()) {
						contOfREVENUEElm = null;
					}
				}
				List<Element> tARGET_REVENUENdList = XMLUtil.getChildElements(g_1Elm, "TARGET_REVENUE");
				for (Element tARGET_REVENUEElm : tARGET_REVENUENdList) {

					contOfTARGET_REVENUEElm = tARGET_REVENUEElm.getTextContent();
					if (contOfTARGET_REVENUEElm.isEmpty()) {
						contOfTARGET_REVENUEElm = null;
					}

				}
				List<Element> cOMPANYNdList = XMLUtil.getChildElements(g_1Elm, "COMPANY");
				for (Element cOMPANYElm : cOMPANYNdList) {

					contOfCOMPANYElm = cOMPANYElm.getTextContent();
					if (contOfCOMPANYElm.isEmpty()) {
						contOfCOMPANYElm = null;
					}

				}
				List<Element> oRGANIZATIONNdList = XMLUtil.getChildElements(g_1Elm, "ORGANIZATION");
				for (Element oRGANIZATIONElm : oRGANIZATIONNdList) {

					contOfORGANIZATIONElm = oRGANIZATIONElm.getTextContent();
					if (contOfORGANIZATIONElm.isEmpty()) {
						contOfORGANIZATIONElm = null;
					}

				}
				List<Element> dEPARTMENTNdList = XMLUtil.getChildElements(g_1Elm, "DEPARTMENT");
				for (Element dEPARTMENTElm : dEPARTMENTNdList) {

					contOfDEPARTMENTElm = dEPARTMENTElm.getTextContent();
					if (contOfDEPARTMENTElm.isEmpty()) {
						contOfDEPARTMENTElm = null;
					}

				}
				List<Element> oFFICENdList = XMLUtil.getChildElements(g_1Elm, "OFFICE");
				for (Element oFFICEElm : oFFICENdList) {

					contOfOFFICEElm = oFFICEElm.getTextContent();
					if (contOfOFFICEElm.isEmpty()) {
						contOfOFFICEElm = null;
					}

				}
				List<Element> bRANDNdList = XMLUtil.getChildElements(g_1Elm, "BRAND");
				for (Element bRANDElm : bRANDNdList) {

					contOfBRANDElm = bRANDElm.getTextContent();
					if (contOfBRANDElm.isEmpty()) {
						contOfBRANDElm = null;
					}

				}
				List<Element> pER_NAME_YEARNdList = XMLUtil.getChildElements(g_1Elm, "PER_NAME_YEAR");
				for (Element pER_NAME_YEARElm : pER_NAME_YEARNdList) {

					contOfPER_NAME_YEARElm = pER_NAME_YEARElm.getTextContent();
					if (contOfPER_NAME_YEARElm.isEmpty()) {
						contOfPER_NAME_YEARElm = null;
					}

				}
				List<Element> pER_NAME_QTRNdList = XMLUtil.getChildElements(g_1Elm, "PER_NAME_QTR");
				for (Element pER_NAME_QTRElm : pER_NAME_QTRNdList) {

					contOfPER_NAME_QTRElm = pER_NAME_QTRElm.getTextContent();
					if (contOfPER_NAME_QTRElm.isEmpty()) {
						contOfPER_NAME_QTRElm = null;
					}

				}
				List<Element> uSD_REVENUENdList = XMLUtil.getChildElements(g_1Elm, "USD_REVENUE");
				for (Element uSD_REVENUEElm : uSD_REVENUENdList) {

					contOfUSD_REVENUEElm = uSD_REVENUEElm.getTextContent();
					if (contOfUSD_REVENUEElm.isEmpty()) {
						contOfUSD_REVENUEElm = null;
					}

				}
				List<Element> lOBNdList = XMLUtil.getChildElements(g_1Elm, "LOB");
				for (Element lOBElm : lOBNdList) {

					contOfLOBElm = lOBElm.getTextContent();
					if (contOfLOBElm.isEmpty()) {
						contOfLOBElm = null;
					}

				}

				// g1Type2 Values in CSV
				g1Builder.append(contOfP_CURRElm).append("|").append(contOfP_YEARElm).append("|")
						.append(contOfP_COMPANYElm).append("|").append(contOfP_ORGElm).append("|")
						.append(contOfCOMPANYElm).append("|").append(contOfORGANIZATIONElm).append("|")
						.append(contOfDEPARTMENTElm).append("|").append(contOfOFFICEElm).append("|")
						.append(contOfPER_NAME_YEARElm).append("|").append(contOfPER_NAME_QTRElm).append("|")
						.append(contOfUSD_REVENUEElm).append("|").append(";\n");

			}

			String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" + "P_CURR VARCHAR(100), " + "P_YEAR VARCHAR(100), "
					+ "P_COMPANY VARCHAR(100), " + "P_ORG VARCHAR(100), " + "COMPANY VARCHAR(100), "
					+ "ORGANIZATION VARCHAR(100), " + "DEPARTMENT VARCHAR(100), " + "OFFICE VARCHAR(100), "
					+ "PER_NAME_YEAR VARCHAR(100), " + "PER_NAME_QTR VARCHAR(100), " + "USD_REVENUE VARCHAR(100) "
					+ ")";

			try (PreparedStatement g1Stmt = databaseConnection().prepareStatement(g1TableQuery)) {
				g1Stmt.executeUpdate();
			}

		}

		if (doc.getElementsByTagName("TARGET_REVENUE").getLength() > 0) {

			// StringBuilder g1Type2Builder = new StringBuilder();
			// Creating Column names for g1Type2 CSV file
			g1Builder.append("P_YEAR").append("|").append("P_COMPANY").append("|").append("P_ORG").append("|")
					.append("REVENUE").append("|").append("TARGET_REVENUE").append("|").append("COMPANY").append("|")
					.append("ORGANIZATION").append("|").append("DEPARTMENT").append("|").append("OFFICE").append("|")
					.append("BRAND").append("|").append("PER_NAME_YEAR").append("|").append("PER_NAME_QTR").append("|")
					.append("LOB").append("|").append(";\n");

			for (Element g_1Elm : g_1NdList2) {

				// NodeList nodeList = doc.getElementsByTagName("REVENUE");
				// if(nodeList.getLength() > 0) {

				// String isMulti = g_1Elm.getAttribute("IsMulti");
				String contOfREVENUEElm = null;
				String contOfTARGET_REVENUEElm = null;
				String contOfCOMPANYElm = null;
				String contOfORGANIZATIONElm = null;
				String contOfDEPARTMENTElm = null;
				String contOfOFFICEElm = null;
				String contOfBRANDElm = null;
				String contOfPER_NAME_YEARElm = null;
				String contOfPER_NAME_QTRElm = null;
				String contOfUSD_REVENUEElm = null;
				String contOfLOBElm = null;

				List<Element> rEVENUENdList = XMLUtil.getChildElements(g_1Elm, "REVENUE");
				for (Element rEVENUEElm : rEVENUENdList) {

					contOfREVENUEElm = rEVENUEElm.getTextContent();
					if (contOfREVENUEElm.isEmpty()) {
						contOfREVENUEElm = null;
					}
				}
				List<Element> tARGET_REVENUENdList = XMLUtil.getChildElements(g_1Elm, "TARGET_REVENUE");
				for (Element tARGET_REVENUEElm : tARGET_REVENUENdList) {

					contOfTARGET_REVENUEElm = tARGET_REVENUEElm.getTextContent();
					if (contOfTARGET_REVENUEElm.isEmpty()) {
						contOfTARGET_REVENUEElm = null;
					}

				}
				List<Element> cOMPANYNdList = XMLUtil.getChildElements(g_1Elm, "COMPANY");
				for (Element cOMPANYElm : cOMPANYNdList) {

					contOfCOMPANYElm = cOMPANYElm.getTextContent();
					if (contOfCOMPANYElm.isEmpty()) {
						contOfCOMPANYElm = null;
					}

				}
				List<Element> oRGANIZATIONNdList = XMLUtil.getChildElements(g_1Elm, "ORGANIZATION");
				for (Element oRGANIZATIONElm : oRGANIZATIONNdList) {

					contOfORGANIZATIONElm = oRGANIZATIONElm.getTextContent();
					if (contOfORGANIZATIONElm.isEmpty()) {
						contOfORGANIZATIONElm = null;
					}

				}
				List<Element> dEPARTMENTNdList = XMLUtil.getChildElements(g_1Elm, "DEPARTMENT");
				for (Element dEPARTMENTElm : dEPARTMENTNdList) {

					contOfDEPARTMENTElm = dEPARTMENTElm.getTextContent();
					if (contOfDEPARTMENTElm.isEmpty()) {
						contOfDEPARTMENTElm = null;
					}

				}
				List<Element> oFFICENdList = XMLUtil.getChildElements(g_1Elm, "OFFICE");
				for (Element oFFICEElm : oFFICENdList) {

					contOfOFFICEElm = oFFICEElm.getTextContent();
					if (contOfOFFICEElm.isEmpty()) {
						contOfOFFICEElm = null;
					}

				}
				List<Element> bRANDNdList = XMLUtil.getChildElements(g_1Elm, "BRAND");
				for (Element bRANDElm : bRANDNdList) {

					contOfBRANDElm = bRANDElm.getTextContent();
					if (contOfBRANDElm.isEmpty()) {
						contOfBRANDElm = null;
					}

				}
				List<Element> pER_NAME_YEARNdList = XMLUtil.getChildElements(g_1Elm, "PER_NAME_YEAR");
				for (Element pER_NAME_YEARElm : pER_NAME_YEARNdList) {

					contOfPER_NAME_YEARElm = pER_NAME_YEARElm.getTextContent();
					if (contOfPER_NAME_YEARElm.isEmpty()) {
						contOfPER_NAME_YEARElm = null;
					}

				}
				List<Element> pER_NAME_QTRNdList = XMLUtil.getChildElements(g_1Elm, "PER_NAME_QTR");
				for (Element pER_NAME_QTRElm : pER_NAME_QTRNdList) {

					contOfPER_NAME_QTRElm = pER_NAME_QTRElm.getTextContent();
					if (contOfPER_NAME_QTRElm.isEmpty()) {
						contOfPER_NAME_QTRElm = null;
					}

				}
				List<Element> uSD_REVENUENdList = XMLUtil.getChildElements(g_1Elm, "USD_REVENUE");
				for (Element uSD_REVENUEElm : uSD_REVENUENdList) {

					contOfUSD_REVENUEElm = uSD_REVENUEElm.getTextContent();
					if (contOfUSD_REVENUEElm.isEmpty()) {
						contOfUSD_REVENUEElm = null;
					}

				}
				List<Element> lOBNdList = XMLUtil.getChildElements(g_1Elm, "LOB");
				for (Element lOBElm : lOBNdList) {

					contOfLOBElm = lOBElm.getTextContent();
					if (contOfLOBElm.isEmpty()) {
						contOfLOBElm = null;
					}

				}

				// g1Type2 Values in CSV
				g1Builder.append(contOfP_YEARElm).append("|").append(contOfP_COMPANYElm).append("|")
						.append(contOfP_ORGElm).append("|").append(contOfREVENUEElm).append("|")
						.append(contOfTARGET_REVENUEElm).append("|").append(contOfCOMPANYElm).append("|")
						.append(contOfORGANIZATIONElm).append("|").append(contOfDEPARTMENTElm).append("|")
						.append(contOfOFFICEElm).append("|").append(contOfBRANDElm).append("|")
						.append(contOfPER_NAME_YEARElm).append("|").append(contOfPER_NAME_QTRElm).append("|")
						.append(contOfLOBElm).append("|").append(";\n");

			}

			String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" + "P_YEAR VARCHAR(100), "
					+ "P_COMPANY VARCHAR(100), " + "P_ORG VARCHAR(100), " + "REVENUE VARCHAR(100), "
					+ "TARGET_REVENUE VARCHAR(100), " + "COMPANY VARCHAR(100), " + "ORGANIZATION VARCHAR(100), "
					+ "DEPARTMENT VARCHAR(100), " + "OFFICE VARCHAR(100), " + "BRAND VARCHAR(100), "
					+ "PER_NAME_YEAR VARCHAR(100), " + "PER_NAME_QTR VARCHAR(100), " + "LOB VARCHAR(100) " + ")";

			try (PreparedStatement g1Stmt = databaseConnection().prepareStatement(g1TableQuery)) {
				g1Stmt.executeUpdate();
			}
		}

		// if((doc.getElementsByTagName("BRAND").getLength() > 0) &&
		// (doc.getElementsByTagName("TARGET_REVENUE").getLength() < 0)) {

		Element g_1Elm = XMLUtil.getChildElement(dATA_DSElm, "G_1");

		if ((g_1Elm.getElementsByTagName("BRAND").getLength() > 0)
				&& (g_1Elm.getElementsByTagName("TARGET_REVENUE").getLength() == 0)) {

			g1Builder.append("BRAND").append("|").append("PRODUCT").append("|").append("LOB").append("|")
					.append("BILLED_QUANTITY").append("|").append("PRODUCT_TYPE").append("|").append("CALENDAR_DATE")
					.append("|").append("REVENUE").append("|").append("BRAND_REVENUE_TOTAL").append("|")
					.append("GRAND_TOTAL_REVENUE").append("|").append(";\n");

			Element bRANDElm = XMLUtil.getChildElement(g_1Elm, "BRAND");
			String contOfBRANDElm = bRANDElm.getTextContent();
			if (contOfBRANDElm.isEmpty()) {
				contOfBRANDElm = null;
			}

			String contOfPRODUCTElm = null;
			String contOfLOBElm = null;
			String contOfBILLED_QUANTITYElm = null;
			String contOfPRODUCT_TYPEElm = null;
			String contOfCALENDAR_DATEElm = null;
			String contOfREVENUEElm = null;
			String contOfBRAND_REVENUE_TOTALElm = null;
			String contOfGRAND_TOTAL_REVENUEElm = null;

			List<Element> g_2NdList = XMLUtil.getChildElements(g_1Elm, "G_2");
			for (Element g_2Elm : g_2NdList) {

				// String isMulti = g_2Elm.getAttribute("IsMulti");

				List<Element> pRODUCTNdList = XMLUtil.getChildElements(g_2Elm, "PRODUCT");
				for (Element pRODUCTElm : pRODUCTNdList) {

					contOfPRODUCTElm = pRODUCTElm.getTextContent();
					if (contOfPRODUCTElm.isEmpty()) {
						contOfPRODUCTElm = null;
					}
				}
				List<Element> lOBNdList = XMLUtil.getChildElements(g_2Elm, "LOB");
				for (Element lOBElm : lOBNdList) {

					contOfLOBElm = lOBElm.getTextContent();
					if (contOfLOBElm.isEmpty()) {
						contOfLOBElm = null;
					}

				}
				List<Element> bILLED_QUANTITYNdList = XMLUtil.getChildElements(g_2Elm, "BILLED_QUANTITY");
				for (Element bILLED_QUANTITYElm : bILLED_QUANTITYNdList) {

					contOfBILLED_QUANTITYElm = bILLED_QUANTITYElm.getTextContent();
					if (contOfBILLED_QUANTITYElm.isEmpty()) {
						contOfBILLED_QUANTITYElm = null;
					}
				}
				List<Element> pRODUCT_TYPENdList = XMLUtil.getChildElements(g_2Elm, "PRODUCT_TYPE");
				for (Element pRODUCT_TYPEElm : pRODUCT_TYPENdList) {

					contOfPRODUCT_TYPEElm = pRODUCT_TYPEElm.getTextContent();
					if (contOfPRODUCT_TYPEElm.isEmpty()) {
						contOfPRODUCT_TYPEElm = null;
					}
				}
				List<Element> cALENDAR_DATENdList = XMLUtil.getChildElements(g_2Elm, "CALENDAR_DATE");
				for (Element cALENDAR_DATEElm : cALENDAR_DATENdList) {

					contOfCALENDAR_DATEElm = cALENDAR_DATEElm.getTextContent();
					if (contOfCALENDAR_DATEElm.isEmpty()) {
						contOfCALENDAR_DATEElm = null;
					}
				}
				List<Element> rEVENUENdList = XMLUtil.getChildElements(g_2Elm, "REVENUE");
				for (Element rEVENUEElm : rEVENUENdList) {

					contOfREVENUEElm = rEVENUEElm.getTextContent();
					if (contOfREVENUEElm.isEmpty()) {
						contOfREVENUEElm = null;
					}
				}

				// contOfBRAND_REVENUE_TOTALElm = null;
				// contOfGRAND_TOTAL_REVENUEElm = null;

				g1Builder.append(contOfBRANDElm).append("|").append(contOfPRODUCTElm).append("|").append(contOfLOBElm)
						.append("|").append(contOfBILLED_QUANTITYElm).append("|").append(contOfPRODUCT_TYPEElm)
						.append("|").append(contOfCALENDAR_DATEElm).append("|").append(contOfREVENUEElm).append("|")
						.append(contOfBRAND_REVENUE_TOTALElm).append("|").append(contOfGRAND_TOTAL_REVENUEElm)
						.append("|").append(";\n");

			}
			Element bRAND_REVENUE_TOTALElm = XMLUtil.getChildElement(g_1Elm, "BRAND_REVENUE_TOTAL");
			contOfBRAND_REVENUE_TOTALElm = bRAND_REVENUE_TOTALElm.getTextContent();
			if (contOfBRAND_REVENUE_TOTALElm.isEmpty()) {
				contOfBRAND_REVENUE_TOTALElm = null;
			}

			Element gRAND_TOTAL_REVENUEElm = XMLUtil.getChildElement(dATA_DSElm, "GRAND_TOTAL_REVENUE");
			contOfGRAND_TOTAL_REVENUEElm = gRAND_TOTAL_REVENUEElm.getTextContent();
			if (contOfGRAND_TOTAL_REVENUEElm.isEmpty()) {
				contOfGRAND_TOTAL_REVENUEElm = null;
			}

			contOfPRODUCTElm = null;
			contOfLOBElm = null;
			contOfBILLED_QUANTITYElm = null;
			contOfPRODUCT_TYPEElm = null;
			contOfCALENDAR_DATEElm = null;
			contOfREVENUEElm = null;

			g1Builder.append(contOfBRANDElm).append("|").append(contOfPRODUCTElm).append("|").append(contOfLOBElm)
					.append("|").append(contOfBILLED_QUANTITYElm).append("|").append(contOfPRODUCT_TYPEElm).append("|")
					.append(contOfCALENDAR_DATEElm).append("|").append(contOfREVENUEElm).append("|")
					.append(contOfBRAND_REVENUE_TOTALElm).append("|").append(contOfGRAND_TOTAL_REVENUEElm).append("|")
					.append(";\n");

			String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" + "BRAND VARCHAR(100), " + "PRODUCT VARCHAR(100), "
					+ "LOB VARCHAR(100), " + "BILLED_QUANTITY VARCHAR(100), " + "PRODUCT_TYPE VARCHAR(100), "
					+ "CALENDAR_DATE VARCHAR(100), " + "REVENUE VARCHAR(100), " + "BRAND_REVENUE_TOTAL VARCHAR(100), "
					+ "GRAND_TOTAL_REVENUE VARCHAR(100) " + ")";

			try (PreparedStatement g1Stmt = databaseConnection().prepareStatement(g1TableQuery)) {
				g1Stmt.executeUpdate();
			}

		}

		Element p_CUSTIDElm = XMLUtil.getChildElement(dATA_DSElm, "P_CUSTID");
		String contOfP_CUSTIDElm = p_CUSTIDElm.getTextContent();

		Element p_ORDIDElm = XMLUtil.getChildElement(dATA_DSElm, "P_ORDID");
		String contOfP_ORDIDElm = p_ORDIDElm.getTextContent();

		List<Element> g_1NdList3 = XMLUtil.getChildElements(dATA_DSElm, "G_1");

		if ((doc.getElementsByTagName("CUSTOMER_NAME").getLength() > 0)
				&& (doc.getElementsByTagName("CUSTOMER_ID").getLength() > 0)) {

			g1Builder.append("P_CUSTID").append("|").append("P_ORDID").append("|").append("CUSTOMER_NAME").append("|")
					.append("CUSTOMER_ID").append("|").append("STREET_ADDRESS").append("|").append("CITY").append("|")
					.append("STATE_PROVINCE").append("|").append("POSTAL_CODE").append("|").append("COUNTRY_NAME")
					.append("|").append("PRIMARY_PHONE_NUMBER").append("|").append("CUST_EMAIL").append("|")
					.append("G2_CUSTOMER_ID").append("|").append("ORDER_ID").append("|").append("ORDER_MODE")
					.append("|").append("ORDER_DATE").append("|").append("LINE_ITEM_ID").append("|")
					.append("UNIT_PRICE").append("|").append("QUANTITY").append("|").append("PRODUCT_NAME").append("|")
					.append("PRODUCT_DESCRIPTION").append("|").append("ORDER_STATUS").append("|").append("ORDER_TOTAL")
					.append("|").append("LINE_TOTAL").append("|").append("FRMTD_ORDER_DATE").append("|")
					.append("CITY_STATE_ZIP").append("|").append("CUST_TOTAL").append("|").append(";\n");

			// G1 Elements
			String contOfCUSTOMER_NAMEElm = null;
			String contOfCUSTOMER_IDElm = null;
			String contOfSTREET_ADDRESSElm = null;
			String contOfCITYElm = null;
			String contOfSTATE_PROVINCEElm = null;
			String contOfPOSTAL_CODEElm = null;
			String contOfCOUNTRY_NAMEElm = null;
			String contOfPRIMARY_PHONE_NUMBERElm = null;
			String contOfCUST_EMAILElm = null;

			// G2 Elements
			String contOfcUSTOMER_ID_G2Elm = null;
			String contOfoRDER_IDElm = null;
			String contOfoRDER_MODEElm = null;
			String contOfoRDER_DATEElm = null;
			String contOflINE_ITEM_IDElm = null;
			String contOfuNIT_PRICEElm = null;
			String contOfqUANTITYElm = null;
			String contOfpRODUCT_NAMEElm = null;
			String contOfpRODUCT_DESCRIPTIONElm = null;
			String contOfoRDER_STATUSElm = null;
			String contOfoRDER_TOTALElm = null;
			String contOflINE_TOTALElm = null;
			String contOffRMTD_ORDER_DATEElm = null;

			String contOfCITY_STATE_ZIPElm = null;
			String contOfCUST_TOTALElm = null;

			g1Builder.append(contOfP_CUSTIDElm).append("|").append(contOfP_ORDIDElm).append("|")
					.append(contOfCUSTOMER_NAMEElm).append("|").append(contOfCUSTOMER_IDElm).append("|")
					.append(contOfSTREET_ADDRESSElm).append("|").append(contOfCITYElm).append("|")
					.append(contOfSTATE_PROVINCEElm).append("|").append(contOfPOSTAL_CODEElm).append("|")
					.append(contOfCOUNTRY_NAMEElm).append("|").append(contOfPRIMARY_PHONE_NUMBERElm).append("|")
					.append(contOfCUST_EMAILElm).append("|").append(contOfcUSTOMER_ID_G2Elm).append("|")
					.append(contOfoRDER_IDElm).append("|").append(contOfoRDER_MODEElm).append("|")
					.append(contOfoRDER_DATEElm).append("|").append(contOflINE_ITEM_IDElm).append("|")
					.append(contOfuNIT_PRICEElm).append("|").append(contOfqUANTITYElm).append("|")
					.append(contOfpRODUCT_NAMEElm).append("|").append(contOfpRODUCT_DESCRIPTIONElm).append("|")
					.append(contOfoRDER_STATUSElm).append("|").append(contOfoRDER_TOTALElm).append("|")
					.append(contOflINE_TOTALElm).append("|").append(contOffRMTD_ORDER_DATEElm).append("|")
					.append(contOfCITY_STATE_ZIPElm).append("|").append(contOfCUST_TOTALElm).append("|").append(";\n");

			for (Element g_1Elm2 : g_1NdList3) {

				// String isMulti = g_1Elm2.getAttribute("IsMulti");

				List<Element> cUSTOMER_NAMENdList = XMLUtil.getChildElements(g_1Elm2, "CUSTOMER_NAME");
				for (Element cUSTOMER_NAMEElm : cUSTOMER_NAMENdList) {
					contOfCUSTOMER_NAMEElm = cUSTOMER_NAMEElm.getTextContent();
					if (contOfCUSTOMER_NAMEElm.isEmpty()) {
						contOfCUSTOMER_NAMEElm = null;
					}
				}
				List<Element> cUSTOMER_IDNdList = XMLUtil.getChildElements(g_1Elm2, "CUSTOMER_ID");
				for (Element cUSTOMER_IDElm : cUSTOMER_IDNdList) {
					contOfCUSTOMER_IDElm = cUSTOMER_IDElm.getTextContent();
					if (contOfCUSTOMER_IDElm.isEmpty()) {
						contOfCUSTOMER_IDElm = null;
					}
				}
				List<Element> sTREET_ADDRESSNdList = XMLUtil.getChildElements(g_1Elm2, "STREET_ADDRESS");
				for (Element sTREET_ADDRESSElm : sTREET_ADDRESSNdList) {
					contOfSTREET_ADDRESSElm = sTREET_ADDRESSElm.getTextContent();
					if (contOfSTREET_ADDRESSElm.isEmpty()) {
						contOfSTREET_ADDRESSElm = null;
					}
				}
				List<Element> cITYNdList = XMLUtil.getChildElements(g_1Elm2, "CITY");
				for (Element cITYElm : cITYNdList) {
					contOfCITYElm = cITYElm.getTextContent();
					if (contOfCITYElm.isEmpty()) {
						contOfCITYElm = null;
					}
				}
				List<Element> sTATE_PROVINCENdList = XMLUtil.getChildElements(g_1Elm2, "STATE_PROVINCE");
				for (Element sTATE_PROVINCEElm : sTATE_PROVINCENdList) {
					contOfSTATE_PROVINCEElm = sTATE_PROVINCEElm.getTextContent();
					if (contOfSTATE_PROVINCEElm.isEmpty()) {
						contOfSTATE_PROVINCEElm = null;
					}
				}
				List<Element> pOSTAL_CODENdList = XMLUtil.getChildElements(g_1Elm2, "POSTAL_CODE");
				for (Element pOSTAL_CODEElm : pOSTAL_CODENdList) {

					contOfPOSTAL_CODEElm = pOSTAL_CODEElm.getTextContent();
					if (contOfPOSTAL_CODEElm.isEmpty()) {
						contOfPOSTAL_CODEElm = null;
					}

				}
				List<Element> cOUNTRY_NAMENdList = XMLUtil.getChildElements(g_1Elm2, "COUNTRY_NAME");
				for (Element cOUNTRY_NAMEElm : cOUNTRY_NAMENdList) {

					contOfCOUNTRY_NAMEElm = cOUNTRY_NAMEElm.getTextContent();
					if (contOfCOUNTRY_NAMEElm.isEmpty()) {
						contOfCOUNTRY_NAMEElm = null;
					}

				}
				List<Element> pRIMARY_PHONE_NUMBERNdList = XMLUtil.getChildElements(g_1Elm2, "PRIMARY_PHONE_NUMBER");
				for (Element pRIMARY_PHONE_NUMBERElm : pRIMARY_PHONE_NUMBERNdList) {

					contOfPRIMARY_PHONE_NUMBERElm = pRIMARY_PHONE_NUMBERElm.getTextContent();
					if (contOfPRIMARY_PHONE_NUMBERElm.isEmpty()) {
						contOfPRIMARY_PHONE_NUMBERElm = null;
					}

				}
				List<Element> cUST_EMAILNdList = XMLUtil.getChildElements(g_1Elm2, "CUST_EMAIL");
				for (Element cUST_EMAILElm : cUST_EMAILNdList) {

					contOfCUST_EMAILElm = cUST_EMAILElm.getTextContent();
					if (contOfCUST_EMAILElm.isEmpty()) {
						contOfCUST_EMAILElm = null;
					}

				}

				List<Element> g_2NdList = XMLUtil.getChildElements(g_1Elm2, "G_2");

				for (Element g_2Elm : g_2NdList) {

					// String isMulti = g_2Elm.getAttribute("IsMulti");

					List<Element> cUSTOMER_IDNdList2 = XMLUtil.getChildElements(g_2Elm, "CUSTOMER_ID");
					for (Element cUSTOMER_ID_G2Elm : cUSTOMER_IDNdList2) {

						contOfcUSTOMER_ID_G2Elm = cUSTOMER_ID_G2Elm.getTextContent();
						if (contOfcUSTOMER_ID_G2Elm.isEmpty()) {
							contOfcUSTOMER_ID_G2Elm = null;
						}
					}
					List<Element> oRDER_IDNdList = XMLUtil.getChildElements(g_2Elm, "ORDER_ID");
					for (Element oRDER_IDElm : oRDER_IDNdList) {

						contOfoRDER_IDElm = oRDER_IDElm.getTextContent();
						if (contOfoRDER_IDElm.isEmpty()) {
							contOfoRDER_IDElm = null;
						}

					}
					List<Element> oRDER_MODENdList = XMLUtil.getChildElements(g_2Elm, "ORDER_MODE");
					for (Element oRDER_MODEElm : oRDER_MODENdList) {

						contOfoRDER_MODEElm = oRDER_MODEElm.getTextContent();
						if (contOfoRDER_MODEElm.isEmpty()) {
							contOfoRDER_MODEElm = null;
						}

					}
					List<Element> oRDER_DATENdList = XMLUtil.getChildElements(g_2Elm, "ORDER_DATE");
					for (Element oRDER_DATEElm : oRDER_DATENdList) {

						contOfoRDER_DATEElm = oRDER_DATEElm.getTextContent();
						if (contOfoRDER_DATEElm.isEmpty()) {
							contOfoRDER_DATEElm = null;
						}

					}
					List<Element> lINE_ITEM_IDNdList = XMLUtil.getChildElements(g_2Elm, "LINE_ITEM_ID");
					for (Element lINE_ITEM_IDElm : lINE_ITEM_IDNdList) {

						contOflINE_ITEM_IDElm = lINE_ITEM_IDElm.getTextContent();
						if (contOflINE_ITEM_IDElm.isEmpty()) {
							contOflINE_ITEM_IDElm = null;
						}

					}
					List<Element> uNIT_PRICENdList = XMLUtil.getChildElements(g_2Elm, "UNIT_PRICE");
					for (Element uNIT_PRICEElm : uNIT_PRICENdList) {

						contOfuNIT_PRICEElm = uNIT_PRICEElm.getTextContent();
						if (contOfuNIT_PRICEElm.isEmpty()) {
							contOfuNIT_PRICEElm = null;
						}

					}
					List<Element> qUANTITYNdList = XMLUtil.getChildElements(g_2Elm, "QUANTITY");
					for (Element qUANTITYElm : qUANTITYNdList) {

						contOfqUANTITYElm = qUANTITYElm.getTextContent();
						if (contOfqUANTITYElm.isEmpty()) {
							contOfqUANTITYElm = null;
						}

					}
					List<Element> pRODUCT_NAMENdList = XMLUtil.getChildElements(g_2Elm, "PRODUCT_NAME");
					for (Element pRODUCT_NAMEElm : pRODUCT_NAMENdList) {

						contOfpRODUCT_NAMEElm = pRODUCT_NAMEElm.getTextContent();
						if (contOfpRODUCT_NAMEElm.isEmpty()) {
							contOfpRODUCT_NAMEElm = null;
						}

					}
					List<Element> pRODUCT_DESCRIPTIONNdList = XMLUtil.getChildElements(g_2Elm, "PRODUCT_DESCRIPTION");
					for (Element pRODUCT_DESCRIPTIONElm : pRODUCT_DESCRIPTIONNdList) {

						contOfpRODUCT_DESCRIPTIONElm = pRODUCT_DESCRIPTIONElm.getTextContent();
						if (contOfpRODUCT_DESCRIPTIONElm.isEmpty()) {
							contOfpRODUCT_DESCRIPTIONElm = null;
						}

					}
					List<Element> oRDER_STATUSNdList = XMLUtil.getChildElements(g_2Elm, "ORDER_STATUS");
					for (Element oRDER_STATUSElm : oRDER_STATUSNdList) {

						contOfoRDER_STATUSElm = oRDER_STATUSElm.getTextContent();
						if (contOfoRDER_STATUSElm.isEmpty()) {
							contOfoRDER_STATUSElm = null;
						}

					}
					List<Element> oRDER_TOTALNdList = XMLUtil.getChildElements(g_2Elm, "ORDER_TOTAL");
					for (Element oRDER_TOTALElm : oRDER_TOTALNdList) {

						contOfoRDER_TOTALElm = oRDER_TOTALElm.getTextContent();
						if (contOfoRDER_TOTALElm.isEmpty()) {
							contOfoRDER_TOTALElm = null;
						}

					}
					List<Element> lINE_TOTALNdList = XMLUtil.getChildElements(g_2Elm, "LINE_TOTAL");
					for (Element lINE_TOTALElm : lINE_TOTALNdList) {

						contOflINE_TOTALElm = lINE_TOTALElm.getTextContent();
						if (contOflINE_TOTALElm.isEmpty()) {
							contOflINE_TOTALElm = null;
						}

					}
					List<Element> fRMTD_ORDER_DATENdList = XMLUtil.getChildElements(g_2Elm, "FRMTD_ORDER_DATE");
					for (Element fRMTD_ORDER_DATEElm : fRMTD_ORDER_DATENdList) {

						contOffRMTD_ORDER_DATEElm = fRMTD_ORDER_DATEElm.getTextContent();
						if (contOffRMTD_ORDER_DATEElm.isEmpty()) {
							contOffRMTD_ORDER_DATEElm = null;
						}

					}

					contOfP_CUSTIDElm = null;
					contOfP_ORDIDElm = null;

					contOfCITY_STATE_ZIPElm = null;
					contOfCUST_TOTALElm = null;

					g1Builder.append(contOfP_CUSTIDElm).append("|").append(contOfP_ORDIDElm).append("|")
							.append(contOfCUSTOMER_NAMEElm).append("|").append(contOfCUSTOMER_IDElm).append("|")
							.append(contOfSTREET_ADDRESSElm).append("|").append(contOfCITYElm).append("|")
							.append(contOfSTATE_PROVINCEElm).append("|").append(contOfPOSTAL_CODEElm).append("|")
							.append(contOfCOUNTRY_NAMEElm).append("|").append(contOfPRIMARY_PHONE_NUMBERElm).append("|")
							.append(contOfCUST_EMAILElm).append("|").append(contOfcUSTOMER_ID_G2Elm).append("|")
							.append(contOfoRDER_IDElm).append("|").append(contOfoRDER_MODEElm).append("|")
							.append(contOfoRDER_DATEElm).append("|").append(contOflINE_ITEM_IDElm).append("|")
							.append(contOfuNIT_PRICEElm).append("|").append(contOfqUANTITYElm).append("|")
							.append(contOfpRODUCT_NAMEElm).append("|").append(contOfpRODUCT_DESCRIPTIONElm).append("|")
							.append(contOfoRDER_STATUSElm).append("|").append(contOfoRDER_TOTALElm).append("|")
							.append(contOflINE_TOTALElm).append("|").append(contOffRMTD_ORDER_DATEElm).append("|")
							.append(contOfCITY_STATE_ZIPElm).append("|").append(contOfCUST_TOTALElm).append("|")
							.append(";\n");
				}

				List<Element> cITY_STATE_ZIPNdList = XMLUtil.getChildElements(g_1Elm2, "CITY_STATE_ZIP");
				for (Element cITY_STATE_ZIPElm : cITY_STATE_ZIPNdList) {
					contOfCITY_STATE_ZIPElm = cITY_STATE_ZIPElm.getTextContent();
					if (contOfCITY_STATE_ZIPElm.isEmpty()) {
						contOfCITY_STATE_ZIPElm = null;
					}

				}
				List<Element> cUST_TOTALNdList = XMLUtil.getChildElements(g_1Elm2, "CUST_TOTAL");
				for (Element cUST_TOTALElm : cUST_TOTALNdList) {
					contOfCUST_TOTALElm = cUST_TOTALElm.getTextContent();
					if (contOfCUST_TOTALElm.isEmpty()) {
						contOfCUST_TOTALElm = null;
					}
				}

				contOfP_CUSTIDElm = null;
				contOfP_ORDIDElm = null;

				contOfcUSTOMER_ID_G2Elm = null;
				contOfoRDER_IDElm = null;
				contOfoRDER_MODEElm = null;
				contOfoRDER_DATEElm = null;
				contOflINE_ITEM_IDElm = null;
				contOfuNIT_PRICEElm = null;
				contOfqUANTITYElm = null;
				contOfpRODUCT_NAMEElm = null;
				contOfpRODUCT_DESCRIPTIONElm = null;
				contOfoRDER_STATUSElm = null;
				contOfoRDER_TOTALElm = null;
				contOffRMTD_ORDER_DATEElm = null;
				contOflINE_TOTALElm = null;

				g1Builder.append(contOfP_CUSTIDElm).append("|").append(contOfP_ORDIDElm).append("|")
						.append(contOfCUSTOMER_NAMEElm).append("|").append(contOfCUSTOMER_IDElm).append("|")
						.append(contOfSTREET_ADDRESSElm).append("|").append(contOfCITYElm).append("|")
						.append(contOfSTATE_PROVINCEElm).append("|").append(contOfPOSTAL_CODEElm).append("|")
						.append(contOfCOUNTRY_NAMEElm).append("|").append(contOfPRIMARY_PHONE_NUMBERElm).append("|")
						.append(contOfCUST_EMAILElm).append("|").append(contOfcUSTOMER_ID_G2Elm).append("|")
						.append(contOfoRDER_IDElm).append("|").append(contOfoRDER_MODEElm).append("|")
						.append(contOfoRDER_DATEElm).append("|").append(contOflINE_ITEM_IDElm).append("|")
						.append(contOfuNIT_PRICEElm).append("|").append(contOfqUANTITYElm).append("|")
						.append(contOfpRODUCT_NAMEElm).append("|").append(contOfpRODUCT_DESCRIPTIONElm).append("|")
						.append(contOfoRDER_STATUSElm).append("|").append(contOfoRDER_TOTALElm).append("|")
						.append(contOflINE_TOTALElm).append("|").append(contOffRMTD_ORDER_DATEElm).append("|")
						.append(contOfCITY_STATE_ZIPElm).append("|").append(contOfCUST_TOTALElm).append("|")
						.append(";\n");
			}

			String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" + "P_CUSTID VARCHAR(100), "
					+ "P_ORDID VARCHAR(100), " + "CUSTOMER_NAME VARCHAR(100), " + "CUSTOMER_ID VARCHAR(100), "
					+ "STREET_ADDRESS VARCHAR(100), " + "CITY VARCHAR(100), " + "STATE_PROVINCE VARCHAR(100), "
					+ "POSTAL_CODE VARCHAR(100), " + "COUNTRY_NAME VARCHAR(100), "
					+ "PRIMARY_PHONE_NUMBER VARCHAR(100), " + "CUST_EMAIL VARCHAR(100), "
					+ "G2_CUSTOMER_ID VARCHAR(100), " + "ORDER_ID VARCHAR(100), " + "ORDER_MODE VARCHAR(100), "
					+ "ORDER_DATE VARCHAR(100), " + "LINE_ITEM_ID VARCHAR(100), " + "UNIT_PRICE VARCHAR(100), "
					+ "QUANTITY VARCHAR(100), " + "PRODUCT_NAME VARCHAR(100), " + "PRODUCT_DESCRIPTION VARCHAR(100), "
					+ "ORDER_STATUS VARCHAR(100), " + "ORDER_TOTAL VARCHAR(100), " + "LINE_TOTAL VARCHAR(100), "
					+ "FRMTD_ORDER_DATE VARCHAR(100), " + "CITY_STATE_ZIP VARCHAR(100), " + "CUST_TOTAL VARCHAR(100) "
					+ ")";

			try (PreparedStatement g1Stmt = databaseConnection().prepareStatement(g1TableQuery)) {
				g1Stmt.executeUpdate();
			}

		}

		List<Element> g_1NdList4 = XMLUtil.getChildElements(dATA_DSElm, "G_1");

		if (doc.getElementsByTagName("QTR").getLength() > 0) {

			g1Builder.append("YEAR").append("|").append("QTR").append("|").append("REVENUE").append("|").append(";\n");

			for (Element g_1Elm3 : g_1NdList4) {

				// String isMulti = g_1Elm3.getAttribute("IsMulti");
				String contOfyEARElm = null;
				String contOfqTRElm = null;
				String contOfrEVENUEElm = null;

				List<Element> yEARNdList = XMLUtil.getChildElements(g_1Elm3, "YEAR");
				for (Element yEARElm : yEARNdList) {

					contOfyEARElm = yEARElm.getTextContent();
					if (contOfyEARElm.isEmpty()) {
						contOfyEARElm = null;
					}

				}
				List<Element> qTRNdList = XMLUtil.getChildElements(g_1Elm3, "QTR");
				for (Element qTRElm : qTRNdList) {

					contOfqTRElm = qTRElm.getTextContent();
					if (contOfqTRElm.isEmpty()) {
						contOfqTRElm = null;
					}

				}
				List<Element> rEVENUENdList = XMLUtil.getChildElements(g_1Elm3, "REVENUE");
				for (Element rEVENUEElm : rEVENUENdList) {

					contOfrEVENUEElm = rEVENUEElm.getTextContent();
					if (contOfrEVENUEElm.isEmpty()) {
						contOfrEVENUEElm = null;
					}

				}

				g1Builder.append(contOfyEARElm).append("|").append(contOfqTRElm).append("|").append(contOfrEVENUEElm)
						.append("|").append(";\n");
			}

			String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" + "YEAR VARCHAR(100), " + "QTR VARCHAR(100), "
					+ "REVENUE VARCHAR(100) " + ")";

			try (PreparedStatement g1Stmt = databaseConnection().prepareStatement(g1TableQuery)) {
				g1Stmt.executeUpdate();
			}

		}

		List<Element> g_2NdList = XMLUtil.getChildElements(dATA_DSElm, "G_2");

		StringBuilder g2Builder = new StringBuilder();

		if (doc.getElementsByTagName("QUANTITY").getLength() > 0) {

			g2Builder.append("YEAR").append("|").append("BRAND").append("|").append("QUANTITY").append("|")
					.append("REVENUE").append("|").append(";\n");

			for (Element g_2Elm : g_2NdList) {

				// String isMulti = g_2Elm.getAttribute("IsMulti");
				String contOfyEARElm = null;
				String contOfbRANDElm = null;
				String contOfqUANTITYElm = null;
				String contOfrEVENUEElm = null;

				List<Element> yEARNdList = XMLUtil.getChildElements(g_2Elm, "YEAR");
				for (Element yEARElm : yEARNdList) {

					contOfyEARElm = yEARElm.getTextContent();
					if (contOfyEARElm.isEmpty()) {
						contOfyEARElm = null;
					}

				}
				List<Element> bRANDNdList = XMLUtil.getChildElements(g_2Elm, "BRAND");
				for (Element bRANDElm : bRANDNdList) {

					contOfbRANDElm = bRANDElm.getTextContent();
					if (contOfbRANDElm.isEmpty()) {
						contOfbRANDElm = null;
					}

				}
				List<Element> qUANTITYNdList = XMLUtil.getChildElements(g_2Elm, "QUANTITY");
				for (Element qUANTITYElm : qUANTITYNdList) {

					contOfqUANTITYElm = qUANTITYElm.getTextContent();
					if (contOfqUANTITYElm.isEmpty()) {
						contOfqUANTITYElm = null;
					}

				}
				List<Element> rEVENUENdList = XMLUtil.getChildElements(g_2Elm, "REVENUE");
				for (Element rEVENUEElm : rEVENUENdList) {

					contOfrEVENUEElm = rEVENUEElm.getTextContent();
					if (contOfrEVENUEElm.isEmpty()) {
						contOfrEVENUEElm = null;
					}
				}

				g2Builder.append(contOfyEARElm).append("|").append(contOfbRANDElm).append("|").append(contOfqUANTITYElm)
						.append("|").append(contOfrEVENUEElm).append("|").append(";\n");
			}
		}

		// Element p_YEARElm = XMLUtil.getChildElement(dATA_DSElm, "P_YEAR");
		// String contOfP_YEARElm = p_YEARElm.getTextContent();

		// Element p_COMPANYElm = XMLUtil.getChildElement(dATA_DSElm, "P_COMPANY");
		// String contOfP_COMPANYElm = p_COMPANYElm.getTextContent();

		// Element p_ORGElm = XMLUtil.getChildElement(dATA_DSElm, "P_ORG");
		// String contOfP_ORGElm = p_ORGElm.getTextContent();

		Element p_DEPTElm = XMLUtil.getChildElement(dATA_DSElm, "P_DEPT");
		String contOfP_DEPTElm = p_DEPTElm.getTextContent();
		if (contOfP_DEPTElm.isEmpty()) {
			contOfP_DEPTElm = null;
		}

		Element p_OFFICEElm = XMLUtil.getChildElement(dATA_DSElm, "P_OFFICE");
		String contOfP_OFFICEElm = p_OFFICEElm.getTextContent();
		if (contOfP_OFFICEElm.isEmpty()) {
			contOfP_OFFICEElm = null;
		}

		List<Element> g_3NdList = XMLUtil.getChildElements(dATA_DSElm, "G_3");

		StringBuilder g3Builder = new StringBuilder();

		if (doc.getElementsByTagName("PRODUCT").getLength() > 0) {

			g3Builder.append("P_YEAR").append("|").append("P_COMPANY").append("|").append("P_ORG").append("|")
					.append("P_DEPT").append("|").append("P_OFFICE").append("|").append("COMPANY").append("|")
					.append("ORGANIZATION").append("|").append("DEPARTMENT").append("|").append("OFFICE").append("|")
					.append("REVENUE").append("|").append("TARGET_REVENUE").append("|").append("PER_NAME_YEAR")
					.append("|").append("BRAND").append("|").append("PRODUCT_TYPE").append("|").append("PRODUCT")
					.append("|").append(";\n");

			for (Element g_3Elm : g_3NdList) {

				// String isMulti = g_3Elm.getAttribute("IsMulti");
				String contOfcOMPANYElm = null;
				String contOfoRGANIZATIONElm = null;
				String contOfdEPARTMENTElm = null;
				String contOfoFFICEElm = null;
				String contOfrEVENUEElm = null;
				String contOftARGET_REVENUEElm = null;
				String contOfpER_NAME_YEARElm = null;
				String contOfbRANDElm = null;
				String contOfpRODUCT_TYPEElm = null;
				String contOfpRODUCTElm = null;

				List<Element> cOMPANYNdList = XMLUtil.getChildElements(g_3Elm, "COMPANY");
				for (Element cOMPANYElm : cOMPANYNdList) {

					contOfcOMPANYElm = cOMPANYElm.getTextContent();
					if (contOfcOMPANYElm.isEmpty()) {
						contOfcOMPANYElm = null;
					}

				}
				List<Element> oRGANIZATIONNdList = XMLUtil.getChildElements(g_3Elm, "ORGANIZATION");
				for (Element oRGANIZATIONElm : oRGANIZATIONNdList) {

					contOfoRGANIZATIONElm = oRGANIZATIONElm.getTextContent();
					if (contOfoRGANIZATIONElm.isEmpty()) {
						contOfoRGANIZATIONElm = null;
					}

				}
				List<Element> dEPARTMENTNdList = XMLUtil.getChildElements(g_3Elm, "DEPARTMENT");
				for (Element dEPARTMENTElm : dEPARTMENTNdList) {

					contOfdEPARTMENTElm = dEPARTMENTElm.getTextContent();
					if (contOfdEPARTMENTElm.isEmpty()) {
						contOfdEPARTMENTElm = null;
					}

				}
				List<Element> oFFICENdList = XMLUtil.getChildElements(g_3Elm, "OFFICE");
				for (Element oFFICEElm : oFFICENdList) {

					contOfoFFICEElm = oFFICEElm.getTextContent();
					if (contOfoFFICEElm.isEmpty()) {
						contOfoFFICEElm = null;
					}

				}
				List<Element> rEVENUENdList = XMLUtil.getChildElements(g_3Elm, "REVENUE");
				for (Element rEVENUEElm : rEVENUENdList) {

					contOfrEVENUEElm = rEVENUEElm.getTextContent();
					if (contOfrEVENUEElm.isEmpty()) {
						contOfrEVENUEElm = null;
					}

				}
				List<Element> tARGET_REVENUENdList = XMLUtil.getChildElements(g_3Elm, "TARGET_REVENUE");
				for (Element tARGET_REVENUEElm : tARGET_REVENUENdList) {

					contOftARGET_REVENUEElm = tARGET_REVENUEElm.getTextContent();
					if (contOftARGET_REVENUEElm.isEmpty()) {
						contOftARGET_REVENUEElm = null;
					}

				}
				List<Element> pER_NAME_YEARNdList = XMLUtil.getChildElements(g_3Elm, "PER_NAME_YEAR");
				for (Element pER_NAME_YEARElm : pER_NAME_YEARNdList) {

					contOfpER_NAME_YEARElm = pER_NAME_YEARElm.getTextContent();
					if (contOfpER_NAME_YEARElm.isEmpty()) {
						contOfpER_NAME_YEARElm = null;
					}

				}
				List<Element> bRANDNdList = XMLUtil.getChildElements(g_3Elm, "BRAND");
				for (Element bRANDElm : bRANDNdList) {

					contOfbRANDElm = bRANDElm.getTextContent();
					if (contOfbRANDElm.isEmpty()) {
						contOfbRANDElm = null;
					}

				}
				List<Element> pRODUCT_TYPENdList = XMLUtil.getChildElements(g_3Elm, "PRODUCT_TYPE");
				for (Element pRODUCT_TYPEElm : pRODUCT_TYPENdList) {

					contOfpRODUCT_TYPEElm = pRODUCT_TYPEElm.getTextContent();
					if (contOfpRODUCT_TYPEElm.isEmpty()) {
						contOfpRODUCT_TYPEElm = null;
					}

				}
				List<Element> pRODUCTNdList = XMLUtil.getChildElements(g_3Elm, "PRODUCT");
				for (Element pRODUCTElm : pRODUCTNdList) {

					contOfpRODUCTElm = pRODUCTElm.getTextContent();
					if (contOfpRODUCTElm.isEmpty()) {
						contOfpRODUCTElm = null;
					}

				}

				g3Builder.append(contOfP_YEARElm).append("|").append(contOfP_COMPANYElm).append("|")
						.append(contOfP_ORGElm).append("|").append(contOfP_DEPTElm).append("|")
						.append(contOfP_OFFICEElm).append("|").append(contOfcOMPANYElm).append("|")
						.append(contOfoRGANIZATIONElm).append("|").append(contOfdEPARTMENTElm).append("|")
						.append(contOfoFFICEElm).append("|").append(contOfrEVENUEElm).append("|")
						.append(contOftARGET_REVENUEElm).append("|").append(contOfpER_NAME_YEARElm).append("|")
						.append(contOfbRANDElm).append("|").append(contOfpRODUCT_TYPEElm).append("|")
						.append(contOfpRODUCTElm).append("|").append(";\n");

			}

		}

		List<Element> details_BI_ServerNdList = XMLUtil.getChildElements(dATA_DSElm, "Details_BI_Server");

		StringBuilder details_BI_Server_Builder = new StringBuilder();

		details_BI_Server_Builder.append("Brand").append("|").append("Order_Number").append("|")
				.append("Product_Description").append("|").append("Paid_Date").append("|").append("Units").append("|")
				.append("Order_Status").append("|").append("Revenue").append("|").append(";\n");

		for (Element details_BI_ServerElm : details_BI_ServerNdList) {

			// String isMulti = details_BI_ServerElm.getAttribute("IsMulti");
			String contOfbrandElm = null;
			String contOforder_NumberElm = null;
			String contOfproduct_DescriptionElm = null;
			String contOfpaid_DateElm = null;
			String contOfunitsElm = null;
			String contOforder_StatusElm = null;
			String contOfrevenueElm = null;

			List<Element> brandNdList = XMLUtil.getChildElements(details_BI_ServerElm, "Brand");
			for (Element brandElm : brandNdList) {

				contOfbrandElm = brandElm.getTextContent();
				if (contOfbrandElm.isEmpty()) {
					contOfbrandElm = null;
				}

			}
			List<Element> order_NumberNdList = XMLUtil.getChildElements(details_BI_ServerElm, "Order_Number");
			for (Element order_NumberElm : order_NumberNdList) {

				contOforder_NumberElm = order_NumberElm.getTextContent();
				if (contOforder_NumberElm.isEmpty()) {
					contOforder_NumberElm = null;
				}

			}
			List<Element> product_DescriptionNdList = XMLUtil.getChildElements(details_BI_ServerElm,
					"Product_Description");
			for (Element product_DescriptionElm : product_DescriptionNdList) {

				contOfproduct_DescriptionElm = product_DescriptionElm.getTextContent();
				if (contOfproduct_DescriptionElm.isEmpty()) {
					contOfproduct_DescriptionElm = null;
				}
			}
			List<Element> paid_DateNdList = XMLUtil.getChildElements(details_BI_ServerElm, "Paid_Date");
			for (Element paid_DateElm : paid_DateNdList) {

				contOfpaid_DateElm = paid_DateElm.getTextContent();
				if (contOfpaid_DateElm.isEmpty()) {
					contOfpaid_DateElm = null;
				}
			}
			List<Element> unitsNdList = XMLUtil.getChildElements(details_BI_ServerElm, "Units");
			for (Element unitsElm : unitsNdList) {

				contOfunitsElm = unitsElm.getTextContent();
				if (contOfunitsElm.isEmpty()) {
					contOfunitsElm = null;
				}

			}
			List<Element> order_StatusNdList = XMLUtil.getChildElements(details_BI_ServerElm, "Order_Status");
			for (Element order_StatusElm : order_StatusNdList) {

				contOforder_StatusElm = order_StatusElm.getTextContent();
				if (contOforder_StatusElm.isEmpty()) {
					contOforder_StatusElm = null;
				}

			}
			List<Element> revenueNdList = XMLUtil.getChildElements(details_BI_ServerElm, "Revenue");
			for (Element revenueElm : revenueNdList) {

				contOfrevenueElm = revenueElm.getTextContent();
				if (contOfrevenueElm.isEmpty()) {
					contOfrevenueElm = null;
				}
			}

			details_BI_Server_Builder.append(contOfbrandElm).append("|").append(contOforder_NumberElm).append("|")
					.append(contOfproduct_DescriptionElm).append("|").append(contOfpaid_DateElm).append("|")
					.append(contOfunitsElm).append("|").append(contOforder_StatusElm).append("|")
					.append(contOfrevenueElm).append("|").append(";\n");
		}

		List<Element> budget_EssbaseNdList = XMLUtil.getChildElements(dATA_DSElm, "Budget_Essbase");

		StringBuilder budget_Essbase_Builder = new StringBuilder();

		budget_Essbase_Builder.append("Budget_Revenue").append("|").append("Actual_Revenue").append("|")
				.append("Quarter").append("|").append("Brand").append("|").append(";\n");

		for (Element budget_EssbaseElm : budget_EssbaseNdList) {

			// String isMulti = budget_EssbaseElm.getAttribute("IsMulti");
			String contOfbudget_RevenueElm = null;
			String contOfactual_RevenueElm = null;
			String contOfquarterElm = null;
			String contOfbrandElm = null;

			List<Element> budget_RevenueNdList = XMLUtil.getChildElements(budget_EssbaseElm, "Budget_Revenue");
			for (Element budget_RevenueElm : budget_RevenueNdList) {

				contOfbudget_RevenueElm = budget_RevenueElm.getTextContent();
				if (contOfbudget_RevenueElm.isEmpty()) {
					contOfbudget_RevenueElm = null;
				}

			}
			List<Element> actual_RevenueNdList = XMLUtil.getChildElements(budget_EssbaseElm, "Actual_Revenue");
			for (Element actual_RevenueElm : actual_RevenueNdList) {

				contOfactual_RevenueElm = actual_RevenueElm.getTextContent();
				if (contOfactual_RevenueElm.isEmpty()) {
					contOfactual_RevenueElm = null;
				}

			}
			List<Element> quarterNdList = XMLUtil.getChildElements(budget_EssbaseElm, "Quarter");
			for (Element quarterElm : quarterNdList) {

				contOfquarterElm = quarterElm.getTextContent();
				if (contOfquarterElm.isEmpty()) {
					contOfquarterElm = null;
				}

			}
			List<Element> brandNdList = XMLUtil.getChildElements(budget_EssbaseElm, "Brand");
			for (Element brandElm : brandNdList) {

				contOfbrandElm = brandElm.getTextContent();
				if (contOfbrandElm.isEmpty()) {
					contOfbrandElm = null;
				}

			}

			budget_Essbase_Builder.append(contOfbudget_RevenueElm).append("|").append(contOfactual_RevenueElm)
					.append("|").append(contOfquarterElm).append("|").append(contOfbrandElm).append("|").append(";\n");

		}

		// DataDS_Sections

		Element sectionsElm = XMLUtil.getChildElement(dATA_DSElm, "sections");
		// String contOfsectionsElm = sectionsElm.getTextContent();

		Element systemElm = XMLUtil.getChildElement(sectionsElm, "system");
		// String contOfsystemElm = systemElm.getTextContent();

		StringBuilder sections_System_Builder = new StringBuilder();

		Element reportElm = XMLUtil.getChildElement(systemElm, "report");
		String contOfreportElm = reportElm.getTextContent();
		if (contOfreportElm.isEmpty()) {
			contOfreportElm = null;
		}

		Element userElm = XMLUtil.getChildElement(systemElm, "user");
		String contOfuserElm = userElm.getTextContent();
		if (contOfuserElm.isEmpty()) {
			contOfuserElm = null;
		}

		Element appElm = XMLUtil.getChildElement(systemElm, "app");
		String contOfappElm = appElm.getTextContent();
		if (contOfappElm.isEmpty()) {
			contOfappElm = null;
		}

		Element formElm = XMLUtil.getChildElement(systemElm, "form");
		String contOfformElm = formElm.getTextContent();
		if (contOfformElm.isEmpty()) {
			contOfformElm = null;
		}

		Element versionElm = XMLUtil.getChildElement(systemElm, "version");
		String contOfversionElm = versionElm.getTextContent();
		if (contOfversionElm.isEmpty()) {
			contOfversionElm = null;
		}

		Element enviromentElm = XMLUtil.getChildElement(systemElm, "enviroment");
		String contOfenviromentElm = enviromentElm.getTextContent();
		if (contOfenviromentElm.isEmpty()) {
			contOfenviromentElm = null;
		}

		Element dateElm = XMLUtil.getChildElement(systemElm, "date");
		String contOfdateElm = dateElm.getTextContent();
		if (contOfdateElm.isEmpty()) {
			contOfdateElm = null;
		}

		Element timeElm = XMLUtil.getChildElement(systemElm, "time");
		String contOftimeElm = timeElm.getTextContent();
		if (contOftimeElm.isEmpty()) {
			contOftimeElm = null;
		}

		sections_System_Builder.append("report").append("|").append("user").append("|").append("app").append("|")
				.append("form").append("|").append("version").append("|").append("enviroment").append("|")
				.append("date").append("|").append("time").append("|").append(";\n");

		sections_System_Builder.append(contOfreportElm).append("|").append(contOfuserElm).append("|")
				.append(contOfappElm).append("|").append(contOfformElm).append("|").append(contOfversionElm).append("|")
				.append(contOfenviromentElm).append("|").append(contOfdateElm).append("|").append(contOftimeElm)
				.append("|").append(";\n");

		Element grid0_1Elm = XMLUtil.getChildElement(sectionsElm, "grid0_1");

		StringBuilder sections_Grid0_1_Builder = new StringBuilder();

		String sectionId = grid0_1Elm.getAttribute("sectionId");
		String type = grid0_1Elm.getAttribute("type");
		// String contOfgrid0_1Elm = grid0_1Elm.getTextContent();

		List<Element> rowsetNdList = XMLUtil.getChildElements(grid0_1Elm, "rowset");

		if ((grid0_1Elm.getElementsByTagName("_ReportCodeAddBook001").getLength() > 0)
				&& (grid0_1Elm.getElementsByTagName("_ReportCodeAddBook001").getLength() > 0)) {

			sections_Grid0_1_Builder.append("sectionId").append("|").append("type").append("|")
					.append("_ReportCodeAddBook001").append("|").append("_Description001").append("|")
					.append("_CollectionManager").append("|").append("_Description001_").append("|")
					.append("_CreditManager").append("|").append("_Description001__").append("|")
					.append("_PayorAddressNumber").append("|").append("_Description001___").append("|")
					.append("_AddressNumber").append("|").append("_NameAlpha").append("|").append("_Company")
					.append("|").append("_Description001____").append("|").append("_DateInvoiceJ").append("|")
					.append("_DateForGLandVoucherJULIA").append("|").append("_YearString").append("|")
					.append("_PeriodNoGeneralLedge").append("|").append("_DateDueJulian").append("|")
					.append("_AsOfDate").append("|").append("_DocVoucherInvoiceE").append("|").append("_DocumentType")
					.append("|").append("_Description001_____").append("|").append("_CompanyKey").append("|")
					.append("_DocumentPayItem").append("|").append("_AmountGross").append("|").append("_AmountOpen")
					.append("|").append("_AmtDiscountAvailable").append("|").append("_DateDiscountDueJulian")
					.append("|").append("_AmountFuture").append("|").append("_CurrentAmountDue").append("|")
					.append("_AmtAgingCategories1").append("|").append("_AmtAgingCategories2").append("|")
					.append("_AmtAgingCategories3").append("|").append("_AmtAgingCategories4").append("|")
					.append("_AmtAgingCategories5").append("|").append("_AmtAgingCategories6").append("|")
					.append("_AmtAgingCategories7").append("|").append("_CurrencyCodeBase").append("|")
					.append("_Description001______").append("|").append(";\n");

			for (Element rowsetElm : rowsetNdList) {
				// String isMulti = rowsetElm.getAttribute("IsMulti");
				String contOf_ReportCodeAddBook001Elm = null;
				String contOf_Description001Elm = null;
				String contOf_CollectionManagerElm = null;
				String contOf_Description001_Elm = null;
				String contOf_CreditManagerElm = null;
				String contOf_Description001__Elm = null;
				String contOf_PayorAddressNumberElm = null;
				String contOf_Description001___Elm = null;
				String contOf_AddressNumberElm = null;
				String contOf_NameAlphaElm = null;
				String contOf_CompanyElm = null;
				String contOf_Description001____Elm = null;
				String contOf_DateInvoiceJElm = null;
				String contOf_DateForGLandVoucherJULIAElm = null;
				String contOf_YearStringElm = null;
				String contOf_PeriodNoGeneralLedgeElm = null;
				String contOf_DateDueJulianElm = null;
				String contOf_AsOfDateElm = null;
				String contOf_DocVoucherInvoiceEElm = null;
				String contOf_DocumentTypeElm = null;
				String contOf_Description001_____Elm = null;
				String contOf_CompanyKeyElm = null;
				String contOf_DocumentPayItemElm = null;
				String contOf_AmountGrossElm = null;
				String contOf_AmountOpenElm = null;
				String contOf_AmtDiscountAvailableElm = null;
				String contOf_DateDiscountDueJulianElm = null;
				String contOf_AmountFutureElm = null;
				String contOf_CurrentAmountDueElm = null;
				String contOf_AmtAgingCategories1Elm = null;
				String contOf_AmtAgingCategories2Elm = null;
				String contOf_AmtAgingCategories3Elm = null;
				String contOf_AmtAgingCategories4Elm = null;
				String contOf_AmtAgingCategories5Elm = null;
				String contOf_AmtAgingCategories6Elm = null;
				String contOf_AmtAgingCategories7Elm = null;
				String contOf_CurrencyCodeBaseElm = null;
				String contOf_Description001______Elm = null;

				List<Element> _ReportCodeAddBook001NdList = XMLUtil.getChildElements(rowsetElm,
						"_ReportCodeAddBook001");
				for (Element _ReportCodeAddBook001Elm : _ReportCodeAddBook001NdList) {
					contOf_ReportCodeAddBook001Elm = _ReportCodeAddBook001Elm.getTextContent();
					if (contOf_ReportCodeAddBook001Elm.isEmpty()) {
						contOf_ReportCodeAddBook001Elm = null;
					}
				}
				List<Element> _Description001NdList = XMLUtil.getChildElements(rowsetElm, "_Description001");
				for (Element _Description001Elm : _Description001NdList) {
					contOf_Description001Elm = _Description001Elm.getTextContent();
					if (contOf_Description001Elm.isEmpty()) {
						contOf_Description001Elm = null;
					}
				}
				List<Element> _CollectionManagerNdList = XMLUtil.getChildElements(rowsetElm, "_CollectionManager");
				for (Element _CollectionManagerElm : _CollectionManagerNdList) {
					contOf_CollectionManagerElm = _CollectionManagerElm.getTextContent();
					if (contOf_CollectionManagerElm.isEmpty()) {
						contOf_CollectionManagerElm = null;
					}
				}
				List<Element> _Description001_NdList = XMLUtil.getChildElements(rowsetElm, "_Description001_");
				for (Element _Description001_Elm : _Description001_NdList) {
					contOf_Description001_Elm = _Description001_Elm.getTextContent();
					if (contOf_Description001_Elm.isEmpty()) {
						contOf_Description001_Elm = null;
					}
				}
				List<Element> _CreditManagerNdList = XMLUtil.getChildElements(rowsetElm, "_CreditManager");
				for (Element _CreditManagerElm : _CreditManagerNdList) {
					contOf_CreditManagerElm = _CreditManagerElm.getTextContent();
					if (contOf_CreditManagerElm.isEmpty()) {
						contOf_CreditManagerElm = null;
					}
				}
				List<Element> _Description001__NdList = XMLUtil.getChildElements(rowsetElm, "_Description001__");
				for (Element _Description001__Elm : _Description001__NdList) {
					contOf_Description001__Elm = _Description001__Elm.getTextContent();
					if (contOf_Description001__Elm.isEmpty()) {
						contOf_Description001__Elm = null;
					}
				}
				List<Element> _PayorAddressNumberNdList = XMLUtil.getChildElements(rowsetElm, "_PayorAddressNumber");
				for (Element _PayorAddressNumberElm : _PayorAddressNumberNdList) {
					contOf_PayorAddressNumberElm = _PayorAddressNumberElm.getTextContent();
					if (contOf_PayorAddressNumberElm.isEmpty()) {
						contOf_PayorAddressNumberElm = null;
					}
				}
				List<Element> _Description001___NdList = XMLUtil.getChildElements(rowsetElm, "_Description001___");
				for (Element _Description001___Elm : _Description001___NdList) {
					contOf_Description001___Elm = _Description001___Elm.getTextContent();
					if (contOf_Description001___Elm.isEmpty()) {
						contOf_Description001___Elm = null;
					}
				}
				List<Element> _AddressNumberNdList = XMLUtil.getChildElements(rowsetElm, "_AddressNumber");
				for (Element _AddressNumberElm : _AddressNumberNdList) {
					contOf_AddressNumberElm = _AddressNumberElm.getTextContent();
					if (contOf_AddressNumberElm.isEmpty()) {
						contOf_AddressNumberElm = null;
					}
				}
				List<Element> _NameAlphaNdList = XMLUtil.getChildElements(rowsetElm, "_NameAlpha");
				for (Element _NameAlphaElm : _NameAlphaNdList) {
					contOf_NameAlphaElm = _NameAlphaElm.getTextContent();
					if (contOf_NameAlphaElm.isEmpty()) {
						contOf_NameAlphaElm = null;
					}
				}
				List<Element> _CompanyNdList = XMLUtil.getChildElements(rowsetElm, "_Company");
				for (Element _CompanyElm : _CompanyNdList) {
					contOf_CompanyElm = _CompanyElm.getTextContent();
					if (contOf_CompanyElm.isEmpty()) {
						contOf_CompanyElm = null;
					}
				}
				List<Element> _Description001____NdList = XMLUtil.getChildElements(rowsetElm, "_Description001____");
				for (Element _Description001____Elm : _Description001____NdList) {
					contOf_Description001____Elm = _Description001____Elm.getTextContent();
					if (contOf_Description001____Elm.isEmpty()) {
						contOf_Description001____Elm = null;
					}
				}
				List<Element> _DateInvoiceJNdList = XMLUtil.getChildElements(rowsetElm, "_DateInvoiceJ");
				for (Element _DateInvoiceJElm : _DateInvoiceJNdList) {
					contOf_DateInvoiceJElm = _DateInvoiceJElm.getTextContent();
					if (contOf_DateInvoiceJElm.isEmpty()) {
						contOf_DateInvoiceJElm = null;
					}
				}
				List<Element> _DateForGLandVoucherJULIANdList = XMLUtil.getChildElements(rowsetElm,
						"_DateForGLandVoucherJULIA");
				for (Element _DateForGLandVoucherJULIAElm : _DateForGLandVoucherJULIANdList) {
					contOf_DateForGLandVoucherJULIAElm = _DateForGLandVoucherJULIAElm.getTextContent();
					if (contOf_DateForGLandVoucherJULIAElm.isEmpty()) {
						contOf_DateForGLandVoucherJULIAElm = null;
					}
				}
				List<Element> _YearStringNdList = XMLUtil.getChildElements(rowsetElm, "_YearString");
				for (Element _YearStringElm : _YearStringNdList) {
					contOf_YearStringElm = _YearStringElm.getTextContent();
					if (contOf_YearStringElm.isEmpty()) {
						contOf_YearStringElm = null;
					}
				}
				List<Element> _PeriodNoGeneralLedgeNdList = XMLUtil.getChildElements(rowsetElm,
						"_PeriodNoGeneralLedge");
				for (Element _PeriodNoGeneralLedgeElm : _PeriodNoGeneralLedgeNdList) {
					contOf_PeriodNoGeneralLedgeElm = _PeriodNoGeneralLedgeElm.getTextContent();
					if (contOf_PeriodNoGeneralLedgeElm.isEmpty()) {
						contOf_PeriodNoGeneralLedgeElm = null;
					}
				}
				List<Element> _DateDueJulianNdList = XMLUtil.getChildElements(rowsetElm, "_DateDueJulian");
				for (Element _DateDueJulianElm : _DateDueJulianNdList) {
					contOf_DateDueJulianElm = _DateDueJulianElm.getTextContent();
					if (contOf_DateDueJulianElm.isEmpty()) {
						contOf_DateDueJulianElm = null;
					}
				}
				List<Element> _AsOfDateNdList = XMLUtil.getChildElements(rowsetElm, "_AsOfDate");
				for (Element _AsOfDateElm : _AsOfDateNdList) {
					contOf_AsOfDateElm = _AsOfDateElm.getTextContent();
					if (contOf_AsOfDateElm.isEmpty()) {
						contOf_AsOfDateElm = null;
					}
				}
				List<Element> _DocVoucherInvoiceENdList = XMLUtil.getChildElements(rowsetElm, "_DocVoucherInvoiceE");
				for (Element _DocVoucherInvoiceEElm : _DocVoucherInvoiceENdList) {
					contOf_DocVoucherInvoiceEElm = _DocVoucherInvoiceEElm.getTextContent();
					if (contOf_DocVoucherInvoiceEElm.isEmpty()) {
						contOf_DocVoucherInvoiceEElm = null;
					}
				}
				List<Element> _DocumentTypeNdList = XMLUtil.getChildElements(rowsetElm, "_DocumentType");
				for (Element _DocumentTypeElm : _DocumentTypeNdList) {
					contOf_DocumentTypeElm = _DocumentTypeElm.getTextContent();
					if (contOf_DocumentTypeElm.isEmpty()) {
						contOf_DocumentTypeElm = null;
					}
				}
				List<Element> _Description001_____NdList = XMLUtil.getChildElements(rowsetElm, "_Description001_____");
				for (Element _Description001_____Elm : _Description001_____NdList) {
					contOf_Description001_____Elm = _Description001_____Elm.getTextContent();
					if (contOf_Description001_____Elm.isEmpty()) {
						contOf_Description001_____Elm = null;
					}
				}
				List<Element> _CompanyKeyNdList = XMLUtil.getChildElements(rowsetElm, "_CompanyKey");
				for (Element _CompanyKeyElm : _CompanyKeyNdList) {
					contOf_CompanyKeyElm = _CompanyKeyElm.getTextContent();
					if (contOf_CompanyKeyElm.isEmpty()) {
						contOf_CompanyKeyElm = null;
					}
				}
				List<Element> _DocumentPayItemNdList = XMLUtil.getChildElements(rowsetElm, "_DocumentPayItem");
				for (Element _DocumentPayItemElm : _DocumentPayItemNdList) {
					contOf_DocumentPayItemElm = _DocumentPayItemElm.getTextContent();
					if (contOf_DocumentPayItemElm.isEmpty()) {
						contOf_DocumentPayItemElm = null;
					}
				}
				List<Element> _AmountGrossNdList = XMLUtil.getChildElements(rowsetElm, "_AmountGross");
				for (Element _AmountGrossElm : _AmountGrossNdList) {
					contOf_AmountGrossElm = _AmountGrossElm.getTextContent();
					if (contOf_AmountGrossElm.isEmpty()) {
						contOf_AmountGrossElm = null;
					}
				}
				List<Element> _AmountOpenNdList = XMLUtil.getChildElements(rowsetElm, "_AmountOpen");
				for (Element _AmountOpenElm : _AmountOpenNdList) {
					contOf_AmountOpenElm = _AmountOpenElm.getTextContent();
					if (contOf_AmountOpenElm.isEmpty()) {
						contOf_AmountOpenElm = null;
					}
				}
				List<Element> _AmtDiscountAvailableNdList = XMLUtil.getChildElements(rowsetElm,
						"_AmtDiscountAvailable");
				for (Element _AmtDiscountAvailableElm : _AmtDiscountAvailableNdList) {
					contOf_AmtDiscountAvailableElm = _AmtDiscountAvailableElm.getTextContent();
					if (contOf_AmtDiscountAvailableElm.isEmpty()) {
						contOf_AmtDiscountAvailableElm = null;
					}
				}
				List<Element> _DateDiscountDueJulianNdList = XMLUtil.getChildElements(rowsetElm,
						"_DateDiscountDueJulian");
				for (Element _DateDiscountDueJulianElm : _DateDiscountDueJulianNdList) {
					contOf_DateDiscountDueJulianElm = _DateDiscountDueJulianElm.getTextContent();
					if (contOf_DateDiscountDueJulianElm.isEmpty()) {
						contOf_DateDiscountDueJulianElm = null;
					}
				}
				List<Element> _AmountFutureNdList = XMLUtil.getChildElements(rowsetElm, "_AmountFuture");
				for (Element _AmountFutureElm : _AmountFutureNdList) {
					contOf_AmountFutureElm = _AmountFutureElm.getTextContent();
					if (contOf_AmountFutureElm.isEmpty()) {
						contOf_AmountFutureElm = null;
					}
				}
				List<Element> _CurrentAmountDueNdList = XMLUtil.getChildElements(rowsetElm, "_CurrentAmountDue");
				for (Element _CurrentAmountDueElm : _CurrentAmountDueNdList) {
					contOf_CurrentAmountDueElm = _CurrentAmountDueElm.getTextContent();
					if (contOf_CurrentAmountDueElm.isEmpty()) {
						contOf_CurrentAmountDueElm = null;
					}
				}
				List<Element> _AmtAgingCategories1NdList = XMLUtil.getChildElements(rowsetElm, "_AmtAgingCategories1");
				for (Element _AmtAgingCategories1Elm : _AmtAgingCategories1NdList) {
					contOf_AmtAgingCategories1Elm = _AmtAgingCategories1Elm.getTextContent();
					if (contOf_AmtAgingCategories1Elm.isEmpty()) {
						contOf_AmtAgingCategories1Elm = null;
					}
				}
				List<Element> _AmtAgingCategories2NdList = XMLUtil.getChildElements(rowsetElm, "_AmtAgingCategories2");
				for (Element _AmtAgingCategories2Elm : _AmtAgingCategories2NdList) {
					contOf_AmtAgingCategories2Elm = _AmtAgingCategories2Elm.getTextContent();
					if (contOf_AmtAgingCategories2Elm.isEmpty()) {
						contOf_AmtAgingCategories2Elm = null;
					}
				}
				List<Element> _AmtAgingCategories3NdList = XMLUtil.getChildElements(rowsetElm, "_AmtAgingCategories3");
				for (Element _AmtAgingCategories3Elm : _AmtAgingCategories3NdList) {
					contOf_AmtAgingCategories3Elm = _AmtAgingCategories3Elm.getTextContent();
					if (contOf_AmtAgingCategories3Elm.isEmpty()) {
						contOf_AmtAgingCategories3Elm = null;
					}
				}
				List<Element> _AmtAgingCategories4NdList = XMLUtil.getChildElements(rowsetElm, "_AmtAgingCategories4");
				for (Element _AmtAgingCategories4Elm : _AmtAgingCategories4NdList) {
					contOf_AmtAgingCategories4Elm = _AmtAgingCategories4Elm.getTextContent();
					if (contOf_AmtAgingCategories4Elm.isEmpty()) {
						contOf_AmtAgingCategories4Elm = null;
					}
				}
				List<Element> _AmtAgingCategories5NdList = XMLUtil.getChildElements(rowsetElm, "_AmtAgingCategories5");
				for (Element _AmtAgingCategories5Elm : _AmtAgingCategories5NdList) {
					contOf_AmtAgingCategories5Elm = _AmtAgingCategories5Elm.getTextContent();
					if (contOf_AmtAgingCategories5Elm.isEmpty()) {
						contOf_AmtAgingCategories5Elm = null;
					}
				}
				List<Element> _AmtAgingCategories6NdList = XMLUtil.getChildElements(rowsetElm, "_AmtAgingCategories6");
				for (Element _AmtAgingCategories6Elm : _AmtAgingCategories6NdList) {
					contOf_AmtAgingCategories6Elm = _AmtAgingCategories6Elm.getTextContent();
					if (contOf_AmtAgingCategories6Elm.isEmpty()) {
						contOf_AmtAgingCategories6Elm = null;
					}
				}
				List<Element> _AmtAgingCategories7NdList = XMLUtil.getChildElements(rowsetElm, "_AmtAgingCategories7");
				for (Element _AmtAgingCategories7Elm : _AmtAgingCategories7NdList) {
					contOf_AmtAgingCategories7Elm = _AmtAgingCategories7Elm.getTextContent();
					if (contOf_AmtAgingCategories7Elm.isEmpty()) {
						contOf_AmtAgingCategories7Elm = null;
					}
				}
				List<Element> _CurrencyCodeBaseNdList = XMLUtil.getChildElements(rowsetElm, "_CurrencyCodeBase");
				for (Element _CurrencyCodeBaseElm : _CurrencyCodeBaseNdList) {
					contOf_CurrencyCodeBaseElm = _CurrencyCodeBaseElm.getTextContent();
					if (contOf_CurrencyCodeBaseElm.isEmpty()) {
						contOf_CurrencyCodeBaseElm = null;
					}
				}
				List<Element> _Description001______NdList = XMLUtil.getChildElements(rowsetElm,
						"_Description001______");
				for (Element _Description001______Elm : _Description001______NdList) {
					contOf_Description001______Elm = _Description001______Elm.getTextContent();
					if (contOf_Description001______Elm.isEmpty()) {
						contOf_Description001______Elm = null;
					}
				}

				sections_Grid0_1_Builder.append(sectionId).append("|").append(type).append("|")
						.append(contOf_ReportCodeAddBook001Elm).append("|").append(contOf_Description001Elm).append("|")
						.append(contOf_CollectionManagerElm).append("|").append(contOf_Description001_Elm).append("|")
						.append(contOf_CreditManagerElm).append("|").append(contOf_Description001__Elm).append("|")
						.append(contOf_PayorAddressNumberElm).append("|").append(contOf_Description001___Elm)
						.append("|").append(contOf_AddressNumberElm).append("|").append(contOf_NameAlphaElm).append("|")
						.append(contOf_CompanyElm).append("|").append(contOf_Description001____Elm).append("|")
						.append(contOf_DateInvoiceJElm).append("|").append(contOf_DateForGLandVoucherJULIAElm)
						.append("|").append(contOf_YearStringElm).append("|").append(contOf_PeriodNoGeneralLedgeElm)
						.append("|").append(contOf_DateDueJulianElm).append("|").append(contOf_AsOfDateElm).append("|")
						.append(contOf_DocVoucherInvoiceEElm).append("|").append(contOf_DocumentTypeElm).append("|")
						.append(contOf_Description001_____Elm).append("|").append(contOf_CompanyKeyElm).append("|")
						.append(contOf_DocumentPayItemElm).append("|").append(contOf_AmountGrossElm).append("|")
						.append(contOf_AmountOpenElm).append("|").append(contOf_AmtDiscountAvailableElm).append("|")
						.append(contOf_DateDiscountDueJulianElm).append("|").append(contOf_AmountFutureElm).append("|")
						.append(contOf_CurrentAmountDueElm).append("|").append(contOf_AmtAgingCategories1Elm)
						.append("|").append(contOf_AmtAgingCategories2Elm).append("|")
						.append(contOf_AmtAgingCategories3Elm).append("|").append(contOf_AmtAgingCategories4Elm)
						.append("|").append(contOf_AmtAgingCategories5Elm).append("|")
						.append(contOf_AmtAgingCategories6Elm).append("|").append(contOf_AmtAgingCategories7Elm)
						.append("|").append(contOf_CurrencyCodeBaseElm).append("|")
						.append(contOf_Description001______Elm).append("|").append(";\n");

			}

			String sections_Grid0_1TableQuery = "CREATE TABLE IF NOT EXISTS Sections_Grid0_1 ("
					+ "sectionId VARCHAR(100), " + "type VARCHAR(100), " + "_ReportCodeAddBook001 VARCHAR(100), "
					+ "_Description001 VARCHAR(100)," + "_CollectionManager VARCHAR(100),"
					+ "_Description001_ VARCHAR(100), " + "_CreditManager VARCHAR(100), "
					+ "_Description001__ VARCHAR(100), " + "_PayorAddressNumber VARCHAR(100), "
					+ "_Description001___ VARCHAR(100), " + "_AddressNumber VARCHAR(100), "
					+ "_NameAlpha VARCHAR(100), " + "_Company VARCHAR(100), " + "_Description001____ VARCHAR(100), "
					+ "_DateInvoiceJ VARCHAR(100), " + "_DateForGLandVoucherJULIA VARCHAR(100), "
					+ "_YearString VARCHAR(100), " + "_PeriodNoGeneralLedge VARCHAR(100), "
					+ "_DateDueJulian VARCHAR(100), " + "_AsOfDate VARCHAR(100), "
					+ "_DocVoucherInvoiceE VARCHAR(100), " + "_DocumentType VARCHAR(100), "
					+ "_Description001_____ VARCHAR(100), " + "_CompanyKey VARCHAR(100), "
					+ "_DocumentPayItem VARCHAR(100), " + "_AmountGross VARCHAR(100), " + "_AmountOpen VARCHAR(100), "
					+ "_AmtDiscountAvailable VARCHAR(100), " + "_DateDiscountDueJulian VARCHAR(100), "
					+ "_AmountFuture VARCHAR(100), " + "_CurrentAmountDue VARCHAR(100), "
					+ "_AmtAgingCategories1 VARCHAR(100), " + "_AmtAgingCategories2 VARCHAR(100), "
					+ "_AmtAgingCategories3 VARCHAR(100), " + "_AmtAgingCategories4 VARCHAR(100), "
					+ "_AmtAgingCategories5 VARCHAR(100), " + "_AmtAgingCategories6 VARCHAR(100), "
					+ "_AmtAgingCategories7 VARCHAR(100), " + "_CurrencyCodeBase VARCHAR(100), "
					+ "_Description001______ VARCHAR(100) " + ")";

			try (PreparedStatement sections_Grid0_1Stmt = databaseConnection()
					.prepareStatement(sections_Grid0_1TableQuery);) {
				sections_Grid0_1Stmt.executeUpdate();
			}

		} else {

			sections_Grid0_1_Builder.append("sectionId").append("|").append("type").append("|").append("_AddressNumber")
					.append("|").append("_Description001").append("|").append("_PrimaryLastVendorNo").append("|")
					.append("_Description001_").append("|").append("_ItemNoUnknownFormat").append("|")
					.append("_Description001__").append("|").append("_DocumentOrderInvoiceE").append("|")
					.append("_OrderType").append("|").append("_Description001___").append("|").append("_CostCenter")
					.append("|").append("_Description001____").append("|").append("_StatusCodeLast").append("|")
					.append("_Description001_____").append("|").append("_StatusCodeNext").append("|")
					.append("_Description001______").append("|").append("_RelatedPoSoNumber").append("|")
					.append("_RelatedOrderType").append("|").append("_Description001_______").append("|")
					.append("_StatusCodeLast_").append("|").append("_Description001________").append("|")
					.append("_StatusCodeNext_").append("|").append("_Description001_________").append("|")
					.append("_OrderNumber").append("|").append("_AsOfDate").append("|")
					.append("_BackordersOlderThanReqDate").append("|").append("_DateTransactionJulian").append("|")
					.append("_DateRequestedJulian").append("|").append("_ScheduledPickDate").append("|")
					.append("_ScheduledPickDate_").append("|").append("_PeriodNoGeneralLedge").append("|")
					.append("_YearString").append("|").append("_UnitOfMeasureAsInput").append("|")
					.append("_Description001__________").append("|").append("_UnitsTransactionQty").append("|")
					.append("_UnitsQuantityShipped").append("|").append("_UnitsQuanBackorHeld").append("|")
					.append("_UnitsLineItemQtyRe").append("|").append("_UnitsOpenQuantity").append("|")
					.append("_CurrencyCodeBase").append("|").append("_Description001___________").append("|")
					.append("_AmountExtendedPrice").append("|").append("_CurrencyCodeFrom").append("|")
					.append("_AmtPricePerUnit2").append("|").append("_AmountReceived").append("|")
					.append("_AmountOpen1").append("|").append("_BuyerNumber").append("|")
					.append("_Description001____________").append("|").append("_UnitOfMeasurePrimary").append("|")
					.append(";\n");

			for (Element rowsetElm : rowsetNdList) {

				// String isMulti= rowsetElm.getAttribute("IsMulti");
				String contOf_AddressNumberElm = null;
				String contOf_Description001Elm = null;
				String contOf_PrimaryLastVendorNoElm = null;
				String contOf_Description001_Elm = null;
				String contOf_ItemNoUnknownFormatElm = null;
				String contOf_Description001__Elm = null;
				String contOf_DocumentOrderInvoiceEElm = null;
				String contOf_OrderTypeElm = null;
				String contOf_Description001___Elm = null;
				String contOf_CostCenterElm = null;
				String contOf_Description001____Elm = null;
				String contOf_StatusCodeLastElm = null;
				String contOf_Description001_____Elm = null;
				String contOf_StatusCodeNextElm = null;
				String contOf_Description001______Elm = null;
				String contOf_RelatedPoSoNumberElm = null;
				String contOf_RelatedOrderTypeElm = null;
				String contOf_Description001_______Elm = null;
				String contOf_StatusCodeLast_Elm = null;
				String contOf_Description001________Elm = null;
				String contOf_StatusCodeNext_Elm = null;
				String contOf_Description001_________Elm = null;
				String contOf_OrderNumberElm = null;
				String contOf_AsOfDateElm = null;
				String contOf_BackordersOlderThanReqDateElm = null;
				String contOf_DateTransactionJulianElm = null;
				String contOf_DateRequestedJulianElm = null;
				String contOf_ScheduledPickDateElm = null;
				String contOf_ScheduledPickDate_Elm = null;
				String contOf_PeriodNoGeneralLedgeElm = null;
				String contOf_YearStringElm = null;
				String contOf_UnitOfMeasureAsInputElm = null;
				String contOf_Description001__________Elm = null;
				String contOf_UnitsTransactionQtyElm = null;
				String contOf_UnitsQuantityShippedElm = null;
				String contOf_UnitsQuanBackorHeldElm = null;
				String contOf_UnitsLineItemQtyReElm = null;
				String contOf_UnitsOpenQuantityElm = null;
				String contOf_CurrencyCodeBaseElm = null;
				String contOf_Description001___________Elm = null;
				String contOf_AmountExtendedPriceElm = null;
				String contOf_CurrencyCodeFromElm = null;
				String contOf_AmtPricePerUnit2Elm = null;
				String contOf_AmountReceivedElm = null;
				String contOf_AmountOpen1Elm = null;
				String contOf_BuyerNumberElm = null;
				String contOf_Description001____________Elm = null;
				String contOf_UnitOfMeasurePrimaryElm = null;

				List<Element> _AddressNumberNdList = XMLUtil.getChildElements(rowsetElm, "_AddressNumber");
				for (Element _AddressNumberElm : _AddressNumberNdList) {
					contOf_AddressNumberElm = _AddressNumberElm.getTextContent();
					if (contOf_AddressNumberElm.isEmpty()) {
						contOf_AddressNumberElm = null;
					}
				}
				List<Element> _Description001NdList = XMLUtil.getChildElements(rowsetElm, "_Description001");
				for (Element _Description001Elm : _Description001NdList) {
					contOf_Description001Elm = _Description001Elm.getTextContent();
					if (contOf_Description001Elm.isEmpty()) {
						contOf_Description001Elm = null;
					}
				}
				List<Element> _PrimaryLastVendorNoNdList = XMLUtil.getChildElements(rowsetElm, "_PrimaryLastVendorNo");
				for (Element _PrimaryLastVendorNoElm : _PrimaryLastVendorNoNdList) {
					contOf_PrimaryLastVendorNoElm = _PrimaryLastVendorNoElm.getTextContent();
					if (contOf_PrimaryLastVendorNoElm.isEmpty()) {
						contOf_PrimaryLastVendorNoElm = null;
					}
				}
				List<Element> _Description001_NdList = XMLUtil.getChildElements(rowsetElm, "_Description001_");
				for (Element _Description001_Elm : _Description001_NdList) {
					contOf_Description001_Elm = _Description001_Elm.getTextContent();
					if (contOf_Description001_Elm.isEmpty()) {
						contOf_Description001_Elm = null;
					}
				}
				List<Element> _ItemNoUnknownFormatNdList = XMLUtil.getChildElements(rowsetElm, "_ItemNoUnknownFormat");
				for (Element _ItemNoUnknownFormatElm : _ItemNoUnknownFormatNdList) {
					contOf_ItemNoUnknownFormatElm = _ItemNoUnknownFormatElm.getTextContent();
					if (contOf_ItemNoUnknownFormatElm.isEmpty()) {
						contOf_ItemNoUnknownFormatElm = null;
					}
				}
				List<Element> _Description001__NdList = XMLUtil.getChildElements(rowsetElm, "_Description001__");
				for (Element _Description001__Elm : _Description001__NdList) {
					contOf_Description001__Elm = _Description001__Elm.getTextContent();
					if (contOf_Description001__Elm.isEmpty()) {
						contOf_Description001__Elm = null;
					}
				}
				List<Element> _DocumentOrderInvoiceENdList = XMLUtil.getChildElements(rowsetElm,
						"_DocumentOrderInvoiceE");
				for (Element _DocumentOrderInvoiceEElm : _DocumentOrderInvoiceENdList) {
					contOf_DocumentOrderInvoiceEElm = _DocumentOrderInvoiceEElm.getTextContent();
					if (contOf_DocumentOrderInvoiceEElm.isEmpty()) {
						contOf_DocumentOrderInvoiceEElm = null;
					}
				}
				List<Element> _OrderTypeNdList = XMLUtil.getChildElements(rowsetElm, "_OrderType");
				for (Element _OrderTypeElm : _OrderTypeNdList) {
					contOf_OrderTypeElm = _OrderTypeElm.getTextContent();
					if (contOf_OrderTypeElm.isEmpty()) {
						contOf_OrderTypeElm = null;
					}
				}
				List<Element> _Description001___NdList = XMLUtil.getChildElements(rowsetElm, "_Description001___");
				for (Element _Description001___Elm : _Description001___NdList) {
					contOf_Description001___Elm = _Description001___Elm.getTextContent();
					if (contOf_Description001___Elm.isEmpty()) {
						contOf_Description001___Elm = null;
					}
				}
				List<Element> _CostCenterNdList = XMLUtil.getChildElements(rowsetElm, "_CostCenter");
				for (Element _CostCenterElm : _CostCenterNdList) {
					contOf_CostCenterElm = _CostCenterElm.getTextContent();
					if (contOf_CostCenterElm.isEmpty()) {
						contOf_CostCenterElm = null;
					}
				}
				List<Element> _Description001____NdList = XMLUtil.getChildElements(rowsetElm, "_Description001____");
				for (Element _Description001____Elm : _Description001____NdList) {
					contOf_Description001____Elm = _Description001____Elm.getTextContent();
					if (contOf_Description001____Elm.isEmpty()) {
						contOf_Description001____Elm = null;
					}
				}
				List<Element> _StatusCodeLastNdList = XMLUtil.getChildElements(rowsetElm, "_StatusCodeLast");
				for (Element _StatusCodeLastElm : _StatusCodeLastNdList) {
					contOf_StatusCodeLastElm = _StatusCodeLastElm.getTextContent();
					if (contOf_StatusCodeLastElm.isEmpty()) {
						contOf_StatusCodeLastElm = null;
					}
				}
				List<Element> _Description001_____NdList = XMLUtil.getChildElements(rowsetElm, "_Description001_____");
				for (Element _Description001_____Elm : _Description001_____NdList) {
					contOf_Description001_____Elm = _Description001_____Elm.getTextContent();
					if (contOf_Description001_____Elm.isEmpty()) {
						contOf_Description001_____Elm = null;
					}
				}
				List<Element> _StatusCodeNextNdList = XMLUtil.getChildElements(rowsetElm, "_StatusCodeNext");
				for (Element _StatusCodeNextElm : _StatusCodeNextNdList) {
					contOf_StatusCodeNextElm = _StatusCodeNextElm.getTextContent();
					if (contOf_StatusCodeNextElm.isEmpty()) {
						contOf_StatusCodeNextElm = null;
					}
				}
				List<Element> _Description001______NdList = XMLUtil.getChildElements(rowsetElm,
						"_Description001______");
				for (Element _Description001______Elm : _Description001______NdList) {
					contOf_Description001______Elm = _Description001______Elm.getTextContent();
					if (contOf_Description001______Elm.isEmpty()) {
						contOf_Description001______Elm = null;
					}
				}
				List<Element> _RelatedPoSoNumberNdList = XMLUtil.getChildElements(rowsetElm, "_RelatedPoSoNumber");
				for (Element _RelatedPoSoNumberElm : _RelatedPoSoNumberNdList) {
					contOf_RelatedPoSoNumberElm = _RelatedPoSoNumberElm.getTextContent();
					if (contOf_RelatedPoSoNumberElm.isEmpty()) {
						contOf_RelatedPoSoNumberElm = null;
					}
				}
				List<Element> _RelatedOrderTypeNdList = XMLUtil.getChildElements(rowsetElm, "_RelatedOrderType");
				for (Element _RelatedOrderTypeElm : _RelatedOrderTypeNdList) {
					contOf_RelatedOrderTypeElm = _RelatedOrderTypeElm.getTextContent();
					if (contOf_RelatedOrderTypeElm.isEmpty()) {
						contOf_RelatedOrderTypeElm = null;
					}
				}
				List<Element> _Description001_______NdList = XMLUtil.getChildElements(rowsetElm,
						"_Description001_______");
				for (Element _Description001_______Elm : _Description001_______NdList) {
					contOf_Description001_______Elm = _Description001_______Elm.getTextContent();
					if (contOf_Description001_______Elm.isEmpty()) {
						contOf_Description001_______Elm = null;
					}
				}
				List<Element> _StatusCodeLast_NdList = XMLUtil.getChildElements(rowsetElm, "_StatusCodeLast_");
				for (Element _StatusCodeLast_Elm : _StatusCodeLast_NdList) {
					contOf_StatusCodeLast_Elm = _StatusCodeLast_Elm.getTextContent();
					if (contOf_StatusCodeLast_Elm.isEmpty()) {
						contOf_StatusCodeLast_Elm = null;
					}
				}
				List<Element> _Description001________NdList = XMLUtil.getChildElements(rowsetElm,
						"_Description001________");
				for (Element _Description001________Elm : _Description001________NdList) {
					contOf_Description001________Elm = _Description001________Elm.getTextContent();
					if (contOf_Description001________Elm.isEmpty()) {
						contOf_Description001________Elm = null;
					}
				}
				List<Element> _StatusCodeNext_NdList = XMLUtil.getChildElements(rowsetElm, "_StatusCodeNext_");
				for (Element _StatusCodeNext_Elm : _StatusCodeNext_NdList) {
					contOf_StatusCodeNext_Elm = _StatusCodeNext_Elm.getTextContent();
					if (contOf_StatusCodeNext_Elm.isEmpty()) {
						contOf_StatusCodeNext_Elm = null;
					}
				}
				List<Element> _Description001_________NdList = XMLUtil.getChildElements(rowsetElm,
						"_Description001_________");
				for (Element _Description001_________Elm : _Description001_________NdList) {
					contOf_Description001_________Elm = _Description001_________Elm.getTextContent();
					if (contOf_Description001_________Elm.isEmpty()) {
						contOf_Description001_________Elm = null;
					}
				}
				List<Element> _OrderNumberNdList = XMLUtil.getChildElements(rowsetElm, "_OrderNumber");
				for (Element _OrderNumberElm : _OrderNumberNdList) {
					contOf_OrderNumberElm = _OrderNumberElm.getTextContent();
					if (contOf_OrderNumberElm.isEmpty()) {
						contOf_OrderNumberElm = null;
					}
				}
				List<Element> _AsOfDateNdList = XMLUtil.getChildElements(rowsetElm, "_AsOfDate");
				for (Element _AsOfDateElm : _AsOfDateNdList) {
					contOf_AsOfDateElm = _AsOfDateElm.getTextContent();
					if (contOf_AsOfDateElm.isEmpty()) {
						contOf_AsOfDateElm = null;
					}
				}
				List<Element> _BackordersOlderThanReqDateNdList = XMLUtil.getChildElements(rowsetElm,
						"_BackordersOlderThanReqDate");
				for (Element _BackordersOlderThanReqDateElm : _BackordersOlderThanReqDateNdList) {
					contOf_BackordersOlderThanReqDateElm = _BackordersOlderThanReqDateElm.getTextContent();
					if (contOf_BackordersOlderThanReqDateElm.isEmpty()) {
						contOf_BackordersOlderThanReqDateElm = null;
					}
				}
				List<Element> _DateTransactionJulianNdList = XMLUtil.getChildElements(rowsetElm,
						"_DateTransactionJulian");
				for (Element _DateTransactionJulianElm : _DateTransactionJulianNdList) {
					contOf_DateTransactionJulianElm = _DateTransactionJulianElm.getTextContent();
					if (contOf_DateTransactionJulianElm.isEmpty()) {
						contOf_DateTransactionJulianElm = null;
					}
				}
				List<Element> _DateRequestedJulianNdList = XMLUtil.getChildElements(rowsetElm, "_DateRequestedJulian");
				for (Element _DateRequestedJulianElm : _DateRequestedJulianNdList) {
					contOf_DateRequestedJulianElm = _DateRequestedJulianElm.getTextContent();
					if (contOf_DateRequestedJulianElm.isEmpty()) {
						contOf_DateRequestedJulianElm = null;
					}
				}
				List<Element> _ScheduledPickDateNdList = XMLUtil.getChildElements(rowsetElm, "_ScheduledPickDate");
				for (Element _ScheduledPickDateElm : _ScheduledPickDateNdList) {
					contOf_ScheduledPickDateElm = _ScheduledPickDateElm.getTextContent();
					if (contOf_ScheduledPickDateElm.isEmpty()) {
						contOf_ScheduledPickDateElm = null;
					}
				}
				List<Element> _ScheduledPickDate_NdList = XMLUtil.getChildElements(rowsetElm, "_ScheduledPickDate_");
				for (Element _ScheduledPickDate_Elm : _ScheduledPickDate_NdList) {
					contOf_ScheduledPickDate_Elm = _ScheduledPickDate_Elm.getTextContent();
					if (contOf_ScheduledPickDate_Elm.isEmpty()) {
						contOf_ScheduledPickDate_Elm = null;
					}
				}
				List<Element> _PeriodNoGeneralLedgeNdList = XMLUtil.getChildElements(rowsetElm,
						"_PeriodNoGeneralLedge");
				for (Element _PeriodNoGeneralLedgeElm : _PeriodNoGeneralLedgeNdList) {
					contOf_PeriodNoGeneralLedgeElm = _PeriodNoGeneralLedgeElm.getTextContent();
					if (contOf_PeriodNoGeneralLedgeElm.isEmpty()) {
						contOf_PeriodNoGeneralLedgeElm = null;
					}
				}
				List<Element> _YearStringNdList = XMLUtil.getChildElements(rowsetElm, "_YearString");
				for (Element _YearStringElm : _YearStringNdList) {
					contOf_YearStringElm = _YearStringElm.getTextContent();
					if (contOf_YearStringElm.isEmpty()) {
						contOf_YearStringElm = null;
					}
				}
				List<Element> _UnitOfMeasureAsInputNdList = XMLUtil.getChildElements(rowsetElm,
						"_UnitOfMeasureAsInput");
				for (Element _UnitOfMeasureAsInputElm : _UnitOfMeasureAsInputNdList) {
					contOf_UnitOfMeasureAsInputElm = _UnitOfMeasureAsInputElm.getTextContent();
					if (contOf_UnitOfMeasureAsInputElm.isEmpty()) {
						contOf_UnitOfMeasureAsInputElm = null;
					}
				}
				List<Element> _Description001__________NdList = XMLUtil.getChildElements(rowsetElm,
						"_Description001__________");
				for (Element _Description001__________Elm : _Description001__________NdList) {
					contOf_Description001__________Elm = _Description001__________Elm.getTextContent();
					if (contOf_Description001__________Elm.isEmpty()) {
						contOf_Description001__________Elm = null;
					}
				}
				List<Element> _UnitsTransactionQtyNdList = XMLUtil.getChildElements(rowsetElm, "_UnitsTransactionQty");
				for (Element _UnitsTransactionQtyElm : _UnitsTransactionQtyNdList) {
					contOf_UnitsTransactionQtyElm = _UnitsTransactionQtyElm.getTextContent();
					if (contOf_UnitsTransactionQtyElm.isEmpty()) {
						contOf_UnitsTransactionQtyElm = null;
					}
				}
				List<Element> _UnitsQuantityShippedNdList = XMLUtil.getChildElements(rowsetElm,
						"_UnitsQuantityShipped");
				for (Element _UnitsQuantityShippedElm : _UnitsQuantityShippedNdList) {
					contOf_UnitsQuantityShippedElm = _UnitsQuantityShippedElm.getTextContent();
					if (contOf_UnitsQuantityShippedElm.isEmpty()) {
						contOf_UnitsQuantityShippedElm = null;
					}
				}
				List<Element> _UnitsQuanBackorHeldNdList = XMLUtil.getChildElements(rowsetElm, "_UnitsQuanBackorHeld");
				for (Element _UnitsQuanBackorHeldElm : _UnitsQuanBackorHeldNdList) {
					contOf_UnitsQuanBackorHeldElm = _UnitsQuanBackorHeldElm.getTextContent();
					if (contOf_UnitsQuanBackorHeldElm.isEmpty()) {
						contOf_UnitsQuanBackorHeldElm = null;
					}
				}
				List<Element> _UnitsLineItemQtyReNdList = XMLUtil.getChildElements(rowsetElm, "_UnitsLineItemQtyRe");
				for (Element _UnitsLineItemQtyReElm : _UnitsLineItemQtyReNdList) {
					contOf_UnitsLineItemQtyReElm = _UnitsLineItemQtyReElm.getTextContent();
					if (contOf_UnitsLineItemQtyReElm.isEmpty()) {
						contOf_UnitsLineItemQtyReElm = null;
					}
				}
				List<Element> _UnitsOpenQuantityNdList = XMLUtil.getChildElements(rowsetElm, "_UnitsOpenQuantity");
				for (Element _UnitsOpenQuantityElm : _UnitsOpenQuantityNdList) {
					contOf_UnitsOpenQuantityElm = _UnitsOpenQuantityElm.getTextContent();
					if (contOf_UnitsOpenQuantityElm.isEmpty()) {
						contOf_UnitsOpenQuantityElm = null;
					}
				}
				List<Element> _CurrencyCodeBaseNdList = XMLUtil.getChildElements(rowsetElm, "_CurrencyCodeBase");
				for (Element _CurrencyCodeBaseElm : _CurrencyCodeBaseNdList) {
					contOf_CurrencyCodeBaseElm = _CurrencyCodeBaseElm.getTextContent();
					if (contOf_CurrencyCodeBaseElm.isEmpty()) {
						contOf_CurrencyCodeBaseElm = null;
					}
				}
				List<Element> _Description001___________NdList = XMLUtil.getChildElements(rowsetElm,
						"_Description001___________");
				for (Element _Description001___________Elm : _Description001___________NdList) {
					contOf_Description001___________Elm = _Description001___________Elm.getTextContent();
					if (contOf_Description001___________Elm.isEmpty()) {
						contOf_Description001___________Elm = null;
					}
				}
				List<Element> _AmountExtendedPriceNdList = XMLUtil.getChildElements(rowsetElm, "_AmountExtendedPrice");
				for (Element _AmountExtendedPriceElm : _AmountExtendedPriceNdList) {
					contOf_AmountExtendedPriceElm = _AmountExtendedPriceElm.getTextContent();
					if (contOf_AmountExtendedPriceElm.isEmpty()) {
						contOf_AmountExtendedPriceElm = null;
					}
				}
				List<Element> _CurrencyCodeFromNdList = XMLUtil.getChildElements(rowsetElm, "_CurrencyCodeFrom");
				for (Element _CurrencyCodeFromElm : _CurrencyCodeFromNdList) {
					contOf_CurrencyCodeFromElm = _CurrencyCodeFromElm.getTextContent();
					if (contOf_CurrencyCodeFromElm.isEmpty()) {
						contOf_CurrencyCodeFromElm = null;
					}
				}
				List<Element> _AmtPricePerUnit2NdList = XMLUtil.getChildElements(rowsetElm, "_AmtPricePerUnit2");
				for (Element _AmtPricePerUnit2Elm : _AmtPricePerUnit2NdList) {
					contOf_AmtPricePerUnit2Elm = _AmtPricePerUnit2Elm.getTextContent();
					if (contOf_AmtPricePerUnit2Elm.isEmpty()) {
						contOf_AmtPricePerUnit2Elm = null;
					}
				}
				List<Element> _AmountReceivedNdList = XMLUtil.getChildElements(rowsetElm, "_AmountReceived");
				for (Element _AmountReceivedElm : _AmountReceivedNdList) {
					contOf_AmountReceivedElm = _AmountReceivedElm.getTextContent();
					if (contOf_AmountReceivedElm.isEmpty()) {
						contOf_AmountReceivedElm = null;
					}
				}
				List<Element> _AmountOpen1NdList = XMLUtil.getChildElements(rowsetElm, "_AmountOpen1");
				for (Element _AmountOpen1Elm : _AmountOpen1NdList) {
					contOf_AmountOpen1Elm = _AmountOpen1Elm.getTextContent();
					if (contOf_AmountOpen1Elm.isEmpty()) {
						contOf_AmountOpen1Elm = null;
					}
				}
				List<Element> _BuyerNumberNdList = XMLUtil.getChildElements(rowsetElm, "_BuyerNumber");
				for (Element _BuyerNumberElm : _BuyerNumberNdList) {
					contOf_BuyerNumberElm = _BuyerNumberElm.getTextContent();
					if (contOf_BuyerNumberElm.isEmpty()) {
						contOf_BuyerNumberElm = null;
					}
				}
				List<Element> _Description001____________NdList = XMLUtil.getChildElements(rowsetElm,
						"_Description001____________");
				for (Element _Description001____________Elm : _Description001____________NdList) {
					contOf_Description001____________Elm = _Description001____________Elm.getTextContent();
					if (contOf_Description001____________Elm.isEmpty()) {
						contOf_Description001____________Elm = null;
					}
				}
				List<Element> _UnitOfMeasurePrimaryNdList = XMLUtil.getChildElements(rowsetElm,
						"_UnitOfMeasurePrimary");
				for (Element _UnitOfMeasurePrimaryElm : _UnitOfMeasurePrimaryNdList) {
					contOf_UnitOfMeasurePrimaryElm = _UnitOfMeasurePrimaryElm.getTextContent();
					if (contOf_UnitOfMeasurePrimaryElm.isEmpty()) {
						contOf_UnitOfMeasurePrimaryElm = null;
					}
				}

				sections_Grid0_1_Builder.append(sectionId).append("|").append(type).append("|")
						.append(contOf_AddressNumberElm).append("|").append(contOf_Description001Elm).append("|")
						.append(contOf_PrimaryLastVendorNoElm).append("|").append(contOf_Description001_Elm).append("|")
						.append(contOf_ItemNoUnknownFormatElm).append("|").append(contOf_Description001__Elm)
						.append("|").append(contOf_DocumentOrderInvoiceEElm).append("|").append(contOf_OrderTypeElm)
						.append("|").append(contOf_Description001___Elm).append("|").append(contOf_CostCenterElm)
						.append("|").append(contOf_Description001____Elm).append("|").append(contOf_StatusCodeLastElm)
						.append("|").append(contOf_Description001_____Elm).append("|").append(contOf_StatusCodeNextElm)
						.append("|").append(contOf_Description001______Elm).append("|")
						.append(contOf_RelatedPoSoNumberElm).append("|").append(contOf_RelatedOrderTypeElm).append("|")
						.append(contOf_Description001_______Elm).append("|").append(contOf_StatusCodeLast_Elm)
						.append("|").append(contOf_Description001________Elm).append("|")
						.append(contOf_StatusCodeNext_Elm).append("|").append(contOf_Description001_________Elm)
						.append("|").append(contOf_OrderNumberElm).append("|").append(contOf_AsOfDateElm).append("|")
						.append(contOf_BackordersOlderThanReqDateElm).append("|")
						.append(contOf_DateTransactionJulianElm).append("|").append(contOf_DateRequestedJulianElm)
						.append("|").append(contOf_ScheduledPickDateElm).append("|")
						.append(contOf_ScheduledPickDate_Elm).append("|").append(contOf_PeriodNoGeneralLedgeElm)
						.append("|").append(contOf_YearStringElm).append("|").append(contOf_UnitOfMeasureAsInputElm)
						.append("|").append(contOf_Description001__________Elm).append("|")
						.append(contOf_UnitsTransactionQtyElm).append("|").append(contOf_UnitsQuantityShippedElm)
						.append("|").append(contOf_UnitsQuanBackorHeldElm).append("|")
						.append(contOf_UnitsLineItemQtyReElm).append("|").append(contOf_UnitsOpenQuantityElm)
						.append("|").append(contOf_CurrencyCodeBaseElm).append("|")
						.append(contOf_Description001___________Elm).append("|").append(contOf_AmountExtendedPriceElm)
						.append("|").append(contOf_CurrencyCodeFromElm).append("|").append(contOf_AmtPricePerUnit2Elm)
						.append("|").append(contOf_AmountReceivedElm).append("|").append(contOf_AmountOpen1Elm)
						.append("|").append(contOf_BuyerNumberElm).append("|")
						.append(contOf_Description001____________Elm).append("|").append(contOf_UnitOfMeasurePrimaryElm)
						.append("|").append(";\n");

			}

			String sections_Grid0_1TableQuery = "CREATE TABLE IF NOT EXISTS Sections_Grid0_1 ("
					+ "sectionId VARCHAR(100), " + "type VARCHAR(100), " + "_AddressNumber VARCHAR(100), "
					+ "_Description001 VARCHAR(100)," + "_PrimaryLastVendorNo VARCHAR(100),"
					+ "_Description001_ VARCHAR(100), " + "_ItemNoUnknownFormat VARCHAR(100), "
					+ "_Description001__ VARCHAR(100), " + "_DocumentOrderInvoiceE VARCHAR(100), "
					+ "_OrderType VARCHAR(100), " + "_Description001___ VARCHAR(100), " + "_CostCenter VARCHAR(100), "
					+ "_Description001____ VARCHAR(100), " + "_StatusCodeLast VARCHAR(100), "
					+ "_Description001_____ VARCHAR(100), " + "_StatusCodeNext VARCHAR(100), "
					+ "_Description001______ VARCHAR(100), " + "_RelatedPoSoNumber VARCHAR(100), "
					+ "_RelatedOrderType VARCHAR(100), " + "_Description001_______ VARCHAR(100), "
					+ "_StatusCodeLast_ VARCHAR(100), " + "_Description001________ VARCHAR(100), "
					+ "_StatusCodeNext_ VARCHAR(100), " + "_Description001_________ VARCHAR(100), "
					+ "_OrderNumber VARCHAR(100), " + "_AsOfDate VARCHAR(100), "
					+ "_BackordersOlderThanReqDate VARCHAR(100), " + "_DateTransactionJulian VARCHAR(100), "
					+ "_DateRequestedJulian VARCHAR(100), " + "_ScheduledPickDate VARCHAR(100), "
					+ "_ScheduledPickDate_ VARCHAR(100), " + "_PeriodNoGeneralLedge VARCHAR(100), "
					+ "_YearString VARCHAR(100), " + "_UnitOfMeasureAsInput VARCHAR(100), "
					+ "_Description001__________ VARCHAR(100), " + "_UnitsTransactionQty VARCHAR(100), "
					+ "_UnitsQuantityShipped VARCHAR(100), " + "_UnitsQuanBackorHeld VARCHAR(100), "
					+ "_UnitsLineItemQtyRe VARCHAR(100), " + "_UnitsOpenQuantity VARCHAR(100), "
					+ "_CurrencyCodeBase VARCHAR(100), " + "_Description001___________ VARCHAR(100), "
					+ "_AmountExtendedPrice VARCHAR(100), " + "_CurrencyCodeFrom VARCHAR(100), "
					+ "_AmtPricePerUnit2 VARCHAR(100), " + "_AmountReceived VARCHAR(100), "
					+ "_AmountOpen1 VARCHAR(100), " + "_BuyerNumber VARCHAR(100), "
					+ "_Description001____________ VARCHAR(100), " + "_UnitOfMeasurePrimary VARCHAR(100) " + ")";

			try (PreparedStatement sections_Grid0_1Stmt = databaseConnection()
					.prepareStatement(sections_Grid0_1TableQuery);) {
				sections_Grid0_1Stmt.executeUpdate();
			}

		}

		map.put("G1", g1Builder.toString());
		map.put("G2", g2Builder.toString());
		map.put("G3", g3Builder.toString());
		map.put("Details_BI_Server", details_BI_Server_Builder.toString());
		map.put("Budget_Essbase", budget_Essbase_Builder.toString());
		map.put("Sections", sections_System_Builder.toString());
		map.put("Grid0_1", sections_Grid0_1_Builder.toString());

		return map;

	}

	/*
	public void writeExcel(Sheet sheet, String[] data) { //int startRow =
	  sheet.getLastRowNum() + 1; int startRow = sheet.getLastRowNum();
	  
	  
	  
	  for (int dataInput = 0; dataInput < data.length; dataInput++) { String[]
	  rowContent = data[dataInput].split("\\|"); Row row = sheet.createRow(startRow
	  + dataInput); for (int i = 0; i < rowContent.length; i++) { Cell cell =
	  row.createCell(i); cell.setCellValue(rowContent[i]); } } }

	public void close(String outputDirectory, String fileName) {
		File directory = new File(outputDirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(directory, fileName + ".xlsx");

		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			workbook.write(outputStream);
		} catch (IOException e) {
			throw new RuntimeException("Error writing to workbook", e);
		}
	}
	*/
	/**
	 * 
	 * @param mapDoc
	 * @param outputFileLocation
	 * @throws Exception
	 */

	public static void extractMapData(Map<String, String> mapDoc, String outputFileLocation) throws Exception {

		String[] splitDatabase = mapDoc.get("G1").split(Pattern.quote(";"));
		writeCsv("G1", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("G2").split(Pattern.quote(";"));
		writeCsv("G2", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("G3").split(Pattern.quote(";"));
		writeCsv("G3", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Details_BI_Server").split(Pattern.quote(";"));
		writeCsv("Details_BI_Server", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Budget_Essbase").split(Pattern.quote(";"));
		writeCsv("Budget_Essbase", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Sections").split(Pattern.quote(";"));
		writeCsv("Sections", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Grid0_1").split(Pattern.quote(";"));
		writeCsv("Grid0_1", splitDatabase, outputFileLocation);

	}

	/**
	 * 
	 * @param csvfile
	 * @param data
	 * @param outputFileLocation
	 */
	public static void writeCsv(String csvfile, String[] data, String outputFileLocation) {

		try {

			File file = new File(outputFileLocation + "\\" + csvfile + ".csv");
			new File(file.getParent()).mkdirs();
			FileWriter fr = new FileWriter(file, true);
			PrintWriter printWriter = new PrintWriter(fr);

			for (int dataInput = 0; dataInput < data.length; dataInput++) {
				printWriter.write(data[dataInput]);
			}

			printWriter.flush();
			printWriter.close();
			// ZipUtil.pack(new File(outputFileLocation + "\\"), new File(outputFileLocation
			// + ".zip"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	private static void createTables(Connection connection) throws SQLException {

		/*
		 * String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" + "ID VARCHAR(100), "
		 * + "YEAR VARCHAR(100), " + "MONTH VARCHAR(100), " +
		 * "OPERATING_AIRLINE VARCHAR(100), " +
		 * "OPERATING_AIRLINE_IATA_CODE VARCHAR(100), " + "GEO_SUMMARY VARCHAR(100), " +
		 * "GEO_REGION VARCHAR(100), " + "ACTIVITY_TYPE_CODE VARCHAR(100), " +
		 * "PRICE_CATEGORY_CODE VARCHAR(100), " + "TERMINAL VARCHAR(100), " +
		 * "BOARDING_AREA VARCHAR(100), " + "CURRENT_COUNT VARCHAR(100), " +
		 * "PREVIOUS_COUNT VARCHAR(100), " + "CHANGE_COUNT VARCHAR(100), " +
		 * "PERCENT_CHANGE VARCHAR(100), " + ")";
		 * 
		 * 
		 * String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" +
		 * "P_CURR VARCHAR(100), " + "P_YEAR VARCHAR(100), " +
		 * "P_COMPANY VARCHAR(100), " + "P_ORG VARCHAR(100), " +
		 * "COMPANY VARCHAR(100), " + "ORGANIZATION VARCHAR(100), " +
		 * "DEPARTMENT VARCHAR(100), " + "OFFICE VARCHAR(100), " +
		 * "PER_NAME_YEAR VARCHAR(100), " + "PER_NAME_QTR VARCHAR(100), " +
		 * "USD_REVENUE VARCHAR(100), " + ")";
		 * 
		 * 
		 * String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" +
		 * "P_YEAR VARCHAR(100), " + "P_COMPANY VARCHAR(100), " + "P_ORG VARCHAR(100), "
		 * + "REVENUE VARCHAR(100), " + "TARGET_REVENUE VARCHAR(100), " +
		 * "COMPANY VARCHAR(100), " + "ORGANIZATION VARCHAR(100), " +
		 * "DEPARTMENT VARCHAR(100), " + "OFFICE VARCHAR(100), " +
		 * "BRAND VARCHAR(100), " + "PER_NAME_YEAR VARCHAR(100), " +
		 * "PER_NAME_QTR VARCHAR(100), " + "LOB VARCHAR(100), " + ")";
		 * 
		 * 
		 * 
		 * String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" +
		 * "BRAND VARCHAR(100), " + "PRODUCT VARCHAR(100), " + "LOB VARCHAR(100), " +
		 * "BILLED_QUANTITY VARCHAR(100), " + "PRODUCT_TYPE VARCHAR(100), " +
		 * "CALENDAR_DATE VARCHAR(100), " + "REVENUE VARCHAR(100), " +
		 * "BRAND_REVENUE_TOTAL VARCHAR(100), " + "GRAND_TOTAL_REVENUE VARCHAR(100), " +
		 * ")";
		 * 
		 * 
		 * String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" +
		 * "P_CUSTID VARCHAR(100), " + "P_ORDID VARCHAR(100), " +
		 * "CUSTOMER_NAME VARCHAR(100), " + "CUSTOMER_ID VARCHAR(100), " +
		 * "STREET_ADDRESS VARCHAR(100), " + "CITY VARCHAR(100), " +
		 * "STATE_PROVINCE VARCHAR(100), " + "POSTAL_CODE VARCHAR(100), " +
		 * "COUNTRY_NAME VARCHAR(100), " + "PRIMARY_PHONE_NUMBER VARCHAR(100), " +
		 * "CUST_EMAIL VARCHAR(100), " + "G2_CUSTOMER_ID VARCHAR(100), " +
		 * "ORDER_ID VARCHAR(100), " + "ORDER_MODE VARCHAR(100), " +
		 * "ORDER_DATE VARCHAR(100), " + "LINE_ITEM_ID VARCHAR(100), " +
		 * "UNIT_PRICE VARCHAR(100), " + "QUANTITY VARCHAR(100), " +
		 * "PRODUCT_NAME VARCHAR(100), " + "PRODUCT_DESCRIPTION VARCHAR(100), " +
		 * "ORDER_STATUS VARCHAR(100), " + "ORDER_TOTAL VARCHAR(100), " +
		 * "LINE_TOTAL VARCHAR(100), " + "FRMTD_ORDER_DATE VARCHAR(100), " +
		 * "CITY_STATE_ZIP VARCHAR(100), " + "CUST_TOTAL VARCHAR(100) " + ")";
		 * 
		 * String g1TableQuery = "CREATE TABLE IF NOT EXISTS G1 (" +
		 * "YEAR VARCHAR(100), " + "QTR VARCHAR(100), " + "REVENUE VARCHAR(100), " +
		 * ")";
		 */

		String g2TableQuery = "CREATE TABLE IF NOT EXISTS G2 (" + "YEAR VARCHAR(100), " + "BRAND VARCHAR(100), "
				+ "QUANTITY VARCHAR(100), " + "REVENUE  VARCHAR(100) " + ")";

		String g3TableQuery = "CREATE TABLE IF NOT EXISTS G3 (" + "P_YEAR VARCHAR(100), " + "P_COMPANY VARCHAR(100), "
				+ "P_ORG VARCHAR(100), " + "P_DEPT VARCHAR(100), " + "P_OFFICE VARCHAR(100), "
				+ "COMPANY VARCHAR(100), " + "ORGANIZATION VARCHAR(100), " + "DEPARTMENT VARCHAR(100), "
				+ "OFFICE VARCHAR(100), " + "REVENUE VARCHAR(100), " + "TARGET_REVENUE VARCHAR(100), "
				+ "PER_NAME_YEAR VARCHAR(100), " + "BRAND VARCHAR(100), " + "PRODUCT_TYPE VARCHAR(100), "
				+ "PRODUCT VARCHAR(100) " + ")";

		String details_BI_Server_TableQuery = "CREATE TABLE IF NOT EXISTS Details_BI_Server (" + "Brand VARCHAR(100), "
				+ "Order_Number VARCHAR(100), " + "Product_Description VARCHAR(100), " + "Paid_Date VARCHAR(100), "
				+ "Units VARCHAR(100), " + "Order_Status VARCHAR(100), " + "Revenue VARCHAR(100) " + ")";

		String budget_Essbase_TableQuery = "CREATE TABLE IF NOT EXISTS Budget_Essbase ("
				+ "Budget_Revenue VARCHAR(100), " + "Actual_Revenue VARCHAR(100), " + "Quarter VARCHAR(100), "
				+ "Brand VARCHAR(100) " + ")";

		String sections_System_TableQuery = "CREATE TABLE IF NOT EXISTS Sections_System (" + "report VARCHAR(100), "
				+ "user VARCHAR(100), " + "app VARCHAR(100), " + "form VARCHAR(100), " + "version VARCHAR(100), "
				+ "enviroment VARCHAR(100), " + "date VARCHAR(100), " + "time VARCHAR(100) " + ")";

		/*
		 * String sections_Grid0_1TableQuery =
		 * "CREATE TABLE IF NOT EXISTS Sections_Grid0_1 (" + "sectionId VARCHAR(100), "
		 * + "type VARCHAR(100), " + "_ReportCodeAddBook001 VARCHAR(100), " +
		 * "_Description001 VARCHAR(100)," + "_CollectionManager VARCHAR(100)," +
		 * "_Description001_ VARCHAR(100), " + "_CreditManager VARCHAR(100), " +
		 * "_Description001__ VARCHAR(100), " + "_PayorAddressNumber VARCHAR(100), " +
		 * "_Description001___ VARCHAR(100), " + "_AddressNumber VARCHAR(100), " +
		 * "_NameAlpha VARCHAR(100), " + "_Company VARCHAR(100), " +
		 * "_Description001____ VARCHAR(100), " + "_DateInvoiceJ VARCHAR(100), " +
		 * "_DateForGLandVoucherJULIA VARCHAR(100), " + "_YearString VARCHAR(100), " +
		 * "_PeriodNoGeneralLedge VARCHAR(100), " + "_DateDueJulian VARCHAR(100), " +
		 * "_AsOfDate VARCHAR(100), " + "_DocVoucherInvoiceE VARCHAR(100), " +
		 * "_DocumentType VARCHAR(100), " + "_Description001_____ VARCHAR(100), " +
		 * "_CompanyKey VARCHAR(100), " + "_DocumentPayItem VARCHAR(100), " +
		 * "_AmountGross VARCHAR(100), " + "_AmountOpen VARCHAR(100), " +
		 * "_AmtDiscountAvailable VARCHAR(100), " +
		 * "_DateDiscountDueJulian VARCHAR(100), " + "_AmountFuture VARCHAR(100), " +
		 * "_CurrentAmountDue VARCHAR(100), " + "_AmtAgingCategories1 VARCHAR(100), " +
		 * "_AmtAgingCategories2 VARCHAR(100), " + "_AmtAgingCategories3 VARCHAR(100), "
		 * + "_AmtAgingCategories4 VARCHAR(100), " +
		 * "_AmtAgingCategories5 VARCHAR(100), " + "_AmtAgingCategories6 VARCHAR(100), "
		 * + "_AmtAgingCategories7 VARCHAR(100), " + "_CurrencyCodeBase VARCHAR(100), "
		 * + "_Description001______ VARCHAR(100) " + ")";
		 * 
		 * 
		 * String sections_Grid0_1TableQuery =
		 * "CREATE TABLE IF NOT EXISTS Sections_Grid0_1 (" + "sectionId VARCHAR(100), "
		 * + "type VARCHAR(100), " + "_AddressNumber VARCHAR(100), " +
		 * "_Description001 VARCHAR(100)," + "_PrimaryLastVendorNo VARCHAR(100)," +
		 * "_Description001_ VARCHAR(100), " + "_ItemNoUnknownFormat VARCHAR(100), " +
		 * "_Description001__ VARCHAR(100), " + "_DocumentOrderInvoiceE VARCHAR(100), "
		 * + "_OrderType VARCHAR(100), " + "_Description001___ VARCHAR(100), " +
		 * "_CostCenter VARCHAR(100), " + "_Description001____ VARCHAR(100), " +
		 * "_StatusCodeLast VARCHAR(100), " + "_Description001_____ VARCHAR(100), " +
		 * "_StatusCodeNext VARCHAR(100), " + "_Description001______ VARCHAR(100), " +
		 * "_RelatedPoSoNumber VARCHAR(100), " + "_RelatedOrderType VARCHAR(100), " +
		 * "_Description001_______ VARCHAR(100), " + "_StatusCodeLast_ VARCHAR(100), " +
		 * "_Description001________ VARCHAR(100), " + "_StatusCodeNext_ VARCHAR(100), "
		 * + "_Description001_________ VARCHAR(100), " + "_OrderNumber VARCHAR(100), " +
		 * "_AsOfDate VARCHAR(100), " + "_BackordersOlderThanReqDate VARCHAR(100), " +
		 * "_DateTransactionJulian VARCHAR(100), " +
		 * "_DateRequestedJulian VARCHAR(100), " + "_ScheduledPickDate VARCHAR(100), " +
		 * "_ScheduledPickDate_ VARCHAR(100), " + "_PeriodNoGeneralLedge VARCHAR(100)" +
		 * "_YearString VARCHAR(100), " + "_UnitOfMeasureAsInput VARCHAR(100), " +
		 * "_Description001__________ VARCHAR(100), " +
		 * "_UnitsTransactionQty VARCHAR(100), " +
		 * "_UnitsQuantityShipped VARCHAR(100), " +
		 * "_UnitsQuanBackorHeld VARCHAR(100), " + "_UnitsLineItemQtyRe VARCHAR(100), "
		 * + "_UnitsOpenQuantity VARCHAR(100), " + "_CurrencyCodeBase VARCHAR(100), " +
		 * "_Description001___________ VARCHAR(100), " +
		 * "_AmountExtendedPrice VARCHAR(100), " + "_CurrencyCodeFrom VARCHAR(100), " +
		 * "_AmtPricePerUnit2 VARCHAR(100), " + "_AmountReceived VARCHAR(100), " +
		 * "_AmountOpen1 VARCHAR(100), " + "_BuyerNumber VARCHAR(100), " +
		 * "_Description001____________ VARCHAR(100), " +
		 * "_UnitOfMeasurePrimary VARCHAR(100), " + ")";
		 * 
		 */

		try (
				// PreparedStatement g1Stmt = connection.prepareStatement(g1TableQuery);
				PreparedStatement g2Stmt = connection.prepareStatement(g2TableQuery);
				PreparedStatement g3Stmt = connection.prepareStatement(g3TableQuery);
				PreparedStatement details_BI_ServerStmt = connection.prepareStatement(details_BI_Server_TableQuery);
				PreparedStatement budget_EssbaseStmt = connection.prepareStatement(budget_Essbase_TableQuery);
				PreparedStatement sections_SystemStmt = connection.prepareStatement(sections_System_TableQuery);
		// PreparedStatement sections_Grid0_1Stmt =
		// connection.prepareStatement(sections_Grid0_1TableQuery);

		) {
			// g1Stmt.executeUpdate();
			g2Stmt.executeUpdate();
			g3Stmt.executeUpdate();
			details_BI_ServerStmt.executeUpdate();
			budget_EssbaseStmt.executeUpdate();
			sections_SystemStmt.executeUpdate();
			// sections_Grid0_1Stmt.executeUpdate();

		}

	}

	/**
	 * 
	 * @param conn
	 * @param mapDoc
	 * @throws Exception
	 */
	public static void extractMapDataForDb(Connection conn, Map<String, String> mapDoc) throws Exception {

		String[] splitDatabase = mapDoc.get("G1").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "G1", splitDatabase);

		splitDatabase = mapDoc.get("G2").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "G2", splitDatabase);

		splitDatabase = mapDoc.get("G3").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "G3", splitDatabase);

		splitDatabase = mapDoc.get("Details_BI_Server").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Details_BI_Server", splitDatabase);

		splitDatabase = mapDoc.get("Budget_Essbase").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Budget_Essbase", splitDatabase);

		splitDatabase = mapDoc.get("Sections").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Sections", splitDatabase);

		splitDatabase = mapDoc.get("Grid0_1").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Grid0_1", splitDatabase);

	}

	/**
	 * 
	 * @param conn
	 * @param dbTableName
	 * @param data
	 * @throws Exception
	 */
	public static void insertDataInDB(Connection conn, String dbTableName, String[] data) throws Exception {

		try {
			String[] header = data[0].split(Pattern.quote("|"));
			StringBuilder headerBuilder = new StringBuilder();
			headerBuilder.append("(");

			StringBuilder valueBuilder = new StringBuilder();
			valueBuilder.append("(");

			for (int i = 0; i < header.length; i++) {
				headerBuilder.append(header[i]);
				valueBuilder.append("?");
				if (i != header.length - 1) {
					headerBuilder.append(",");
					valueBuilder.append(",");
				}
			}
			headerBuilder.append(")");
			valueBuilder.append(")");

			// Query to delete all records in a table
			String deleteQuery = "Truncate table" + " " + dbTableName;

			String query = "INSERT INTO " + dbTableName + headerBuilder.toString() + " VALUES "
					+ valueBuilder.toString();

			// // Executing the query
			PreparedStatement deletePstmt = conn.prepareStatement(deleteQuery);
			deletePstmt.executeUpdate();

			PreparedStatement pstmt = conn.prepareStatement(query);
			// skipping first line as header
			for (int dataInput = 1; dataInput < data.length; dataInput++) {
				String[] splitFtrData = data[dataInput].split(Pattern.quote("|"));
				for (int splitData = 0; splitData < splitFtrData.length; splitData++) {
					pstmt.setString(splitData + 1, splitFtrData[splitData]);

				}
				pstmt.executeUpdate();
			}

		}

		catch (Exception e) {
			System.out.println("failure:" + e.getMessage());
		}

	}

}

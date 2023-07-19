package com.samp_products_d;

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

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.samp_products_d.XMLUtil;

/**
 * The class SAMP_PRODUCTS_D_Parsing_Service
 * 
 * @author 10719785
 */
public class SAMP_PRODUCTS_D_ParsingService {

	/**
	 *
	 * Main
	 *
	 * @param args[]
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File file = new File(
				"C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\samp_products_d\\sample%2exml");
		
		/*
		File file = new File(
				"C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\samp_products_d\\products_d%2exml");
		
		File file = new File(
				"C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\samp_products_d\\_xdo_local%2eproducts_d%2exmly);
		*/
		
		String absolutePath = file.getAbsolutePath();

		String outputFileLocation = "C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\samp_products_d\\Samp_Products_D";

		Document doc = readXMLDocumentFromFile(absolutePath);
		Map<String, String> parse = parseXmlDoc(doc);

		extractMapData(parse, outputFileLocation);

		// System.out.println(parse);

		for (Map.Entry<String, String> entry : parse.entrySet()) {
			System.out.println(entry.getKey() + ":" + "\n");
			System.out.println(entry.getValue());
		}

		Connection conn = databaseConnection();
		extractMapDataForDb(conn, parse);
	}

	/**
	 *
	 * Database Connection
	 *
	 * @param
	 * @return Connection
	 * @throws SQLException
	 */
	public static Connection databaseConnection() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sampleproductsDB?useSSL=false",
				"root", "root123");
		createTables(conn);
		return conn;
	}

	/**
	 *
	 * Read XML document from file
	 *
	 * @param fileNameWithPath {@link String}
	 * @return Document
	 * @throws Exception
	 */
	public static Document readXMLDocumentFromFile(String fileNameWithPath) throws Exception {

		// Get Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Build Document
		Document document = builder.parse(new File(fileNameWithPath));

		// Normalize the XML Structure; It's just too important !!
		document.getDocumentElement().normalize();
		return document;
	}

	/**
	 *
	 * Parse xml document
	 *
	 * @param doc {@link Document}
	 * @return Map<String, String>
	 * @throws Exception
	 */
	public static Map<String, String> parseXmlDoc(Document doc) throws Exception {

		Map<String, String> map = new HashMap<>();

		Element tableElm = doc.getDocumentElement();

		String isMulti = null;
		String contOfPROD_KEYElm = null;
		String contOfPROD_DSCElm = null;
		String contOfATTRIBUTE_2Elm = null;
		String contOfATTRIBUTE_1Elm = null;
		String contOfTYPEElm = null;
		String contOfLOBElm = null;
		String contOfBRANDElm = null;
		String contOfSEQUENCEElm = null;
		String contOfBRAND_KEYElm = null;
		String contOfLOB_KEYElm = null;
		String contOfTYPE_KEYElm = null;

		String tableName = tableElm.getAttribute("Name");
		String contOfTableElm = tableElm.getTextContent();

		StringBuilder samp_products_d_Builder = new StringBuilder();

		samp_products_d_Builder.append("TableName").append("|").append("PROD_KEY").append("|").append("PROD_DSC")
				.append("|").append("ATTRIBUTE_2").append("|").append("ATTRIBUTE_1").append("|").append("TYPE")
				.append("|").append("LOB").append("|").append("BRAND").append("|").append("SEQUENCE").append("|")
				.append("BRAND_KEY").append("|").append("LOB_KEY").append("|").append("TYPE_KEY").append("|")
				.append("isMulti").append("|").append(";\n");

		List<Element> samp_products_d_NdList = XMLUtil.getChildElements(tableElm, "SAMP_PRODUCTS_D");
		for (Element samp_products_d_Elm : samp_products_d_NdList) {

			isMulti = samp_products_d_Elm.getAttribute("IsMulti");
			if (isMulti.isEmpty()) {
				isMulti = null;
			}

			List<Element> pROD_KEYNdList = XMLUtil.getChildElements(samp_products_d_Elm, "PROD_KEY");
			for (Element pROD_KEYElm : pROD_KEYNdList) {

				contOfPROD_KEYElm = pROD_KEYElm.getTextContent();

			}
			List<Element> pROD_DSCNdList = XMLUtil.getChildElements(samp_products_d_Elm, "PROD_DSC");
			for (Element pROD_DSCElm : pROD_DSCNdList) {
				contOfPROD_DSCElm = pROD_DSCElm.getTextContent();

			}
			List<Element> aTTRIBUTE_2NdList = XMLUtil.getChildElements(samp_products_d_Elm, "ATTRIBUTE_2");
			for (Element aTTRIBUTE_2Elm : aTTRIBUTE_2NdList) {
				contOfATTRIBUTE_2Elm = aTTRIBUTE_2Elm.getTextContent();

			}
			List<Element> aTTRIBUTE_1NdList = XMLUtil.getChildElements(samp_products_d_Elm, "ATTRIBUTE_1");
			for (Element aTTRIBUTE_1Elm : aTTRIBUTE_1NdList) {
				contOfATTRIBUTE_1Elm = aTTRIBUTE_1Elm.getTextContent();

			}
			List<Element> tYPENdList = XMLUtil.getChildElements(samp_products_d_Elm, "TYPE");
			for (Element tYPEElm : tYPENdList) {

				contOfTYPEElm = tYPEElm.getTextContent();

			}
			List<Element> lOBNdList = XMLUtil.getChildElements(samp_products_d_Elm, "LOB");
			for (Element lOBElm : lOBNdList) {

				contOfLOBElm = lOBElm.getTextContent();
			}
			List<Element> bRANDNdList = XMLUtil.getChildElements(samp_products_d_Elm, "BRAND");
			for (Element bRANDElm : bRANDNdList) {

				contOfBRANDElm = bRANDElm.getTextContent();

			}
			List<Element> sEQUENCENdList = XMLUtil.getChildElements(samp_products_d_Elm, "SEQUENCE");
			for (Element sEQUENCEElm : sEQUENCENdList) {
				contOfSEQUENCEElm = sEQUENCEElm.getTextContent();

			}
			List<Element> bRAND_KEYNdList = XMLUtil.getChildElements(samp_products_d_Elm, "BRAND_KEY");
			for (Element bRAND_KEYElm : bRAND_KEYNdList) {

				contOfBRAND_KEYElm = bRAND_KEYElm.getTextContent();
			}
			List<Element> lOB_KEYNdList = XMLUtil.getChildElements(samp_products_d_Elm, "LOB_KEY");
			for (Element lOB_KEYElm : lOB_KEYNdList) {
				contOfLOB_KEYElm = lOB_KEYElm.getTextContent();

			}
			List<Element> tYPE_KEYNdList = XMLUtil.getChildElements(samp_products_d_Elm, "TYPE_KEY");
			for (Element tYPE_KEYElm : tYPE_KEYNdList) {

				contOfTYPE_KEYElm = tYPE_KEYElm.getTextContent();
			}

			samp_products_d_Builder.append(tableName).append("|").append(contOfPROD_KEYElm).append("|")
					.append(contOfPROD_DSCElm).append("|").append(contOfATTRIBUTE_2Elm).append("|")
					.append(contOfATTRIBUTE_1Elm).append("|").append(contOfTYPEElm).append("|").append(contOfLOBElm)
					.append("|").append(contOfBRANDElm).append("|").append(contOfSEQUENCEElm).append("|")
					.append(contOfBRAND_KEYElm).append("|").append(contOfLOB_KEYElm).append("|")
					.append(contOfTYPE_KEYElm).append("|").append(isMulti).append("|").append(";\n");

		}
		map.put("SAMP_PRODUCTS_D", samp_products_d_Builder.toString());

		return map;
	}

	/**
	 *
	 * Extract map data
	 *
	 * @param mapDoc             {@link Map<String, String>}
	 * @param outputFileLocation {@link String}
	 * @throws Exception
	 */
	public static void extractMapData(Map<String, String> mapDoc, String outputFileLocation) throws Exception {

		String[] splitDatabase = mapDoc.get("SAMP_PRODUCTS_D").split(Pattern.quote(";"));
		writeCsv("SAMP_PRODUCTS_D", splitDatabase, outputFileLocation);

	}

	/**
	 *
	 * Write csv
	 *
	 * @param csvfile            the csvfile.
	 * @param data               the data.
	 * @param outputFileLocation the output file location.
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
	 * Create tables
	 *
	 * @param connection {@link Connection}
	 * @throws SQLException
	 */
	private static void createTables(Connection connection) throws SQLException {

		String sampleProductsDTableQuery = "CREATE TABLE IF NOT EXISTS SAMP_PRODUCTS_D (" + "TableName VARCHAR(100), "
				+ "PROD_KEY INT, " + "PROD_DSC VARCHAR(100), " + "ATTRIBUTE_2 VARCHAR(100), "
				+ "ATTRIBUTE_1 VARCHAR(100), " + "TYPE VARCHAR(100), " + "LOB VARCHAR(100), " + "BRAND VARCHAR(100), "
				+ "SEQUENCE INT, " + "BRAND_KEY INT, " + "LOB_KEY INT, " + "TYPE_KEY INT, " + "isMulti VARCHAR(100), "
				+ "PRIMARY KEY (PROD_KEY)" + ")";

		try (PreparedStatement statement = connection.prepareStatement(sampleProductsDTableQuery)) {
			statement.executeUpdate();

		}

	}

	/**
	 *
	 * Extract map data for db
	 *
	 * @param conn   {@link String}
	 * @param mapDoc {@link Map<String, String>}
	 * @throws Exception
	 */
	public static void extractMapDataForDb(Connection conn, Map<String, String> mapDoc) throws Exception {

		String[] splitDatabase = mapDoc.get("SAMP_PRODUCTS_D").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "SAMP_PRODUCTS_D", splitDatabase);

	}

	/**
	 *
	 * Insert data in DB
	 *
	 * @param conn        {@link Connection}
	 * @param dbTableName {@link String}
	 * @param data        {@link String[]}
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

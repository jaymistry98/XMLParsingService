package com.report;

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
 * The class ReportParsingService
 * 
 * @author 10719785
 */
public class ReportParsingService {

	/**
	 *
	 * Main
	 *
	 * @param args[]
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//File file = new File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\report\\_report.xdo");
		
		File file = new File("C:\\Users\\jay\\eclipse-workspace\\ParsingService\\src\\com\\Published-Reports\\reports\\sales+performance+report%2exdo"
				+ "\\_report%2exdo");
		
		
		String absolutePath = file.getAbsolutePath();

		String outputFileLocation = "C:\\Users\\jay\\Desktop\\ReportCSV";

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
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/reportDB?useSSL=false", "root",
				"root123");
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

		Element reportElm = doc.getDocumentElement();
		StringBuilder reportBuilder = new StringBuilder();
		String cachePerUser = reportElm.getAttribute("cachePerUser");
		if (cachePerUser.isEmpty()) {
			cachePerUser = null;
		}
		String cacheSmartRefresh = reportElm.getAttribute("cacheSmartRefresh");
		if (cacheSmartRefresh.isEmpty()) {
			cacheSmartRefresh = null;
		}
		String cacheUserRefresh = reportElm.getAttribute("cacheUserRefresh");
		if (cacheUserRefresh.isEmpty()) {
			cacheUserRefresh = null;
		}
		String dataModel = reportElm.getAttribute("dataModel");
		if (dataModel.isEmpty()) {
			dataModel = null;
		}
		String producerName = reportElm.getAttribute("producerName");
		if (producerName.isEmpty()) {
			producerName = null;
		}
		String useBipParameters = reportElm.getAttribute("useBipParameters");
		if (useBipParameters.isEmpty()) {
			useBipParameters = null;
		}
		String version = reportElm.getAttribute("version");
		if (version.isEmpty()) {
			version = null;
		}
		String xmlns = reportElm.getAttribute("xmlns");
		if (xmlns.isEmpty()) {
			xmlns = null;
		}
		String xmlnsXsd = reportElm.getAttribute("xmlns:xsd");
		if (xmlnsXsd.isEmpty()) {
			xmlnsXsd = null;
		}
		String customDataControl = reportElm.getAttribute("customDataControl");
		if (customDataControl.isEmpty()) {
			customDataControl = null;
		}
		String parameterTaskFlow = reportElm.getAttribute("parameterTaskFlow");
		if (parameterTaskFlow.isEmpty()) {
			parameterTaskFlow = null;
		}
		String parameterVOName = reportElm.getAttribute("parameterVOName");
		if (parameterVOName.isEmpty()) {
			parameterVOName = null;
		}
		String contOfreportElm = reportElm.getTextContent();
		
		reportBuilder.append("xmlns").append("|").append("xmlnsXsd").append("|").append("version").append("|")
		.append("dataModel").append("|").append("useBipParameters").append("|").append("producerName")
		.append("|").append("parameterVOName").append("|").append("parameterTaskFlow").append("|")
		.append("customDataControl").append("|").append("cachePerUser").append("|").append("cacheSmartRefresh")
		.append("|").append("cacheUserRefresh").append("|")
		.append(";\n");

		reportBuilder.append(xmlns).append("|").append(xmlnsXsd).append("|").append(version).append("|")
		.append(dataModel).append("|").append(useBipParameters).append("|").append(producerName).append("|")
		.append(parameterVOName).append("|").append(parameterTaskFlow).append("|").append(customDataControl)
		.append("|").append(cachePerUser).append("|").append(cacheSmartRefresh).append("|")
		.append(cacheUserRefresh).append("|").append(";\n");
		/*
		reportBuilder.append("xmlns").append("|").append("xmlns:xsd").append("|").append("version").append("|")
				.append("dataModel").append("|").append("useBipParameters").append("|").append("producerName")
				.append("|").append("parameterVOName").append("|").append("parameterTaskFlow").append("|")
				.append("customDataControl").append("|").append("cachePerUser").append("|").append("cacheSmartRefresh")
				.append("|").append("cacheUserRefresh").append("|").append("contentsOfReportElement").append("|")
				.append(";\n");

		reportBuilder.append(xmlns).append("|").append(xmlnsXsd).append("|").append(version).append("|")
				.append(dataModel).append("|").append(useBipParameters).append("|").append(producerName).append("|")
				.append(parameterVOName).append("|").append(parameterTaskFlow).append("|").append(customDataControl)
				.append("|").append(cachePerUser).append("|").append(cacheSmartRefresh).append("|")
				.append(cacheUserRefresh).append("|").append(contOfreportElm).append("|").append(";\n");
		*/

		Element dataModelElm = XMLUtil.getChildElement(reportElm, "dataModel");
		StringBuilder dataModelBuilder = new StringBuilder();
		String url = dataModelElm.getAttribute("url");
		if (url.isEmpty()) {
			url = null;
		}
		String cache = dataModelElm.getAttribute("cache");
		if (cache.isEmpty()) {
			cache = null;
		}
		String contOfdataModelElm = dataModelElm.getTextContent();
		if (contOfdataModelElm.isEmpty()) {
			contOfdataModelElm = null;
		}

		dataModelBuilder.append("url").append("|").append("cache").append("|").append("contentsOfDataModelElement").append("|").append(";\n");
		dataModelBuilder.append(url).append("|").append(cache).append("|").append(contOfdataModelElm).append("|").append(";\n");

		Element descriptionElm = XMLUtil.getChildElement(reportElm, "description");
		StringBuilder descriptionBuilder = new StringBuilder();
		descriptionBuilder.append("contentsOfDescriptionElement").append("|").append(";\n");
		String contOfdescriptionElm = descriptionElm.getTextContent();
		if (contOfdescriptionElm.isEmpty()) {
			contOfdescriptionElm = null;
		}
		descriptionBuilder.append(contOfdescriptionElm).append("|").append(";\n");

		List<Element> propertyNdList = XMLUtil.getChildElements(reportElm, "property");
		StringBuilder propertyBuilder = new StringBuilder();
		propertyBuilder.append("name").append("|").append("value").append("|").append("IsMulti").append("|")
				.append(";\n");
		for (Element propertyElm : propertyNdList) {
			String isMulti = propertyElm.getAttribute("IsMulti");
			if (isMulti.isEmpty()) {
				isMulti = null;
			}
			String name = propertyElm.getAttribute("name");
			String value = propertyElm.getAttribute("value");
			if (value.isEmpty()) {
				value = null;
			}

			propertyBuilder.append(name).append("|").append(value).append("|").append(isMulti).append("|")
					.append(";\n");
		}

		/*
		Element parametersElm = XMLUtil.getChildElement(reportElm, "parameters");
		StringBuilder parametersBuilder = new StringBuilder();
		String paramPerLine = parametersElm.getAttribute("paramPerLine");
		if (paramPerLine.isEmpty()) {
			paramPerLine = null;
		}
		String style = parametersElm.getAttribute("style");
		if (style.isEmpty()) {
			style = null;
		}
		String contOfparametersElm = parametersElm.getTextContent();
		if (contOfparametersElm.isEmpty()) {
			contOfparametersElm = null;
		}

		parametersBuilder.append("paramPerLine").append("|").append("style").append("|")
				.append("contentsOfParametersElement").append("|").append(";\n");
		parametersBuilder.append(paramPerLine).append("|").append(style).append("|").append(contOfparametersElm)
				.append("|").append(";\n");
		
		*/
		
		
		Element parametersElm = XMLUtil.getChildElement(reportElm, "parameters");
		StringBuilder parametersBuilder = new StringBuilder();
		parametersBuilder.append("paramPerLine").append("|").append("style").append("|")
		.append("id").append("|").append("label").append("|").append("defaultValue").append("|")
		.append("visible").append("|").append("rowPlacement").append("|").append("isMulti").append("|").append("contOfRadioButtonElm").append("|").append(";\n");
		
		String contOfRadioButtonElm = null;

		String paramPerLine= parametersElm.getAttribute("paramPerLine");
		if (paramPerLine.isEmpty()) {
			paramPerLine = null;
		}
		String style= parametersElm.getAttribute("style");
		if (style.isEmpty()) {
			style = null;
		}
		String contOfparametersElm = parametersElm.getTextContent();
		if (contOfparametersElm.isEmpty()) {
			contOfparametersElm = null;
		}
		
		String parameterIsMulti = null;
		String defaultValue = null;
		String id = null;
		String parameterlabel = null;
		String rowPlacement = null;
		String visible = null;
		
		parametersBuilder.append(paramPerLine).append("|").append(style).append("|")
		.append(id).append("|").append(parameterlabel).append("|").append(defaultValue).append("|")
		.append(visible).append("|").append(rowPlacement).append("|").append(parameterIsMulti).append("|").append(contOfRadioButtonElm).append("|").append(";\n");
		
		List<Element> parameterNdList = XMLUtil.getChildElements(parametersElm, "parameter");
		for (Element parameterElm: parameterNdList) {

			parameterIsMulti= parameterElm.getAttribute("IsMulti");
			if (parameterIsMulti.isEmpty()) {
				parameterIsMulti = null;
			}
			defaultValue= parameterElm.getAttribute("defaultValue");
			if (defaultValue.isEmpty()) {
				defaultValue = null;
			}
			id= parameterElm.getAttribute("id");
			if (id.isEmpty()) {
				id = null;
			}
			parameterlabel= parameterElm.getAttribute("label");
			if (parameterlabel.isEmpty()) {
				parameterlabel = null;
			}
			rowPlacement= parameterElm.getAttribute("rowPlacement");
			if (rowPlacement.isEmpty()) {
				rowPlacement = null;
			}
			visible= parameterElm.getAttribute("visible");
			if (visible.isEmpty()) {
				visible = null;
			}

			List<Element> radioButtonNdList = XMLUtil.getChildElements(parameterElm, "radioButton");
			for (Element radioButtonElm: radioButtonNdList) {
				contOfRadioButtonElm = radioButtonElm.getTextContent();
				if (contOfRadioButtonElm.isEmpty()) {
					contOfRadioButtonElm = null;
				}
			}
			
			parametersBuilder.append(paramPerLine).append("|").append(style).append("|")
			.append(id).append("|").append(parameterlabel).append("|").append(defaultValue).append("|")
			.append(visible).append("|").append(rowPlacement).append("|").append(parameterIsMulti).append("|").append(contOfRadioButtonElm).append("|").append(";\n");
		}
		
		
		

		Element templatesElm = XMLUtil.getChildElement(reportElm, "templates");
		StringBuilder templatesBuilder = new StringBuilder();
		/*
		templatesBuilder.append("default").append("|").append("label").append("|").append("url").append("|")
				.append("type").append("|").append("outputFormat").append("|").append("defaultFormat").append("|")
				.append("active").append("|").append("viewOnline").append("|").append("IsMulti").append("|")
				.append("contentsOfTemplatesElement").append("|").append(";\n");
		*/
		templatesBuilder.append("defaultLabel").append("|").append("label").append("|").append("url").append("|")
		.append("type").append("|").append("outputFormat").append("|").append("defaultFormat").append("|")
		.append("locale").append("|").append("disableMasterTemplate").append("|").append("active").append("|").append("viewOnline").append("|").append("IsMulti").append("|")
		.append("contOfmasterTemplateElm").append("|").append(";\n");

		String defaultLabel = templatesElm.getAttribute("default");
		String contOftemplatesElm = templatesElm.getTextContent();
		
		Element masterTemplateElm = XMLUtil.getChildElement(templatesElm, "masterTemplate");
		String contOfmasterTemplateElm = masterTemplateElm.getTextContent();
		if (contOfmasterTemplateElm.isEmpty()) {
			contOfmasterTemplateElm = null;
		}
		
		List<Element> templateNdList = XMLUtil.getChildElements(templatesElm, "template");
		for (Element templateElm : templateNdList) {

			String isMulti = templateElm.getAttribute("IsMulti");
			if (isMulti.isEmpty()) {
				isMulti = null;
			}
			String active = templateElm.getAttribute("active");
			if (active.isEmpty()) {
				active = null;
			}
			String defaultFormat = templateElm.getAttribute("defaultFormat");
			if (defaultFormat.isEmpty()) {
				defaultFormat = null;
			}
			String label = templateElm.getAttribute("label");
			if (label.isEmpty()) {
				label = null;
			}
			String locale = templateElm.getAttribute("locale");
			if (locale.isEmpty()) {
				locale = null;
			}
			String outputFormat = templateElm.getAttribute("outputFormat");
			if (outputFormat.isEmpty()) {
				outputFormat = null;
			}
			String type = templateElm.getAttribute("type");
			if (type.isEmpty()) {
				type = null;
			}
			String urlTemplate = templateElm.getAttribute("url");
			if (urlTemplate.isEmpty()) {
				urlTemplate = null;
			}
			String viewOnline = templateElm.getAttribute("viewOnline");
			if (viewOnline.isEmpty()) {
				viewOnline = null;
			}
			
			
			String disableMasterTemplate= templateElm.getAttribute("disableMasterTemplate");
			if (disableMasterTemplate.isEmpty()) {
				disableMasterTemplate = null;
			}
			/*
			templatesBuilder.append(defaultAttribute).append("|").append(label).append("|").append(urlTemplate)
					.append("|").append(type).append("|").append(outputFormat).append("|").append(defaultFormat)
					.append("|").append(active).append("|").append(viewOnline).append("|").append(isMulti).append("|")
					.append(contOftemplatesElm).append("|").append(";\n");
			*/
			templatesBuilder.append(defaultLabel).append("|").append(label).append("|").append(urlTemplate)
			.append("|").append(type).append("|").append(outputFormat).append("|").append(defaultFormat)
			.append("|").append(locale).append("|").append(disableMasterTemplate).append("|").append(active).append("|").append(viewOnline).append("|").append(isMulti).append("|")
			.append(contOfmasterTemplateElm).append("|").append(";\n");
		}
		

		map.put("Report", reportBuilder.toString());
		map.put("DataModel", dataModelBuilder.toString());
		map.put("Description", descriptionBuilder.toString());
		map.put("Property", propertyBuilder.toString());
		map.put("Parameters", parametersBuilder.toString());
		map.put("Templates", templatesBuilder.toString());

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

		String[] splitDatabase = mapDoc.get("Report").split(Pattern.quote(";"));
		writeCsv("Report", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("DataModel").split(Pattern.quote(";"));
		writeCsv("DataModel", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Description").split(Pattern.quote(";"));
		writeCsv("Description", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Property").split(Pattern.quote(";"));
		writeCsv("Property", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Parameters").split(Pattern.quote(";"));
		writeCsv("Parameters", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Templates").split(Pattern.quote(";"));
		writeCsv("Templates", splitDatabase, outputFileLocation);
		
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

		String reportTableQuery = "CREATE TABLE IF NOT EXISTS Report (" + "xmlns VARCHAR(1000), "
				+ "xmlnsXsd VARCHAR(1000), " + "version VARCHAR(100), " + "dataModel  VARCHAR(100), "
				+ "useBipParameters VARCHAR(100), " + "producerName VARCHAR(100), " + "parameterVOName VARCHAR(100), "
				+ "parameterTaskFlow VARCHAR(100), " + "customDataControl VARCHAR(100), "
				+ "cachePerUser VARCHAR(100), " + "cacheSmartRefresh VARCHAR(100), " + "cacheUserRefresh VARCHAR(100)"
				+ ")";
		
		/*
		String reportTableQuery = "CREATE TABLE IF NOT EXISTS Report (" + "xmlns VARCHAR(1000), "
				+ "xmlns:xsd VARCHAR(1000), " + "version VARCHAR(100), " + "dataModel  VARCHAR(100), "
				+ "useBipParameters VARCHAR(100), " + "producerName VARCHAR(100), " + "parameterVOName VARCHAR(100), "
				+ "parameterTaskFlow VARCHAR(100), " + "customDataControl VARCHAR(100), "
				+ "cachePerUser VARCHAR(100), " + "cacheSmartRefresh VARCHAR(100), " + "cacheUserRefresh VARCHAR(100), "
				+ "contentsOfReportElement VARCHAR(100), " + "PRIMARY KEY (xmlns)" + ")";
		*/

		String dataModelTableQuery = "CREATE TABLE IF NOT EXISTS DataModel (" + "url VARCHAR(1000), " + "cache VARCHAR(50), "
				+ "contentsOfDataModelElement VARCHAR(100)" + ")";

		String descriptionTableQuery = "CREATE TABLE IF NOT EXISTS Description ("
				+ "contentsOfDescriptionElement VARCHAR(1000)" + ")";

		String propertyTableQuery = "CREATE TABLE IF NOT EXISTS Property (" + "name VARCHAR(100), "
				+ "value VARCHAR(50)," + "IsMulti VARCHAR(50)," + "PRIMARY KEY (name)" + ")";
		
		
		String parametersTableQuery = "CREATE TABLE IF NOT EXISTS Parameters (" + "paramPerLine INT, "
				+ "style VARCHAR(100), " + "id VARCHAR(50)," + "label VARCHAR(50)," 
				+ "defaultValue VARCHAR(50)," + "visible VARCHAR(50)," + "rowPlacement VARCHAR(50)," + "IsMulti VARCHAR(50)," + "contOfRadioButtonElm VARCHAR(100),"
				+ "PRIMARY KEY (id)" + ")";
		/*
		String templatesTableQuery = "CREATE TABLE IF NOT EXISTS Templates (" + "default VARCHAR(100),"
				+ "label VARCHAR(100), " + "url VARCHAR(100), " + "type VARCHAR(100), " + "outputFormat VARCHAR(100), "
				+ "defaultFormat VARCHAR(100), " + "active VARCHAR(100), " + "viewOnline VARCHAR(100), "
				+ "IsMulti VARCHAR(100), " + "contentsOfTemplatesElement VARCHAR(100), " + "PRIMARY KEY (label)" + ")";
		*/
		String templatesTableQuery = "CREATE TABLE IF NOT EXISTS Templates (" + "defaultLabel VARCHAR(100),"
				+ "label VARCHAR(100), " + "url VARCHAR(100), " + "type VARCHAR(100), " + "outputFormat VARCHAR(100), "
				+ "defaultFormat VARCHAR(100), " + "locale VARCHAR(100), " + "disableMasterTemplate VARCHAR(100), " + "active VARCHAR(100), " + "viewOnline VARCHAR(100), "
				+ "IsMulti VARCHAR(100), " + "contOfmasterTemplateElm VARCHAR(1000), " + "PRIMARY KEY (label)" + ")";
		
		

		try (PreparedStatement statement = connection.prepareStatement(reportTableQuery);
				PreparedStatement statement2 = connection.prepareStatement(dataModelTableQuery);
				PreparedStatement statement3 = connection.prepareStatement(descriptionTableQuery);
				PreparedStatement statement4 = connection.prepareStatement(propertyTableQuery);
				PreparedStatement statement5 = connection.prepareStatement(parametersTableQuery);
				PreparedStatement statement6 = connection.prepareStatement(templatesTableQuery)
			) {
			statement.executeUpdate();
			statement2.executeUpdate();
			statement3.executeUpdate();
			statement4.executeUpdate();
			statement5.executeUpdate();
			statement6.executeUpdate();

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

		String[] splitDatabase = mapDoc.get("Report").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Report", splitDatabase);

		splitDatabase = mapDoc.get("DataModel").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "DataModel", splitDatabase);

		splitDatabase = mapDoc.get("Description").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Description", splitDatabase);

		splitDatabase = mapDoc.get("Property").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Property", splitDatabase);

		splitDatabase = mapDoc.get("Parameters").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Parameters", splitDatabase);

		splitDatabase = mapDoc.get("Templates").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Templates", splitDatabase);
		
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

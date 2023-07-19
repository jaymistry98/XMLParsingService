package com.rootnodeparser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RootNodeParser {

	public static void main(String[] args) {

		String directoryPath = "C:\\Users\\jay\\eclipse-workspace\\RecastDataMigration\\";
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();

		List<String> xmlFiles = new ArrayList<>();
		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().endsWith(".xml")) {
				xmlFiles.add(file.getName());
			}
		}

		// Make sure xml database is created prior to running code

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/rootnodedb?useSSL=false",
				"root", "root123")) {
			createTables(connection);

			Workbook workbook = new XSSFWorkbook();

			Sheet configInformationSheet = workbook.createSheet("Config Information");
			createConfigInformationHeaderRow(configInformationSheet);

			Sheet performanceRatingsSheet = workbook.createSheet("Performance Ratings");
			createPerformanceRatingsHeaderRow(performanceRatingsSheet);

			Sheet proficiencyRatingsSheet = workbook.createSheet("Proficiency Ratings");
			createProficiencyRatingsHeaderRow(proficiencyRatingsSheet);

			Sheet setupDetailsSheet = workbook.createSheet("Setup Details");
			createSetupDetailsHeaderRow(setupDetailsSheet);

			Sheet exportCompetenciesSheet = workbook.createSheet("Export Competencies");
			createExportCompetenciesHeaderRow(exportCompetenciesSheet);

			Sheet exObjSheet = workbook.createSheet("Export ObjectivesVO");
			createExportObjectivesHeaderRow(exObjSheet);

			Sheet overAllSheet = workbook.createSheet("OverAll Rating");
			createOverallRatingHeaderRow(overAllSheet);

			for (int i = 0; i < xmlFiles.size(); i++) {
				String xmlFile = xmlFiles.get(i);
				int fileId = i + 1;

				File file = new File(xmlFile);
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);

				insertConfigInformation(document, connection, fileId, xmlFile, configInformationSheet);
				insertPerformanceRatings(document, connection, fileId, xmlFile, performanceRatingsSheet);
				insertProficiencyRatings(document, connection, fileId, xmlFile, proficiencyRatingsSheet);
				insertSetupDetails(document, connection, fileId, xmlFile, setupDetailsSheet);
				insertExportCompetencies(document, connection, fileId, xmlFile, exportCompetenciesSheet);
				insertExportObjectivesVO(document, connection, fileId, xmlFile, exObjSheet);
				insertOverAllRatingVORow(document, connection, fileId, xmlFile, overAllSheet);
			}

			String excelFile = "ParsedAnnualAppraisalData.xlsx";
			try (FileOutputStream outputStream = new FileOutputStream(excelFile)) {
				workbook.write(outputStream);
			}

			System.out.println("Excel file created: " + excelFile);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createTables(Connection connection) throws SQLException {
		String configTableQuery = "CREATE TABLE IF NOT EXISTS ConfigInformation (" + "FileId INT, "
				+ "FileName VARCHAR(100), " + "PersonId VARCHAR(50), " + "AppraisalId INT, "
				+ "RatingLevels VARCHAR(200), " + "RatingLevelsId VARCHAR(200), " + "ProficiencyScale VARCHAR(200), "
				+ "ProficiencyScaleId VARCHAR(200), " + "WeighingScale VARCHAR(200), "
				+ "WeighingScaleId VARCHAR(200), " + "PRIMARY KEY (FileName)" + ")";

		// Performance Ratings Table Query
		String perfRatingsTableQuery = "CREATE TABLE IF NOT EXISTS PerformanceRatings (" + "FileName VARCHAR(100), "
				+ "PerfStepValue INT," + "PerfRatingScale VARCHAR(50)," + "PerfBehaveIndicator VARCHAR(200),"
				+ "FOREIGN KEY (FileName) REFERENCES ConfigInformation(FileName)" + ")";

		// Proficiency Ratings Table Query
		String proRatingsTableQuery = "CREATE TABLE IF NOT EXISTS ProficiencyRatings (" + "FileName VARCHAR(100), "
				+ "StepValue INT," + "RatingScale VARCHAR(50)," + "BehaveIndicator VARCHAR(200),"
				+ "FOREIGN KEY (FileName) REFERENCES ConfigInformation(FileName)" + ")";

		// Setup Details Table Query
		String setupTableQuery = "CREATE TABLE IF NOT EXISTS SetupDetails (" + "FileName VARCHAR(100), "
				+ "Initiator VARCHAR(200)," + "MainAppraiser VARCHAR(200)," + "AppraisalDate VARCHAR(200),"
				+ "NextAppraisalDate VARCHAR(200)," + "PeriodStartDate VARCHAR(200)," + "PeriodEndDate VARCHAR(200),"
				+ "AppraisalPurpose VARCHAR(200)," + "Template VARCHAR(200),"
				+ "FOREIGN KEY (FileName) REFERENCES ConfigInformation(FileName)" + ")";

		// Export Competencies Table Query
		String expCompTableQuery = "CREATE TABLE IF NOT EXISTS ExportCompetencies (" + "FileName VARCHAR(100), "
				+ "CompName VARCHAR(50), " + "CompDesc LONGTEXT, " + "CompCeid INT, " + "CompBgid INT," + "CompOvn INT,"
				+ "CompType VARCHAR(50)," + "CompCid INT," + "CompRlid VARCHAR(50)," + "CompPceid INT,"
				+ "CompAsid INT," + "CompFromdt VARCHAR(50)," + "CompObjid INT," + "CompObjname VARCHAR(50),"
				+ "CompCmnts LONGTEXT," + "CmpUpdateFlag VARCHAR(50),"
				+ "FOREIGN KEY (FileName) REFERENCES ConfigInformation(FileName)" + ")";
		// Export Objective Table Query
		String exportObjTableQuery = "CREATE TABLE IF NOT EXISTS ExportObjectives (" + "FileName VARCHAR(100),"
				+ "ObjId INT," + "ObjName VARCHAR(50)," + "ObjTardt VARCHAR(50)," + "ObjStartdt VARCHAR(50),"
				+ "ObjBgid INT," + "ObjOvn INT," + "ObjOwnpid INT," + "ObjAchdt VARCHAR(50)," + "ObjDet LONGTEXT,"
				+ "ObjCmnts LONGTEXT," + "ObjSc VARCHAR(50)," + "ObjAppiid INT," + "PprPrid INT," + "PprOvn INT,"
				+ "PprPlid VARCHAR(50)," + "PprCmnts VARCHAR(200)," + "PprPid INT," + "ObjUpdateFlag VARCHAR(50),"
				+ "FOREIGN KEY (FileName) REFERENCES ConfigInformation(FileName)" + ")";

		// Overall Rating Table Query
		String overAllTableQuery = "CREATE TABLE IF NOT EXISTS OverAllRating (" + "FileName VARCHAR(100),"
				+ "OverallRating VARCHAR(50)," + "OverallComments VARCHAR(200),"
				+ "FOREIGN KEY (FileName) REFERENCES ConfigInformation(FileName)" + ")";

		try (PreparedStatement statement = connection.prepareStatement(configTableQuery);
				PreparedStatement statement2 = connection.prepareStatement(perfRatingsTableQuery);
				PreparedStatement statement3 = connection.prepareStatement(proRatingsTableQuery);
				PreparedStatement statement4 = connection.prepareStatement(setupTableQuery);
				PreparedStatement statement5 = connection.prepareStatement(expCompTableQuery);
				PreparedStatement statement6 = connection.prepareStatement(exportObjTableQuery);
				PreparedStatement statement7 = connection.prepareStatement(overAllTableQuery)) {
			statement.executeUpdate();
			statement2.executeUpdate();
			statement3.executeUpdate();
			statement4.executeUpdate();
			statement5.executeUpdate();
			statement6.executeUpdate();
			statement7.executeUpdate();

		}

	}

	// insertNode Functions
	// inserting Configuration Information into MySQL Database
	private static void insertConfigInformation(Document document, Connection connection, int fileId, String fileName,
			Sheet configInformationSheet) throws SQLException {
		Element rootElement = document.getDocumentElement();
		Element configInformationElement = (Element) rootElement.getElementsByTagName("ConfigInformation").item(0);

		String personId = getChildElementTextContent(configInformationElement, "PersonId");
		int appraisalId = Integer.parseInt(getChildElementTextContent(configInformationElement, "AppraisalId"));
		String ratingLevels = getChildElementTextContent(configInformationElement, "RatingLevels");
		String ratingLevelsId = getChildElementTextContent(configInformationElement, "RatingLevelsId");
		String proficiencyScale = getChildElementTextContent(configInformationElement, "ProficiencyScale");
		String proficiencyScaleId = getChildElementTextContent(configInformationElement, "ProficiencyScaleId");
		String weighingScale = getChildElementTextContent(configInformationElement, "WeighingScale");
		String weighingScaleId = getChildElementTextContent(configInformationElement, "WeighingScaleId");

		String insertQuery = "INSERT INTO ConfigInformation (FileId, FileName, PersonId, AppraisalId, RatingLevels, "
				+ "RatingLevelsId, ProficiencyScale, ProficiencyScaleId, WeighingScale, WeighingScaleId) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
			statement.setInt(1, fileId);
			statement.setString(2, fileName);
			statement.setString(3, personId);
			statement.setInt(4, appraisalId);
			statement.setString(5, ratingLevels);
			statement.setString(6, ratingLevelsId);
			statement.setString(7, proficiencyScale);
			statement.setString(8, proficiencyScaleId);
			statement.setString(9, weighingScale);
			statement.setString(10, weighingScaleId);
			statement.executeUpdate();
		}

		insertConfigInformationRow(configInformationSheet, fileId, fileName, personId, appraisalId, ratingLevels,
				ratingLevelsId, proficiencyScale, proficiencyScaleId, weighingScale, weighingScaleId);
	}

	// inserting Performance Ratings into MySQL Database
	private static void insertPerformanceRatings(Document document, Connection connection, int fileId, String fileName,
			Sheet performanceRatingsSheet) throws SQLException {
		Element rootElement = document.getDocumentElement();
		NodeList performanceRatingsList = rootElement.getElementsByTagName("PerformanceRatingsVO");

		for (int i = 0; i < performanceRatingsList.getLength(); i++) {
			Element performanceRatingsElement = (Element) performanceRatingsList.item(i);
			Element performanceRatingsRowElement = (Element) performanceRatingsElement
					.getElementsByTagName("PerformanceRatingsVORow").item(0);

			int perfStepValue = Integer
					.parseInt(getChildElementTextContent(performanceRatingsRowElement, "PerfStepValue"));
			String perfRatingScale = getChildElementTextContent(performanceRatingsRowElement, "PerfRatingScale");
			String perfBehaveIndicator = getChildElementTextContent(performanceRatingsRowElement,
					"PerfBehaveIndicator");

			String insertQuery = "INSERT INTO PerformanceRatings (FileName, PerfStepValue, PerfRatingScale, PerfBehaveIndicator) "
					+ "VALUES (?, ?, ?, ?)";

			try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
				// statement.setInt(1, fileId);
				statement.setString(1, fileName);
				statement.setInt(2, perfStepValue);
				statement.setString(3, perfRatingScale);
				statement.setString(4, perfBehaveIndicator);
				statement.executeUpdate();
			}

			insertPerformanceRatingsRow(performanceRatingsSheet, fileId, fileName, perfStepValue, perfRatingScale,
					perfBehaveIndicator);
		}
	}

	// inserting Proficiency Ratings into MySQL Database
	private static void insertProficiencyRatings(Document document, Connection connection, int fileId, String fileName,
			Sheet proficiencyRatingsSheet) throws SQLException {
		Element rootElement = document.getDocumentElement();
		NodeList proficiencyRatingsList = rootElement.getElementsByTagName("ProficiencyRatingsVO");

		for (int i = 0; i < proficiencyRatingsList.getLength(); i++) {
			Element proficiencyRatingsElement = (Element) proficiencyRatingsList.item(i);
			Element proficiencyRatingsRowElement = (Element) proficiencyRatingsElement
					.getElementsByTagName("ProficiencyRatingsVORow").item(0);

			int proStepValue = Integer.parseInt(getChildElementTextContent(proficiencyRatingsRowElement, "StepValue"));
			String proRatingScale = getChildElementTextContent(proficiencyRatingsRowElement, "RatingScale");
			String proBehaveIndicator = getChildElementTextContent(proficiencyRatingsRowElement, "BehaveIndicator");

			String insertQuery = "INSERT INTO ProficiencyRatings (FileName, StepValue, RatingScale, BehaveIndicator) "
					+ "VALUES (?, ?, ?, ?)";

			try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
				statement.setString(1, fileName);
				statement.setInt(2, proStepValue);
				statement.setString(3, proRatingScale);
				statement.setString(4, proBehaveIndicator);
				statement.executeUpdate();
			}

			insertPerformanceRatingsRow(proficiencyRatingsSheet, fileId, fileName, proStepValue, proRatingScale,
					proBehaveIndicator);
		}
	}

	// inserting Setup Details into MySQL Database
	private static void insertSetupDetails(Document document, Connection connection, int fileId, String fileName,
			Sheet setupDetailsSheet) throws SQLException {
		Element rootElement = document.getDocumentElement();
		Element setupDetailsElement = (Element) rootElement.getElementsByTagName("SetupDetails").item(0);

		String initiator = getChildElementTextContent(setupDetailsElement, "Initiator");
		String mainAppraiser = getChildElementTextContent(setupDetailsElement, "MainAppraiser");
		String appraisalDate = getChildElementTextContent(setupDetailsElement, "AppraisalDate");
		String nextAppraisalDate = getChildElementTextContent(setupDetailsElement, "NextAppraisalDate");
		String periodStartDate = getChildElementTextContent(setupDetailsElement, "PeriodStartDate");
		String periodEndDate = getChildElementTextContent(setupDetailsElement, "PeriodEndDate");
		String appraisalPurpose = getChildElementTextContent(setupDetailsElement, "AppraisalPurpose");
		String template = getChildElementTextContent(setupDetailsElement, "Template");

		String insertQuery = "INSERT INTO SetupDetails (FileName, Initiator, MainAppraiser, AppraisalDate, NextAppraisalDate, "
				+ "PeriodStartDate, PeriodEndDate, AppraisalPurpose, Template) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
			// statement.setInt(1, fileId);
			statement.setString(1, fileName);
			statement.setString(2, initiator);
			statement.setString(3, mainAppraiser);
			statement.setString(4, appraisalDate);
			statement.setString(5, nextAppraisalDate);
			statement.setString(6, periodStartDate);
			statement.setString(7, periodEndDate);
			statement.setString(8, appraisalPurpose);
			statement.setString(9, template);

			statement.executeUpdate();

			insertSetupDetailsRow(setupDetailsSheet, fileId, fileName, initiator, mainAppraiser, appraisalDate,
					nextAppraisalDate, periodStartDate, periodEndDate, appraisalPurpose, template);
		}
	}

	// inserting Export Competencies into MySQL Database
	private static void insertExportCompetencies(Document document, Connection connection, int fileId, String fileName,
			Sheet exportCompetenciesSheet) throws SQLException {
		Element rootElement = document.getDocumentElement();
		NodeList exportCompetenciesList = rootElement.getElementsByTagName("ExportCompetenciesVO");

		for (int i = 0; i < exportCompetenciesList.getLength(); i++) {
			Element exportCompetenciesElement = (Element) exportCompetenciesList.item(i);
			Element exportCompetenciesRowElement = (Element) exportCompetenciesElement
					.getElementsByTagName("ExportCompetenciesVORow").item(0);

			String compName = getChildElementTextContent(exportCompetenciesRowElement, "CompName");
			String compDesc = getChildElementTextContent(exportCompetenciesRowElement, "CompDesc");
			int compCeid = Integer.parseInt(getChildElementTextContent(exportCompetenciesRowElement, "CompCeid"));
			String compBgId = getChildElementTextContent(exportCompetenciesRowElement, "CompBgid");
			String compOvn = getChildElementTextContent(exportCompetenciesRowElement, "CompOvn");
			String compType = getChildElementTextContent(exportCompetenciesRowElement, "CompType");
			String compCid = getChildElementTextContent(exportCompetenciesRowElement, "CompCid");
			String compRlid = getChildElementTextContent(exportCompetenciesRowElement, "CompRlid");
			String compPceid = getChildElementTextContent(exportCompetenciesRowElement, "CompPceid");
			String compAsid = getChildElementTextContent(exportCompetenciesRowElement, "CompAsid");
			String compFromdt = getChildElementTextContent(exportCompetenciesRowElement, "CompFromdt");
			String compObjid = getChildElementTextContent(exportCompetenciesRowElement, "CompObjid");
			String compObjname = getChildElementTextContent(exportCompetenciesRowElement, "CompObjname");
			String compCmnts = getChildElementTextContent(exportCompetenciesRowElement, "CompCmnts");
			String cmpUpdateFlag = getChildElementTextContent(exportCompetenciesRowElement, "CmpUpdateFlag");

			String insertQuery = "INSERT INTO ExportCompetencies (FileName, CompName, CompDesc, CompCeid, CompBgId, CompOvn, CompType, CompCid, CompRlid, CompPceid, CompAsid, CompFromdt, CompObjid, CompObjname, CompCmnts, CmpUpdateFlag) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
				statement.setString(1, fileName);
				statement.setString(2, compName);
				statement.setString(3, compDesc);
				statement.setInt(4, compCeid);
				statement.setString(5, compBgId);
				statement.setString(6, compOvn);
				statement.setString(7, compType);
				statement.setString(8, compCid);
				statement.setString(9, compRlid);
				statement.setString(10, compPceid);
				statement.setString(11, compAsid);
				statement.setString(12, compFromdt);
				statement.setString(13, compObjid);
				statement.setString(14, compObjname);
				statement.setString(15, compCmnts);
				statement.setString(16, cmpUpdateFlag);

				statement.executeUpdate();
			}

			insertExportCompetenciesRow(exportCompetenciesSheet, fileId, fileName, compName, compDesc, compCeid,
					compBgId, compOvn, compType, compCid, compRlid, compPceid, compAsid, compFromdt, compObjid,
					compObjname, compCmnts, cmpUpdateFlag);
		}
	}

	// inserting Export Objectives into MySQL Database
	private static void insertExportObjectivesVO(Document document, Connection connection, int fileId, String fileName,
			Sheet exObjSheet) throws SQLException {
		Element rootElement = document.getDocumentElement();
		NodeList exObjList = rootElement.getElementsByTagName("ExportObjectivesVO");

		for (int i = 0; i < exObjList.getLength(); i++) {
			Element exObjElement = (Element) exObjList.item(i);
			Element exObjRowElement = (Element) exObjElement.getElementsByTagName("ExportObjectivesVORow").item(0);

			int ObjId = Integer.parseInt(getChildElementTextContent(exObjRowElement, "ObjId"));
			String ObjName = getChildElementTextContent(exObjRowElement, "ObjName");
			String ObjTardt = getChildElementTextContent(exObjRowElement, "ObjTardt");
			String ObjStartdt = getChildElementTextContent(exObjRowElement, "ObjStartdt");
			int ObjBgid = Integer.parseInt(getChildElementTextContent(exObjRowElement, "ObjBgid"));
			int ObjOvn = Integer.parseInt(getChildElementTextContent(exObjRowElement, "ObjOvn"));
			int ObjOwnpid = Integer.parseInt(getChildElementTextContent(exObjRowElement, "ObjOwnpid"));
			String ObjAchdt = getChildElementTextContent(exObjRowElement, "ObjAchdt");
			String ObjDet = getChildElementTextContent(exObjRowElement, "ObjDet");
			String ObjCmnts = getChildElementTextContent(exObjRowElement, "ObjCmnts");
			String ObjSc = getChildElementTextContent(exObjRowElement, "ObjSc");
			int ObjAppiid = Integer.parseInt(getChildElementTextContent(exObjRowElement, "ObjAppiid"));
			int PprPrid = Integer.parseInt(getChildElementTextContent(exObjRowElement, "PprPrid"));
			int PprOvn = Integer.parseInt(getChildElementTextContent(exObjRowElement, "PprOvn"));
			String PprPlid = getChildElementTextContent(exObjRowElement, "PprPlid");
			String PprCmnts = getChildElementTextContent(exObjRowElement, "PprCmnts");
			int PprPid = Integer.parseInt(getChildElementTextContent(exObjRowElement, "PprPid"));
			String ObjUpdateFlag = getChildElementTextContent(exObjRowElement, "ObjUpdateFlag");

			String insertQuery = "INSERT INTO ExportObjectives (FileName, ObjId, ObjName, ObjTardt,ObjStartdt,ObjBgid,ObjOvn,"
					+ "ObjOwnpid,ObjAchdt,ObjDet,ObjCmnts,ObjSc,ObjAppiid,PprPrid,PprOvn,PprPlid,PprCmnts,PprPid,ObjUpdateFlag) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
				statement.setString(1, fileName);
				statement.setInt(2, ObjId);
				statement.setString(3, ObjName);
				statement.setString(4, ObjTardt);
				statement.setString(5, ObjStartdt);
				statement.setInt(6, ObjBgid);
				statement.setInt(7, ObjOvn);
				statement.setInt(8, ObjOwnpid);
				statement.setString(9, ObjAchdt);
				statement.setString(10, ObjDet);
				statement.setString(11, ObjCmnts);
				statement.setString(12, ObjSc);
				statement.setInt(13, ObjAppiid);
				statement.setInt(14, PprPrid);
				statement.setInt(15, PprOvn);
				statement.setString(16, PprPlid);
				statement.setString(17, PprCmnts);
				statement.setInt(18, PprPid);
				statement.setString(19, ObjUpdateFlag);

				statement.executeUpdate();
			}

			insertExportObjectivesVORow(exObjSheet, fileId, fileName, ObjId, ObjName, ObjTardt, ObjStartdt, ObjBgid,
					ObjOvn, ObjOwnpid, ObjAchdt, ObjDet, ObjCmnts, ObjSc, ObjAppiid, PprPrid, PprOvn, PprPlid, PprCmnts,
					PprPid, ObjUpdateFlag);
		}
	}

	// inserting Overall Rating into MySQL Database
	private static void insertOverAllRatingVORow(Document document, Connection connection, int fileId, String fileName,
			Sheet overAllSheet) throws SQLException {
		Element rootElement = document.getDocumentElement();
		Element overAllElement = (Element) rootElement.getElementsByTagName("OverAllRatingVORow").item(0);

		String OverallRating = getChildElementTextContent(overAllElement, "OverallRating");
		String OverallComments = getChildElementTextContent(overAllElement, "OverallComments");

		String insertQuery = "INSERT INTO OverAllRating (FileName, OverallRating,OverallComments) "
				+ "VALUES (?, ?, ?)";

		try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
			statement.setString(1, fileName);
			statement.setString(2, OverallRating);
			statement.setString(3, OverallComments);

			statement.executeUpdate();

			insertOverallRatingVORow(overAllSheet, fileId, fileName, OverallRating, OverallComments);
		}
	}

	// insertRow Functions
	private static void insertConfigInformationRow(Sheet configInformationSheet, int fileId, String fileName,
			String personId, int appraisalId, String ratingLevels, String ratingLevelsId, String proficiencyScale,
			String proficiencyScaleId, String weighingScale, String weighingScaleId) {

		Row dataRow = configInformationSheet.createRow(configInformationSheet.getLastRowNum() + 1);
		dataRow.createCell(0).setCellValue(fileId);
		dataRow.createCell(1).setCellValue(fileName);
		dataRow.createCell(2).setCellValue(personId);
		dataRow.createCell(3).setCellValue(appraisalId);
		dataRow.createCell(4).setCellValue(ratingLevels);
		dataRow.createCell(5).setCellValue(ratingLevelsId);
		dataRow.createCell(6).setCellValue(proficiencyScale);
		dataRow.createCell(7).setCellValue(proficiencyScaleId);
		dataRow.createCell(8).setCellValue(weighingScale);
		dataRow.createCell(9).setCellValue(weighingScaleId);

	}

	private static void insertPerformanceRatingsRow(Sheet performanceRatingsSheet, int fileId, String fileName,
			int perfStepValue, String perfRatingScale, String perfBehaveIndicator) {

		Row dataRow = performanceRatingsSheet.createRow(performanceRatingsSheet.getLastRowNum() + 1);
		dataRow.createCell(0).setCellValue(fileName);
		dataRow.createCell(1).setCellValue(perfStepValue);
		dataRow.createCell(2).setCellValue(perfRatingScale);
		dataRow.createCell(3).setCellValue(perfBehaveIndicator);
	}

	private static void insertSetupDetailsRow(Sheet setupDetailsSheet, int fileId, String fileName, String initiator,
			String mainAppraiser, String appraisalDate, String nextAppraisalDate, String periodStartDate,
			String periodEndDate, String appraisalPurpose, String template) {

		Row dataRow = setupDetailsSheet.createRow(setupDetailsSheet.getLastRowNum() + 1);
		dataRow.createCell(0).setCellValue(fileName);
		dataRow.createCell(1).setCellValue(initiator);
		dataRow.createCell(2).setCellValue(mainAppraiser);
		dataRow.createCell(3).setCellValue(appraisalDate);
		dataRow.createCell(4).setCellValue(nextAppraisalDate);
		dataRow.createCell(5).setCellValue(periodStartDate);
		dataRow.createCell(6).setCellValue(periodEndDate);
		dataRow.createCell(7).setCellValue(appraisalPurpose);
		dataRow.createCell(8).setCellValue(template);

	}

	private static void insertExportCompetenciesRow(Sheet performanceRatingsSheet, int fileId, String fileName,
			String CompName, String CompDesc, int compCeid, String compBgId, String compOvn, String CompType,
			String compCid, String CompRlid, String compPceid, String compAsid, String CompFromdt, String compObjid,
			String CompObjname, String CompCmnts, String CompUpdateFlag) {

		Row dataRow = performanceRatingsSheet.createRow(performanceRatingsSheet.getLastRowNum() + 1);
		dataRow.createCell(0).setCellValue(fileName);
		dataRow.createCell(1).setCellValue(CompName);
		dataRow.createCell(2).setCellValue(CompDesc);
		dataRow.createCell(3).setCellValue(compCeid);
		dataRow.createCell(4).setCellValue(compBgId);
		dataRow.createCell(5).setCellValue(compOvn);
		dataRow.createCell(6).setCellValue(CompType);
		dataRow.createCell(7).setCellValue(compCid);
		dataRow.createCell(8).setCellValue(CompRlid);
		dataRow.createCell(9).setCellValue(compPceid);
		dataRow.createCell(10).setCellValue(compAsid);
		dataRow.createCell(11).setCellValue(CompFromdt);
		dataRow.createCell(12).setCellValue(compObjid);
		dataRow.createCell(13).setCellValue(CompObjname);
		dataRow.createCell(14).setCellValue(CompCmnts);
		dataRow.createCell(15).setCellValue(CompUpdateFlag);
	}

	private static void insertExportObjectivesVORow(Sheet exObjSheet, int fileId, String fileName, int ObjId,
			String ObjName, String ObjTardt, String ObjStartdt, int ObjBgid, int ObjOvn, int ObjOwnpid, String ObjAchdt,
			String ObjDet, String ObjCmnts, String ObjSc, int ObjAppiid, int PprPrid, int PprOvn, String PprPlid,
			String PprCmnts, int PprPid, String ObjUpdateFlag) {
		Row dataRow = exObjSheet.createRow(exObjSheet.getLastRowNum() + 1);
		dataRow.createCell(0).setCellValue(fileName);
		dataRow.createCell(1).setCellValue(ObjId);
		dataRow.createCell(2).setCellValue(ObjName);
		dataRow.createCell(3).setCellValue(ObjTardt);
		dataRow.createCell(4).setCellValue(ObjStartdt);
		dataRow.createCell(5).setCellValue(ObjBgid);
		dataRow.createCell(6).setCellValue(ObjOvn);
		dataRow.createCell(7).setCellValue(ObjOwnpid);
		dataRow.createCell(8).setCellValue(ObjAchdt);
		dataRow.createCell(9).setCellValue(ObjDet);
		dataRow.createCell(10).setCellValue(ObjCmnts);
		dataRow.createCell(11).setCellValue(ObjSc);
		dataRow.createCell(12).setCellValue(ObjAppiid);
		dataRow.createCell(13).setCellValue(PprPrid);
		dataRow.createCell(14).setCellValue(PprOvn);
		dataRow.createCell(15).setCellValue(PprPlid);
		dataRow.createCell(16).setCellValue(PprCmnts);
		dataRow.createCell(17).setCellValue(PprPid);
		dataRow.createCell(18).setCellValue(ObjUpdateFlag);

	}

	private static void insertOverallRatingVORow(Sheet overAllSheet, int fileId, String fileName, String OverallRating,
			String OverallComments) {
		Row dataRow = overAllSheet.createRow(overAllSheet.getLastRowNum() + 1);
		dataRow.createCell(0).setCellValue(fileName);
		dataRow.createCell(1).setCellValue(OverallRating);
		dataRow.createCell(2).setCellValue(OverallComments);
	}

	// createHeaderRow Functions
	private static void createConfigInformationHeaderRow(Sheet configInformationSheet) {
		Row headerRow = configInformationSheet.createRow(0);

		headerRow.createCell(0).setCellValue("FileId");
		headerRow.createCell(1).setCellValue("FileName");
		headerRow.createCell(2).setCellValue("PersonId");
		headerRow.createCell(3).setCellValue("AppraisalId");
		headerRow.createCell(4).setCellValue("RatingLevels");
		headerRow.createCell(5).setCellValue("RatingLevelsId");
		headerRow.createCell(6).setCellValue("ProficiencyScale");
		headerRow.createCell(7).setCellValue("ProficiencyScaleId");
		headerRow.createCell(8).setCellValue("WeighingScale");
		headerRow.createCell(9).setCellValue("WeighingScaleId");
	}

	private static void createPerformanceRatingsHeaderRow(Sheet performanceRatingsSheet) {
		Row headerRow = performanceRatingsSheet.createRow(0);

		headerRow.createCell(0).setCellValue("FileName");
		headerRow.createCell(1).setCellValue("PerfStepValue");
		headerRow.createCell(2).setCellValue("PerfRatingScale");
		headerRow.createCell(3).setCellValue("PerfBehaveIndicator");
	}

	private static void createProficiencyRatingsHeaderRow(Sheet proficiencyRatingsSheet) {
		Row headerRow = proficiencyRatingsSheet.createRow(0);

		headerRow.createCell(0).setCellValue("FileName");
		headerRow.createCell(1).setCellValue("StepValue");
		headerRow.createCell(2).setCellValue("RatingScale");
		headerRow.createCell(3).setCellValue("BehaveIndicator");
	}

	private static void createSetupDetailsHeaderRow(Sheet setupDetailsSheet) {
		Row headerRow = setupDetailsSheet.createRow(0);

		headerRow.createCell(0).setCellValue("FileName");
		headerRow.createCell(1).setCellValue("Initiator");
		headerRow.createCell(2).setCellValue("MainAppraiser");
		headerRow.createCell(3).setCellValue("Appraisal Date");
		headerRow.createCell(4).setCellValue("Next Appraisal Date");
		headerRow.createCell(5).setCellValue("Period Start Date");
		headerRow.createCell(6).setCellValue("Period End Date");
		headerRow.createCell(7).setCellValue("Appraisal Purpose");
		headerRow.createCell(8).setCellValue("Template");
	}

	private static void createExportCompetenciesHeaderRow(Sheet exportCompetenciesSheet) {
		Row headerRow = exportCompetenciesSheet.createRow(0);

		headerRow.createCell(0).setCellValue("FileName");
		headerRow.createCell(1).setCellValue("CompName");
		headerRow.createCell(2).setCellValue("CompDesc");
		headerRow.createCell(3).setCellValue("CompCeid");
		headerRow.createCell(4).setCellValue("CompBgid");
		headerRow.createCell(5).setCellValue("CompOvn");
		headerRow.createCell(6).setCellValue("CompType");
		headerRow.createCell(7).setCellValue("CompCid");
		headerRow.createCell(8).setCellValue("CompRlid");
		headerRow.createCell(9).setCellValue("CompPceid");
		headerRow.createCell(10).setCellValue("CompAsid");
		headerRow.createCell(11).setCellValue("CompFromdt");
		headerRow.createCell(12).setCellValue("CompObjid");
		headerRow.createCell(13).setCellValue("CompObjname");
		headerRow.createCell(14).setCellValue("CompCmnts");
		headerRow.createCell(15).setCellValue("CmpUpdateFlag");

	}

	private static void createExportObjectivesHeaderRow(Sheet exObjSheet) {
		Row headerRow = exObjSheet.createRow(0);
		headerRow.createCell(0).setCellValue("FileName");
		headerRow.createCell(1).setCellValue("ObjId");
		headerRow.createCell(2).setCellValue("ObjName");
		headerRow.createCell(3).setCellValue("ObjTardt");
		headerRow.createCell(4).setCellValue("ObjStartdt");
		headerRow.createCell(5).setCellValue("ObjBgid");
		headerRow.createCell(6).setCellValue("ObjOvn");
		headerRow.createCell(7).setCellValue("ObjOwnpid");
		headerRow.createCell(8).setCellValue("ObjAchdt");
		headerRow.createCell(9).setCellValue("ObjDet");
		headerRow.createCell(10).setCellValue("ObjCmnts");
		headerRow.createCell(11).setCellValue("ObjTardt");
		headerRow.createCell(12).setCellValue("ObjSc");
		headerRow.createCell(13).setCellValue("PprPrid");
		headerRow.createCell(14).setCellValue("PprOvn");
		headerRow.createCell(15).setCellValue("PprPlid");
		headerRow.createCell(16).setCellValue("PprCmnts");
		headerRow.createCell(17).setCellValue("PprPid");
		headerRow.createCell(18).setCellValue("ObjUpdateFlag");

	}

	private static void createOverallRatingHeaderRow(Sheet overAllSheet) {
		Row headerRow = overAllSheet.createRow(0);
		headerRow.createCell(0).setCellValue("FileName");
		headerRow.createCell(1).setCellValue("OverallRating");
		headerRow.createCell(2).setCellValue("OverallComments");

	}

	private static String getChildElementTextContent(Element parentElement, String childTagName) {
		NodeList nodeList = parentElement.getElementsByTagName(childTagName);
		if (nodeList.getLength() > 0) {
			Node childNode = nodeList.item(0);
			return childNode.getTextContent();
		}
		return "";
	}
}

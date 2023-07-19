package com.main;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import com.main.XMLUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zeroturnaround.zip.ZipUtil;

import com.lti.recast.XMLParsing.databaseToExcelExport.DbtoExcelExport;
import com.lti.recast.XMLParsing.databaseToExcelExport.DimensionExcel;
import com.lti.recast.XMLParsing.databaseToExcelExport.LogicalLayerExcel;
import com.lti.recast.XMLParsing.databaseToExcelExport.LogicalTableSourceExcel;
import com.lti.recast.XMLParsing.databaseToExcelExport.PhysicalExcel;
import com.lti.recast.XMLParsing.databaseToExcelExport.PresentationExcel;
import com.lti.recast.XMLParsing.databaseToExcelExport.PresentationHeirarchy;

/**
 * SampleParsingService.
 * 
 * @author 10715837
 *
 */
@Service
public class SampleParsingService {

	private static String userName;

	@Value("${spring.datasource.username}")
	public void setUsername(String value) {
		this.userName = value;
	}

	/**
	 * userPasswd.
	 */

	private static String userPasswd;

	@Value("${spring.datasource.password}")
	public void setPassword(String value) {
		this.userPasswd = value;
	}

	/**
	 * databaseUrl.
	 */

	private static String databaseUrl;

	@Value("${spring.datasource.url}")
	public void setdatabaseUrl(String value) {
		this.databaseUrl = value;
	}

	static Map<String, String> mdsidNameMap = new HashMap<>();

	/**
	 * getParsingData.
	 * 
	 * @param schemaFileName {@link String}
	 * @param outputFileLocation {@link String}
	 * @param margingPath {@link String}
	 */
	public static void getParsingData(String schemaFileName, String outputFileLocation, String margingPath) {
		try {
			Document doc = readXMLDocumentFromFile(schemaFileName);
			boolean isloadMdsid = false;
			int count = 0;
			Map<String, String> mapDoc = parseXmlDoc(doc, isloadMdsid, count);
			Map<String, String> margingMap =obieeMarging(doc);
			extractMapData(mapDoc, outputFileLocation);
			//margingMapDataOnCSV(margingMap, outputFileLocation);
			Connection conn = databaseConnection();
			extractMapDataForDb(conn, mapDoc);
			//margingDataOnDB(conn, margingMap);
			DimensionExcel dimentionExcel = new DimensionExcel();
			dimentionExcel.dimentionExcel(databaseUrl, userName, userPasswd);
			LogicalLayerExcel logicalLayerExcel = new LogicalLayerExcel();
			logicalLayerExcel.LogicalExcel(databaseUrl, userName, userPasswd);
			LogicalTableSourceExcel logicalTableSourceExcel = new LogicalTableSourceExcel();
			logicalTableSourceExcel.logicalSourceExcel(databaseUrl, userName, userPasswd);
			PhysicalExcel physical = new PhysicalExcel();
			physical.physicalExl(databaseUrl, userName, userPasswd);
			PresentationExcel presentation = new PresentationExcel();
			presentation.presentationExl(databaseUrl, userName, userPasswd);
			PresentationHeirarchy presentationHeirchy = new PresentationHeirarchy();
			presentationHeirchy.getPresenttionHeirachy(databaseUrl, userName, userPasswd);
			DbtoExcelExport dbtoExcelExport = new DbtoExcelExport();
			dbtoExcelExport.export(margingPath,databaseUrl,userName,userPasswd);

		} catch (Exception e) {
			System.out.println("Exception occured:" + e.getMessage());
		}

	}

	/**
	 * 
	 * databaseConnection.
	 * 
	 * @return Connection
	 * @throws SQLException 
	 */
	public static Connection databaseConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(databaseUrl, userName, userPasswd);
		return conn;
	}

	/**
	 * readXMLDocumentFromFile.
	 * 
	 * @param fileNameWithPath {@link String}
	 * @return Document.
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
	 * parseXmlDoc.
	 * 
	 * @param doc         {@link Document}
	 * @param isloadMdsid {@link boolean}
	 * @param count       {@link count}
	 * @return Map
	 * @throws Exception
	 */
	private static Map<String, String> parseXmlDoc(Document doc, boolean isloadMdsid, int count) throws Exception {
		Map<String, String> map = new HashMap<>();

		StringBuilder dbBuilder = new StringBuilder();
		StringBuilder featureBuilder = new StringBuilder();
		StringBuilder connectionPoolRefBuilder = new StringBuilder();
		StringBuilder physicalTblBuilder = new StringBuilder();
		StringBuilder physicalKeyBuilder = new StringBuilder();
		StringBuilder physclClmnBuilder = new StringBuilder();
		StringBuilder connectionPoolBuilder = new StringBuilder();
		StringBuilder businessModelBuilder = new StringBuilder();
		StringBuilder logicalTblBuilder = new StringBuilder();
		StringBuilder logicalClmBuilder = new StringBuilder();
		StringBuilder attributeDefnBuilder = new StringBuilder();
		StringBuilder logicalKeyBuilder = new StringBuilder();
		StringBuilder logicalTblSrceBuilder = new StringBuilder();
		StringBuilder columnMappingBuilder = new StringBuilder();
		StringBuilder logicalTblFragmntBuilder = new StringBuilder();
		StringBuilder userBuilder = new StringBuilder();
		StringBuilder logicalComplexJoinBuilder = new StringBuilder();
		StringBuilder presentationTableBuilder = new StringBuilder();
		StringBuilder presentationColumnBuilder = new StringBuilder();
		StringBuilder queryPrivilegeBuilder = new StringBuilder();
		StringBuilder groupBuilder = new StringBuilder();
		StringBuilder presentationHierarchyBuilder = new StringBuilder();
		StringBuilder presentationLevelBuilder = new StringBuilder();
		StringBuilder measureDefnBuilder = new StringBuilder();
		StringBuilder variableBuilder = new StringBuilder();
		StringBuilder privilegePackageBuilder = new StringBuilder();
		StringBuilder objectPrivilegeBuilder = new StringBuilder();
		StringBuilder dimensionBuilder = new StringBuilder();
		StringBuilder logicalLevelBuilder = new StringBuilder();
		StringBuilder physicalForeignKeyBuilder = new StringBuilder();
		StringBuilder refTableSourceBuilder = new StringBuilder();
		StringBuilder groupByLgclTblSrcBuilder = new StringBuilder();
		StringBuilder dmnsnLogicalKeyBuilder = new StringBuilder();
		StringBuilder presentationCatalogBuilder = new StringBuilder();

		if (isloadMdsid) {
			dbBuilder.append("dbName").append("|").append("dbTypeId").append("|").append("mdsid").append("|")
					.append("name").append("|").append("type").append("|").append("description").append(";\n");
			featureBuilder.append("featureName").append("|").append("value").append(";\n");
			connectionPoolRefBuilder.append("connectionPoolRef").append("|").append("refId").append(";\n");

			physicalTblBuilder.append("name").append("|").append("maxConn").append("|").append("mdsid").append("|")
					.append("type").append("|").append("iconIndex").append("|").append("isCacheable").append("|")
					.append("pollFreq").append("|").append("containerRef").append("|").append("containerRefName")
					.append("|").append("x").append("|").append("y").append("|").append("sourceTableRef").append("|")
					.append("sourceTblRefName").append("|").append("uri").append("|").append("description")
					.append(";\n");

			physclClmnBuilder.append("physicalTableName").append("|").append("dataType").append("|").append("msid")
					.append("|").append("Name").append("|").append("precision_col").append("|").append("special_type")
					.append("|").append("extName").append("|").append("sourceColumnRef").append("|")
					.append("iconIndexphclClm").append(";\n");

			physicalKeyBuilder.append("physicalTableName").append("|").append("physicalKeyMdsid").append("|")
					.append("physicalKeyName").append("|").append("columnRef").append("|").append("refId").append("|")
					.append("columnRefName").append(";\n");

			connectionPoolBuilder.append("connectionPoolName").append("|").append("maxConn").append("|")
					.append("connectionPoolType").append("|").append("connectionPoolMdsid").append("|")
					.append("maxConnDiff").append("|").append("outputType").append("|").append("timeout").append("|")
					.append("user").append("|").append("password").append("|").append("dataSource").append("|")
					.append("databaseRef").append("|").append("xmlRefreshInterval").append("|").append("description")
					.append(";\n");

			businessModelBuilder.append("businessModelName").append("|").append("mdsid").append("|")
					.append("isClassicStar").append("|").append("isAvailable").append("|").append("description")
					.append(";\n");

			logicalTblBuilder.append("logicalTableName").append("|").append("mdsid").append("|")
					.append("subjectAreaRef").append("|").append("subjectAreaRefName").append("|").append("x")
					.append("|").append("y").append("|").append("descriptin").append(";\n");

			logicalClmBuilder.append("logicalTableName").append("|").append("logicalColumnName").append("|")
					.append("logicalColumnMdsid").append("|").append("isWriteable").append("|").append("dscription")
					.append(";\n");

			attributeDefnBuilder.append("logicalColumnName").append("|").append("attributeDefnName").append("|")
					.append("attributeDefnMdsid").append("|").append("contOfExprTextElm").append("|")
					.append("contOfExprTextDescElm").append("|").append("objectRef").append("|").append("objectRfName")
					.append("|").append("objectTypeId").append("|").append("refId").append(";\n");

			logicalKeyBuilder.append("logicalTableName").append("|").append("logicalKeyName").append("|")
					.append("logicalKeyMdsid").append("|").append("isPrimary").append("|").append("logicalColumnRef")
					.append("|").append("lgclClmnRefName").append("|").append("refId").append(";\n");

			dimensionBuilder.append("dimensionName").append("|").append("dimensionMdsid").append("|")
					.append("isTimeDim").append("|").append("isValueBased").append("|").append("isRagged").append("|")
					.append("isSkipped").append("|").append("defaultRootLevelRef").append("|")
					.append("defaultRootLevelRefName").append("|").append("subjctAreaRef").append("|")
					.append("subjctAreaRefName").append("|").append("desription").append(";\n");

			logicalTblSrceBuilder.append("logicalTableSourceName").append("|").append("logicalTableSourceMdsid")
					.append("|").append("isActive").append("|").append("logicalTableRef").append("|")
					.append("lgclTblRefName").append(";\n");

			columnMappingBuilder.append("logicalTableSourceName").append("|").append("logiclExprDesc").append("|")
					.append("physicalExprDesc").append("|").append("logicalColmExprList").append("|")
					.append("lgclTblObjRefName").append("|").append("exprList").append("|")
					.append("physclTblObjRefName").append(";\n");

			logicalTblFragmntBuilder.append("logicalTableSourceName").append("|").append("linkList").append("|")
					.append("whereClauseList").append("|").append("groupByList").append("|").append("fragmentContList")
					.append("|").append("groupByexprTextDesc").append("|").append("groupByExprText").append("|")
					.append("startNodeTableRefName").append("|").append("fragmntContentExprText").append("|")
					.append("fragmntContentExprTextDesc").append("|").append("whereExprText").append("|")
					.append("whereExprTextDesc").append(";\n");

			logicalComplexJoinBuilder.append("logicalComplexJoinName").append("|").append("logicalComplexJoinMdsid")
					.append("|").append("isAggregate1").append("|").append("isAggregate2").append("|")
					.append("multiplicity1").append("|").append("multiplicity2").append("|").append("type").append("|")
					.append("logicalTable1Ref").append("|").append("lgclTbl1RefName").append("|")
					.append("logicalTable2Ref").append("|").append("lgclTbl2RefName").append(";\n");

			userBuilder.append("userName").append("|").append("userMdsid").append("|").append("logStatisticsStatus")
					.append("|").append("descripton").append(";\n");
			presentationTableBuilder.append("presentationTableName").append("|").append("presentationTableMdsid")
					.append("|").append("hasDispName").append("|").append("hasDispDescription").append("|")
					.append("containerRef").append("|").append("prsntionTableRefName").append("|")
					.append("prsntatnHierarchyrefId").append("|").append("presentationHierarchyRef").append("|")
					.append("presentationHierarchyRefName").append("|").append("desription").append(";\n");

			presentationColumnBuilder.append("presentationTableName").append("|").append("presentationColumnName")
					.append("|").append("presentationColumnMdsid").append("|").append("overrideLogicalName").append("|")
					.append("hasDispName").append("|").append("hasDispDescription").append("|")
					.append("logicalColumnRef").append("|").append("lgclClmnRefName").append("|").append("dscription")
					.append(";\n");

			queryPrivilegeBuilder.append("queryPrivilegeName").append("|").append("mdsid").append("|")
					.append("maxExecTime").append("|").append("maxRows").append("|").append("execPhysicalPrivilege")
					.append("|").append("populatePrivilege").append(";\n");

			groupBuilder.append("groupName").append("|").append("groupMdsid").append("|").append("displayName")
					.append("|").append("logStatisticsStatus").append("|").append("refId").append("|")
					.append("groupRef").append(";\n");

			presentationHierarchyBuilder.append("presentationHierarchyName").append("|").append("mdsid").append("|")
					.append("hasDispName").append("|").append("hasDispDescription").append("|").append("containerRef")
					.append("|").append("contnrRefName").append("|").append("logicalDimensionRef").append("|")
					.append("lgclDimensionRefName").append("|").append("aliasList").append(";\n");

			presentationLevelBuilder.append("presentationHierarchyName").append("|").append("presentationLevelName")
					.append("|").append("mdsid").append("|").append("hasDispName").append("|")
					.append("hasDispDescription").append("|").append("logicalLevelRef").append("|")
					.append("lgclLvlRefName").append("|").append("aliasName").append("|").append("refId").append("|")
					.append("presentationColumnRef").append("|").append("prsnttnClmnRefName").append("|")
					.append("aliasLst").append(";\n");

			measureDefnBuilder.append("logicalColumnName").append("|").append("measureDefnName").append("|")
					.append("measureDefnMdsid").append("|").append("isCommutative").append("|").append("exprMdsid")
					.append("|").append("exprName").append("|").append("aggrRuleName").append("|")
					.append("aggrRuleMdsid").append("|").append("isDefault").append("|").append("contOfExprTextElm")
					.append("|").append("contOfExprTextDescElm").append("|").append("refId").append("|")
					.append("objectTypeId").append("|").append("objectRef").append("|").append("objectRefName")
					.append(";\n");

			variableBuilder.append("variableName").append("|").append("variableMdsid").append("|").append("exprName")
					.append("|").append("exprMdsid").append("|").append("contofexprTextElm").append("|")
					.append("contofexprTextDescElm").append(";\n");

			privilegePackageBuilder.append("privilegePkgName").append("|").append("privilegePkgMdsid").append("|")
					.append("roleRef").append("|").append("logicalQueryMaxExecTime").append("|")
					.append("queryPrivilegeMpngElm").append("|").append("databaseRef").append("|").append("dbRefName")
					.append(";\n");

			objectPrivilegeBuilder.append("objectPrivilegeName").append("|").append("objPrvlgeMdsid").append("|")
					.append("type").append("|").append("privilegePackageRef").append("|").append("privilegePkgRefName")
					.append("|").append("refId").append("|").append("objectTypeId").append("|").append("objectRef")
					.append("|").append("objectRefName").append(";\n");
			physicalForeignKeyBuilder.append("physicalTableName").append("|").append("physicalFrnKeyName").append("|")
					.append("physicalFrnKeyMdsid").append("|").append("counterPartKeyRef").append("|")
					.append("colmnRef").append("|").append("colmnRefName").append("|").append("rfId").append(";\n");

			refTableSourceBuilder.append("logicalTableName").append("|").append("lgclTblSrceRefName").append("|")
					.append("refId").append("|").append("logicalTableSourceRef").append(";\n");

			logicalLevelBuilder.append("dimensionName").append("|").append("logicalLevelName").append("|")
					.append("logicalLevelMdsid").append("|").append("isGTA").append("|").append("memberCount")
					.append("|").append("levelConst").append("|").append("logicalLevelRefId").append("|")
					.append("logicalLevelRef").append("|").append("lgclLvlRefName").append("|").append("refLgclClmList")
					.append("|").append("logicalClumnRefName").append("|").append("refPreferredDrillDownRefId")
					.append("|").append("refPreferredDrillDownlogicalLevelRef").append("|")
					.append("refPreferredDrillDownlogicalLevelRefName").append(";\n");

			groupByLgclTblSrcBuilder.append("refId").append("|").append("objectTypeId").append("|").append("objectRef")
					.append("|").append("objectRefName").append(";\n");

			dmnsnLogicalKeyBuilder.append("logicalLevelName").append("|").append("logicalKeyMdsid").append("|")
					.append("logicalKeyName").append("|").append("isPrimary").append("|").append("isChronKey")
					.append("|").append("isForDrillDown").append("|").append("logicalKeyRefId").append("|")
					.append("logicalKeylogicalColumnRef").append("|").append("logicalKeylogicalColumnRefName")
					.append(";\n");

			presentationCatalogBuilder.append("presentationCatalogName").append("|").append("presentationCatalogMdsid")
					.append("|").append("prstnCatalogHasDispName").append("|").append("isAutoAggr").append("|")
					.append("prstnCatalogHasDispDescription").append("|").append("subjectAreaRef").append("|")
					.append("subjctAreRefName").append("|").append("defaultFactColumnRef").append("|")
					.append("defaultFactColumnRefName").append("|").append("prstnCatalogDesription").append("|")
					.append("visibilityMdsid").append("|").append("visibilityName").append("|")
					.append("visibilityExprText").append("|").append("visibilityExprTextDesc").append("|")
					.append("refId").append("|").append("presentationTableRef").append("|")
					.append("prsntionTableRefName").append(";\n");
		}
		Element dECLAREElm = doc.getDocumentElement();

		List<Element> dbNdList = XMLUtil.getChildElements(dECLAREElm, "Database");
		for (Element databaseElm : dbNdList) {
			String dbName = databaseElm.getAttribute("dbName");
			String dbTypeId = databaseElm.getAttribute("dbTypeId");
			String mdsid = databaseElm.getAttribute("mdsid");
			String name = databaseElm.getAttribute("name");
			String type = databaseElm.getAttribute("type");

			Element descriptionElm = XMLUtil.getChildElement(databaseElm, "Description");

			String description = descriptionElm.getTextContent();
			if (description.isEmpty()) {
				description = null;
			}
			if (isloadMdsid) {
				dbBuilder.append(dbName).append("|").append(dbTypeId).append("|").append(mdsid).append("|").append(name)
						.append("|").append(type).append("|").append(description).append(";\n");

			} else {
				mdsidNameMap.put(mdsid, name);

			}

			map.put("databasetable", dbBuilder.toString());

			List<Element> featureNdList = XMLUtil.getChildElements(databaseElm, "Feature");
			for (Element featureElm : featureNdList) {

				// String isMulti= featureElm.getAttribute("IsMulti");
				String featureName = featureElm.getAttribute("name");
				String value = featureElm.getAttribute("value");
				if (isloadMdsid) {
					featureBuilder.append(featureName).append("|").append(value).append(";\n");
				}

			}
			map.put("Feature", featureBuilder.toString());
			Element refConnectionPoolsElm = XMLUtil.getChildElement(databaseElm, "RefConnectionPools");

			List<Element> refConnectionPoolNdList = XMLUtil.getChildElements(refConnectionPoolsElm,
					"RefConnectionPool");
			for (Element refConnectionPoolElm : refConnectionPoolNdList) {

				String connectionPoolRef = refConnectionPoolElm.getAttribute("connectionPoolRef");
				String refId = refConnectionPoolElm.getAttribute("refId");

				if (isloadMdsid) {
					connectionPoolRefBuilder.append(connectionPoolRef).append("|").append(refId).append(";\n");
				}
			}
		}
		map.put("RefConnectionPool", connectionPoolRefBuilder.toString());

		List<Element> physicalTableNdList = XMLUtil.getChildElements(dECLAREElm, "PhysicalTable");
		for (Element physicalTableElm : physicalTableNdList) {

			String containerRef = physicalTableElm.getAttribute("containerRef");
			String[] refMdsid = containerRef.split(Pattern.quote("#"));
			String containerRefName = null;

			if (mdsidNameMap.containsKey(refMdsid[1])) {
				containerRefName = mdsidNameMap.get(refMdsid[1]);

			}
			String maxConn = physicalTableElm.getAttribute("maxConn");
			String physicalTableMdsid = physicalTableElm.getAttribute("mdsid");
			String physicalTableName = physicalTableElm.getAttribute("name");

			String physicalTabletype = physicalTableElm.getAttribute("type");
			String iconIndex = physicalTableElm.getAttribute("iconIndex");
			String isCacheable = physicalTableElm.getAttribute("isCacheable");
			String pollFreq = physicalTableElm.getAttribute("pollFreq");
			String x = physicalTableElm.getAttribute("x");
			String y = physicalTableElm.getAttribute("y");
			String uri = physicalTableElm.getAttribute("uri");
			String sourceTableRef = physicalTableElm.getAttribute("sourceTableRef");
			String[] sourceMdsid = containerRef.split(Pattern.quote("#"));
			String sourceTblRefName = null;

			if (mdsidNameMap.containsKey(sourceMdsid[1])) {
				sourceTblRefName = mdsidNameMap.get(sourceMdsid[1]);

			}

			Element descriptionElm = XMLUtil.getChildElement(physicalTableElm, "Description");
			String description = descriptionElm.getTextContent();

			if (isloadMdsid) {
				physicalTblBuilder.append(physicalTableName).append("|").append(maxConn).append("|")
						.append(physicalTableMdsid).append("|").append(physicalTabletype).append("|").append(iconIndex)
						.append("|").append(isCacheable).append("|").append(pollFreq).append("|").append(containerRef)
						.append("|").append(containerRefName).append("|").append(x).append("|").append(y).append("|")
						.append(sourceTableRef).append("|").append(sourceTblRefName).append("|").append(uri).append("|")
						.append(description).append(";\n");

			} else {
				mdsidNameMap.put(physicalTableMdsid, physicalTableName);

			}

			List<Element> physicalColumnNdList = XMLUtil.getChildElements(physicalTableElm, "PhysicalColumn");
			for (Element physicalColumnElm : physicalColumnNdList) {

				String dataType = physicalColumnElm.getAttribute("dataType");
				String physicalColumndMsid = physicalColumnElm.getAttribute("mdsid");
				String physicalColumnName = physicalColumnElm.getAttribute("name");
				String precision = physicalColumnElm.getAttribute("precision");
				String specialType = physicalColumnElm.getAttribute("specialType");
				String extName = physicalColumnElm.getAttribute("extName");
				String sourceColumnRef = physicalColumnElm.getAttribute("sourceColumnRef");
				if (sourceColumnRef.isEmpty()) {
					sourceColumnRef = null;
				}
				String iconIndexphclClm = physicalColumnElm.getAttribute("iconIndex");
				if (iconIndexphclClm.isEmpty()) {
					iconIndexphclClm = null;
				}

				if (isloadMdsid) {
					physclClmnBuilder.append(physicalTableName).append("|").append(dataType).append("|")
							.append(physicalColumndMsid).append("|").append(physicalColumnName).append("|")
							.append(precision).append("|").append(specialType).append("|").append(extName).append("|")
							.append(sourceColumnRef).append("|").append(iconIndexphclClm).append(";\n");

				} else {
					mdsidNameMap.put(physicalColumndMsid, physicalColumnName);
				}

			}
			map.put("PhysicalColumn", physclClmnBuilder.toString());
			List<Element> physicalKeyNdList = XMLUtil.getChildElements(physicalTableElm, "PhysicalKey");
			for (Element physicalKeyElm : physicalKeyNdList) {

				String physicalKeyMdsid = physicalKeyElm.getAttribute("mdsid");
				String physicalKeyName = physicalKeyElm.getAttribute("name");

				Element refColumnsElm = XMLUtil.getChildElement(physicalKeyElm, "RefColumns");

				List<Element> refColumnNdList = XMLUtil.getChildElements(refColumnsElm, "RefColumn");
				for (Element refColumnElm : refColumnNdList) {

					String columnRef = refColumnElm.getAttribute("columnRef");
					String[] clmRefMdsid = columnRef.split(Pattern.quote("#"));
					String columnRefName = null;

					if (mdsidNameMap.containsKey(clmRefMdsid[1])) {
						columnRefName = mdsidNameMap.get(clmRefMdsid[1]);

					}

					String refId = refColumnElm.getAttribute("refId");

					physicalKeyBuilder.append(physicalTableName).append("|").append(physicalKeyMdsid).append("|")
							.append(physicalKeyName).append("|").append(columnRef).append("|").append(refId).append("|")
							.append(columnRefName).append(";\n");
				}
			}
			List<Element> physicalForeignKeyNdList = XMLUtil.getChildElements(physicalTableElm, "PhysicalForeignKey");
			for (Element physicalForeignKeyElm : physicalForeignKeyNdList) {

				String physicalFrnKeyMdsid = physicalForeignKeyElm.getAttribute("mdsid");
				String physicalFrnKeyName = physicalForeignKeyElm.getAttribute("name");
				String counterPartKeyRef = physicalForeignKeyElm.getAttribute("counterPartKeyRef");

				Element refClmnsElm = XMLUtil.getChildElement(physicalForeignKeyElm, "RefColumns");

				List<Element> refClmnsElmNdList = XMLUtil.getChildElements(refClmnsElm, "RefColumn");
				for (Element refClmnElm : refClmnsElmNdList) {

					String colmnRef = refClmnElm.getAttribute("columnRef");
					String[] colmnRefMdsid = colmnRef.split(Pattern.quote("#"));
					String colmnRefName = null;

					if (mdsidNameMap.containsKey(colmnRefMdsid[1])) {
						colmnRefName = mdsidNameMap.get(colmnRefMdsid[1]);
					}
					String rfId = refClmnElm.getAttribute("refId");
					if (isloadMdsid) {
						physicalForeignKeyBuilder.append(physicalTableName).append("|").append(physicalFrnKeyName)
								.append("|").append(physicalFrnKeyMdsid).append("|").append(counterPartKeyRef)
								.append("|").append(colmnRef).append("|").append(colmnRefName).append("|").append(rfId)
								.append(";\n");

					} else {
						mdsidNameMap.put(physicalFrnKeyMdsid, physicalFrnKeyName);
					}
				}
			}
			map.put("PhysicalForeignKey", physicalForeignKeyBuilder.toString());
			map.put("PhysicalKey", physicalKeyBuilder.toString());
		}
		map.put("PhysicalTable", physicalTblBuilder.toString());

		List<Element> connectionPoolNdList = XMLUtil.getChildElements(dECLAREElm, "ConnectionPool");
		for (Element connectionPoolElm : connectionPoolNdList) {
			String dataSource = connectionPoolElm.getAttribute("dataSource");
			String databaseRef = connectionPoolElm.getAttribute("databaseRef");
			String maxConn = connectionPoolElm.getAttribute("maxConn");
			String maxConnDiff = connectionPoolElm.getAttribute("maxConnDiff");
			String connectionPoolMdsid = connectionPoolElm.getAttribute("mdsid");
			String connectionPoolName = connectionPoolElm.getAttribute("name");
			String outputType = connectionPoolElm.getAttribute("outputType");
			String password = connectionPoolElm.getAttribute("password");
			String timeout = connectionPoolElm.getAttribute("timeout");
			String connectionPoolType = connectionPoolElm.getAttribute("type");
			String user = connectionPoolElm.getAttribute("user");
			String xmlRefreshInterval = connectionPoolElm.getAttribute("xmlRefreshInterval");

			Element descriptionElm = XMLUtil.getChildElement(connectionPoolElm, "Description");
			String description = descriptionElm.getTextContent();
			if (description.isEmpty()) {
				description = null;
			}

			if (isloadMdsid) {
				connectionPoolBuilder.append(connectionPoolName).append("|").append(maxConn).append("|")
						.append(connectionPoolType).append("|").append(connectionPoolMdsid).append("|")
						.append(maxConnDiff).append("|").append(outputType).append("|").append(timeout).append("|")
						.append(user).append("|").append(password).append("|").append(dataSource).append("|")
						.append(databaseRef).append("|").append(xmlRefreshInterval).append("|").append(description)
						.append(";\n");

			} else {
				mdsidNameMap.put(connectionPoolMdsid, connectionPoolName);
			}
		}
		map.put("ConnectionPool", connectionPoolBuilder.toString());

		Element businessModelElm = XMLUtil.getChildElement(dECLAREElm, "BusinessModel");

		String isAvailable = businessModelElm.getAttribute("isAvailable");
		String isClassicStar = businessModelElm.getAttribute("isClassicStar");
		String businessModelMdsid = businessModelElm.getAttribute("mdsid");
		String businessModelName = businessModelElm.getAttribute("name");

		Element descriptionElm = XMLUtil.getChildElement(businessModelElm, "Description");

		String description = descriptionElm.getTextContent();
		if (description.isEmpty()) {
			description = null;
		}
		if (isloadMdsid) {
			businessModelBuilder.append(businessModelName).append("|").append(businessModelMdsid).append("|")
					.append(isClassicStar).append("|").append(isAvailable).append("|").append(description)
					.append(";\n");
		} else {
			mdsidNameMap.put(businessModelMdsid, businessModelName);
		}

		map.put("BusinessModel", businessModelBuilder.toString());

		List<Element> logicalTableNdList = XMLUtil.getChildElements(dECLAREElm, "LogicalTable");
		for (Element logicalTableElm : logicalTableNdList) {

			String logicalTableMdsid = logicalTableElm.getAttribute("mdsid");
			String logicalTableName = logicalTableElm.getAttribute("name");
			String subjectAreaRef = logicalTableElm.getAttribute("subjectAreaRef");
			String x = logicalTableElm.getAttribute("x");
			String y = logicalTableElm.getAttribute("y");

			String[] subjectAreaRefMdsid = subjectAreaRef.split(Pattern.quote("#"));
			String subjectAreaRefName = null;

			if (mdsidNameMap.containsKey(subjectAreaRefMdsid[1])) {
				subjectAreaRefName = mdsidNameMap.get(subjectAreaRefMdsid[1]);
			}
			Element descriptinElm = XMLUtil.getChildElement(logicalTableElm, "Description");

			String descriptin = descriptinElm.getTextContent();

			if (isloadMdsid) {
				logicalTblBuilder.append(logicalTableName).append("|").append(logicalTableMdsid).append("|")
						.append(subjectAreaRef).append("|").append(subjectAreaRefName).append("|").append(x).append("|")
						.append(y).append("|").append(descriptin).append(";\n");

			} else {
				mdsidNameMap.put(logicalTableMdsid, logicalTableName);
			}

			List<Element> logicalColumnNdList = XMLUtil.getChildElements(logicalTableElm, "LogicalColumn");
			for (Element logicalColumnElm : logicalColumnNdList) {

				String isWriteable = logicalColumnElm.getAttribute("isWriteable");
				String logicalColumnMdsid = logicalColumnElm.getAttribute("mdsid");
				String logicalColumnName = logicalColumnElm.getAttribute("name");
				Element dscriptionElm = XMLUtil.getChildElement(logicalColumnElm, "Description");

				String dscription = dscriptionElm.getTextContent();

				if (isloadMdsid) {
					logicalClmBuilder.append(logicalTableName).append("|").append(logicalColumnName).append("|")
							.append(logicalColumnMdsid).append("|").append(isWriteable).append("|").append(dscription)
							.append(";\n");
				} else {
					mdsidNameMap.put(logicalColumnMdsid, logicalColumnName);
				}

				List<Element> measureDefnNdList = XMLUtil.getChildElements(logicalColumnElm, "MeasureDefn");
				for (Element measureDefnElm : measureDefnNdList) {

					String measureDefnMdsid = measureDefnElm.getAttribute("mdsid");
					String measureDefnName = measureDefnElm.getAttribute("name");
					String isCommutative = measureDefnElm.getAttribute("isCommutative");

					Element aggrRuleElm = XMLUtil.getChildElement(measureDefnElm, "AggrRule");

					String aggrRuleMdsid = aggrRuleElm.getAttribute("mdsid");
					String aggrRuleName = aggrRuleElm.getAttribute("name");
					String isDefault = aggrRuleElm.getAttribute("isDefault");

					Element exprElm = XMLUtil.getChildElement(aggrRuleElm, "Expr");

					String exprMdsid = exprElm.getAttribute("mdsid");
					String exprName = exprElm.getAttribute("name");

					Element exprTextElm = XMLUtil.getChildElement(exprElm, "ExprText");
					String contOfExprTextElm = exprTextElm.getTextContent();
					Element exprTextDescElm = XMLUtil.getChildElement(exprElm, "ExprTextDesc");
					String contOfExprTextDescElm = exprTextDescElm.getTextContent();

					Element objectRefListElm = XMLUtil.getChildElement(exprElm, "ObjectRefList");
					Element refObjectElm = XMLUtil.getChildElement(objectRefListElm, "RefObject");

					String objectRef = refObjectElm.getAttribute("objectRef");

					String[] objectRefMdsid = objectRef.split(Pattern.quote("#"));
					String objectRefName = null;

					if (mdsidNameMap.containsKey(objectRefMdsid[1])) {
						objectRefName = mdsidNameMap.get(objectRefMdsid[1]);

					}
					String objectTypeId = refObjectElm.getAttribute("objectTypeId");
					String refId = refObjectElm.getAttribute("refId");
					if (isloadMdsid) {
						measureDefnBuilder.append(logicalColumnName).append("|").append(measureDefnName).append("|")
								.append(measureDefnMdsid).append("|").append(isCommutative).append("|")
								.append(exprMdsid).append("|").append(exprName).append("|").append(aggrRuleName)
								.append("|").append(aggrRuleMdsid).append("|").append(isDefault).append("|")
								.append(contOfExprTextElm).append("|").append(contOfExprTextDescElm).append("|")
								.append(refId).append("|").append(objectTypeId).append("|").append(objectRef)
								.append("|").append(objectRefName).append(";\n");
					} else {
						mdsidNameMap.put(measureDefnMdsid, measureDefnName);
						mdsidNameMap.put(aggrRuleMdsid, aggrRuleName);
						mdsidNameMap.put(exprMdsid, exprName);
					}
				}
				map.put("MeasureDefn", measureDefnBuilder.toString());

				List<Element> attributeDefnNdList = XMLUtil.getChildElements(logicalColumnElm, "AttributeDefn");
				for (Element attributeDefnElm : attributeDefnNdList) {

					String attributeDefnMdsid = attributeDefnElm.getAttribute("mdsid");
					String attributeDefnName = attributeDefnElm.getAttribute("name");

					Element exprTextElm = XMLUtil.getChildElement(attributeDefnElm, "ExprText");
					String contOfExprTextElm = exprTextElm.getTextContent();
					Element exprTextDescElm = XMLUtil.getChildElement(attributeDefnElm, "ExprTextDesc");
					String contOfExprTextDescElm = exprTextDescElm.getTextContent();

					Element objectRefListElm = XMLUtil.getChildElement(attributeDefnElm, "ObjectRefList");
					Element refObjectElm = XMLUtil.getChildElement(objectRefListElm, "RefObject");

					String objectRef = refObjectElm.getAttribute("objectRef");

					String[] objectRfMdsid = objectRef.split(Pattern.quote("#"));
					String objectRfName = null;

					if (mdsidNameMap.containsKey(objectRfMdsid[1])) {
						objectRfName = mdsidNameMap.get(objectRfMdsid[1]);
					}
					String objectTypeId = refObjectElm.getAttribute("objectTypeId");
					String refId = refObjectElm.getAttribute("refId");

					if (isloadMdsid) {
						attributeDefnBuilder.append(logicalColumnName).append("|").append(attributeDefnName).append("|")
								.append(attributeDefnMdsid).append("|").append(contOfExprTextElm).append("|")
								.append(contOfExprTextDescElm).append("|").append(objectRef).append("|")
								.append(objectRfName).append("|").append(objectTypeId).append("|").append(refId)
								.append(";\n");
					} else {
						mdsidNameMap.put(attributeDefnMdsid, attributeDefnName);
					}
				}
				map.put("AttributeDefn", attributeDefnBuilder.toString());
			}
			map.put("LogicalColumn", logicalClmBuilder.toString());
			List<Element> logicalKeyNdList = XMLUtil.getChildElements(logicalTableElm, "LogicalKey");
			for (Element logicalKeyElm : logicalKeyNdList) {

				String isPrimary = logicalKeyElm.getAttribute("isPrimary");
				String logicalKeyMdsid = logicalKeyElm.getAttribute("mdsid");
				String logicalKeyName = logicalKeyElm.getAttribute("name");

				Element refColumnsElm = XMLUtil.getChildElement(logicalKeyElm, "RefColumns");
				Element refLogicalColumnElm = XMLUtil.getChildElement(refColumnsElm, "RefLogicalColumn");

				String logicalColumnRef = refLogicalColumnElm.getAttribute("logicalColumnRef");
				String[] lgclClmnRefMdsid = logicalColumnRef.split(Pattern.quote("#"));
				String lgclClmnRefName = null;

				if (mdsidNameMap.containsKey(lgclClmnRefMdsid[1])) {
					lgclClmnRefName = mdsidNameMap.get(lgclClmnRefMdsid[1]);

				}
				String refId = refLogicalColumnElm.getAttribute("refId");

				if (isloadMdsid) {
					logicalKeyBuilder.append(logicalTableName).append("|").append(logicalKeyName).append("|")
							.append(logicalKeyMdsid).append("|").append(isPrimary).append("|").append(logicalColumnRef)
							.append("|").append(lgclClmnRefName).append("|").append(refId).append(";\n");

				} else {
					mdsidNameMap.put(logicalKeyMdsid, logicalKeyName);
				}

			}
			map.put("LogicalKey", logicalKeyBuilder.toString());

			List<Element> refTableSourcesNdList = XMLUtil.getChildElements(logicalTableElm, "RefTableSources");
			for (Element refTableSourcesElm : refTableSourcesNdList) {

				List<Element> refLogicalTableSourceNdList = XMLUtil.getChildElements(refTableSourcesElm,
						"RefLogicalTableSource");
				for (Element refTableSourceElm : refLogicalTableSourceNdList) {
					String logicalTableSourceRef = refTableSourceElm.getAttribute("logicalTableSourceRef");
					String[] lgclTblSrceMdsid = logicalTableSourceRef.split(Pattern.quote("#"));
					String lgclTblSrceRefName = null;

					if (mdsidNameMap.containsKey(lgclTblSrceMdsid[1])) {
						lgclTblSrceRefName = mdsidNameMap.get(lgclTblSrceMdsid[1]);

					}
					String refId = refTableSourceElm.getAttribute("refId");
					if (isloadMdsid) {
						refTableSourceBuilder.append(logicalTableName).append("|").append(lgclTblSrceRefName)
								.append("|").append(refId).append("|").append(logicalTableSourceRef).append(";\n");
					}
				}
			}
			map.put("refTableSource", refTableSourceBuilder.toString());
		}
		map.put("LogicalTable", logicalTblBuilder.toString());

		List<Element> dimensionNdList = XMLUtil.getChildElements(dECLAREElm, "Dimension");

		for (Element dimensionElm : dimensionNdList) {
			String dimensionMdsid = dimensionElm.getAttribute("mdsid");
			String dimensionName = dimensionElm.getAttribute("name");
			String isTimeDim = dimensionElm.getAttribute("isTimeDim");
			String isValueBased = dimensionElm.getAttribute("isValueBased");
			String isRagged = dimensionElm.getAttribute("isRagged");
			String isSkipped = dimensionElm.getAttribute("isSkipped");
			String defaultRootLevelRef = dimensionElm.getAttribute("defaultRootLevelRef");

			String[] defaultRootLevelRefMdsid = defaultRootLevelRef.split(Pattern.quote("#"));
			String defaultRootLevelRefName = null;

			if (mdsidNameMap.containsKey(defaultRootLevelRefMdsid[1])) {
				defaultRootLevelRefName = mdsidNameMap.get(defaultRootLevelRefMdsid[1]);
			}

			String subjctAreaRef = dimensionElm.getAttribute("subjectAreaRef");
			String[] subjctAreaRefMdsid = subjctAreaRef.split(Pattern.quote("#"));
			String subjctAreaRefName = null;

			if (mdsidNameMap.containsKey(subjctAreaRefMdsid[1])) {
				subjctAreaRefName = mdsidNameMap.get(subjctAreaRefMdsid[1]);
			}

			Element desriptionElm = XMLUtil.getChildElement(dimensionElm, "Description");

			String desription = desriptionElm.getTextContent();

			List<Element> logicalLevelNdList = XMLUtil.getChildElements(dimensionElm, "LogicalLevel");
			String lgclLvlRefName = null;
			String refPreferredDrillDownlogicalLevelRefName = null;

			for (Element logicalLevelElm : logicalLevelNdList) {

				String logicalLevelMdsid = logicalLevelElm.getAttribute("mdsid");
				String logicalLevelName = logicalLevelElm.getAttribute("name");
				String isGTA = logicalLevelElm.getAttribute("isGTA");
				String memberCount = logicalLevelElm.getAttribute("memberCount");
				String levelConst = logicalLevelElm.getAttribute("levelConst");

				Element refChldLvlsListElm = XMLUtil.getChildElement(logicalLevelElm, "RefChildLevels");
				Element refLgiclLvlElm = XMLUtil.getChildElement(refChldLvlsListElm, "RefLogicalLevel");
				String logicalLevelRefId = refLgiclLvlElm.getAttribute("refId");
				String logicalLevelRef = refLgiclLvlElm.getAttribute("logicalLevelRef");

				if (logicalLevelRef != null && logicalLevelRef.length() != 0) {
					String[] lgclLvlRefMdsid = logicalLevelRef.split(Pattern.quote("#"));
					if (mdsidNameMap.containsKey(lgclLvlRefMdsid[1])) {
						lgclLvlRefName = mdsidNameMap.get(lgclLvlRefMdsid[1]);
					}
				}

				Element refPreferredDrillDownListElm = XMLUtil.getChildElement(logicalLevelElm,
						"RefPreferredDrillDown");
				Element refPreferredDrillDownElm = XMLUtil.getChildElement(refPreferredDrillDownListElm,
						"RefLogicalLevel");
				String refPreferredDrillDownRefId = refPreferredDrillDownElm.getAttribute("refId");
				String refPreferredDrillDownlogicalLevelRef = refPreferredDrillDownElm.getAttribute("logicalLevelRef");

				if (refPreferredDrillDownlogicalLevelRef != null
						&& refPreferredDrillDownlogicalLevelRef.length() != 0) {
					String[] lgclLvlRefDrillDownMdsid = refPreferredDrillDownlogicalLevelRef.split(Pattern.quote("#"));
					if (mdsidNameMap.containsKey(lgclLvlRefDrillDownMdsid[1])) {
						refPreferredDrillDownlogicalLevelRefName = mdsidNameMap.get(lgclLvlRefDrillDownMdsid[1]);
					}
				}

				String lgclClmnrefId = null;
				String logicalClumnRef = null;
				String logicalClumnRefName = null;
				StringBuilder refLgclClmBuilder = new StringBuilder();
				List<String> refLgclClmList = new ArrayList<>();
				Element refLgclClmnsListElm = XMLUtil.getChildElement(logicalLevelElm, "RefLogicalColumns");
				List<Element> refLgclClmnNdListElm = XMLUtil.getChildElements(refLgclClmnsListElm, "RefLogicalColumn");
				for (Element refLgclClmnElm : refLgclClmnNdListElm) {
					lgclClmnrefId = refLgclClmnElm.getAttribute("refId");
					logicalClumnRef = refLgclClmnElm.getAttribute("logicalColumnRef");
					if (logicalClumnRef != null && logicalClumnRef.length() != 0) {
						String[] logicalClumnRefMdsid = logicalClumnRef.split(Pattern.quote("#"));
						if (mdsidNameMap.containsKey(logicalClumnRefMdsid[1])) {
							logicalClumnRefName = mdsidNameMap.get(logicalClumnRefMdsid[1]);

						}
					}
					refLgclClmBuilder.append("{lgclClmnrefId:" + lgclClmnrefId + " , logicalClumnRef:" + logicalClumnRef
							+ "logicalClumnRefName:" + logicalClumnRefName + "}");
					refLgclClmList.add(refLgclClmBuilder.toString());
				}

				String logicalKeylogicalColumnRefName = null;
				List<Element> logicalKeyNList = XMLUtil.getChildElements(logicalLevelElm, "LogicalKey");
				if (logicalKeyNList != null)
					for (Element logicalKeyElm : logicalKeyNList) {

						String logicalKeyMdsid = logicalKeyElm.getAttribute("mdsid");
						String logicalKeyName = logicalKeyElm.getAttribute("name");
						String isPrimary = logicalKeyElm.getAttribute("isPrimary");
						String isChronKey = logicalKeyElm.getAttribute("isChronKey");
						String isForDrillDown = logicalKeyElm.getAttribute("isForDrillDown");

						Element refClmnsListElm = XMLUtil.getChildElement(logicalKeyElm, "RefColumns");
						Element refLgcalClmnElm = XMLUtil.getChildElement(refClmnsListElm, "RefLogicalColumn");

						String logicalKeyRefId = refLgcalClmnElm.getAttribute("refId");
						if (logicalKeyRefId.isEmpty()) {
							logicalKeyRefId = null;
						}
						String logicalKeylogicalColumnRef = refLgcalClmnElm.getAttribute("logicalColumnRef");

						if (logicalKeylogicalColumnRef != null && logicalKeylogicalColumnRef.length() != 0) {
							String[] lgclClmnRefMdsid = logicalKeylogicalColumnRef.split(Pattern.quote("#"));
							if (mdsidNameMap.containsKey(lgclClmnRefMdsid[1])) {
								logicalKeylogicalColumnRefName = mdsidNameMap.get(lgclClmnRefMdsid[1]);

							}
						}
						if (logicalKeylogicalColumnRef.isEmpty()) {
							logicalKeylogicalColumnRef = null;
						}

						dmnsnLogicalKeyBuilder.append(logicalLevelName).append("|").append(logicalKeyMdsid).append("|")
								.append(logicalKeyName).append("|").append(isPrimary).append("|").append(isChronKey)
								.append("|").append(isForDrillDown).append("|").append(logicalKeyRefId).append("|")
								.append(logicalKeylogicalColumnRef).append("|").append(logicalKeylogicalColumnRefName)
								.append(";\n");
					}
				map.put("DmnsnLogicalKey", dmnsnLogicalKeyBuilder.toString());

				if (isloadMdsid) {
					logicalLevelBuilder.append(dimensionName).append("|").append(logicalLevelName).append("|")
							.append(logicalLevelMdsid).append("|").append(isGTA).append("|").append(memberCount)
							.append("|").append(levelConst).append("|").append(logicalLevelRefId).append("|")
							.append(logicalLevelRef).append("|").append(lgclLvlRefName).append("|")
							.append(refLgclClmList).append("|").append(logicalClumnRefName).append("|")
							.append(refPreferredDrillDownRefId).append("|").append(refPreferredDrillDownlogicalLevelRef)
							.append("|").append(refPreferredDrillDownlogicalLevelRefName).append(";\n");

				} else {
					mdsidNameMap.put(logicalLevelMdsid, logicalLevelName);

				}

			}

			map.put("LogicalLevel", logicalLevelBuilder.toString());
			if (isloadMdsid) {
				dimensionBuilder.append(dimensionName).append("|").append(dimensionMdsid).append("|").append(isTimeDim)
						.append("|").append(isValueBased).append("|").append(isRagged).append("|").append(isSkipped)
						.append("|").append(defaultRootLevelRef).append("|").append(defaultRootLevelRefName).append("|")
						.append(subjctAreaRef).append("|").append(subjctAreaRefName).append("|").append(desription)
						.append(";\n");
			} else {
				mdsidNameMap.put(dimensionMdsid, dimensionName);
			}

		}
		map.put("Dimension", dimensionBuilder.toString());

		List<Element> logicalTableSourceNdList = XMLUtil.getChildElements(dECLAREElm, "LogicalTableSource");
		for (Element logicalTableSourceElm : logicalTableSourceNdList) {

			String isActive = logicalTableSourceElm.getAttribute("isActive");
			String logicalTableRef = logicalTableSourceElm.getAttribute("logicalTableRef");
			String[] lgclTblRefMdsid = logicalTableRef.split(Pattern.quote("#"));
			String lgclTblRefName = null;

			if (mdsidNameMap.containsKey(lgclTblRefMdsid[1])) {
				lgclTblRefName = mdsidNameMap.get(lgclTblRefMdsid[1]);

			}

			String logicalTableSourceMdsid = logicalTableSourceElm.getAttribute("mdsid");
			String logicalTableSourceName = logicalTableSourceElm.getAttribute("name");

			if (isloadMdsid) {
				logicalTblSrceBuilder.append(logicalTableSourceName).append("|").append(logicalTableSourceMdsid)
						.append("|").append(isActive).append("|").append(logicalTableRef).append("|")
						.append(lgclTblRefName).append(";\n");
			} else {
				mdsidNameMap.put(logicalTableSourceMdsid, logicalTableSourceName);
			}
			List<Element> columnMappingNdList = XMLUtil.getChildElements(logicalTableSourceElm, "ColumnMapping");
			for (Element columnMappingElm : columnMappingNdList) {

				List<String> logicalColmExprList = new ArrayList<>();
				List<String> exprList = new ArrayList<>();
				String logiclExprDesc = "";
				String lgclTblObjRefName = "";
				List<Element> logicalColumnExprNdList = XMLUtil.getChildElements(columnMappingElm, "LogicalColumnExpr");
				for (Element logicalColumnExprElm : logicalColumnExprNdList) {

					StringBuilder logicalClmExprBuilder = new StringBuilder();
					String logicalColumnExprMdsid = logicalColumnExprElm.getAttribute("mdsid");
					String logicalColumnExprName = logicalColumnExprElm.getAttribute("name");

					Element exprTextLgclClmElm = XMLUtil.getChildElement(logicalColumnExprElm, "ExprText");

					String lgclClmExprText = exprTextLgclClmElm.getTextContent();
					Element exprTextDescLgclClmElm = XMLUtil.getChildElement(logicalColumnExprElm, "ExprTextDesc");
					logiclExprDesc = exprTextDescLgclClmElm.getTextContent();
					String lgclClmExprTextDesc = exprTextDescLgclClmElm.getTextContent();
					Element objectRefListElm = XMLUtil.getChildElement(logicalColumnExprElm, "ObjectRefList");

					Element refObjectLgclClmElm = XMLUtil.getChildElement(objectRefListElm, "RefObject");

					String objectRef = refObjectLgclClmElm.getAttribute("objectRef");
					String[] objRefMdsid = objectRef.split(Pattern.quote("#"));

					if (mdsidNameMap.containsKey(objRefMdsid[1])) {
						lgclTblObjRefName = mdsidNameMap.get(objRefMdsid[1]);
					}
					String objectTypeId = refObjectLgclClmElm.getAttribute("objectTypeId");
					String refId = refObjectLgclClmElm.getAttribute("refId");

					mdsidNameMap.put(logicalColumnExprMdsid, logicalColumnExprName);
					logicalClmExprBuilder
							.append("{mdsid:" + logicalColumnExprMdsid + ", name:" + logicalColumnExprName
									+ ",ExprText:" + lgclClmExprText + ",ExprTextDesc:" + lgclClmExprTextDesc)
							.append("refObject:" + objectRef + ",referenceObjName: " + lgclTblObjRefName
									+ ",objectTypeId:" + objectTypeId + ",refId:" + refId + "}");
					logicalColmExprList.add(logicalClmExprBuilder.toString());
				}
				String physicalExprDesc = "";
				String physclTblObjRefName = "";
				List<Element> exprNdList = XMLUtil.getChildElements(columnMappingElm, "Expr");
				for (Element exprElm : exprNdList) {
					StringBuilder exprBuilder = new StringBuilder();

					String exprMdsid = exprElm.getAttribute("mdsid");
					String exprName = exprElm.getAttribute("name");

					Element exprText = XMLUtil.getChildElement(exprElm, "ExprText");
					String exprTextElm = exprText.getTextContent();
					Element exprTextDesc = XMLUtil.getChildElement(exprElm, "ExprTextDesc");
					physicalExprDesc = exprTextDesc.getTextContent();
					String exprTextDescElm = exprTextDesc.getTextContent();
					Element objectRefListExprElm = XMLUtil.getChildElement(exprElm, "ObjectRefList");

					Element refObjectElm = XMLUtil.getChildElement(objectRefListExprElm, "RefObject");

					String objectRef = refObjectElm.getAttribute("objectRef");
					String[] objectRefMdsid = objectRef.split(Pattern.quote("#"));

					if (mdsidNameMap.containsKey(objectRefMdsid[1])) {
						physclTblObjRefName = mdsidNameMap.get(objectRefMdsid[1]);

					}

					String objectTypeId = refObjectElm.getAttribute("objectTypeId");
					String refId = refObjectElm.getAttribute("refId");
					mdsidNameMap.put(exprMdsid, exprName);
					exprBuilder
							.append("{exprMdsid:" + exprMdsid + ", name:" + exprName + ", ExprText:" + exprTextElm
									+ ", ExprTextDesc:" + exprTextDescElm + ", objectRef:" + objectRef
									+ ", objectRefName:" + physclTblObjRefName)
							.append(", objectTypeId:" + objectTypeId + ", refId:" + refId + "}");
					exprList.add(exprBuilder.toString());
				}
				if (isloadMdsid) {
					columnMappingBuilder.append(logicalTableSourceName).append("|").append(logiclExprDesc).append("|")
							.append(physicalExprDesc).append("|").append(logicalColmExprList).append("|")
							.append(lgclTblObjRefName).append("|").append(exprList).append("|")
							.append(physclTblObjRefName).append(";\n");
				}
			}

			map.put("ColumnMapping", columnMappingBuilder.toString());

			List<String> linkList = new ArrayList<>();
			List<String> whereClauseList = new ArrayList<>();
			String startNodeTableRefName = "";
			List<Element> linkNdList = XMLUtil.getChildElements(logicalTableSourceElm, "Link");
			for (Element linkElm : linkNdList) {
				StringBuilder linkBuilder = new StringBuilder();

				String startNodeTableRef = linkElm.getAttribute("startNodeTableRef");
				String[] startNodeTableRefMdsid = startNodeTableRef.split(Pattern.quote("#"));

				if (mdsidNameMap.containsKey(startNodeTableRefMdsid[1])) {
					startNodeTableRefName = mdsidNameMap.get(startNodeTableRefMdsid[1]);

				}

				linkBuilder.append("{startNodeTableRef:" + startNodeTableRef + ", startNodeTableRefName:"
						+ startNodeTableRefName + "}");
				linkList.add(linkBuilder.toString());

			}
			List<Element> whereClauseNdList = XMLUtil.getChildElements(logicalTableSourceElm, "WhereClause");
			String whereExprText = "";
			String whereExprTextDesc = "";
			for (Element whereClauseElm : whereClauseNdList) {
				StringBuilder whereClauseBuilder = new StringBuilder();

				String whereClauseMdsid = whereClauseElm.getAttribute("mdsid");
				String whereClauseName = whereClauseElm.getAttribute("name");

				Element exprTextWhereElm = XMLUtil.getChildElement(whereClauseElm, "ExprText");
				whereExprText = exprTextWhereElm.getTextContent();
				if (whereExprText.isEmpty()) {
					whereExprText = null;
				}

				Element exprTextDescElm = XMLUtil.getChildElement(whereClauseElm, "ExprTextDesc");
				whereExprTextDesc = exprTextDescElm.getTextContent();
				if (whereExprTextDesc.isEmpty()) {
					whereExprTextDesc = null;
				}
				mdsidNameMap.put(whereClauseMdsid, whereClauseName);
				whereClauseBuilder.append("{mdsid: " + whereClauseMdsid + ", name:" + whereClauseName + ", exprText: "
						+ whereExprText + ", exprTextDesc:" + whereExprTextDesc + "}");
				whereClauseList.add(whereClauseBuilder.toString());

			}
			List<String> groupByList = new ArrayList<>();
			List<Element> groupByNdList = XMLUtil.getChildElements(logicalTableSourceElm, "GroupBy");
			String groupByExprText = "";
			String groupByexprTextDesc = "";
			for (Element groupByElm : groupByNdList) {
				StringBuilder groupByBuilder = new StringBuilder();

				String groupByMdsid = groupByElm.getAttribute("mdsid");
				String groupByName = groupByElm.getAttribute("name");

				Element exprTextElm = XMLUtil.getChildElement(groupByElm, "ExprText");

				groupByExprText = exprTextElm.getTextContent();
				Element exprTextDescGroupElm = XMLUtil.getChildElement(groupByElm, "ExprTextDesc");
				groupByexprTextDesc = exprTextDescGroupElm.getTextContent();

				mdsidNameMap.put(groupByMdsid, groupByName);

				Element objectRefListExprElm = XMLUtil.getChildElement(groupByElm, "ObjectRefList");

				List<Element> refObjectGroupByNdList = XMLUtil.getChildElements(objectRefListExprElm, "RefObject");
				List<String> groupRefList = new ArrayList<>();
				for (Element refObjectGroupByElm : refObjectGroupByNdList) {
					StringBuilder groupRefBuilder = new StringBuilder();

					String objectRef = refObjectGroupByElm.getAttribute("objectRef");
					String objectRefName = null;
					String objectTypeId = null;
					String refId = null;
					if (objectRef != null && objectRef.length() != 0) {
						String[] objectRefMdsid = objectRef.split(Pattern.quote("#"));

						if (mdsidNameMap.containsKey(objectRefMdsid[1])) {
							objectRefName = mdsidNameMap.get(objectRefMdsid[1]);
						}

						objectTypeId = refObjectGroupByElm.getAttribute("objectTypeId");
						refId = refObjectGroupByElm.getAttribute("refId");
					}
					groupRefBuilder.append("{objectRefName:" + objectRefName + ", objectRef:" + objectRef
							+ ", objectTypeId:" + objectTypeId + ", refId:" + refId + "}");
					groupRefList.add(groupRefBuilder.toString());
					groupByLgclTblSrcBuilder.append(refId).append("|").append(objectTypeId).append("|")
							.append(objectRef).append("|").append(objectRefName).append(";\n");

				}
				map.put("LgclTblSrcGroupBy", groupByLgclTblSrcBuilder.toString());
				groupByBuilder.append("{mdsid:" + groupByMdsid + ", name: " + groupByName + ", ExprText:"
						+ groupByExprText + ", ExprTextDesc: " + groupByexprTextDesc + ", groupReference: "
						+ groupRefList + " }");
				groupByList.add(groupByBuilder.toString());
			}

			List<Element> fragmentContentNdList = XMLUtil.getChildElements(logicalTableSourceElm, "FragmentContent");
			List<String> fragmentContList = new ArrayList<>();
			String fragmntContentExprText = "";
			String fragmntContentExprTextDesc = "";
			for (Element fragmentContentElm : fragmentContentNdList) {
				StringBuilder fragmentContentBuilder = new StringBuilder();
				String fragmentContentMdsid = fragmentContentElm.getAttribute("mdsid");
				String fragmentContentName = fragmentContentElm.getAttribute("name");

				Element exprTextFrgmntElm = XMLUtil.getChildElement(fragmentContentElm, "ExprText");

				fragmntContentExprText = exprTextFrgmntElm.getTextContent();
				if (fragmntContentExprText.isEmpty()) {
					fragmntContentExprText = null;
				}
				Element exprTextDescFrgmntElm = XMLUtil.getChildElement(fragmentContentElm, "ExprTextDesc");

				fragmntContentExprTextDesc = exprTextDescFrgmntElm.getTextContent();
				if (fragmntContentExprTextDesc.isEmpty()) {
					fragmntContentExprTextDesc = null;
				}
				mdsidNameMap.put(fragmentContentMdsid, fragmentContentName);
				fragmentContentBuilder
						.append("{mdsid:" + fragmentContentMdsid + ",name: " + fragmentContentName + ", ExprText: "
								+ fragmntContentExprText + ",ExprTextDesc: " + fragmntContentExprTextDesc + "}");
				fragmentContList.add(fragmentContentBuilder.toString());
			}
			if (isloadMdsid) {
				logicalTblFragmntBuilder.append(logicalTableSourceName).append("|").append(linkList).append("|")
						.append(whereClauseList).append("|").append(groupByList).append("|").append(fragmentContList)
						.append("|").append(groupByexprTextDesc).append("|").append(groupByExprText).append("|")
						.append(startNodeTableRefName).append("|").append(fragmntContentExprText).append("|")
						.append(fragmntContentExprTextDesc).append("|").append(whereExprText).append("|")
						.append(whereExprTextDesc).append(";\n");
			}

			map.put("LogicalTblSrcChild", logicalTblFragmntBuilder.toString());
		}
		map.put("LogicalTableSource", logicalTblSrceBuilder.toString());

		List<Element> logicalComplexJoinNdList = XMLUtil.getChildElements(dECLAREElm, "LogicalComplexJoin");
		for (Element logicalComplexJoinElm : logicalComplexJoinNdList) {

			String isAggregate1 = logicalComplexJoinElm.getAttribute("isAggregate1");
			String isAggregate2 = logicalComplexJoinElm.getAttribute("isAggregate2");
			String logicalTable1Ref = logicalComplexJoinElm.getAttribute("logicalTable1Ref");
			String[] lgclTbl1RefMdsid = logicalTable1Ref.split(Pattern.quote("#"));
			String lgclTbl1RefName = null;

			if (mdsidNameMap.containsKey(lgclTbl1RefMdsid[1])) {
				lgclTbl1RefName = mdsidNameMap.get(lgclTbl1RefMdsid[1]);
			}
			String logicalTable2Ref = logicalComplexJoinElm.getAttribute("logicalTable2Ref");
			String[] lgclTbl2RefMdsid = logicalTable2Ref.split(Pattern.quote("#"));
			String lgclTbl2RefName = null;

			if (mdsidNameMap.containsKey(lgclTbl2RefMdsid[1])) {
				lgclTbl2RefName = mdsidNameMap.get(lgclTbl2RefMdsid[1]);
			}
			String logicalComplexJoinMdsid = logicalComplexJoinElm.getAttribute("mdsid");
			String multiplicity1 = logicalComplexJoinElm.getAttribute("multiplicity1");
			String multiplicity2 = logicalComplexJoinElm.getAttribute("multiplicity2");
			String logicalComplexJoinName = logicalComplexJoinElm.getAttribute("name");
			String logicalComplexJoinType = logicalComplexJoinElm.getAttribute("type");

			if (isloadMdsid) {
				logicalComplexJoinBuilder.append(logicalComplexJoinName).append("|").append(logicalComplexJoinMdsid)
						.append("|").append(isAggregate1).append("|").append(isAggregate2).append("|")
						.append(multiplicity1).append("|").append(multiplicity2).append("|")
						.append(logicalComplexJoinType).append("|").append(logicalTable1Ref).append("|")
						.append(lgclTbl1RefName).append("|").append(logicalTable2Ref).append("|")
						.append(lgclTbl2RefName).append(";\n");
			} else {
				mdsidNameMap.put(logicalComplexJoinMdsid, logicalComplexJoinName);
			}
		}
		map.put("LogicalComplexJoin", logicalComplexJoinBuilder.toString());

		List<Element> presentationCatalogNdList = XMLUtil.getChildElements(dECLAREElm, "PresentationCatalog");
		for (Element presentationCatalogElm : presentationCatalogNdList) {
			String hasDispDescription = presentationCatalogElm.getAttribute("hasDispDescription");
			String hasDispName = presentationCatalogElm.getAttribute("hasDispName");
			String isAutoAggr = presentationCatalogElm.getAttribute("isAutoAggr");
			String presentationCatalogMdsid = presentationCatalogElm.getAttribute("mdsid");
			String presentationCatalogName = presentationCatalogElm.getAttribute("name");
			String subjectAreRef = presentationCatalogElm.getAttribute("subjectAreaRef");
			String[] subjectAreRefMdsid = subjectAreRef.split(Pattern.quote("#"));
			String subjctAreRefName = null;

			if (mdsidNameMap.containsKey(subjectAreRefMdsid[1])) {
				subjctAreRefName = mdsidNameMap.get(subjectAreRefMdsid[1]);
			}
			String defaultFactColumnRef = presentationCatalogElm.getAttribute("defaultFactColumnRef");
			String[] defaultFactColumnRefMdsid = defaultFactColumnRef.split(Pattern.quote("#"));
			String defaultFactColumnRefName = null;

			if (mdsidNameMap.containsKey(defaultFactColumnRefMdsid[1])) {
				defaultFactColumnRefName = mdsidNameMap.get(defaultFactColumnRefMdsid[1]);
			}
			Element desriptionElm = XMLUtil.getChildElement(presentationCatalogElm, "Description");

			String desription = desriptionElm.getTextContent();
			Element visibilityFilterElm = XMLUtil.getChildElement(presentationCatalogElm, "VisibilityFilter");
			String visibilityMdsid = visibilityFilterElm.getAttribute("mdsid");
			String visibilityName = visibilityFilterElm.getAttribute("name");
			Element exprTextVisibilityElm = XMLUtil.getChildElement(visibilityFilterElm, "ExprText");
			String visibilityExprText = exprTextVisibilityElm.getTextContent();
			Element exprTextDescVisibilityElm = XMLUtil.getChildElement(visibilityFilterElm, "ExprTextDesc");
			String visibilityExprTextDesc = exprTextDescVisibilityElm.getTextContent();

			Element refTablesElm = XMLUtil.getChildElement(presentationCatalogElm, "RefTables");

			List<Element> refPresentationTableNdList = XMLUtil.getChildElements(refTablesElm, "RefPresentationTable");
			for (Element refPresentationTableElm : refPresentationTableNdList) {

				String presentationTableRef = refPresentationTableElm.getAttribute("presentationTableRef");
				String[] prsntionTableRefMdsid = presentationTableRef.split(Pattern.quote("#"));
				String prsntionTableRefName = null;

				if (mdsidNameMap.containsKey(prsntionTableRefMdsid[1])) {
					prsntionTableRefName = mdsidNameMap.get(prsntionTableRefMdsid[1]);
				}
				String refId = refPresentationTableElm.getAttribute("refId");

				if (isloadMdsid) {
					presentationCatalogBuilder.append(presentationCatalogName).append("|")
							.append(presentationCatalogMdsid).append("|").append(hasDispName).append("|")
							.append(isAutoAggr).append("|").append(hasDispDescription).append("|").append(subjectAreRef)
							.append("|").append(subjctAreRefName).append("|").append(defaultFactColumnRef).append("|")
							.append(defaultFactColumnRefName).append("|").append(desription).append("|")
							.append(visibilityMdsid).append("|").append(visibilityName).append("|")
							.append(visibilityExprText).append("|").append(visibilityExprTextDesc).append("|")
							.append(refId).append("|").append(presentationTableRef).append("|")
							.append(prsntionTableRefName).append(";\n");

				} else {
					mdsidNameMap.put(visibilityMdsid, visibilityName);
				}

			}
			map.put("presentationcatalog", presentationCatalogBuilder.toString());
		}

		List<Element> presentationTableNdList = XMLUtil.getChildElements(dECLAREElm, "PresentationTable");
		for (Element presentationTableElm : presentationTableNdList) {

			String presentationTableContainerRef = presentationTableElm.getAttribute("containerRef");

			String[] prsntionTableCntnerRefMdsid = presentationTableContainerRef.split(Pattern.quote("#"));
			String prsntionTableRefName = null;

			if (mdsidNameMap.containsKey(prsntionTableCntnerRefMdsid[1])) {
				prsntionTableRefName = mdsidNameMap.get(prsntionTableCntnerRefMdsid[1]);
			}
			String presentationTableHasDispDescription = presentationTableElm.getAttribute("hasDispDescription");
			String presentationTableHasDispName = presentationTableElm.getAttribute("hasDispName");
			String presentationTableMdsid = presentationTableElm.getAttribute("mdsid");
			String presentationTableName = presentationTableElm.getAttribute("name");
			Element presentationTblElm = XMLUtil.getChildElement(presentationTableElm, "Description");

			String desription = presentationTblElm.getTextContent();

			Element RefHierarchiesElm = XMLUtil.getChildElement(presentationTableElm, "RefHierarchies");

			List<Element> refPrsnttionHierarchyNsList = XMLUtil.getChildElements(RefHierarchiesElm,
					"RefPresentationHierarchy");

			String prsntatnHierarchyrefId = null;
			String presentationHierarchyRef = null;
			String presentationHierarchyRefName = null;

			for (Element refPrsnttionHierarchyElm : refPrsnttionHierarchyNsList) {
				prsntatnHierarchyrefId = refPrsnttionHierarchyElm.getAttribute("refId");
				presentationHierarchyRef = refPrsnttionHierarchyElm.getAttribute("presentationHierarchyRef");

				String[] presentationHierarchyRefMdsid = presentationHierarchyRef.split(Pattern.quote("#"));

				if (mdsidNameMap.containsKey(presentationHierarchyRefMdsid[1])) {
					presentationHierarchyRefName = mdsidNameMap.get(presentationHierarchyRefMdsid[1]);
				}
			}
			if (isloadMdsid) {
				presentationTableBuilder.append(presentationTableName).append("|").append(presentationTableMdsid)
						.append("|").append(presentationTableHasDispName).append("|")
						.append(presentationTableHasDispDescription).append("|").append(presentationTableContainerRef)
						.append("|").append(prsntionTableRefName).append("|").append(prsntatnHierarchyrefId).append("|")
						.append(presentationHierarchyRef).append("|").append(presentationHierarchyRefName).append("|")
						.append(desription).append(";\n");
			} else {
				mdsidNameMap.put(presentationTableMdsid, presentationTableName);
			}

			List<Element> presentationColumnNdList = XMLUtil.getChildElements(presentationTableElm,
					"PresentationColumn");
			for (Element presentationColumnElm : presentationColumnNdList) {

				String presentationColumnHasDispDescription = presentationColumnElm.getAttribute("hasDispDescription");
				String presentationColumnHasDispName = presentationColumnElm.getAttribute("hasDispName");
				String logicalColumnRef = presentationColumnElm.getAttribute("logicalColumnRef");
				String[] lgclClmnRefMdsid = logicalColumnRef.split(Pattern.quote("#"));
				String lgclClmnRefName = null;

				if (mdsidNameMap.containsKey(lgclClmnRefMdsid[1])) {
					lgclClmnRefName = mdsidNameMap.get(lgclClmnRefMdsid[1]);
				}
				String presentationColumnMdsid = presentationColumnElm.getAttribute("mdsid");
				String presentationColumnName = presentationColumnElm.getAttribute("name");
				String overrideLogicalName = presentationColumnElm.getAttribute("overrideLogicalName");

				Element dscriptionElm = XMLUtil.getChildElement(presentationColumnElm, "Description");
				String dscription = dscriptionElm.getTextContent();

				if (isloadMdsid) {
					presentationColumnBuilder.append(presentationTableName).append("|").append(presentationColumnName)
							.append("|").append(presentationColumnMdsid).append("|").append(overrideLogicalName)
							.append("|").append(presentationColumnHasDispName).append("|")
							.append(presentationColumnHasDispDescription).append("|").append(logicalColumnRef)
							.append("|").append(lgclClmnRefName).append("|").append(dscription).append(";\n");
				} else {
					mdsidNameMap.put(presentationColumnMdsid, presentationColumnName);
				}

			}
			map.put("PresentationColumn", presentationColumnBuilder.toString());
		}
		map.put("PresentationTable", presentationTableBuilder.toString());
		List<Element> queryPrivilegeNsList = XMLUtil.getChildElements(dECLAREElm, "QueryPrivilege");
		for (Element queryPrivilegeElm : queryPrivilegeNsList) {
			String queryPrivilegeMdsid = queryPrivilegeElm.getAttribute("mdsid");
			String queryPrivilegeName = queryPrivilegeElm.getAttribute("name");
			String maxExecTime = queryPrivilegeElm.getAttribute("maxExecTime");
			String maxRows = queryPrivilegeElm.getAttribute("maxRows");
			String execPhysicalPrivilege = queryPrivilegeElm.getAttribute("execPhysicalPrivilege");
			String populatePrivilege = queryPrivilegeElm.getAttribute("populatePrivilege");

			if (isloadMdsid) {
				queryPrivilegeBuilder.append(queryPrivilegeName).append("|").append(queryPrivilegeMdsid).append("|")
						.append(maxExecTime).append("|").append(maxRows).append("|").append(execPhysicalPrivilege)
						.append("|").append(populatePrivilege).append(";\n");
			} else {
				mdsidNameMap.put(queryPrivilegeMdsid, queryPrivilegeName);
			}
		}
		map.put("QueryPrivilege", queryPrivilegeBuilder.toString());

		List<Element> privilegePackageNsList = XMLUtil.getChildElements(dECLAREElm, "PrivilegePackage");
		for (Element privilegePackageElm : privilegePackageNsList) {
			String privilegePkgMdsid = privilegePackageElm.getAttribute("mdsid");
			String privilegePkgName = privilegePackageElm.getAttribute("name");
			String roleRef = privilegePackageElm.getAttribute("roleRef");
			String logicalQueryMaxExecTime = privilegePackageElm.getAttribute("LogicalQueryMaxExecTime");

			Element queryPrivilegeMpngElm = XMLUtil.getChildElement(privilegePackageElm, "QueryPrivilegeMapping");
			String databaseRef1 = queryPrivilegeMpngElm.getAttribute("databaseRef");

			String[] databaseRefMdsid = databaseRef1.split(Pattern.quote("#"));
			String dbRefName = null;

			if (mdsidNameMap.containsKey(databaseRefMdsid[1])) {

				dbRefName = mdsidNameMap.get(databaseRefMdsid[1]);
			}

			if (isloadMdsid) {
				privilegePackageBuilder.append(privilegePkgName).append("|").append(privilegePkgMdsid).append("|")
						.append(roleRef).append("|").append(logicalQueryMaxExecTime).append("|")
						.append(queryPrivilegeMpngElm).append("|").append(databaseRef1).append("|").append(dbRefName)
						.append(";\n");
			} else {
				mdsidNameMap.put(privilegePkgMdsid, privilegePkgName);
			}
		}
		map.put("PrivilegePackage", privilegePackageBuilder.toString());

		List<Element> objectPrivilegeNsList = XMLUtil.getChildElements(dECLAREElm, "ObjectPrivilege");
		for (Element objectPrivilegeElm : objectPrivilegeNsList) {
			String objPrvlgElmMdsid = objectPrivilegeElm.getAttribute("mdsid");
			String objectPrivilegeName = objectPrivilegeElm.getAttribute("name");
			String type = objectPrivilegeElm.getAttribute("type");
			String privilegePackageRef = objectPrivilegeElm.getAttribute("privilegePackageRef");
			String[] privilegePackageRefMdsid = privilegePackageRef.split(Pattern.quote("#"));
			String privilegePkgRefName = null;

			if (mdsidNameMap.containsKey(privilegePackageRefMdsid[1])) {

				privilegePkgRefName = mdsidNameMap.get(privilegePackageRefMdsid[1]);
			}
			Element RefObjectsElm = XMLUtil.getChildElement(objectPrivilegeElm, "RefObjects");

			List<Element> refObjectNsList = XMLUtil.getChildElements(RefObjectsElm, "RefObject");
			for (Element refObjectElm : refObjectNsList) {
				String refId = refObjectElm.getAttribute("refId");
				String objectTypeId = refObjectElm.getAttribute("objectTypeId");
				String objectRef = refObjectElm.getAttribute("objectRef");
				String[] objectRefMdsid = objectRef.split(Pattern.quote("#"));
				String objectRefName = null;

				if (mdsidNameMap.containsKey(objectRefMdsid[1])) {

					objectRefName = mdsidNameMap.get(objectRefMdsid[1]);
				}

				if (isloadMdsid) {
					objectPrivilegeBuilder.append(objectPrivilegeName).append("|").append(objPrvlgElmMdsid).append("|")
							.append(type).append("|").append(privilegePackageRef).append("|")
							.append(privilegePkgRefName).append("|").append(refId).append("|").append(objectTypeId)
							.append("|").append(objectRef).append("|").append(objectRefName).append(";\n");
				} else {
					mdsidNameMap.put(objPrvlgElmMdsid, objectPrivilegeName);
				}
			}
		}
		map.put("ObjectPrivilege", objectPrivilegeBuilder.toString());

		List<Element> presentationHierarchyNdList = XMLUtil.getChildElements(dECLAREElm, "PresentationHierarchy");
		for (Element presentationHierarchyElm : presentationHierarchyNdList) {

			String presentationHierarchyMdsid = presentationHierarchyElm.getAttribute("mdsid");
			String presentationHierarchyName = presentationHierarchyElm.getAttribute("name");
			String hasDispName = presentationHierarchyElm.getAttribute("hasDispName");
			String hasDispDescription = presentationHierarchyElm.getAttribute("hasDispDescription");
			String aliasNamePh = null;
			List<String> aliasList = new ArrayList<>();
			StringBuilder alisBuilder = new StringBuilder();
			List<Element> aliasNdListPh = XMLUtil.getChildElements(presentationHierarchyElm, "Alias");
			for (Element aliasElm : aliasNdListPh) {

				aliasNamePh = aliasElm.getAttribute("name");
				alisBuilder.append(aliasNamePh);
			}
			aliasList.add(alisBuilder.toString());
			String containerRef1 = presentationHierarchyElm.getAttribute("containerRef");
			String[] contnrRefMdsid = containerRef1.split(Pattern.quote("#"));
			String contnrRefName = null;
			if (mdsidNameMap.containsKey(contnrRefMdsid[1])) {
				contnrRefName = mdsidNameMap.get(contnrRefMdsid[1]);
			}
			String logicalDimensionRef = presentationHierarchyElm.getAttribute("logicalDimensionRef");
			String[] lgclDimensionRefMdsid = logicalDimensionRef.split(Pattern.quote("#"));
			String lgclDimensionRefName = null;

			if (mdsidNameMap.containsKey(lgclDimensionRefMdsid[1])) {
				lgclDimensionRefName = mdsidNameMap.get(lgclDimensionRefMdsid[1]);
			}

			if (isloadMdsid) {
				presentationHierarchyBuilder.append(presentationHierarchyName).append("|")
						.append(presentationHierarchyMdsid).append("|").append(hasDispName).append("|")
						.append(hasDispDescription).append("|").append(containerRef1).append("|").append(contnrRefName)
						.append("|").append(logicalDimensionRef).append("|").append(lgclDimensionRefName).append("|")
						.append(aliasList).append(";\n");
			} else {
				mdsidNameMap.put(presentationHierarchyMdsid, presentationHierarchyName);
			}

			List<Element> presentationLevelNdList = XMLUtil.getChildElements(presentationHierarchyElm,
					"PresentationLevel");
			for (Element presentationLevelElm : presentationLevelNdList) {
				String presentationLevelMdsid = presentationLevelElm.getAttribute("mdsid");
				String presentationLevelName = presentationLevelElm.getAttribute("name");
				String plhasDispName = presentationLevelElm.getAttribute("hasDispName");
				String plhasDispDescription = presentationLevelElm.getAttribute("hasDispDescription");
				String logicalLevelRef = presentationLevelElm.getAttribute("logicalLevelRef");
				String[] lgclLvlRefMdsid = logicalLevelRef.split(Pattern.quote("#"));
				String lgclLvlRefName = null;
				if (mdsidNameMap.containsKey(lgclLvlRefMdsid[1])) {
					lgclLvlRefName = mdsidNameMap.get(lgclLvlRefMdsid[1]);
				}
				String aliasName = null;
				List<String> aliasLst = new ArrayList<>();
				StringBuilder aliasBulder = new StringBuilder();
				List<Element> aliasNdList = XMLUtil.getChildElements(presentationLevelElm, "Alias");
				for (Element aliasElm : aliasNdList) {

					aliasName = aliasElm.getAttribute("name");
					aliasBulder.append(aliasName);
				}
				aliasLst.add(aliasBulder.toString());
				Element refDisplayColumnsElm = XMLUtil.getChildElement(presentationLevelElm, "RefDisplayColumns");

				// String contrefDisplayColumnsElm = refDisplayColumnsElm.getTextContent();
				Element refObjectElm = XMLUtil.getChildElement(refDisplayColumnsElm, "RefPresentationColumn");

				String refId = refObjectElm.getAttribute("refId");
				if (refId.isEmpty()) {
					refId = null;
				}
				String presentationColumnRef = refObjectElm.getAttribute("presentationColumnRef");
				if (presentationColumnRef.isEmpty()) {
					presentationColumnRef = null;
				}
				String prsnttnClmnRefName = null;
				if (presentationColumnRef != null) {
					String[] prsnttnClmnRefMdsid = presentationColumnRef.split(Pattern.quote("#"));

					if (mdsidNameMap.containsKey(prsnttnClmnRefMdsid[1])) {
						prsnttnClmnRefName = mdsidNameMap.get(prsnttnClmnRefMdsid[1]);
					}
				}
				if (isloadMdsid) {
					presentationLevelBuilder.append(presentationHierarchyName).append("|").append(presentationLevelName)
							.append("|").append(presentationLevelMdsid).append("|").append(plhasDispName).append("|")
							.append(plhasDispDescription).append("|").append(logicalLevelRef).append("|")
							.append(lgclLvlRefName).append("|").append(aliasName).append("|").append(refId).append("|")
							.append(presentationColumnRef).append("|").append(prsnttnClmnRefName).append("|")
							.append(aliasLst).append(";\n");

				} else {
					mdsidNameMap.put(presentationLevelMdsid, presentationLevelName);
				}

			}
			map.put("PresentationLevel", presentationLevelBuilder.toString());
		}
		map.put("PresentationHierarchy", presentationHierarchyBuilder.toString());

		List<Element> groupNsList = XMLUtil.getChildElements(dECLAREElm, "Group");
		{
			for (Element groupElm : groupNsList) {
				String groupMdsid = groupElm.getAttribute("mdsid");
				String groupName = groupElm.getAttribute("name");
				String displayName = groupElm.getAttribute("displayName");
				String logStatisticsStatus = groupElm.getAttribute("logStatisticsStatus");

				Element refChildGroupsElm = XMLUtil.getChildElement(groupElm, "RefChildGroups");

				Element reChdGroupElm = XMLUtil.getChildElement(refChildGroupsElm, "RefGroup");

				String refId = reChdGroupElm.getAttribute("refId");
				if (refId.isEmpty()) {
					refId = null;
				}
				String groupRef = reChdGroupElm.getAttribute("groupRef");
				if (groupRef.isEmpty()) {
					groupRef = null;
				}
				if (isloadMdsid) {
					groupBuilder.append(groupName).append("|").append(groupMdsid).append("|").append(displayName)
							.append("|").append(logStatisticsStatus).append("|").append(refId).append("|")
							.append(groupRef).append(";\n");
				} else {
					mdsidNameMap.put(groupMdsid, groupName);
				}
			}
			map.put("GroupTable", groupBuilder.toString());

		}

		Element userElm = XMLUtil.getChildElement(dECLAREElm, "User");

		String userMdsid = userElm.getAttribute("mdsid");
		String userName = userElm.getAttribute("name");
		String logStatisticsStatus = userElm.getAttribute("logStatisticsStatus");

		Element descriptionEm = XMLUtil.getChildElement(userElm, "Description");
		String descripton = descriptionEm.getTextContent();

		if (isloadMdsid) {
			userBuilder.append(userName).append("|").append(userMdsid).append("|").append(logStatisticsStatus)
					.append("|").append(descripton).append(";\n");
		} else {
			mdsidNameMap.put(userMdsid, userName);
		}

		map.put("User", userBuilder.toString());

		Element variableElm = XMLUtil.getChildElement(dECLAREElm, "Variable");

		String variableMdsid = variableElm.getAttribute("mdsid");
		String variableName = variableElm.getAttribute("name");

		Element exprElm = XMLUtil.getChildElement(variableElm, "Expr");

		String exprMdsid = exprElm.getAttribute("mdsid");
		String exprName = exprElm.getAttribute("name");

		Element exprTextElm = XMLUtil.getChildElement(exprElm, "ExprText");

		String contofexprTextElm = exprTextElm.getTextContent();

		Element exprTextDescElm = XMLUtil.getChildElement(exprElm, "ExprTextDesc");

		String contofexprTextDescElm = exprTextDescElm.getTextContent();
		if (isloadMdsid) {
			variableBuilder.append(variableName).append("|").append(variableMdsid).append("|").append(exprName)
					.append("|").append(exprMdsid).append("|").append(contofexprTextElm).append("|")
					.append(contofexprTextDescElm).append(";\n");
		} else {
			mdsidNameMap.put(variableMdsid, variableName);
		}

		map.put("Variable", variableBuilder.toString());

		isloadMdsid = true;
		count++;
		if (isloadMdsid == true && count == 1) {
			map = SampleParsingService.parseXmlDoc(doc, isloadMdsid, count);
		}

		return map;

	}

	/**
	 * extractMapData.
	 * 
	 * @param mapDoc {@link String}
	 * @throws Exception
	 */
	public static void extractMapData(Map<String, String> mapDoc, String outputFileLocation) throws Exception {

		String[] splitDatabase = mapDoc.get("databasetable").split(Pattern.quote(";"));
		writeCsv("databasetable", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Feature").split(Pattern.quote(";"));
		writeCsv("Feature", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("RefConnectionPool").split(Pattern.quote(";"));
		writeCsv("RefConnectionPool", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("PhysicalTable").split(Pattern.quote(";"));
		writeCsv("PhysicalTable", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("PhysicalColumn").split(Pattern.quote(";"));
		writeCsv("PhysicalColumn", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("PhysicalKey").split(Pattern.quote(";"));
		writeCsv("PhysicalKey", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("ConnectionPool").split(Pattern.quote(";"));
		writeCsv("ConnectionPool", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("BusinessModel").split(Pattern.quote(";"));
		writeCsv("BusinessModel", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("LogicalTable").split(Pattern.quote(";"));
		writeCsv("LogicalTable", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("LogicalColumn").split(Pattern.quote(";"));
		writeCsv("LogicalColumn", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("AttributeDefn").split(Pattern.quote(";"));
		writeCsv("AttributeDefn", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("LogicalKey").split(Pattern.quote(";"));
		writeCsv("LogicalKey", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("LogicalTableSource").split(Pattern.quote(";"));
		writeCsv("LogicalTableSource", splitDatabase, outputFileLocation);
		splitDatabase = mapDoc.get("ColumnMapping").split(Pattern.quote(";"));
		writeCsv("ColumnMapping", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("LogicalTblSrcChild").split(Pattern.quote(";"));
		writeCsv("LogicalTblSrcChild", splitDatabase, outputFileLocation);
		splitDatabase = mapDoc.get("LogicalComplexJoin").split(Pattern.quote(";"));
		writeCsv("LogicalComplexJoin", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("PresentationTable").split(Pattern.quote(";"));
		writeCsv("PresentationTable", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("PresentationColumn").split(Pattern.quote(";"));
		writeCsv("PresentationColumn", splitDatabase, outputFileLocation);
		splitDatabase = mapDoc.get("User").split(Pattern.quote(";"));
		writeCsv("User", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("QueryPrivilege").split(Pattern.quote(";"));
		writeCsv("QueryPrivilege", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("GroupTable").split(Pattern.quote(";"));
		writeCsv("GroupTable", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("presentationcatalog").split(Pattern.quote(";"));
		writeCsv("presentationcatalog", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("PresentationHierarchy").split(Pattern.quote(";"));
		writeCsv("PresentationHierarchy", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("PresentationLevel").split(Pattern.quote(";"));
		writeCsv("PresentationLevel", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("MeasureDefn").split(Pattern.quote(";"));
		writeCsv("MeasureDefn", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Variable").split(Pattern.quote(";"));
		writeCsv("Variable", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("PrivilegePackage").split(Pattern.quote(";"));
		writeCsv("PrivilegePackage", splitDatabase, outputFileLocation);
		splitDatabase = mapDoc.get("ObjectPrivilege").split(Pattern.quote(";"));
		writeCsv("ObjectPrivilege", splitDatabase, outputFileLocation);

		splitDatabase = mapDoc.get("Dimension").split(Pattern.quote(";"));
		writeCsv("Dimension", splitDatabase, outputFileLocation);
		splitDatabase = mapDoc.get("LogicalLevel").split(Pattern.quote(";"));
		writeCsv("LogicalLevel", splitDatabase, outputFileLocation);
		splitDatabase = mapDoc.get("PhysicalForeignKey").split(Pattern.quote(";"));
		writeCsv("PhysicalForeignKey", splitDatabase, outputFileLocation);
		splitDatabase = mapDoc.get("refTableSource").split(Pattern.quote(";"));
		writeCsv("refTableSource", splitDatabase, outputFileLocation);
		splitDatabase = mapDoc.get("LgclTblSrcGroupBy").split(Pattern.quote(";"));
		writeCsv("LgclTblSrcGroupBy", splitDatabase, outputFileLocation);
		splitDatabase = mapDoc.get("DmnsnLogicalKey").split(Pattern.quote(";"));
		writeCsv("DmnsnLogicalKey", splitDatabase, outputFileLocation);
		
	}
	public static void margingMapDataOnCSV(Map<String, String> mapDoc,String outputFileLocation) throws Exception {
		String[] margingData = mapDoc.get("DbDetails").split(Pattern.quote(";\n"));
		writeCsv("DbDetails", margingData,outputFileLocation);

	}
	/**
	 * extractMapDataForDb.
	 * 
	 * @param conn   {@link Connection}
	 * @param mapDoc {@link String}
	 * @throws Exception
	 */
	public static void extractMapDataForDb(Connection conn, Map<String, String> mapDoc) throws Exception {
		String[] splitDatabase = mapDoc.get("databasetable").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "databasetable", splitDatabase);

		splitDatabase = mapDoc.get("Feature").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Feature", splitDatabase);
		splitDatabase = mapDoc.get("PhysicalTable").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "PhysicalTable", splitDatabase);
		splitDatabase = mapDoc.get("PhysicalColumn").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "PhysicalColumn", splitDatabase);
		splitDatabase = mapDoc.get("PhysicalKey").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "PhysicalKey", splitDatabase);
		splitDatabase = mapDoc.get("ConnectionPool").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "ConnectionPool", splitDatabase);
		splitDatabase = mapDoc.get("RefConnectionPool").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "RefConnectionPool", splitDatabase);
		splitDatabase = mapDoc.get("BusinessModel").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "BusinessModel", splitDatabase);
		splitDatabase = mapDoc.get("LogicalTable").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "LogicalTable", splitDatabase);
		splitDatabase = mapDoc.get("LogicalColumn").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "LogicalColumn", splitDatabase);
		splitDatabase = mapDoc.get("AttributeDefn").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "AttributeDefn", splitDatabase);
		splitDatabase = mapDoc.get("LogicalKey").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "LogicalKey", splitDatabase);
		splitDatabase = mapDoc.get("LogicalTableSource").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "LogicalTableSource", splitDatabase);
		splitDatabase = mapDoc.get("ColumnMapping").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "ColumnMapping", splitDatabase);
		splitDatabase = mapDoc.get("LogicalTblSrcChild").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "LogicalTblSrcChild", splitDatabase);
		splitDatabase = mapDoc.get("LogicalComplexJoin").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "LogicalComplexJoin", splitDatabase);

		splitDatabase = mapDoc.get("PresentationTable").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "PresentationTable", splitDatabase);
		splitDatabase = mapDoc.get("PresentationColumn").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "PresentationColumn", splitDatabase);
		splitDatabase = mapDoc.get("User").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "User", splitDatabase);
		splitDatabase = mapDoc.get("QueryPrivilege").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "QueryPrivilege", splitDatabase);
		splitDatabase = mapDoc.get("GroupTable").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "GroupTable", splitDatabase);

		splitDatabase = mapDoc.get("presentationcatalog").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "presentationcatalog", splitDatabase);
		splitDatabase = mapDoc.get("PresentationHierarchy").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "PresentationHierarchy", splitDatabase);
		splitDatabase = mapDoc.get("PresentationLevel").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "PresentationLevel", splitDatabase);
		splitDatabase = mapDoc.get("MeasureDefn").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "MeasureDefn", splitDatabase);
		splitDatabase = mapDoc.get("Variable").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Variable", splitDatabase);
		splitDatabase = mapDoc.get("PrivilegePackage").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "PrivilegePackage", splitDatabase);
		splitDatabase = mapDoc.get("ObjectPrivilege").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "ObjectPrivilege", splitDatabase);
		splitDatabase = mapDoc.get("Dimension").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "Dimension", splitDatabase);
		splitDatabase = mapDoc.get("LogicalLevel").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "LogicalLevel", splitDatabase);
		splitDatabase = mapDoc.get("PhysicalForeignKey").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "PhysicalForeignKey", splitDatabase);
		splitDatabase = mapDoc.get("refTableSource").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "refTableSource", splitDatabase);
		splitDatabase = mapDoc.get("LgclTblSrcGroupBy").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "LgclTblSrcGroupBy", splitDatabase);
		splitDatabase = mapDoc.get("DmnsnLogicalKey").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "DmnsnLogicalKey", splitDatabase);
		
	}
	public static void margingDataOnDB(Connection conn, Map<String, String> mapDoc) throws Exception {
		String[] margeDatabase = mapDoc.get("DbDetails").split(Pattern.quote(";\n"));
		insertDataInDB(conn, "DbDetails", margeDatabase);
	}

	/**
	 * writeCsv.
	 * 
	 * @param csvfile {@link String}
	 * @param data    {@link String}
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
			//ZipUtil.pack(new File(outputFileLocation + "\\"), new File(outputFileLocation + ".zip"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * insertDataInDB.
	 * 
	 * @param conn        {@link Connection}
	 * @param dbTableName {@link String}
	 * @param data        {@link String}
	 * @throws ExcinsertDataInDBeption
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

	private static Map<String, String> obieeMarging(Document doc) throws Exception {
		Map<String, String> map = new HashMap<>();

		StringBuilder dbBuilder = new StringBuilder();
		dbBuilder.append("mdsid").append("|").append("name").append("|").append("type").append("|").append("dbName")
				.append("|").append("dbTypeId").append("|").append("description").append("|").append("featureName")
				.append("|").append("featureValue").append("|").append("refId").append("|").append("connectionPoolRef")
				.append(";\n");

		
		
		Element dECLAREElm = doc.getDocumentElement();

		List<Element> dbNdList = XMLUtil.getChildElements(dECLAREElm, "Database");
		for (Element databaseElm : dbNdList) {
			String dbName = databaseElm.getAttribute("dbName");
			String dbTypeId = databaseElm.getAttribute("dbTypeId");
			String mdsid = databaseElm.getAttribute("mdsid");
			String name = databaseElm.getAttribute("name");
			String type = databaseElm.getAttribute("type");

			Element descriptionElm = XMLUtil.getChildElement(databaseElm, "Description");

			String description = descriptionElm.getTextContent();
			if (description.isEmpty()) {
				description = null;
			}

			List<Element> featureNdList = XMLUtil.getChildElements(databaseElm, "Feature");
			for (Element featureElm : featureNdList) {

				// String isMulti= featureElm.getAttribute("IsMulti");
				String featureName = featureElm.getAttribute("name");
				String featureValue = featureElm.getAttribute("value");

				Element refConnectionPoolsElm = XMLUtil.getChildElement(databaseElm, "RefConnectionPools");
				List<Element> refConnectionPoolNdList = XMLUtil.getChildElements(refConnectionPoolsElm,
						"RefConnectionPool");
				for (Element refConnectionPoolElm : refConnectionPoolNdList) {

					String connectionPoolRef = refConnectionPoolElm.getAttribute("connectionPoolRef");
					String refId = refConnectionPoolElm.getAttribute("refId");

					dbBuilder.append(mdsid).append("|").append(name).append("|").append(type).append("|").append(dbName)
							.append("|").append(dbTypeId).append("|").append(description).append("|")
							.append(featureName).append("|").append(featureValue).append("|").append(refId).append("|")
							.append(connectionPoolRef).append(";\n");

				}
			}
		}
		map.put("DbDetails", dbBuilder.toString());

		

		

		return map;

	}
}





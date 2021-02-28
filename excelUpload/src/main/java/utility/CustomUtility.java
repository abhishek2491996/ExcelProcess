package utility;


import java.io.File;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import databaseConnection.MySqlConnection;

@SuppressWarnings("unused")
public class CustomUtility {

	private static Connection conn = null;

	@SuppressWarnings("rawtypes")
	private static Collection<Map> resultMap;
	private static Statement stmt = null;
	private static ResultSet rst = null;
	private static ResultSetMetaData md = null;
	private static int colcount = 0;
	private static PreparedStatement pstmt = null;
	private static LinkedList<Map<String, String>> resultList = null;
	private static ArrayList<String> iColumns = null;
	private static HashMap<String, Object> sqlHashMap = null;
	private static List<HashMap<String, Object>> listSqlMap = null;
	private static boolean insertion = false;
	private static String exception = null;
	private static CustomUtility utility = null;

	static Logger log = Logger.getLogger(CustomUtility.class);
	
	private static final List<String> colListStatusflow = Arrays.asList(
			new String[] { "OBJECTTYPE", "OBJECTID", "HIERARCHYID", "KEYVALUE", "STATUS", "DELFLAG", "CLIENTID" });

	public CustomUtility() throws ClassNotFoundException, SQLException {
		log.info("Calling constructor");
		MySqlConnection mysql = new MySqlConnection();
		conn = mysql.getConnection();
		log.info("connection done " + conn);
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			log.error("Exception occured in connection " + e);
		}
	}

	// Update Query without parameterized
	public int executeUpdate(String Query) {
		int rowAffected = 0;
		try {
			stmt = conn.createStatement();
			log.info("Query for execution " + Query);
			rowAffected = rowAffected + stmt.executeUpdate(Query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rowAffected;
	}

	// Update Query without parameterized
	public long executeLargeUpdate(String Query) {
		long rowAffected = 0;
		try {
			stmt = conn.createStatement();
			log.info("Query for execution " + Query);
			rowAffected = rowAffected + stmt.executeLargeUpdate(Query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rowAffected;
	}

	// List of Update Query without parameterized
	public static int executeUpdate(List<String> queryList) throws ClassNotFoundException, SQLException {
		MySqlConnection mysql = new MySqlConnection();
		conn = mysql.getConnection();
		int rowAffected = 0;
		try {
			stmt = conn.createStatement();
			for (String query : queryList) {
				log.info("Query  " + query);
				rowAffected = rowAffected + stmt.executeUpdate(query);
			}
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		return rowAffected;
	}

	// Execute Query in Batch with Statement
	public static int executeUpdateBatch(List<String> queryList) throws SQLException, ClassNotFoundException {
		MySqlConnection mysql = new MySqlConnection();
		conn = mysql.getConnection();
		int rowAffected = 0;
		stmt = conn.createStatement();
		for (String query : queryList) {
			// log.info("query list "+query);
			stmt.addBatch(query);
		}
		long[] returnArray = stmt.executeLargeBatch();

		conn.commit();
		log.info("Insert long array " + returnArray.length);

		return rowAffected;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int performBatchInsert(List<HashMap<String, Object>> insertQueryMap) throws SQLException, ClassNotFoundException {
		int numberInsert = 0;
		List<Object[]> listValues = new ArrayList();
		String insertQuery = "";
		for (Map mapRecord : insertQueryMap) {
			insertQuery = (String) mapRecord.get("Query");

			Object[] argsVal = (Object[]) mapRecord.get("valuesArr");
			listValues.add(argsVal);
		}
		numberInsert = CustomUtility.executeUpdateBatch(insertQuery, listValues);
		return numberInsert;

	}

	public static int executeUpdateBatch(String Query, List<Object[]> queryList)
			throws BatchUpdateException, SQLException, ClassNotFoundException {
		MySqlConnection mysql = new MySqlConnection();
		conn = mysql.getConnection();
		log.info("connection done " + conn);

		int rowAffected = 0;
		 System.out.println("Query " + Query);

		pstmt = conn.prepareStatement(Query);
		for (Object[] dataVal : queryList) {
			
			System.out.println("Object Array " + Arrays.toString(dataVal));
			for (int i = 0; i < dataVal.length; i++) {
				pstmt.setObject(i + 1, dataVal[i]);
			}
			pstmt.addBatch();
		}
		int[] returnArray = pstmt.executeBatch();

		conn.commit();
		conn.close();
		log.info("Insert long array " + returnArray.length);
		rowAffected = returnArray.length;
		return rowAffected;
	}
	
	public static int executeUpdateBatch(String Query, List<Object[]> queryList,Connection connection )
			throws BatchUpdateException, SQLException, ClassNotFoundException {
		MySqlConnection mysql = new MySqlConnection();
		connection = mysql.getConnection();
		log.info("connection done " + connection);

		int rowAffected = 0;
		 log.info("Query " + Query);
		 System.out.println("Query " + Query);
		pstmt = connection.prepareStatement(Query);
		for (Object[] dataVal : queryList) {
			
			 System.out.println("Object Array " + Arrays.toString(dataVal));
			for (int i = 0; i < dataVal.length; i++) {
				pstmt.setObject(i + 1, dataVal[i]);
			}
			pstmt.addBatch();
		}
		int[] returnArray = pstmt.executeBatch();

		connection.commit();
		//conn.close();
		log.info("Insert long array " + returnArray.length);
		
		rowAffected = returnArray.length;
		return rowAffected;
	}

	// Update Query with parameterized
	public int executeUpdate(String Query, Object[] arr) {
		try {
			log.info("connection of db" + conn);
			System.out.println("Query "+Query);
			System.out.println("Values "+Arrays.toString(arr));
			pstmt = conn.prepareStatement(Query);
			for (int i = 0; i < arr.length; i++) {
				pstmt.setObject(i + 1, arr[i]);
			}
			pstmt.executeUpdate();
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
				return 0;

			}
			return 0;
		}
		return 1;

	}

	// List of Update Query with parameterized
	public static int executeUpdate(String Query, List<Object[]> arr) {
		log.info("Query   ::: " + Query);
		try{
			pstmt = conn.prepareStatement(Query);
			for (int i = 0; i < arr.size(); i++) {
				Object dataArray[] = arr.get(i);
				log.info("Object Array " + Arrays.toString(dataArray));
				for (int j = 0; j < dataArray.length; j++) {
					pstmt.setObject(i + 1, dataArray[i]);
				}
				int rowff =pstmt.executeUpdate();
				log.info("Insertion Done "+rowff);
			}
			conn.commit();
			log.info("Successfully Inserted");
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				return 0;
			}
			e.printStackTrace();
			return 0;
		}

		return 1;

	}

	// Select Query Method with Collection
	@SuppressWarnings("rawtypes")
	public Collection<Map> executeQuery(String query, ArrayList<String> iColumns) {
		query = "SET TRANSACTION ISOLATION LEVEL READ COMMITTED; " + query;
		log.info("Executing Query  " + query);
		resultMap = new ArrayList<Map>();
		long start = System.currentTimeMillis();
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(query);
			md = rst.getMetaData();
			colcount = md.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			while (rst.next()) {
				/*
				 * Map<String,Object> map=new TreeMap<String,Object>(); for(int
				 * i=1;i<=colcount;i++) { map.put(md.getColumnName(i),
				 * rst.getObject(i)); }
				 */
				Map<String, Object> map = new TreeMap<String, Object>();
				for (int i = 1; i <= colcount; i++) {
					map.put(md.getColumnName(i),rst.getObject(i));
				}

				resultMap.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();

		log.info("Took   " + ((end - start) / 1000) + "  s");

		return resultMap;
	}

	
	//Select Query with parameterized 
	@SuppressWarnings("rawtypes")
	public static  Collection<Map> executeParamQuery(String query,Object[] param){
		long startTime = System.currentTimeMillis();
		resultMap=new ArrayList<Map>();
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(query);
			for(int i=0;i<param.length;i++){
				pstmt.setObject(i+1, param[i]);
			}
			rst= pstmt.executeQuery();
			md = rst.getMetaData();
			colcount = md.getColumnCount();
			while (rst.next()) {
				Map<String, Object> map = new TreeMap<String, Object>();
				for (int i = 1; i <= colcount; i++) {
					map.put(md.getColumnName(i), rst.getString(i));
				}

				resultMap.add(map);
			}
			log.info("Result Map "+resultMap);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultMap;
	}
	
	// Select Query Method with Collection skip count & fetch count
	@SuppressWarnings("rawtypes")
	public Collection<Map> executeQuery(String query, Collection<String> iColumns, int skipCount,
			int fetchCount) {
		log.info("Executing Query  " + query);

		resultMap = new ArrayList<Map>();
		long start = System.currentTimeMillis();
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(query);
	
			md = rst.getMetaData();
			colcount = md.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			while (rst.next()) {
				/*
				 * Map<String,Object> map=new TreeMap<String,Object>(); for(int
				 * i=1;i<=colcount;i++) { map.put(md.getColumnName(i),
				 * rst.getObject(i)); }
				 */
				Map<String, Object> map = new TreeMap<String, Object>();
				for (int i = 1; i <= colcount; i++) {
					map.put(md.getColumnName(i), rst.getObject(i));
				}

				resultMap.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();

		log.info("Took   " + ((end - start) / 1000) + "  s");

		return resultMap;
	}

	// Select Query Method with List
	public static List<Map<String, String>> executeQuery(String Query) {
		log.info("Exceute Query " + Query);
		resultList = new LinkedList<Map<String, String>>();
		long start = System.currentTimeMillis();
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(Query);
			md = rst.getMetaData();
			colcount = md.getColumnCount();
			while (rst.next()) {
				Map<String, String> map = new TreeMap<String, String>();
				for (int i = 1; i <= colcount; i++) {
					map.put(md.getColumnName(i), rst.getString(i));
				}
				resultList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();

		log.info("Took   " + ((end - start) / 1000) + "  s");
		return resultList;
	}

	// create and return object
	public static String createObjectAndRtnObjNo(String objType, String userId, String clientId, String lang,
			Map<String, Object> visitMap, boolean workflowRequired, String plantCode) throws SQLException {
		sqlHashMap = new HashMap<String, Object>();
		String nextObjectNumber = getMDONumber(objType, "0", "0", plantCode);
		if (!visitMap.containsKey("objectnumber")) {
			visitMap.put("objectnumber", nextObjectNumber);
		}
		log.info("nextObjectNumber  " + nextObjectNumber);
		String Query = "";
		if (!visitMap.isEmpty()) {
			String structId = getStructId(objType);

			pstmt = conn.prepareStatement("INSERT INTO DYN_" + structId + "_" + objType);

			sqlHashMap = insertHashMaptoQuery(visitMap, structId, objType, "1");

			String sqlQuery = (String) sqlHashMap.get("Query");
			Object[] param = (Object[]) sqlHashMap.get("ObjectArr");
			log.info("Query  " + sqlQuery);
			log.info("ObjectArr  " + Arrays.toString(param));

			try {
				pstmt = conn.prepareStatement(sqlQuery);
				for (int i = 0; i < param.length; i++) {
					pstmt.setObject(i + 1, param[i]);
				}
				pstmt.executeUpdate();
				insertion = true;
			} catch (Exception ex) {
				conn.rollback();
				insertion = false;
				exception = ex.toString();
				log.info("Exception occured" + exception);
			}
		}
		if (insertion) {
			conn.commit();
			updateNextMDO(objType, plantCode, nextObjectNumber);
		}
		return nextObjectNumber;
	}

	private static String insertStatusFlowQuery(String Query) {
		String colListStatus = StringUtils.join(colListStatusflow, ",");

		return null;

	}

	// To fetch all grid Data
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONObject getGridData(String objectType, String objectNumber) {
		JSONObject jsObj = new JSONObject();
		try {
			String query = "SELECT DISTINCT PARENTFIELD FROM METADATA_MDO WHERE OBJECTTYPE='" + objectType
					+ "' AND PARENTFIELD IS NOT NULL ";
			iColumns = new ArrayList<String>();
			Collection<Map> list = utility.executeQuery(query, iColumns);
			JSONObject jsonArr = new JSONObject();
			for (Map map : list) {
				JSONArray gridData = null;
				String getGridId = (String) map.get("PARENTFIELD");
				// log.info(getGridId);
				if (StringUtils.isNotBlank(getGridId) && StringUtils.isNotBlank(objectNumber)) {
					gridData = getGridJSON(getGridId, objectNumber);
					if (gridData.size() < 10) {
						log.info("Gird data for table " + getGridId + "  data " + gridData);
					}
				}
				if (gridData != null) {
					jsonArr.put("META_TT_" + getGridId, gridData);
				}
			}

			if (jsonArr != null) {
				jsObj.put("gridData", jsonArr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsObj;

	}

	// remove leading zeros
	public static String removeLeadingZeros(String removeChar, String value) {
		String strPattern = "^" + removeChar + "+";
		String nwval = value.replaceAll(strPattern, "");
		log.info("New Value " + nwval);
		return nwval;

	}
	// To fetch specific grid data return JSONObject gridData

	@SuppressWarnings({ "unchecked" })

	public static JSONObject getSpecificGridData(String objectType, String gridId, String objectNumber) {
		JSONObject jsObj = new JSONObject();
		try {
			JSONObject jsonArr = new JSONObject();
			JSONArray gridData = null;
			if (StringUtils.isNotBlank(gridId) && StringUtils.isNotBlank(objectNumber)) {
				gridData = getGridJSON(gridId, objectNumber);
				log.info("Grid data for table " + gridId + "  data " + gridData);
			}
			if (StringUtils.isNotBlank(gridId) && !gridId.contains("META_TT_")) {
				gridId = "META_TT_" + gridId;
			}
			jsObj.put(gridId, gridData);

			// jsObj.put("gridData", jsonArr);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jsObj;

	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static Map<String, Object> convertJSONHashMap(JSONObject jsonData) {
		Map<String, Object> convertHash = null;
		ObjectMapper mapper = new ObjectMapper();
		// mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
		String strJSON = jsonData.toString();
		try {
			// convert JSON string to Map
			convertHash = mapper.readValue(strJSON, Map.class);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return convertHash;
	}

	// To fetch specific grid Data return JSONArray
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static JSONArray getGridJSON(String getGridId, String objectNumber) throws SQLException, ParseException {
		iColumns = new ArrayList<String>();
		long start = System.currentTimeMillis();
		ObjectMapper mapperObj = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		log.info("Get grid id " + getGridId);
		String tableName = "META_TT_" + getGridId;

		if (getGridId.startsWith("META_TT_")) {
			tableName = getGridId;
		}
		String getGridData = "SELECT * FROM " + tableName + " WHERE MATERIALNO='" + objectNumber + "'";
		Collection<Map> list = utility.executeQuery(getGridData, iColumns);
		// log.info("list data "+list);
		for (Map<?, ?> map : list) {
			String jsonObj = null;
			try {
				jsonObj = mapperObj.writeValueAsString(map);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (jsonObj != null) {
				JSONParser jsParser = new JSONParser();
				JSONObject jsStr = (JSONObject) jsParser.parse(jsonObj);
				jsonArr.add(jsStr);
			}
		}

		long end = System.currentTimeMillis();
		log.info("Time taken for grid data " + (end - start) + " ms");
		return jsonArr;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JSONObject createRecord(String moduleId, String userId, String roleId, String event, String lang,
			Map<String, Object> dataMap, JSONObject jsonObject, String plantCode, boolean workflowRequired,
			boolean isDraft, String objectNumber, boolean validation, JSONObject jsonObjectTemp, boolean integrate,
			String blank, boolean approved) throws SQLException {
		sqlHashMap = new HashMap<String, Object>();
		listSqlMap = new ArrayList<>();

		HashMap<String, Object> insQueryMap = new HashMap<String, Object>();
		String moduleID = moduleId;
		String userID = userId;
		String roleID = roleId;
		String eventId = event;
		String lng = lang;

		String nextObjectNumber = "";
		nextObjectNumber = getMDONumber(moduleID, "0", "0", plantCode);
		JSONObject jsonObj = new JSONObject();

		if (!dataMap.isEmpty()) {
			if (!dataMap.containsKey("objectNumber")) {
				dataMap.put("objectNumber", nextObjectNumber);
			}

			String structId = getStructId(moduleId);

			if ("1".equals(eventId)) {
				sqlHashMap = insertHashMaptoQuery(dataMap, structId, moduleID, eventId);
			}
			if ("4".equals(eventId)) {
				sqlHashMap = insertHashMaptoQuery(dataMap, structId, moduleID, eventId);
			}
			if (sqlHashMap != null && !sqlHashMap.isEmpty()) {
				listSqlMap.add(sqlHashMap);
			}

		}

		// log.info("headerRecord "+headerRecord);
		if (jsonObject != null && !jsonObject.isEmpty()) {
			JSONObject gridData = (JSONObject) jsonObject.get("gridData");
			Set<String> keySetJSON = gridData.keySet();
			for (String gridId : keySetJSON) {
				Object objGrid = gridData.get(gridId);
				if (objGrid instanceof JSONArray) {
					JSONArray jsoArrGrid = (JSONArray) objGrid;
					log.info("Json Grid Array " + jsoArrGrid.size() + " for grid " + gridId);
					for (int i = 0; i < jsoArrGrid.size(); i++) {
						Object objGridData = jsoArrGrid.get(i);
						if (objGridData instanceof JSONObject) {
							JSONObject jsonGridData = (JSONObject) objGridData;
							Map<String, Object> visitMap = convertJSONHashMap(jsonGridData);
							// String insertGrid =insertHashMaptoQuery(visitMap,
							// gridId);
							sqlHashMap = insertHashMaptoQuery(visitMap, gridId);
							if (sqlHashMap != null && !sqlHashMap.isEmpty()) {
								listSqlMap.add(sqlHashMap);
							}
							// log.info(" grid query "+insertGrid));
						}

					}
				}
			}
		}

		for (Map map : listSqlMap) {
			String sqlQuery = (String) map.get("Query");
			Object[] param = (Object[]) map.get("ObjectArr");
			log.info("Query  " + sqlQuery);
			log.info("ObjectArr  " + Arrays.toString(param));

			try {
				pstmt = conn.prepareStatement(sqlQuery);
				for (int i = 0; i < param.length; i++) {
					pstmt.setObject(i + 1, param[i]);
				}
				pstmt.executeUpdate();
				insertion = true;
			} catch (Exception ex) {
				conn.rollback();
				insertion = false;
				exception = ex.toString();
				log.info("Exception occured" + exception);
				break;
			}
		}
		if (insertion) {
			conn.commit();
			updateNextMDO(moduleID, plantCode, nextObjectNumber);
			log.info("Total number of insert for one record" + listSqlMap.size());
			jsonObj.put("objectNumber", nextObjectNumber);
			jsonObj.put("STATUS", "Success");
			if ("1".equals(eventId))
				jsonObj.put("MESSAGE", "Record Succesfully Created " + nextObjectNumber);
			if ("4".equals(eventId))
				jsonObj.put("MESSAGE", "Record Succesfully Updated " + nextObjectNumber);
		} else {
			jsonObj.put("objectNumber", nextObjectNumber);
			jsonObj.put("STATUS", "FAIL");
			jsonObj.put("Exception", exception);

		}

		return jsonObj;

	}

	public boolean sendMail(String senderid, String recipientId, String emailSubject, String emailMesssage) {
		boolean b = false;
		String to = recipientId;
		final String user = senderid;
		final String password = "9050480957a";

		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(user, password);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(emailSubject);
			message.setText(emailMesssage);
			Transport.send(message);
			log.info("Message sent");
			b = true;
		} catch (Exception e) {
			log.info("Exception occured " + e);
			b = false;
		}
		return b;

	}

	public boolean sendMailWithAttachment(String senderid, String recipientId, String emailSubject,
			String emailMesssage) {
		return false;

	}

	// To fetch Structure Id of Module
	private static String getStructId(String objType) {
		String structId = "";
		String Query = "select TOP 1 STRUCID from metadata_mdo WHERE OBJECTTYPE='" + objType + "'";
		List<Map<String, String>> listofMap = new ArrayList<Map<String, String>>();
		log.info("Query   " + Query);
		listofMap = executeQuery(Query);
		for (Map<String, String> map : listofMap) {
			structId = (String) map.get("STRUCID");
		}
		return structId;

	}

	// To update Next MDO number with PREFIX + New Number
	public static String updateNextMDO(String objType, String plantCode, String nextObjectNumber) throws SQLException {
		long start = System.currentTimeMillis();

		Matcher matcher = Pattern.compile("\\d").matcher(nextObjectNumber);
		matcher.find();
		int indexOfDigit = matcher.start();
		int length = nextObjectNumber.substring(indexOfDigit, nextObjectNumber.length()).length();
		long prefixObjectNumber = Long.parseLong(nextObjectNumber.substring(indexOfDigit, nextObjectNumber.length()));
		prefixObjectNumber = prefixObjectNumber + 1;
		String formatted = String.format("%0" + length + "d", prefixObjectNumber);
		String updateObjectNumber = nextObjectNumber.substring(0, indexOfDigit) + formatted;
		String updateNextMdoNumber = "UPDATE NEXTMDONUMBER SET NEXT_NUMBER = '" + updateObjectNumber
				+ "' WHERE OBJECTID='" + objType + "' AND PLANTCODE='" + plantCode + "'";
		int row = utility.executeUpdate(updateNextMdoNumber);
		long end = System.currentTimeMillis();
		log.info("Time taken to update next mdo number " + ((end - start)) + " ms");
		return updateNextMdoNumber;
	}

	// Io fetch Next MDO Number from Table NEXTMDONUMBER
	public static String getMDONumber(String objType, String criteriafield, String criteriaFieldValue,
			String plantCode) {
		String objectNumber = "";
		String Query = "SELECT NEXT_NUMBER FROM NEXTMDONUMBER WHERE OBJECTID='" + objType + "' AND PLANTCODE='"
				+ plantCode + "'";
		List<Map<String, String>> listofMap = new ArrayList<Map<String, String>>();
		log.info("Query   " + Query);
		listofMap = executeQuery(Query);
		for (Map<String, String> map : listofMap) {
			objectNumber = (String) map.get("NEXT_NUMBER");
		}
		return objectNumber;
	}

	public static HashMap<String, Object> insertHashMaptoQuery(Map<String, Object> dataMap, String structId,
			String objectId, String eventId) {

		HashMap<String, Object> returnSqlParam = new HashMap<String, Object>();
		String sqlQuery = "";

		dataMap.values().removeAll(Collections.singleton(null));
		log.info(dataMap);
		if ("1".equals(eventId)) {
			Collection<Object> mapValues = dataMap.values();
			Object args[] = mapValues.toArray();

			StringBuilder sql = new StringBuilder("INSERT INTO ").append("DYN_" + structId + "_" + objectId)
					.append(" (");
			StringBuilder placeholders = new StringBuilder();
			for (Iterator<String> iter = dataMap.keySet().iterator(); iter.hasNext();) {
				sql.append(iter.next());
				placeholders.append("?");
				if (iter.hasNext()) {
					sql.append(",");
					placeholders.append(",");
				}
			}
			sql.append(") VALUES (").append(placeholders).append(")");
			sqlQuery = sql.toString();
			if (StringUtils.isNotBlank(sqlQuery) && args.length > 0) {
				returnSqlParam.put("Query", sqlQuery);
				returnSqlParam.put("ObjectArr", args);
			}
		}
		if ("4".equals(eventId)) {
			Collection<Object> mapValues = dataMap.values();
			Object args[] = mapValues.toArray();
			String objectNumber = (String) dataMap.get("objectNumber");
			dataMap.remove("objectNumber");

			StringBuilder sql = new StringBuilder("UPDATE ").append("DYN_" + structId + "_" + objectId).append(" SET ");
			StringBuilder placeholders = new StringBuilder();
			for (Iterator<String> iter = dataMap.keySet().iterator(); iter.hasNext();) {
				sql.append(iter.next() + " = ?");
				if (iter.hasNext()) {
					sql.append(", ");
				}
			}
			sql.append(" WHERE OBJECTNUMBER =?");
			sqlQuery = sql.toString();
			if (StringUtils.isNotBlank(sqlQuery) && args.length > 0) {
				// log.info("Query "+sqlQuery);
				// log.info("ObjectArr "+args);
				returnSqlParam.put("Query", sqlQuery);
				returnSqlParam.put("ObjectArr", args);
			}
		}

		return returnSqlParam;

	}

	public static HashMap<String, Object> insertHashMaptoQuery(Map<String, Object> visitMap, String gridId) {
		HashMap<String, Object> returnSqlParam = new HashMap<String, Object>();
		visitMap.values().removeAll(Collections.singleton(null));
		String sqlQuery = "";
		String tableName = gridId;
		if (StringUtils.isNotBlank(gridId) && !gridId.startsWith("META_TT_")) {
			tableName = "META_TT_" + gridId;
		}
		if (!visitMap.containsKey("objnr")) {
			String uniqueum = UIDGenerator.getRandomNumber("0", 18);
			visitMap.put("objnr", uniqueum);
		}
		if (visitMap.containsKey("objnr")) {
			String uniqueum = UIDGenerator.getRandomNumber("0", 18);
			visitMap.put("objnr", uniqueum);
		}
		Object args[] = visitMap.values().toArray();
		String uniqueum = UIDGenerator.getRandomNumber("1", 18);
		// args[args.length-1]=uniqueum;

		StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
		StringBuilder placeholders = new StringBuilder();

		for (Iterator<String> iter = visitMap.keySet().iterator(); iter.hasNext();) {
			sql.append(iter.next());
			placeholders.append("?");

			if (iter.hasNext()) {
				sql.append(",");
				placeholders.append(",");
			}
		}

		sql.append(") VALUES (").append(placeholders).append(")");

		sqlQuery = sql.toString();
		returnSqlParam.put("Query", sqlQuery);
		returnSqlParam.put("ObjectArr", args);
		return returnSqlParam;

	}

	@SuppressWarnings({ "unchecked" })
	public static List<String> getGridsInsertQuery(String jsonData, String objectNumber) throws ParseException {
		long starttime = System.currentTimeMillis();
		List<String> insertQuery = new ArrayList<String>();
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(jsonData);
		Object jsonType = json.get("gridData");
		JSONObject getJSONGrid = null;
		JSONArray getJSONGridArray = null;
		if (jsonType instanceof JSONObject) {
			getJSONGrid = (JSONObject) jsonType;
			insertQuery = insertJSONQuery(getJSONGrid, objectNumber);
		}
		if (jsonType instanceof JSONArray) {
			getJSONGridArray = (JSONArray) jsonType;
			for (int j = 0; j < getJSONGridArray.size(); j++) {
				JSONObject gridName = (JSONObject) getJSONGridArray.get(j);
			}

		}

		Set<String> jsonKey = getJSONGrid.keySet();
		log.info("Insert Query Method " + jsonKey.toString());
		log.info("Json Key  " + jsonKey);
		return insertQuery;
	}

	@SuppressWarnings("unchecked")
	public static List<String> insertJSONQuery(JSONObject getJSONGrid, String objectNumber) {
		long starttime = System.currentTimeMillis();
		List<String> insertQuery = new ArrayList<String>();
		Set<String> jsonKey = getJSONGrid.keySet();
		log.info("Insert Query Method " + jsonKey.toString());
		log.info("Json Key  " + jsonKey);

		Iterator<String> itr = jsonKey.iterator();
		while (itr.hasNext()) {
			String tableName = itr.next();
			JSONArray jsonArray = (JSONArray) getJSONGrid.get(tableName);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject object = (JSONObject) jsonArray.get(i);
				List<String> listkey = new ArrayList<String>();
				List<String> listValues = new ArrayList<String>();
				Set<String> insideJsonKey = object.keySet();
				Iterator<String> listitr = insideJsonKey.iterator();
				while (listitr.hasNext()) {
					String insideListKey = listitr.next();
					String queryValues = (String) object.get(insideListKey);
					if (StringUtils.isNotBlank(queryValues)) {
						listkey.add(insideListKey);
						listValues.add((String) object.get(insideListKey));
					}
				}
				insertQuery.add("INSERT INTO META_TT_" + tableName + "  (MATERIALNO," + String.join(" ,", listkey)
				+ ") values ('" + objectNumber + "','" + String.join("','", listValues) + "') ;");
			}

		}
		// }
		long endtime = System.currentTimeMillis();
		log.info("Time taken to convert json to insert query {}" + (endtime - starttime) + " ms");
		return insertQuery;
	}

	public static String getNumberOfInput(List<String> columnList) {
		String inputParam = " ";
		for (String input : columnList) {
			inputParam = inputParam + " ? ,";
		}

		if (inputParam.endsWith(",")) {
			inputParam = inputParam.substring(0, inputParam.length() - 1);
		}

		return inputParam;
	}

	public static String changeObject(String objectNumber, String objectType, String userName, String clientId,
			String lang, HashMap<String, String> fieldValues, boolean workflowRequired, int i) {
		String updQr = "UPDATE DYN_234_" + objectType + " SET ";
		Iterator<Entry<String, String>> itr = fieldValues.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, String> t = itr.next();
			if (itr.hasNext())
				updQr = updQr + t.getKey() + " = '" + t.getValue() + "',";
			else
				updQr = updQr + t.getKey() + " = '" + t.getValue() + "'";
		}

		return updQr + " WHERE OBJECTNUMBER='" + objectNumber + "'";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })

	public static HashMap<String, Object> insertObject(HashMap fieldValues, String tableName) {
		HashMap<String, Object> returnSqlParam = new HashMap<String, Object>();
		fieldValues.values().removeAll(Collections.singleton(null));
		String sqlQuery = "";
		StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
		StringBuilder placeholders = new StringBuilder();

		for (Iterator<String> iter = fieldValues.keySet().iterator(); iter.hasNext();) {
			sql.append("[" + iter.next() + "]");
			placeholders.append("?");

			if (iter.hasNext()) {
				sql.append(",");
				placeholders.append(",");
			}
		}

		sql.append(") VALUES (").append(placeholders).append(")");

		sqlQuery = sql.toString();
		Object args[] = fieldValues.values().toArray();
		returnSqlParam.put("Query", sqlQuery);
		returnSqlParam.put("valuesArr", args);
		return returnSqlParam;

		// return affectedRows;

	}

	public static JSONArray convertCsvToJSONArray(File Csvfile) throws JsonProcessingException, IOException {

		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();
		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(Csvfile).readAll();
		ObjectMapper mapper = new ObjectMapper();

		String jsonStr = mapper.writeValueAsString(readAll);
		JSONArray jsonArr = new JSONArray();
		try {
			jsonArr = (JSONArray) (new JSONParser()).parse(jsonStr);
		} catch (ParseException e) {
			log.info("Exception - " + e.getMessage());
		}
		return jsonArr;
	}

	@SuppressWarnings({ "rawtypes", "static-access" })
	public static void main(String args[]) throws SQLException, ParseException, ClassNotFoundException {

		long starttime = System.currentTimeMillis();
		CustomUtility utility = new CustomUtility();

		// log.info("JSONObject of grid Data
		// "+getgridData("1008","FL_LANG_GRID", "A052-__UI__"));

		removeLeadingZeros("0", "0002443319");
		TreeMap<String, Object> dataMap = new TreeMap<String, Object>();
		dataMap.put("ABCINDIC", "11");
		dataMap.put("ACQDATE", "21");
		dataMap.put("ACQUISVAL", "31");
		dataMap.put("ACTUAL_SUPFLOC", "41");
		dataMap.put("ASSET_NO", null);
		dataMap.put("BUS_AREA", null);
		dataMap.put("CATEGORY", null);
		dataMap.put("COSTCENTER", null);
		dataMap.put("CONSTTYPE", null);
		dataMap.put("EDITMASK", null);
		dataMap.put("EQINSTALL", null);
		dataMap.put("F_AUTHGRP", null);
		dataMap.put("F_CATPROFILE", null);
		dataMap.put("F_COMP_CODE", null);
		dataMap.put("F_DESC", null);
		dataMap.put("FLOC_STATUS", null);
		dataMap.put("PLANPLANT", null);
		dataMap.put("PLANGROUP", null);
		dataMap.put("STANDORDER", "111");
		dataMap.put("FL_PF_Z3", null);
		dataMap.put("FL_PF_C0", null);
		dataMap.put("FL_PF_Y8", null);
		dataMap.put("FL_PF_Z4", null);
		dataMap.put("WORK_CTR", null);

		JSONObject jsonObject = new JSONObject();
		// JSONArray getGridDataArr = getGridJSON("META_TT_FL_LANG_GRID",
		// utility, "A052-__UI__");
		// jsonObject.put("META_TT_FL_LANG_GRID", getGridDataArr);
		// JSONArray getGridDataArr =
		// getGridJSON("META_TT_FL_CLASSIFICATION_GRID", utility,
		// "A052-__UI__");
		// jsonObject.put("META_TT_FL_CLASSIFICATION_GRID", getGridDataArr);
		JSONObject jsonObjectTemp = new JSONObject();

		boolean workflowRequired = false, isDraft = false, validation = false, integrate = true, approved = true;

		String moduleId = "1008";
		String userId = "admin";
		String roleId = "87555523453242324";
		String event = "4";
		String lang = "en";
		String plantCode = "0";
		String objectNumber = "A052-__UI__";
		String blank = "";
		// JSONObject createRecordMethod = createRecord(moduleId, userId,
		// roleId, event, lang, dataMap, jsonObject,
		// plantCode, workflowRequired, isDraft, objectNumber, validation,
		// jsonObjectTemp, integrate, blank,
		// approved);
		// log.info("create"
		// + " record method " + createRecordMethod);
		long endtime = System.currentTimeMillis();

		Collection<Map> resultMap = utility.executeQuery("SELECT * FROM DYN_8080_1007_BKP_20191311",
				new ArrayList<String>(), 1000, 50);
		log.info("resultmap  " + resultMap);
		log.info("Total time taken " + ((endtime - starttime) / 1000) + "  seconds");

		String gridData = "{\"gridData\":{\"LANGUAGEGRID\":[{\"LANGUAGE_G\":\"EN\",\"MATERIALDESCRIPTION\":\"CABLELOCK;TESST;TEST\",\"Attribute1\":\"MRO_TYPE\",\"AttributeValue1\":\"TESST\",\"Attribute2\":\"MRO_SIZE\",\"AttributeValue2\":\"TEST\",\"Attribute3\":\"MRO_MOUNT\",\"AttributeValue3\":\"\",\"Attribute4\":\"MRO_MATERIAL\",\"AttributeValue4\":\"\",\"Attribute5\":\"MRO_COLOR\",\"AttributeValue5\":\"\",\"Attribute6\":\"\",\"AttributeValue6\":\"\",\"Attribute7\":\"\",\"AttributeValue7\":\"\",\"Attribute8\":\"\",\"AttributeValue8\":\"\",\"Attribute9\":\"\",\"AttributeValue9\":\"\",\"Attribute10\":\"\",\"AttributeValue10\":\"\",\"Attribute11\":\"\",\"AttributeValue11\":\"\",\"Attribute12\":\"\",\"AttributeValue12\":\"\",\"Attribute13\":\"\",\"AttributeValue13\":\"\",\"Attribute14\":\"\",\"AttributeValue14\":\"\",\"Attribute15\":\"\",\"AttributeValue15\":\"\",\"ID\":\"4\"}],\"PURCHASETEXTGRID\":[{\"LANGUAGEPURCHASE\":\"EN\",\"DESCRIPTIONPURCHASE\":\"CABLE LOCK;TYPE:TESST;SIZE:TEST\",\"ADDITIONALDESC\":\"\",\"ID\":\"4\"}]}}";
		// log.info(getGridsInsertQuery(gridData,"ABC"));
	}

}

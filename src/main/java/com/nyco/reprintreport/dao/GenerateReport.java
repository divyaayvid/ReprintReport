package com.nyco.reprintreport.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nyco.reprintreport.bean.ReportBean;
import com.nyco.reprintreport.util.ReportContants;

import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class GenerateReport {

	Connection con = null;
	PreparedStatement pstatement = null;
	ResultSet result = null;

	/**
	 * @param query
	 * @param wworkbook
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public WritableSheet CreateReportSheet(String query,String header,String eventType,WritableWorkbook wworkbook)
			throws WriteException, RowsExceededException {
		WritableSheet wsheet = wworkbook.createSheet("First Sheet", 0);
		Label label;

		if (!"".equals(query)) {
			String data = "";
			try {
				data = getGridData(query,header,eventType);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			query = data;
			WritableFont cellFont = new WritableFont(WritableFont.TIMES, 12);
			cellFont.setColour(Colour.PINK);

			WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
			cellFormat.setBackground(Colour.BLUE);

			String[] s = query.split("\\^");
			for (int i = 0; i < s.length; i++) {
				String ss = s[i];
				String[] inner = ss.split("\\|");
				for (int j = 0; j < inner.length; j++) {
					if (i == 0) {
						label = new Label(j, i, inner[j], cellFormat);
						wsheet.addCell(label);
					} else {
						label = new Label(j, i, inner[j]);
						wsheet.addCell(label);
					}
				}
			}
		}
		return wsheet;
	}

	public String getGridData(String query,String header,String eventType) throws SQLException {
		String resultString = header;
		try {
			con = DBConnection.getConnection();
			System.out.println("query..." + query);
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				if(eventType.equalsIgnoreCase("REPRINT_BY_EMPLOYEE")){
					resultString = resultString + result.getString("Badge#") + "|" + result.getString("Associate Name")+"^";
				
				}else if(eventType.equalsIgnoreCase("REPRIENT_PREVIOUSLY_SHIPPED")){
					resultString = resultString + result.getString("Order#") + "|" + result.getString("Reprint date") + "|"
							+ result.getString("Initial Ship date") +"|"+result.getString("Tracking number")+"|"
							+ result.getString("Total# reprints")+"|"+ result.getString("Store#")+"|"+
							 result.getString("Store Name")+"|"+result.getString("Badge ID")+"|"
							+result.getString("Associate Name") + "^";
				
				}else if(eventType.equalsIgnoreCase("REPRINT_BY_STORE")){
					resultString = resultString + result.getString("Store#") + "|" + result.getString("Store Name") + "|"
							+ result.getString("No. of Reprints")+ "^";
				
				}else if(eventType.equalsIgnoreCase("SUMMARIZED_REPRIENTS")){
					resultString = resultString + result.getString("Badge#") + "|" + result.getString("Associate Name") + "|"
							+ result.getString("Store#") + "|" + result.getString("Store Name")+"|"+ result.getString("Total Reprints Count")+ "^";
				
				}else if(eventType.equalsIgnoreCase("TABLET_DEVICE_REPORTS")){
					resultString = resultString + result.getString("Store#") + "|" + result.getString("Device") + "|"
							+ result.getString("OPPS_Actions#") + "^";
				
				}else if(eventType.equalsIgnoreCase("ALL_REPRINTS")){
					resultString = resultString + result.getString("Order#") + "|" + result.getString("Reprint date") + "|"
							+ result.getString("Initial Ship date") +"|"+result.getString("Tracking number")+"|"
							+ result.getString("Total# reprints")+"|"+ result.getString("Store#")+"|"+
							 result.getString("Store Name")+"|"+result.getString("Badge ID")+"|"
							+result.getString("Associate Name")+"|"+
							"^";
				
//				} else{
//					resultString = resultString + result.getString("Order#") + "|" + result.getString("Reprint date") + "|"
//							+ result.getString("Initial Ship date") +"|"+result.getString("Tracking number")+"|"
//							+ result.getString("Total# reprints")+"|"+ result.getString("Store#")+"|"+
//							 result.getString("Store Name")+"|"+result.getString("Badge ID")+"|"
//							+result.getString("Associate Name")+"|"+result.getString("Device")+"|"+
//							 result.getString("OPPS_Actions#")+"^";
//				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultString;
	}

	/**
	 * @param l
	 * @param startDate
	 * @param endDate
	 * @throws SQLException
	 */
	public List<ReportBean> getTabletDeviceReport(String startDate, String endDate) throws SQLException {
		String query = "select store_number as Store#,data6 as Device,count(data5) as OPPS_Actions# from wf_event_tracking where create_date > "
				+ "'" + startDate + "'" + "and create_date < " + "'" + endDate + "'"
				+ " and data6='TABLET' GROUP BY store_number,data6 order by count(data5) DESC;";
		System.out.println("query..." + query);
		pstatement = con.prepareStatement(query);
		result = pstatement.executeQuery();
		List<ReportBean> list = new ArrayList<ReportBean>();
		while (result.next()) {
			ReportBean cd = new ReportBean();
			cd.setType(result.getString("Store#"));
			// cd.setId(result.getInt("Device"));
			cd.setStoreName(result.getString("OPPS_Actions#"));
			cd.setQuery(query);
			list.add(cd);
		}
		return list;
	}
	
	
	/**
	 * @param l
	 * @param startDate
	 * @param endDate
	 * @throws SQLException
	 */
	public List<ReportBean> getAllReprints(String startDate, String endDate) throws SQLException {
		String query = new String();
		String query1 ="select * from WF_EVENT_TRACKING";
		// this is for Daily Report with start date and end date 
		if(startDate !=null && endDate!=null ){
		 query = ReportContants.ALL_REPRINT_QUERY	+ "'" + startDate + "'" + ReportContants.CREATE_DATE + "'" + endDate + "'" + ReportContants.ALL_REPRINT_QUERY1;
		}
		//this is for Daily Report with end date  
		else if(endDate!=null ){
			 query = ReportContants.ALL_REPRINT_QUERY + "'" + endDate + "'"  +ReportContants.ALL_REPRINT_QUERY1;
		}
		List<ReportBean> list = new ArrayList<ReportBean>();
		System.out.println("query..." + query1);
		try {
			con = DBConnection.getConnection();
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			
			while (result.next()) {
				ReportBean cd = new ReportBean();
				cd.setType(result.getString(ReportContants.ORDER));
				cd.setStoreName(result.getString("Reprint date"));
				cd.setInitial_Ship_date(result.getString("Initial Ship date"));
				cd.setTracking_number(result.getString("Tracking number"));
				cd.setTotal_reprints(result.getString("Total# reprints"));
				cd.setStoreNo(result.getString("Store#"));
				cd.setStoreName(result.getString("Store Name"));
				cd.setBadge_ID(result.getInt("Badge ID"));
				cd.setAsssociate_Name(result.getString("Associate Name"));
				cd.setQuery(query);
				list.add(cd);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	public List<ReportBean> getReprintShippedReport(String startDate, String endDate) throws SQLException {
		String query = null;
		
		// this is for Daily Report with start date and end date 
		if(startDate !=null && endDate!=null ){
		 query = ReportContants.ALL_REPRINT_QUERY
				+ "'" + startDate + "'" + ReportContants.CREATE_DATE + "'" + endDate + "'"
				+ " AND  ET.STORE_NUMBER = SN.ID AND EMP.BADGE= ET.BADGE_NUMBER AND ET.DATA4 = O.ORDER_NUMBER AND ET.STORE_NUMBER = O.SHIP_NODE AND ET.DATA2 = O.SHIP_GROUP AND ET.DATA5 <>'' AND ET.EVENT_TYPE = 'REPRINT' GROUP BY ET.DATA4, CONVERT(VARCHAR(10),ET.LAST_UPDATE_DATE,110), ET.DATA5,  ET.EVENT_TYPE, SN.NAME, ET.STORE_NUMBER, ET.BADGE_NUMBER, EMP.FIRST_NAME+' '+ EMP.LAST_NAME, CONVERT(VARCHAR(10),O.COMPLETED_DATE,110) order by count(data5) DESC;;";
		System.out.println("query..." + query);
		}
		//this is for Daily Report with end date  
		else if(endDate!=null ){
			 query = "SELECT ET.DATA4 AS 'Order#', CONVERT(VARCHAR(10),ET.LAST_UPDATE_DATE,110) AS 'Reprint date', CONVERT(VARCHAR(10),O.COMPLETED_DATE,110) AS 'Initial Ship date', ET.DATA5 AS 'Tracking number', COUNT(ET.EVENT_TYPE) AS 'Total# reprints' , ET.STORE_NUMBER AS 'Store#', SN.NAME AS 'Store Name', ET.BADGE_NUMBER AS 'Badge ID', (EMP.FIRST_NAME + ' ' + EMP.LAST_NAME) AS 'Associate Name' FROM WF_EVENT_TRACKING ET, WF_SHIP_NODE SN, WF_ORDER O,WF_EMPLOYEE EMP WHERE ET.CREATE_DATE <="
					+ "'" + endDate + "'" 
					// + "and create_date < " + "'" + endDate + "'"
					+ " AND  ET.STORE_NUMBER = SN.ID AND EMP.BADGE= ET.BADGE_NUMBER AND ET.DATA4 = O.ORDER_NUMBER AND ET.STORE_NUMBER = O.SHIP_NODE AND ET.DATA2 = O.SHIP_GROUP AND ET.DATA5 <>''AND ET.EVENT_TYPE = 'REPRINT' GROUP BY ET.DATA4, CONVERT(VARCHAR(10),ET.LAST_UPDATE_DATE,110), ET.DATA5,  ET.EVENT_TYPE, SN.NAME, ET.STORE_NUMBER, ET.BADGE_NUMBER, EMP.FIRST_NAME+' '+ EMP.LAST_NAME ,CONVERT(VARCHAR(10),O.COMPLETED_DATE,110) order by count(data5) DESC;";
			System.out.println("query..." + query);
			
		}
		
		pstatement = con.prepareStatement(query);
		result = pstatement.executeQuery();
		List<ReportBean> list = new ArrayList<ReportBean>();
		while (result.next()) {
			ReportBean cd = new ReportBean();
			cd.setType(result.getString("Store#"));
			// cd.setId(result.getInt("Device"));
			cd.setStoreName(result.getString("OPPS_Actions#"));
			cd.setQuery(query);
			list.add(cd);
		}
		return list;
	}
	
	public List<ReportBean> getSummerizedReport(String startDate, String endDate) throws SQLException {
		String query = null;
		
		// this is for Daily Report with start date and end date 
		if(startDate !=null && endDate!=null ){
		 query = ReportContants.ALL_REPRINT_QUERY
				+ "'" + startDate + "'" + ReportContants.CREATE_DATE + "'" + endDate + "'"
				+ " AND  ET.STORE_NUMBER = SN.ID AND EMP.BADGE= ET.BADGE_NUMBER AND ET.DATA4 = O.ORDER_NUMBER AND ET.STORE_NUMBER = O.SHIP_NODE AND ET.DATA2 = O.SHIP_GROUP AND ET.DATA5 <>'' AND ET.EVENT_TYPE = 'REPRINT' GROUP BY ET.DATA4, CONVERT(VARCHAR(10),ET.LAST_UPDATE_DATE,110), ET.DATA5,  ET.EVENT_TYPE, SN.NAME, ET.STORE_NUMBER, ET.BADGE_NUMBER, EMP.FIRST_NAME+' '+ EMP.LAST_NAME, CONVERT(VARCHAR(10),O.COMPLETED_DATE,110) order by count(data5) DESC;;";
		System.out.println("query..." + query);
		}
		//this is for Daily Report with end date  
		else if(endDate!=null ){
			 query = "SELECT ET.DATA4 AS 'Order#', CONVERT(VARCHAR(10),ET.LAST_UPDATE_DATE,110) AS 'Reprint date', CONVERT(VARCHAR(10),O.COMPLETED_DATE,110) AS 'Initial Ship date', ET.DATA5 AS 'Tracking number', COUNT(ET.EVENT_TYPE) AS 'Total# reprints' , ET.STORE_NUMBER AS 'Store#', SN.NAME AS 'Store Name', ET.BADGE_NUMBER AS 'Badge ID', (EMP.FIRST_NAME + ' ' + EMP.LAST_NAME) AS 'Associate Name' FROM WF_EVENT_TRACKING ET, WF_SHIP_NODE SN, WF_ORDER O,WF_EMPLOYEE EMP WHERE ET.CREATE_DATE <="
					+ "'" + endDate + "'" 
					// + "and create_date < " + "'" + endDate + "'"
					+ " AND  ET.STORE_NUMBER = SN.ID AND EMP.BADGE= ET.BADGE_NUMBER AND ET.DATA4 = O.ORDER_NUMBER AND ET.STORE_NUMBER = O.SHIP_NODE AND ET.DATA2 = O.SHIP_GROUP AND ET.DATA5 <>''AND ET.EVENT_TYPE = 'REPRINT' GROUP BY ET.DATA4, CONVERT(VARCHAR(10),ET.LAST_UPDATE_DATE,110), ET.DATA5,  ET.EVENT_TYPE, SN.NAME, ET.STORE_NUMBER, ET.BADGE_NUMBER, EMP.FIRST_NAME+' '+ EMP.LAST_NAME ,CONVERT(VARCHAR(10),O.COMPLETED_DATE,110) order by count(data5) DESC;";
			System.out.println("query..." + query);
			
		}
		
		pstatement = con.prepareStatement(query);
		result = pstatement.executeQuery();
		List<ReportBean> list = new ArrayList<ReportBean>();
		while (result.next()) {
			ReportBean cd = new ReportBean();
			cd.setType(result.getString("Store#"));
			// cd.setId(result.getInt("Device"));
			cd.setStoreName(result.getString("OPPS_Actions#"));
			cd.setQuery(query);
			list.add(cd);
		}
		return list;
	}




}

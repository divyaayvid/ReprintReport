package com.nyco.reprintreport.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nyco.reprintreport.bean.ReportBean;
import com.nyco.reprintreport.bean.TabletDeviceBean;
import com.nyco.reprintreport.dao.DBConnection;
import com.nyco.reprintreport.dao.GenerateReport;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

@RestController
public class ReprintreportController  extends DBConnection {
	GenerateReport generateReport = new GenerateReport();

	@GetMapping("/reports")
	public ModelAndView showCharts() {
		return new ModelAndView("ReprintReport.html");
	}

	@GetMapping("/getStoreList")
	public List<String> getStoreList(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		List<String> storeList = new ArrayList<String>();
		try {

			
			storeList.add("ALL_REPRINTS");//All_Reprints_9AM
			storeList.add("REPRIENT_PREVIOUSLY_SHIPPED");//Reprints Previously Shipped			
			storeList.add("REPRINT_BY_STORE");//Reprint_WeeklyReport_ByStore
			storeList.add("REPRINT_BY_EMPLOYEE");////Reprints Previously Shipped_9AM
			storeList.add("SUMMARIZED_REPRIENTS");//Weekly_Summarizes_All_Reprints
			storeList.add("TABLET_DEVICE_REPORTS");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return storeList;
	}

	@GetMapping("/chartData")
	public List<ReportBean> getPieChartData(HttpServletRequest request, HttpServletResponse response)
			throws SQLException {
		String resultString = "";
		List<ReportBean> list = new ArrayList<ReportBean>();
		List<TabletDeviceBean> list1 = new ArrayList<TabletDeviceBean>();
		String eventType = request.getParameter("storeName");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		System.out.println("-------"+eventType);

		try {
			if(eventType.equals("ALL_REPRINTS")){
//				if( !startDate.isEmpty() && !endDate.isEmpty()  ){
					System.out.println("-----if------" + eventType + "-----"+startDate+"----------"+endDate);
					ReportBean r = new ReportBean();
					r.setOrder("or");
					r.setReprint_date("2017-04-09");
					r.setInitial_Ship_date("2017-04-09");
					r.setTracking_number("t1234");
					r.setTotal_reprints("10");
					r.setStoreNo("1");
					r.setStoreName("XYZ");
					r.setBadge_ID(11);
					r.setAsssociate_Name("zzz");
					r.setQuery("query");
					list.add(r);
//					list = generateReport.getAllReprints(startDate, endDate);
//				}
//				else if(endDate != null){
//					
////					list = generateReport.getTabletDeviceReport(startDate, endDate);	
//				}
			}
			else if(eventType.equals("REPRIENT_PREVIOUSLY_SHIPPED")){
//				list = generateReport.getTabletDeviceReport(startDate, endDate);
				System.out.println("---------------");
				ReportBean r = new ReportBean();
				r.setOrder("or");
				r.setReprint_date("2017-04-09");
				r.setInitial_Ship_date("2017-04-09");
				r.setTracking_number("t1234");
				r.setTotal_reprints("10");
				r.setStoreNo("1");
				r.setStoreName("XYZ");
				r.setBadge_ID(11);
				r.setAsssociate_Name("zzz");
				r.setQuery("query");
				list.add(r);
				
			}
			else if(eventType.equals("REPRINT_BY_STORE")){
				ReportBean r = new ReportBean();
				r.setStoreNo("2");
				r.setStoreName("store2");
				r.setTotal_reprints("2");
				r.setQuery("query");
				list.add(r);
				
			}else if(eventType.equals("REPRINT_BY_EMPLOYEE")){
				ReportBean r = new ReportBean();
				r.setBadge_ID(11);
				r.setAsssociate_Name("zzz");
				r.setQuery("query");
				list.add(r);
				
			}
			else if(eventType.equals("SUMMARIZED_REPRIENTS")){
				ReportBean r = new ReportBean();
				r.setBadge_ID(11);
				r.setAsssociate_Name("zzz");
				r.setStoreNo("2");
				r.setStoreName("store2");
				r.setTotal_reprints("2");
				r.setQuery("query");
				list.add(r);
				
			}else if(eventType.equals("TABLET_DEVICE_REPORTS")){
//				TabletDeviceBean t = new TabletDeviceBean();
//				t.setStore("2");
//				t.setDevice("store2");
//				t.setoPPS_Actions("actions");;
//				t.setQuery("query");
//				list1.add(t);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	@GetMapping("/xlsDownload")
	public void doDownloadXlsResults(HttpServletRequest request, HttpServletResponse response)
			throws IOException, WriteException, BiffException {
		WritableWorkbook wworkbook;
		WritableSheet writableSheet;

		String filename = "Tablet_Device";
		filename = filename.concat(".xls");
		OutputStream out = null;
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		String query = request.getParameter("query");
		String header = request.getParameter("header");
		String eventType = request.getParameter("storeName");
		wworkbook = Workbook.createWorkbook(response.getOutputStream());
		if (filename == "Tablet_Device") {
			writableSheet = generateReport.CreateReportSheet(query,header,eventType, wworkbook);
		} else if (filename == "Tablet_Device") {

		} else if (filename == "Tablet_Device") {

		}

		wworkbook.write();
		wworkbook.close();
		// return null;
	}
}

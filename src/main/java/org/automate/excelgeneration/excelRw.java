package org.automate.excelgeneration;

import org.automate.database.SessionCreator;
import org.automate.database.UNETMemberETA;
import org.automate.database.UNETProviderETA;
import org.hibernate.SessionFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


public class excelRw {
	
	private static String file_path ="src/main/resources/UNET_ESR2.xls";
	private static Sheet sheet;
    private static File file = new File(file_path);
    private static int flag_status_provider =0;
    private static int flag_status_member =0;
    private static int flag_overall=0;
    private static String curr_date;
	
	private Workbook getExcelWorkbook(){

		HSSFWorkbook workbook = null;
        if(!file.exists()){
            workbook = new HSSFWorkbook();
        }
        else {
            try{
                FileInputStream fileInputStream = new FileInputStream(file);
                workbook = new HSSFWorkbook(fileInputStream);
                fileInputStream.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return workbook;
  	}
	
	private void writeWorkbook(Workbook workbook) throws IOException{
        try {
            FileOutputStream fileOutputStream  = new FileOutputStream(file_path);
            workbook.write(fileOutputStream);
            fileOutputStream.close();

            System.out.println("file successfully written to : "+ file_path);

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();

        }
    }
	
	public excelRw(String input_prev_date,String input_curr_date) throws IOException {
		String prev_date = input_prev_date;
		curr_date = input_curr_date;
		String comment1 = "In Progress";
		String comment2 = "In Progress";
		Workbook wb = getExcelWorkbook();
		 if (wb.getNumberOfSheets()!=0){
			  sheet = wb.getSheet("OnePay UNET Executive Summary");
			  System.out.println("unet excel sheet opened");
	        }
	    
		 
		
		 formatRequiredCells();
		 SessionCreator sessionCreator = SessionCreator.getInstance();
		 SessionFactory sessionfactory = sessionCreator.sessionFactory;
		 UNETProviderETA unetProviderETA = new UNETProviderETA();
		 UNETMemberETA unetMemberETA = new UNETMemberETA();
		 
		 //to calculate current date ETA
		 Map<String,Map<String, String>> mapProvider = unetProviderETA.getQuery(sessionfactory);
		 Map<String, Map<String, String>> mapMember = unetMemberETA.getQuery(sessionfactory);
		 if(mapProvider.isEmpty() ) {
			 System.out.println("Files not loaded so cannot generate the excel!");
			 return;
		 }
		 enterETA(mapProvider, 14, curr_date);
		 if(mapMember.isEmpty() ) {
			 System.out.println("Files not loaded so cannot generate the excel!");
			 return;
		 }
		 enterETA(mapMember, 18, curr_date);
		 if(flag_status_member==1 && flag_status_provider==1)
			 flag_overall=1;
		 
		 //give Remarks based on current date ETA
		 for(int i=0; i<sheet.getNumMergedRegions(); i++){
	            CellRangeAddress region = sheet.getMergedRegion(i);
	            int rownum = region.getFirstRow();
	            int colnum = region.getFirstColumn();
	            CellStyle cellStyle = sheet.getRow(15).getCell(4).getCellStyle();
	            CellStyle cellStyle2 = sheet.getRow(45).getCell(3).getCellStyle();
	            
	            String str = sheet.getRow(rownum).getCell(colnum).getStringCellValue();
	            System.out.println(i+"  "+str);
	            if(str.equals("In Progress") || str.equals("Completed")){
	            	if(flag_overall==1) {
	            		comment1="Completed";
	            		sheet.getRow(rownum).getCell(colnum).setCellStyle(cellStyle);
	            		}
	            	else {
	            		comment1="In Progress";
	            		sheet.getRow(rownum).getCell(colnum).setCellStyle(cellStyle2);}
	                	sheet.getRow(rownum).getCell(colnum).setCellValue(comment1);
	            }
	            if(str.startsWith("Provider:")){
	            	if(flag_status_member==1) {
	            		comment1="Completed";
	            	}
	            	if(flag_status_provider==1) {
	            		comment2="Completed";
	            	}
	            	
	                String str1 = "Provider: "+ curr_date +" is "+ comment2+"."+"\nMember: "+ curr_date +" is "+ comment1;
	                sheet.getRow(rownum).getCell(colnum).setCellValue(str1);
	            }
	            if(str.startsWith("Payment")){
	                sheet.getRow(rownum).getCell(colnum).setCellValue(str.replace(str.substring(str.lastIndexOf(" "))," "+curr_date));
	            }
	        }
		 
		 //to calculated UNET feeds ETA
		 Map<String, Map<String, String>> mapProvider_feeds = unetProviderETA.getFeedsEta(sessionfactory);
		 Map<String, Map<String, String>> mapMember_feeds = unetMemberETA.getFeedsEta(sessionfactory);
		 enterFeedProv(mapProvider_feeds);
		 enterFeedMemb(mapMember_feeds);
		 
		 //to calculate previous date ETA
		 Map<String, Map<String, String>> mapProvider_prev = unetProviderETA.getPreviousQuery(prev_date, sessionfactory);
		 Map<String, Map<String, String>> mapMember_prev = unetMemberETA.getPreviousQuery(prev_date, sessionfactory);
		 enterETA(mapProvider_prev, 24, prev_date);
		 enterETA(mapMember_prev, 28, prev_date);
		 
		
		 writeWorkbook( wb);

		 

	}
	
	private void formatRequiredCells() {
	//arguments: 1st is row to be formated and 2nd is last cell number upto which row is formated
		
        formatRow(14, 16);
        formatRow(16, 16);
        formatRow(18, 14);
        formatRow(20, 14);

        formatRow(24, 16);
        formatRow(26, 16);
        formatRow(28, 14);
        formatRow(30, 14);

        formatRow(35, 16);
        formatRow(36, 16);

        formatRow(40, 16);
        formatRow(41, 16);
    }

    private void formatRow(int i, int j) {
        CellStyle cellStyle1 = sheet.getRow(35).getCell(4).getCellStyle();
        Row row = sheet.getRow(i);
        int cellnum =row.getFirstCellNum()+2;
        while(cellnum<=j){
            row.getCell(cellnum).setCellValue(" ");
            row.getCell(cellnum).setCellStyle(cellStyle1);
            cellnum++;
        }
        System.out.println(i+ " is formated");
    }
    
	private void enterFeedMemb(Map<String, Map<String, String>> feeds) {
		Map<String, String> feeds_eta = feeds.get("eta");
		Map<String, String> feeds_noteta = feeds.get("c");
		
		int cellnum =4;
		int rowindex = 40;
        Row row1 = sheet.getRow(rowindex);
        Row row2 = sheet.getRow(rowindex+1);
        CellStyle cellStyle1 = sheet.getRow(35).getCell(4).getCellStyle();
        
        for(Map.Entry<String,String> entry:feeds_eta.entrySet()) {
        	
        	if(entry.getKey().equals("GEFT")) {
        		row1.getCell(7).setCellValue(entry.getValue());
        		row1.getCell(7).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("OVPAY")) {
        		row1.getCell(4).setCellValue(entry.getValue());
        		row1.getCell(4).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("CHKRC")) {
        		row1.getCell(9).setCellValue(entry.getValue());
        		row1.getCell(9).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("UCAS")) {
        		row1.getCell(6).setCellValue(entry.getValue());
        		row1.getCell(6).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("TOPS")) {
        		row1.getCell(5).setCellValue(entry.getValue());
        		row1.getCell(5).setCellStyle(cellStyle1);
        	}
        	
        	if(entry.getKey().equals("EOB")) {
        		row1.getCell(8).setCellValue(entry.getValue());
        		row1.getCell(8).setCellStyle(cellStyle1);
        	}
        }
        
        ///to change this
        if(!feeds_eta.isEmpty()) {
        	if( !feeds_eta.get("EOB").equals(null)) {
        String eta_EHEALTH=feeds_eta.get("EOB").substring(0, 9).substring(3,5);
        String eta_EHEALTH2 = feeds_eta.get("EOB").substring(0, 9).replaceAll(eta_EHEALTH+"$",Integer.toString(Integer.valueOf(eta_EHEALTH)+1));
        row1.getCell(10).setCellValue(eta_EHEALTH2.concat(" 02:00 PM CST"));
		row1.getCell(10).setCellStyle(cellStyle1);
		row1.getCell(11).setCellValue(eta_EHEALTH2.concat(" 02:00 PM CST"));
		row1.getCell(11).setCellStyle(cellStyle1);
        }}
        if(!feeds_noteta.isEmpty()) {
        	if( !feeds_noteta.get("OVPAY").equals(null)) {
        String eta_EHEALTH=feeds_noteta.get("OVPAY").substring(0, 9).substring(3,5);
        String eta_EHEALTH2 = feeds_noteta.get("OVPAY").substring(0, 9).replaceAll(eta_EHEALTH+"$",Integer.toString(Integer.valueOf(eta_EHEALTH)+1));
        row1.getCell(10).setCellValue(eta_EHEALTH2.concat(" 02:00 PM CST"));
		row1.getCell(10).setCellStyle(cellStyle1);
		row1.getCell(11).setCellValue(eta_EHEALTH2.concat(" 02:00 PM CST"));
		row1.getCell(11).setCellStyle(cellStyle1);
        }}
        
		for(Map.Entry<String,String> entry:feeds_noteta.entrySet()) {

        	if(entry.getKey().equals("GEFT")) {
        		row2.getCell(7).setCellValue(entry.getValue());
        		row2.getCell(7).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("OVPAY")) {
        		row2.getCell(4).setCellValue(entry.getValue());
        		row2.getCell(4).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("CHKRC")) {
        		row2.getCell(9).setCellValue(entry.getValue());
        		row2.getCell(9).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("UCAS")) {
        		row2.getCell(6).setCellValue(entry.getValue());
        		row2.getCell(6).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("TOPS")) {
        		row2.getCell(5).setCellValue(entry.getValue());
        		row2.getCell(5).setCellStyle(cellStyle1);
        	}        	
        	if(entry.getKey().equals("EOB")) {
        		row2.getCell(8).setCellValue(entry.getValue());
        		row2.getCell(8).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("EHEALTH")) {
        		row1.getCell(10).setCellValue(" ");
        		row2.getCell(10).setCellValue(entry.getValue());
        		row2.getCell(10).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("CAMSRX")) {
        		row1.getCell(11).setCellValue(" ");
        		row2.getCell(11).setCellValue(entry.getValue());
        		row2.getCell(11).setCellStyle(cellStyle1);        	}
        }

	}
	
	private void enterFeedProv(Map<String, Map<String, String>> feeds) {
		Map<String, String> feeds_eta = feeds.get("eta");
		Map<String, String> feeds_noteta = feeds.get("c");
		
		int cellnum =4;
		int rowindex = 35;
        Row row1 = sheet.getRow(rowindex);
        Row row2 = sheet.getRow(rowindex+1);
        CellStyle cellStyle1 = sheet.getRow(35).getCell(4).getCellStyle();
        
        for(Map.Entry<String,String> entry:feeds_eta.entrySet()) {
        	if(entry.getKey().equals("DELTRX")) {
        		row1.getCell(4).setCellValue(entry.getValue());
        		row1.getCell(4).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("SAM")) {
        		row1.getCell(5).setCellValue(entry.getValue());
        		row1.getCell(5).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("Treasury")) {
        		row1.getCell(7).setCellValue(entry.getValue());
        		row1.getCell(7).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("OVPAY")) {
        		row1.getCell(8).setCellValue(entry.getValue());
        		row1.getCell(8).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("CHKRC")) {
        		row1.getCell(14).setCellValue(entry.getValue());
        		row1.getCell(14).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("UCAS")) {
        		row1.getCell(13).setCellValue(entry.getValue());
        		row1.getCell(13).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("TOPS")) {
        		row1.getCell(11).setCellValue(entry.getValue());
        		row1.getCell(11).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("CAMS")) {
        		row1.getCell(12).setCellValue(entry.getValue());
        		row1.getCell(12).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("B2B-835")) {
        		row1.getCell(10).setCellValue(entry.getValue());
        		row1.getCell(10).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("EPS-835")) {
        		row1.getCell(9).setCellValue(entry.getValue());
        		row1.getCell(9).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("PRA")) {
        		row1.getCell(15).setCellValue(entry.getValue());
        		row1.getCell(15).setCellStyle(cellStyle1);
        	}
        }
        
		for(Map.Entry<String,String> entry:feeds_noteta.entrySet()) {
        	if(entry.getKey().equals("DELTRX")) {
        		row2.getCell(4).setCellValue(entry.getValue());
        		row2.getCell(4).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("SAM")) {
        		row2.getCell(5).setCellValue(entry.getValue());
        		row2.getCell(5).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("Treasury")) {
        		row2.getCell(7).setCellValue(entry.getValue());
        		row2.getCell(7).setCellStyle(cellStyle1);

        	}
        	if(entry.getKey().equals("OVPAY")) {
        		row2.getCell(8).setCellValue(entry.getValue());
        		row2.getCell(8).setCellStyle(cellStyle1);

        	}
        	if(entry.getKey().equals("CHKRC")) {
        		row2.getCell(14).setCellValue(entry.getValue());
        		row2.getCell(14).setCellStyle(cellStyle1);

        	}
        	if(entry.getKey().equals("UCAS")) {
        		row2.getCell(13).setCellValue(entry.getValue());
        		row2.getCell(13).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("TOPS")) {
        		row2.getCell(11).setCellValue(entry.getValue());
        		row2.getCell(11).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("CAMS")) {
        		row2.getCell(12).setCellValue(entry.getValue());
        		row2.getCell(12).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("B2B-835")) {
        		row2.getCell(10).setCellValue(entry.getValue());
        		row2.getCell(10).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("EPS-835")) {
        		row2.getCell(9).setCellValue(entry.getValue());
        		row2.getCell(9).setCellStyle(cellStyle1);
        	}
        	if(entry.getKey().equals("PRA")) {
        		row2.getCell(15).setCellValue(entry.getValue());
        		row2.getCell(15).setCellStyle(cellStyle1);
        	}
        }
		
		row2.getCell(6).setCellValue("NA");
		row2.getCell(6).setCellStyle(cellStyle1);

	}
	
	private void enterETA(Map<String, Map<String, String>> UNET, int rowindex, String date) {
        Row row1 = sheet.getRow(rowindex);
        CellStyle cellStyle = sheet.getRow(rowindex+1).getCell(4).getCellStyle(); //blue color
        CellStyle cellStyle2 = sheet.getRow(45).getCell(3).getCellStyle();			//green color
//        CellStyle cellStyle1 = sheet.getRow(35).getCell(4).getCellStyle();			//center alignment
        Map<String, String> UNET_eta = UNET.get("eta");
		Map<String, String> UNET_noteta = UNET.get("c");
		
        if(rowindex==14|| rowindex==24) {  
        	row1.getCell(3).setCellValue("UNET\n"+date);
}
        else {
        	row1.getCell(3).setCellValue("Member\n"+date);

        }
        
        checkNEnter(UNET_noteta,rowindex,rowindex+1, "Complete",cellStyle);
        checkNEnter(UNET_eta, rowindex+2, rowindex+1, "On Plan", cellStyle2);
        
		
	}
	
	private void checkNEnter(Map<String, String> uNET, int rowindex,int rowindex2, String comment, CellStyle cellStyle) {
		Row row1 = sheet.getRow(rowindex);
		CellStyle cellStyle1 = sheet.getRow(35).getCell(4).getCellStyle();			//center alignment
	     
		for(Map.Entry<String,String> entry:uNET.entrySet()) {
        	if(entry.getKey().equals("PrePreProcessor") || entry.getKey().equals("IntakeFileLoading")) {
        		row1.getCell(5).setCellValue(entry.getValue());
        		row1.getCell(5).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(5).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(5).setCellStyle(cellStyle);
        	}
        	if(entry.getKey().equals("PreProcessor") || entry.getKey().equals("DBIProcessing")) {
        		row1.getCell(6).setCellValue(entry.getValue());
        		row1.getCell(6).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(6).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(6).setCellStyle(cellStyle);
        	}
        	if(entry.getKey().equals("Intake") || entry.getKey().equals("SchedulingOfSub")) {
        		row1.getCell(7).setCellValue(entry.getValue());
        		row1.getCell(7).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(7).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(7).setCellStyle(cellStyle);

        	}
        	if(entry.getKey().equals("Scheduling") || entry.getKey().equals("Harvesting")) {
        		row1.getCell(8).setCellValue(entry.getValue());
        		row1.getCell(8).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(8).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(8).setCellStyle(cellStyle);

        	}
        	if(entry.getKey().equals("ReleaseNConsolidation") || entry.getKey().equals("ConsolidationNOverpayment")) {
        		row1.getCell(9).setCellValue(entry.getValue());
        		row1.getCell(9).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(9).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(9).setCellStyle(cellStyle);

        	}
        	if(entry.getKey().equals("PaymentProcessing") || entry.getKey().equals("CorePayment")) {
        		row1.getCell(10).setCellValue(entry.getValue());
        		row1.getCell(10).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(10).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(10).setCellStyle(cellStyle);
        	}
        	if(entry.getKey().equals("835EPS_B2B") || entry.getKey().equals("BenefitHeaderProcessing")) {
        		row1.getCell(11).setCellValue(entry.getValue());
        		row1.getCell(11).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(11).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(11).setCellStyle(cellStyle);
        	}
        	if(entry.getKey().equals("EPSFundingFile") || entry.getKey().equals("MemberEhealthFeedbackFile")) {
        		if(comment.equals("Complete"))
        			flag_status_member=1;
        		row1.getCell(12).setCellValue(entry.getValue());
        		row1.getCell(12).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(12).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(12).setCellStyle(cellStyle);
        	}
        	if(entry.getKey().equals("FundingReport") || entry.getKey().equals("PostPayExtract")) {
        		row1.getCell(13).setCellValue(entry.getValue());
        		row1.getCell(13).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(13).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(13).setCellStyle(cellStyle);
        	}
        	if(entry.getKey().equals("ProviderPRA")) {
        		if(comment.equals("Complete"))
        			flag_status_provider=1;
        		row1.getCell(14).setCellValue(entry.getValue());
        		row1.getCell(14).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(14).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(14).setCellStyle(cellStyle);
        	}
        	if(entry.getKey().equals("PostPaymentExtract")) {
        		row1.getCell(15).setCellValue(entry.getValue());
        		row1.getCell(15).setCellStyle(cellStyle1);
        		sheet.getRow(rowindex2).getCell(15).setCellValue(comment);
                sheet.getRow(rowindex2).getCell(15).setCellStyle(cellStyle);
        	}
        }
	}



   // public static void main(String[] args) {

//        public excelRw(String inputCurrdate, String inputLastdate, String inputcomments) {
//        Workbook wb = null;
//        Sheet sh = null;
//        FileInputStream fis= null;
//        FileOutputStream fos=null;
//        Row row;
//        Cell cellCom;
//        Cell cellComDate;
//        Cell cellFormat = null;
//        Cell cell;
//        Cell cell1;
//        Cell cell2;
//        //String in_date = "2019-10-08";
//        //String in_date_last = "2019-10-07";
//       // String in_comments = "Comments : cycle is running";
//        String status = null;
//        String in_date = inputCurrdate;
//        String in_date_last = inputLastdate;
//        String  in_comments = "Comments : " +inputcomments;
//
//        List batchData = null;
//        List batchData1 = null;
//        String endTime = null;
//        String codeReturnTrue = "True";
//        try {
//            fis = new FileInputStream("/sftp-inbox/Vbs_Excel_Example.xlsx");
//            //fis = new FileInputStream("./Vbs_Excel_Example.xlsx");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            wb = WorkbookFactory.create(fis);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InvalidFormatException e) {
//            e.printStackTrace();
//        }
//
//
//        sh = wb.getSheet("VBS_Excel_Example");
//        System.out.println("Control goes to excel");
//
//        //HSSFCellStyle my_style = new HSSFWorkbook();
//
//        CellStyle backgroundStyle = wb.createCellStyle();
//
//
//        System.out.println("calling hiberbante utils");
//        // getDateFormat dateformat = new getDateFormat();
//        System.out.println("in_date value " +in_date);
//
////**********************************************************************************************************************
////Setting comments and dates
////**********************************************************************************************************************
//
//
//        //row = sh.getRow(0);
//        //cell = row.getCell(0);
//
//        //cell.setCellValue("HSE -Application Processing Overall Status");
//
//
//        cellCom = sh.getRow(1).getCell(0);
//        cellCom.setCellValue("Executive Summary Status Run of " +in_date);
//
//        cellComDate = sh.getRow(6).getCell(0);
//        cellComDate.setCellValue("Status of Health Statement Engine Application processing date  " +in_date);
//
//
//        System.out.println("in_date_new value " +in_date);
//        System.out.println("String hibernarte");
//
//
////**********************************************************************************************
////*******************************format before updating*********************************************
//
//        cellFormat = sh.getRow(11).getCell(2);
//        cellFormat.setCellValue(" ");
//
//        CellStyle cellStyle2 = wb.createCellStyle();
//        cellStyle2.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
//        cellStyle2.setFillPattern(FillPatternType.NO_FILL);
//        // cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(2);
//        cellFormat.setCellValue(" ");
//
//
//        cellFormat = sh.getRow(11).getCell(3);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(3);
//        cellFormat.setCellValue(" ");
//
//
//        cellFormat = sh.getRow(11).getCell(4);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(4);
//        cellFormat.setCellValue(" ");
//
//
//        cellFormat = sh.getRow(11).getCell(5);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(5);
//        cellFormat.setCellValue(" ");
//
//        cellFormat = sh.getRow(11).getCell(6);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(6);
//        cellFormat.setCellValue(" ");
//
//
//        cellFormat = sh.getRow(11).getCell(7);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(7);
//        cellFormat.setCellValue(" ");
//
//
//        cellFormat = sh.getRow(11).getCell(8);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(8);
//        cellFormat.setCellValue(" ");
//
//        cellFormat = sh.getRow(11).getCell(9);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(9);
//        cellFormat.setCellValue(" ");
//
//        cellFormat = sh.getRow(11).getCell(10);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(10);
//        cellFormat.setCellValue(" ");
//
//
//        cellFormat = sh.getRow(11).getCell(11);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(11);
//        cellFormat.setCellValue(" ");
//
//        cellFormat = sh.getRow(11).getCell(12);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(12);
//        cellFormat.setCellValue(" ");
//
//
//        cellFormat = sh.getRow(11).getCell(13);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//        cellFormat = sh.getRow(10).getCell(13);
//        cellFormat.setCellValue(" ");
//
//        cellFormat =sh.getRow(3).getCell(3);
//        cellFormat.setCellValue(" ");
//        cellFormat.setCellStyle(cellStyle2);
//
//
////************************************************************************************************
//
////***********************************************************************************************************************
////currnet processing cycle details
////***********************************************************************************************************************
//
//        HibernateUtils utils = new HibernateUtils();
//        batchData = utils.testRetrieveByQuery(in_date);
//
//        String stratTime = null;
//        String batchNmae = null;
//
//
//
//        Font font1 = wb.createFont();
//        font1.setColor(IndexedColors.WHITE.getIndex());
//        CellStyle cellStyle1 = wb.createCellStyle();
//        cellStyle1.setFont(font1);
//        cellStyle1.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
//        cellStyle1.setFillPattern(FillPatternType.THIN_HORZ_BANDS);
//        // cell.setCellStyle(cellStyle1);
//
//        Font font = wb.createFont();
//        font.setColor(IndexedColors.WHITE.getIndex());
//        // font.setBold(true);
//        // set up background color
//        CellStyle cellStyle = wb.createCellStyle();
//        cellStyle.setFont(font);
//        cellStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
//        cellStyle.setFillPattern(FillPatternType.THIN_HORZ_BANDS);
//        // cell.setCellStyle(cellStyle);
//
//            cell =sh.getRow(3).getCell(3);
//            cell.setCellValue("In Progress");
//            cell.setCellStyle(cellStyle1);
//
//            status = "HSE Processing for cycle (HSE Processing" + in_date + ") is in progress";
//            Cell cell5 = sh.getRow(4).getCell(0);
//            //System.out.println(cell);
//            cell5.setCellValue("HSE Processing for cycle (HSE Processing" + in_date + ") is in progress");
//
//
//        for (Iterator iterator = batchData.iterator(); iterator.hasNext(); ) {
//            frameworkBS batchstatus = (frameworkBS) iterator.next();
//            System.out.println("Starting " + batchstatus.getBTCH_NM());
//
//
//
//            if ((batchstatus.getBTCH_NM().equals("sjCreateSubForRelease")) && (batchstatus.getBTCH_STS_CD().equals("C"))) {
//                cell = sh.getRow(11).getCell(2);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//
//
//
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(2);
//                //cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//
//
//
//            }
//
//            else if ((batchstatus.getBTCH_NM().equals("sjLoadHseClmHist")) && (batchstatus.getBTCH_STS_CD().equals("C"))) {
//                cell = sh.getRow(11).getCell(3);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                System.out.println(cell);
//
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(3);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//
//
//            }
//
//            else if ((batchstatus.getBTCH_NM().equals("sjLoadAdoptionPurge")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(4);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(4);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            else if ((batchstatus.getBTCH_NM().equals("sjTopsResp")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(5);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(5);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            else if ((batchstatus.getBTCH_NM().equals("sjCamsRespValLoad")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(6);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                System.out.println(cell);
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(6);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            else if ((batchstatus.getBTCH_NM().equals("sjUpdateClmKeyProcInd")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(7);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(7);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            else if ((batchstatus.getBTCH_NM().equals("sjClmsPullFdbck")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(8);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(8);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            else if ((batchstatus.getBTCH_NM().equals("sjLoadBnftHdrRespfile")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(9);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(9);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            else if ((batchstatus.getBTCH_NM().equals("sjHseDuncanMSG")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(10);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(10);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            else if ((batchstatus.getBTCH_NM().equals("sjHseDuncanConsolidate")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(11);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(11);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            if ((batchstatus.getBTCH_NM().equals("seqCreateReport")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(12);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(12);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//            else if ((batchstatus.getBTCH_NM().equals("sjLoadPoleobsprsAdoption")) && (batchstatus.getBTCH_STS_CD().equals("C"))){
//                cell = sh.getRow(11).getCell(13);
//                cell.setCellValue(" ");
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//                endTime = batchstatus.getEND_DTTM();
//                cell = sh.getRow(10).getCell(13);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//
//                cell =sh.getRow(3).getCell(3);
//                cell.setCellValue("Complete");
//                cell.setCellStyle(cellStyle);
//
//
//                Cell cell10 = sh.getRow(4).getCell(0);
//                //System.out.println(cell);
//                cell5.setCellValue("HSE Processing for cycle (HSE Processing" + in_date + ") completed successfully");
//
//
//            }
//            //(cell1.getValue()==null ||cell1.getValue().toString().length()==0)
//           //else if ((batchstatus.getBTCH_NM().equals("sjLoadPoleobsprsAdoption")) && ((batchstatus.getBTCH_STS_CD().equals("R")) || (batchstatus.getBTCH_STS_CD().equals(" "))))
//
//          //  {
//                //else if ((sh.getRow(11).getCell(13).getStringCellValue()==null) || (sh.getRow(11).getCell(13).toString().length()==0) || (sh.getRow(11).getCell(13).equals(" "))){
//           //     cell =sh.getRow(3).getCell(3);
//            //    cell.setCellValue("In Progress");
//           //     cell.setCellStyle(cellStyle1);
//            //}
//        }
//
////**********************************************************************************************************************
////calculation the status of the cycle
////**********************************************************************************************************************
//       // status = "HSE Processing for cycle (HSE Processing" + in_date + ") is in progress";
//       // Cell cell5 = sh.getRow(4).getCell(0);
//        //System.out.println(cell);
//       // cell5.setCellValue("HSE Processing for cycle (HSE Processing" + in_date + ") is in progress");
//
//        Cell cell6 = sh.getRow(5).getCell(0);
//        cell6.setCellValue(in_comments);
//
//        Cell cell7 = sh.getRow(13).getCell(0);
//        cell7.setCellValue(in_comments);
//
//        String statusLast = "Comments: HSE processing " + in_date_last + " is Completed and file has been sent  for processing in Duncan Prod environment";
//        Cell cell8 = sh.getRow(22).getCell(0);
//        cell8.setCellValue(statusLast);
////**********************************************************************************************************************
////Last cycle processing details
////************************************************************************************************************************
//        int i=1;
//        Date date = new Date();
//        Date newDate = subtractDays(date, i);
//        System.out.println("Java Date after subtracting "+i+" days: "+newDate.toString());
//
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        //batch_dt =getDateFormat(df.getDateFormat(newDate));
//        String batch_dt=df.format(newDate);
//        batch_dt=batch_dt.toUpperCase();
//
//        System.out.print("dateBefore frmat to batch dt "+batch_dt);
//
//        Cell cell4 = sh.getRow(16).getCell(0);
//        cell4.setCellValue("Status of Health Statement Engine Application processing date  " +in_date_last);
//
//
//
//
//
//
///*        String minusDate = null;
//        String fmt = "yyyy-MM-dd";
//        String dt = in_date;
//        java.text.DateFormat df = new java.text.SimpleDateFormat(fmt);
//        java.util.Calendar cal = java.util.Calendar.getInstance();
//        try {
//            cal.setTime(df.parse(dt));
//            cal.add(Calendar.DAY_OF_MONTH, -1);
//            minusDate = String.valueOf(cal.getTime());
//            System.out.println(cal.getTime());
//        } catch (Exception e) {
//        }*/
//
//        HibernateUtils utilslast = new HibernateUtils();
//        batchData1 = utilslast.testRetrieveByQuery(in_date_last);
//
//
//        for (Iterator iterator = batchData1.iterator(); iterator.hasNext(); ) {
//            frameworkBS batchstatus1 = (frameworkBS) iterator.next();
//            System.out.println("Starting Testing");
//            System.out.println("Starting " + batchstatus1.getBTCH_NM());
//
//            if (batchstatus1.getBTCH_NM().equals("sjCreateSubForRelease")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(2);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//
//
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjLoadHseClmHist")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(3);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//                endTime = batchstatus1.getEND_DTTM();
//                stratTime = batchstatus1.getSTRT_DTTM();
//                batchNmae = batchstatus1.getBTCH_NM();
//
//                System.out.println("Inside Testing endTime " +endTime);
//                System.out.println("Inside Testing stratTime " +stratTime);
//                System.out.println("Inside Testing batchNmae " +batchNmae);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjLoadAdoptionPurge")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(4);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//                endTime = batchstatus1.getEND_DTTM();
//                stratTime = batchstatus1.getSTRT_DTTM();
//                batchNmae = batchstatus1.getBTCH_NM();
//
//                System.out.println("Inside Testing endTime " +endTime);
//                System.out.println("Inside Testing stratTime " +stratTime);
//                System.out.println("Inside Testing batchNmae " +batchNmae);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjTopsResp")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(5);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjCamsRespValLoad")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(6);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjUpdateClmKeyProcInd")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(7);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjClmsPullFdbck")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(8);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjLoadBnftHdrRespfile")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(9);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjHseDuncanMSG")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(10);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjHseDuncanConsolidate")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(11);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("seqCreateReport")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(12);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//            }
//
//            if (batchstatus1.getBTCH_NM().equals("sjLoadPoleobsprsAdoption")) {
//                endTime = batchstatus1.getEND_DTTM();
//                cell = sh.getRow(20).getCell(13);
//                cell.setCellValue(" ");
//                cell.setCellValue(endTime);
//
//
//            }
//        }
//
//        try {
//            //fos = new FileOutputStream("./Vbs_Excel_Example.xlsx");
//            fos = new FileOutputStream("/sftp-inbox/Vbs_Excel_Example.xlsx");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            wb.write(fos);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            fos.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

//    public static Date subtractDays(Date date, int days) {
//        // TODO Auto-generated method stub
//        GregorianCalendar cal = new GregorianCalendar();
//        cal.setTime(date);
//        cal.add(Calendar.DATE, -days);
//
//        return cal.getTime();
//        //return null;
//    }
//
//    private String getDateFormat(String batch_dt){
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        Date myDate = Date.from(Instant.parse(batch_dt));
//        return formatter.format(myDate);
//    }
    //return codeReturnTrue;
}


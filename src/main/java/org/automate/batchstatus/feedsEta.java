package org.automate.batchstatus;

import java.sql.Timestamp;

public class feedsEta implements java.io.Serializable {

	 	private String DATA_FL_NM;
	    private String PRTN_ID;
	    private Timestamp STRT_DTTM;
	    private Timestamp END_DTTM;
	    private String TRNSF_TYP_CD;
	    private String AUD_NM;
	    private String STS_CD;
	    private int FL_ID;
	    private String FTP_HST_NM;
	    private Timestamp CREAT_DTTM;
	    private Timestamp UPDT_DTTM;


		private int DATA_BYTE_NBR;
	    private int AUD_BYTE_NBR;
	    private String CREAT_BY_NM;
	    private String UPDT_BY_NM;
	    private String RMOT_DIR_NM;
	    private String RMOT_DATA_FL_NM;
	    private String RMOT_AUD_FL_NM;
	    private String RMOT_TRIG_FL_NM;


//	    private String END_DTTM;
	    private int TRNSF_ATMPT_CNT; 
	    
	    
	    public feedsEta(String dATA_FL_NM, String pRTN_ID, Timestamp sTRT_DTTM, Timestamp eND_DTTM, String tRNSF_TYP_CD,
		String aUD_NM, String sTS_CD, int fL_ID, String fTP_HST_NM, Timestamp cREAT_DTTM, Timestamp uPDT_DTTM,
		int dATA_BYTE_NBR, int aUD_BYTE_NBR, String cREAT_BY_NM, String uPDT_BY_NM, String rMOT_DIR_NM,
		String rMOT_DATA_FL_NM, String rMOT_AUD_FL_NM, String rMOT_TRIG_FL_NM, int tRNSF_ATMPT_CNT) {
	DATA_FL_NM = dATA_FL_NM;
	PRTN_ID = pRTN_ID;
	STRT_DTTM = sTRT_DTTM;
	END_DTTM = eND_DTTM;
	TRNSF_TYP_CD = tRNSF_TYP_CD;
	AUD_NM = aUD_NM;
	STS_CD = sTS_CD;
	FL_ID = fL_ID;
	FTP_HST_NM = fTP_HST_NM;
	CREAT_DTTM = cREAT_DTTM;
	UPDT_DTTM = uPDT_DTTM;
	DATA_BYTE_NBR = dATA_BYTE_NBR;
	AUD_BYTE_NBR = aUD_BYTE_NBR;
	CREAT_BY_NM = cREAT_BY_NM;
	UPDT_BY_NM = uPDT_BY_NM;
	RMOT_DIR_NM = rMOT_DIR_NM;
	RMOT_DATA_FL_NM = rMOT_DATA_FL_NM;
	RMOT_AUD_FL_NM = rMOT_AUD_FL_NM;
	RMOT_TRIG_FL_NM = rMOT_TRIG_FL_NM;
	TRNSF_ATMPT_CNT = tRNSF_ATMPT_CNT;
}



		public String getDATA_FL_NM() {
			return DATA_FL_NM;
		}



		public void setDATA_FL_NM(String dATA_FL_NM) {
			DATA_FL_NM = dATA_FL_NM;
		}



		public String getPRTN_ID() {
			return PRTN_ID;
		}



		public void setPRTN_ID(String pRTN_ID) {
			PRTN_ID = pRTN_ID;
		}



		public Timestamp getSTRT_DTTM() {
			return STRT_DTTM;
		}



		public void setSTRT_DTTM(Timestamp sTRT_DTTM) {
			STRT_DTTM = sTRT_DTTM;
		}



		public Timestamp getEND_DTTM() {
			return END_DTTM;
		}



		public void setEND_DTTM(Timestamp eND_DTTM) {
			END_DTTM = eND_DTTM;
		}



		public String getTRNSF_TYP_CD() {
			return TRNSF_TYP_CD;
		}



		public void setTRNSF_TYP_CD(String tRNSF_TYP_CD) {
			TRNSF_TYP_CD = tRNSF_TYP_CD;
		}



		public String getAUD_NM() {
			return AUD_NM;
		}



		public void setAUD_NM(String aUD_NM) {
			AUD_NM = aUD_NM;
		}



		public String getSTS_CD() {
			return STS_CD;
		}



		public void setSTS_CD(String sTS_CD) {
			STS_CD = sTS_CD;
		}



		public int getFL_ID() {
			return FL_ID;
		}



		public void setFL_ID(int fL_ID) {
			FL_ID = fL_ID;
		}



		public String getFTP_HST_NM() {
			return FTP_HST_NM;
		}



		public void setFTP_HST_NM(String fTP_HST_NM) {
			FTP_HST_NM = fTP_HST_NM;
		}



		public Timestamp getCREAT_DTTM() {
			return CREAT_DTTM;
		}



		public void setCREAT_DTTM(Timestamp cREAT_DTTM) {
			CREAT_DTTM = cREAT_DTTM;
		}



		public Timestamp getUPDT_DTTM() {
			return UPDT_DTTM;
		}



		public void setUPDT_DTTM(Timestamp uPDT_DTTM) {
			UPDT_DTTM = uPDT_DTTM;
		}



		public int getTRNSF_ATMPT_CNT() {
			return TRNSF_ATMPT_CNT;
		}



		public void setTRNSF_ATMPT_CNT(int tRNSF_ATMPT_CNT) {
			TRNSF_ATMPT_CNT = tRNSF_ATMPT_CNT;
		}



		public int getDATA_BYTE_NBR() {
			return DATA_BYTE_NBR;
		}



		public void setDATA_BYTE_NBR(int dATA_BYTE_NBR) {
			DATA_BYTE_NBR = dATA_BYTE_NBR;
		}



		public int getAUD_BYTE_NBR() {
			return AUD_BYTE_NBR;
		}



		public void setAUD_BYTE_NBR(int aUD_BYTE_NBR) {
			AUD_BYTE_NBR = aUD_BYTE_NBR;
		}



		public String getCREAT_BY_NM() {
			return CREAT_BY_NM;
		}



		public void setCREAT_BY_NM(String cREAT_BY_NM) {
			CREAT_BY_NM = cREAT_BY_NM;
		}



		public String getUPDT_BY_NM() {
			return UPDT_BY_NM;
		}



		public void setUPDT_BY_NM(String uPDT_BY_NM) {
			UPDT_BY_NM = uPDT_BY_NM;
		}



		public String getRMOT_DIR_NM() {
			return RMOT_DIR_NM;
		}



		public void setRMOT_DIR_NM(String rMOT_DIR_NM) {
			RMOT_DIR_NM = rMOT_DIR_NM;
		}



		public String getRMOT_DATA_FL_NM() {
			return RMOT_DATA_FL_NM;
		}



		public void setRMOT_DATA_FL_NM(String rMOT_DATA_FL_NM) {
			RMOT_DATA_FL_NM = rMOT_DATA_FL_NM;
		}



		public String getRMOT_AUD_FL_NM() {
			return RMOT_AUD_FL_NM;
		}



		public void setRMOT_AUD_FL_NM(String rMOT_AUD_FL_NM) {
			RMOT_AUD_FL_NM = rMOT_AUD_FL_NM;
		}



		public String getRMOT_TRIG_FL_NM() {
			return RMOT_TRIG_FL_NM;
		}



		public void setRMOT_TRIG_FL_NM(String rMOT_TRIG_FL_NM) {
			RMOT_TRIG_FL_NM = rMOT_TRIG_FL_NM;
		}




	    public feedsEta(){
	    }


	    public void setPK_MS_TRANSFER_FILE(PK_MS_TRANSFER_FILE id) {
			this.DATA_FL_NM = id.getDATA_FL_NM();
			this.PRTN_ID = id.getPRTN_ID();
		}

		public PK_MS_TRANSFER_FILE getPK_MS_TRANSFER_FILE() {
			return new PK_MS_TRANSFER_FILE(
					DATA_FL_NM,
					PRTN_ID
			);
		}

	  

}

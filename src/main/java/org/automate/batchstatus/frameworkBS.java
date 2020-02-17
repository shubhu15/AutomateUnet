package org.automate.batchstatus;
import java.sql.Timestamp;

public class frameworkBS implements java.io.Serializable {

    public String getBTCH_NBR() {
        return BTCH_NBR;
    }

    public String getBTCH_NM() {
        return BTCH_NM;
    }

    public String getINVOK_ID() {
        return INVOK_ID;
    }

    public Timestamp getSTRT_DTTM() {
        return STRT_DTTM;
    }

    public Timestamp getEND_DTTM() {
        return END_DTTM;
    }

    public String getBTCH_STS_CD() {
        return BTCH_STS_CD;
    }

    public String getRUN_MDE_TXT() {
        return RUN_MDE_TXT;
    }

    public String getCREAT_BY_NM() {
        return CREAT_BY_NM;
    }

    public String getUPDT_BY_NM() {
        return UPDT_BY_NM;
    }

    public String getUPDT_DTTM() {
        return UPDT_DTTM;
    }

    private String BTCH_NBR;

    public void setBTCH_NBR(String BTCH_NBR) {
        this.BTCH_NBR = BTCH_NBR;
    }

    public void setBTCH_NM(String BTCH_NM) {
        this.BTCH_NM = BTCH_NM;
    }

    public void setINVOK_ID(String INVOK_ID) {
        this.INVOK_ID = INVOK_ID;
    }

    public void setSTRT_DTTM(Timestamp STRT_DTTM) {
        this.STRT_DTTM = STRT_DTTM;
    }

    public void setEND_DTTM(Timestamp END_DTTM) {
        this.END_DTTM = END_DTTM;
    }

    public void setBTCH_STS_CD(String BTCH_STS_CD) {
        this.BTCH_STS_CD = BTCH_STS_CD;
    }

    public void setRUN_MDE_TXT(String RUN_MDE_TXT) {
        this.RUN_MDE_TXT = RUN_MDE_TXT;
    }

    public void setCREAT_BY_NM(String CREAT_BY_NM) {
        this.CREAT_BY_NM = CREAT_BY_NM;
    }

    public void setUPDT_BY_NM(String UPDT_BY_NM) {
        this.UPDT_BY_NM = UPDT_BY_NM;
    }

    public void setUPDT_DTTM(String UPDT_DTTM) {
        this.UPDT_DTTM = UPDT_DTTM;
    }

    private String BTCH_NM;
    private String INVOK_ID;
    private Timestamp STRT_DTTM;
    private Timestamp END_DTTM;
    private String BTCH_STS_CD;
    private String RUN_MDE_TXT;



    private String CREAT_BY_NM;


    public Timestamp getCREAT_DTTM() {
        return CREAT_DTTM;
    }

    public void setCREAT_DTTM(Timestamp CREAT_DTTM) {
        this.CREAT_DTTM = CREAT_DTTM;
    }

    private Timestamp CREAT_DTTM;
//    private String END_DTTM;
    private String UPDT_BY_NM;
    private String UPDT_DTTM;

    public frameworkBS(){
    }



    public frameworkBS(String BTCH_NBR, String BTCH_NM, String INVOK_ID, Timestamp STRT_DTTM, Timestamp END_DTTM, String BTCH_STS_CD, String RUN_MDE_TXT, String CREAT_BY_NM, String UPDT_BY_NM, String UPDT_DTTM, Timestamp CREAT_DTTM ){
        this.BTCH_NBR = BTCH_NBR;
        this.BTCH_NM = BTCH_NM;
        this.INVOK_ID = INVOK_ID;
        this.STRT_DTTM = STRT_DTTM;
        this.END_DTTM = END_DTTM;
        this.BTCH_STS_CD = BTCH_STS_CD;
        this.RUN_MDE_TXT = RUN_MDE_TXT;
        this.CREAT_BY_NM = CREAT_BY_NM;
        this.UPDT_BY_NM = UPDT_BY_NM;
        this.UPDT_DTTM = UPDT_DTTM;
        this.CREAT_DTTM = CREAT_DTTM;
    }



}

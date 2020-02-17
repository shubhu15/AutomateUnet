package org.automate.batchstatus;

import java.sql.Timestamp;

public class cycleDate implements java.io.Serializable {

    public cycleDate(){}
    public cycleDate(String PARM_NM, String CATGY_CD, String PARM_DESC, String CURR_VAL, Timestamp UPDT_DTTM, String UPDT_BY_NM, String CREAT_BY_NM, Timestamp CREAT_DTTM) {
        this.PARM_NM = PARM_NM;
        this.CATGY_CD = CATGY_CD;
        this.PARM_DESC = PARM_DESC;
        this.CURR_VAL = CURR_VAL;
        this.UPDT_DTTM = UPDT_DTTM;
        this.UPDT_BY_NM = UPDT_BY_NM;
        this.CREAT_BY_NM = CREAT_BY_NM;
        this.CREAT_DTTM = CREAT_DTTM;
    }

    public String getPARM_NM() {
        return PARM_NM;
    }

    public void setPARM_NM(String PARM_NM) {
        this.PARM_NM = PARM_NM;
    }

    public String getCATGY_CD() {
        return CATGY_CD;
    }

    public void setCATGY_CD(String CATGY_CD) {
        this.CATGY_CD = CATGY_CD;
    }

    public String getPARM_DESC() {
        return PARM_DESC;
    }

    public void setPARM_DESC(String PARM_DESC) {
        this.PARM_DESC = PARM_DESC;
    }

    public String getCURR_VAL() {
        return CURR_VAL;
    }

    public void setCURR_VAL(String CURR_VAL) {
        this.CURR_VAL = CURR_VAL;
    }

    public Timestamp getUPDT_DTTM() {
        return UPDT_DTTM;
    }

    public void setUPDT_DTTM(Timestamp UPDT_DTTM) {
        this.UPDT_DTTM = UPDT_DTTM;
    }

    public String getUPDT_BY_NM() {
        return UPDT_BY_NM;
    }

    public void setUPDT_BY_NM(String UPDT_BY_NM) {
        this.UPDT_BY_NM = UPDT_BY_NM;
    }

    public String getCREAT_BY_NM() {
        return CREAT_BY_NM;
    }

    public void setCREAT_BY_NM(String CREAT_BY_NM) {
        this.CREAT_BY_NM = CREAT_BY_NM;
    }

    public Timestamp getCREAT_DTTM() {
        return CREAT_DTTM;
    }

    public void setCREAT_DTTM(Timestamp CREAT_DTTM) {
        this.CREAT_DTTM = CREAT_DTTM;
    }

    private String PARM_NM;
    private String CATGY_CD;
    private String PARM_DESC;
    private String CURR_VAL;
    private Timestamp UPDT_DTTM;
    private String UPDT_BY_NM;
    private String CREAT_BY_NM;
    private Timestamp CREAT_DTTM;

}

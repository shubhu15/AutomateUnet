package org.automate.batchstatus;

import java.io.Serializable;

public class PK_MS_TRANSFER_FILE implements Serializable {

    private String DATA_FL_NM;

    public PK_MS_TRANSFER_FILE(String DATA_FL_NM, String PRTN_ID) {
        this.DATA_FL_NM = DATA_FL_NM;
        this.PRTN_ID = PRTN_ID;
    }

    public String getDATA_FL_NM() {
        return DATA_FL_NM;
    }

    public void setDATA_FL_NM(String DATA_FL_NM) {
        this.DATA_FL_NM = DATA_FL_NM;
    }

    public String getPRTN_ID() {
        return PRTN_ID;
    }

    public void setPRTN_ID(String PRTN_ID) {
        this.PRTN_ID = PRTN_ID;
    }

    private String PRTN_ID;

    @Override
    public boolean equals(Object arg0) {
        if(arg0 == null) return false;
        if(!(arg0 instanceof PK_MS_TRANSFER_FILE)) return false;
        PK_MS_TRANSFER_FILE arg1 = (PK_MS_TRANSFER_FILE) arg0;
        return (this.PRTN_ID == arg1.getPRTN_ID()) &&
                (this.DATA_FL_NM == arg1.getDATA_FL_NM());
    }

    @Override
    public int hashCode() {
        int hsCode;
        hsCode = PRTN_ID.hashCode();
        hsCode = 19 * hsCode+ DATA_FL_NM.hashCode();
        return hsCode;
    }

    private PK_MS_TRANSFER_FILE() {
    }
}

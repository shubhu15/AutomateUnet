package org.automate.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.automate.batchstatus.cycleDate;
import org.automate.batchstatus.feedsEta;
import org.automate.batchstatus.frameworkBS;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UNETMemberETA {
//    private static Logger logger = LogManager.getLogger(TestTimeDiff.class);

	private static Map<String, Map<String, String>> map =new HashMap<>();
	private static Map<String, Map<String, String>> map_prev =new HashMap<>();
	private static Map<String, Map<String, String>> map_feeds =new HashMap<>();

    public static Timestamp start_IntakeFileLoading = null;

    public static Timestamp end_IntakeFileLoading;
    public static Timestamp start_IntakeFileLoading_aWeekAgo = null;
    private static int flag_IntakeFileLoading =1;

    public static Timestamp end_DBIProcessing = null;
    private static int flag_DBIProcessing=1;

    public static Timestamp end_SchedulingOfSub = null;
    private static int flag_SchedulingOfSub=1;

    public static Timestamp end_Harvesting = null;
    private static int flag_Harvesting=1;

    public static Timestamp end_ConsolidationNOverpayment = null;
    private Timestamp end_Payment = null;
    private static int flag_ConsolidationNOverpayment=1;

    public static Timestamp end_CorePayment = null;
    private static int flag_CorePayment=1;

    public static Timestamp end_BenefitHeaderProcessing = null;
    private static int flag_BenefitHeaderProcessing=1;

    public static Timestamp end_MemberEhealthFeedbackFile = null;
    private static int flag_MemberEhealthFeedbackFile=1;

    public static Timestamp end_PostPayExtract = null;
    private static int flag_PostPayExtract=1;
    
    public Map<String, Map<String, String>>  getFeedsEta(SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            //Query for cycle date
//            List<cycleDate> list_n = session.createQuery("from cycleDate where PARM_NM ='CycleDate'").list();
//            Timestamp cycledate_start = list_n.get(0).getUPDT_DTTM();
//            String  cycle_dt = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(cycledate_start.getTime()));
//            System.out.println(cycle_dt);
//            cycle_dt="21-Jan-20 02.08.08 PM";
//            logger.info("SAVING LOGS FOR " + cycle_dt + "at " +curr_date);
            String  end = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(UNETMemberETA.getEnd_CorePayment().getTime()));

            //Query for PROVIDER-Feeds ETA
            List<feedsEta> list_main = session.createQuery("from feedsEta where fl_id in (2921, 2050,2051,2919,2951,2916)"
                    + " and CREAT_DTTM >='"
                    + end+"' order by end_dttm").list();
            System.out.println("transaction_6 for MEMBER FEEDS started");
            map_feeds =printTimeDiffFeeds(list_main, session);

            Collection<String> value_list = map_feeds.get("eta").values();
            Collection<String> key_list = map_feeds.get("eta").keySet();

            Iterator values = value_list.iterator();
            Iterator keys = key_list.iterator();

//            Collection<String> list_2 = map_feeds.get("c");
            System.out.println("not completed: ETA");
            while (values.hasNext()){
                System.out.println(keys.next()+  " : "+ values.next());
            }


            values = map_feeds.get("c").values().iterator();
            keys = map_feeds.get("c").keySet().iterator();
            System.out.println("Completed: End Time");
            while (values.hasNext()){
                System.out.println(keys.next()+  " : "+ values.next());
            }
            tx.commit();
        }
        catch(HibernateException ex){
            if (tx != null) {
                tx.rollback();
            }
            ex.printStackTrace(System.err);
        } finally{
            session.close();
        }
        return map_feeds;

    }

    private Map<String, Map<String, String>> printTimeDiffFeeds(List<feedsEta> list, Session session) {

        Map<String, Map<String, String>> map_feeds = new HashMap<>();
        Map<String, String> Map_eta = new HashMap<>();
        Map<String, String> Map_noeta = new HashMap<>();


        List<feedsEta> l1;


//        if(list.size()==0){
//            System.out.println("Files not loaded yet! unable to calculate ETA!");
////            logger.warn("Files not loaded yet! unable to calculate ETA!");
//            return listMap;
//
//        }
        int flag_GEFT = 0;
        int flag_EOB = 0;
        int flag_Ovpay = 0;
        int flag_Chkrc = 0;
        int flag_UCAS = 0;
        int flag_TOPS = 0;


        for (int i = 0; i <= list.size(); i++) {
            System.out.println(i);

            String eta;
            String noEta;

            if(i== list.size()&& i!=0){
                break;
            }

            if (list.size()==0 || (list.get(i).getFL_ID() != 2921 && flag_Ovpay == 0 && list.size()<1)) {
                flag_Ovpay = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2921, dateALastWeek(UNETMemberETA.getEnd_CorePayment()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2921 " + eta);
                Map_eta.put("OVPAY", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2921) {
                flag_Ovpay = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("OVPAY", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 2050 && flag_UCAS == 0 && list.size()<3)) {
                flag_UCAS = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2050, dateALastWeek(UNETMemberETA.getEnd_CorePayment()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2050 " + eta);
                Map_eta.put("UCAS", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2050) {
                flag_UCAS = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("UCAS", noEta);
            }
            if ( list.size()==0 || (list.get(i).getFL_ID() != 2051 && flag_GEFT == 0 && list.size()<3)) {
                flag_GEFT = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2051, dateALastWeek(UNETMemberETA.getEnd_CorePayment()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2051 " + eta);
                Map_eta.put("GEFT", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2051) {
                flag_GEFT = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("GEFT", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 2919 && flag_TOPS == 0 && list.size()<4)) {
                flag_TOPS = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2919, dateALastWeek(UNETMemberETA.getEnd_CorePayment()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2919 " + eta);
                Map_eta.put("TOPS", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2919) {
                flag_TOPS = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("TOPS", noEta);
            }
            if ( list.size()==0 || (list.get(i).getFL_ID() != 2951 && flag_EOB == 0 && list.size()<6)) {
                flag_EOB = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2951, dateALastWeek(UNETMemberETA.getEnd_CorePayment()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2951 " + eta);
                Map_eta.put("EOB", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2951) {
                flag_EOB = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("EOB", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 2916 && flag_Chkrc == 0 && list.size()<7)) {
                flag_Chkrc = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2916, dateALastWeek(UNETMemberETA.getEnd_CorePayment()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2916 " + eta);
                Map_eta.put("CHKRC", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2916) {
                flag_Chkrc = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("CHKRC", noEta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2962) {
            	noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("EHEALTH", noEta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2801) {
            	noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("CAMSRX", noEta);
            }

            //ehealth and camsrx default etas

        }

//        Map_eta.put("EHEALTH", value)
        map_feeds.put("eta", Map_eta);
        map_feeds.put("c",Map_noeta);

        return map_feeds;

    }
    
    private List<feedsEta> aWeekBackObjectFeeds(int i, String dateALastWeek, Session session) {
        Query q2 = session.createQuery("from feedsEta where FL_ID = "+ i+" and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + dateALastWeek + "' order by CREAT_DTTM desc");
        if(q2.list().size()<1){
            String day = dateALastWeek.substring(8,10);
            String dateAweekback2 = dateALastWeek.replaceAll(day+"$",Integer.toString(Integer.valueOf(day)+1));
            return session.createQuery("from feedsEta where FL_ID = "+ i+" and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + dateAweekback2 + "' order by CREAT_DTTM").list();
        }
        else{
            return q2.list();}
    }
    public Timestamp getCurrentEta(Timestamp a_week_back_eta){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(a_week_back_eta.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 7);

        return new Timestamp(cal.getTime().getTime());
    }

    public Map<String, Map<String, String>> getPreviousQuery(String prev_date, SessionFactory sessionFactory){


        //prev_date = 2020-01-20
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            List<cycleDate> list_n = session.createQuery("from cycleDate where PARM_NM ='CycleDate'").list();
            Timestamp cycledate_start = list_n.get(0).getUPDT_DTTM();
            String  cycle_dt = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(cycledate_start.getTime()));
//            cycle_dt="24-Jan-20 02.08.08 AM";

            List<frameworkBS> list_prev = session.createQuery("from frameworkBS where INVOK_ID = 'NONE' and BTCH_NM ='seqMbrEmptyStgBkt'" +
                    " and to_char(CREAT_DTTM, 'yyyy-MM-dd') = '"+prev_date+"' order by CREAT_DTTM").list();
            Timestamp start_prev = list_prev.get(0).getSTRT_DTTM();
            String  prev_start_date = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(start_prev.getTime()));

            List list_main = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','PE','401','UNET') and BTCH_NM in ('seqMbrEmptyStgBkt'," +
                    "'seqMbrITKExtrc','seqProcessTopsDBI','seqOPAGenDBITopsFeedback','seqMbrSchedule','seqMbrEmptyRlseBkt'," +
                    "'seqMbrExtLoadHarvesting','seqMbrPayment1','seqMbrOTSUnlockUnused','seqMbrOFSPmtData','seqBenHdrIDMbrGenReqFile'," +
                    "'seqBenHdrIDMbrLoadRespTbl','seqMbrEOBFile','seqeHealthMbrCreateFeedBckFiles','seqMbr03CreateFICSFile') and " +
                    "CREAT_DTTM between '"+prev_start_date+"' and '"+ cycle_dt+"' order by CREAT_DTTM").list();
            System.out.println("transaction_2 for UNET-MEMBER started for previous date");
            map_prev =printTimeDiff_member(list_main, session);

            Collection<String> value_list = map_prev.get("eta").values();
            Collection<String> key_list = map_prev.get("eta").keySet();

            Iterator<String> values = value_list.iterator();
            Iterator<String> keys = key_list.iterator();

//            Collection<String> list_2 = map_feeds.get("c");
            System.out.println("not completed: ETA");
            while (values.hasNext()){
                System.out.println(keys.next()+  " : "+ values.next());
            }


            values = map_prev.get("c").values().iterator();
            keys = map_prev.get("c").keySet().iterator();
            System.out.println("Completed: End Time");
            while (values.hasNext()){
                System.out.println(keys.next()+  " : "+ values.next());
            }


            tx.commit();
                   }
        catch(HibernateException ex){
            if (tx != null) {
                tx.rollback();
            }
            ex.printStackTrace(System.err);
        } finally{
            session.close();
        }
		return map_prev;
    }

    public Map<String, Map<String, String>> getQuery( SessionFactory sessionFactory){

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            //Query for cycle date
            List<cycleDate> list_n = session.createQuery("from cycleDate where PARM_NM ='CycleDate'").list();
            Timestamp cycledate_start = list_n.get(0).getUPDT_DTTM();
            String  cycle_dt = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(cycledate_start.getTime()));
//            System.out.println(cycle_dt+ " at " +curr_date);
//            cycle_dt="21-Jan-20 02.08.08 PM";
//            cycle_dt="24-Jan-20 02.08.08 AM";
//            logger.info("SAVING LOGS FOR " + cycle_dt + " at " +curr_date);


            //Query for UNET-MEMBER
            List list = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','PE','401','UNET') and BTCH_NM in ('seqMbrEmptyStgBkt'," +
                    "'seqMbrITKExtrc','seqProcessTopsDBI','seqOPAGenDBITopsFeedback','seqMbrSchedule','seqMbrEmptyRlseBkt'," +
                    "'seqMbrExtLoadHarvesting','seqMbrPayment1','seqMbrOTSUnlockUnused','seqMbrOFSPmtData','seqBenHdrIDMbrGenReqFile'," +
                    "'seqBenHdrIDMbrLoadRespTbl','seqMbrEOBFile','seqeHealthMbrCreateFeedBckFiles','seqMbr03CreateFICSFile') and " +
                            "CREAT_DTTM >= '"+ cycle_dt+"' order by CREAT_DTTM").list();
            System.out.println("transaction_2 for UNET-MEMBER started");
            map = printTimeDiff_member(list, session);
            Collection<String> value_list = map.get("eta").values();
            Collection<String> key_list = map.get("eta").keySet();

            Iterator<String> values = value_list.iterator();
            Iterator<String> keys = key_list.iterator();

//            Collection<String> list_2 = map_feeds.get("c");
            System.out.println("not completed: ETA");
            while (values.hasNext()){
                System.out.println(keys.next()+  " : "+ values.next());
            }


            values = map.get("c").values().iterator();
            keys = map.get("c").keySet().iterator();
            System.out.println("Completed: End Time");
            while (values.hasNext()){
                System.out.println(keys.next()+  " : "+ values.next());
            }

            tx.commit();
        }
        catch(HibernateException ex){
            if (tx != null) {
                tx.rollback();
            }
            ex.printStackTrace(System.err);
        } finally{
            session.close();
        }
        return map;

    }

    private Map<String, Map<String, String>> printTimeDiff_member(List<frameworkBS> list, Session session) {
    	Map<String,Map<String, String>> map =new HashMap<>();
    	Map<String, String> map_eta = new HashMap<>();
    	Map<String, String> map_noEta = new HashMap<>();
    	
    	
    	List<frameworkBS> l1;
        List<frameworkBS> l2;

        
        if(list.size()==0){
            System.out.println("Files not loaded yet! unable to calculate ETA!");
//            logger.warn("Files not loaded yet! unable to calculate ETA!");
            return map;
        }

        for(int i=0; i<list.size(); i++) {
            System.out.println(i);

            String eta;
            String noEta;
            
            if (i == 0 && list.get(i).getBTCH_NM().equals("seqMbrEmptyStgBkt")) {
                setStart_IntakeFileLoading(list.get(i).getSTRT_DTTM());
                l1 = aWeekBackObject("NONE", "seqMbrEmptyStgBkt", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                setStart_IntakeFileLoading_aWeekAgo(l1.get(0).getSTRT_DTTM());
            }


            if (!list.get(i).getBTCH_NM().equals("seqMbrITKExtrc") && flag_IntakeFileLoading == 1 && list.size() < 2) {
                if (UNETMemberETA.getEnd_IntakeFileLoading() == null) {
                    flag_IntakeFileLoading = 0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqMbrITKExtrc", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), getStart_IntakeFileLoading_aWeekAgo());
                    setEnd_IntakeFileLoading(new Timestamp(sf+UNETMemberETA.getStart_IntakeFileLoading().getTime()));
                    eta = addExtraMargin(getEnd_IntakeFileLoading(), 1800000);
//                    logger.info(" Intake File Loading on plan with ETA = " + eta);
                    System.out.println("the end time of Intake File Loading " + eta/* added to end time of prepreprocessor*/);
                    map_eta.put("IntakeFileLoading", eta);
                }
            }
            ////////////////////////
            if (list.get(i).getBTCH_NM().equals("seqMbrITKExtrc")) {
                flag_IntakeFileLoading = 0;
                if (list.get(i).getBTCH_STS_CD().equals("C")) {
                    setEnd_IntakeFileLoading(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_IntakeFileLoading());
                    System.out.println("the end time of Intake File Loading " + noEta/* added to end time of prepreprocessor*/);
                    map_noEta.put("IntakeFileLoading", noEta);
                } else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqMbrITKExtrc", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), start_IntakeFileLoading_aWeekAgo);
                    setEnd_IntakeFileLoading(new Timestamp(sf+UNETMemberETA.getStart_IntakeFileLoading().getTime()));
                    eta = addExtraMargin(getEnd_IntakeFileLoading(), 1800000);
//                    logger.info(" Intake File Loading in progress with ETA = " + eta);
                    map_eta.put("IntakeFileLoading", eta);
                    System.out.println("the end time of Intake File Loading " + eta/* added to end time of prepreprocessor*/);
                }

            }

            if(!list.get(i).getBTCH_NM().equals("seqProcessTopsDBI") && flag_DBIProcessing==1 && list.size()<3){
                if(getEnd_IntakeFileLoading()!=null && getEnd_DBIProcessing() == null){
                    flag_DBIProcessing =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPAGenDBITopsFeedback", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_DBIProcessing( new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 120000));
                    eta = addExtraMargin(getEnd_DBIProcessing(), 1800000);
//                    logger.info(" DBIProcessing on plan with ETA = "+ eta);
                    System.out.println("the end time of DBIProcessing "+eta /* added to end time of Payment Processing*/);
                    map_eta.put("DBIProcessing", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAGenDBITopsFeedback") ){
                flag_DBIProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    setEnd_DBIProcessing(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_DBIProcessing());
                    System.out.println("the end time of DBI processing " + noEta/* added to end time of prepreprocessor*/);
                    map_noEta.put("DBIProcessing", noEta);
                }
            }

            if (!list.get(i).getBTCH_NM().equals("seqMbrSchedule") && flag_SchedulingOfSub == 1 && list.size() <=5) {
                if (getEnd_DBIProcessing()!=null  && UNETMemberETA.getEnd_SchedulingOfSub() == null) {
                    flag_SchedulingOfSub = 0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqMbrSchedule", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_SchedulingOfSub(new Timestamp(sf+UNETMemberETA.getEnd_DBIProcessing().getTime()));
                    eta=addExtraMargin(getEnd_SchedulingOfSub(), 1800000);
                    map_eta.put("SchedulingOfSub", eta);
//                    logger.info(" Scheduling Of Sub on plan with ETA = " + eta);
                    System.out.println("the end time of Scheduling Of Sub " + eta /* added to end time of prepreprocessor*/);
                }
            }

            //////////////////
            if (list.get(i).getBTCH_NM().equals("seqMbrSchedule")) {
            	flag_SchedulingOfSub = 0;
                if (list.get(i).getBTCH_STS_CD().equals("C")) {
                    setEnd_SchedulingOfSub(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_SchedulingOfSub());
                    System.out.println("the end time of scheduling of sub" + noEta/* added to end time of prepreprocessor*/);
                    map_noEta.put("SchedulingOfSub", noEta);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqMbrEmptyRlseBkt") && flag_Harvesting==1 && list.size()<=7){
                if(getEnd_SchedulingOfSub()!=null && getEnd_Harvesting() == null){
                    flag_Harvesting =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqMbrEmptyRlseBkt", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("PE", "seqMbrExtLoadHarvesting", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_Harvesting( new Timestamp(getEnd_SchedulingOfSub().getTime()+sf));
                    eta=addExtraMargin(getEnd_Harvesting(), 1800000);
                    map_eta.put("Harvesting", eta);
//                    logger.info(" Harvesting on plan with ETA = "+ eta);
                    System.out.println("the end time of Harvesting "+ eta/* added to end time of Payment Processing*/);
                }
            }
            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrExtLoadHarvesting") ){
            	flag_Harvesting =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                	setEnd_Harvesting(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_Harvesting());
                    System.out.println("the end time of Harvesting" + noEta/* added to end time of prepreprocessor*/);
                    map_noEta.put("Harvesting", noEta);
                    }
            }


            if(!list.get(i).getBTCH_NM().equals("seqMbrPayment1") && flag_ConsolidationNOverpayment==1 && list.size()<9){
                if(getEnd_Harvesting()!=null && getEnd_ConsolidationNOverpayment() == null){
                    flag_ConsolidationNOverpayment =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqMbrPayment1", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("NONE", "seqMbrOTSUnlockUnused", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    end_Payment = new Timestamp(getEnd_Harvesting().getTime()+300000+timeDiffCalculate(l1.get(0).getEND_DTTM(),l1.get(0).getSTRT_DTTM()));
                    setEnd_ConsolidationNOverpayment( new Timestamp(getEnd_Harvesting().getTime()+sf+300000));
                    eta =addExtraMargin(getEnd_ConsolidationNOverpayment(), 1800000);
                    map_eta.put("ConsolidationNOverpayment", eta);
//                    logger.info(" Consolidation N Overpayment on plan with ETA = "+ eta);
                    System.out.println("the end time of Consolidation N Overpayment "+ eta /* added to end time of Payment Processing*/);
                }
            }

            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrOTSUnlockUnused") ){
            	flag_ConsolidationNOverpayment =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                	setEnd_ConsolidationNOverpayment(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_ConsolidationNOverpayment());
                    System.out.println("the end time of consolidation and overpayment" + noEta/* added to end time of prepreprocessor*/);
                    map_noEta.put("ConsolidationNOverpayment", noEta);
                    }
            }

            if(!list.get(i).getBTCH_NM().equals("seqBenHdrIDMbrGenReqFile") && flag_BenefitHeaderProcessing==1 && list.size()<10){
                if(getEnd_ConsolidationNOverpayment()!=null && getEnd_BenefitHeaderProcessing() == null){
                    flag_BenefitHeaderProcessing =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqBenHdrIDMbrGenReqFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("NONE", "seqBenHdrIDMbrLoadRespTbl", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_BenefitHeaderProcessing( new Timestamp(end_Payment.getTime()+sf+180000));
                    eta =addExtraMargin(getEnd_BenefitHeaderProcessing(), 1800000);
                    map_eta.put("BenefitHeaderProcessing", eta);
//                    logger.info(" Benefit Header Processing on plan with ETA = "+ eta);
                    System.out.println("the end time of Benefit Header Processing "+ eta /* added to end time of Payment Processing*/);
                }
            }
            /////////////
            if(list.get(i).getBTCH_NM().equals("seqBenHdrIDMbrLoadRespTbl") ){
            	flag_BenefitHeaderProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                	setEnd_BenefitHeaderProcessing(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_BenefitHeaderProcessing());
                    System.out.println("the end time of benefit header processing" + noEta/* added to end time of prepreprocessor*/);
                    map_noEta.put("BenefitHeaderProcessing", noEta);}
            }


            if(!list.get(i).getBTCH_NM().equals("seqMbrOFSPmtData") && flag_CorePayment==1 && list.size()<12){
                if(getEnd_ConsolidationNOverpayment()!=null && getEnd_CorePayment() == null){
                    flag_CorePayment =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqMbrOFSPmtData", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_CorePayment( new Timestamp(getEnd_ConsolidationNOverpayment().getTime()+sf));
                    eta =addExtraMargin(getEnd_CorePayment(), 1800000);
                    map_eta.put("CorePayment", eta);
//                    logger.info(" Core Payment on plan with ETA = "+ eta);
                    System.out.println("the end time of Core Payment "+ eta /* added to end time of Payment Processing*/);
                }
            }
            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrOFSPmtData") ){
            	flag_CorePayment =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                	setEnd_CorePayment(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_CorePayment());
                    System.out.println("the end time of core payment" + noEta/* added to end time of prepreprocessor*/);
                    map_noEta.put("CorePayment", noEta);}
            }

            if(!list.get(i).getBTCH_NM().equals("seqMbrEOBFile") && flag_MemberEhealthFeedbackFile==1 && list.size()<14){
                if(getEnd_BenefitHeaderProcessing()!=null && getEnd_MemberEhealthFeedbackFile() == null){
                    flag_MemberEhealthFeedbackFile =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE","seqMbrEOBFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("NONE", "seqeHealthMbrCreateFeedBckFiles", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_MemberEhealthFeedbackFile( new Timestamp(getEnd_BenefitHeaderProcessing().getTime()+sf+180000));
                    eta= addExtraMargin(getEnd_MemberEhealthFeedbackFile(), 1800000);
                    map_eta.put("MemberEhealthFeedbackFile", eta);
//                    logger.info(" Member Ehealth FeedbackFile on plan with ETA = "+ eta);
                    System.out.println("the end time of Member Ehealth FeedbackFile "+ eta /* added to end time of Payment Processing*/);
                }
            }
            ///////////// TODOOOOOOOO
            if(list.get(i).getBTCH_NM().equals("seqMbrPayment1") && !list.get(i).getBTCH_STS_CD().equals("C") && list.size()<=5) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqMbrOTSUnlockUnused", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                setEnd_DBIProcessing(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 720));
//                logger.info(" DBIProcessing in progress with ETA = "+ addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000));
                System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqeHealthMbrCreateFeedBckFiles") ){
            	flag_MemberEhealthFeedbackFile =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                	setEnd_MemberEhealthFeedbackFile(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_MemberEhealthFeedbackFile());
                    System.out.println("the end time of MemberEhealthFeedbackFile " + noEta/* added to end time of prepreprocessor*/);
                    map_noEta.put("MemberEhealthFeedbackFile", noEta);}
            }

            if(!list.get(i).getBTCH_NM().equals("seqMbr03CreateFICSFile") && flag_PostPayExtract==1 && list.size()<15){
                if(getEnd_MemberEhealthFeedbackFile()!=null && getEnd_PostPayExtract() == null){
                    flag_PostPayExtract =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("401","seqMbr03CreateFICSFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    setEnd_PostPayExtract( new Timestamp(getEnd_MemberEhealthFeedbackFile().getTime()+sf));
                    eta = addExtraMargin(getEnd_PostPayExtract(), 1800000);
//                    logger.info(" Post Pay Extract on plan with ETA = "+ eta);
                    System.out.println("the end time of  Post Pay Extract "+ eta /* added to end time of Payment Processing*/);
                    map_eta.put("PostPayExtract", eta);
                }
            }
            /////////////
            if(list.get(i).getBTCH_NM().equals("seqMbrPayment1") && !list.get(i).getBTCH_STS_CD().equals("C") && list.size()<=5) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqProcessTopsDBI", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqMbrOTSUnlockUnused", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                setEnd_DBIProcessing(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf + 720));
//                logger.info(" DBIProcessing in progress with ETA = "+ addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000));
                System.out.println("the end time of DBIProcessing "+ addExtraMargin(getEnd_DBIProcessing(), 1800000) /* added to end time of Payment Processing*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqMbr03CreateFICSFile") ){
            	flag_PostPayExtract =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                	setEnd_PostPayExtract(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_PostPayExtract());
                    System.out.println("the end time of PostPayExtract " + noEta/* added to end time of prepreprocessor*/);
                    map_noEta.put("PostPayExtract", noEta);}
            }
        }
        map.put("eta", map_eta);
        map.put("c", map_noEta);
        return map;
    }


    public static Timestamp getStart_IntakeFileLoading() {
        return start_IntakeFileLoading;
    }

    public static void setStart_IntakeFileLoading(Timestamp start_IntakeFileLoading) {
        UNETMemberETA.start_IntakeFileLoading = start_IntakeFileLoading;
    }

    public static Timestamp getEnd_IntakeFileLoading() {
        return end_IntakeFileLoading;
    }

    public static void setEnd_IntakeFileLoading(Timestamp end_IntakeFileLoading) {
        UNETMemberETA.end_IntakeFileLoading = end_IntakeFileLoading;
    }

    public static Timestamp getStart_IntakeFileLoading_aWeekAgo() {
        return start_IntakeFileLoading_aWeekAgo;
    }

    public static void setStart_IntakeFileLoading_aWeekAgo(Timestamp start_IntakeFileLoading_aWeekAgo) {
        UNETMemberETA.start_IntakeFileLoading_aWeekAgo = start_IntakeFileLoading_aWeekAgo;
    }

    public static Timestamp getEnd_DBIProcessing() {
        return end_DBIProcessing;
    }

    public static void setEnd_DBIProcessing(Timestamp end_DBIProcessing) {
        UNETMemberETA.end_DBIProcessing = end_DBIProcessing;
    }

    public static Timestamp getEnd_SchedulingOfSub() {
        return end_SchedulingOfSub;
    }

    public static void setEnd_SchedulingOfSub(Timestamp end_SchedulingOfSub) {
        UNETMemberETA.end_SchedulingOfSub = end_SchedulingOfSub;
    }

    public static Timestamp getEnd_Harvesting() {
        return end_Harvesting;
    }

    public static void setEnd_Harvesting(Timestamp end_Harvesting) {
        UNETMemberETA.end_Harvesting = end_Harvesting;
    }

    public static Timestamp getEnd_ConsolidationNOverpayment() {
        return end_ConsolidationNOverpayment;
    }

    public static void setEnd_ConsolidationNOverpayment(Timestamp end_ConsolidationNOverpayment) {
        UNETMemberETA.end_ConsolidationNOverpayment = end_ConsolidationNOverpayment;
    }

    public static Timestamp getEnd_CorePayment() {
        return end_CorePayment;
    }

    public static void setEnd_CorePayment(Timestamp end_CorePayment) {
        UNETMemberETA.end_CorePayment = end_CorePayment;
    }

    public static Timestamp getEnd_BenefitHeaderProcessing() {
        return end_BenefitHeaderProcessing;
    }

    public static void setEnd_BenefitHeaderProcessing(Timestamp end_BenefitHeaderProcessing) {
        UNETMemberETA.end_BenefitHeaderProcessing = end_BenefitHeaderProcessing;
    }

    public static Timestamp getEnd_MemberEhealthFeedbackFile() {
        return end_MemberEhealthFeedbackFile;
    }

    public static void setEnd_MemberEhealthFeedbackFile(Timestamp end_MemberEhealthFeedbackFile) {
        UNETMemberETA.end_MemberEhealthFeedbackFile = end_MemberEhealthFeedbackFile;
    }

    public static Timestamp getEnd_PostPayExtract() {
        return end_PostPayExtract;
    }

    public static void setEnd_PostPayExtract(Timestamp end_PostPayExtract) {
        UNETMemberETA.end_PostPayExtract = end_PostPayExtract;
    }
    public String getETAinString(Timestamp tm){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/YY hh:mm a");
        String df= simpleDateFormat.format(tm); //adding 30 mins to extend the ETA
        return df.replaceFirst(" ", "\n") +" CST";
    }

    public String addExtraMargin(Timestamp tm , long extra){
        return getETAinString(new Timestamp(tm.getTime()+extra));
    }

    public String dateALastWeek(Timestamp current_date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(current_date.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -7);

        Timestamp tm = new Timestamp(cal.getTime().getTime());
        String s = tm.toString().substring(0, 10);
        return s;
    }

    public List<frameworkBS> aWeekBackObject(String INVOK_ID, String  BTCH_NM, String dateAweekBack,Session session){
        Query q2 = session.createQuery("from frameworkBS where INVOK_ID = '"+ INVOK_ID+"' and BTCH_NM = '"+ BTCH_NM+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + dateAweekBack + "' and BTCH_STS_CD = 'C' order by CREAT_DTTM desc");
        if(q2.list().size()<1){
            String day = dateAweekBack.substring(8,10);
            String dateAweekback2 = dateAweekBack.replaceAll(day+"$",Integer.toString(Integer.valueOf(day)+1));
            System.out.println(BTCH_NM+ "  : "+dateAweekback2 );
            return session.createQuery("from frameworkBS where INVOK_ID = '"+ INVOK_ID+"' and BTCH_NM = '"+ BTCH_NM+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + dateAweekback2 + "' and BTCH_STS_CD = 'C' order by CREAT_DTTM").list();
        }
        else{
            return q2.list();}

    }

    public long timeDiffCalculate(Timestamp t_end, Timestamp t_start){
        long diff = t_end.getTime() - t_start.getTime();
        return diff;

    }

}

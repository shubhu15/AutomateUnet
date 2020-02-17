package org.automate.database;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.automate.batchstatus.cycleDate;
import org.automate.batchstatus.feedsEta;
import org.automate.batchstatus.frameworkBS;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UNETProviderETA {
	
	private static Map<String, Map<String, String>> map =new HashMap<>();
	private static Map<String, Map<String, String>> map_prev =new HashMap<>();
	private static Map<String, Map<String, String>> map_feeds =new HashMap<>();

//    private static Logger logger = LogManager.getLogger(TestTimeDiff.class);

    //    Timestamp curr_Timestamp = new Timestamp(System.currentTimeMillis());
    public static Timestamp start_PrePreProcessor = null;

    public static Timestamp end_PrePreProcessor;
    public static Timestamp start_PrePreProcessor_aWeekAgo = null;
    private static int flag_PrePreProcessor=1;

    public static Timestamp end_PreProcessor = null;
    private static int flag_PreProcessor=1;

    public static Timestamp end_Intake = null;
    private static int flag_Intake=1;

    public static Timestamp end_Scheduling = null;
    private static int flag_Scheduling=1;

    public static Timestamp end_ReleaseNConsolidation = null;
    private static int flag_ReleaseNConsolidation=1;

    public static Timestamp end_PaymentProcessing = null;
    private static int flag_PaymentProcessing=1;

    public static Timestamp end_PostPaymentExtract = null;
    private static int flag_PostPaymentExtract=1;

    public static Timestamp end_835EPS_B2B = null;
    private static int flag_835EPS_B2B=1;

    public static Timestamp end_EPSFundingFile = null;
    private static int flag_EPSFundingFile=1;

    public static Timestamp end_FundingReport = null;
    private static int flag_FundingReport=1;

    public static Timestamp end_ProviderPRA = null;
    private static int flag_ProviderPRA=1;

    private static int flag_ExcelUpdate = 1;
//    
    public Map<String, Map<String, String>> getFeedsEta(SessionFactory sessionFactory){
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

            String  end = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(UNETMemberETA.getEnd_DBIProcessing().getTime()));

            //Query for PROVIDER-Feeds ETA
            List<feedsEta> list_main = session.createQuery("from feedsEta where fl_id in (2407,905,526,5032,5011,"
                    + "5013,2117,911,2150,2128,1008) and CREAT_DTTM >='"
                    +end +"' order by end_dttm").list();
            System.out.println("transaction_5 for PROVIDER FEEDS started");
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
        int flag_DELTRX = 0;
        int flag_SAM = 0;
        int flag_Treasury = 0;
        int flag_Ovpay = 0;
        int flag_Chkrc = 0;
        int flag_UCASFeed = 0;
        int flag_TOPSFeed = 0;
        int flag_CAMSFeed = 0;
        int flag_B2B835 = 0;
        int flag_EPS835 = 0;
        int flag_PRA = 0;

        for (int i = 0; i <= list.size(); i++) {
            System.out.println(i);

            String eta;
            String noEta;

            if(i== list.size() && i!=0){
                break;
            }
            if ( list.size()==0 || (list.get(i).getFL_ID() != 2407 && flag_DELTRX == 0)) {
                flag_DELTRX = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2407, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2407 " + eta);
                Map_eta.put("DELTRX", eta);
            }
            else if (list.size()>0 && list.get(i).getFL_ID() == 2407) {
                flag_DELTRX = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("DELTRX", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 905 && flag_SAM == 0 && list.size()<2)) {
                flag_SAM = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(905, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 905 " + eta);

                Map_eta.put("SAM", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 905) {
                flag_SAM = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("SAM", noEta);
            }
            if ( list.size()==0 ||(list.get(i).getFL_ID() != 5032 && flag_Ovpay == 0 && list.size()<3)){
                flag_Ovpay = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(5032, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 5032 " + eta);
                Map_eta.put("OVPAY", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 5032) {
                flag_Ovpay = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("OVPAY", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 2128 && flag_Chkrc == 0 && list.size()<4)) {
                flag_Chkrc = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2128, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2128 " + eta);
                Map_eta.put("CHKRC", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2128) {
                flag_Chkrc = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("CHKRC", noEta);
            }

            if ( list.size()==0 ||(list.get(i).getFL_ID() != 526 && flag_Treasury == 0 && list.size()<5)) {
                flag_Treasury = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(526, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 526 " + eta);
                Map_eta.put("Treasury", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 526) {
                flag_Treasury = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("Treasury", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 2150 && flag_UCASFeed == 0 && list.size()<6) ){
                flag_UCASFeed = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2150, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2150 " + eta);
                Map_eta.put("UCAS", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2150) {
                flag_UCASFeed = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("UCAS", noEta);
            }

            if (list.size()==0 || (list.get(i).getFL_ID() != 2117 && flag_TOPSFeed == 0 && list.size()<7)) {
                flag_TOPSFeed = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(2117, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 2117 " + eta);
                Map_eta.put("TOPS", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 2117) {
                flag_TOPSFeed = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("TOPS", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 911 && flag_CAMSFeed == 0 && list.size()<8)) {
                flag_CAMSFeed = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(911, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 911 " + eta);
                Map_eta.put("CAMS", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 911) {
                flag_CAMSFeed = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("CAMS", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 5013 && flag_B2B835 == 0 && list.size()<9)) {
                flag_B2B835 = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(5013, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 5013 " + eta);
                Map_eta.put("B2B-835", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 5013) {
                flag_B2B835 = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("B2B-835", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 5011 && flag_EPS835 == 0 && list.size()<10)) {
                flag_EPS835 = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(5011, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 5011 " + eta);
                Map_eta.put("EPS-835", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 5011) {
                flag_EPS835 = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("EPS-835", noEta);
            }

            if ( list.size()==0 || (list.get(i).getFL_ID() != 1008 && flag_PRA == 0 && list.size()<11)) {
                flag_PRA = 1;
                System.out.println("on plan");
                l1 = aWeekBackObjectFeeds(1008, dateALastWeek(UNETMemberETA.getEnd_DBIProcessing()), session);
                Timestamp tb = l1.get(0).getEND_DTTM();
                eta = addExtraMargin(getCurrentEta(tb), 1800000);
                System.out.println("the end time of 1008 " + eta);
                Map_eta.put("PRA", eta);
            }
            if (list.size()>0 && list.get(i).getFL_ID() == 1008) {
                flag_PRA = 1;
                noEta = getETAinString(list.get(i).getEND_DTTM());
                Map_noeta.put("PRA", noEta);
            }


        }

        map_feeds.put("eta", Map_eta);
        map_feeds.put("c",Map_noeta);

        return map_feeds;

    }

    private List<feedsEta> aWeekBackObjectFeeds(int i, String dateALastWeek, Session session) {
        List<feedsEta> l = session.createQuery("from feedsEta where FL_ID = "+ i+" and to_char(END_DTTM, 'YYYY-MM-DD')= '" + dateALastWeek + "' order by END_DTTM desc").list();
//        List<feedsEta> l =q2.list();
        if(l.size()<1){
            String day = dateALastWeek.substring(8,10);
            String dateAweekback2 = dateALastWeek.replace(day,Integer.toString(Integer.valueOf(day)+1));
            return session.createQuery("from feedsEta where FL_ID = "+ i+" and to_char(END_DTTM, 'YYYY-MM-DD')= '" + dateAweekback2 + "' order by END_DTTM").list();
        }
        else{
            return l;}
    }
    public Timestamp getCurrentEta(Timestamp a_week_back_eta){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(a_week_back_eta.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 7);

       return new Timestamp(cal.getTime().getTime());
    }

	public Map<String,Map<String, String>> getPreviousQuery(String prev_date, SessionFactory sessionFactory){


        //prev_date = 2020-01-20
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            List<cycleDate> list_n = session.createQuery("from cycleDate where PARM_NM ='CycleDate'").list();
            Timestamp cycledate_start = list_n.get(0).getUPDT_DTTM();
            String  cycle_dt = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(cycledate_start.getTime()));
//            cycle_dt="24-Jan-20 02.08.08 AM";

            List<frameworkBS> list_prev = session.createQuery("from frameworkBS where INVOK_ID = 'NONE' and BTCH_NM ='seqEmptyClmStg'" +
                    " and to_char(CREAT_DTTM, 'yyyy-MM-dd') = '"+prev_date+"' order by CREAT_DTTM").list();
            Timestamp start_prev = list_prev.get(0).getSTRT_DTTM();
            String  prev_start_date = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(start_prev.getTime()));

            List<frameworkBS> list_main = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqLoad835DbPrePr','seqEmptyClmStg','seqOPAITKLdStg','seqOPATruncateRlseTables'," +
                    "'seqOPALoadReleaseProcessing','seqOPAPaymentProcessing','seqOPA835PostpaymentLoad'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAUnetPreProc','seqCreateUCASDailyExt') and " +
                    "CREAT_DTTM between '"+prev_start_date+"' and '"+ cycle_dt+
                    "' order by CREAT_DTTM").list();
            System.out.println("transaction_1 UNET-PROVIDER started for previous date");
            map_prev =printTimeDiff(list_main, session);

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
    public Map<String, Map<String, String>> getQuery(SessionFactory sessionFactory){

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            //Query for cycle date
            List<cycleDate> list_n = session.createQuery("from cycleDate where PARM_NM ='CycleDate'").list();
            Timestamp cycledate_start = list_n.get(0).getUPDT_DTTM();
            String  cycle_dt = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a").format(new Date(cycledate_start.getTime()));
            System.out.println(cycle_dt);
//            cycle_dt="24-Jan-20 02.08.08 AM";
//            logger.info("SAVING LOGS FOR " + cycle_dt + "at " +curr_date);


            //Query for UNET-PROVIDER
            List<frameworkBS> list_main = session.createQuery("from frameworkBS where INVOK_ID in ('NONE','UNET') and BTCH_NM in ('seqOPASndRjctReport'," +
                    "'seqLoad835DbPrePr','seqEmptyClmStg','seqOPAITKLdStg','seqOPATruncateRlseTables'," +
                    "'seqOPALoadReleaseProcessing','seqOPAPaymentProcessing','seqOPA835PostpaymentLoad'," +
                    "'seqOPAPrvdrSchedulingFS','seqOPAFSPrvConsldtData','seqOPAFullSrcPrvPymtPrcsngFnlzn'," +
                    "'seqOPA835ValX12FileCreationPayables','seqOPACreateEPSFile','seqOPAEPSReport_FS'," +
                    "'seqOPAProvPRAFile','seqOPAUnetPreProc','seqCreateUCASDailyExt') and " +
                    "CREAT_DTTM >= '"+ cycle_dt+
                    "' order by CREAT_DTTM").list();
            System.out.println("transaction_1 for UNET-PROVIDER started");
//            logger.info("transaction_1 for UNET-PROVIDER started");
             map =printTimeDiff(list_main, session);
             Collection<String> value_list = map.get("eta").values();
             Collection<String> key_list = map.get("eta").keySet();

             Iterator<String> values = value_list.iterator();
             Iterator<String> keys = key_list.iterator();

//             Collection<String> list_2 = map_feeds.get("c");
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

    public Map<String,Map<String, String>> printTimeDiff(List<frameworkBS> list, Session session){

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

        for(int i=0; i<list.size(); i++){
            System.out.println(i);

            String eta;
            String noEta;

            if(i==0 && list.get(i).getBTCH_NM().equals("seqEmptyClmStg")){
                UNETProviderETA.setStart_PrePreProcessor(list.get(i).getSTRT_DTTM());
                l1 = aWeekBackObject("NONE", "seqEmptyClmStg", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                UNETProviderETA.setStart_PrePreProcessor_aWeekAgo(l1.get(1).getSTRT_DTTM());
            }

            if(list.size()<4 && flag_PrePreProcessor==1){
                flag_PrePreProcessor=0;
                System.out.println("on plan");
                l1 = aWeekBackObject("NONE", "seqLoad835DbPrePr", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), UNETProviderETA.getStart_PrePreProcessor_aWeekAgo());
                UNETProviderETA.setEnd_PrePreProcessor(new Timestamp(sf+UNETProviderETA.getStart_PrePreProcessor().getTime()));
//                df= simpleDateFormat.format(new Timestamp(end_PrePreProcessor.getTime()+1800000)); //adding 30 mins to extend the ETA
                eta =addExtraMargin(UNETProviderETA.getEnd_PrePreProcessor(),1800000) ;
                System.out.println("the end time of Pre-Pre Processor "+ eta/* added to current start time of file loaded*/);
//                logger.info(" PrePreProcessor on plan with ETA = "+ eta);
//                concurrentHashMap.put(eta, "ETA");
//                etaList.add(eta);
                map_eta.put("PrePreProcessor", eta);
            }

            if(i==3 && list.get(i).getBTCH_NM().equals("seqLoad835DbPrePr")){
                if(!list.get(i).getBTCH_STS_CD().equals("C") && flag_PrePreProcessor==1){
                    flag_PrePreProcessor=0;
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqLoad835DbPrePr", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), UNETProviderETA.getStart_PrePreProcessor_aWeekAgo());
                    UNETProviderETA.setEnd_PrePreProcessor(new Timestamp(sf+UNETProviderETA.getStart_PrePreProcessor().getTime()));
                    eta =addExtraMargin(UNETProviderETA.getEnd_PrePreProcessor(), 1800000);
                    System.out.println("the end time of Pre-Pre Processor"+ eta /* added to current start time of file loaded*/);
//                    logger.info(" PrePreProcessor in progress with ETA = "+eta);
//                    concurrentHashMap.put(eta, "ETA");
                    map_eta.put("PrePreProcessor", eta);
                }
                else if(list.get(i).getBTCH_STS_CD().equals("C")){
                    flag_PrePreProcessor =0;
                    UNETProviderETA.setEnd_PrePreProcessor(list.get(i).getEND_DTTM());
                    noEta= getETAinString(getEnd_PrePreProcessor());
//                    concurrentHashMap.put(getETAinString(getEnd_PrePreProcessor()),"C");
//                    noEtaList.add(noEta);
                    map_noEta.put("PrePreProcessor", noEta);
                }
            }


            if(!list.get(i).getBTCH_NM().equals("seqOPAUnetPreProc") && flag_PreProcessor==1 && list.size()<5){
                if(end_PrePreProcessor != null && end_PreProcessor == null){
                    flag_PreProcessor =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqOPAUnetPreProc", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_PreProcessor(new Timestamp(UNETProviderETA.getEnd_PrePreProcessor().getTime()+sf));
                    eta = addExtraMargin(UNETProviderETA.getEnd_PreProcessor(), 1800000);
//                    logger.info(" Pre Process on plan with ETA = "+eta);
                    System.out.println("the end time of Pre-Process "+ eta /* added to end time of prepreprocessor*/);
                    map_eta.put("PreProcessor", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAUnetPreProc")) {
                flag_PreProcessor =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_PreProcessor(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_PreProcessor());
                    map_noEta.put("PreProcessor", noEta);
                }
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqOPAUnetPreProc", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_PreProcessor(new Timestamp(UNETProviderETA.getEnd_PrePreProcessor().getTime()+sf));
                    eta =addExtraMargin(UNETProviderETA.getEnd_PreProcessor(), 1800000);
//                    logger.info(" Pre Process in progress with ETA = "+eta );
                    System.out.println("the end time of Pre-Process"+ eta/* added to end time of prepreprocessor*/);
                    map_eta.put("PreProcessor", eta);
                }

            }


            if(!list.get(i).getBTCH_NM().equals("seqOPAITKLdStg") && flag_Intake==1 && list.size()<6){
                if(end_PreProcessor != null && end_Intake == null){
                    flag_Intake =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPAITKLdStg", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPASndRjctReport", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_Intake(new Timestamp(UNETProviderETA.getEnd_PreProcessor().getTime()+sf));
                    eta =addExtraMargin(UNETProviderETA.getEnd_Intake(), 1800000);
//                    logger.info(" Intake on plan with ETA = "+eta );
                    System.out.println("the end time of Intake"+ eta /* added to end time of preprocessor*/);
                    map_eta.put("Intake", eta);
                }
            }
            if (list.get(i).getBTCH_NM().equals("seqOPAITKLdStg") && list.size()<7){
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPAITKLdStg", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPASndRjctReport", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                UNETProviderETA.setEnd_Intake(new Timestamp(UNETProviderETA.getEnd_PreProcessor().getTime()+sf));
                eta =addExtraMargin(UNETProviderETA.getEnd_Intake(), 1800000);
                map_eta.put("Intake", eta);
//                logger.info(" Intake in progress with ETA = "+ eta);
                System.out.println("the end time of Intake"+ eta /* added to end time of preprocessor*/);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPASndRjctReport")){
                flag_Intake =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_Intake(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_Intake());
                    map_noEta.put("Intake", noEta);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPATruncateRlseTables") && flag_Scheduling== 1 && list.size()<8){
                if(end_Intake != null && end_Scheduling == null){
                    flag_Scheduling =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPATruncateRlseTables", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPAPrvdrSchedulingFS", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_Scheduling(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf));
                    eta=addExtraMargin(UNETProviderETA.getEnd_Scheduling(), 1800000);
//                    logger.info(" Scheduling on plan with ETA = "+eta );
                    System.out.println("the end time of Scheduling"+ eta /* added to end time of intake*/);
                    map_eta.put("Scheduling", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPATruncateRlseTables") && !list.get(i).getBTCH_STS_CD().equals("C")){
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPATruncateRlseTables", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPAPrvdrSchedulingFS", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                UNETProviderETA.setEnd_Scheduling(new Timestamp(UNETProviderETA.getEnd_Intake().getTime()+sf));
                eta=addExtraMargin(UNETProviderETA.getEnd_Scheduling(), 1800000);
//                logger.info(" Scheduling in progress with ETA = "+ eta);
                System.out.println("the end time of Scheduling"+ eta /* added to end time of intake*/);
                map_eta.put("Scheduling", eta);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAPrvdrSchedulingFS") ){
                flag_Scheduling =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_Scheduling(list.get(i).getEND_DTTM());
                    noEta =getETAinString(getEnd_Scheduling());
                    map_noEta.put("Scheduling", noEta);
                }
            }

            if (!list.get(i).getBTCH_NM().equals("seqOPALoadReleaseProcessing") && flag_ReleaseNConsolidation==1 && list.size()<10){
                if (end_Scheduling != null && end_ReleaseNConsolidation== null){
                    flag_ReleaseNConsolidation =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPALoadReleaseProcessing", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPAFSPrvConsldtData", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_ReleaseNConsolidation( new Timestamp(UNETProviderETA.getEnd_Scheduling().getTime()+sf));
                    eta = addExtraMargin(UNETProviderETA.getEnd_ReleaseNConsolidation(), 1800000);
//                    logger.info(" Release & Consolidation on plan with ETA = "+eta );
                    System.out.println("the end time of Release & Consolidation"+ eta /* added to end time of Scheduling*/);
                    map_eta.put("ReleaseNConsolidation", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPALoadReleaseProcessing") && list.size()<11) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPALoadReleaseProcessing", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPAFSPrvConsldtData", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                UNETProviderETA.setEnd_ReleaseNConsolidation(new Timestamp(UNETProviderETA.getEnd_Scheduling().getTime()+sf));
                eta = addExtraMargin(UNETProviderETA.getEnd_ReleaseNConsolidation(), 1800000);
//                logger.info(" Release & Consolidation in progress with ETA = "+eta );
                System.out.println("the end time of Release & Consolidation"+ eta /* added to end time of Scheduling*/);
                map_eta.put("ReleaseNConsolidation", eta);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAFSPrvConsldtData")){
                flag_ReleaseNConsolidation =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_ReleaseNConsolidation( list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_ReleaseNConsolidation());
                    map_noEta.put("ReleaseNConsolidation", noEta);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPAPaymentProcessing") && flag_PaymentProcessing==1 && list.size()<12){
                if(end_ReleaseNConsolidation != null && end_PaymentProcessing== null){
                    flag_PaymentProcessing =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPAPaymentProcessing", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPAFullSrcPrvPymtPrcsngFnlzn", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(),  l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_PaymentProcessing(new Timestamp(UNETProviderETA.getEnd_ReleaseNConsolidation().getTime()+sf));
                    eta =addExtraMargin(UNETProviderETA.getEnd_PaymentProcessing(), 1800000);
//                    logger.info(" Payment Processing on plan with ETA = "+ eta);
                    System.out.println("the end time of Payment Processing"+ eta /* added to end time of Release n consolidation*/);
                    map_eta.put("PaymentProcessing", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAPaymentProcessing") && !list.get(i).getBTCH_STS_CD().equals("C")) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPAPaymentProcessing", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPAFullSrcPrvPymtPrcsngFnlzn", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(),  l1.get(0).getSTRT_DTTM());
                UNETProviderETA.setEnd_PaymentProcessing( new Timestamp(UNETProviderETA.getEnd_ReleaseNConsolidation().getTime()+sf));
                eta =addExtraMargin(UNETProviderETA.getEnd_PaymentProcessing(), 1800000);
//                logger.info(" Payment Processing in progress with ETA = "+ eta);
                System.out.println("the end time of Payment Processing"+ eta /* added to end time of Release n consolidation*/);
                map_eta.put("PaymentProcessing", eta);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAFullSrcPrvPymtPrcsngFnlzn")){
                flag_PaymentProcessing =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_PaymentProcessing(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_PaymentProcessing());
                    map_noEta.put("PaymentProcessing", noEta);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqCreateUCASDailyExt") && flag_PostPaymentExtract==1 && list.size()<14){
                if(end_PaymentProcessing!= null && end_PostPaymentExtract==null){
                    flag_PostPaymentExtract =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("NONE", "seqCreateUCASDailyExt", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate( l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_PostPaymentExtract(new Timestamp(UNETProviderETA.getEnd_PaymentProcessing().getTime()+sf));
                    eta = addExtraMargin(UNETProviderETA.getEnd_PostPaymentExtract(), 1800000);
//                    logger.info(" Post Payment Extract (OTS,TOPS, UCAS on plan with ETA = "+ eta);
                    System.out.println("the end time of Post Payment Extract (OTS,TOPS, UCAS )"+ eta /* added to end time of Payment Processing*/);
                    map_eta.put("PostPaymentExtract", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqCreateUCASDailyExt")){
                flag_PostPaymentExtract =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_PostPaymentExtract(list.get(i).getEND_DTTM());
                    noEta= getETAinString(getEnd_PostPaymentExtract());
                    map_noEta.put("PostPaymentExtract", noEta);
                }
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("NONE", "seqCreateUCASDailyExt", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate( l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_PostPaymentExtract(new Timestamp(UNETProviderETA.getEnd_PaymentProcessing().getTime()+sf));
                    eta =addExtraMargin(UNETProviderETA.getEnd_PostPaymentExtract(), 1800000);
                    map_eta.put("PostPaymentExtract", eta);
//                    logger.info(" Post Payment Extract (OTS,TOPS, UCAS in progress with ETA = "+ eta);
                    System.out.println("the end time of Post Payment Extract (OTS,TOPS, UCAS )"+ eta/* added to end time of Payment Processing*/);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPA835PostpaymentLoad") && flag_835EPS_B2B==1 && list.size()<15){
                if(end_PaymentProcessing!=null && end_835EPS_B2B == null){
                    flag_835EPS_B2B =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET","seqOPA835PostpaymentLoad", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    l2 = aWeekBackObject("UNET", "seqOPA835ValX12FileCreationPayables", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                    long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_835EPS_B2B( new Timestamp(UNETProviderETA.getEnd_PaymentProcessing().getTime()+sf));
                    eta =addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000);
//                    logger.info(" 835 EPS/B2B on plan with ETA = "+ eta);
                    System.out.println("the end time of 835 EPS/B2B"+ eta /* added to end time of Payment Processing*/);
                    map_eta.put("835EPS_B2B", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPA835PostpaymentLoad") && !list.get(i).getBTCH_STS_CD().equals("C")) {
                System.out.println("in progress");
                l1 = aWeekBackObject("UNET","seqOPA835PostpaymentLoad", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                l2 = aWeekBackObject("UNET", "seqOPA835ValX12FileCreationPayables", dateALastWeek(list.get(i).getCREAT_DTTM()),session);
                long sf = timeDiffCalculate(l2.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                UNETProviderETA.setEnd_835EPS_B2B(new Timestamp(end_PaymentProcessing.getTime()+sf));
                eta = addExtraMargin(UNETProviderETA.getEnd_835EPS_B2B(), 1800000);
//                logger.info(" 835 EPS/B2B in progress with ETA = "+ eta);
                System.out.println("the end time of 835 EPS/B2B"+ eta/* added to end time of Payment Processing*/);
                map_eta.put("835EPS_B2B", eta);
            }
            if(list.get(i).getBTCH_NM().equals("seqOPA835ValX12FileCreationPayables") ){
                flag_835EPS_B2B =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_835EPS_B2B(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_835EPS_B2B());
                    map_noEta.put("835EPS_B2B", noEta);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPACreateEPSFile") && flag_EPSFundingFile==1 && list.size()<17){
                if(end_835EPS_B2B!=null && end_EPSFundingFile== null){
                    flag_EPSFundingFile =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET", "seqOPACreateEPSFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_EPSFundingFile(new Timestamp(UNETProviderETA.getEnd_835EPS_B2B().getTime()+sf));
                    eta=addExtraMargin(UNETProviderETA.getEnd_EPSFundingFile(), 1800000);
//                    logger.info(" EPS Funding File on plan with ETA = "+  eta);
                    System.out.println("the end time of EPS Funding File"+ eta /* added to end time of 835EPS_B2B*/);
                    map_eta.put("EPSFundingFile", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPACreateEPSFile")){
                flag_EPSFundingFile =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_EPSFundingFile(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_EPSFundingFile());
                    map_noEta.put("EPSFundingFile", noEta);
                }
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("UNET", "seqOPACreateEPSFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_EPSFundingFile(new Timestamp(UNETProviderETA.getEnd_835EPS_B2B().getTime()+sf));
                    eta = addExtraMargin(UNETProviderETA.getEnd_EPSFundingFile(), 1800000);
                    map_eta.put("EPSFundingFile", eta);
//                    logger.info(" EPS Funding File in progress with ETA = "+ eta );
                    System.out.println("the end time of EPS Funding File"+ eta /* added to end time of 835EPS_B2B*/);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPAEPSReport_FS") && flag_FundingReport==1 && list.size()<18) {
                if(end_EPSFundingFile!=null && end_FundingReport==null){
                    flag_FundingReport = 0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET", "seqOPAEPSReport_FS", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_FundingReport(new Timestamp(UNETProviderETA.getEnd_EPSFundingFile().getTime()+sf));
                    eta = addExtraMargin(UNETProviderETA.getEnd_FundingReport(), 1800000);
//                    logger.info(" Funding Report on plan with ETA = "+ eta);
                    System.out.println("the end time of Funding Report "+ eta /* added to end time of EPS Funding file*/);
                    map_eta.put("FundingReport", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAEPSReport_FS")){
                flag_FundingReport = 0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_FundingReport( list.get(i).getEND_DTTM());
                    noEta= getETAinString(getEnd_FundingReport());
                    map_noEta.put("FundingReport", noEta);
                }
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("UNET", "seqOPAEPSReport_FS", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_FundingReport(new Timestamp(UNETProviderETA.getEnd_EPSFundingFile().getTime()+sf));
                    eta = addExtraMargin(UNETProviderETA.getEnd_FundingReport(), 1800000);
                    map_eta.put("FundingReport", eta);
//                    logger.info(" Funding Report in progress with ETA = "+ eta);
                    System.out.println("the end time of Funding Report "+ eta /* added to end time of EPS Funding file*/);
                }
            }

            if(!list.get(i).getBTCH_NM().equals("seqOPAProvPRAFile") && flag_ProviderPRA==1 && list.size()<19){
                if(end_EPSFundingFile!=null && end_ProviderPRA==null){
                    flag_ProviderPRA =0;
                    System.out.println("on plan");
                    l1 = aWeekBackObject("UNET", "seqOPAProvPRAFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_ProviderPRA(new Timestamp(UNETProviderETA.getEnd_EPSFundingFile().getTime()+sf));
                    eta =addExtraMargin(UNETProviderETA.getEnd_ProviderPRA(), 1800000);
//                    logger.info(" Provider PRA on plan with ETA = "+ eta);
                    System.out.println("the end time of Provider PRA "+ eta /* added to end time of EPS Funding file*/);
                    map_eta.put("ProviderPRA", eta);
                }
            }
            if(list.get(i).getBTCH_NM().equals("seqOPAProvPRAFile")){
                flag_ProviderPRA =0;
                if (list.get(i).getBTCH_STS_CD().equals("C")){
                    UNETProviderETA.setEnd_ProviderPRA(list.get(i).getEND_DTTM());
                    noEta = getETAinString(getEnd_ProviderPRA());
                    map_noEta.put("ProviderPRA", noEta);
                    flag_ExcelUpdate = 0;
                }
                else {
                    System.out.println("in progress");
                    l1 = aWeekBackObject("UNET", "seqOPAProvPRAFile", dateALastWeek(list.get(i).getCREAT_DTTM()), session);
                    long sf = timeDiffCalculate(l1.get(0).getEND_DTTM(), l1.get(0).getSTRT_DTTM());
                    UNETProviderETA.setEnd_ProviderPRA(new Timestamp(end_EPSFundingFile.getTime()+sf));
                    eta = addExtraMargin(UNETProviderETA.getEnd_ProviderPRA(), 1800000);
//                    logger.info(" Provider PRA in progress with ETA = "+ eta);
                    System.out.println("the end time of Provider PRA "+ eta/* added to end time of EPS Funding file*/);
                    map_eta.put("ProviderPRA", eta);
                }
            }
           

        }
        map.put("c", map_noEta);
        map.put("eta", map_eta);

        return map;


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
        Query q2 = session.createQuery("from frameworkBS where INVOK_ID = '"+ INVOK_ID+"' and BTCH_NM = '"+ BTCH_NM+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + dateAweekBack + "' order by CREAT_DTTM desc");
        if(q2.list().size()<1){
            String day = dateAweekBack.substring(8,10);
            String dateAweekback2 = dateAweekBack.replace(day,Integer.toString(Integer.valueOf(day)+1));
            return session.createQuery("from frameworkBS where INVOK_ID = '"+ INVOK_ID+"' and BTCH_NM = '"+ BTCH_NM+"' and to_char(CREAT_DTTM, 'YYYY-MM-DD')='" + dateAweekback2 + "' order by CREAT_DTTM").list();
        }
        else{
            return q2.list();}

    }

    public long timeDiffCalculate(Timestamp t_end, Timestamp t_start){
        long diff = t_end.getTime() - t_start.getTime();
        return diff;

    }


    public static Timestamp getStart_PrePreProcessor() {
        return start_PrePreProcessor;
    }

    public static void setStart_PrePreProcessor(Timestamp start_PrePreProcessor) {
        UNETProviderETA.start_PrePreProcessor = start_PrePreProcessor;
    }

    public static Timestamp getEnd_PrePreProcessor() {
        return end_PrePreProcessor;
    }

    public static void setEnd_PrePreProcessor(Timestamp end_PrePreProcessor) {
        UNETProviderETA.end_PrePreProcessor = end_PrePreProcessor;
    }

    public static Timestamp getStart_PrePreProcessor_aWeekAgo() {
        return start_PrePreProcessor_aWeekAgo;
    }

    public static void setStart_PrePreProcessor_aWeekAgo(Timestamp start_PrePreProcessor_aWeekAgo) {
        UNETProviderETA.start_PrePreProcessor_aWeekAgo = start_PrePreProcessor_aWeekAgo;
    }

    public static Timestamp getEnd_PreProcessor() {
        return end_PreProcessor;
    }

    public static void setEnd_PreProcessor(Timestamp end_PreProcessor) {
        UNETProviderETA.end_PreProcessor = end_PreProcessor;
    }

    public static Timestamp getEnd_Intake() {
        return end_Intake;
    }

    public static void setEnd_Intake(Timestamp end_Intake) {
        UNETProviderETA.end_Intake = end_Intake;
    }

    public static Timestamp getEnd_Scheduling() {
        return end_Scheduling;
    }

    public static void setEnd_Scheduling(Timestamp end_Scheduling) {
        UNETProviderETA.end_Scheduling = end_Scheduling;
    }

    public static Timestamp getEnd_ReleaseNConsolidation() {
        return end_ReleaseNConsolidation;
    }

    public static void setEnd_ReleaseNConsolidation(Timestamp end_ReleaseNConsolidation) {
        UNETProviderETA.end_ReleaseNConsolidation = end_ReleaseNConsolidation;
    }

    public static Timestamp getEnd_PaymentProcessing() {
        return end_PaymentProcessing;
    }

    public static void setEnd_PaymentProcessing(Timestamp end_PaymentProcessing) {
        UNETProviderETA.end_PaymentProcessing = end_PaymentProcessing;
    }

    public static Timestamp getEnd_PostPaymentExtract() {
        return end_PostPaymentExtract;
    }

    public static void setEnd_PostPaymentExtract(Timestamp end_PostPaymentExtract) {
        UNETProviderETA.end_PostPaymentExtract = end_PostPaymentExtract;
    }

    public static Timestamp getEnd_835EPS_B2B() {
        return end_835EPS_B2B;
    }

    public static void setEnd_835EPS_B2B(Timestamp end_835EPS_B2B) {
        UNETProviderETA.end_835EPS_B2B = end_835EPS_B2B;
    }

    public static Timestamp getEnd_EPSFundingFile() {
        return end_EPSFundingFile;
    }

    public static void setEnd_EPSFundingFile(Timestamp end_EPSFundingFile) {
        UNETProviderETA.end_EPSFundingFile = end_EPSFundingFile;
    }

    public static Timestamp getEnd_FundingReport() {
        return end_FundingReport;
    }

    public static void setEnd_FundingReport(Timestamp end_FundingReport) {
        UNETProviderETA.end_FundingReport = end_FundingReport;
    }

    public static Timestamp getEnd_ProviderPRA() {
        return end_ProviderPRA;
    }

    public static void setEnd_ProviderPRA(Timestamp end_ProviderPRA) {
        UNETProviderETA.end_ProviderPRA = end_ProviderPRA;
    }

}

package com.songzi.hibernate;

import com.songzi.hibernate.entity.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.*;

/**某个用户在使用流量情况下的资费生成*/
public class Solution3 {

    private static Transaction tran;
    private static Session sessionObj;
    private static SessionFactory sessionFactoryObj;

    private static Customer cus;

    public Solution3(SessionFactory sessionFactoryObj){
        this.sessionFactoryObj = sessionFactoryObj;
        this.sessionObj = sessionFactoryObj.openSession();
    }

    public Date date;
    /**===============================需要用到的数据库增删改查的方法=================================*/

    /**通过顾客id查找流量资费*/
    private List<Postage> searchFlowPostageByCustomerId(Customer cus, int type){
        sessionObj = sessionFactoryObj.openSession();
        tran = sessionObj.beginTransaction();
        List<Postage> res = new ArrayList<Postage>();
        List<Orders> list = new ArrayList<Orders>();
        try {

            Query query1 = sessionObj.createQuery("from Orders where cid =:cid and cancel_time = null");
            query1.setParameter("cid", cus.getId());
            list = query1.list();
            for(Orders o : list){
                Query query2 = sessionObj.createQuery("from Postage where pid =:pid and type =:type");
                query2.setParameter("pid", o.getPackageByPid().getId());
                query2.setParameter("type", type);
                res.addAll(query2.list());
            }
            tran.commit();
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
            tran.rollback();
        }
        return res;
    }

    /**新增单次资费
     * @param amount 数量
     * @param money 金额
     * */
    private boolean addSinglecost(double amount, double money, int type){
        sessionObj = sessionFactoryObj.openSession();
        tran = sessionObj.beginTransaction();
        try {
            Singlecost sc = new Singlecost();
            sc.setType(type);
            sc.setAmount(amount);
            sc.setCustomerByCid(cus);
            sc.setMoney(money);
            sc.setDate(new java.sql.Timestamp(date.getTime()));
            sessionObj.saveOrUpdate(sc);
            if (!tran.wasCommitted())
                tran.commit();
            return true;
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
            tran.rollback();
        }
        return false;
    }

    /**根据本次资费，修改顾客账户余额
     * @param  flow 本次使用的流量
     * @param  money 金额
     * @param  type 本次使用的流量类型
     * */
    private boolean changeCustomerNums(double flow, double money, int type){
        try {
            sessionObj = sessionFactoryObj.openSession();
            tran = sessionObj.beginTransaction();
            cus.setNums(cus.getNums()-money);
            //System.out.println(cus.getDoFl());
            //System.out.println(flow);
            if(type == 1) cus.setLoFl(cus.getLoFl()+flow);
            if(type == 2) cus.setDoFl(cus.getDoFl()+flow);
            //System.out.println(cus.getDoFl());
            sessionObj.saveOrUpdate(cus);


            tran.commit();
            return true;
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
            tran.rollback();
        }
        return false;
    }


    /**=========================================实现题目的逻辑=========================================*/

    /**主程序，包含一些输入逻辑*/
    public void Q3() {
        System.out.println("================这是第三个问题==============");

        //设定时间为2018-10-10
        try {
            String time = "2018-10-10 23:55:38";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(time);
        }catch(java.text.ParseException e){
            e.getErrorOffset();
        }

        Scanner sc = new Scanner(System.in);
        //sc.useDelimiter("/n");
        System.out.println("请输入当前用户id（范围1-10）");
        String cus_id = sc.next();
        long cid = Long.valueOf(cus_id);
        cus = (Customer)sessionObj.get(Customer.class, cid);
        if (Integer.valueOf(cus_id) > 10 || Integer.valueOf(cus_id) <= 0) {
            System.out.println("您的输入有误");
        } else {
            System.out.println("现为顾客 " + cus_id + " 进行服务");
        }
        while (true) {
            System.out.println("请输入本次流量的量：");
            double flow = sc.nextDouble();
            //System.out.println("PS:根据测试数据，建议进行输入两次10");
            System.out.println("请确定本次流量类型：1为本地流量，2为国内流量");
            int type = sc.nextInt();
            double res = 0;
            long startTime = System.currentTimeMillis();    //获取开始时间
            if(type == 1 ) res = calculateLocal(flow);
            else if(type == 2) res = calculateDom(flow);
            else System.out.println("您的输入有误，请重试");
            long endTime = System.currentTimeMillis();    //获取结束时间
            System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        }

    }

    /**计算本地流量具体实现类
     * @param flow
     *              单次流量数目
     * @return 单次资费
     * */
    private double calculateLocal(double flow){
        double money = 0;

        double addFlow = flow;

        //套餐中提供的本地流量和国内流量
        double offerLocalFlow = 0;
        double offerDoFlow = 0;

        Base b = cus.getBaseByBid();

        //已经使用的本地流量和国内流量
        double lo_fl = cus.getLoFl();
        double do_fl = cus.getDoFl();

        List<Postage> pos = searchFlowPostageByCustomerId(cus, 1);
        for(Postage po : pos){
            offerLocalFlow += po.getNums();
            //System.out.println(offerLocalFlow);
        }
        List<Postage> pos_2 = searchFlowPostageByCustomerId(cus, 2);
        for(Postage po : pos_2){
            offerDoFlow += po.getNums();
            //System.out.println(offerDoFlow);
        }

        System.out.println("==================正在记录并计算本次资费，请稍后===================");

        System.out.print("您的当前资费为 ");

        //所使用的本地流量没超出优惠本地流量范围
        if(lo_fl + flow < offerLocalFlow) {
            System.out.println("0 元，套餐剩余可使用本地流量为 " + (offerLocalFlow-(lo_fl + flow) + "M" +
                    "剩余可使用国内流量为 " + (offerDoFlow-do_fl))+ "M");
            System.out.println("请注意，您的流量若超出套餐优惠，将按照本地流量"
                    + b.getLoFl() + "元/分钟计费，国内流量" + b.getDoFl() + "元/分钟计费");
            System.out.println("=======================================================================\n");

            //新增本次资费
            addSinglecost(flow, 0, 1);
            changeCustomerNums(flow, 0, 1);
            System.out.println("Situation1");
            return 0;
        }
        //所使用的本地流量没超出优惠本地流量+国内流量范围（先使用本地流量，不够使用国内流量，等于这部分本地流量记录在全国流量内）
        else if(lo_fl + flow + do_fl< offerLocalFlow + offerDoFlow){
            System.out.println("0 元，套餐剩余可使用本地流量为 0 M" +
                    "剩余可使用国内流量为 " + (offerLocalFlow + offerDoFlow - lo_fl - flow)+ "M");
            System.out.println("请注意，您的流量若超出套餐优惠，将按照本地流量"
                    + b.getLoFl() + "元/分钟计费，国内流量" + b.getDoFl() + "元/分钟计费");
            System.out.println("=======================================================================\n");

            //新增本次资费
            addSinglecost(offerLocalFlow-lo_fl, 0, 1);
            addSinglecost(flow - (offerLocalFlow-lo_fl), 0, 2);
            //修改customer余额
            //changeCustomerNums(0, 0, 1);
            changeCustomerNums(lo_fl + flow - offerLocalFlow, 0, 2);
            System.out.println("Situation2");
            return 0;
        }
        //这次所使用的本地流量超出优惠本地流量+国内流量范围,但之前累计使用的尚未超出
        else if(lo_fl + do_fl< offerLocalFlow + offerDoFlow){
            money = (lo_fl + do_fl + flow - offerLocalFlow - offerDoFlow)*b.getLoFl();

            System.out.println(money + " 元，套餐剩余可使用本地流量为 0 M" +
                    "剩余可使用国内流量为 0 M");
            System.out.println("请注意，您的流量已经超出套餐优惠，现按照本地流量"
                    + b.getLoFl() + "元/分钟计费，国内流量" + b.getDoFl() + "元/分钟计费");
            System.out.println("=======================================================================\n");

            //新增本次资费
            addSinglecost(flow, money, 2);
            //修改customer余额
            changeCustomerNums(flow + offerDoFlow - do_fl, money, 1);
            changeCustomerNums(lo_fl + flow - offerLocalFlow, 0, 2);
            System.out.println("Situation3");
        }
        //之前使用的本地流量已经超出优惠本地流量+国内流量范围，这次直接累加资费
        else{
            money = flow * b.getLoFl();

            System.out.println(money + " 元，套餐剩余可使用本地流量为 0 M" +
                    "剩余可使用国内流量为 0 M");
            System.out.println("请注意，您的流量已经超出套餐优惠，现按照本地流量"
                    + b.getLoFl() + "元/分钟计费，国内流量" + b.getDoFl() + "元/分钟计费");
            System.out.println("=======================================================================\n");
            //新增本次资费
            addSinglecost(flow, money, 1);
            //修改customer余额
            changeCustomerNums(flow, money, 1);
            System.out.println("Situation4");
        }
        return money;

    }

    /**计算国内流量具体实现类
     * @param flow
     *              单次流量数目
     * @return 单次资费
     * */
    private double calculateDom(double flow){
        double money = 0;

        double addFlow = flow;

        //套餐中提供的国内流量
        double offerDoFlow = 0;

        Base b = cus.getBaseByBid();

        //已经使用的国内流量
        double do_fl = cus.getDoFl();

        List<Postage> pos_2 = searchFlowPostageByCustomerId(cus, 2);
        for(Postage po : pos_2){
            offerDoFlow += po.getNums();
            System.out.println(offerDoFlow);
        }
        System.out.println(offerDoFlow);
        System.out.println("==================正在记录并计算本次资费，请稍后===================");

        System.out.print("您的当前资费为 ");

        //所使用的国内流量没超出优惠国内流量范围

        if( flow + do_fl < offerDoFlow) {
            System.out.println("0 元，套餐剩余可使用国内流量为 " + (offerDoFlow-do_fl - flow)+ "M");
            System.out.println("请注意，您的流量已经超出套餐优惠，现在按照国内流量" + b.getDoFl() + "元/分钟计费");
            System.out.println("=======================================================================\n");

            //新增本次资费
            addSinglecost(flow, 0, 2);
            changeCustomerNums(flow, 0, 2);
            return 0;
        }
        //这次所使用的国内流量超出优惠国内流量范围,但之前累计使用的尚未超出
        else if( do_fl < offerDoFlow){
            money = (do_fl + flow - offerDoFlow)*b.getDoFl();

            System.out.println(money + " 元，套餐剩余可使用国内流量为 0 M");
            System.out.println("请注意，您的流量已经超出套餐优惠，现在按照国内流量" + b.getDoFl() + "元/分钟计费");
            System.out.println("=======================================================================\n");

            //新增本次资费
            addSinglecost(flow, money, 2);
            //修改customer余额
            changeCustomerNums(flow, money, 2);
        }
        //之前使用的国内流量已经超出优惠国内流量范围，这次直接累加资费
        else{
            money = flow * b.getDoFl();

            System.out.println(money + " 元，套餐剩余可使用本地流量为 0 M" +
                    "剩余可使用国内流量为 0 M");
            System.out.println("请注意，您的流量已经超出套餐优惠，现按照本地流量"
                    + b.getLoFl() + "元/分钟计费，国内流量" + b.getDoFl() + "元/分钟计费");
            System.out.println("=======================================================================\n");
            //新增本次资费
            addSinglecost(flow, money, 2);
            //修改customer余额
            changeCustomerNums(flow, money, 2);
        }
        return money;
    }

}

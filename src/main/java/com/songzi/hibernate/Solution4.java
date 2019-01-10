package com.songzi.hibernate;

/**某个用户月账单的生成*/

import com.songzi.hibernate.entity.Customer;
import com.songzi.hibernate.entity.Orders;
import com.songzi.hibernate.entity.Postage;
import com.songzi.hibernate.entity.Singlecost;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.*;

/**格式
 * ===========以下为您的本月账单==============
 *  您的账户余额为    x 元
 *  您本月资费为
 *      ......          .........
 *      ......          .........
 *  -----------------------------------
 * 您当前正在使用的套餐有 套餐x 套餐y
 * 您本月已通话时长为，其中套餐内通话时长为
 * 次月将自动清零
 * ------------------------------------
 * */
public class Solution4 {

    private static Transaction tran;
    private static Session sessionObj;
    private static SessionFactory sessionFactoryObj;

    private static Customer cus;

    public Solution4(SessionFactory sessionFactoryObj){
        this.sessionFactoryObj = sessionFactoryObj;
        this.sessionObj = sessionFactoryObj.openSession();
    }

    public Date date;
    /**===============================需要用到的数据库增删改查的方法=================================*/
    /**通过顾客查找单次资费表*/
    private List<Singlecost> searchSinglecostByCustomer(Customer cus){
        sessionObj = sessionFactoryObj.openSession();
        tran = sessionObj.beginTransaction();
        List<Singlecost> res = new ArrayList<Singlecost>();
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            Query query2 = sessionObj.createQuery("from Singlecost where month(date)=:month and year(date)=:year)");
            query2.setParameter("month", month);
            query2.setParameter("year", year);
            res = query2.list();
            tran.commit();
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
            sessionObj.getTransaction().rollback();
        }
        return res;
    }

    /**通过顾客查找其订购的套餐*/
    private List<com.songzi.hibernate.entity.Package> searchPackageByCustomer(Customer cus){
        sessionObj = sessionFactoryObj.openSession();
        tran = sessionObj.beginTransaction();
        List<com.songzi.hibernate.entity.Package> list = new ArrayList<com.songzi.hibernate.entity.Package>();
        try {
            Query query = sessionObj.createQuery("from Orders where cid =:cid and cancel_time = null");
            query.setParameter("cid", cus.getId());
            List<Orders> orders = query.list();
            for(Orders o : orders){
                list.add(o.getPackageByPid());
            }
            tran.commit();
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        return list;
    }

    /**通过顾客查找其资费*/
    private List<Postage> searchPostageByCustomer(Customer cus) {
        sessionObj = sessionFactoryObj.openSession();
        tran = sessionObj.beginTransaction();
        List<Postage> res = new ArrayList<Postage>();
        List<Orders> list = new ArrayList<Orders>();
        try {
            Query query1 = sessionObj.createQuery("from Orders where cid =:cid and cancel_time = null");
            query1.setParameter("cid", cus.getId());
            list = query1.list();
            for (Orders o : list) {
                Query query2 = sessionObj.createQuery("from Postage where pid =:pid");
                query2.setParameter("pid", o.getPackageByPid().getId());
                res.addAll(query2.list());
                //System.out.println(query2.list().size());
            }
            tran.commit();
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
            sessionObj.getTransaction().rollback();
        }
        //System.out.println(res.size());
        return res;
    }

    /**=========================================实现题目的逻辑=========================================*/

    /**主程序，包含一些输入逻辑*/
    public void Q4() {
        System.out.println("================这是第四个问题==============");

        //设定时间为2018-10-10
        try {
            String time = "2018-10-11 23:55:38";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(time);
        } catch (java.text.ParseException e) {
            e.getErrorOffset();
        }

        Scanner sc = new Scanner(System.in);
        //sc.useDelimiter("/n");
        System.out.println("请输入当前用户id（范围1-10）");
        String cus_id = sc.next();
        long cid = Long.valueOf(cus_id);
        cus = (Customer) sessionObj.get(Customer.class, cid);

        if (Integer.valueOf(cus_id) > 10 || Integer.valueOf(cus_id) <= 0) {
            System.out.println("您的输入有误");
        } else {
            System.out.println("现为顾客 " + cus_id + " 进行服务");
        }
        long startTime = System.currentTimeMillis();    //获取开始时间
        //账户余额
        double balance = cus.getNums();
        //单次资费获取
        List<Singlecost> scs = searchSinglecostByCustomer(cus);
        //System.out.println("资费此数" + scs.size());
        //本月通话资费
        double tel_sc = 0;
        //本月本地流量资费
        double lofl_sc = 0;
        //本月国内流量资费
        double dofl_sc = 0;
        //本月短信资费
        double text_sc = cus.getText() * cus.getBaseByBid().getText();
        //System.out.println(scs.size());
        for(Singlecost s : scs){
            if(s.getMoney() != null && s.getType() == 0){ tel_sc += s.getMoney(); }
            else if(s.getMoney() != null && s.getType() == 1){ lofl_sc += s.getMoney(); }
            else if(s.getMoney() != null && s.getType() == 2){ dofl_sc += s.getMoney(); }
        }
        //本月已通话时长
        double tel_amount = cus.getTel();
        //本月已使用本地流量
        double lofl_amount = cus.getLoFl();
        //本月已使用国内流量
        double dofl_amount = cus.getDoFl();
        //本月短信数
        double text_amount = cus.getText();

        //套餐资费提供的
        double offer_tel = 0;
        double offer_lofl = 0;
        double offer_dofl = 0;
        double offer_text = 0;
        List<com.songzi.hibernate.entity.Package> packages = searchPackageByCustomer(cus);
        List<Postage> postages = searchPostageByCustomer(cus);
        for(Postage p : postages){
            if(p.getType() == 0) offer_tel += p.getNums();
            else if(p.getType() == 1) offer_lofl += p.getNums();
            else if(p.getType() == 2) offer_dofl += p.getNums();
            else if(p.getType() == 3) offer_text += p.getNums();
        }
        //System.out.println(offer_lofl + " " + offer_dofl);

        System.out.println("===========以下为您的本月账单==============");
        System.out.println("您的账户余额为                " + balance + "元");
        System.out.println("------------------------------------------");
        System.out.println("您本月资费");
        System.out.println("本月通话资费                  " + tel_sc + "元");
        System.out.println("本月本地流量资费              " + lofl_sc + "元");
        System.out.println("本月国内流量资费              " + dofl_sc + "元");
        System.out.println("本月短信资费                  " + text_sc + "元");
        System.out.println("------------------------------------------");
        System.out.print("您当前正在使用的套餐有 ");
        for(com.songzi.hibernate.entity.Package p : packages){
            System.out.print("套餐" + p.getId() + "; ");
        }
        System.out.println();
        System.out.println("您本月已通话时长为" + tel_amount +"，其中套餐内通话时长为" +
                ((tel_amount>offer_tel)?(offer_tel):(tel_amount)));

        System.out.println("您本月已使用本地流量为" + lofl_amount +"，其中套餐内本地流量为" +
                ((lofl_amount>offer_lofl)?(offer_lofl):(lofl_amount)));

        System.out.println("您本月已使用国内流量为" + dofl_amount +"，其中套餐内国内流量为"+
                ((dofl_amount>offer_dofl)?(offer_dofl):(dofl_amount)));
        //System.out.println(dofl_amount + " " + offer_dofl);

        System.out.println("您本月已发短信数为" + text_amount +"，其中套餐内短信数为"+
                ((text_amount>offer_text)?(offer_text):(text_amount)));

        System.out.println("------------------------------------------");

        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
    }
}
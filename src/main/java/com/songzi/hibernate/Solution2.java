package com.songzi.hibernate;

import com.songzi.hibernate.entity.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.*;

/**这是第二个问题：某个用户在通话情况下的资费生成*/
public class Solution2 {

    private static Transaction tran;
    private static Session sessionObj;
    private static SessionFactory sessionFactoryObj;

    private static Customer cus;

    public Solution2(SessionFactory sessionFactoryObj){
        this.sessionFactoryObj = sessionFactoryObj;
        this.sessionObj = sessionFactoryObj.openSession();
    }

    public Date date;
    /**===============================需要用到的数据库增删改查的方法=================================*/

    /**通过顾客id查找资费*/
    private List<Postage> searchPostageByCustomerId(long cid){

        List<Postage> res = new ArrayList<Postage>();
        List<Orders> list = new ArrayList<Orders>();
        try {

            Query query1 = sessionObj.createQuery("from Orders where cid =:cid and cancel_time = null");
            query1.setParameter("cid", cid);
            list = query1.list();
            for(Orders o : list){
                Query query2 = sessionObj.createQuery("from Postage where pid =:pid and type = 0");
                query2.setParameter("pid", o.getCustomerByCid().getId());
                res.addAll(query2.list());
            }
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
            sessionObj.getTransaction().rollback();
        }
        return res;
    }

    /**新增单次资费
     * @param cid 顾客id
     * @param amount 数量
     * @param money 金额
     * */
    private boolean addSinglecost(long cid, double amount, double money){
        try {
            sessionObj = sessionFactoryObj.openSession();
            tran = sessionObj.beginTransaction();
            Singlecost sc = new Singlecost();
            sc.setType(0);
            sc.setAmount(amount);
            sc.setCustomerByCid((Customer)sessionObj.get(Customer.class, cid));
            sc.setMoney(money);
            sc.setDate(new java.sql.Timestamp(date.getTime()));
            sessionObj.save(sc);
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
     * @param  cid 顾客id
     * @param  money 金额
     * @param  tel 总时间tel
     * */
    private boolean changeCustomerNums(long cid, double money, double tel){
        try {
            sessionObj = sessionFactoryObj.openSession();
            tran = sessionObj.beginTransaction();
            Customer cus = (Customer)sessionObj.get(Customer.class, cid);
            cus.setNums(cus.getNums()-money);
            cus.setTel(tel);
            sessionObj.save(cus);

            if (!tran.wasCommitted())
                tran.commit();
            return true;
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
            tran.rollback();
        }
        return false;
    }

    /**修改customer的本月通话时间*/
    private boolean changeCustomerTel(double time){
        try {
            //System.out.println(time);
            sessionObj = sessionFactoryObj.openSession();
            tran = sessionObj.beginTransaction();
            cus.setTel(time);
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
    public void Q2() {
        System.out.println("================这是第二个问题==============");

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
        if (Integer.valueOf(cus_id) > 10) {
            System.out.println("您的输入有误");
        } else {
            System.out.println("现为顾客 " + cus_id + " 进行服务");
            cus = (Customer)sessionObj.get(Customer.class, cid);
        }
        while (true) {
            System.out.println("请输入本次正在进行或已经结束的通话时长（通过当前时长、已有时长、套餐计算应有资费）：");
            System.out.println("PS:根据测试数据，建议进行输入两次20");
            double time = sc.nextDouble();
            long startTime = System.currentTimeMillis();    //获取开始时间
            double res = calculate(cid, time);
            long endTime = System.currentTimeMillis();    //获取结束时间
            System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        }

    }

    /**计算资费具体实现类
     * @param cus_id
     *              顾客id
     * @param time
     *              单次时间
     * @return 单次资费
     * */
    private double calculate(long cus_id, double time){
        double addTime = time + cus.getTel();
        double offerTime = 0;

        Customer cus = (Customer) sessionObj.get(Customer.class, cus_id);
        Base b = cus.getBaseByBid();

        //System.out.println(addTime);
        List<Postage> pos = searchPostageByCustomerId(cus_id);
        for(Postage po : pos){
            offerTime += po.getNums();
            //System.out.println(offerTime);
        }

        System.out.println("==================正在记录并计算本次资费，请稍后===================");

        System.out.print("您的此次需要支付的资费为 ");

        double money = 0;
        if(cus.getTel() +time <= offerTime) {
            System.out.println("0 元，套餐剩余可使用通话时间为 " + (offerTime - addTime));
            System.out.println("请注意，您的通话量若超出套餐优惠通话量，将按照" + b.getTel() + "元/分钟计费");
            System.out.println("=======================================================================\n");

            //新增本次资费
            addSinglecost(cus_id, time, 0);
            changeCustomerTel(addTime);
            return 0;
        }
        else if(cus.getTel() <= offerTime){
            money = (addTime-offerTime)*b.getTel();
            System.out.println( money + "元，套餐剩余可使用通话时间为 0 分钟");
            System.out.println("请注意，您的通话量已 超出 套餐优惠通话量，现在将按照 " + b.getTel() + " 元/分钟计费");
            System.out.println("=======================================================================\n");

            //新增本次资费
            addSinglecost(cus_id, time, (addTime-offerTime)*b.getTel());
            //修改customer余额,tel数量
            changeCustomerNums(cus_id, (addTime-offerTime)*b.getTel(), addTime);
        }
        else {
            money = time*b.getTel();
            System.out.println(money + "元，套餐剩余可使用通话时间为 0 分钟");
            System.out.println("请注意，您的通话量已 超出 套餐优惠通话量，现在将按照 " + b.getTel() + " 元/分钟计费");
            System.out.println("=======================================================================\n");

            //新增本次资费
            addSinglecost(cus_id, time, time*b.getTel());
            //修改customer余额,tel数量
            changeCustomerNums(cus_id, time*b.getTel(), addTime);
        }

        return money;

    }

}

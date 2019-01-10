package com.songzi.hibernate;

import com.songzi.hibernate.entity.Customer;
import com.songzi.hibernate.entity.Orders;
import com.songzi.hibernate.entity.Postage;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.*;

/**这是第一个问题：对某个用户进行套餐的查询（包括历史记录）、订购、退订（考虑立即生效和次月生效）操作*/
public class Solution1 {
    private static Session sessionObj;
    private static SessionFactory sessionFactoryObj;
    private static Transaction tran;

    public Date date;

    public Solution1(SessionFactory sessionFactoryObj){
        this.sessionFactoryObj = sessionFactoryObj;
        this.sessionObj = sessionFactoryObj.openSession();
    }

    /**===============================需要用到的数据库增删改查的方法=================================*/

    /**根据顾客id查找套餐订单*/
    private List<Orders> searchOrdersByCustomerId(long cid){
        sessionObj = sessionFactoryObj.openSession();
        tran = sessionObj.beginTransaction();
        List<Orders> list = new ArrayList<Orders>();
        try {
            Query query_now = sessionObj.createQuery("from Orders where cid =:id");
            query_now.setParameter("id", cid);
            list = query_now.list();
            tran.commit();
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        return list;
    }

    /**获取所有的套餐*/
    private List<com.songzi.hibernate.entity.Package> searchAllPachages(){
        sessionObj = sessionFactoryObj.openSession();
        tran = sessionObj.beginTransaction();
        List<com.songzi.hibernate.entity.Package> list = new ArrayList<com.songzi.hibernate.entity.Package>();
        try {
            Query query_now = sessionObj.createQuery("from Package");
            list = query_now.list();
            tran.commit();;
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        return list;
    }

    /**新增套餐，加上新增时间和类型*/
    private boolean addOrder(long cid, long pid, int type){
        try {
            sessionObj = sessionFactoryObj.openSession();
            tran = sessionObj.beginTransaction();
            Orders o = new Orders();
            o.setCustomerByCid((Customer)sessionObj.get(Customer.class, cid));
            o.setPackageByPid((com.songzi.hibernate.entity.Package) sessionObj.get(com.songzi.hibernate.entity.Package.class, pid));
            o.setOrderTime(new java.sql.Timestamp(date.getTime()));
            o.setOrderType(type);
            sessionObj.save(o);
            tran.commit();
            return true;
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
            tran.rollback();
        }
        return false;
    }

    /**取消套餐，加上取消时间和类型*/
    private boolean cancelOrder(Orders o, int type){
        try {
            sessionObj = sessionFactoryObj.openSession();
            tran = sessionObj.beginTransaction();
            tran = sessionObj.beginTransaction();
            o.setCancelTime(new java.sql.Timestamp(date.getTime()));
            o.setCancelType(type);
            sessionObj.save(o);
            if (!tran.wasCommitted())
                tran.commit();
            return true;
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
            tran.rollback();
        }
        return true;
    }

    /**通过套餐id查找其中套餐资费*/
    private List<Postage> getPostageByPackageId(long pid){
        List<Postage> list = new ArrayList<Postage>();
        try {
            sessionObj = sessionFactoryObj.openSession();
            tran = sessionObj.beginTransaction();
            Query query_now = sessionObj.createQuery("from Postage where pid =:pid");
            query_now.setParameter("pid", pid);
            list = query_now.list();
            tran.commit();
        }catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        return list;
    }

    /**=========================================实现题目的逻辑=========================================*/

    /**主程序，包含一些输入逻辑*/
    public void Q1() {
        System.out.println("================这是第一个问题==============");
        //设定时间为2018-10-10
        try {
            String time = "2018-10-10 23:55:38";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(time);
        }catch(java.text.ParseException e){
            e.getErrorOffset();
        }

        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("/n");
        System.out.println("请输入您要查找的用户id（范围1-10）");
        String cus_id = sc.nextLine();
        Long cid = Long.valueOf(cus_id);
        if (cid == null) {
            System.out.println("您的输入有误");
        } else {
            System.out.println("现为顾客 " + cus_id + " 进行服务");
        }
        while (true) {
            System.out.println("请输入数字来选择您要进行的操作：查询->1；订购->2；退订->3");
            String operation = sc.nextLine();
            if (operation.equals("1")) {
                find(cid);
            } else if (operation.equals("2")) {
                order(cid);
            } else if (operation.equals("3")) {
                cancel(cid);
            } else {
                System.out.println("您的输入错误，本次操作取消！");
                break;
            }
        }
        sessionObj.close();
    }

    /**对某个用户进行套餐的查询（包括历史记录） 的逻辑代码
     * @param cid 顾客id
     * */
    private void find(long cid){
        long startTime = System.currentTimeMillis();    //获取开始时间


        ArrayList<Orders> now = new  ArrayList<Orders>();
        ArrayList<Orders> pass = new  ArrayList<Orders>();

        List<Orders> list = searchOrdersByCustomerId(cid);
        //System.out.println(list.size());
        for (Orders order : list) {
            //已经订购的套餐
            if(order.getCancelTime() == null){
                now.add(order);
            }
            else{
                pass.add(order);
            }
        }
        if(now.size() == 0){System.out.println("您当前没有套餐");}
        else {
            System.out.println("您正在使用的套餐包括：");
            for (Orders n : now) {
                System.out.print("套餐 " + n.getPackageByPid().getId() + " 订购时间：" + n.getOrderTime());
                if(n.getOrderType() == 0) { System.out.println(" 订购生效时间：" + n.getOrderTime());}
                else {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(n.getOrderTime());//设置起时间
                    cal.add(Calendar.MONTH, 1);
                    System.out.println(" 订购生效时间：" + cal.getTime());
                }
            }
        }
        if(pass.size() == 0){System.out.println("您没有历史套餐");}
        else {
            System.out.println("您的历史套餐包括：");
            for (Orders p : pass) {
                System.out.print("套餐" + p.getPackageByPid().getId() + " 订购时间：" + p.getOrderTime());
                if(p.getOrderType() == 0) { System.out.print(" 订购生效时间：" + p.getOrderTime());}
                else{
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(p.getOrderTime());//设置起时间
                    cal.add(Calendar.MONTH, 1);
                    System.out.print(" 订购生效时间：" + cal.getTime());
                }
                System.out.print(" 退订时间：" + p.getCancelTime());
                if(p.getCancelType() == 0) { System.out.println(" 退订生效时间：" + p.getCancelTime());}
                else{
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(p.getCancelTime());//设置起时间
                    cal.add(Calendar.MONTH, 1);
                    System.out.println(" 退订生效时间：" + cal.getTime());
                }
            }
        }

        long endTime = System.currentTimeMillis();    //获取结束时间

        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        //sessionObj.getTransaction().commit();
    }

    /**
     * 订购套餐的逻辑代码
     * */
    private void order(long cid){

        List<com.songzi.hibernate.entity.Package> list = searchAllPachages();

        //给出用户可订购的套餐
        System.out.println("当前可选套餐套餐见下");
        List<Orders> list_o = searchOrdersByCustomerId(cid);
        ArrayList<com.songzi.hibernate.entity.Package> now = new ArrayList<com.songzi.hibernate.entity.Package>();
        for (Orders order : list_o) {
            if (order.getCancelTime() == null) {
                now.add(order.getPackageByPid());
            }
        }
        list.removeAll(now);
        getAllPackageInfo(list);

        System.out.println();
        while(true) {
            System.out.println("选择您要订购的套餐编号：");
            Scanner sc = new Scanner(System.in);
            long pid = sc.nextInt();
            for(com.songzi.hibernate.entity.Package p : list){
                if(p.getId() == pid){
                    System.out.println("是否当月生效, 是为0，不是为1");
                    int str = sc.nextInt();
                    long startTime = System.currentTimeMillis();    //获取开始时间
                    boolean res = addOrder(cid, pid, str);
                    System.out.println("==============正在为您办理，请稍后====================");
                    if (res) System.out.println("==============办理成功====================");
                    else System.out.println("==============办理失败====================");
                    long endTime = System.currentTimeMillis();    //获取结束时间
                    System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
                }
            }
            break;
        }
    }

    /**
     * 退订套餐的逻辑代码
     * */
    private void cancel(long cid){
        List<Orders> list_o = searchOrdersByCustomerId(cid);
        ArrayList<Orders> os = new ArrayList<Orders>();
        ArrayList<com.songzi.hibernate.entity.Package> ps = new ArrayList<com.songzi.hibernate.entity.Package>();
        for (Orders order : list_o) {
            if (order.getCancelTime() == null) {
                os.add(order);
                ps.add(order.getPackageByPid());
            }
        }
        while(true) {
            System.out.println("选择您要退订的套餐编号：");
            Scanner sc = new Scanner(System.in);
            long pid = sc.nextInt();
            com.songzi.hibernate.entity.Package p = (com.songzi.hibernate.entity.Package) sessionObj.get(com.songzi.hibernate.entity.Package.class, pid);
            for (int i = 0; i < ps.size(); i++) {
                if (ps.contains(p)) {
                    if (ps.get(i).getId() == pid) {
                        System.out.println("退订是否当月生效, 是为0，不是为1");
                        int str = sc.nextInt();
                        long startTime = System.currentTimeMillis();    //获取开始时间
                        boolean res = cancelOrder(os.get(i), str);
                        System.out.println("==============正在为您办理，请稍后====================");
                        if (res) System.out.println("==============办理成功====================");
                        else System.out.println("==============办理失败====================");
                        long endTime = System.currentTimeMillis();    //获取结束时间
                        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
                        return;
                    }
                }
            }
            break;
        }
    }

    /**获得所有套餐的信息*/
    private void getAllPackageInfo(List<com.songzi.hibernate.entity.Package> list){
        for(com.songzi.hibernate.entity.Package pa : list){

            System.out.print("套餐 " + pa.getId() + " 月功能费" + pa.getFee() + "元 ");
            List<Postage> list2 = getPostageByPackageId(pa.getId());
            int count = 1;
            for (Postage po : list2) {
                System.out.print("资费" + count + ":");
                if (po.getType() == 0) System.out.print("最多可拨打" + po.getNums() + "分钟电话; ");
                else if (po.getType() == 1) System.out.print("最多可以获得" + po.getNums() + "M流量; ");
                else if (po.getType() == 2) System.out.print("最多可以获得" + po.getNums() + "M流量; ");
                else System.out.print("最多发送" + po.getNums() + "条短信; ");
                count++;
            }
            System.out.println();
        }
    }
}


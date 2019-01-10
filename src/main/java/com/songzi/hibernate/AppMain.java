package com.songzi.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class AppMain {

    private static SessionFactory sessionFactoryObj;

    public static SessionFactory init() {
        // Creating Configuration Instance & Passing Hibernate Configuration File
        Configuration configObj = new Configuration();
        configObj.configure("hibernate.cfg.xml");

        // Since Hibernate Version 4.x, ServiceRegistry Is Being Used
        ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();

        // Creating Hibernate SessionFactory Instance
        sessionFactoryObj = configObj.buildSessionFactory(serviceRegistryObj);
        return sessionFactoryObj;
    }

	/**main方法*/
	public static void main(String[] args) {
	    init();
	    Solution1 s1 = new Solution1(sessionFactoryObj);
	    Solution2 s2 = new Solution2(sessionFactoryObj);
        Solution3 s3 = new Solution3(sessionFactoryObj);
        Solution4 s4 = new Solution4(sessionFactoryObj);

        System.out.println(".......开始进行您的操作.......\n");

        /**运行某个题目需要注释掉之前的,如下，因为每个里面都是无限循环*/
		/**第一题——进行查询、订购、退订等操作*/
		//s1.Q1();

		/**第二题——某个用户在通话情况下的资费生成*/
		//s2.Q2();

        /**第三题——某个用户在使用流量情况下的资费生成*/
        //s3.Q3();

        /**第四题——本月账单*/
        //s4.Q4();

	}
}
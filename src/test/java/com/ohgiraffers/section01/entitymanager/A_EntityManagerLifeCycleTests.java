package com.ohgiraffers.section01.entitymanager;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class A_EntityManagerLifeCycleTests {

    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeAll //모든 테스트 수행하기 전에 딱 한번
    public static void initFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpatest");
    }

    @BeforeEach //테스트가 수행 되기 전마다 한번씩
    public void initManager() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterAll //모든 테스트 수행하기 전에 딱 한번
    public static void closeFactory() {
        entityManagerFactory.close();
    }

    @AfterEach //테스트가 수행 되기 전마다 한번씩
    public void closeManager() {
        entityManager.close();
    }

    @Test
    public void 엔터티_매니저_팩토리와_엔터티_매니저_생명주기_확인1() {
        System.out.println("entityManagerFactory.hashCode : " + entityManagerFactory.hashCode());
        System.out.println("entityManager.hashCode : " + entityManager.hashCode());
    }

    @Test
    public void 엔터티_매니저_팩토리와_엔터티_매니저_생명주기_확인2() {
        System.out.println("entityManagerFactory.hashCode : " + entityManagerFactory.hashCode());
        System.out.println("entityManager.hashCode : " + entityManager.hashCode());
    }

    /*
    팩토리는 해쉬코드가 같고 매니저는 해쉬코드가 다르다.
    엔터티 매니저 팩토리와 엔터티 매니저의 라이프 사이클을 알아본다.
    entityManagerFactory.hashCode : 1491779598
    entityManager.hashCode : 1792015149
    entityManagerFactory.hashCode : 1491779598
    entityManager.hashCode : 1839529707
    */

}

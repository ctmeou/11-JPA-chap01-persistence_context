package com.ohgiraffers.section02.crud;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class A_EntityManagerCRUDTests {

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
    public void 메뉴코드로_메뉴_조회_테스트() {

        // given
        int menuCode = 2;

        // when
        Menu foundMenu = entityManager.find(Menu.class, menuCode);

        // then
        Assertions.assertNotNull(foundMenu);
        Assertions.assertEquals(menuCode, foundMenu.getMenuCode());
        System.out.println("foundMenu = " + foundMenu);

    }

    @Test
    public void 새로운_메뉴_추가_테스트() {

        // given
        Menu menu = new Menu();
        menu.setMenuName("JPA 테스트용 신규 메뉴");
        menu.setMenuPrice(5000);
        menu.setCategoryCode(4);
        menu.setOrderableStatus("Y");

        // when
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            entityManager.persist(menu);
            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
            e.printStackTrace(); //예외 발생 당시의 호출스택에 있던 메소드의 정보와 예외 결과를 화면에 출력함
        }

        // then
        Assertions.assertTrue(entityManager.contains(menu));

    }

    @Test
    public void 메뉴_이름_수정_테스트() {

        // given
        Menu menu = entityManager.find(Menu.class, 2);
        System.out.println("menu = " + menu);
        String menuNameToChange = "갈치스무디";

        // when
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            menu.setMenuName(menuNameToChange);
            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
            e.printStackTrace();
        }

        // then
        Assertions.assertEquals(menuNameToChange, entityManager.find(Menu.class, 2).getMenuName());

    }


    @Test
    public void 메뉴_삭제하기_테스트() {

        // given
        Menu menuToRemove = entityManager.find(Menu.class, 1);

        // when
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            entityManager.remove(menuToRemove);
            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
            e.printStackTrace();
        }

        // then
        Menu removedMenu = entityManager.find(Menu.class, 1);
        Assertions.assertEquals(null, removedMenu);

    }

}

package com.ohgiraffers.section03.persistencecontext;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

public class A_EntityLifeCycleTests {

    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeAll //모든 테스트 수행하기 전에 딱 한번
    public static void initFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpatest");
    }

    @BeforeEach //테스트가 수행되기 전마다 한번씩
    public void initManager() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterAll //모든 테스트 수행하기 전에 딱 한번
    public static void closeFactory() {
        entityManagerFactory.close();
    }

    @AfterEach //테스트가 수행되기 전마다 한번씩
    public void closeManager() {
        entityManager.close();
    }


    @Test
    public void 비영속성_테스트() {

        // given
        Menu foundMenu = entityManager.find(Menu.class, 11); //영속 엔티티(영속 객체)
        Menu newMenu = new Menu(); //비영속성(비영속 객체)
        newMenu.setMenuCode(foundMenu.getMenuCode());
        newMenu.setMenuName(foundMenu.getMenuName());
        newMenu.setMenuPrice(foundMenu.getMenuPrice());
        newMenu.setCategoryCode(foundMenu.getCategoryCode());
        newMenu.setOrderableStatus(foundMenu.getOrderableStatus());

        // when
        boolean isTrue = (foundMenu == newMenu); //객체의 주소값 비교
        //foundMenu는 영속 내부에 있는 것이고 newMenu는 비영속에 있는 것이기 때문에 주소 값이 같을 수가 없다.

        // then
        assertFalse(isTrue);

    }

    @Test
    public void 영속성_연속_조회_테스트() {

        // give
        /* 똑같은 pk로 조회 -> 둘다 영속 엔티티(find 해왔으니까)
           그렇다면 둘은 별도의 객체일까 같은 객체일까?
           1차 캐시 - Map으로 관리되는 캐시(key(@id이며 매핑한 식별자) - menuCode, value - obj)
           1차 캐시에서 먼저 find해온 객체(foundMenu1)를 찾기 때문에 둘은 같은 객체로 예상할 수 있다. = 같은 해쉬코드를 갖는다. */
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 11);
        //1. 1차 캐시를 확인 2. select 구문 실행(1차 캐시에 없을 경우) 3. 엔터티 객체 생성 및 매핑
        //1. 1차 캐시를 확인 2. 그 객체 반환

        // when
        boolean isTrue = (foundMenu1 == foundMenu2);

        // then
        assertTrue(isTrue);

    }

    @Test
    public void 영속성_객체_추가_테스트() {

        // given
        //비영속 객체
        Menu menuToRegist = new Menu();
        menuToRegist.setMenuCode(500);
        menuToRegist.setMenuName("수박죽");
        menuToRegist.setMenuPrice(10000);
        menuToRegist.setCategoryCode(1);
        menuToRegist.setOrderableStatus("Y");

        // when
        entityManager.persist(menuToRegist);
        Menu foundMenu = entityManager.find(Menu.class, 500);
        boolean isTrue = (menuToRegist == foundMenu);
        //menuToRegist가 persist로 1차 캐시에 저장되었기 때문에
        //foundMenu를 엔터티매니저로 find할 경우 1차 캐시에 있는 menuToRegist를 찾아오기 때문에 둘은 같다.

        // then
        assertTrue(isTrue);

    }

    @Test
    public void 영속성_객체_추가_값_변경_테스트() {

        // given
        Menu menuToRegist = new Menu();
        menuToRegist.setMenuCode(500);
        menuToRegist.setMenuName("수박죽");
        menuToRegist.setMenuPrice(10000);
        menuToRegist.setCategoryCode(1);
        menuToRegist.setOrderableStatus("Y");

        // when
        entityManager.persist(menuToRegist);
        menuToRegist.setMenuName("메론죽");
        Menu foundMenu = entityManager.find(Menu.class, 500);
        //foundMenu.getMenuName() = 메론죽

        // then
        assertEquals("메론죽", foundMenu.getMenuName());

    }

    @Test
    public void 준영속성_detach_테스트() {

        // given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        // when
        entityManager.detach(foundMenu2);
        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);

        // then
        assertEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice());
        assertEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());

    }

    @Test
    public void 준영속성_clear_테스트() {

        // given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        // when
        entityManager.clear();
        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);
        // 객체는 메모리상 존재하지만 영속성에서 관리되지 않는 상태이다.

        // then
        assertEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice()); //오류 발생 시작
        assertEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());

    }

    @Test
    public void 준영속성_close_테스트() {

        // given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        // when
        entityManager.close();
        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);

        // then
        assertEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice()); //오류 발생 시작
        assertEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());

    }

    @Test
    public void 삭제_remove_테스트() {

        // given
        Menu foundMenu = entityManager.find(Menu.class, 2);

        // when
        entityManager.remove(foundMenu);
        //온전히 삭제하겠다. 라는 의미 => 다시 select 하지 않는다. null값 o
        Menu refoundMenu = entityManager.find(Menu.class, 2);

        // then
        assertEquals(2, foundMenu.getMenuCode());
        assertEquals(null, refoundMenu);

    }

    @Test
    public void 병합_merge_수정_테스트() {

        // given
        Menu menuToDetach = entityManager.find(Menu.class, 2);
        entityManager.detach(menuToDetach);

        // when
        menuToDetach.setMenuName("수박죽");
        Menu refoundMenu = entityManager.find(Menu.class, 2);
        entityManager.merge(menuToDetach);

        // then
        Menu mergedMenu = entityManager.find(Menu.class, 2);
        assertEquals("수박죽", mergedMenu.getMenuName());
        //detach 됐던 객체를 다시 영속성 관리를 하는 것(merge)
        //=> 수박죽

    }

    @Test
    public void 병합_merge_삽입_테스트() {

        // given
        Menu menuToDetach = entityManager.find(Menu.class, 2);
        entityManager.detach(menuToDetach);

        // when
        menuToDetach.setMenuCode(999); //DB에서 조회할 수 없는 키 값으로 변경 => 존재하지 않을 경우에는 새롭게 INSERT된다.
        menuToDetach.setMenuName("수박죽");
        entityManager.merge(menuToDetach); //영속 상태의 엔티티와 병합해야 하지만 존재하지 않을 경우 삽입 된다.

        //then
        Menu mergedMenu = entityManager.find(Menu.class, 999);
        assertEquals("수박죽", mergedMenu.getMenuName());

    }
}

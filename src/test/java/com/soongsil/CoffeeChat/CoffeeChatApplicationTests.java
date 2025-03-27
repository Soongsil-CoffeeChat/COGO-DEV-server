package com.soongsil.CoffeeChat;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.global.config.QueryDSLConfig;

@SpringBootTest
@Transactional
@Import(QueryDSLConfig.class)
// @Commit
class CoffeeChatApplicationTests {

    @Autowired private EntityManager em;

    @Autowired private JPAQueryFactory queryFactory;
    /*
    @Test
    void contextLoads() {
    	PossibleDate pd = new PossibleDate();
    	em.persist(pd);
    	JPAQueryFactory query=new JPAQueryFactory(em);
    	QPossibleDate qpd=new QPossibleDate("possibleDate");

    	PossibleDate result = queryFactory
    			.selectFrom(qpd)
    			.fetchOne();

    	Assertions.assertThat(result).isEqualTo(pd);
    	Assertions.assertThat(result.getId()).isEqualTo(pd.getId());
    }

    */
}

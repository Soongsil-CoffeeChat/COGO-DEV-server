package com.soongsil.CoffeeChat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.config.queryDSL.QueryDSLConfig;
import com.soongsil.CoffeeChat.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@Transactional
@Import(QueryDSLConfig.class)
//@Commit
class CoffeeChatApplicationTests {

	@Autowired
	private EntityManager em;

	@Autowired
	private JPAQueryFactory queryFactory;
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


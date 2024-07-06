package com.soongsil.CoffeeChat.QueryTest;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.config.queryDSL.QueryDSLConfig;
import com.soongsil.CoffeeChat.entity.QMentor;
import com.soongsil.CoffeeChat.entity.QUser;
import com.soongsil.CoffeeChat.entity.User;
import jakarta.persistence.EntityManager;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.soongsil.CoffeeChat.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.entity.QUser.user;

@SpringBootTest
@Import(QueryDSLConfig.class)
public class GetMentorListTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    public void getMentorListByJoin(){  //일반 join으로 멘토 리스트 불러오기 테스트

        String searchPart="BE";
        List<Tuple> result= queryFactory
                .select(user.role, mentor.id)
                .from(user)
                .join(user.mentor, mentor)
                .where(mentor.part.eq(searchPart))
                .fetch();
        System.out.println("result"+ result.get(0)+", "+result.get(499));
    }

    @Test
    public void getMentorListByFetch() {
        String searchPart = "BE";

        QUser user = QUser.user;
        QMentor mentor = QMentor.mentor;

        // Fetch join을 사용하여 엔티티를 로드
        List<User> users = queryFactory
                .selectFrom(user)
                .join(user.mentor, mentor).fetchJoin()
                .where(mentor.part.eq(searchPart))
                .fetch();

        // 필요한 필드만 추출하여 DTO로 변환
        List<UserMentorDTO> result = users.stream()
                .map(u -> new UserMentorDTO(u.getRole(), u.getMentor().getId()))
                .toList();

        // 결과 크기 출력
        System.out.println("Result size: " + result.size());

        // 결과가 충분히 존재하는지 확인
        if (result.size() > 0) {
            System.out.println("First result: " + result.get(0));
        }
        if (result.size() > 499) {
            System.out.println("500th result: " + result.get(499));
        }
    } /*
        String part="BE";
        List<Tuple> result= queryFactory
                .select(user.name, mentor.id)
                .from(user)
                .join(user.mentor, mentor).fetchJoin()
                .where(mentor.part.eq(part))
                .fetch();
        System.out.println("result"+ result.get(0)+", "+result.get(499));
    }
*/


}

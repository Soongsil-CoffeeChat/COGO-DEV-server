package com.soongsil.CoffeeChat.aspect;

import com.soongsil.CoffeeChat.repository.ApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Aspect
@Slf4j
public class ApplicationCleanupAspect {

    private final ApplicationRepository applicationRepository;

    public ApplicationCleanupAspect(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteExpiredApplications() {
        log.info("[*] Deleting expired applications.. ");
        // 매일 자정에 현재 시간보다 이전인 COGO 삭제
        applicationRepository.deleteExpiredApplications();
    }
}

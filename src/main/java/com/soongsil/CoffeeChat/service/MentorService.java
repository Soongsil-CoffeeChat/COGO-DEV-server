package com.soongsil.CoffeeChat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


import com.soongsil.CoffeeChat.dto.PerformanceRequest;
import com.soongsil.CoffeeChat.dto.PerformanceResult;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

@Service
public class MentorService {
	private final MentorRepository mentorRepository;
	private final UserRepository userRepository;
	private final PossibleDateRepository possibleDateRepository;
	private final ThreadPoolTaskExecutor executor;

	public MentorService(MentorRepository mentorRepository,
						 UserRepository userRepository, PossibleDateRepository possibleDateRepository, @Qualifier("performanceExecutor")ThreadPoolTaskExecutor executor) {
		this.mentorRepository = mentorRepository;
		this.userRepository = userRepository;
		this.possibleDateRepository=possibleDateRepository;
		this.executor=executor;
	}

	public PerformanceResult executePerformanceTest(int apiNum, PerformanceRequest request) {
		int userCount = request.getUserCount();
		int totalRequests = request.getTotalRequests();
		System.out.println("totalRequests = " + totalRequests);

		List<Future<List<Long>>> futures = new ArrayList<>();

		for (int i = 0; i < userCount; i++) {
			futures.add(executor.submit(() -> {
				List<Long> executionTimes = new ArrayList<>();

				for (int j = 0; j < totalRequests; j++) {
					long startTime = System.currentTimeMillis();
					switch(apiNum){
						case 1: getMentorDtoListByPartWithJpa("BE"); break;
						case 2: getMentorDtoListByPart("BE"); break;
						case 3: getMentorDtoListByPartWithFetch("BE"); break;
					}
					System.out.println("apiNum = " + apiNum);

					long endTime = System.currentTimeMillis();
					executionTimes.add(endTime - startTime);  // 각 요청의 실행 시간 기록
				}

				return executionTimes;
			}));
		}

		List<Double> averageTimesPerThread = new ArrayList<>();
		List<Long> allExecutionTimes = new ArrayList<>();

		// 대기 중인 모든 스레드가 완료될 때까지 기다림
		for (Future<List<Long>> future : futures) {
			try {
				List<Long> executionTimes = future.get();
				allExecutionTimes.addAll(executionTimes);

				// 스레드별 평균 실행 시간 계산
				double averageTimePerThread = executionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
				averageTimesPerThread.add(averageTimePerThread);

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		// 전체 스레드의 평균 실행 시간 계산
		double overallAverageTime = allExecutionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

		return new PerformanceResult(averageTimesPerThread, overallAverageTime);
	}

	@PreDestroy
	public void shutdownExecutor() {
		executor.shutdown();
		try {
			if (!executor.getThreadPoolExecutor().awaitTermination(60, TimeUnit.SECONDS)) {
				executor.getThreadPoolExecutor().shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.getThreadPoolExecutor().shutdownNow();
			Thread.currentThread().interrupt();
		}
	}




	@Transactional
	public List<ResponseMentorListInfo> getMentorDtoListByPart(String part) {
		return mentorRepository.getMentorListByPart(part); //일반join
	}

	@Transactional
	public List<ResponseMentorListInfo> getMentorDtoListByPartWithFetch(String part) {
		//fetch join
		List<ResponseMentorListInfo> dtos=new ArrayList<>();
		List<User> users=mentorRepository.getMentorListByPart2(part);
		for(User user:users){
			dtos.add(ResponseMentorListInfo.toDto(user.getMentor(), user));
		}
		return dtos;
	}

	@Transactional
	public List<ResponseMentorListInfo> getMentorDtoListByPartWithJpa(String part){
		List<ResponseMentorListInfo> dtos=new ArrayList<>();
		List<Mentor> mentors=mentorRepository.findAllByPart(part);
		for(Mentor mentor:mentors){
			dtos.add(ResponseMentorListInfo.toDto(mentor, userRepository.findByMentor(mentor)));
		}
		return dtos;
	}

	public List<PossibleDateRequestDto> findPossibleDateListByMentor(String username) {
		return possibleDateRepository.getPossibleDatesByUsername(username);
	}

	@Transactional
	public Mentor saveUserPicture(String username, String picture){
		User user = userRepository.findByUsername(username);
		Mentor mentor = user.getMentor();
		mentor.setPicture(picture);
		return mentorRepository.save(mentor);
	}
}

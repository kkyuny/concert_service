package com.hhdplus.concert_service.business.service;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.repository.ConcertRepository;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
public class ConcertService {

    static Logger LOGGER = LoggerFactory.getLogger(ConcertService.class);

    private final ConcertRepository concertRepository;

    public List<LocalDateTime> findAvailableDates(Long concertId) {
        // 콘서트 ID로 예약 가능한 날짜를 조회
        return concertRepository.findAvailableDatesByConcertId(concertId)
                .stream()
                .map(ConcertDomain::getConcertDate)
                .collect(Collectors.toList());
    }

    public List<Long> findReservedSeats(Long concertId, LocalDateTime concertDate) {
        // 예약된 좌석 번호를 조회
        List<Long> reservedSeats = concertRepository.findReservedSeatsByConcertIdAndDate(concertId, concertDate)
                .stream()
                .map(ConcertDomain::getSeatNo)
                .toList();

        // 좌석 번호 1~50 중 예약되지 않은 좌석 번호 반환
        return LongStream.rangeClosed(1, 50)
                .filter(seatNo -> !reservedSeats.contains(seatNo))
                .boxed()
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean reserveSeat(Long concertId, LocalDateTime concertDate, Long userId, Long seatNo) {
        // 좌석 예약
        if (concertRepository.existsByConcertIdAndDateAndSeatNo(concertId, concertDate, seatNo)) {
            return false; // 좌석이 이미 예약되어 있는 경우
        }

        ConcertDomain reservedSeat = ConcertDomain.builder()
                .concertId(concertId)
                .concertDate(concertDate)
                .seatNo(seatNo)
                .status("reserved")
                .userId(userId)
                .build();
        try{
            concertRepository.saveConcertReservation(reservedSeat);

            return true; // 예약 성공
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("Seat reservation error", e);

            return false;
        }
    }

    public Optional<ConcertDomain> getUserReservation(ConcertDomain domain){
        Long concertId = domain.getConcertId();
        LocalDateTime concertDate = domain.getConcertDate();
        Long seatNo = domain.getSeatNo();

        return concertRepository.getUserReservation(concertId, concertDate, seatNo);
    }

    @Transactional
    public void changeConcertReserveToFinish(ConcertDomain concertDomain) {
        try{
            concertDomain.setStatus("paid");
            concertRepository.saveConcertReservation(concertDomain);
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("Reservation status change error", e);
        }
    }

    public ConcertDomain findConcertReservation(Long id){
        Optional<ConcertDomain> result = concertRepository.findConcertReservation(id);
        if(result.isPresent()){
            return result.get();
        } else {
            throw new InvalidReqBodyException("Concert reservation not found");
        }
    }
}

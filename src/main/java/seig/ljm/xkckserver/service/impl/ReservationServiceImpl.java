package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.Reservation;
import seig.ljm.xkckserver.mapper.ReservationMapper;
import seig.ljm.xkckserver.service.ReservationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    private ReservationMapper reservationMapper;
    @Autowired
    public ReservationServiceImpl(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    @Override
    public Page<Reservation> getVisitorReservations(Integer visitorId, Integer pageNum, Integer pageSize) {
        Page<Reservation> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Reservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("visitor_id", visitorId)
                   .orderByDesc("create_time");
        return page(page, queryWrapper);
    }

    @Override
    public Page<Reservation> getReservationPage(Integer pageNum, Integer pageSize, String status, LocalDateTime startTime, LocalDateTime endTime) {
        Page<Reservation> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Reservation> queryWrapper = new QueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        
        if (startTime != null && endTime != null) {
            queryWrapper.between("start_time", startTime, endTime);
        }
        
        queryWrapper.orderByDesc("create_time");
        return page(page, queryWrapper);
    }

    @Override
    public boolean createReservation(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now();
        reservation.setCreateTime(now);
        reservation.setUpdateTime(now);
        reservation.setStatus("pending");
        return save(reservation);
    }

    @Override
    public boolean cancelReservation(Integer reservationId) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setStatus("canceled");
        reservation.setUpdateTime(LocalDateTime.now());
        return updateById(reservation);
    }

    @Override
    public boolean confirmReservation(Integer reservationId) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setHostConfirm("confirmed");
        reservation.setStatus("confirmed");
        reservation.setUpdateTime(LocalDateTime.now());
        return updateById(reservation);
    }

    @Override
    public boolean rejectReservation(Integer reservationId) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setHostConfirm("rejected");
        reservation.setStatus("rejected");
        reservation.setUpdateTime(LocalDateTime.now());
        return updateById(reservation);
    }
}

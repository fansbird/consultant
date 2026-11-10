package com.info.consultant.service;

import com.info.consultant.mapper.ReservationMapper;
import com.info.consultant.pojo.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    @Autowired
    private ReservationMapper reservationMapper;
    //添加预约
    public void insert(Reservation reservation){
        reservationMapper.insert(reservation);
    }
    //查询预约
    public Reservation findbyPhone(String phone) {
        return reservationMapper.findbyPhone(phone);
    }

}

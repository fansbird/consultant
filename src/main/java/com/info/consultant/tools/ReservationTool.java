package com.info.consultant.tools;

import com.info.consultant.pojo.Reservation;
import com.info.consultant.service.ReservationService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationTool {
    @Autowired
    private ReservationService reservationService;

    //添加预约信息
    @Tool("预约志愿填报服务")
    public void addReservation(
            @P("考生姓名")String name,
            @P("考生性别")String gender,
            @P("考生手机号")String phone,
            @P("考生时间，格式为yyyy-MM-dd HH:mm:ss")LocalDateTime communicationTime,
            @P("考生省份")String province,
            @P("考生志愿分数")Integer estimatedScore
    ) {
        Reservation reservation = new Reservation(null, name, gender, phone, communicationTime, province, estimatedScore);
        reservationService.insert(reservation);
    }
    //查询预约信息
    @Tool("查询预约志愿填报服务")
    public Reservation findReservation(@P("考生手机号")String phone) {
        return reservationService.findbyPhone(phone);
    }
}

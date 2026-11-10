package com.info.consultant.mapper;

import com.info.consultant.pojo.Reservation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReservationMapper {
    //添加预约
    @Insert("insert into reservation(name,gender,phone,communication_time,province,estimated_score) values(#{name},#{gender},#{phone},#{communicationTime},#{province},#{estimatedScore})")
    public void insert(Reservation reservation);

    //查询预约
    @Select("select * from reservation where phone=#{phone}")
    public Reservation findbyPhone(String phone);

}

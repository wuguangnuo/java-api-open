package com.xyj.api.model.test;

import com.xyj.api.dto.LocData;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * MongoDB UserLocation
 */
@Data
@Accessors(chain = true)
@Document(collection = "UserLocation", value = "UserLocation")
public class UserLocation implements Serializable {
    // 服务时间  时间戳
    @Field("StartTime")
    private long startTime;
    // 员工id
    @Field("EmployeeId")
    private int employeeId;
    // 订单号  初始化地址默认订单号 为 “0”
    @Field("BillNo")
    private String billNo;
    // 日期  时间戳
    @Field("Day")
    private long day;
    // 手机
    @Field("Phone")
    private String phone;
    // 所属门店
    @Field("StoreId")
    private int storeId;
    // 是否可谓队长
    @Field("IsLeader")
    private boolean isLeader;
    // 员工编号
    @Field("No")
    private String no;
    // 真实姓名
    @Field("RealName")
    private String realName;
    // 中文地址
    @Field("addr")
    private String addr;
    // 是否自动派单的   false  否   true  是
    @Field("isauto")
    private boolean isAuto;
    // 地理位置坐标经纬度
    @Field("Loc")
    private LocData loc;
}

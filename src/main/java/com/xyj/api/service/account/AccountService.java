package com.xyj.api.service.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.xyj.api.dto.account.LoginDto;
import com.xyj.api.dto.account.RegisterDto;
import com.xyj.api.enums.RedisPrefixKeyEnum;
import com.xyj.api.enums.sys.SmsType;
import com.xyj.api.model.ApiRes;
import com.xyj.api.model.account.EmployeeView;
import com.xyj.api.model.account.LoginModel;
import com.xyj.api.service.IBaseService;
import com.xyj.api.utils.EncryptUtil;
import com.xyj.api.utils.RedisUtil;
import com.xyj.api.dto.account.LoginPhoneDto;
import com.xyj.api.entity.*;
import com.xyj.api.enums.employee.EmployeeLevelEnum;
import com.xyj.api.enums.employee.SexEnum;
import com.xyj.api.enums.employee.WorkingPropertysEnum;
import com.xyj.api.enums.employee.EmployeeStateEnum;
import com.xyj.api.enums.employee.EmployeeTypeEnum;
import com.xyj.api.mapper.*;
import com.xyj.api.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AccountService implements IBaseService {
    /**
     * 过期时间, 分钟
     */
    private static final int expireTime = 90000;

    private final EmployeeMapper employeeMapper;
    private final BaomuAbilityMapper baomuAbilityMapper;
    private final CityAreaMapper cityAreaMapper;
    private final StoreMapper storeMapper;
    private final DepartmentMapper departmentMapper;
    private final EmployeeInfoMapper employeeInfoMapper;
    private final SiteMapper siteMapper;

    private final RedisUtil redisUtil;
    private final EncryptUtil encryptUtil;
    private final DateUtil dateUtil;

    @Autowired
    public AccountService(EmployeeMapper employeeMapper, BaomuAbilityMapper baomuAbilityMapper, RedisUtil redisUtil, CityAreaMapper cityAreaMapper, StoreMapper storeMapper, DepartmentMapper departmentMapper, EmployeeInfoMapper employeeInfoMapper, SiteMapper siteMapper, EncryptUtil encryptUtil, DateUtil dateUtil) {
        this.employeeMapper = employeeMapper;
        this.baomuAbilityMapper = baomuAbilityMapper;
        this.redisUtil = redisUtil;
        this.cityAreaMapper = cityAreaMapper;
        this.storeMapper = storeMapper;
        this.departmentMapper = departmentMapper;
        this.employeeInfoMapper = employeeInfoMapper;
        this.siteMapper = siteMapper;
        this.encryptUtil = encryptUtil;
        this.dateUtil = dateUtil;
    }

    /**
     * 工号密码登录
     *
     * @param loginDto
     * @return
     */
    public ApiRes<LoginModel> login(LoginDto loginDto) {
        Employee emp = employeeMapper.selectOne(
                new QueryWrapper<Employee>().lambda()
                        .eq(Employee::getNo, loginDto.getAccount())
        );
        if (emp == null) {
            return ApiRes.fail("帐号不存在");
        }
        String enctrypPassword = encryptUtil.EncryptString(loginDto.getPassword());
        if (!emp.getPassword().equals(enctrypPassword)) {
            return ApiRes.fail("帐号密码不正确");
        }

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisUtil.set(token, RedisPrefixKeyEnum.ELogin.toString(), emp.getId() + ":" + emp.getNo(), expireTime);

        boolean isNotPwd = Strings.isNullOrEmpty(emp.getPassword());

        LoginModel loginData = new LoginModel();
        Date dateNow = new Date();

        Date dateAfter = dateUtil.dateAddMinutes(dateNow, expireTime);

        loginData.setPhone(emp.getPhone())
                .setAccount(emp.getNo())
                .setGrade(String.valueOf(emp.getLevel()))
                .setNotPassword(isNotPwd)
                .setHeadImg(emp.getHeadPortrait())
                .setFailureTime(dateAfter)
                .setToken(token);

        return ApiRes.suc(loginData);
    }

    /**
     * 手机号快速登录
     *
     * @param dto
     * @return
     */
    public ApiRes<LoginModel> loginByPhone(LoginPhoneDto dto) {
        List<Employee> employee = employeeMapper.selectList(
                new QueryWrapper<Employee>().lambda()
                        .eq(Employee::getPhone, dto.getPhoneNumber())
        );
        if (employee == null || employee.size() == 0) return ApiRes.err("该手机号未注册");
        if (employee.size() != 1) return ApiRes.err("账号异常");

//        测试 Redis set
//        redisUtil.set(phoneNumber + "_" + 1, RedisPrefixKeyEnum.Sms.toString(), 8888);

        String code = redisUtil.get(dto.getPhoneNumber() + "_" + SmsType.TelLogin.toString(), RedisPrefixKeyEnum.Sms.toString());
        if (Strings.isNullOrEmpty(code) || !code.replaceAll("\"", "").equals(dto.getPhoneCode().replaceAll("\"", "")))
            return ApiRes.err("验证码错误");
        Employee emp = employee.get(0);
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisUtil.set(token, RedisPrefixKeyEnum.ELogin.toString(), emp.getId() + ":" + emp.getNo(), expireTime);

        LoginModel login = new LoginModel()
                .setPhone(dto.getPhoneNumber())
                .setAccount(emp.getNo())
                .setGrade(String.valueOf(emp.getLevel()))
                .setNotPassword(Strings.isNullOrEmpty(emp.getPassword()))
                .setHeadImg(emp.getHeadPortrait())
                .setFailureTime(dateUtil.dateAddMinutes(new Date(), expireTime))
                .setToken(token);

        return ApiRes.suc("登录成功", login);
    }

    /**
     * 员工/保姆注册
     *
     * @param dto
     * @return
     */
    public ApiRes<EmployeeView> register(RegisterDto dto) {

        Employee emp = employeeMapper.selectOne(
                new QueryWrapper<Employee>().lambda()
                        .eq(Employee::getPhone, dto.getPhone())
                        .eq(Employee::getEmployeeType, EmployeeTypeEnum.服务员工.getValue()));
        if (emp != null) {
            return ApiRes.fail("该手机已经注册");
        }
        String code = redisUtil.get(dto.getPhone() + "_" + SmsType.Register.toString(), RedisPrefixKeyEnum.Sms.toString());
        if (Strings.isNullOrEmpty(code) || !code.replaceAll("\"", "").equals(dto.getCode().replaceAll("\"", ""))) {
            return ApiRes.fail("验证码错误");
        }

        Employee employee = new Employee();
        // 设置员工NO
        if (dto.getRegisterType() == 0) {
            Employee user = employeeMapper.selectList(
                    new QueryWrapper<Employee>().lambda()
                            .likeRight(Employee::getNo, dto.getAreaCode())
                            .orderByDesc(Employee::getNo)).get(0);
            if (user == null) {
                employee.setNo(dto.getAreaCode() + "000001");
            } else {
                int nol = user.getNo().length();
                employee.setNo(dto.getAreaCode() + String.format("%06d", Integer.valueOf(user.getNo().substring(nol - 6, nol)) + 1));
            }
        } else {
            Employee user = employeeMapper.selectList(
                    new QueryWrapper<Employee>().lambda()
                            .likeRight(Employee::getNo, "BM")
                            .orderByDesc(Employee::getNo)).get(0);
            if (user == null) {
                employee.setNo("BM00000001");
            } else {
                employee.setNo("BM" + String.format("%08d", Integer.valueOf(user.getNo().replace("BM", "")) + 1));
            }
        }

        employee.setAddress(dto.getAddress())
                .setCityId(dto.getCityId())
                .setIntroducer(dto.getIntroducer())
                .setRealName(dto.getRealName())
                .setPhone(dto.getPhone())
                .setWorkingLifeDate(dto.getWorkingLifeDate())
                .setAmount(0d)
                .setPassword(encryptUtil.EncryptString("123456"))
                .setIsLeader(false).setState(EmployeeStateEnum.下架.getValue())
                .setIsSys(false)
                .setCreateDate(LocalDateTime.now())
                .setIsFirstSingle(false)
                .setMentionAmount(0d)
                .setFreezeAmount(0d)
                .setAmount(0d)
                .setEntryTime(LocalDateTime.now())
                .setUpdateDate(LocalDateTime.now())
                .setIsAutoPd(true)
                .setIsDriver(false)
                .setLevel(EmployeeLevelEnum.一星.getValue())
                .setEmployeeType(EmployeeTypeEnum.服务员工.getValue())
                .setWorkingProperty(WorkingPropertysEnum.全职.getValue());

        CityArea cityArea = cityAreaMapper.selectOne(
                new QueryWrapper<CityArea>().lambda()
                        .eq(CityArea::getAreaCode, dto.getAreaCode()));
        employee.setAreaId(cityArea.getId())
                .setCityId(cityArea.getCityId());

        Store store = storeMapper.selectList(
                new QueryWrapper<Store>().lambda().
                        eq(Store::getCityId, cityArea.getCityId())
                        .orderByAsc(Store::getId)).get(0);
        // 门店部门
        Department department = departmentMapper.selectList(
                new QueryWrapper<Department>().lambda()
                        .eq(Department::getStoreId, store.getId())
                        .orderByAsc(Department::getId)).get(0);
        if (department == null) {
            department.setId(0);
        }
        employee.setStoreId(store.getId())
                .setDepartId(department.getId());
        Site site = siteMapper.selectList(
                new QueryWrapper<Site>().lambda()
                        .eq(Site::getAreaId, cityArea.getId())).get(0);
        employee.setSiteId(site.getId());
        if (dto.getRegisterType() == 1) {
            employee.setBaomuWorkType(dto.getBaomuWorkModel())
                    .setWorkingLifeDate(dto.getWorkingLifeDate())
                    .setIsBaomu(true);
        }

        int empInsRes = employeeMapper.insert(employee);
        if (empInsRes != 1) {
            return ApiRes.err("api Insert 新增失败");
        }

        if (dto.getRegisterType() == 1 && dto.getBaomuAbility().size() > 0) {
            baomuAbilityMapper.delete(
                    new QueryWrapper<BaomuAbility>().lambda()
                            .eq(BaomuAbility::getBaomuID, employee.getId())
            );

            for (Integer sa : dto.getBaomuAbility()) {
                baomuAbilityMapper.insert(
                        new BaomuAbility()
                                .setDictID(sa)
                                .setBAvalue(0f)
                                .setBaomuID(employee.getId()));
            }
        }

        EmployeeInfo info = new EmployeeInfo();
        info.setSex(SexEnum.未知.getValue());
        info.setEmployeeId(employee.getId());
        int empInfoRes = employeeInfoMapper.insert(info);
        if (empInfoRes != 1) {
            ApiRes.err("EmployeeInfo Insert 新增失败");
        }

        EmployeeView employeeView = new EmployeeView().setNo(employee.getNo()).setPassword("123456");
        return ApiRes.suc(employeeView);
    }
}

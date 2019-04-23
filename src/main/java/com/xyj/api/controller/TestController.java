package com.xyj.api.controller;

import com.baomidou.mybatisplus.core.toolkit.IOUtils;
import com.google.code.kaptcha.Producer;
import com.google.common.base.Strings;
import com.xyj.api.dto.LocData;
import com.xyj.api.dto.test.ImageDto;
import com.xyj.api.dto.test.ThumbnailatorDto;
import com.xyj.api.enums.employee.EmployeeStateEnum;
import com.xyj.api.enums.employee.EmployeeTypeEnum;
import com.xyj.api.model.ApiRes;
import com.xyj.api.model.test.EmailInfo;
import com.xyj.api.service.IBaseService;
import com.xyj.api.dto.test.UploadAddr;
import com.java.api.utils.*;
import com.xyj.api.entity.Employee;
import com.xyj.api.mapper.EmployeeMapper;
import com.xyj.api.model.test.UserLocation;
import com.xyj.api.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@Api(value = "测试controller", tags = {"仅供测试"})
@RequestMapping(value = "test")
public class TestController implements IBaseService {

    private final OSSClientUtil ossClientUtil;
    private final TencentAIUtil tencentAIUtil;
    private final EmailUtil emailUtil;
    private final BaiduAIUtil baiduAIUtil;
    private final ThumbnailsUtil thumbnailsUtil;
    private final ImageUtils imageUtils;
    private final PDFUtil pdfUtil;
    private final EmployeeMapper employeeMapper;
    private final MongoTemplate mongoTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final Producer producer;

    @Autowired
    public TestController(OSSClientUtil ossClientUtil, TencentAIUtil tencentAIUtil, EmailUtil emailUtil, BaiduAIUtil baiduAIUtil, ThumbnailsUtil thumbnailsUtil, ImageUtils imageUtils, PDFUtil pdfUtil, EmployeeMapper employeeMapper, MongoTemplate mongoTemplate, Producer producer, RabbitTemplate rabbitTemplate) {
        this.ossClientUtil = ossClientUtil;
        this.tencentAIUtil = tencentAIUtil;
        this.emailUtil = emailUtil;
        this.baiduAIUtil = baiduAIUtil;
        this.thumbnailsUtil = thumbnailsUtil;
        this.imageUtils = imageUtils;
        this.pdfUtil = pdfUtil;
        this.employeeMapper = employeeMapper;
        this.mongoTemplate = mongoTemplate;
        this.producer = producer;
        this.rabbitTemplate = rabbitTemplate;
    }

    private static long timestamp = System.currentTimeMillis() / 1000;

    @PostMapping(value = "/classify")
    @ApiOperation(value = "人像分割")
    public String classify(@RequestBody ImageDto dto) {

        String[] imgs = dto.getImg().split(",");
        if (imgs.length != 2) return "图片需要含URI";

        String base64 = baiduAIUtil.classify(imgs[1]);
        if (Strings.isNullOrEmpty(base64)) return "图片错误，识别失败";

        String fileName = "Temp/" + "baiduAiClassify-" + UUID.randomUUID().toString().replaceAll("-", "");
        String name = ossClientUtil.uploadImg2Oss(imgs[0] + "," + base64, fileName);
        String imgUrl = ossClientUtil.getImgUrl(name);
        if (Strings.isNullOrEmpty(imgUrl) || !imgUrl.contains("?")) return "图片保存错误(处理后图片)";
        String[] imgUrls = imgUrl.split("[?]");

        String fileNameOrigin = "Temp/" + "baiduAiClassify-" + UUID.randomUUID().toString().replaceAll("-", "");
        String nameOrigin = ossClientUtil.uploadImg2Oss(dto.getImg(), fileNameOrigin);
        String imgUrlOrigin = ossClientUtil.getImgUrl(nameOrigin);
        if (Strings.isNullOrEmpty(imgUrlOrigin) || !imgUrlOrigin.contains("?")) return "图片保存错误(原图)";
        String[] imgUrlsOrigin = imgUrlOrigin.split("[?]");

        return "完成，处理后图片: \n" + imgUrls[0] + "\n原图: \n" + imgUrlsOrigin[0];
    }

    @PostMapping(value = "/uploadImg")
    @ApiOperation(value = "上传图片到OSS")
    public String uploadImg(@RequestBody ImageDto dto) {
        String[] imgs = dto.getImg().split(",");
        if (imgs.length != 2) return "图片需要含URI";

        String fileName = "Temp/" + "uploadImgTest-" + UUID.randomUUID().toString().replaceAll("-", "");
        String name = ossClientUtil.uploadImg2Oss(dto.getImg(), fileName);
        String imgUrl = ossClientUtil.getImgUrl(name);
        if (Strings.isNullOrEmpty(imgUrl) || !imgUrl.contains("?")) return "图片保存错误";
        String[] imgUrls = imgUrl.split("[?]");

        return "上传成功，图片链接: \n" + imgUrls[0];
    }

    @PostMapping(value = "uploadFile")
    @ApiOperation(value = "上传文件到OSS")
    public String uploadFile(@ApiParam(value = "上传文件", required = true) MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename().toLowerCase();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        String name = "Temp/" + UUID.randomUUID().toString().replaceAll("-", "") + suffix;
        String fileUrl;

        ossClientUtil.uploadFile2OSS(file.getInputStream(), name);
        if (Strings.isNullOrEmpty(name)) return "上传失败";

        fileUrl = ossClientUtil.getUrl(name);
        if (!fileUrl.startsWith("http")) return "获取失败";

        return "文件上传：\n" + fileUrl.substring(0, fileUrl.indexOf('?'));
    }

    @GetMapping(value = "/downloadImg")
    @ApiOperation(value = "下载图片")
    public String downloadImg(String imgUrl) {
        String uri = "data:" + ossClientUtil.getcontentType(imgUrl.substring(imgUrl.lastIndexOf("."))) + ";base64,";
        String a = uri + imageUtils.image2Base64(imgUrl); // 稳定
//        String b = uri + imageUtils.getBase64ByImgUrl(imgUrl); // Toolkit

        return "原图转base64: \n" + a;
    }

    @PostMapping(value = "/thumbnailator")
    @ApiOperation(value = "图片压缩(jpg)")
    public String thumbnailator(@RequestBody ThumbnailatorDto dto) {
        if (dto.getScale() > 1 || dto.getScale() < 0) return "Scale 输入不合法";
        if (dto.getScale() == 0) dto.setScale(0.5);
        String uri = "data:" + ossClientUtil.getcontentType(dto.getImgUrl().substring(dto.getImgUrl().lastIndexOf("."))) + ";base64,";

        return "压缩后base64: \n" + uri + thumbnailsUtil.imageCompress(dto.getImgUrl(), dto.getScale());
    }

    @GetMapping(value = "/textChat")
    @ApiOperation(value = "智能闲聊(会话每小时重置)")
    public String textChat(String question) {
        if (Strings.isNullOrEmpty(question)) return "说点什么?";
        String answer = tencentAIUtil.textChat(System.currentTimeMillis() / 3600000 + "", question);
        return Strings.isNullOrEmpty(answer) ? "NLP TextChat Error!" : answer;
    }

    @GetMapping(value = "/textTranslate")
    @ApiOperation(value = "中英互译")
    public String textTranslate(String text) {
        if (Strings.isNullOrEmpty(text)) return "";
        String trans = tencentAIUtil.textTranslate(text, 0);
        return Strings.isNullOrEmpty(trans) ? "NLP Translate Error!" : trans;
    }

    @GetMapping(value = "/sendMail")
    @ApiOperation(value = "发送邮件")
    public String sendMail(String eMail) {
        if (Strings.isNullOrEmpty(eMail)) return "eMail 不能为空!";
        if (!eMail.matches("[\\w.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) return "邮箱地址格式错误!";

        long timestamp2 = System.currentTimeMillis() / 1000;
        if (timestamp2 - timestamp < 10) return "请求过于频繁，请稍后重试！";
        else timestamp = timestamp2;

        String subject = "测试邮件 from JAVA API";
        String content = "<div style='font-size:16px;user-select:none'><p>这是一封<strong style='color:red;font-size:20px;'>测试邮件</strong>。</p><p>点击进入\uD83D\uDC49<a style='color:blue;text-decoration:none'href='http://ayiapi.xiaoyujia.com/swagger-ui.html#/'>小羽佳API</a></p><p style='font-size:14px;font-family:Courier New'>@author&nbsp;<a style='color:black;text-decoration:none'href='https://github.com/wuguangnuo'>WuGuangNuo</a></p></div>";
        EmailInfo emailInfo = new EmailInfo(eMail, subject, content);
        boolean b = emailUtil.sendHtmlMail(emailInfo);
        return b ? "发送成功!" : "发送失败";
    }

    @PostMapping(value = "/vatInvoice")
    @ApiOperation(value = "普通增值税发票识别(图片/pdf)")
    public String vatInvoice(@ApiParam(value = "发票 图片/pdf", required = true) MultipartFile file) throws Exception {
        if (file.getSize() > 2 * 1024 * 1024) return "大小限制：2M";

        String fileName = file.getOriginalFilename().toLowerCase();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        String name = "Temp/" + UUID.randomUUID().toString().replaceAll("-", "") + suffix;
        String base64Str; // 转换后的图片 base64
        String fileUrl; // 文件上传

        if (suffix.equals(".pdf")) {
            base64Str = pdfUtil.pdf2Image(file);
        } else if (suffix.equals(".png") || suffix.equals(".jpg") || suffix.equals(".jpeg") || suffix.equals(".bmp")) {
            base64Str = imageUtils.image2Base64(file);
        } else {
            return "支持的格式：PDF、PNG、JPG、JPEG、BMP。";
        }

        if (Strings.isNullOrEmpty(base64Str)) return "格式转换有误";

        String data = baiduAIUtil.vatInvoice(base64Str);
        if (Strings.isNullOrEmpty(data)) return "识别失败";

        ossClientUtil.uploadFile2OSS(file.getInputStream(), name);
        if (Strings.isNullOrEmpty(name)) return "上传失败";

        fileUrl = ossClientUtil.getUrl(name);
        if (!fileUrl.startsWith("http")) return "获取失败";

        return "源文件上传：\n" + fileUrl.substring(0, fileUrl.indexOf('?')) + "\n识别结果：\n" + data;
    }

    @GetMapping("captcha.jpg")
    @ApiOperation("验证码")
    public void captcha(HttpServletResponse response, String uuid) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //生成文字验证码
        String code = UUID.randomUUID().toString().substring(0, 4);

        BufferedImage image = producer.createImage(code);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    @GetMapping(value = "/rabbitMQ")
    @ApiOperation(value = "消息队列")
    public void rabbitMQ() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text");

        String msg = new Date().toString();
        String msg1 = "message1 " + msg, msg2 = "message2 " + msg;

        rabbitTemplate.convertAndSend("message1", msg1);
        log.info("发送消息1：" + msg1);

        rabbitTemplate.convertAndSend("message2", msg2);
        log.info("发送消息2：" + msg2);
    }

    @GetMapping(value = "/getInformation")
    @ApiOperation(value = "员工个人信息")
    public ApiRes<Employee> getInformation() {
        Employee data = employeeMapper.selectById(getUserData().getId());

        if (data != null) {
            return ApiRes.suc("获取成功", data);
        } else {
            return ApiRes.fail("查询为空");
        }
    }

    @PostMapping(value = "/uploadAddr")
    @ApiOperation(value = "MongoDB 测试")
    public ApiRes<String> uploadAddr(@RequestBody @Valid UploadAddr dto) {
        if (Strings.isNullOrEmpty(dto.getAddr()) || dto.getLon() == 0 || dto.getLat() == 0)
            return ApiRes.err("输入数据不完整");

        SimpleDateFormat sdfOne = new SimpleDateFormat("yyyy-MM-dd");
        long startTime = (long) Math.floor(System.currentTimeMillis() / 1000);
        long day = 0;
        try {
            day = (long) Math.floor(sdfOne.parse(sdfOne.format(new Date())).getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Employee emp = employeeMapper.selectById(getUserData().getId());

        UserLocation userLocation = new UserLocation()
                .setEmployeeId(emp.getId())
                .setBillNo("upload" + startTime)
                .setDay(day)
                .setStartTime(startTime)
                .setLoc(new LocData().setLat(dto.getLat()).setLon(dto.getLon()))
                .setPhone(emp.getPhone())
                .setStoreId(emp.getStoreId())
                .setLeader(emp.getIsLeader())
                .setNo(emp.getNo())
                .setRealName(emp.getRealName())
                .setAddr(dto.getAddr());
        if (emp.getEmployeeType() == EmployeeTypeEnum.服务员工.getValue()
                && emp.getIsAutoPd()
                && emp.getState() == EmployeeStateEnum.上架.getValue()) {
            userLocation.setAuto(true);
        } else {
            userLocation.setAuto(false);
        }

        try {
            // List<UserLocation> lists = mongoTemplate.find(query, UserLocation.class);
            mongoTemplate.remove(
                    new Query().addCriteria(
                            Criteria.where("EmployeeId").is(emp.getId())
                    ),
                    UserLocation.class,
                    "UserLocation"
            );
            mongoTemplate.save(userLocation, "UserLocation");
            return ApiRes.suc("Success");
        } catch (Exception e) {
            return ApiRes.err(e.toString());
        }
    }
}

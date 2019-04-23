package com.xyj.api.model.test;

/**
 * 邮件信息 Model
 */
public class EmailInfo {
    private final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private String smtpServer; // SMTP服务器地址
    private String port; // 端口
    private String fromUserName; // 登录SMTP服务器的用户名,发送人邮箱地址
    private String fromUserPassword; // 登录SMTP服务器的密码
    private String toUser; // 收件人
    private String subject; // 邮件主题
    private String content; // 邮件正文

    public EmailInfo(String toUser, String subject, String content) {
        this.toUser = toUser;
        this.subject = subject;
        this.content = content;
        this.smtpServer = "smtp.mxhichina.com";
        this.port = "465";
        this.fromUserName = "1@wuguangnuo.cn";
        this.fromUserPassword = "wgn_8888";
    }

    public String getSSL_FACTORY() {
        return SSL_FACTORY;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public String getPort() {
        return port;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public String getFromUserPassword() {
        return fromUserPassword;
    }

    public String getToUser() {
        return toUser;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
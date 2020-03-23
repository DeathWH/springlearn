package com.wang.springlearn.Entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * @author wh
 */
@PropertySource("classpath:application.properties")
public class SftpAuthority {
    @Value("${ftp.host }")
    private String host;
    @Value("${ftp.port }")
    private int port;
    @Value("${ftp.user }")
    private String user;
    @Value("${ftp.password }")
    private String password;

    public SftpAuthority(String user,String host,int port,String password){
        this.host = host;
        this.password = password;
        this.port = port;
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

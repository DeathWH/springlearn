package com.wang.springlearn.service;

import com.wang.springlearn.Entity.SftpAuthority;
import org.apache.ibatis.annotations.Param;

public interface SftpService {
    void createChannel(SftpAuthority sftpAuthority);

    void closeChannel();

    boolean uploadFile(SftpAuthority sftpAuthority,String src,String dst);

    boolean removeFile(SftpAuthority sftpAuthority,String dst);

    boolean writeData2File(String str, String dataType, String filePath);

    boolean runShell(SftpAuthority sftpAuthority,String cmdPath,String graphIp);
}

package com.wang.springlearn.service;

import com.jcraft.jsch.*;
import com.wang.springlearn.Entity.HugeGraphDic;
import com.wang.springlearn.Entity.SftpAuthority;
import io.swagger.models.auth.In;
import netscape.javascript.JSException;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Executable;
import java.util.Properties;

@Service
public class SftpServiceImpl implements SftpService{
    private Session session;
    private Channel channel;
    private ChannelSftp channelSftp;

    /**
     * 建立远程sftp连接
     * @param sftpAuthority
     */
    @Override
    public void createChannel(SftpAuthority sftpAuthority) {
        try{
            JSch jSch = new JSch();
            session = jSch.getSession(sftpAuthority.getUser(),sftpAuthority.getHost(),sftpAuthority.getPort());
            session.setPassword(sftpAuthority.getPassword());

            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking","no");
            session.setConfig(properties);
            session.setTimeout(0);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp)channel;
        } catch (JSException | JSchException e){
            e.printStackTrace();
        }
    }

    /**
     * 关闭远程连接
     */
    @Override
    public void closeChannel() {
        if(channel != null){
            channel.disconnect();
        }
        if(session != null){
            session.disconnect();
        }
    }

    /**
     * 批量导入第一步，将获取的数据保存为对应的文件
     * @param str
     * @param dataType
     * @throws IOException
     */
    @Override
    public boolean writeData2File(@Param("字符串") String str, @Param("数据类型")String dataType, @Param("保存的根目录")String filePath) {
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        try{
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file,false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(str);
            bufferedWriter.close();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上传文件至远程服务器
     * @param sftpAuthority
     * @param src
     * @param dst
     * @return
     */
    @Override
    public boolean uploadFile(SftpAuthority sftpAuthority, String src, String dst) {
        if(channelSftp == null){
            System.out.println("need create channelSftp before upload file");
            return false;
        }
        if(channelSftp.isClosed()){
            createChannel(sftpAuthority);
        }

        try{
            channelSftp.put(src,dst,ChannelSftp.OVERWRITE);
            return true;
        } catch (SftpException e){
            System.out.println("upload failed");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除远程服务器的文件
     * @param sftpAuthority
     * @param dst
     * @return
     */
    @Override
    public boolean removeFile(SftpAuthority sftpAuthority, String dst) {
        if(channelSftp == null){
            System.out.println("need create channelSftp before upload file");
            return false;
        }
        if(channelSftp.isClosed()){
            createChannel(sftpAuthority);
        }
        try{
            channelSftp.rm(dst);
            return true;
        } catch (SftpException e){
            System.out.println("rm failed");
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 远程执行shell
     * @param sftpAuthority
     * @param shellPath
     * @param graphIp
     * @return
     */
    @Override
    public boolean runShell(SftpAuthority sftpAuthority,String shellPath,String graphIp) {
        if(channelSftp == null){
            System.out.println("need create channelSftp before upload file");
            return false;
        }
        if(channelSftp.isClosed()){
            createChannel(sftpAuthority);
        }
        try{
            JSch jSch = new JSch();
            session = jSch.getSession(sftpAuthority.getUser(),sftpAuthority.getHost(),sftpAuthority.getPort());
            session.setPassword(sftpAuthority.getPassword());

            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking","no");
            session.setConfig(properties);
            session.setTimeout(0);
            session.connect();

            channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec)channel;
            channelExec.setCommand("cd " +shellPath +";./testsh.sh " + graphIp);
            InputStream in = channel.getInputStream();
            channel.connect();

            StringBuffer sb = new StringBuffer();
            int c = -1;
            while((c = in.read()) != -1){
                sb.append((char)c);
            }
            System.out.println("输出结果：" + sb.toString());
            in.close();
        } catch (JSchException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            channel.disconnect();
            session.disconnect();
        }
        return false;
    }
}

package com.wang.springlearn.Controller;

import com.baidu.hugegraph.driver.GremlinManager;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.structure.gremlin.ResultSet;
import com.sun.xml.internal.bind.v2.TODO;
import com.wang.springlearn.Entity.HugeGraphDic;
import com.wang.springlearn.Entity.SftpAuthority;
import com.wang.springlearn.service.HugeGraphService;
import com.wang.springlearn.service.SftpService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


@Controller
public class HugeGraphController {

    @Autowired
    HugeGraphService hugeGraphService;

    @Autowired
    SftpService sftpService;

    @Value("${DataSavePath}")
    private String dataSavePath;

    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.user}")
    private String user;
    @Value("${ftp.password}")
    private String password;
    @Value("${ftp.path}")
    private String fileSavePath;


    /**
     * 保存数据到本地
     * @param str
     * @param dateType
     * @return
     * @throws IOException
     */
//    @RequestMapping(value = "/save")
//    public boolean saveData(@Param("数据") String str,@Param("数据类型") String dateType) throws IOException {
//        return hugeGraphService.writeData2File(str,dateType,dataSavePath);
//    }

    /**
     * 上传文件到远程
     * @param src
     * @param dst
     */
    public void uploadFile(String src,String dst){
        SftpAuthority sftpAuthority = new SftpAuthority(user,host,port,password);
        sftpService.createChannel(sftpAuthority);
        sftpService.uploadFile(sftpAuthority,"E:\\test\\edge_IP所属人员.json",fileSavePath);
        sftpService.closeChannel();
    }

    /**
     * 删除远程文件
     * @param dst
     */
    public void rmFile(String dst){
        SftpAuthority sftpAuthority = new SftpAuthority(user,host,port,password);
        sftpService.createChannel(sftpAuthority);
        sftpService.removeFile(sftpAuthority,dst);
    }

    /**
     * 批量导入
     * @param str
     * @param dataType
     * @return
     * @throws IOException
     */
//    public boolean batchImport(@Param("数据") String str,@Param("数据类型") String dataType) throws IOException {
//        String fileName = HugeGraphDic.valueOf(dataType).getDataName();
////        将数据写入本地文件
//        hugeGraphService.writeData2File(str,dataType,dataSavePath);
////        将文件上传到服务器
//        SftpAuthority sftpAuthority = new SftpAuthority(user,host,port,password);
//        sftpService.createChannel(sftpAuthority);
//        sftpService.uploadFile(sftpAuthority,dataSavePath+fileName, fileSavePath);
//        sftpService.closeChannel();
////        TODO 执行批量导入脚本
////        不需要删除文件，直接用空文件覆盖即可，保留删除代码
////        sftpService.createChannel(sftpAuthority);
////        sftpService.removeFile(sftpAuthority,fileSavePath+"/"+fileName);
//
////        生成内容为空的文件进行替换
//        sftpService.createChannel(sftpAuthority);
//        hugeGraphService.writeData2File("",dataType,dataSavePath);
//        sftpService.uploadFile(sftpAuthority,dataSavePath+fileName, fileSavePath);
//        return true;
//    }
}

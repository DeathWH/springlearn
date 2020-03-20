package com.wang.springlearn.Controller;

import com.baidu.hugegraph.driver.GremlinManager;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.structure.gremlin.ResultSet;
import com.wang.springlearn.service.HugeGraphService;
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

    @Value("${DataSavePath}")
    private String path;
    /**
     * 根据输入的MD5、账号、IP、域名、URL、组织、人员、技术返回数据集
     * @param string
     * @return
     */
    @RequestMapping(value = "/getDataByName")
    @ResponseBody
    @ApiOperation(value="获取顶点数据",notes="根据输入的MD5、账号、IP、域名、URL、组织、人员、技术返回数据集", httpMethod = "POST")
    @ApiParam(value = "string",required = true)
    public ResultSet getDataByName(@Param("string") String string){
        return hugeGraphService.getDataByName(string);
    }

    /**
     * 根据多个输入获取多个顶点及关系
     * @param list
     * @return
     */
    @RequestMapping(value = "/getDataByMultiple")
    @ApiOperation(value="根据多个输入获取多个顶点及关系",notes="根据多个输入获取多个顶点及关系", httpMethod = "POST")
    public HashMap getDataByMultiple(@Param("list") List<HashMap> list) {
        return hugeGraphService.getDataByMultiple(list);
    }

    /**
     * 保存数据到本地
     * @param str
     * @param dateType
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/save")
    public String saveData(@Param("数据") String str,@Param("数据类型") String dateType) throws IOException {
        return hugeGraphService.writeData2File(str,dateType,path);
    }
}

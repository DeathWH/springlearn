package com.wang.springlearn.service;

import com.baidu.hugegraph.driver.GraphManager;
import com.baidu.hugegraph.driver.GremlinManager;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.structure.constant.T;
import com.baidu.hugegraph.structure.graph.Edge;
import com.baidu.hugegraph.structure.graph.Vertex;
import com.baidu.hugegraph.structure.gremlin.Result;
import com.baidu.hugegraph.structure.gremlin.ResultSet;
import io.swagger.annotations.Api;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import sun.awt.image.ImageWatched;

import java.util.*;

/**
@author wanghuai
 */
@Service
@Api(value = "",description = "HugeGraph的相关接口信息")
public class HugeGraphService {

    /**
     * 根据输入的MD5、账号、IP、域名、URL、组织、人员、技术返回数据集
     * @param string
     * @return
     */
    public ResultSet getDataByName(@Param("string") String string){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080", "hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + string + "')").execute();

        return  resultSet;
    }

    /**
     * 根据多个输入获取多个顶点及关系
     * @param list
     * @return
     */
    public HashMap getDataByMultiple(@Param("list") List<HashMap> list){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080", "hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();

        //查询所有点的信息
        String query = "g.V().or(";
        int size = list.size();
        List vertexList = new ArrayList();
        List keyList = new ArrayList();
        for(int i=0;i<size;i++){
            String key = (String) list.get(i).keySet().iterator().next();
            String value = (String) list.get(i).get(key);
            vertexList.add(value);
            keyList.add(key);
            query += "values('" + key + "').is('" + value + "'),";
        }
        query += ").dedup()";
        ResultSet resultVertexSet = gremlin.gremlin(query).execute();

        String ss = "g.V().or(";
        String doubleS = "";
        for(int j=0;j<size;j++){
            doubleS += "values('" + keyList.get(j) + "').is('" + vertexList.get(j) + "'),";
        }
        ss += doubleS + ").bothE().as('e').otherV().or(" + doubleS + ").select('e').dedup()";
        ResultSet resultEdgeSet = gremlin.gremlin(ss).execute();

        HashMap resultHashMap = new HashMap(2);
        resultHashMap.put("resultVertexSet",resultVertexSet);
        resultHashMap.put("resultEdgeSet",resultEdgeSet);
        return resultHashMap;
    }

    /**
     * 根据多个输入和关联步数获取多个顶点及关系
     * @param list
     * @param steps
     * @return
     */
    public HashMap searchByMultipleAndSteps(@Param("数据列表") List<HashMap> list, @Param("关联步数") int steps){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080", "hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();

        //查询所有点的信息
        String query = "g.V().or(";
        int size = list.size();
        List vertexList = new ArrayList();
        List keyList = new ArrayList();
        for(int i=0;i<size;i++){
            String key = (String) list.get(i).keySet().iterator().next();
            String value = (String) list.get(i).get(key);
            vertexList.add(value);
            keyList.add(key);
            query += "values('" + key + "').is('" + value + "'),";
        }
        query += ").repeat(bothE().bothV()).times(" + steps + ").dedup()";
        ResultSet resultVertexSet = gremlin.gremlin(query).execute();
        ResultSet resultVertexSet1 = gremlin.gremlin(query + ".group().by(label)").execute();

        //查询所有的边关系
        ArrayList keySetList = new ArrayList();
        ArrayList valueSetList = new ArrayList();
        for(int i = 0;i < resultVertexSet.data().size();i++){
            LinkedHashMap linkedHashMap = (LinkedHashMap) resultVertexSet.data().get(i);
            LinkedHashMap linkedHashMap1 = (LinkedHashMap) linkedHashMap.get("properties");
            String kkk = "";
            for(Object key:linkedHashMap1.keySet()){
                kkk = key.toString();
                keySetList.add(kkk);
                String value = (String)linkedHashMap1.get(kkk);
                valueSetList.add(value);
                break;
            }
        }
        String doubleS = "";
        for(int j=0;j<resultVertexSet.size();j++){
            doubleS += "values('" + keySetList.get(j) + "').is('" + valueSetList.get(j) + "'),";
        }
        String ss = "g.V().or(";
        ss += doubleS + ").bothE().as('e').otherV().or(" + doubleS + ").select('e').dedup()";
        ResultSet resultEdgeSet = gremlin.gremlin(ss).execute();

        HashMap resultHashMap = new HashMap(2);
        resultHashMap.put("resultVertexSet",resultVertexSet1);
        resultHashMap.put("resultEdgeSet",resultEdgeSet);
        return resultHashMap;
    }

    /**
     * 获取所有的图顶点
     * @return
     */
    public ResultSet getAll(){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080", "hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        ResultSet resultSet1 = gremlin.gremlin("g.V()").execute();

        return  resultSet1;
    }

    /**
     * 根据输入的点，获取所有关联的边和边的顶点（已去重）
     * 边的数据以ResultSet的结构保存在resultEdgeSet
     * 点的数据经过去重处理以HashMap的结构保存在resultVertexMap
     * 最终返回数据以HashMap的结构，key-value形式保存，resultVertexSet-resultVertexMap、resultEdgeSet-resultEdgeSet
     * @param string
     * @return
     */
    public HashMap getEdgeByName(@Param("string") String string){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080", "hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        //获取所有的边
        ResultSet resultEdgeSet = gremlin.gremlin("g.V().hasValue('" + string + "').bothE()").execute();
        //获取所有边的两端顶点
        ResultSet resultVertexSet = gremlin.gremlin("g.V().hasValue('" + string + "').bothE().otherV().dedup().group().by(label)").execute();

        HashMap hashMapResult = new HashMap(2);
        //点数据
        hashMapResult.put("resultVertexSet",resultVertexSet);
        //边数据
        hashMapResult.put("resultEdgeSet",resultEdgeSet);
        return hashMapResult;
    }

    /**
     * 确定使用原属性值还是新属性值
     * 只有当原属性数据不为空且新属性数据为空时，保留原属性数据，否则都用新属性数据进行覆盖
     * @param oldObject
     * @param newString
     * @return
     */
    public String oldOrNew(Object oldObject, String newString){
        Boolean oldObjectNotNull = (oldObject != null ||oldObject != "");
        Boolean newStringIsNull = (newString == "");
//        if((oldObject != null ||oldObject != "") && newString == ""){
        if(oldObjectNotNull && newStringIsNull){
            return oldObject.toString();
        } else {
            return newString;
        }
    }

    /**
     * 插入MD5点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param md5(必填)
     * @param md5Size
     * @param md5Date
     * @param md5Name
     * @param md5Type
     * @param md5From
     * @return
     */
    public String insertMD5Vertex(@Param("MD5") String md5, @Param("文件大小") int md5Size, @Param("编译时间") String md5Date,
                               @Param("原始文件名") String md5Name, @Param("文件类型") String md5Type, @Param("文件来源") String md5From){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + md5 + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            Vertex md5InsertResult = graph.addVertex(T.label, "MD5", "md5", md5, "文件大小", md5Size,
                    "编译时间",md5Date,"原始文件名", md5Name,"文件类型", md5Type, "文件来源", md5From);
        } else {
            //文件大小为int型，需要转换String才能进行判断
            String ss = md5Size +"";
            String string = oldOrNew(resultSet.get(0).getVertex().property("文件大小"), ss);
            Integer integer = new Integer(string);
            int newMd5Size = integer.intValue();
            String newMd5Date = oldOrNew(resultSet.get(0).getVertex().property("编译时间"), md5Date);
            String newMd5Name = oldOrNew(resultSet.get(0).getVertex().property("原始文件名"), md5Name);
            String newMd5Type = oldOrNew(resultSet.get(0).getVertex().property("文件类型"), md5Type);
            String  newMd5From = oldOrNew(resultSet.get(0).getVertex().property("文件来源"), md5From);

            Vertex md5InsertResult = graph.addVertex(T.label, "MD5", "md5", md5, "文件大小", newMd5Size,
                    "编译时间",newMd5Date,"原始文件名", newMd5Name,"文件类型", newMd5Type, "文件来源", newMd5From);
        }
        return "OK";
    }

    /**
     * 插入IP点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param ip(必填)
     * @param ipAddress
     * @param ipType
     * @param ipMalicious
     * @return
     */
    public String insertIPVretex(@Param("IP") String ip, @Param("地理位置") String ipAddress, @Param("设备类型") String ipType,
                                 @Param("恶意节点标识") String ipMalicious){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + ip + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            Vertex ipInsertResult = graph.addVertex(T.label, "IP", "ip", ip, "地理位置", ipAddress,
                    "设备类型",ipType,"恶意节点标识", ipMalicious);
        } else {
            String newIPAddress = oldOrNew(resultSet.get(0).getVertex().property("地理位置"), ipAddress);
            String newIPType = oldOrNew(resultSet.get(0).getVertex().property("设备类型"), ipType);
            String newIPMalicious = oldOrNew(resultSet.get(0).getVertex().property("恶意节点标识"), ipMalicious);

            Vertex ipInsertResult = graph.addVertex(T.label, "IP", "ip", ip, "地理位置", newIPAddress,
                    "设备类型",newIPType,"恶意节点标识", newIPMalicious);
        }
        return "OK";
    }

    /**
     * 插入账号点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param account(必填)
     * @param passwd
     * @param accountType
     * @param accountBelong
     * @return
     */
    public String insertAccountVretex(@Param("账号") String account, @Param("账号密码") String passwd, @Param("账号类型") String accountType,
                                 @Param("账号所属地区") String accountBelong){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + account + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            Vertex accountInsertResult = graph.addVertex(T.label, "账号", "账号", account, "账号密码", passwd,
                    "账号类型",accountType,"账号所属地区", accountBelong);
        } else {
            String newPasswd = oldOrNew(resultSet.get(0).getVertex().property("账号密码"), passwd);
            String newAccountType = oldOrNew(resultSet.get(0).getVertex().property("账号类型"), accountType);
            String newAccountBelong = oldOrNew(resultSet.get(0).getVertex().property("账号所属地区"), accountBelong);

            Vertex accountInsertResult = graph.addVertex(T.label, "账号", "账号", account, "账号密码", newPasswd,
                    "账号类型",newAccountType,"账号所属地区", newAccountBelong);
        }
        return "OK";
    }

    /**
     * 插入技术点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param tech(必填)
     * @param techType
     * @param cve
     * @param authority
     * @return
     */
    public String insertTechVretex(@Param("技术") String tech, @Param("技术类型") String techType, @Param("漏洞编号") String cve,
                                      @Param("需要权限") String authority){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + tech + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            Vertex techInsertResult = graph.addVertex(T.label, "技术", "技术", tech, "技术类型", techType,
                    "漏洞编号",cve,"需要权限", authority);
        } else {
            String newTechType = oldOrNew(resultSet.get(0).getVertex().property("技术类型"), techType);
            String newCVE = oldOrNew(resultSet.get(0).getVertex().property("漏洞编号"), cve);
            String newAuthority = oldOrNew(resultSet.get(0).getVertex().property("需要权限"), authority);

            Vertex techInsertResult = graph.addVertex(T.label, "技术", "技术", tech, "技术类型", newTechType,
                    "漏洞编号",newCVE, "需要权限", newAuthority);
        }
        return "OK";
    }

    /**
     * 插入域名点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param domain(必填)
     * @param domainArea
     * @param domainStatus
     * @param domainCertificate
     * @param domainService
     * @param domainMalicious
     * @return
     */
    public String insertDomainVretex(@Param("域名") String domain, @Param("注册地区") String domainArea, @Param("状态") String domainStatus,
                                     @Param("证书（颁发者）") String domainCertificate, @Param("域名服务商") String domainService,
                                     @Param("恶意域名标识") String domainMalicious){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + domain + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            Vertex domainInsertResult = graph.addVertex(T.label, "域名", "域名", domain, "注册地区", domainArea,
                        "状态",domainStatus,"证书（颁发者）", domainCertificate, "域名服务商", domainService, "恶意域名标识", domainMalicious);
        } else {
            String newDomainArea = oldOrNew(resultSet.get(0).getVertex().property("注册地区"), domainArea);
            String newDomainStatus = oldOrNew(resultSet.get(0).getVertex().property("状态"), domainStatus);
            String newDomainCertificate = oldOrNew(resultSet.get(0).getVertex().property("证书（颁发者）"), domainCertificate);
            String newDomainService = oldOrNew(resultSet.get(0).getVertex().property("域名服务商"), domainService);
            String newDomainMalicious = oldOrNew(resultSet.get(0).getVertex().property("恶意域名标识"), domainMalicious);

            Vertex domainInsertResult = graph.addVertex(T.label, "域名", "域名", domain, "注册地区", newDomainArea,
                    "状态",newDomainStatus,"证书（颁发者）", newDomainCertificate, "域名服务商", newDomainService, "恶意域名标识", newDomainMalicious);
        }
        return "OK";
    }

    /**
     * 插入URL点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param url(必填)
     * @param urlMalicious
     * @return
     */
    public String insertURLVretex(@Param("URL") String url, @Param("恶意URL标识") String urlMalicious){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + url + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            Vertex urlInsertResult = graph.addVertex(T.label, "URL", "URL", url, "恶意URL标识", urlMalicious);
        } else {
            String newURLMalicious = oldOrNew(resultSet.get(0).getVertex().property("状态"), urlMalicious);

            Vertex urlInsertResult = graph.addVertex(T.label, "URL", "URL", url, "恶意URL标识", newURLMalicious);
        }
        return "OK";
    }

    /**
     * 插入人员点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param peopleID(必填)
     * @param peopleName
     * @param peopleCountry
     * @param peopleType
     * @param peopleJob
     * @return
     */
    public String insertPeopleVretex(@Param("人员ID") String peopleID, @Param("人员姓名") String peopleName, @Param("国籍") String peopleCountry,
                                     @Param("性质") String peopleType, @Param("工作职位") String peopleJob){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + peopleID + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            Vertex peopleInsertResult = graph.addVertex(T.label, "人员", "人员ID", peopleID, "人员姓名", peopleName,
                    "国籍", peopleCountry, "性质", peopleType, "工作职位", peopleJob);
        } else {
            String newPeopleName = oldOrNew(resultSet.get(0).getVertex().property("人员姓名"), peopleName);
            String newPeopleCountry= oldOrNew(resultSet.get(0).getVertex().property("国籍"), peopleCountry);
            String newPeopleType = oldOrNew(resultSet.get(0).getVertex().property("性质"), peopleType);
            String newPeopleJob = oldOrNew(resultSet.get(0).getVertex().property("工作职位"), peopleJob);

            Vertex peopleInsertResult = graph.addVertex(T.label, "人员", "人员ID", peopleID, "人员姓名", newPeopleName,
                    "国籍", newPeopleCountry, "性质", newPeopleType, "工作职位", newPeopleJob);
        }
        return "OK";
    }

    /**
     * 插入组织点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param orgName(必填)
     * @param orgType
     * @param orgCountry
     * @param orgTargetIndustry
     * @param orgTargetPopulation
     * @param orgPurpose
     * @param orgWithMe
     * @return
     */
    public String insertOrganizationVretex(@Param("组织") String orgName, @Param("组织类别") String orgType, @Param("国家及地区") String orgCountry,
                                           @Param("目标行业") String orgTargetIndustry, @Param("目标人群") String orgTargetPopulation,
                                           @Param("攻击目的") String orgPurpose, @Param("是否涉我") String orgWithMe){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + orgName + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            Vertex orgInsertResult = graph.addVertex(T.label, "组织", "组织", orgName, "组织类别", orgType,
                    "国家及地区", orgCountry, "目标行业", orgTargetIndustry, "目标人群", orgTargetPopulation, "攻击目的", orgPurpose, "是否涉我", orgWithMe);
        } else {
            String newOrgType = oldOrNew(resultSet.get(0).getVertex().property("组织类别"), orgType);
            String newOrgCountry= oldOrNew(resultSet.get(0).getVertex().property("国家及地区"), orgCountry);
            String newOrgTargetIndustry = oldOrNew(resultSet.get(0).getVertex().property("目标行业"), orgTargetIndustry);
            String newOrgTargetPopulation = oldOrNew(resultSet.get(0).getVertex().property("目标人群"), orgTargetPopulation);
            String newOrgPurpose = oldOrNew(resultSet.get(0).getVertex().property("攻击目的"), orgPurpose);
            String newOrgWithMe = oldOrNew(resultSet.get(0).getVertex().property("是否涉我"), orgWithMe);

            Vertex orgInsertResult = graph.addVertex(T.label, "组织", "组织", orgName, "组织类别", newOrgType,
                    "国家及地区", newOrgCountry, "目标行业", newOrgTargetIndustry, "目标人群", newOrgTargetPopulation, "攻击目的", newOrgPurpose, "是否涉我", newOrgWithMe);
        }
        return "OK";
    }

    /**
     * 新增边
     * @param vertex1
     * @param vertex2
     * @param relation
     * @return
     */
    public String insertEdge(@Param("顶点1") String vertex1, @Param("顶点2") String vertex2, @Param("关系") String relation){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();

        ResultSet resultSet1 = gremlin.gremlin("g.V().hasValue('" + vertex1 + "')").execute();
        Vertex vertexa = resultSet1.get(0).getVertex();
        ResultSet resultSet2 = gremlin.gremlin("g.V().hasValue('" + vertex2 + "')").execute();
        Vertex vertexb =resultSet2.get(0).getVertex();

        Edge inserEdgeResult = vertexa.addEdge(relation,vertexb);

        return "OK";
    }

    /**
     * 根据关联顶点类型进行筛选
     * @param vertex
     * @param type
     * @return
     */
    public HashMap searchByCondition(@Param("顶点") String vertex, @Param("类型") String type){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        //获取指定类型的顶点
        ResultSet vertexResultSet = gremlin.gremlin("g.V().hasValue('" + vertex + "').bothE().otherV().hasLabel('" + type +"').dedup()").execute();

        //获取符合条件的边
        ResultSet edgeResultSet = gremlin.gremlin("g.V().hasValue('" + vertex + "').bothE().as('e').otherV().hasLabel('" + type + "').select('e').dedup()").execute();

        HashMap resultHashMap = new HashMap(2);
        resultHashMap.put("edgeResultSet",edgeResultSet);
        resultHashMap.put("vertexResultSet",vertexResultSet);

        return resultHashMap;
    }

    /**
     * 根据多个关联顶点类型进行筛选
     * @param label
     * @param key
     * @param value
     * @param typeList
     * @return
     */
    public HashMap searchByVertexConditionList(@Param("顶点标签") String label,@Param("顶点属性") String key,@Param("顶点属性值") String value, @Param("顶点类型") List<String> typeList){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        //获取指定类型的顶点
        String vertexQuery = "g.V().has('" + label + "',"+ "'" + key + "'," + "'" + value + "'" + ").bothE().otherV().hasLabel(";
        for(String type:typeList){
            vertexQuery += "'" + type + "',";
        }
        vertexQuery += ").dedup()";
        ResultSet vertexResultSet = gremlin.gremlin(vertexQuery).execute();
        //获取符合条件的边
        String edgeQuery = "g.V().has('" + label + "',"+ "'" + key + "'," + "'" + value + "'" + ").bothE().as('e').otherV().hasLabel(";
        for(String type:typeList){
            edgeQuery += "'" + type + "',";
        }
        edgeQuery += ").select('e').dedup()";
        ResultSet edgeResultSet = gremlin.gremlin(edgeQuery).execute();

        HashMap resultHashMap = new HashMap(2);
        resultHashMap.put("resultEdgeSet",edgeResultSet);
        resultHashMap.put("resultVertexSet",vertexResultSet);

        return resultHashMap;
    }

    /**
     * 根据多个关联边类型进行筛选
     * @param label
     * @param key
     * @param value
     * @param typeList
     * @return
     */
    public HashMap searchByEdgeConditionList(@Param("顶点标签") String label,@Param("顶点属性") String key,@Param("顶点属性值") String value, @Param("边类型") List<String> typeList){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        //获取指定类型的顶点
        String vertexQuery = "g.V().has('" + label + "',"+ "'" + key + "'," + "'" + value + "'" + ").bothE().hasLabel(";
        for(String type:typeList){
            vertexQuery += "'" + type + "',";
        }
        vertexQuery += ").otherV().dedup()";
        ResultSet vertexResultSet = gremlin.gremlin(vertexQuery).execute();
        //获取符合条件的边
        String edgeQuery = "g.V().has('" + label + "',"+ "'" + key + "'," + "'" + value + "'" + ").bothE().hasLabel(";
        for(String type:typeList){
            edgeQuery += "'" + type + "',";
        }
        edgeQuery += ").dedup()";
        ResultSet edgeResultSet = gremlin.gremlin(edgeQuery).execute();

        HashMap resultHashMap = new HashMap(2);
        resultHashMap.put("resultEdgeSet",edgeResultSet);
        resultHashMap.put("resultVertexSet",vertexResultSet);

        return resultHashMap;
    }

    /**
     * 根据顶点值获取关联的组织
     * @param value
     * @return
     */
    public ResultSet searchOrgByVertex(@Param("顶点属性值") String value){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        int times = 4;
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + value +  "').repeat(both()).times(" + times + ").hasLabel('组织').dedup()").execute();

        return resultSet;
    }

    /**
     * 获取从顶点到组织的最短路径
     * @param value1
     * @param value2
     * @return
     */
    public ResultSet searchPathBy2Vertex(@Param("起始顶点属性值") String value1, @Param("组织名称") String value2){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();

        int times = 4;
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('" + value1 +
                "').repeat(bothE().otherV().simplePath())" +
                ".until(has('组织','"+ value2 + "').and().loops().is(lte("+ times +")))" +
                ".path()").execute();

        if(resultSet == null){
            return null;
        } else {
            int step = ((List)((LinkedHashMap)resultSet.data().get(0)).get("objects")).size();
            int num = resultSet.data().size();
            if(num == 1){
                return resultSet;
            } else {
                for(int j=num-1;j>0;j--){
                    if(((List)((LinkedHashMap)resultSet.data().get(j)).get("objects")).size()>step){
                        resultSet.data().remove(j);
                    } else {
                        break;
                    }
                }
                return resultSet;
            }
        }


//        if(resultSet.size() > 1){
//            HashMap hashMap = new HashMap();
//            LinkedHashMap linkedHashMap = (LinkedHashMap) resultSet.data().get(0);
//            List list1 = (List) linkedHashMap.get("objects");
//            hashMap.put(0,list1);
//
//            int key = 1;
//            int depth = list1.size();
//            for(int j=1;j<resultSet.data().size();j++){
//                LinkedHashMap linkedHashMap1 = (LinkedHashMap) resultSet.data().get(j);
//                if(((List)linkedHashMap1.get("objects")).size() == depth){
//                    hashMap.put(key,(List)linkedHashMap1.get("objects"));
//                    key++;
//                }else {
//                    break;
//                }
//            }
//            return hashMap;
//        } else if(resultSet.size() == 1){
//            LinkedHashMap linkedHashMap = (LinkedHashMap) resultSet.data().get(0);
//            HashMap hashMap = new HashMap();
//            hashMap.put(0,(List) linkedHashMap.get("objects"));
//            return hashMap;
//        } else {
//            return null;
//        }

    }

}

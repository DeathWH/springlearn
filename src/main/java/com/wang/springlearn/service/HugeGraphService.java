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
            String vertexKey = (String)linkedHashMap.get("label");
            String vertexValue = ((String)linkedHashMap.get("id")).substring(2);
            keySetList.add(vertexKey);
            valueSetList.add(vertexValue);
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
     * 根据输入的多个顶点、期望关联的顶点类型和边类型、关联步数，返回过滤的顶点集合和边集合
     * @param list
     * @param vertexSetList
     * @param edgeSetList
     * @param steps
     * @return
     */
    public HashMap searchByVetexsAndEdgesAndSteps(@Param("顶点数据") List<HashMap> list,
                                                  @Param("顶点类型") List<String> vertexSetList,
                                                  @Param("边类型") List<String> edgeSetList,
                                                  @Param("关联步数") int steps){
        //如果顶点数据为空或顶点类型为空，则返回空
        if(list == null || vertexSetList == null){
            return null;
        }

        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080", "hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();

        //查询关联点的信息
        String query = "g.V().or(";
        int size = list.size();
        List vertexList = new ArrayList();
        List keyList = new ArrayList();
        String querySS = "";
        for(int i=0;i<size;i++){
            String key = (String) list.get(i).keySet().iterator().next();
            String value = (String) list.get(i).get(key);
            vertexList.add(value);
            keyList.add(key);
            querySS += "values('" + key + "').is('" + value + "'),";
        }
        String queryNew = query + querySS + ").repeat(bothE().bothV()).times(" + steps + ").or(" + querySS + "hasLabel(";
        for(String str:vertexSetList){
            queryNew += "'" + str + "',";
        }
        ResultSet resultVertexSet1 = gremlin.gremlin(queryNew + ")).dedup().group().by(label)").execute();
        ResultSet resultVertexSet = gremlin.gremlin(queryNew + ")).dedup()").execute();

        ResultSet resultEdgeSet = null;
        //如果边关系不为空，则查询关联的边关系
        if(edgeSetList != null){
            ArrayList keySetList = new ArrayList();
            ArrayList valueSetList = new ArrayList();
            for(int i = 0;i < resultVertexSet.data().size();i++){
                LinkedHashMap linkedHashMap = (LinkedHashMap) resultVertexSet.data().get(i);
                String vertexKey = (String)linkedHashMap.get("label");
                String vertexValue = ((String)linkedHashMap.get("id")).substring(2);
                keySetList.add(vertexKey);
                valueSetList.add(vertexValue);
            }

            String doubleS = "";
            for(int j=0;j<resultVertexSet.size();j++){
                doubleS += "values('" + keySetList.get(j) + "').is('" + valueSetList.get(j) + "'),";
            }
            String ss = "g.V().or(";
            ss += doubleS + ").bothE().hasLabel(";
            for(String str:edgeSetList){
                ss += "'" + str + "',";
            }
            ss += ").as('e').bothV().or(" + doubleS + ").select('e').dedup()";
            resultEdgeSet = gremlin.gremlin(ss).execute();
        }

        HashMap resultHashMap = new HashMap(2);
        resultHashMap.put("resultVertexSet",resultVertexSet1);
        resultHashMap.put("resultEdgeSet",resultEdgeSet);
        return resultHashMap;
    }

    /**
     * 删除顶点
     * @param key
     * @param value
     * @return
     */
    public String deleteVertex(@Param("顶点属性")String key,@Param("顶点属性值")String value){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080", "hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        String query1 = "g.V().or(values('" + key + "').is('" + value + "')).bothE().as('e').bothV()" +
                ".or(values('" + key + "').is('" + value + "')).select('e').drop()";
        String query2 = "g.V().or(values('" + key + "').is('" + value + "')).drop()";
        ResultSet resultSet1 = gremlin.gremlin(query1).execute();
        ResultSet resultSet2 = gremlin.gremlin(query2).execute();
        if(resultSet1.size() != 0){
            return "删除边失败";
        }
        if(resultSet2.size() != 0){
            return "删除点失败";
        }
        return "删除成功";
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
     * @param MD5
     * @param md5Name
     * @param md5Type
     * @param md5From
     * @param md5Family
     * @param md5Process
     * @param md5Registry
     * @return
     */
    public String insertMD5Vertex(@Param("MD5") String MD5, @Param("原始文件名") String md5Name, @Param("文件类型") String md5Type,
                                  @Param("文件来源") String md5From, @Param("家族信息") String md5Family,
                                  @Param("进程") String md5Process, @Param("注册表项") String md5Registry){
        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().has('MD5','MD5','" + MD5 + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try {
                graph.addVertex(T.label, "MD5", "MD5", MD5,
                        "原始文件名", md5Name,"文件类型", md5Type, "文件来源", md5From, "家族信息", md5Family,
                        "进程", md5Process, "注册表项", md5Registry);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newMd5Name = oldOrNew(resultSet.get(0).getVertex().property("原始文件名"), md5Name);
            String newMd5Type = oldOrNew(resultSet.get(0).getVertex().property("文件类型"), md5Type);
            String newMd5From = oldOrNew(resultSet.get(0).getVertex().property("文件来源"), md5From);
            String newMd5Family = oldOrNew(resultSet.get(0).getVertex().property("家族信息"), md5Family);
            String newMd5Process = oldOrNew(resultSet.get(0).getVertex().property("进程"), md5Process);
            String newMd5Registry = oldOrNew(resultSet.get(0).getVertex().property("注册表项"), md5Registry);

            try{
                graph.addVertex(T.label, "MD5", "MD5", MD5,"原始文件名", newMd5Name,"文件类型", newMd5Type,
                        "文件来源", newMd5From, "家族信息", newMd5Family, "进程", newMd5Process, "注册表项", newMd5Registry);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
    }

    /**
     * 插入IP点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param IP(必填)
     * @param ipAddress
     * @param ipType
     * @param ipMalicious
     * @return
     */
    public String insertIPVretex(@Param("IP") String IP, @Param("地理位置") String ipAddress, @Param("设备类型") String ipType,
                                 @Param("恶意节点标识") String ipMalicious){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().has('IP','IP','" + IP + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try{
                graph.addVertex(T.label, "IP", "IP", IP, "地理位置", ipAddress,
                        "设备类型",ipType,"恶意节点标识", ipMalicious);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newIPAddress = oldOrNew(resultSet.get(0).getVertex().property("地理位置"), ipAddress);
            String newIPType = oldOrNew(resultSet.get(0).getVertex().property("设备类型"), ipType);
            String newIPMalicious = oldOrNew(resultSet.get(0).getVertex().property("恶意节点标识"), ipMalicious);

            try{
                graph.addVertex(T.label, "IP", "IP", IP, "地理位置", newIPAddress,
                        "设备类型",newIPType,"恶意节点标识", newIPMalicious);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
    }

    /**
     * 插入账号点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param account
     * @param accountType
     * @param accountBelong
     * @param accountName
     * @return
     */
    public String insertAccountVretex(@Param("账号") String account, @Param("账号类型") String accountType,
                                 @Param("账号注册地") String accountBelong, @Param("昵称") String accountName){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().has('账号','账号','" + account + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try{
                graph.addVertex(T.label, "账号", "账号", account,
                        "账号类型",accountType,"账号注册地", accountBelong,"昵称",accountName);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newAccountType = oldOrNew(resultSet.get(0).getVertex().property("账号类型"), accountType);
            String newAccountBelong = oldOrNew(resultSet.get(0).getVertex().property("账号注册地"), accountBelong);
            String newAccountName = oldOrNew(resultSet.get(0).getVertex().property("昵称"), accountName);

            try{
                graph.addVertex(T.label, "账号", "账号", account,
                        "账号类型",newAccountType,"账号注册地", newAccountBelong,"昵称",newAccountName);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
    }

    /**
     * 插入技术点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param tech
     * @param techType
     * @param platform
     * @param desc
     * @return
     */
    public String insertTechVretex(@Param("技术") String tech, @Param("技术类型") String techType, @Param("平台") String platform,
                                      @Param("描述") String desc){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().has('技术','技术','" + tech + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try{
                graph.addVertex(T.label, "技术", "技术", tech, "技术类型", techType,
                        "平台",platform,"描述", desc);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newTechType = oldOrNew(resultSet.get(0).getVertex().property("技术类型"), techType);
            String newPlatForm = oldOrNew(resultSet.get(0).getVertex().property("平台"), platform);
            String newDesc = oldOrNew(resultSet.get(0).getVertex().property("描述"), desc);

            try{
                graph.addVertex(T.label, "技术", "技术", tech, "技术类型", newTechType,
                        "平台",newPlatForm, "描述", newDesc);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
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
        ResultSet resultSet = gremlin.gremlin("g.V().has('域名','域名','" + domain + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try{
                graph.addVertex(T.label, "域名", "域名", domain, "注册地区", domainArea,
                        "状态",domainStatus,"证书（颁发者）", domainCertificate, "域名服务商", domainService, "恶意域名标识", domainMalicious);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newDomainArea = oldOrNew(resultSet.get(0).getVertex().property("注册地区"), domainArea);
            String newDomainStatus = oldOrNew(resultSet.get(0).getVertex().property("状态"), domainStatus);
            String newDomainCertificate = oldOrNew(resultSet.get(0).getVertex().property("证书（颁发者）"), domainCertificate);
            String newDomainService = oldOrNew(resultSet.get(0).getVertex().property("域名服务商"), domainService);
            String newDomainMalicious = oldOrNew(resultSet.get(0).getVertex().property("恶意域名标识"), domainMalicious);

            try{
                graph.addVertex(T.label, "域名", "域名", domain, "注册地区", newDomainArea,
                        "状态",newDomainStatus,"证书（颁发者）", newDomainCertificate, "域名服务商", newDomainService, "恶意域名标识", newDomainMalicious);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
    }

    /**
     * 插入URL点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param URL(必填)
     * @param urlMalicious
     * @return
     */
    public String insertURLVretex(@Param("URL") String URL, @Param("恶意URL标识") String urlMalicious){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().has('URL','URL','" + URL + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try{
                graph.addVertex(T.label, "URL", "URL", URL, "恶意URL标识", urlMalicious);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newURLMalicious = oldOrNew(resultSet.get(0).getVertex().property("状态"), urlMalicious);

            try{
                graph.addVertex(T.label, "URL", "URL", URL, "恶意URL标识", newURLMalicious);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
    }

    /**
     * 插入人员点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param peopleID
     * @param peopleName
     * @param peopleCountry
     * @param peopleHome
     * @param peopleJob
     * @return
     */
    public String insertPeopleVretex(@Param("人员") String peopleID, @Param("人员姓名") String peopleName, @Param("国籍") String peopleCountry,
                                     @Param("居住地") String peopleHome, @Param("职业") String peopleJob){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().has('人员','人员','" + peopleID + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try{
                graph.addVertex(T.label, "人员", "人员", peopleID, "人员姓名", peopleName,
                        "国籍", peopleCountry, "居住地", peopleHome, "职业", peopleJob);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newPeopleName = oldOrNew(resultSet.get(0).getVertex().property("人员姓名"), peopleName);
            String newPeopleCountry= oldOrNew(resultSet.get(0).getVertex().property("国籍"), peopleCountry);
            String newPeopleHome = oldOrNew(resultSet.get(0).getVertex().property("居住地"), peopleHome);
            String newPeopleJob = oldOrNew(resultSet.get(0).getVertex().property("职业"), peopleJob);

            try{
                graph.addVertex(T.label, "人员", "人员", peopleID, "人员姓名", newPeopleName,
                        "国籍", newPeopleCountry, "居住地", newPeopleHome, "职业", newPeopleJob);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
    }

    /**
     * 插入组织点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param orgName(必填)
     * @param orgType
     * @param orgCountry
     * @param orgTargetIndustry
     * @param orgTargetPopulation
     * @param orgWithMe
     * @return
     */
    public String insertOrganizationVretex(@Param("组织") String orgName, @Param("组织类别") String orgType, @Param("国家及地区") String orgCountry,
                                           @Param("目标行业") String orgTargetIndustry, @Param("目标人群") String orgTargetPopulation,
                                           @Param("是否涉我") String orgWithMe){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().has('组织','组织','" + orgName + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try{
                graph.addVertex(T.label, "组织", "组织", orgName, "组织类别", orgType,
                        "国家及地区", orgCountry, "目标行业", orgTargetIndustry, "目标人群", orgTargetPopulation, "是否涉我", orgWithMe);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newOrgType = oldOrNew(resultSet.get(0).getVertex().property("组织类别"), orgType);
            String newOrgCountry= oldOrNew(resultSet.get(0).getVertex().property("国家及地区"), orgCountry);
            String newOrgTargetIndustry = oldOrNew(resultSet.get(0).getVertex().property("目标行业"), orgTargetIndustry);
            String newOrgTargetPopulation = oldOrNew(resultSet.get(0).getVertex().property("目标人群"), orgTargetPopulation);
            String newOrgWithMe = oldOrNew(resultSet.get(0).getVertex().property("是否涉我"), orgWithMe);

            try{
                graph.addVertex(T.label, "组织", "组织", orgName, "组织类别", newOrgType,
                        "国家及地区", newOrgCountry, "目标行业", newOrgTargetIndustry, "目标人群", newOrgTargetPopulation, "是否涉我", newOrgWithMe);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
    }

    /**
     * 插入硬件点，并取原属性值和新属性值的并集，对原数据进行覆盖
     * @param hardware
     * @param hardwareType
     * @param uuType
     * @param uuID
     * @param hardwareMade
     * @param cve
     * @return
     */
    public String insertHardwareVertex(@Param("硬件") String hardware, @Param("硬件类型") String hardwareType,
                                       @Param("唯一标识类型") String uuType, @Param("唯一标识") String uuID,
                                       @Param("制造商") String hardwareMade, @Param("关联漏洞") String cve){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().has('硬件','硬件','" + hardware + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try{
                graph.addVertex(T.label, "硬件", "硬件", hardware, "硬件类型", hardwareType,
                        "唯一标识类型", uuType, "唯一标识", uuID, "制造商", hardwareMade, "关联漏洞", cve);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newHardwareType= oldOrNew(resultSet.get(0).getVertex().property("硬件类型"), hardwareType);
            String newUUType= oldOrNew(resultSet.get(0).getVertex().property("唯一标识类型"), uuType);
            String newUUID = oldOrNew(resultSet.get(0).getVertex().property("唯一标识"), uuID);
            String newHardwareMade = oldOrNew(resultSet.get(0).getVertex().property("制造商"), hardwareMade);
            String newCVE = oldOrNew(resultSet.get(0).getVertex().property("关联漏洞"), cve);

            try{
                graph.addVertex(T.label, "硬件", "硬件", hardware, "硬件类型", newHardwareType,
                        "唯一标识类型", newUUType, "唯一标识", newUUID, "制造商", newHardwareMade,
                        "关联漏洞", newCVE);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
    }


    public String insertSoftwareVertex(@Param("软件") String sotfware, @Param("软件类型") String softwareType,
                                       @Param("类型子类") String softwareType2, @Param("软件版本") String softwareVersion){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080","hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        GraphManager graph = hugeClient.graph();
        ResultSet resultSet = gremlin.gremlin("g.V().has('软件','软件','" + sotfware + "')").execute();

        if(resultSet == null || resultSet.size() == 0){
            try{
                graph.addVertex(T.label, "软件", "软件", sotfware, "软件类型", softwareType,
                        "类型子类", softwareType2, "软件版本", softwareVersion);
            } catch (Exception e){
                System.out.println(e);
                return "新增失败";
            }
            return "新增成功";
        } else {
            String newSoftwareType= oldOrNew(resultSet.get(0).getVertex().property("软件类型"), softwareType);
            String newSoftwareType2= oldOrNew(resultSet.get(0).getVertex().property("类型子类"), softwareType2);
            String newSoftwareVersion = oldOrNew(resultSet.get(0).getVertex().property("软件版本"), softwareVersion);

            try{
                graph.addVertex(T.label, "软件", "软件", sotfware, "软件类型", newSoftwareType,
                        "类型子类", newSoftwareType2, "软件版本", newSoftwareVersion);
            } catch (Exception e){
                System.out.println(e);
                return "更新失败";
            }
            return "更新成功";
        }
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

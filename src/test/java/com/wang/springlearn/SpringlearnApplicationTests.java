package com.wang.springlearn;

import com.baidu.hugegraph.structure.gremlin.ResultSet;
import com.wang.springlearn.service.HugeGraphService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringlearnApplicationTests {

    private static final String s = "f74b90e77f38a06ff3f48d6de182a45a";

    @Test
//    public void  getOneByName(){
//        String string="f74b90e77f38a06ff3f48d6de182a45a";
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        ResultSet resultSet = hugeGraphService.getDataByName(string);
//    }
//    public void getDataByM(){
//        HashMap hashMap1 = new HashMap();
//        HashMap hashMap2 = new HashMap();
//        HashMap hashMap3 = new HashMap();
//        HashMap hashMap4 = new HashMap();
//        HashMap hashMap5 = new HashMap();
//        hashMap1.put("ip","82.221.129.17");
//        hashMap2.put("ip","82.221.129.16");
//        hashMap3.put("md5","f74b90e77f38a06ff3f48d6de182a45a");
//        hashMap4.put("域名","manage-163-account.com");
//        hashMap5.put("域名","manage-163-account1.com");
//
//        List<HashMap> list = new ArrayList<>();
//        list.add(hashMap1);
//        list.add(hashMap2);
//        list.add(hashMap3);
//        list.add(hashMap4);
//        list.add(hashMap5);
//
//
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        HashMap resultSet = hugeGraphService.getDataByMultiple(list);
//    }

//    @Test
//    public void getAll(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        ResultSet resultSet = hugeGraphService.getAll();
//    }

//    public void getEdge(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        HashMap hashMap = hugeGraphService.getEdgeByName("DarkHotel");
//
//        System.out.println(hashMap.size());
//    }

//    public void insertMD5Vertex(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String s = hugeGraphService.insertMD5Vertex("12345678",65536,"","",
//                "","下载");
//
//        System.out.println("****************************" + s +"************************");
//    }

//    public void insertIPVertex(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String s = hugeGraphService.insertIPVretex("10.10.10.10","中国","","非恶意节点");
//
//        System.out.println("****************************" + s +"************************");
//    }

//    public void insertAccountVertex(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String s = hugeGraphService.insertAccountVretex("123456@qq.com","","腾讯","中国");
//
//        System.out.println("****************************" + s +"************************");
//    }

//    public void insertTechVertex(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String s = hugeGraphService.insertTechVretex("漏洞溢出攻击","漏洞利用","CVE-20200108-0001","普通用户权限");
//
//        System.out.println("****************************" + s +"************************");
//    }

//    public void insertDomainVertex(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String s = hugeGraphService.insertDomainVretex("qq.com","中国","可访问","AABB","AABB","非恶意域名");
//
//        System.out.println("****************************" + s +"************************");
//    }

//    public void insertURLVertex(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String s = hugeGraphService.insertURLVretex("www.zone.sina.com", "恶意");
//
//        System.out.println("****************************" + s +"************************");
//    }

//    public void insertPeopleVertex(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String s = hugeGraphService.insertPeopleVretex("01010101", "SaoDiSeng","中国",
//                "政府职员","扫地僧");
//
//        System.out.println("****************************" + s +"************************");
//    }
//    public void insertOrgVertex(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String s = hugeGraphService.insertOrganizationVretex("CETC","企业","中国","",
//                "中国","","涉我");
//
//        System.out.println("****************************" + s +"************************");
//    }
//
//    public void insertEdge(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String s = hugeGraphService.insertEdge("qq.com","DarkHotel","域名所属组织");
//
//        System.out.println("****************************" + s +"************************");
//    }

//    public void searchByCondition(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        HashMap hashMap = hugeGraphService.searchByCondition("DarkHotel","域名");
//        System.out.println("****************************************************");
//    }
//        public void searchByVertexConditionList(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        List<String> typeList = new ArrayList<>();
//        typeList.add("域名");
//        typeList.add("MD5");
//        HashMap hashMap = hugeGraphService.searchByVertexConditionList("IP","ip","82.221.129.16",typeList);
//        System.out.println("****************************************************");
//    }

//    public void searchByEdgeConditionList(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        List<String>typeList = new ArrayList<>();
//        typeList.add("MD5通联IP");
//        typeList.add("解析的IP地址");
//        HashMap hashMap = hugeGraphService.searchByEdgeConditionList("IP","ip","82.221.129.16",typeList);
//        System.out.println("****************************************************");
//    }

    public void searchByMultipleAndSteps(){
        HugeGraphService hugeGraphService = new HugeGraphService();
        HashMap hashMap1 = new HashMap();
        HashMap hashMap2 = new HashMap();

        hashMap1.put("硬件","hardware001");
        hashMap2.put("域名","uu.manage-163-account.com");

        List<HashMap> list = new ArrayList<>();
        list.add(hashMap1);
        list.add(hashMap2);
        HashMap hashMap = hugeGraphService.searchByMultipleAndSteps(list,1);
        System.out.println("****************************************************");
    }

//    public void searchByVetexsAndEdgesAndSteps(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        HashMap hashMap1 = new HashMap();
//        HashMap hashMap2 = new HashMap();
//
//        hashMap1.put("硬件","hardware001");
//        hashMap2.put("域名","uu.manage-163-account.com");
//
//        List<HashMap> list = new ArrayList<>();
//        list.add(hashMap1);
//        list.add(hashMap2);
//
//        List<String> vertexSetList = new ArrayList<>();
//        vertexSetList.add("IP");
//        vertexSetList.add("域名");
//        List<String> edgeSetList = new ArrayList<>();
//        edgeSetList.add("通联IP");
//        HashMap hashMap = hugeGraphService.searchByVetexsAndEdgesAndSteps(list,vertexSetList,edgeSetList,1);
//        System.out.println("****************************************************");
//    }
//    public void deleteVertex(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        String kkk = hugeGraphService.deleteVertex("硬件","hardware001");
//        System.out.println("****************************************************");
//    }

//    public void searchOrgByValue(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        List list = hugeGraphService.searchOrgByVertex("82.221.129.17");
//        System.out.println("****************************************************");
//    }
//
//    public void searchPathByValue(){
//        HugeGraphService hugeGraphService = new HugeGraphService();
//        ResultSet list = hugeGraphService.searchPathBy2Vertex("82.221.129.17", "DarkHotel");
//        System.out.println("****************************************************");
//    }

//    public void ceshi(){
//        HugeClient hugeClient = new HugeClient("http://192.168.10.148:8080","hugegraph");
//        GremlinManager gremlin = hugeClient.gremlin();
//
//        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('DarkHotel').bothE().sideEffect(store('a')).bothV().hasLabel('域名').sideEffect(store('b')).cap('a','b')").execute();
//
//        return;
//    }
}

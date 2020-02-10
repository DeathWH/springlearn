package com.wang.springlearn.Controller;

import com.baidu.hugegraph.driver.GremlinManager;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.structure.gremlin.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class hugegraph {
//    @Autowired
//    private SearchFromHugeGraph searchFromHugeGraph;
    @RequestMapping(value = "/demo")
    public ResultSet getAll(){
        HugeClient hugeClient = new HugeClient("http://192.168.10.168:8080", "hugegraph");
        GremlinManager gremlin = hugeClient.gremlin();
        ResultSet resultSet = gremlin.gremlin("g.V().hasValue('f74b90e77f38a06ff3f48d6de182a45a')").execute();

        return resultSet;
    }
}

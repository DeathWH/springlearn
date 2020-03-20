package com.wang.springlearn.Entity;

/**
 * @author wh
 */

public enum HugeGraphDic {

    账号("0","vertex_account.csv"),
    域名("1","vertex_domain.csv"),
    硬件("2","vertex_hardware.csv"),
    样本("3","vertex_md5.csv"),
    人员("4","vertex_person.csv"),
    软件("5","vertex_software.csv"),
    技术("6","vertex_tech.csv"),
    URL("7","vertex_url.csv"),
    组织("8","vertex_zz.csv"),
    IP("9","vertex_ip.csv"),
    IP所属人员("10","edge_IP所属人员.json"),
    IP通联URL("11","edge_IP通联URL.json"),
    IP注册账号("12","edge_IP注册账号.json"),
    MD5开发人员("13","edge_MD5开发人员.json"),
    MD5使用技术("14","edge_MD5使用技术.json"),
    MD5通联IP("15","edge_MD5通联IP.json"),
    MD5通联域名("16","edge_MD5通联域名.json"),
    MD5下载来源IP("17","edge_MD5下载来源IP.json"),
    MD5下载来源URL("18","edge_MD5下载来源URL.json"),
    人员所属组织("19","edge_人员所属组织.json"),
    人员掌握技术("20","edge_人员掌握技术.json"),
    软件登陆账号("21","edge_软件登陆账号.json"),
    软件来源IP("22","edge_软件来源IP.json"),
    通联IP("23","edge_通联IP.json"),
    同源关系("24","edge_同源关系.json"),
    虚实对应关系("25","edge_虚实对应关系.json"),
    硬件历史所属IP("26","edge_硬件历史所属IP.json"),
    域名解析IP地址("27","edge_域名解析IP地址.json"),
    域名注册人员("28","edge_域名注册人员.json"),
    域名注册账号("29","edge_域名注册账号.json"),
    账号所属人员("30","edge_账号所属人员.json"),
    子域名("31","edge_子域名.json"),
    组织历史样本("32","edge_组织历史样本.json"),
    组织使用IP("33","edge_组织使用IP.json"),
    组织使用URL("34","edge_组织使用URL.json"),
    组织使用技术("35","edge_组织使用技术.json"),
    组织使用域名("36","edge_组织使用域名.json"),
    组织使用账号("37","edge_组织使用账号.json"),
    ;


    public String getIndex() {
        return index;
    }

    public String getDataName() {
        return dataName;
    }

    private String index;
    private String dataName;

    private HugeGraphDic(String index, String dataName) {
        this.index = index;
        this.dataName = dataName;
    }




}

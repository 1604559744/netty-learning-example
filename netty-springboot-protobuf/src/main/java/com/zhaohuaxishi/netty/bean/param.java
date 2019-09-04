package com.zhaohuaxishi.netty.bean;

/**
 * @Author: zhaohuaxishi丶
 * @Description:
 * @Date: Creaded in 16:15 2019/8/30 0030
 */
public class param {
    /**
    * 下发时间
    */
    private String time;
    /**
    * 沉淀池编号
    */
    private String pool;
    /**
    * 采集编号
    */
    private String code;
    /**
    * 下发模型编号
    */
    private String tempoId;
    /**
    * 投药量
    */
    private String pac;
    /**
    * 0:成功，-1失败
    */
    private int retCode;
    /**
    * 命令结果描述
    */
    private String retVal;
    /**
    * 水厂编号
    */
    private String factory;
    /**
    * 命令类型
    */
    private String command;

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTempoId() {
        return tempoId;
    }

    public void setTempoId(String tempoId) {
        this.tempoId = tempoId;
    }

    public String getPac() {
        return pac;
    }

    public void setPac(String pac) {
        this.pac = pac;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getRetVal() {
        return retVal;
    }

    public void setRetVal(String retVal) {
        this.retVal = retVal;
    }
}

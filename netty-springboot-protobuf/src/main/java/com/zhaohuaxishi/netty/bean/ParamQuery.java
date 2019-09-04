package com.zhaohuaxishi.netty.bean;

/**
 * @Author: zhaohuaxishi丶
 * @Description:
 * @Date: Creaded in 16:25 2019/8/30 0030
 */
public class ParamQuery {
    /**
    * 指令序号
    */
    private String seq;
    /**
     * 设备编号（在下发时，指前置机编号）
     */
    private String code;
    /**
     * 设备类型（前置机）
     */
    private String type;
    /**
     * 网关编号
     */
    private String portId;
    /**
     * 水司编号
     */
    private String group;
    /**
     * 水厂编号
     */
    private String factory;

    private param paramBean;

    public param getParamBean() {
        return paramBean;
    }

    public void setParamBean(param paramBean) {
        this.paramBean = paramBean;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }
}

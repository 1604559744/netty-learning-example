package com.zhaohuaxishi.netty.dao;

import com.zhaohuaxishi.netty.bean.ParamQuery;
import com.zhaohuaxishi.netty.bean.param;
import com.zhaohuaxishi.netty.common.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveData {

    private static final Logger logger = LoggerFactory.getLogger(SaveData.class);

    private static AtomicInteger sum = new AtomicInteger(1);

    public static void main(String[] args) {
        System.out.println(new java.util.Date(System.currentTimeMillis()));
        System.out.println(new Timestamp(System.currentTimeMillis()));
    }

    private static void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if(preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("存储数据出现异常", e);
        }
    }

    public static void saveControllableSocketInfo(ParamQuery bean){
        String sql="update gdh_realtime set time=?,groupCode=?,factoryCode=?,poolNo=?,seq=?,code=?,value=?";
        PreparedStatement preparedStatement=null;
        Connection connection = JDBC.getConnect();
        try {
            param paramBean=bean.getParamBean();
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,paramBean.getTime());
            preparedStatement.setString(2,paramBean.getCode());
            preparedStatement.setString(3,paramBean.getFactory());
            preparedStatement.setString(4,paramBean.getPool());
            preparedStatement.setString(5,bean.getSeq());
            preparedStatement.setString(6,bean.getType());
            preparedStatement.setString(7,paramBean.getPac());
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            logger.error(Thread.currentThread().getName() + "存储出现异常", e);
        }finally {
            closePreparedStatement(preparedStatement);
        }
    }
}

package com.zhaohuaxishi.netty.common;



import com.zhaohuaxishi.netty.dao.JDBC;

import java.sql.Connection;

public class ConnectionHolder {

    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

    public static void add(Connection connection) {
        CONNECTION_HOLDER.set(connection);
    }

    public static Connection getCurrentConnection() {
        return CONNECTION_HOLDER.get();
    }

    public static void remove() {
        JDBC.close(CONNECTION_HOLDER.get());
        CONNECTION_HOLDER.remove();
    }
}

package com.moon.im.common.enums;

public enum RouteHashMethodEnum {

    /**
     * TreeMap
     */
    TREE(1,"com.moon.im.common.route.algorithm.consistenthash" +
            ".TreeMapConsistentHash"),

    /**
     * 自定义map
     */
    CUSTOMER(2,"com.moon.im.common.route.algorithm.consistenthash.xxxx"),

    ;


    private final int code;
    private final String clazz;

    public static RouteHashMethodEnum getHandler(int ordinal) {
        for (int i = 0; i < RouteHashMethodEnum.values().length; i++) {
            if (RouteHashMethodEnum.values()[i].getCode() == ordinal) {
                return RouteHashMethodEnum.values()[i];
            }
        }
        throw new IllegalArgumentException("Not Such Map");
    }

    RouteHashMethodEnum(int code, String clazz){
        this.code=code;
        this.clazz=clazz;
    }

    public String getClazz() {
        return clazz;
    }

    public int getCode() {
        return code;
    }
}

package com.moon.im.common.enums;

public enum ImUrlRouteWayEnum {

    /**
     * 随机
     */
    RANDOM(1,"com.moon.im.common.route.algorithm.random.RandomHandle"),


    /**
     * 1.轮训
     */
    LOOP(2,"com.moon.im.common.route.algorithm.loop.LoopHandle"),

    /**
     * HASH
     */
    HASH(3,"com.moon.im.common.route.algorithm.consistenthash.ConsistentHashHandle"),
    ;


    private final int code;
    private final String clazz;


    public static ImUrlRouteWayEnum getHandler(int ordinal) {
        for (int i = 0; i < ImUrlRouteWayEnum.values().length; i++) {
            if (ImUrlRouteWayEnum.values()[i].getCode() == ordinal) {
                return ImUrlRouteWayEnum.values()[i];
            }
        }
        throw new IllegalArgumentException("Not Such Handler");
    }

    ImUrlRouteWayEnum(int code, String clazz){
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

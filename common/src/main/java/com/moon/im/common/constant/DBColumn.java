package com.moon.im.common.constant;

/**
 * @author Chanmoey
 * @date 2023年01月29日
 */
public class DBColumn {

    private DBColumn() {
    }

    public static final String APP_ID = "app_id";

    /**
     * user
     */
    public static final String USER_ID = "user_id";

    public static final String DEL_FLAG = "del_flag";

    /**
     * friendship
     */
    public static final String FROM_ID = "from_id";

    public static final String TO_ID = "to_id";

    public static final String FS_STATUS = "status";

    /**
     * group
     */
    public static final String GROUP_ID = "group_id";

    public static final String GROUP_NAME = "group_name";

    public static final String ROLE = "role";

    public static final String MEMBER_ID = "member_id";

    public static final String GROUP_TYPE = "group_type";
}

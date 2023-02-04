package com.moon.im.common.util;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
public class Trans2StaticFinalStyle {

    private Trans2StaticFinalStyle(){}

    public static void mTrans2StaticFinalStyle(String text, String split, String join) {
        StringBuilder sb = new StringBuilder();
        String[] words = text.split(split);
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                sb.append(Character.toUpperCase(word.charAt(i)));
                if (i < word.length() - 1 && Character.isUpperCase(word.charAt(i + 1))) {
                    sb.append('_');
                }
            }
            sb.append(join);
        }
        System.out.println(sb);
    }

    public static void main(String[] args) {
        Trans2StaticFinalStyle.mTrans2StaticFinalStyle("ModifyUserAfter " +
                "CreateGroupAfter " +
                "UpdateGroupAfter " +
                "DestroyGroupAfter", " ", "\n");
    }
}

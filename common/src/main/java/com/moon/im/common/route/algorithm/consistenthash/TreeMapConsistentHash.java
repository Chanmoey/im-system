package com.moon.im.common.route.algorithm.consistenthash;

import com.moon.im.common.enums.UserErrorCode;
import com.moon.im.common.exception.ApplicationException;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {

    private final TreeMap<Long, String> treeMap = new TreeMap<>();

    private static final int VIRTUAL_NODE_SIZE = 2;

    @Override
    protected void add(long key, String value) {
        // 插入虚拟节点，避免Hash倾斜
        for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
            treeMap.put(super.hash("node" + key + "i"), value);
        }
        // 插入本身
        treeMap.put(super.hash(String.valueOf(key)), value);
    }

    @Override
    protected String getFirstNodeValue(String value) {

        if (treeMap.size() == 0) {
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }

        Long hash = super.hash(value);
        SortedMap<Long, String> last = treeMap.tailMap(hash);
        if (!last.isEmpty()) {
            return last.get(last.firstKey());
        }

        return treeMap.firstEntry().getValue();
    }

    @Override
    protected void processBefore() {
        treeMap.clear();
    }
}

package xyz.demj.utils;

import java.util.Collection;
import java.util.Map;

import xyz.demj.utils.BasicManager;

/**
 * Created by demj on 2016/5/15 0015.
 * Author: demj
 */
public class BasicManagerImpl<K,V> extends BasicManager<K,V> {
    @Override
    protected Operation<K, V> internalAddItem(V v) {
        return null;
    }

    @Override
    protected Operation<K, V> internalDeleteItem(K k) {
        return null;
    }

    @Override
    protected Operation<K, V> internalUpdateItem(K k, V v) {
        return null;
    }

    @Override
    protected Operation<K, V> internalGetItem(K k) {
        return null;
    }

    @Override
    protected Collection<Operation<K, V>> internalAddItems(Collection<? extends V> elements) {
        return null;
    }

    @Override
    protected Collection<Operation<K, V>> internalDeleteItems(Collection<? extends K> keys) {
        return null;
    }

    @Override
    protected Map<K, V> internalGetItems(Collection<? extends K> keys) {
        return null;
    }

    @Override
    protected Collection<Operation<K, V>> internalUpdateItems(Map<? extends K, ? extends V> map) {
        return null;
    }

    @Override
    protected Collection<Operation<K, V>> internalDoOtherActions(int type, Map<? extends K, ? extends V> map) {
        return null;
    }

    @Override
    protected Operation<K, V> internalDoOtherAction(int type, K k, V v) {
        return null;
    }
}

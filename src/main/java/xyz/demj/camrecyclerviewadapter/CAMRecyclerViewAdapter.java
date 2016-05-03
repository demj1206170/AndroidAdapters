package xyz.demj.camrecyclerviewadapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by demj on 2016/5/1 0001.
 * Author: demj
 *
 * @param <E> the element's type
 */
public abstract class CAMRecyclerViewAdapter<E, VH extends CAMRecyclerViewAdapter.CAMViewHolder<E>> extends RecyclerView.Adapter<VH> {

    private static final String ADAPTER_TAG = "CAMRecyclerViewAdapter";
    private static final String DEFAULT_ITEM_SELECT_STATE_CHANGED_MSG = "you see this, because you hasn't set onItemSelectStateChangedListener yet.";
    private static final String NULL_LISTENER_ITEM_SELECT_STATE_CHANGED_MSG = "you see this, because you  set onItemSelectStateChangedListener with null.";
    private static final SimpleOnItemSelectStateChangedListener DEFAULT_ON_ITEM_SELECT_STATE_CHANGED_LISTENER = new SimpleOnItemSelectStateChangedListener(DEFAULT_ITEM_SELECT_STATE_CHANGED_MSG);
    private static final SimpleOnItemSelectStateChangedListener NULL_ON_ITEM_SELECT_STATE_CHANGED_LISTENER = new SimpleOnItemSelectStateChangedListener(NULL_LISTENER_ITEM_SELECT_STATE_CHANGED_MSG);
    private static final String DEFAULT_ITEM_CLICK_MSG = "you see this, because you hasn't set onItemClickListener yet.";
    private static final String NULL_LISTENER_ITEM_CLICK_MSG = "you see this, because you set onItemClickListener with null.";
    private static final SimpleOnItemClickListener DEFAULT_ON_ITEM_CLICK_LISTENER = new SimpleOnItemClickListener(DEFAULT_ITEM_CLICK_MSG);
    private static final SimpleOnItemClickListener NULL_ON_ITEM_CLICK_LISTENER = new SimpleOnItemClickListener(NULL_LISTENER_ITEM_CLICK_MSG);
    private static final String DEFAULT_ITEM_LONG_CLICK_MSG = "you see this, because you hasn't set onItemLongClickListener yet.";
    private static final String NULL_LISTENER_ITEM_LONG_CLICK_MSG = "you see this, because you set onItemLongClickListener with null.";
    private static final SimpleOnItemLongClickListener DEFAULT_ITEM_LONG_CLICK_LISTENER = new SimpleOnItemLongClickListener(DEFAULT_ITEM_LONG_CLICK_MSG);
    private static final SimpleOnItemLongClickListener NULL_ITEM_LONG_CLICK_LISTENER = new SimpleOnItemLongClickListener(NULL_LISTENER_ITEM_LONG_CLICK_MSG);
    private static final String DEFAULT_HANDLE_CLICK_MSG = "you see this, because you hasn't set setOnHandleClickListener yet,\n but you return true in shouldHandleClick method.";
    private static final String NULL_LISTENER_HANDLE_CLICK_MSG = "you see this, because you set setOnHandleClickListener with null,\n" +
            " but you return true in shouldHandleClick method.";
    private static final SimpleHandleClickListener DEFAULT_HANDLE_CLICK_LISTENER = new SimpleHandleClickListener(DEFAULT_HANDLE_CLICK_MSG);
    private static final SimpleHandleClickListener NULL_HANDLE_CLICK_LISTENER = new SimpleHandleClickListener(NULL_LISTENER_HANDLE_CLICK_MSG);
    private final List<E> mElementList;
    private final ArrayMap<E, Boolean> mSelectedItems = new ArrayMap<>();
    private OnHandleClickListener mOnHandleClickListener = DEFAULT_HANDLE_CLICK_LISTENER;
    private OnItemSelectStateChangedListener mOnItemSelectStateChangedListener = DEFAULT_ON_ITEM_SELECT_STATE_CHANGED_LISTENER;
    private OnItemClickListener mOnItemClickListener = DEFAULT_ON_ITEM_CLICK_LISTENER;
    private OnItemLongClickListener mOnItemLongClickListener = DEFAULT_ITEM_LONG_CLICK_LISTENER;
    private boolean isInContentActionMode;


    public CAMRecyclerViewAdapter() {
        mElementList = new LinkedList<>();
    }

    /**
     * set to listen handleClick event when return true in {@link CAMRecyclerViewAdapter#shouldHandleClick}.
     *
     * @param onHandleClickListener
     */
    public void setOnHandleClickListener(@Nullable OnHandleClickListener onHandleClickListener) {
        if (onHandleClickListener == null)
            mOnHandleClickListener = NULL_HANDLE_CLICK_LISTENER;
        else
            mOnHandleClickListener = onHandleClickListener;
    }

    /**
     * set to listen item select state changed event.
     *
     * @param onItemSelectStateChangedListener
     */
    public void setOnItemSelectStateChangedListener(@Nullable OnItemSelectStateChangedListener onItemSelectStateChangedListener) {
        if (onItemSelectStateChangedListener == null)
            mOnItemSelectStateChangedListener = NULL_ON_ITEM_SELECT_STATE_CHANGED_LISTENER;
        else
            mOnItemSelectStateChangedListener = onItemSelectStateChangedListener;
    }

    /**
     * set to listen item click event.
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null)
            mOnItemClickListener = NULL_ON_ITEM_CLICK_LISTENER;
        else
            mOnItemClickListener = onItemClickListener;
    }

    /**
     * set to listen item long click event.
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener onItemLongClickListener) {
        if (onItemLongClickListener == null)
            mOnItemLongClickListener = NULL_ITEM_LONG_CLICK_LISTENER;
        else
            mOnItemLongClickListener = onItemLongClickListener;
    }

    /**
     * query adapter has attach to content action mode.
     *
     * @return true yes,false no.
     */
    public boolean isInContentActionMode() {
        return isInContentActionMode;
    }

    /**
     * mark adapter attach to content action mode,
     * this should be used in child adapter only.
     *
     * @param inContentActionMode
     */
    protected void setInContentActionMode(boolean inContentActionMode) {
        isInContentActionMode = inContentActionMode;
    }

    /**
     * add an E instance into internal list.
     *
     * @param e      the E's instance.
     * @param notify notify whether or not notify DataObserver such as RecyclerView
     *               to handle data set changed event.Note, if you add element
     *               in the other thread except main thread, you should always set this is false.
     */
    public void add(E e, boolean notify) {
        add(e, mElementList.size(), notify);
    }

    /**
     * add an E instance into internal list.
     *
     * @param e the E's instance.
     */
    public void add(E e) {
        add(e, mElementList.size(), true);
    }

    /**
     * add an E instance into internal list.
     *
     * @param e        the E's instance.
     * @param position the insert position.
     * @param notify   notify whether or not notify DataObserver such as RecyclerView
     *                 to handle data set changed event.Note, if you add element
     *                 in the other thread except main thread, you should always set this is false.
     */
    public void add(E e, int position, boolean notify) {
        if (!validateInsertPosition(position))
            return;
        if (e != null) {
            mElementList.add(position,e);
            if (notify) {
                notifyItemInserted(mElementList.size());
            }
        }
    }

    /**
     * add an E instance into internal list.
     *
     * @param e        the E's instance.
     * @param position the insert position.
     */
    public void add(E e, int position) {
        add(e, position, true);
    }

    /**
     * add more than one element by Collection.
     *
     * @param collection the E elements container.
     * @param notify     notify whether or not notify DataObserver such as RecyclerView
     *                   to handle data set changed event.Note, if you add element
     *                   in the other thread except main thread, you should always set this is false.
     */
    public void addAll(Collection<E> collection, boolean notify) {
        collection = filterElements(collection);
        int preSize = mElementList.size();
        mElementList.addAll(collection);
        if (notify) {
            notifyItemRangeInserted(preSize, collection.size());
        }
    }

    /**
     * add more than one element by Collection.
     *
     * @param collection the E elements container.
     */
    public void addAll(Collection<E> collection) {
        addAll(collection, true);
    }

    /**
     * @param position the element' position you want removed.
     * @param notify   notify whether or not notify DataObserver such as RecyclerView
     *                 to handle data set changed event.Note, if you add element
     *                 in the other thread except main thread, you should always set this is false.
     */
    public void remove(int position, boolean notify) {
        if (!validatePosition(position))
            return;
        mElementList.remove(position);
        if (notify) {
            notifyItemRemoved(position);
        }
    }

    /**
     * @param position the element' position you want removed.
     */
    public void remove(int position) {
        remove(position, true);
    }


    /**
     * remove all element int internal list.
     *
     * @param notify notify whether or not notify DataObserver such as RecyclerView
     *               to handle data set changed event.Note, if you add element
     *               in the other thread except main thread, you should always set this is false.
     */
    public void removeAll(boolean notify) {
        int size = mElementList.size();
        mElementList.clear();
        if (notify) {
            notifyItemRangeRemoved(0, size);
        }
    }

    /**
     * remove all element int internal list.
     */
    public void removeAll() {
        removeAll(true);
    }

    /**
     * return the element count hold by internal list.
     *
     * @return count.
     */
    @Override
    public int getItemCount() {
        return mElementList.size();
    }

    /**
     * this method is qualify with final,
     * so you should use {@link CAMRecyclerViewAdapter#realBindViewHolder} to bind you ViewHolder.
     *
     * @param holder
     * @param position
     */
    @Override
    public final void onBindViewHolder(VH holder, int position) {
        E e = mElementList.get(position);
        holder.mPositionTag = e;
        realBindViewHolder(holder, position);
    }

    /**
     * query whether the specify element is selected in CAM.
     *
     * @param position the element's position you want query.
     * @return true is selected, false is not.
     */
    public boolean isItemSelected(int position) {
        E e = mElementList.get(position);
        if (e == null)
            return false;
        Boolean b = mSelectedItems.get(e);
        if (b == null)
            return false;
        return b;
    }

    /**
     * replace the specify element with new element.
     *
     * @param e        the new element.
     * @param position the specify element's position
     * @param notify   notify whether or not notify DataObserver such as RecyclerView
     *                 to handle data set changed event.Note, if you add element
     *                 in the other thread except main thread, you should always set this is false.
     */
    void set(E e, int position, boolean notify) {
        if (!validatePosition(position))
            return;
        if (e != null) {
            E preE = mElementList.set(position, e);
            if (isInContentActionMode) {
                Boolean b = mSelectedItems.get(preE);
                if (b != null) {
                    mSelectedItems.remove(preE);
                    mSelectedItems.put(e, b);
                }
            }
            if (notify) {
                notifyItemChanged(position);
            }
        }
    }

    /**
     * filter array by pass those null element.
     *
     * @param elements the array want to filter.
     * @return new filtered without null element array,or null if pass a null elements.
     */
    private E[] filterElements(E[] elements) {
        if (elements == null)
            return null;
        List<E> arrayList = new ArrayList<>(elements.length);
        for (E element : elements) {
            if (element != null)
                arrayList.add(element);
        }
        return (E[]) arrayList.toArray();
    }

    /**
     * filter collection by pass those null element.
     *
     * @param collection the collection want to filter.
     * @return new filtered without null element collection.
     */
    private List<E> filterElements(Collection<E> collection) {
        List<E> list = Collections.emptyList();
        if (collection == null)
            return list;
        list = new ArrayList<>(collection.size());
        for (E e : collection) {
            if (e != null) {
                list.add(e);
            }
        }
        return list;
    }

    /**
     * add more than one element by a array.
     *
     * @param elements the array that contain E's elements.
     * @param startPos insert start position.
     * @param notify   notify whether or not notify DataObserver such as RecyclerView
     *                 to handle data set changed event.Note, if you add element
     *                 in the other thread except main thread, you should always set this is false.
     */
    public void addItems(E[] elements, int startPos, boolean notify) {
        if (elements == null || !validateInsertPosition(startPos))
            return;
        elements = filterElements(elements);
        ArrayList<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        mElementList.addAll(startPos, list);
        if (notify) {
            notifyItemRangeInserted(startPos, elements.length);
        }
    }

    /**
     * add more than one element by a array.
     *
     * @param elements the array that contain E's elements.
     * @param startPos insert start position.
     */
    public void addItems(E[] elements, int startPos) {
        addItems(elements, startPos, true);
    }

    /**
     * add more than one element by a array.
     *
     * @param elements the array that contain E's elements.
     * @param notify   notify whether or not notify DataObserver such as RecyclerView
     *                 to handle data set changed event.Note, if you add element
     *                 in the other thread except main thread, you should always set this is false.
     */
    public void addItems(E[] elements, boolean notify) {
        addItems(elements, 0, notify);
    }

    /**
     * add more than one element by a array.
     *
     * @param elements the array that contain E's elements.
     */
    public void addItems(E[] elements) {
        addItems(elements, 0, true);
    }

    /**
     * remove items from internal list by collection hold element.
     *
     * @param collection            the elements you want remove.
     * @param notify                notify whether or not notify DataObserver such as RecyclerView
     *                              to handle data set changed event.Note, if you add element
     *                              in the other thread except main thread, you should always set this is false.
     * @param notifyAfterAllRemoved whether or not after remove all specify elements then notify DataObserver.
     */
    public void removeItems(Collection<E> collection, boolean notify, boolean notifyAfterAllRemoved) {
        collection = filterElements(collection);
        for (E e : collection) {
            int position = mElementList.indexOf(e);
            if (position == -1)
                continue;
            mElementList.remove(e);
            if (notify && !notifyAfterAllRemoved) {
                notifyItemRemoved(position);
            }
        }
        if (notify && notifyAfterAllRemoved)
            notifyDataSetChanged();
    }

    /**
     * remove items from internal list by collection hold element.
     *
     * @param collection the elements you want remove.
     * @param notify     notify whether or not notify DataObserver such as RecyclerView
     *                   to handle data set changed event.Note, if you add element
     *                   in the other thread except main thread, you should always set this is false.
     */
    public void removeItems(Collection<E> collection, boolean notify) {
        removeItems(collection, notify, false);
    }

    /**
     * remove items from internal list by collection hold element.
     *
     * @param collection the elements you want remove.
     */
    public void removeItems(Collection<E> collection) {
        removeItems(collection, true, false);
    }

    /**
     * remove items from internal list by array hold element.
     *
     * @param elements              the elements you want remove.
     * @param notify                notify whether or not notify DataObserver such as RecyclerView
     *                              to handle data set changed event.Note, if you add element
     *                              in the other thread except main thread, you should always set this is false.
     * @param notifyAfterAllRemoved whether or not after remove all specify elements then notify DataObserver.
     */
    public void removeItems(E[] elements, boolean notify, boolean notifyAfterAllRemoved) {
        elements = filterElements(elements);
        if (elements != null) {
            for (E e : elements) {
                int position = mElementList.indexOf(e);
                if (position == -1)
                    continue;
                mElementList.remove(e);
                if (notify && !notifyAfterAllRemoved) {
                    notifyItemRemoved(position);
                }
            }
            if (notify && notifyAfterAllRemoved)
                notifyDataSetChanged();
        }
    }

    /**
     * remove items from internal list by array hold element.
     *
     * @param elements the elements you want remove.
     * @param notify   notify whether or not notify DataObserver such as RecyclerView
     *                 to handle data set changed event.Note, if you add element
     *                 in the other thread except main thread, you should always set this is false.
     */
    public void removeItems(E[] elements, boolean notify) {
        removeItems(elements, notify, false);
    }

    /**
     * remove items from internal list by array hold element.
     *
     * @param elements the elements you want remove.
     */
    public void removeItems(E[] elements) {
        removeItems(elements, true, false);
    }

    /**
     * remove items from internal list by array hold element's position.
     *
     * @param positions             the elements' position you want remove.
     * @param notify                notify whether or not notify DataObserver such as RecyclerView
     *                              to handle data set changed event.Note, if you add element
     *                              in the other thread except main thread, you should always set this is false.
     * @param notifyAfterAllRemoved whether or not after remove all specify elements then notify DataObserver.
     */
    public void removeItemsByPosition(int[] positions, boolean notify, boolean notifyAfterAllRemoved) {
        if (positions == null)
            return;
        ArrayList<E> arrayList = new ArrayList<>(positions.length);
        for (int i : positions) {
            if (!validatePosition(i))
                continue;
            E e = mElementList.get(i);
            arrayList.add(e);
        }
        for (E e : arrayList) {
            int position = mElementList.indexOf(e);
            mElementList.remove(e);
            if (notify && !notifyAfterAllRemoved) {
                notifyItemRemoved(position);
            }
        }
        if (notify && notifyAfterAllRemoved) {
            notifyDataSetChanged();
        }
    }

    /**
     * remove items from internal list by array hold element's position.
     *
     * @param positions the elements' position you want remove.
     * @param notify    notify whether or not notify DataObserver such as RecyclerView
     *                  to handle data set changed event.Note, if you add element
     *                  in the other thread except main thread, you should always set this is false.
     */
    public void removeItemsByPosition(int[] positions, boolean notify) {
        removeItemsByPosition(positions, notify, false);
    }

    /**
     * remove items from internal list by array hold element's position.
     *
     * @param positions the elements' position you want remove.
     */
    public void removeItemsByPosition(int[] positions) {
        removeItemsByPosition(positions, true, false);
    }

    /**
     * remove items from internal list by collection hold element's position.
     *
     * @param positions             the elements' position you want remove.
     * @param notify                notify whether or not notify DataObserver such as RecyclerView
     *                              to handle data set changed event.Note, if you add element
     *                              in the other thread except main thread, you should always set this is false.
     * @param notifyAfterAllRemoved whether or not after remove all specify elements then notify DataObserver.
     */
    public void removeItemsByPosition(Collection<Integer> positions, boolean notify, boolean notifyAfterAllRemoved) {
        if (positions != null) {
            ArrayList<Integer> list = new ArrayList<>(positions);
            Integer[] integers = new Integer[list.size()];
            list.toArray(integers);
            int[] pos = new int[integers.length];
            System.arraycopy(integers, 0, pos, 0, integers.length);
            removeItemsByPosition(pos, notify, notifyAfterAllRemoved);
        }
    }

    /**
     * remove items from internal list by collection hold element's position.
     *
     * @param positions the elements' position you want remove.
     * @param notify    notify whether or not notify DataObserver such as RecyclerView
     *                  to handle data set changed event.Note, if you add element
     *                  in the other thread except main thread, you should always set this is false.
     */
    public void removeItemsByPosition(Collection<Integer> positions, boolean notify) {
        removeItemsByPosition(positions, notify, false);
    }

    /**
     * remove items from internal list by collection hold element's position.
     *
     * @param positions the elements' position you want remove.
     */
    public void removeItemsByPosition(Collection<Integer> positions) {
        removeItemsByPosition(positions, true, false);
    }

    /**
     * set specify element is selected when in CAM.
     *
     * @param position the element's position.
     */
    public void setSelectedItem(int position) {
        setItemSelection(position, true);
    }

    /**
     * remove specify element is selected when in CAM.
     *
     * @param position the element's position.
     */
    public void removeSelectedItem(int position) {
        setItemSelection(position, false);
    }

    /**
     * set specify element is selected or not.
     *
     * @param position the element's position.
     * @param selected true is selected,false not.
     */
    public void setItemSelection(int position, boolean selected) {
        setItemSelection(position, selected, true);
    }

    /**
     * set specify element is selected or not.
     *
     * @param position the element's position.
     * @param selected true is selected,false not.
     * @param notify   notify whether or not notify DataObserver such as RecyclerView
     *                 to handle data set changed event.Note, if you add element
     *                 in the other thread except main thread, you should always set this is false.
     */
    public void setItemSelection(int position, boolean selected, boolean notify) {
        E e = mElementList.get(position);
        if (e != null) {
            mSelectedItems.put(e, selected);
            if (notify)
                notifyItemChanged(position);
            mOnItemSelectStateChangedListener.onItemCheckStateChanged(position, selected);
        }
    }

    /**
     * set specify elements is selected.
     *
     * @param positions the element's positions.
     * @param notify    notify whether or not notify DataObserver such as RecyclerView
     *                  to handle data set changed event.Note, if you add element
     *                  in the other thread except main thread, you should always set this is false.
     */
    public void setSelectedItems(Collection<Integer> positions, boolean notify) {
        if (positions == null)
            return;
        int size = mElementList.size();
        int[] selectedList = new int[positions.size()];
        int index = -1;
        for (int i : positions) {
            if (i < 0 || i >= size) {
                Log.w(ADAPTER_TAG, "set selected item in position " + i + " is out of index,ignored it");
                continue;
            }
            E e = mElementList.get(i);
            mSelectedItems.put(e, true);
            selectedList[++index] = i;
        }
        if (notify) {
            notifyDataSetChanged();
            int[] finalSelectedItems = new int[index + 1];
//            for (int i = 0; i < index + 1; i++) {
//                finalSelectedItems[i] = selectedList[i];
//            }
            System.arraycopy(selectedList, 0, finalSelectedItems, 0, index + 1);
            notifyItemCheckedRangeStateChanged(finalSelectedItems, null, true);
        }
    }

    /**
     * set all item is selected when in CAM.
     *
     * @param notify notify whether or not notify DataObserver such as RecyclerView
     *               to handle data set changed event.Note, if you add element
     *               in the other thread except main thread, you should always set this is false.
     */
    public void setSelectAll(boolean notify) {
        int size = mElementList.size();
        int[] selectedItems = new int[size];
        for (int i = 0; i < size; i++) {
            selectedItems[i] = i;
            mSelectedItems.put(mElementList.get(i), true);
        }
        if (notify) {
            notifyItemRangeChanged(0, size);
            notifyItemCheckedRangeStateChanged(selectedItems, null, true);
        }
    }

    /**
     * set all item is selected when in CAM.
     */
    public void setSelectAll() {
        setSelectAll(true);
    }

    /**
     * set all item is not selected when in CAM or will exit CAM.
     *
     * @param notify notify whether or not notify DataObserver such as RecyclerView
     *               to handle data set changed event.Note, if you add element
     *               in the other thread except main thread, you should always set this is false.
     */
    public void clearSelection(boolean notify) {
        mSelectedItems.clear();
        if (notify) {
            notifyDataSetChanged();
            notifyItemCheckedRangeStateChanged(null, null, false);
        }
    }

    /**
     * query the selected item count.
     *
     * @return selected item count.
     */
    public int getSelectedItemCount() {
        int count = 0;
        for (E e : mSelectedItems.keySet()) {
            if (mSelectedItems.get(e)) {
                ++count;
            }
        }
        return count;
    }

    /**
     * query selected item's positions.
     *
     * @return all selected item's position.
     */
    public ArrayList<Integer> getSelectedItemPositions() {
        LinkedList<Integer> selectedItemList = new LinkedList<>();
        for (E e : mSelectedItems.keySet()) {
            if (mSelectedItems.get(e)) {
                selectedItemList.add(mElementList.indexOf(e));
            }
        }
        Log.w(ADAPTER_TAG, "before sort list is " + selectedItemList);
        Collections.sort(selectedItemList);
        Log.w(ADAPTER_TAG, "sorted list is " + selectedItemList);
        return new ArrayList<>(selectedItemList);
    }

    /**
     * query all selected item.
     *
     * @return all selected item.
     */
    public ArrayList<E> getSelectedItems() {
        ArrayList<Integer> selectedPositions = getSelectedItemPositions();
        int size = selectedPositions.size();
        ArrayList<E> selectedItems = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            selectedItems.add(mElementList.get(selectedPositions.get(i)));
        }
        return selectedItems;
    }


    /**
     * set specify elements are selected in CAM.
     *
     * @param positions the element's positions.
     */
    public void setSelectedItems(Collection<Integer> positions) {
        setSelectedItems(positions, true);
    }

    /**
     * set all item is not selected when in CAM or will exit CAM.
     */
    public void clearSelection() {
        clearSelection(true);
    }

    /**
     * used to call mOnItemSelectStateChangedListener methods.
     *
     * @param positions    the state changed items' position,
     *                     if positions is null that will notify all items state to ifBeforeNull.
     * @param states       the items state.
     * @param ifBeforeNull if states is null,then will create a new states array that fill with it.
     */
    private void notifyItemCheckedRangeStateChanged(int[] positions, boolean[] states, boolean ifBeforeNull) {
        int size;
        if (positions == null) {
            size = mElementList.size();
            positions = new int[size];
            states = new boolean[size];
            for (int i = 0; i < size; i++)
                positions[i] = i;
        } else size = positions.length;

        if (states == null) {
            states = new boolean[size];
            for (int i = 0; i < size; i++)
                states[i] = ifBeforeNull;
        }
        mOnItemSelectStateChangedListener.onItemsCheckStateChanged(positions, states);
    }

    /**
     * attach to content action mode,this used when you call startActionMode or startSupportActionMode.
     * or when action mode's CallBack's onCreateActionMode or onPrepareActionMode method.
     * call this let adapter into CAM.
     *
     * @param triggerPosition the trigger start action mode item's position.
     */
    public void attachToActionMode(int triggerPosition) {
        mSelectedItems.clear();
        isInContentActionMode = true;
        if (validatePosition(triggerPosition)) {
            setItemSelection(triggerPosition, true, true);
        }
    }

    /**
     * this is used to notify adapter application is exit CAM,and do something.
     */
    public void detachFromActionMode() {
        isInContentActionMode = false;
        clearSelection(true);
    }

    /**
     * this is marked with final, you can use {@link CAMRecyclerViewAdapter#realCreateViewHolder} to create a ViewHolder.
     *
     * @param parent
     * @param viewType
     * @return
     */
    /**/
    @Override
    final public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH viewHolder = realCreateViewHolder(parent, viewType);
        if (viewHolder != null)
            viewHolder.mAdapter = this;
        return viewHolder;
    }

    /**
     * this is used to toggle item's select state when in CAM.
     *
     * @param position the item's position.
     */
    /**/
    protected void toggleSelection(int position) {
        if (!validatePosition(position))
            return;
        setItemSelection(position, !isItemSelected(position), true);
    }


    private boolean validatePosition(int position) {
        return !(position < 0 || position >= mElementList.size());
    }

    private boolean validateInsertPosition(int position) {
        return !(position < 0 || position > mElementList.size());
    }

    /**
     * the {@link CAMRecyclerViewAdapter} you create should implement this method and real do bind viewHolder here.
     *
     * @param holder
     * @param position
     */
    protected abstract void realBindViewHolder(VH holder, int position);

    /**
     * the {@link CAMRecyclerViewAdapter} you create should implement,and return an not null instance.
     *
     * @param parent   the view's parent.
     * @param viewType the view's type.
     * @return a not null {@link CAMViewHolder<E>}.
     */
    @NonNull
    protected abstract VH realCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * this method can override by child which told you want handle view click event,and before then
     * you should call {@link CAMViewHolder<E>#addHandleClickView} let that view
     * can handle click event or you can call {@link View#setOnClickListener(View.OnClickListener)}
     * and pass a {@link CAMViewHolder<E>} instance to that view.
     *
     * @param view     the view that you want handle click event alone.
     * @param position the view's parent's position.
     * @return
     */
    protected boolean shouldHandleClick(View view, int position) {
        return false;
    }

    /**
     * get specify position's element.
     *
     * @param position
     * @return
     */
    public E get(int position) {
        return mElementList.get(position);
    }

    /**
     * this is used to handle view click event alone.
     */
    public interface OnHandleClickListener {
        /**
         * @param v        the v you want to handle click event alone.
         * @param position the v's parent's position,in other word,it is item's position.
         */
        void handleClick(View v, int position);
    }

    /**
     * this is used to handle whole ViewHolder's click event.
     */
    public interface OnItemClickListener {
        /**
         * @param view     the root view.
         * @param position the item's position.
         */
        void onItemClick(View view, int position);
    }

    /**
     * this is used to handle whole ViewHolder's long click event.
     */
    public interface OnItemLongClickListener {
        /**
         * @param view     the root view.
         * @param position the item's position.
         */
        void onItemLongClick(View view, int position);
    }

    /**
     * this is used to listen item select state changed event when adapter is attach to CAM.
     */
    public interface OnItemSelectStateChangedListener {
        /**
         * @param position   the position is the item that select state changed.
         * @param isSelected the item is selected or not.
         */
        void onItemCheckStateChanged(int position, boolean isSelected);

        /**
         * @param positions the positions is the item that select state changed.
         * @param states    the items is selected or not.
         */
        void onItemsCheckStateChanged(int[] positions, boolean[] states);
    }

    /**
     * the ViewHolder extends {@link android.support.v7.widget.RecyclerView.ViewHolder}
     *
     * @param <E> the data's type.
     */
    public static class CAMViewHolder<E> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private static final String TAG = "CAMViewHolder";
        //private final View mRootView;
        protected E mPositionTag;
        protected CAMRecyclerViewAdapter<E, ? extends CAMViewHolder<E>> mAdapter;
        private boolean isClickable = true;
        private boolean isClickableInCAM = true;
        private boolean canTriggerCAM = true;
        private boolean isLongClickable = true;

        public CAMViewHolder(View itemView) {
            super(itemView);
            //mRootView = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * this is used to handle click event alone.
         *
         * @param view
         */
        public void addHandleClickView(View view) {
            if (view != null)
                view.setOnClickListener(this);
        }

        /**
         * handle view's click event
         *
         * @param v the clicked view.
         */
        @Override
        public void onClick(View v) {
            if (!isClickable)
                return;
            int position = mAdapter.mElementList.indexOf(mPositionTag);
            if (position < 0) {
                Log.w(TAG, "position < 0 while handle view click,the click event will ignored");
                return;
            }
            if (mAdapter.shouldHandleClick(v, position)) {
                mAdapter.mOnHandleClickListener.handleClick(v, position);
                Log.w(TAG, "child CAMRecyclerViewAdapter has handle click event");
                return;
            }
            if (!mAdapter.isInContentActionMode) {
                mAdapter.mOnItemClickListener.onItemClick(v, position);
                Log.w(TAG, "has handle click event in item click listener");
            } else {
                if (isClickableInCAM) {
                    mAdapter.toggleSelection(position);
                    Log.w(TAG, "call adapter toggle selection");
                }
            }
        }

        /**
         * set this ViewHolder's rootView can click when {@link CAMRecyclerViewAdapter} is attach to CAM.
         *
         * @param clickableInCAM can or not.
         */
        public void setClickableInCAM(boolean clickableInCAM) {
            isClickableInCAM = clickableInCAM;
        }

        /**
         * set this ViewHolder's rootView can trigger CAM.
         *
         * @param canTriggerCAM can or not.
         */
        public void setCanTriggerCAM(boolean canTriggerCAM) {
            this.canTriggerCAM = canTriggerCAM;
        }

        /**
         * set this ViewHolder's rootView can handle long click event.
         *
         * @param longClickable can or not.
         */
        public void setLongClickable(boolean longClickable) {
            isLongClickable = longClickable;
        }

        /**
         * set this ViewHolder's root can handle click event.
         *
         * @param clickable can or not.
         */
        public void setClickable(boolean clickable) {
            isClickable = clickable;
        }

        /**
         * handle view's long click event
         *
         * @param v the long clicked view.
         */
        @Override
        public boolean onLongClick(View v) {

            if (!isClickable ||
                    !isLongClickable ||
                    !canTriggerCAM)
                return false;

            int position = mAdapter.mElementList.indexOf(mPositionTag);
            if (position < 0) {
                Log.w(TAG, "position < 0 while handle view long click,the long  click event will ignored");
                return false;
            }
            if (!mAdapter.isInContentActionMode) {
                mAdapter.mOnItemLongClickListener.onItemLongClick(v, position);
                Log.w(TAG, "long click event is handled in position " + position);
            }
            return true;
        }
    }

    private static class SimpleOnItemSelectStateChangedListener implements OnItemSelectStateChangedListener {
        private final String mString;

        public SimpleOnItemSelectStateChangedListener(String msg) {
            mString = msg;
        }

        @Override
        public void onItemCheckStateChanged(int position, boolean isSelected) {
            Log.w(ADAPTER_TAG, mString + " position is " + position);
        }

        @Override
        public void onItemsCheckStateChanged(int[] positions, boolean[] states) {
            Log.w(ADAPTER_TAG, mString);
        }
    }

    private static class SimpleOnItemClickListener implements OnItemClickListener {

        final String mString;

        SimpleOnItemClickListener(String msg) {
            mString = msg;
        }

        @Override
        public void onItemClick(View view, int position) {
            Log.w(ADAPTER_TAG, mString + " position is " + position);
        }
    }

    private static class SimpleOnItemLongClickListener implements OnItemLongClickListener {

        final String mString;

        SimpleOnItemLongClickListener(String msg) {
            mString = msg;
        }

        @Override
        public void onItemLongClick(View view, int position) {
            Log.w(ADAPTER_TAG, mString + " position is " + position);
        }
    }

    private static class SimpleHandleClickListener implements OnHandleClickListener {

        final String mString;

        SimpleHandleClickListener(String msg) {
            mString = msg;
        }

        @Override
        public void handleClick(View v, int position) {
            Log.w(ADAPTER_TAG, mString + " position is " + position);
        }
    }


}

package xyz.demj.camrecyclerviewadapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import xyz.demj.camrecyclerviewadapter.CAMRecyclerViewAdapter.CAMViewHolder;

/**
 * Created by demj on 2016/5/3 0003.
 * Author demj
 */
public abstract class ConverterAdapter<E extends ConverterAdapter.To<T>, T extends ConverterAdapter.To<E>> {
    final CAMRecyclerViewAdapter<T, ConverterViewHolder<E, T>> mAdapter = new CAMRecyclerViewAdapter<T, ConverterViewHolder<E, T>>() {
        @Override
        protected void realBindViewHolder(ConverterViewHolder<E, T> holder, int position) {
            ConverterAdapter.this.realBindViewHolder(holder.mOriViewHolder, position);
        }

        @Override
        public int getItemViewType(int position) {
            return ConverterAdapter.this.getItemViewType(position);
        }

        @Override
        protected boolean shouldHandleClick(View view, int position) {
            return ConverterAdapter.this.shouldHandleClick(view,position);
        }

        @NonNull
        @Override
        protected ConverterViewHolder<E, T> realCreateViewHolder(ViewGroup parent, int viewType) {
            CAMViewHolder<E> oriViewHolder = ConverterAdapter.this.realCreateViewHolder(parent, viewType);

            return new ConverterViewHolder<>(oriViewHolder);
        }
    };

    protected int getItemViewType(int position) {
        return position;
    }

    protected abstract CAMViewHolder<E> realCreateViewHolder(ViewGroup parent, int viewType);

    protected abstract void realBindViewHolder(CAMViewHolder<E> holder, int position);

    public void notifyItemInserted(int po) {
        mAdapter.notifyItemInserted(po);
    }

    public void setUpWithRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(mAdapter);
    }


    static class ConverterViewHolder<E, T> extends CAMRecyclerViewAdapter.CAMViewHolder<T> implements CAMViewHolder.SetterListener {

        private CAMViewHolder<E> mOriViewHolder;

        public ConverterViewHolder(CAMViewHolder<E> oriViewHolder) {
            super(oriViewHolder.itemView);
            mOriViewHolder = oriViewHolder;
            mOriViewHolder.mSetterListener = this;
            isClickable = mOriViewHolder.isClickable;
            isClickableInCAM = mOriViewHolder.isClickableInCAM;
            isLongClickable = mOriViewHolder.isLongClickable;
            isCanTriggerCAM = mOriViewHolder.isCanTriggerCAM;


        }

        @Override
        public void onClick(View v) {
            super.onClick(v);

        }

        public ConverterViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void addHandleClickView(View view) {
            mOriViewHolder.addHandleClickView(view);
        }

        @Override
        public void setClickableInCAM(boolean clickableInCAM) {
            mOriViewHolder.setClickableInCAM(clickableInCAM);
        }

        @Override
        public void setLongClickable(boolean longClickable) {
            mOriViewHolder.setLongClickable(longClickable);
        }

        @Override
        public void setClickable(boolean clickable) {
            mOriViewHolder.setClickable(clickable);
        }

//        @Override
//        public boolean onLongClick(View v) {
//            return mOriViewHolder.onLongClick(v);
//        }
//
//        @Override
//        public void onClick(View v) {
//            mOriViewHolder.onClick(v);
//        }

        @Override
        public void setCanTriggerCAM(boolean canTriggerCAM) {
            mOriViewHolder.setCanTriggerCAM(canTriggerCAM);
        }


        @Override
        public void listenSetClickable(boolean clickable) {
            isClickable = clickable;
        }

        @Override
        public void listenSetClickableInCAM(boolean clickableInCAM) {
            isClickableInCAM = clickableInCAM;
        }

        @Override
        public void listenSetCanTriggerCAM(boolean canTriggerCAM) {
            this.isCanTriggerCAM = canTriggerCAM;
        }

        @Override
        public void listenSetLongClickable(boolean longClickable) {
            this.isLongClickable = longClickable;
        }

        @Override
        public void listenAddHandleClickView(View view) {
            addHandleClickView(view);
        }

        @Override
        public boolean listenClick(View view) {
            int position=mAdapter.mElementList.indexOf(mPositionTag);
            internalClick(view,position);
            return true;
        }

        @Override
        public boolean listenLongClick(View view) {
            this.onLongClick(view);
            return true;
        }
    }


    public static interface To<T> {
        T to();
    }

    public interface From<E> {
        E from();
    }


    /**
     * set to listen handleClick event when return true in {@link CAMRecyclerViewAdapter#shouldHandleClick}.
     *
     * @param onHandleClickListener
     */
    public void setOnHandleClickListener(@Nullable CAMRecyclerViewAdapter.OnHandleClickListener onHandleClickListener) {
        mAdapter.setOnHandleClickListener(onHandleClickListener);
    }

    /**
     * set to listen item select state changed event.
     *
     * @param onItemSelectStateChangedListener
     */
    public void setOnItemSelectStateChangedListener(@Nullable CAMRecyclerViewAdapter.OnItemSelectStateChangedListener onItemSelectStateChangedListener) {
        mAdapter.setOnItemSelectStateChangedListener(onItemSelectStateChangedListener);
    }

    /**
     * set to listen item click event.
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(@Nullable CAMRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        mAdapter.setOnItemClickListener(onItemClickListener);
    }

    /**
     * set to listen item long click event.
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(@Nullable CAMRecyclerViewAdapter.OnItemLongClickListener onItemLongClickListener) {
        mAdapter.setOnItemLongClickListener(onItemLongClickListener);
    }

    /**
     * query adapter has attach to content action mode.
     *
     * @return true yes,false no.
     */
    public boolean isInContentActionMode() {
        return mAdapter.isInContentActionMode();
    }

    /**
     * mark adapter attach to content action mode,
     * this should be used in child adapter only.
     *
     * @param inContentActionMode
     */
    protected void setInContentActionMode(boolean inContentActionMode) {
        mAdapter.setInContentActionMode(inContentActionMode);
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
        mAdapter.add(e.to(), notify);
    }

    /**
     * add an E instance into internal list.
     *
     * @param e the E's instance.
     */
    public void add(E e) {
        mAdapter.add(e.to(), mAdapter.getItemCount(), true);
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
        mAdapter.add(e.to(), position, notify);
    }

    /**
     * add an E instance into internal list.
     *
     * @param e        the E's instance.
     * @param position the insert position.
     */
    public void add(E e, int position) {
        mAdapter.add(e.to(), position);
    }

    protected CAMRecyclerViewAdapter getAdapter() {
        return mAdapter;
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
        mAdapter.addAll(toT(collection), notify);
    }

    private Collection<T> toT(Collection<E> collection) {
        Collection<T> collection1 = Collections.emptyList();
        if (collection == null)
            return collection1;
        collection1 = new ArrayList<>(collection.size());
        for (E e : collection) {
            if (e != null)
                collection1.add(e.to());
        }
        return collection1;
    }

    /**
     * add more than one element by Collection.
     *
     * @param collection the E elements container.
     */
    public void addAll(Collection<E> collection) {
        mAdapter.addAll(toT(collection), true);
    }

    /**
     * @param position the element' position you want removed.
     * @param notify   notify whether or not notify DataObserver such as RecyclerView
     *                 to handle data set changed event.Note, if you add element
     *                 in the other thread except main thread, you should always set this is false.
     */
    public void remove(int position, boolean notify) {
        mAdapter.remove(position, notify);
    }

    /**
     * @param position the element' position you want removed.
     */
    public void remove(int position) {
        mAdapter.remove(position, true);
    }


    /**
     * remove all element int internal list.
     *
     * @param notify notify whether or not notify DataObserver such as RecyclerView
     *               to handle data set changed event.Note, if you add element
     *               in the other thread except main thread, you should always set this is false.
     */
    public void removeAll(boolean notify) {
        mAdapter.removeAll(notify);
    }

    /**
     * remove all element int internal list.
     */
    public void removeAll() {
        mAdapter.removeAll(true);
    }

    /**
     * return the element count hold by internal list.
     *
     * @return count.
     */
    public int getItemCount() {
        return mAdapter.getItemCount();
    }


    /**
     * query whether the specify element is selected in CAM.
     *
     * @param position the element's position you want query.
     * @return true is selected, false is not.
     */
    public boolean isItemSelected(int position) {
        return mAdapter.isItemSelected(position);
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
        mAdapter.set(e.to(), position, notify);
    }

    /**
     * filter array by pass those null element.
     *
     * @param elements the array want to filter.
     * @return new filtered without null element array,or null if pass a null elements.
     */
    private T[] filterElements(E[] elements) {
        if (elements == null)
            return null;
        List<T> arrayList = new ArrayList<>(elements.length);
        for (E element : elements) {
            if (element != null)
                arrayList.add(element.to());
        }
        return (T[]) arrayList.toArray();
    }

    /**
     * filter collection by pass those null element.
     *
     * @param collection the collection want to filter.
     * @return new filtered without null element collection.
     */
    private List<T> filterElements(Collection<E> collection) {
        List<T> list = Collections.emptyList();
        if (collection == null)
            return list;
        list = new ArrayList<>(collection.size());
        for (E e : collection) {
            if (e != null) {
                list.add(e.to());
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
        mAdapter.addItems(filterElements(elements), startPos, notify);
    }

    /**
     * add more than one element by a array.
     *
     * @param elements the array that contain E's elements.
     * @param startPos insert start position.
     */
    public void addItems(E[] elements, int startPos) {
        mAdapter.addItems(filterElements(elements), startPos, true);
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
        mAdapter.addItems(filterElements(elements), 0, notify);
    }

    /**
     * add more than one element by a array.
     *
     * @param elements the array that contain E's elements.
     */
    public void addItems(E[] elements) {
        mAdapter.addItems(filterElements(elements), 0, true);
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
        mAdapter.removeItems(filterElements(collection), notify, notifyAfterAllRemoved);
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
        mAdapter.removeItems(filterElements(collection), notify, false);
    }

    /**
     * remove items from internal list by collection hold element.
     *
     * @param collection the elements you want remove.
     */
    public void removeItems(Collection<E> collection) {
        mAdapter.removeItems(filterElements(collection), true, false);
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
        mAdapter.removeItems(filterElements(elements), notify, notifyAfterAllRemoved);
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
        mAdapter.removeItems(filterElements(elements), notify, false);
    }

    /**
     * remove items from internal list by array hold element.
     *
     * @param elements the elements you want remove.
     */
    public void removeItems(E[] elements) {
        mAdapter.removeItems(filterElements(elements), true, false);
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
        mAdapter.removeItemsByPosition(positions, notify, notifyAfterAllRemoved);
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
        mAdapter.removeItemsByPosition(positions, notify, false);
    }

    /**
     * remove items from internal list by array hold element's position.
     *
     * @param positions the elements' position you want remove.
     */
    public void removeItemsByPosition(int[] positions) {
        mAdapter.removeItemsByPosition(positions, true, false);
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
        mAdapter.removeItemsByPosition(positions, notify, notifyAfterAllRemoved);
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
        mAdapter.removeItemsByPosition(positions, notify, false);
    }

    /**
     * remove items from internal list by collection hold element's position.
     *
     * @param positions the elements' position you want remove.
     */
    public void removeItemsByPosition(Collection<Integer> positions) {
        mAdapter.removeItemsByPosition(positions, true, false);
    }

    /**
     * set specify element is selected when in CAM.
     *
     * @param position the element's position.
     */
    public void setSelectedItem(int position) {
        mAdapter.setItemSelection(position, true);
    }

    /**
     * remove specify element is selected when in CAM.
     *
     * @param position the element's position.
     */
    public void removeSelectedItem(int position) {
        mAdapter.setItemSelection(position, false);
    }

    /**
     * set specify element is selected or not.
     *
     * @param position the element's position.
     * @param selected true is selected,false not.
     */
    public void setItemSelection(int position, boolean selected) {
        mAdapter.setItemSelection(position, selected, true);
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
        mAdapter.setItemSelection(position, selected, notify);
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
        mAdapter.setSelectedItems(positions, notify);
    }

    /**
     * set all item is selected when in CAM.
     *
     * @param notify notify whether or not notify DataObserver such as RecyclerView
     *               to handle data set changed event.Note, if you add element
     *               in the other thread except main thread, you should always set this is false.
     */
    public void setSelectAll(boolean notify) {
        mAdapter.setSelectAll(notify);
    }

    /**
     * set all item is selected when in CAM.
     */
    public void setSelectAll() {
        mAdapter.setSelectAll(true);
    }

    /**
     * set all item is not selected when in CAM or will exit CAM.
     *
     * @param notify notify whether or not notify DataObserver such as RecyclerView
     *               to handle data set changed event.Note, if you add element
     *               in the other thread except main thread, you should always set this is false.
     */
    public void clearSelection(boolean notify) {
        mAdapter.clearSelection(notify);
    }

    /**
     * query the selected item count.
     *
     * @return selected item count.
     */
    public int getSelectedItemCount() {
        return mAdapter.getSelectedItemCount();
    }

    /**
     * query selected item's positions.
     *
     * @return all selected item's position.
     */
    public ArrayList<Integer> getSelectedItemPositions() {

        return mAdapter.getSelectedItemPositions();
    }

    /**
     * query all selected item.
     *
     * @return all selected item.
     */
    public ArrayList<E> getSelectedItems() {

        ArrayList<T> selected = mAdapter.getSelectedItems();
        ArrayList<E> arrayList = new ArrayList<>(selected.size());
        for (T t : selected) {
            arrayList.add(t.to());
        }
        return arrayList;
    }


    /**
     * set specify elements are selected in CAM.
     *
     * @param positions the element's positions.
     */
    public void setSelectedItems(Collection<Integer> positions) {
        mAdapter.setSelectedItems(positions, true);
    }

    /**
     * set all item is not selected when in CAM or will exit CAM.
     */
    public void clearSelection() {
        mAdapter.clearSelection(true);
    }


    /**
     * attach to content action mode,this used when you call startActionMode or startSupportActionMode.
     * or when action mode's CallBack's onCreateActionMode or onPrepareActionMode method.
     * call this let adapter into CAM.
     *
     * @param triggerPosition the trigger start action mode item's position.
     */
    public void attachToActionMode(int triggerPosition) {
        mAdapter.attachToActionMode(triggerPosition);
    }

    /**
     * this is used to notify adapter application is exit CAM,and do something.
     */
    public void detachFromActionMode() {
        mAdapter.detachFromActionMode();
    }


    /**
     * this is used to toggle item's select state when in CAM.
     *
     * @param position the item's position.
     */
    /**/
    protected void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
    }


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
        return mAdapter.get(position).to();
    }


}



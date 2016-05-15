package xyz.demj.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by demj on 2016/5/14 0014.
 * Author: demj
 */
public abstract class BasicManager<K, V> {
    public static final int ACTION_ADD = 1;
    public static final int ACTION_DELETE = 2;
    public static final int ACTION_UPDATE = 3;
    private PublishSubject<Operation<K, V>> mPublishSubject;
    private ConnectableObservable<Operation<K, V>> mOperationConnectableObservable = null;
    private Subscription mSubscription;
    private final Object mSubjectLock = new Object();

    private final ArrayList<OnActionResultListener<K, V>> mOnActionResultListeners = new ArrayList<>();


    protected static class Operation<K, V> {
        public int action;
        public boolean isError;
        public Map<K, V> elements = Collections.emptyMap();

        public Operation() {
        }

        public Operation(int action) {
            this.action = action;
        }
    }

    private Action1<Operation<K, V>> mOperationAction = new Action1<Operation<K, V>>() {

        @Override
        public void call(final Operation<K, V> ekOperation) {
            Observable.just(1)
                    .flatMap(new Func1<Integer, Observable<OnActionResultListener<K, V>>>() {
                        @Override
                        public Observable<OnActionResultListener<K, V>> call(Integer integer) {
                            return Observable.from(mOnActionResultListeners);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<OnActionResultListener<K, V>>() {
                        @Override
                        public void call(OnActionResultListener<K, V> listener) {
                            if (ekOperation.isError) {
                                listener.onError(ekOperation.action, ekOperation.elements);
                            } else {
                                listener.onResult(ekOperation.action, ekOperation.elements);
                            }
                        }
                    });
        }
    };

    public void addItem(V v) {
        doAction(ACTION_ADD, null, v);
    }

    public void addItems(Collection<? extends V> collection) {
        doActions(ACTION_ADD, null, collection, null);
    }

    public void deleteItem(K k) {
        doAction(ACTION_DELETE, k, null);
    }

    public void deleteItems(Collection<? extends K> collection) {
        doActions(ACTION_DELETE, collection, null, null);
    }

    public void update(K key, V newOne) {
        doAction(ACTION_UPDATE, key, newOne);
    }

    public void getItem(K k, OnQueryResultListener<K, V> listener) {
        getItems(Collections.singleton(k), listener);
    }

    public void getItems(final Collection<K> keys, final OnQueryResultListener<K, V> listener) {
        if (listener == null)
            return;
        Observable.just(keys)
                .map(new Func1<Collection<K>, Map<K, V>>() {
                    @Override
                    public Map<K, V> call(Collection<K> ks) {
                        return internalGetItems(ks);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<K, V>>() {
                    @Override
                    public void call(Map<K, V> vs) {
                        listener.onSuccess(vs);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        listener.onFailed(keys, throwable);
                    }
                });
    }


    protected void doAction(final int type, final K k, final V v) {
        Observable.just(type)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Integer, Operation<K, V>>() {
                    @Override
                    public Operation<K, V> call(Integer integer) {
                        Operation<K, V> operation;
                        switch (integer) {
                            case ACTION_ADD:
                                operation = internalAddItem(v);
                                break;
                            case ACTION_DELETE:
                                operation = internalDeleteItem(k);
                                break;
                            case ACTION_UPDATE:
                                operation = internalUpdateItem(k, v);
                                break;
                            default:
                                operation = internalDoOtherAction(type, k, v);
                                break;
                        }
                        return operation;
                    }
                })
                .subscribe(new Action1<Operation<K, V>>() {
                    @Override
                    public void call(Operation<K, V> kvOperation) {
                        notifyOnAction(kvOperation);
                    }
                });
    }

    protected void doActions(final int type, final Collection<? extends K> keys, final Collection<? extends V> values, final Map<? extends K, ? extends V> map) {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Integer, Observable<Operation<K, V>>>() {
                    @Override
                    public Observable<Operation<K, V>> call(Integer integer) {
                        Collection<Operation<K, V>> collection;
                        switch (type) {
                            case ACTION_ADD:
                                collection = internalAddItems(values);
                                break;
                            case ACTION_DELETE:
                                collection = internalDeleteItems(keys);
                                break;
                            case ACTION_UPDATE:
                                collection = internalUpdateItems(map);
                                break;
                            default:
                                collection = internalDoOtherActions(type, map);
                                break;
                        }

                        return Observable.from(collection);
                    }
                })
                .subscribe(new Action1<Operation<K, V>>() {
                    @Override
                    public void call(Operation<K, V> kvOperation) {
                        notifyOnAction(kvOperation);
                    }
                });
    }


    protected abstract Operation<K, V> internalAddItem(V v);

    protected abstract Operation<K, V> internalDeleteItem(K k);

    protected abstract Operation<K, V> internalUpdateItem(K k, V v);

    protected abstract Operation<K, V> internalGetItem(K k);

    protected abstract Collection<Operation<K, V>> internalAddItems(Collection<? extends V> elements);

    protected abstract Collection<Operation<K, V>> internalDeleteItems(Collection<? extends K> keys);

    protected abstract Map<K, V> internalGetItems(Collection<? extends K> keys);

    protected abstract Collection<Operation<K, V>> internalUpdateItems(Map<? extends K, ? extends V> map);

    protected abstract Collection<Operation<K, V>> internalDoOtherActions(int type, Map<? extends K, ? extends V> map);

    protected abstract Operation<K, V> internalDoOtherAction(int type, K k, V v);


    protected void notifyOnAction(int action, Map<K, V> elements, boolean isError) {
        Operation<K, V> operation = new Operation<>();
        if (elements != null) {
            operation.elements = elements;
        }
        operation.isError = isError;
        notifyOnAction(operation);

    }

    protected void notifyOnAction(Operation<K, V> operation) {
        if (operation != null) {
            synchronized (mSubjectLock) {
                if (mPublishSubject != null) {
                    mPublishSubject.onNext(operation);
                }
            }
        }
    }

    public void registerOnActionResultListener(OnActionResultListener<K, V> listener) {
        if (listener == null) {
            throw new IllegalStateException("OnActionResultListener listener is null");
        }
        synchronized (mOnActionResultListeners) {
            //create
            if (mPublishSubject == null) {
                mPublishSubject = PublishSubject.create();
                mOperationConnectableObservable = mPublishSubject.publish();
                mOperationConnectableObservable.connect();
                mSubscription = mOperationConnectableObservable.subscribe(mOperationAction);
            }
            if (mOnActionResultListeners.contains(listener))
                throw new IllegalStateException("OnActionResultListener " + listener + " is already registered.");
            mOnActionResultListeners.add(listener);
        }
    }

    public void unregisterOnActionResultListener(OnActionResultListener<K, V> listener) {
        if (listener == null) {
            throw new IllegalStateException("OnActionResultListener listener is null");
        }
        synchronized (mOnActionResultListeners) {
            int index = mOnActionResultListeners.indexOf(listener);
            if (index == -1)
                throw new IllegalStateException("OnActionResultListener " + listener + " was not registered");
            mOnActionResultListeners.remove(index);
            //destroy when listener is empty.
            if (mOnActionResultListeners.size() <= 0) {
                synchronized (mSubjectLock) {
                    if (!mSubscription.isUnsubscribed()) {
                        mSubscription.unsubscribe();
                    }
                    mSubscription = null;
                    mOperationConnectableObservable = null;
                    if (!mPublishSubject.hasCompleted()) {
                        mPublishSubject.onCompleted();
                    }
                    mPublishSubject = null;
                }
            }
        }
    }

    public interface OnActionResultListener<K, V> {

        void onResult(int action, Map<K, V> elements);

        void onError(int action, Map<K, V> elements);
    }

    public interface OnQueryResultListener<K, V> {
        void onSuccess(Map<K, V> result);

        void onFailed(Collection<K> keys, Throwable throwable);
    }
}

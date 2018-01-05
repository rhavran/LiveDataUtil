package com.havran.livedatautil;

import android.arch.core.util.Function;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * LiveDataUtil helps to deal with live data specific cases.
 *
 * @author Roman Havran
 * @version 0.1
 */
public class LiveDataUtil {

    /**
     * Performs only one call and then unsubscribes from observer.
     *
     * @param data       Live data to perform call.
     * @param observable callback observer.
     * @param <T>        type of return value.
     */
    public static <T> void singleCallUnsafe(final LiveData<T> data, final Observer<T> observable) {
        data.observeForever(new Observer<T>() {
            @Override
            public void onChanged(@Nullable T object) {
                data.removeObserver(this);
                observable.onChanged(object);
            }
        });
    }

    /**
     * Performs only one call and then unsubscribes from observer.
     *
     * @param owner      The LifecycleOwner which controls the observer
     * @param data       Live data to perform call.
     * @param observable callback observer.
     * @param <T>        type of return value.
     */
    public static <T> void singleCall(LifecycleOwner owner, final LiveData<T> data,
                                      final Observer<T> observable) {
        data.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T object) {
                data.removeObserver(this);
                observable.onChanged(object);
            }
        });
    }

    /**
     * This live data is called only if mapping data has changed.
     *
     * @param data live data
     * @param <T>  result type.
     * @return new live data that tracks changes and fires only mapping data has changed.
     */
    @MainThread
    public static <T, X> LiveData<X> mapChangeSensitiveAfter(final MutableLiveData<T> data,
                                                             @NonNull final Function<T, X> func) {
        final MediatorLiveData<X> result = new MediatorLiveData<>();

        result.addSource(data, new Observer<T>() {

            private X mPreviousValue = null;

            @Override
            public void onChanged(@Nullable T x) {
                X candaidate = func.apply(x);
                if (mPreviousValue == null
                        || !mPreviousValue.equals(candaidate)) {
                    mPreviousValue = candaidate;
                    result.setValue(candaidate);
                }
            }
        });

        return result;
    }

    /**
     * This live data is called only if data has changed.
     *
     * @param data live data
     * @param <T>  result type.
     * @return new live data that tracks changes and fires only data has changed.
     */
    @MainThread
    public static <T, X> LiveData<X> mapChangeSensitiveBefore(final MutableLiveData<T> data,
                                                              @NonNull final Function<T, X> func) {
        final MediatorLiveData<X> result = new MediatorLiveData<>();

        result.addSource(data, new Observer<T>() {

            private T mPreviousValue = null;

            @Override
            public void onChanged(@Nullable T x) {

                if (mPreviousValue == null
                        || !mPreviousValue.equals(x)) {
                    mPreviousValue = x;
                    result.setValue(func.apply(x));
                }
            }
        });

        return result;
    }

    /**
     * This live data is called only if data has changed.
     *
     * @param data live data
     * @param <T>  result type.
     * @return new live data that tracks changes and fires only id data has changed.
     */
    @MainThread
    public static <T> LiveData<T> changeSensitive(final MutableLiveData<T> data) {
        final MediatorLiveData<T> result = new MediatorLiveData<>();

        result.addSource(data, new Observer<T>() {

            private T mPreviousValue = null;

            @Override
            public void onChanged(@Nullable T x) {
                if (mPreviousValue == null
                        || !mPreviousValue.equals(x)) {
                    mPreviousValue = x;
                    result.setValue(x);
                }
            }
        });

        return result;
    }
}

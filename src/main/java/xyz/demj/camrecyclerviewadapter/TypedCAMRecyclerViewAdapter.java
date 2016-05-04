package xyz.demj.camrecyclerviewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by demj on 2016/5/3 0003.
 * Author demj
 */
public abstract class TypedCAMRecyclerViewAdapter<E extends ConverterAdapter.To<TypedCAMRecyclerViewAdapter.Typed<E>>>
        extends ConverterAdapter<E, TypedCAMRecyclerViewAdapter.Typed<E>> {

    private int mHeaderResId = R.layout.layout_header;
    private int mFooterResId = R.layout.layout_footer;
    private int mDividerResId = R.layout.layout_divider;

    private int headerCount = 0;
    private int footerCount = 0;
    private int dividerCount = 0;
    private int tipCount = 0;
    private int itemCount = 0;

    @Override
    protected CAMRecyclerViewAdapter.CAMViewHolder<E> realCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case Typed.TYPE_DIVIDER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_color_list_divider, parent, false);

                return new DividerViewHolder<>(view);
            }

            case Typed.TYPE_FOOTER: {
                View v = inflater.inflate(R.layout.layout_footer, parent, false);
                return new HeaderFooterViewHolder<>(v);
            }

            case Typed.TYPE_HEADER: {
                View v = inflater.inflate(R.layout.layout_header, parent, false);
                return new HeaderFooterViewHolder<>(v);
            }


            case Typed.TYPE_TIPPER: {
                View v = inflater.inflate(R.layout.layout_tipper, parent, false);
                return new TipperViewHolder<>(v);
            }

            default:
                return createViewHolder(parent, viewType);
        }

    }


    @Override
    protected void realBindViewHolder(CAMRecyclerViewAdapter.CAMViewHolder<E> holder, int position) {


        Typed<E> typed = mAdapter.get(position);

        switch (getItemViewType(position)) {
            case Typed.TYPE_HEADER:

                break;
            case Typed.TYPE_FOOTER:
                break;
            case Typed.TYPE_DIVIDER:
                break;
            case Typed.TYPE_TIPPER:
                TipperViewHolder<E> viewHolder = (TipperViewHolder<E>) holder;
                viewHolder.mTextView.setText(typed.mTipString);
                break;
            default:
                bindViewHolder(holder, position);
                break;
        }
    }

    protected abstract void bindViewHolder(CAMRecyclerViewAdapter.CAMViewHolder<E> holder, int position);

    protected abstract CAMRecyclerViewAdapter.CAMViewHolder<E> createViewHolder(ViewGroup parent, int viewType);


    public void setDividerResId(int dividerResId) {
        mDividerResId = dividerResId;
    }

    public void setFooterResId(int footerResId) {
        mFooterResId = footerResId;
    }

    public void setHeaderResId(int headerResId) {
        mHeaderResId = headerResId;
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.get(position).type;
    }


    public void addFooter(E e) {
        footerCount++;
        addTypedItem(e, Typed.TYPE_FOOTER, getItemCount());
    }

    public void addDivider(E e, int position) {
        dividerCount++;
        addTypedItem(e, Typed.TYPE_DIVIDER, position);
    }

    public void addTipper(String tip, int position) {
        if (tip != null) {
            Typed<E> e = new Typed<>(null, Typed.TYPE_TIPPER);
            e.mTipString = tip;
            mAdapter.add(e,position);
            tipCount++;
        }
    }

    public void addHeader(E e) {

        addTypedItem(e, Typed.TYPE_HEADER, headerCount++);
    }

    public void addItem(E e, int position) {
        if (e != null) {
            itemCount++;
            addTypedItem(e, Typed.TYPE_ITEM, position);
        }
    }

    private void addTypedItem(E e, int type, int position) {
        e.to().type = type;
        mAdapter.add(e.to(), position);
    }

    public static class Typed<E> implements ConverterAdapter.To<E> {
        public static final int TYPE_HEADER = 1;
        public static final int TYPE_DIVIDER = 2;
        public static final int TYPE_FOOTER = 4;
        public static final int TYPE_TIPPER = 8;
        public static final int TYPE_ITEM = 16;
        private static final int DEFAULT_RES_ID = -1;
        private static final int NO_RES_ID = -2;
        public int type;
        public E e;
        private String mTipString;
        private int resId = DEFAULT_RES_ID;

        public Typed(E e, int type) {
            this.e = e;
            this.type = type;
        }

        @Override
        public E to() {
            return e;
        }
    }

    public static class TypedViewHolder<E> extends CAMRecyclerViewAdapter.CAMViewHolder<E> {
        public TypedViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class HeaderFooterViewHolder<E> extends TypedViewHolder<E> {

        public HeaderFooterViewHolder(View itemView) {
            super(itemView);
            setClickable(false);
            setLongClickable(false);
        }
    }

    private static class DividerViewHolder<E> extends TypedViewHolder<E> {
        View rootView;

        public DividerViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            setClickable(false);
            setLongClickable(false);
        }
    }

    private static class TipperViewHolder<E> extends TypedViewHolder<E> {
        TextView mTextView;

        public TipperViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            setClickable(false);
            setLongClickable(false);
        }
    }

}


package me.yokeyword.indexablerv;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by YoKey on 16/10/6.
 */
@SuppressWarnings("unchecked")
class RealAdapter<T extends IndexableEntity> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<EntityWrapper<T>> mDatasList = new ArrayList<>();
    private ArrayList<EntityWrapper<T>> mHeaderDatasList = new ArrayList<>();
    private ArrayList<EntityWrapper<T>> mFooterDatasList = new ArrayList<>();
    private IndexableAdapter<T> mAdapter;

    private SparseArray<IndexableHeaderAdapter> mHeaderAdapterMap = new SparseArray<>();
    private SparseArray<IndexableFooterAdapter> mFooterAdapterMap = new SparseArray<>();

    private IndexableAdapter.OnItemTitleClickListener mTitleClickListener;
    private IndexableAdapter.OnItemContentClickListener<T> mContentClickListener;
    private IndexableAdapter.OnItemTitleLongClickListener mTitleLongClickListener;
    private IndexableAdapter.OnItemContentLongClickListener<T> mContentLongClickListener;

    void setIndexableAdapter(IndexableAdapter<T> adapter) {
        this.mAdapter = adapter;
    }

    void addIndexableHeaderAdapter(IndexableHeaderAdapter adapter) {
        mHeaderDatasList.addAll(0, adapter.getDatas());
        mHeaderAdapterMap.put(adapter.getItemViewType(), adapter);
        notifyDataSetChanged();
    }

    void addIndexableFooterAdapter(IndexableFooterAdapter adapter) {
        mFooterDatasList.addAll(adapter.getDatas());
        mFooterAdapterMap.put(adapter.getItemViewType(), adapter);
        notifyDataSetChanged();
    }

    void addDatas(ArrayList<EntityWrapper<T>> datas) {
        mDatasList.clear();
        mDatasList.addAll(mHeaderDatasList);
        mDatasList.addAll(datas);
        mDatasList.addAll(mFooterDatasList);
        notifyDataSetChanged();
    }

    ArrayList<EntityWrapper<T>> getItems() {
        return mDatasList;
    }

    @Override
    public int getItemViewType(int position) {
        return mDatasList.get(position).getItemType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        final RecyclerView.ViewHolder holder;

        if (viewType == EntityWrapper.TYPE_TITLE) {
            holder = mAdapter.onCreateTitleViewHolder(parent);
        } else if (viewType == EntityWrapper.TYPE_CONTENT) {
            holder = mAdapter.onCreateContentViewHolder(parent);
        } else {
            AbstractHeaderFooterAdapter adapter;
            if (mHeaderAdapterMap.indexOfKey(viewType) >= 0) {
                adapter = mHeaderAdapterMap.get(viewType);
            } else {
                adapter = mFooterAdapterMap.get(viewType);
            }
            holder = adapter.onCreateContentViewHolder(parent);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                EntityWrapper<T> wrapper = mDatasList.get(position);
                if (viewType == EntityWrapper.TYPE_TITLE) {
                    if (mTitleClickListener != null) {
                        mTitleClickListener.onItemClick(v, position, wrapper.getIndexTitle());
                    }
                } else if (viewType == EntityWrapper.TYPE_CONTENT) {
                    if (mContentClickListener != null) {
                        mContentClickListener.onItemClick(v, wrapper.getOriginalPosition(), position, wrapper.getData());
                    }
                } else {
                    AbstractHeaderFooterAdapter adapter;
                    if (mHeaderAdapterMap.indexOfKey(viewType) >= 0) {
                        adapter = mHeaderAdapterMap.get(viewType);
                    } else {
                        adapter = mFooterAdapterMap.get(viewType);
                    }

                    if (adapter != null) {
                        AbstractHeaderFooterAdapter.OnItemClickListener listener = adapter.getOnItemClickListener();
                        if (listener != null) {
                            listener.onItemClick(v, position, wrapper.getData());
                        }
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                EntityWrapper<T> wrapper = mDatasList.get(position);
                if (viewType == EntityWrapper.TYPE_TITLE) {
                    if (mTitleLongClickListener != null) {
                        return mTitleLongClickListener.onItemLongClick(v, position, wrapper.getIndexTitle());
                    }
                } else if (viewType == EntityWrapper.TYPE_CONTENT) {
                    if (mContentLongClickListener != null) {
                        return mContentLongClickListener.onItemLongClick(v, wrapper.getOriginalPosition(), position, wrapper.getData());
                    }
                } else {
                    AbstractHeaderFooterAdapter adapter;
                    if (mHeaderAdapterMap.indexOfKey(viewType) >= 0) {
                        adapter = mHeaderAdapterMap.get(viewType);
                    } else {
                        adapter = mFooterAdapterMap.get(viewType);
                    }

                    if (adapter != null) {
                        AbstractHeaderFooterAdapter.OnItemLongClickListener listener = adapter.getOnItemLongClickListener();
                        if (listener != null) {
                            return listener.onItemLongClick(v, position, wrapper.getData());
                        }
                    }
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EntityWrapper<T> item = mDatasList.get(position);

        int viewType = getItemViewType(position);
        if (viewType == EntityWrapper.TYPE_TITLE) {
            mAdapter.onBindTitleViewHolder(holder, item.getIndexTitle());
        } else if (viewType == EntityWrapper.TYPE_CONTENT) {
            mAdapter.onBindContentViewHolder(holder, item.getData());
        } else {
            AbstractHeaderFooterAdapter adapter;
            if (mHeaderAdapterMap.indexOfKey(viewType) >= 0) {
                adapter = mHeaderAdapterMap.get(viewType);
            } else {
                adapter = mFooterAdapterMap.get(viewType);
            }
            adapter.onBindContentViewHolder(holder, item.getData());
        }
    }

    @Override
    public int getItemCount() {
        return mDatasList.size();
    }

    void setOnItemTitleClickListener(IndexableAdapter.OnItemTitleClickListener listener) {
        this.mTitleClickListener = listener;
    }

    void setOnItemContentClickListener(IndexableAdapter.OnItemContentClickListener<T> listener) {
        this.mContentClickListener = listener;
    }

    void setOnItemTitleLongClickListener(IndexableAdapter.OnItemTitleLongClickListener listener) {
        this.mTitleLongClickListener = listener;
    }

    void setOnItemContentLongClickListener(IndexableAdapter.OnItemContentLongClickListener<T> listener) {
        this.mContentLongClickListener = listener;
    }
}

package com.example.monitoring;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LogListAdapter extends BaseAdapter {
    private static final int ITEM_VIEW_TYPE_STRS = 0 ;
    private static final int ITEM_VIEW_TYPE_IMGS = 1 ;
    private static final int ITEM_VIEW_TYPE_MAX = 2 ;

    // 아이템 데이터 리스트.
    private ArrayList<LogListViewItem> listViewItemList = new ArrayList<LogListViewItem>() ;

    public LogListAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX ;
    }

    // position 위치의 아이템 타입 리턴.
    @Override
    public int getItemViewType(int position) {
        return listViewItemList.get(position).getType() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogViewHolder holder;

        final Context context = parent.getContext();
        int viewType = getItemViewType(position) ;
        LogListViewItem listViewItem = listViewItemList.get(position);


        switch (viewType){
            case ITEM_VIEW_TYPE_STRS:
                if (convertView == null){
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

                    // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
                    convertView = inflater.inflate(R.layout.listview_log_content_item,
                            parent, false);
                    TextView contentTextView = (TextView) convertView.findViewById(R.id.textViewContent) ;
                    TextView timeTextView = convertView.findViewById(R.id.textViewTime) ;

                    holder = new LogViewHolder();
                    holder.m_contentTextView = contentTextView;
                    holder.m_timeTextView = timeTextView;
                    convertView.setTag(holder);
                }
                else  holder = (LogViewHolder) convertView.getTag();
                holder.m_contentTextView.setText(listViewItem.getContentStr());
                holder.m_timeTextView.setText(listViewItem.getTimeStr());
                break;
            case ITEM_VIEW_TYPE_IMGS:
                if (convertView == null){
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

                    convertView = inflater.inflate(R.layout.listview_log_date_item,
                            parent, false);

                    ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView) ;
                    TextView dateTextView = (TextView) convertView.findViewById(R.id.textViewDate) ;

                    //iconImageView.setImageDrawable(listViewItem.getIcon());
                    //dateTextView.setText(listViewItem.getDateStr());

                    holder = new LogViewHolder();
                    holder.m_iconImageView = iconImageView;
                    holder.m_dateTextView = dateTextView;
                    convertView.setTag(holder);
                }
                else  holder = (LogViewHolder) convertView.getTag();
                holder.m_iconImageView.setImageDrawable(listViewItem.getIcon());
                holder.m_dateTextView.setText(listViewItem.getDateStr());


        }
        /*
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            LogListViewItem listViewItem = listViewItemList.get(position);

            switch (viewType) {
                case ITEM_VIEW_TYPE_STRS:
                    convertView = inflater.inflate(R.layout.listview_log_content_item,
                            parent, false);
                    TextView contentTextView = (TextView) convertView.findViewById(R.id.textViewContent) ;
                    TextView timeTextView = convertView.findViewById(R.id.textViewTime) ;

                    //contentTextView.setText(listViewItem.getContentStr());
                    //timeTextView.setText(listViewItem.getTimeStr());

                    holder = new LogViewHolder();
                    holder.m_contentTextView = contentTextView;
                    holder.m_timeTextView = timeTextView;
                    convertView.setTag(holder);

                    holder.m_contentTextView.setText(listViewItem.getContentStr());
                    holder.m_timeTextView.setText(listViewItem.getTimeStr());

                    break;
                case ITEM_VIEW_TYPE_IMGS:
                    convertView = inflater.inflate(R.layout.listview_log_date_item,
                            parent, false);

                    ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView) ;
                    TextView dateTextView = (TextView) convertView.findViewById(R.id.textViewDate) ;

                    //iconImageView.setImageDrawable(listViewItem.getIcon());
                    //dateTextView.setText(listViewItem.getDateStr());

                    holder = new LogViewHolder();
                    holder.m_iconImageView = iconImageView;
                    holder.m_dateTextView = dateTextView;
                    convertView.setTag(holder);

                    holder.m_iconImageView.setImageDrawable(listViewItem.getIcon());
                    holder.m_dateTextView.setText(listViewItem.getDateStr());
                    break;
            }
        }
        else{
            holder = (LogViewHolder) convertView.getTag();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            LogListViewItem listViewItem = listViewItemList.get(position);
            switch (viewType) {
                case ITEM_VIEW_TYPE_STRS:
                    //convertView = inflater.inflate(R.layout.listview_log_content_item,
                    //        parent, false);
                    holder.m_contentTextView.setText(listViewItem.getContentStr());
                    holder.m_timeTextView.setText(listViewItem.getTimeStr());
                    break;
                case ITEM_VIEW_TYPE_IMGS:
                    //convertView = inflater.inflate(R.layout.listview_log_date_item,
                    //        parent, false);
                    holder.m_iconImageView.setImageDrawable(listViewItem.getIcon());
                    holder.m_dateTextView.setText(listViewItem.getDateStr());
                    break;
            }
        }

         */





        return convertView;

    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 첫 번째 아이템 추가를 위한 함수.
    public void addItem(String title, String desc) {
        LogListViewItem item = new LogListViewItem() ;

        item.setType(ITEM_VIEW_TYPE_STRS) ;
        item.setContentStr(title); ;
        item.setTimeStr(desc); ;

        listViewItemList.add(item) ;
    }

    // 두 번째 아이템 추가를 위한 함수.
    public void addItem(Drawable icon, String text) {
        LogListViewItem item = new LogListViewItem() ;

        item.setType(ITEM_VIEW_TYPE_IMGS) ;
        item.setIcon(icon);
        item.setDateStr(text);

        listViewItemList.add(item);
    }

    public void deleteItem(){
        //LogListViewItem item = new LogListViewItem() ;

        listViewItemList.clear();
    }

    public class LogViewHolder {
        public TextView m_contentTextView;
        public TextView m_timeTextView;
        public ImageView m_iconImageView;
        public TextView m_dateTextView;

    }
}
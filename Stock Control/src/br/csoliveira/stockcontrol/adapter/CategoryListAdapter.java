package br.csoliveira.stockcontrol.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.csoliveira.stockcontrol.R;
import br.csoliveira.stockcontrol.model.Category;

public class CategoryListAdapter extends BaseAdapter {

    private ArrayList<Category> mCategory = new ArrayList<Category>();
    private Context mContext;

    public CategoryListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mCategory != null) {
            return mCategory.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mCategory != null) {
            return mCategory.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mCategory != null) {
            return mCategory.get(position).hashCode();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(mContext, R.layout.category_item, null);
        TextView categoryName = (TextView) convertView.findViewById(R.id.category_name);
        if (categoryName != null) {
            categoryName.setText(mCategory.get(position).getCategory());
        }
        return convertView;
    }

    public void updateCategory(ArrayList<Category> categoryArray) {
        if (mCategory != null) {
            mCategory.clear();
        }
        mCategory = categoryArray;
        notifyDataSetChanged();
    }

}

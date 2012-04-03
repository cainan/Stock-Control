package br.csoliveira.stockcontrol.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.csoliveira.stockcontrol.R;
import br.csoliveira.stockcontrol.model.Product;
import br.csoliveira.stockcontrol.util.Utils;

public class ProductListAdapter extends BaseAdapter {

    private ArrayList<Product> mProduct = new ArrayList<Product>();
    private Context mContext;

    public ProductListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mProduct != null) {
            return mProduct.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mProduct != null) {
            return mProduct.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mProduct != null) {
            return mProduct.get(position).hashCode();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(mContext, R.layout.product_item, null);
        TextView productName = (TextView) convertView.findViewById(R.id.product_name);
        if (productName != null) {
            productName.setText(mProduct.get(position).getProductName());
        }
        return convertView;
    }

    public void updateProduct(ArrayList<Product> productArray) {
        if (mProduct != null) {
            mProduct.clear();
        }
        Utils.doLog("qtd: " + mProduct.size());

        mProduct = productArray;
        notifyDataSetChanged();
    }

}

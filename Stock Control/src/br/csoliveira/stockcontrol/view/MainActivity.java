package br.csoliveira.stockcontrol.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import br.csoliveira.stockcontrol.R;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */

    private Button mCategoryBtn;
    private Button mProductBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initview();
    }

    /**
     * Initialize the view
     */
    private void initview() {
        mCategoryBtn = (Button) findViewById(R.id.category_btn);
        if (mCategoryBtn != null) {
            mCategoryBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                }

            });
        }

        mProductBtn = (Button) findViewById(R.id.product_btn);
        if (mProductBtn != null) {
            mProductBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ProductActivity.class));

                }
            });
        }
    }
}
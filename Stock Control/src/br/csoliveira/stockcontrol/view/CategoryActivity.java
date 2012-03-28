package br.csoliveira.stockcontrol.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import br.csoliveira.stockcontrol.R;
import br.csoliveira.stockcontrol.model.Category;
import br.csoliveira.stockcontrol.model.database.DatabaseDelegate;
import br.csoliveira.stockcontrol.util.Utils;

public class CategoryActivity extends Activity {

    private static final int DIALOG_ADD_CATEGORY = 10;

    private Button mAddBtn;

    private DatabaseDelegate mDatabaseDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);

        mDatabaseDelegate = DatabaseDelegate.getInstance(getApplicationContext());

        initView();
    }

    private void initView() {
        mAddBtn = (Button) findViewById(R.id.add_category_btn);
        if (mAddBtn != null) {
            mAddBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_ADD_CATEGORY);
                }
            });
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG_ADD_CATEGORY) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(getString(R.string.category_name_text));

            LayoutInflater factory = LayoutInflater.from(this);
            final View textField = factory.inflate(R.layout.add_category_layout, null);
            final EditText editText = (EditText) textField.findViewById(R.id.category_name_field);
            builder.setView(textField);

            builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dismissDialog(DIALOG_ADD_CATEGORY);
                    if (editText != null) {
                        Utils.doLog(editText.getText().toString());
                        saveCategory(editText.getText().toString());
                        editText.setText("");
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dismissDialog(DIALOG_ADD_CATEGORY);
                    if (editText != null) {
                        editText.setText("");
                    }
                }
            });

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        return null;
    }

    protected void saveCategory(String name) {
        Category category = new Category();
        category.setCategory(name);
        mDatabaseDelegate.insertCategory(this, category);
    }

}

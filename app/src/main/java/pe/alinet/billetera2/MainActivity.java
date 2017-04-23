package pe.alinet.billetera2;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener{

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listview);
        getLoaderManager().initLoader(0,null,this);

        Button btnAddGasto =  (Button) findViewById(R.id.btnAddGasto);
        btnAddGasto.setOnClickListener(this);

        loadLabels();
    }

    @Override
    public void onClick(View view){
        ContentValues newValues = new ContentValues();
        long fecha = java.lang.System.currentTimeMillis();
        EditText txtCantidad = (EditText) findViewById(R.id.txtCantidad);
        EditText txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);

        if (view.getId()== R.id.btnAddGasto) {

            newValues.put(BilleteraContentProvider.KEY_QUANTITY, txtCantidad.getText().toString());
            newValues.put(BilleteraContentProvider.KEY_DESCRIPTION, txtDescripcion.getText().toString());
            newValues.put(BilleteraContentProvider.KEY_CREATION_DATE, fecha);

            ContentResolver cr = getContentResolver();
            Uri myRowUri = cr.insert(BilleteraContentProvider.CONTENT_URI, newValues);
            txtCantidad.setText("");
            txtDescripcion.setText("");

            loadLabels();
        }
        getLoaderManager().restartLoader(0, null, this);
    }

    public void loadLabels(){
        ContentResolver cr = getContentResolver();
        String[] proyection = {"sum("+BilleteraContentProvider.KEY_QUANTITY +")"};
        Cursor q = cr.query(BilleteraContentProvider.CONTENT_URI, proyection,
                "1", null, null);

        TextView lbGasto = (TextView) findViewById(R.id.lbGastoTotal);

        q.moveToFirst();
        lbGasto.setText(String.format("%.2f", q.getFloat(0)));
        q.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLabels();
        getLoaderManager().restartLoader(0, null, this);
    }


    // SimpleCursorAdapter para llenar el listview

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = new CursorLoader(this,
                BilleteraContentProvider.CONTENT_URI, null, null, null, null);
        return loader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        String[] fromColumns = new String[]{
                BilleteraContentProvider.KEY_QUANTITY,
                BilleteraContentProvider.KEY_DESCRIPTION,
                BilleteraContentProvider.KEY_CREATION_DATE};

        int[] toLayoutIDs = new int[] {
                R.id.row,
                R.id.rowDesc,
                R.id.rowDate};

        SimpleCursorAdapter myadapter = new SimpleCursorAdapter(this,R.layout.billetera_item,
                cursor,
                fromColumns,
                toLayoutIDs,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        BilleteraCursorBinder binder = new BilleteraCursorBinder();
        myadapter.setViewBinder(binder);
        listview.setAdapter(myadapter);
    }


    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().initLoader(0,null,this);
    }
}

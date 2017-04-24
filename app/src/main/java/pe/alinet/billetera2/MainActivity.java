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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        InputFragment.OnNewInputAddedListener{

    ListView listview;
    long selected = -1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listview);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setSelector(android.R.color.holo_blue_light);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleCursorAdapter adapter = (SimpleCursorAdapter) listview.getAdapter();
                selected = adapter.getItemId(position);
            }
        });

        getLoaderManager().initLoader(0, null, this);

//        Button btnAddGasto =  (Button) findViewById(R.id.btnAddGasto);
//        btnAddGasto.setOnClickListener(this);

        loadLabels();
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
        if (id == R.id.action_delete) {

            if (selected > -1) {
                Toast.makeText(this, "selected:" + String.valueOf(selected), Toast.LENGTH_LONG).show();
                ContentResolver cr = getContentResolver();
                cr.delete(Uri.parse("content://pe.alinet.billetera/items/"+String.valueOf(selected)),null,null);
                loadLabels();
                getLoaderManager().restartLoader(0, null, this);
            }
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
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onNewInputAdded(String description, String amount) {
        ContentValues newValues = new ContentValues();
        long fecha = java.lang.System.currentTimeMillis();
        newValues.put(BilleteraContentProvider.KEY_QUANTITY,amount);
        newValues.put(BilleteraContentProvider.KEY_DESCRIPTION, description);
        newValues.put(BilleteraContentProvider.KEY_CREATION_DATE, fecha);

        ContentResolver cr = getContentResolver();
        Uri myRowUri = cr.insert(BilleteraContentProvider.CONTENT_URI, newValues);

        loadLabels();

        getLoaderManager().restartLoader(0, null, this);

    }

}

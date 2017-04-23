package pe.alinet.billetera2;

import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aliosh on 17/04/2017.
 */
public class BilleteraCursorBinder implements SimpleCursorAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

        if ( columnIndex == cursor.getColumnIndex(BilleteraContentProvider.KEY_QUANTITY)) {
            float quantity = cursor.getFloat(columnIndex);
            TextView text = (TextView) view;
            text.setText(String.format("%.2f",quantity));
            return  true;
        }

        if ( columnIndex == cursor.getColumnIndex(BilleteraContentProvider.KEY_CREATION_DATE)){
            long fecha = cursor.getLong(columnIndex);
            TextView text = (TextView) view;
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm a");
            String dateString = sdf.format(new Date(fecha));
            text.setText(dateString);
            return true;
        }

        return false;
    }
}

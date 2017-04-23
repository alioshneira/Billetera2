package pe.alinet.billetera2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Aliosh on 23/04/2017.
 */
public class InputFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view =  inflater.inflate(R.layout.input_fragment,container,false);
        final EditText tDescription = (EditText) view.findViewById(R.id.tDescription);
        final EditText tAmount = (EditText) view.findViewById(R.id.tAmount);
        final Button btnAdd = (Button) view.findViewById(R.id.btnAdd);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = tDescription.getText().toString();
                String amount = tAmount.getText().toString();

                onNewInputAddedListener.onNewInputAdded(description,amount);
            }
        });
        return view ;
    }

    public interface OnNewInputAddedListener{
        void onNewInputAdded(String description, String amount);
    }

    private OnNewInputAddedListener onNewInputAddedListener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            onNewInputAddedListener = (OnNewInputAddedListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+
                    "must implement OnNewInputAddedListener");
        }
    }

}


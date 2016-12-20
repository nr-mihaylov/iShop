package edu.student.nrm.ishop;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.student.nrm.ishop.model.ShoppingItem;

public class CreateItemFragment extends Fragment {
    private static final String KEY = "id-key";
    private String id;

    private FragmentManager fManager;

    public CreateItemFragment() {}

    public static CreateItemFragment newInstance(String key) {
        CreateItemFragment fragment = new CreateItemFragment();
        Bundle args = new Bundle();
        args.putString(KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(KEY);
        }
        fManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_item, container, false);

        final EditText labelInput = (EditText) view.findViewById(R.id.new_item_label_input);
        final EditText qtyInput = (EditText) view.findViewById(R.id.new_item_quantity_input);
        final Button createItem = (Button) view.findViewById(R.id.create_item_action);

        final Fragment fragRef = this;
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ishop-330be.firebaseio.com/lists/" + id + "/items");

        createItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String label = labelInput.getText().toString();
                String qty = qtyInput.getText().toString();

                if(label.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Item label cannot be empty", Toast.LENGTH_SHORT).show();
                } else if(qty.isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(), "Item quantity cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    ShoppingItem si = new ShoppingItem(label, Integer.valueOf(qty));
                    dbRef.push().setValue(si);

                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    fManager.popBackStack();

                }

            }
        });

        return view;
    }
}

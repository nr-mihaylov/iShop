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

import edu.student.nrm.ishop.model.ShoppingList;

public class CreateListFragment extends Fragment {
    private FragmentManager fManager;

    public CreateListFragment() {}

    public static CreateListFragment newInstance() {
        return new CreateListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_list, container, false);

        final EditText labelInput = (EditText) view.findViewById(R.id.create_list_name_input);
        final Button bCreate = (Button) view.findViewById(R.id.create_list_action);

        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String label = labelInput.getText().toString();

                if(!label.isEmpty()) {

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ishop-330be.firebaseio.com/lists");
                    ShoppingList sl = new ShoppingList(label);
                    dbRef.push().setValue(sl);

                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    fManager.popBackStack();

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "List name cannot be empty", Toast.LENGTH_SHORT).show();
                }



            }
        });

        return view;
    }
}

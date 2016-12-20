package edu.student.nrm.ishop;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.student.nrm.ishop.model.ShoppingList;

public class ListFragment extends Fragment {
    private int mColumnCount = 2;
    private FragmentManager fManager;

    public ListFragment() {}

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, CreateListFragment.newInstance())
                        .addToBackStack(null)
                        .commit();

            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), mColumnCount));

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ishop-330be.firebaseio.com/lists");
        final FirebaseRecyclerAdapter<ShoppingList, ListViewHolder> fbAdapter = new FirebaseRecyclerAdapter<ShoppingList, ListViewHolder>(
                ShoppingList.class,
                R.layout.fragment_item,
                ListViewHolder.class,
                dbRef
        ) {
            @Override
            protected void populateViewHolder(ListViewHolder viewHolder, ShoppingList model, final int position) {
                final String key = this.getRef(position).getKey();

                viewHolder.title.setText(model.getLabel());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        fManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, ListItemFragment.newInstance(key))
                                .addToBackStack(null)
                                .commit();

                    }
                });
            }
        };

        recyclerView.setAdapter(fbAdapter);

        return view;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ListViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.item_title);
        }
    }
}

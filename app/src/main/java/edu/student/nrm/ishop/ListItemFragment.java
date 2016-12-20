package edu.student.nrm.ishop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.student.nrm.ishop.model.ShoppingItem;

import static android.R.attr.data;

public class ListItemFragment extends Fragment {

    private static final String KEY = "id-key";
    private String id;
    private int mColumnCount = 1;

    DatabaseReference dbRef;
    private FragmentManager fManager;

    public ListItemFragment() {}
    public static ListItemFragment newInstance(String key) {
        ListItemFragment fragment = new ListItemFragment();
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
        setHasOptionsMenu(true);
        fManager = getActivity().getSupportFragmentManager();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ishop-330be.firebaseio.com/lists/" + id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listitem_list, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.item_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, CreateItemFragment.newInstance(id))
                        .addToBackStack(null)
                        .commit();

            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.item_list);
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), mColumnCount));

        final FirebaseRecyclerAdapter<ShoppingItem, ItemViewHolder> fbAdapter = new FirebaseRecyclerAdapter<ShoppingItem, ItemViewHolder> (
                ShoppingItem.class,
                R.layout.fragment_listitem,
                ItemViewHolder.class,
                dbRef.child("items")
        ) {
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, ShoppingItem model, final int position) {

                final String label = model.getLabel();
                final int qty = model.getQuantity();

                viewHolder.label.setText(label);
                viewHolder.quantity.setText(String.valueOf(qty));

                final DatabaseReference fbRef = this.getRef(position);
                viewHolder.itemView.findViewById(R.id.item_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fbRef.removeValue();

                        final SharedPreferences undo = getActivity().getSharedPreferences("UNDO", 0);
                        SharedPreferences.Editor editor = undo.edit();
                        editor.putString("undo_label", label);
                        editor.putInt("undo_qty", qty);
                        editor.commit();

                        Snackbar.make(v, "Item Deleted", Snackbar.LENGTH_LONG).
                                setAction("Undo", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        dbRef.child("items").push().setValue(new ShoppingItem(undo.getString("undo_label", "error"),undo.getInt("undo_qty", 0) ));
                                    }

                                }).show();

                    }
                });

            }
        };

        recyclerView.setAdapter(fbAdapter);

        return view;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        TextView quantity;

        public ItemViewHolder(View v) {
            super(v);
            label = (TextView) v.findViewById(R.id.item_label);
            quantity = (TextView) v.findViewById(R.id.item_quantity);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;

        int id = item.getItemId();

        switch(id) {

            case R.id.action_clear_all:

                builder.setMessage("Are you sure you wish to remove all items from the list?").setTitle("Empty List");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbRef.child("items").removeValue();
                    }
                });
                builder.setNegativeButton("No", null);
                dialog = builder.create();
                dialog.show();

                break;

            case R.id.action_delete_list:

                builder.setMessage("Are you sure you wish to delete the list?").setTitle("Delete List");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbRef.removeValue();
                        fManager.popBackStack();
                    }
                });
                builder.setNegativeButton("No", null);
                dialog = builder.create();
                dialog.show();

                break;

            case R.id.action_share:

                dbRef.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String text = "";

                        for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {

                            ShoppingItem si = itemSnapshot.getValue(ShoppingItem.class);

                            if(!text.isEmpty()) {
                                text += ", ";
                            }

                            text += si.getLabel() + " (" + si.getQuantity() + ")";

                        }

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                break;

        }

        return super.onOptionsItemSelected(item);
    }
}

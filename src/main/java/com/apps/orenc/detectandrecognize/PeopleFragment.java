package com.apps.orenc.detectandrecognize;


import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;


/**
 * A simple {@link ListFragment} subclass.
 */
public class PeopleFragment extends ListFragment
        implements PersonItemLongClickAlertDialog.OnPersonItemAlertDialogClickListener, EditPersonAlertDialog.OnEditPersonAlertDialogClickListener {

    private static final String TAG = "PeopleFragment";

    public static final String TITLE = "People";

    // Dialog that pops when and item is long clicked.
    private PersonItemLongClickAlertDialog mPersonLognClickDialog;
    private ShowPersonAlertDialog mShowPersonAlertDialog;
    private EditPersonAlertDialog mEditPersonAlertDialog;

    private int mCurrentClickedItemPosition;

    // Member that holds the list view items.
    private List<Person> mPersons;
    private PeopleListViewAdapter mAdapter;

    private ListView mPeopleListView;

    public PeopleFragment() {

    }

    public void setArguments(List<Person> persons, PeopleListViewAdapter adapter) {
        // Required empty public constructor
        mPersons = persons;
        mAdapter = adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        // Initialize the dialogs and register their listeners.
        mPersonLognClickDialog = new PersonItemLongClickAlertDialog();
        mPersonLognClickDialog.setOnPersonItemAlertDialogListener(this);

        mShowPersonAlertDialog = new ShowPersonAlertDialog();

        mEditPersonAlertDialog = new EditPersonAlertDialog();
        mEditPersonAlertDialog.setOnEditPersonAlertDialogClickListener(this);

        SingletonPeopleSQLiteHelper helper = SingletonPeopleSQLiteHelper.getInstance(getActivity().getApplicationContext());

        setListAdapter(mAdapter);

        mPersons.clear();
        List<Person> persons = helper.getAllPersons();
        if(persons != null && persons.size() > 0) {
            mPersons.addAll(helper.getAllPersons());
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container, false);

        // Set people list view with long click listeners.
        mPeopleListView = (ListView) view.findViewById(R.id.people_list_view);
        if(mPeopleListView != null) {
            mPeopleListView.setOnItemLongClickListener(new PersonItemLongClickListener());
        }

        // Inflate the layout for this fragment
        return view;
    }


    private class PersonItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private static final String TAG = "PersonItemLongClickListener";

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            // Save the selected list item index and show the dialog.
            mCurrentClickedItemPosition = position;
            mPersonLognClickDialog.show(PeopleFragment.this.getChildFragmentManager(), TAG);

            return true;
        }
    }


    @Override
    public void onShowOperationClick() {
        // Set person details and show dialog.
        mShowPersonAlertDialog.setPerson(mPersons.get(mCurrentClickedItemPosition));
        mShowPersonAlertDialog.show(PeopleFragment.this.getChildFragmentManager(), TAG);
    }

    @Override
    public void onEditOperationClick() {
        // Set current person details and show the editor dialog.
        mEditPersonAlertDialog.setPerson(mPersons.get(mCurrentClickedItemPosition));
        mEditPersonAlertDialog.show(PeopleFragment.this.getChildFragmentManager(), TAG);
    }

    @Override
    public void onRemoveOperationClick() {
        // Take the person for removal.
        Person personToRemove = mPersons.get(mCurrentClickedItemPosition);
        // Remove the person from the list.
        mPersons.remove(mCurrentClickedItemPosition);
        // Remove the person from the database.
        SingletonPeopleSQLiteHelper helper = SingletonPeopleSQLiteHelper.getInstance(getActivity().getApplicationContext());
        helper.deletePerson(personToRemove);
        // Refresh the list view.
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEditPersonAlertDialogPositiveClick() {
        // Get the person for update.
        Person personToUpdate = mPersons.get(mCurrentClickedItemPosition);
        // Update the database.
        SingletonPeopleSQLiteHelper helper = SingletonPeopleSQLiteHelper.getInstance(getActivity().getApplicationContext());
        helper.updatePerson(personToUpdate);
        // Refresh the list view.
        mAdapter.notifyDataSetChanged();
    }

}

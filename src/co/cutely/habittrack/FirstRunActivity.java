package co.cutely.habittrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

public class FirstRunActivity  extends Activity implements AdapterView.OnItemClickListener,
		View.OnClickListener {

	/** The list of ideas associated with the ListView */
	private List<String> mIdeasArray;
	private boolean[] mChecked;
	
	@Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_run);
        
        // Get the list of habit ideas
        final String[] ideasArray = this.getResources().getStringArray(R.array.habitIdeasList);
        mIdeasArray = Arrays.asList(ideasArray);
        mChecked = new boolean[mIdeasArray.size()];
        
        // Get the ListView
        final ListView ideasList = (ListView)this.findViewById(R.id.firstRunIdeasList);
        ideasList.setAdapter(new IdeaAdapter(this, mIdeasArray));
        ideasList.setOnItemClickListener(this);
        
        // And the button
        final Button continueButton = (Button)this.findViewById(R.id.firstRunContinue);
        continueButton.setOnClickListener(this);
	}

	/**
     * Inner class to implement the required bits of a custom ArrayAdapter
     * to support the "idea_list_item" ListView item
     */
    private class IdeaAdapter extends ArrayAdapter<String> {
    	private LayoutInflater mInflater;
    	
    	public IdeaAdapter(final Context context, final List<String> habits) {
    		super(context, 0, habits);
    		
    		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	

    	@Override
        public View getView(int position, final View providedView, final ViewGroup parent) {
    		final View view;
    		
    		// See if the view was passed in or if it needs to be inflated
    		if (providedView == null) {
                view = mInflater.inflate(R.layout.idea_list_item, null);
            } else {
                view = providedView;
            }

    		// Populate the view
    		final CheckBox ideaCheckbox = (CheckBox)view.findViewById(R.id.ideaCheckBox);
    		ideaCheckbox.setText(mIdeasArray.get(position));
    		ideaCheckbox.setChecked(mChecked[position]);
    		
    		return view;
    	}
    }

    /**
     * Click handler for a habit idea in the list
     * 
     * @param parent 	The AdapterView where the click happened.
     * @param view 	The view within the AdapterView that was clicked (this will be a view provided by the adapter)
     * @param position 	The position of the view in the adapter.
     * @param id 	The row id of the item that was clicked. 
     */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// First, update the state
		mChecked[position] = !mChecked[position];
		
		// Update the checkbox
		final CheckBox ideaCheckbox = (CheckBox)view.findViewById(R.id.ideaCheckBox);
		ideaCheckbox.setChecked(mChecked[position]);
	}

	/**
	 * Click handler for the continue button
	 * 
	 * @param view The item clicked
	 */
	@Override
	public void onClick(View view) {
        // Make a new list of habits to track
        final ArrayList<TrackedHabit> startingList = new ArrayList<TrackedHabit>();

        // Iterate through the checked items and add them to the list 
        final int itemCount = mChecked.length;
		for (int index = 0; index < itemCount; ++index) {
			if (mChecked[index]) {
				// Add the habit
				final TrackedHabit habit = new TrackedHabit();
				habit.setName(mIdeasArray.get(index));
				habit.setCount(0);
				startingList.add(habit);
			}
			
		}
		
		// Send the list back
        final GlobalState state = (GlobalState)this.getApplication();
        state.setInitialList(startingList);
        
        // Reset the flag for first run so the list will be picked up
        state.setFirstRun(true);

        // Finish the activity!
        finish();
	}
}

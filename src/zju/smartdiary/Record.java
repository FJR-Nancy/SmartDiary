package zju.smartdiary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public  class Record extends Fragment {

	
	static Record newInstance() {
		Record f = new Record();
	    return f;
	}
	   
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Context context = this.getActivity();
		TextView tv = new TextView(context);
		
		tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		tv.setText("These are the records");

		return tv;
	}

}
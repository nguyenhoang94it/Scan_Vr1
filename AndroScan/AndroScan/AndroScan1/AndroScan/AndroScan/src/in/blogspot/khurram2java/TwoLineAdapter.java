package in.blogspot.khurram2java;

import in.blogspot.khurram2java.R.layout;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TwoLineAdapter extends ArrayAdapter<ScannedItems>
{
	Context context = null;
	ArrayList<ScannedItems> arr = null;
	int layoutID;
	 
	public TwoLineAdapter(Context context, int layoutId,
			ArrayList<ScannedItems> arr) {
		
		
		
		super(context, layoutId, arr);
		// TODO Auto-generated constructor stub
		 this.context = context;
		 this.layoutID = layoutId;
		 this.arr = new ArrayList<ScannedItems>(arr);
	}
	
	@Override
	public ScannedItems getItem(int position) {
		// TODO Auto-generated method stub
		return arr.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	public View getView(int position, View arg1, ViewGroup arg2)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		
		if(arg1 == null)
		{
//			if(position == 0){
//				arg1 = inflater.inflate(R.layout.first_item_layoutt, arg2, false);
//			} else {
					arg1 = inflater.inflate(R.layout.item_listview, arg2, false);
//					}
		
			holder = new ViewHolder();
			//holder.itemId = (TextView) arg1.findViewById(R.id.tvItemId);
			holder.itemCode = (TextView) arg1.findViewById(R.id.tvItemCode);
			holder.itemNote = (TextView) arg1.findViewById(R.id.tvItemNote);
			holder.lnLayout = (LinearLayout) arg1.findViewById(R.id.lnLayout);
			
			
			arg1.setTag(holder);
		} else
		{
			holder = (ViewHolder) arg1.getTag();
		}
		ScannedItems item = (ScannedItems) arr.get(position);
		holder.itemCode.setText(item.getCode());
//		holder.itemNote.setText(item.getTime() + " *** " + item.getNote());
		String textNote = "<small>" + item.getTime()  + "</small>"
			    + "<font color=#AEB404>" + " *** " + "</font>"
				+ item.getNote();
//				+ "<font color=#F7FE2E>" + item.getNote() + "</font>";
		holder.itemNote.setText(Html.fromHtml(textNote));
		
		//The first row is different
		if(position == 0){
			holder.lnLayout.setBackgroundResource(R.drawable.border_text_view);
			holder.itemCode.setTextColor(Color.WHITE);
//			holder.itemNote.setTextColor(Color.YELLOW);
			String textFirstNote = "<small>" + "<font color=#F7FE2E>" + item.getTime() + "</font>" + "</small>"
					    		 + "<font color=#FFFFFF>" + " *** " + "</font>"
					    		 + "<font color=#F7FE2E>" + item.getNote() + "</font>";
			holder.itemNote.setText(Html.fromHtml(textFirstNote));
		}			
		return arg1;
	}
	public static class ViewHolder
	{
		TextView itemCode, itemNote;
		LinearLayout lnLayout;
	}

	
	
}

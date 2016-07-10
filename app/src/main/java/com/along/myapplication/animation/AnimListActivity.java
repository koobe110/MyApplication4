package com.along.myapplication.animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.along.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class AnimListActivity extends Activity{
	
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.anim_list_layout);
		listView=(ListView) findViewById(R.id.listView);
		List<String>list=new ArrayList<String>();
		for(int i=0;i<20;i++)
		{
			list.add("Item"+(i+1));
		}
		ArrayAdapter<String>adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
	    listView.setAdapter(adapter);
	    LayoutAnimationController lac=new LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.zoom_in));
	    lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
	    listView.setLayoutAnimation(lac);
	    listView.startLayoutAnimation();
	}

}
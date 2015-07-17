package cn.kangeqiu.kq.activity.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.CathecticActivity;
import com.nowagme.football.VoteActivity;
import com.nowagme.util.ImagerLoader;

public class TrueView {

	private Activity context;
	private LayoutInflater inflater;
	ImageView faceimg;
	TextView txt_name, txt_true;
	private ImagerLoader loader = new ImagerLoader();
	
	public TrueView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(final JSONObject members) throws JSONException {
		View view = inflater.inflate(R.layout.true_item, null);
		faceimg = (ImageView) view.findViewById(R.id.faceimg);
		txt_true = (TextView) view.findViewById(R.id.txt_true);
		txt_name = (TextView) view.findViewById(R.id.txt_name);
		loader.LoadImage(members.getString("icon"), faceimg);
		txt_name.setText(members.getString("nickname"));
		String state=members.getString("state");
		if (state.equals("0")) {
			txt_true.setText("不信");
			txt_true.setTextColor(Color.parseColor("#95DD89"));
		}else {
			txt_true.setText("我信");
			txt_true.setTextColor(Color.parseColor("#F2394C"));
		}
		
		return view;
	}

}

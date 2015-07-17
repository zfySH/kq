package cn.kangeqiu.kq.activity.view;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.VoteActivity;

public class VoteFirstView {
	private Activity context;
	private LayoutInflater inflater;
	private Map<String, Object> option = new HashMap<String, Object>();
	private boolean isChecked = false;

	public VoteFirstView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(JSONObject option, final int position, boolean isCheck) throws JSONException {
		View convertView = inflater.inflate(R.layout.vote_item1, null);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView txt_meeting = (TextView) convertView
				.findViewById(R.id.txt_meeting);
		TextView txt_meetingnum = (TextView) convertView
				.findViewById(R.id.txt_meetingnum);
		ImageView img_check = (ImageView) convertView
				.findViewById(R.id.img_check);
		RelativeLayout rl_content = (RelativeLayout) convertView
				.findViewById(R.id.rl_content);

		if (isCheck) {
			img_check.setImageResource(R.drawable.img_check);
		} else {
			img_check.setImageResource(R.drawable.img_notcheck);
		}
		name.setText(option.getString("name"));
		txt_meeting.setText(option.getString("name"));
		txt_meetingnum.setText(option.getString("count"));
		rl_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				((VoteActivity) context).changeState(position);
			}
		});
		return convertView;
	}
}

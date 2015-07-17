package cn.kangeqiu.kq.activity.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

public class VoteSecondView {
	private Activity context;
	private LayoutInflater inflater;
	private int width;
	private int rect;
	private int join_count;

	public VoteSecondView(Activity context, int join_count) {
		this.context = context;
		this.join_count = join_count;
		inflater = context.getLayoutInflater();

	}

	public View getView(JSONObject option) throws JSONException {
		View convertView = inflater.inflate(R.layout.vote_item2, null);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView txt_meeting = (TextView) convertView
				.findViewById(R.id.txt_meeting);
		TextView txt_meetingnum = (TextView) convertView
				.findViewById(R.id.txt_meetingnum);
		name.setText(option.getString("name"));
		// 获取宽度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		if (join_count == 0) {
			rect = 0;
		} else {
			rect = Integer.parseInt(option.getString("count"))* 1000/ join_count/ 10
					+ (Integer.parseInt(option.getString("count")) * 1000
							/ join_count % 10 < 5 ? 0 : 1);
		}
		txt_meeting.setWidth(((width - 400) / 100) * rect);

		txt_meetingnum.setText(rect + "%");
		return convertView;
	}
}

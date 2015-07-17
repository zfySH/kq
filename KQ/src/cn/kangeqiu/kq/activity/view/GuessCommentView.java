package cn.kangeqiu.kq.activity.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class GuessCommentView {
	private Activity context;
	private LayoutInflater inflater;

	public GuessCommentView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(JSONObject record) {
		View view = inflater.inflate(R.layout.guess_comment_item, null);
		CircleImageView faceImg = (CircleImageView) view
				.findViewById(R.id.abc_fragment_nearby__listview__iv_faceimg);
		TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
		TextView txt_time = (TextView) view.findViewById(R.id.txt_time);
		TextView txt_reply = (TextView) view.findViewById(R.id.txt_reply);
		try {
			// new DownAndShowImageTask(record.getString("icon"), faceImg)
			// .execute();
			ImagerLoader loader = new ImagerLoader();
			loader.LoadImage(record.getString("icon"), faceImg);
			
			txt_name.setText(record.getString("nickname"));

			String choose = "选择了";
			if ((record.getString("type").equals("0"))) {
				choose += "“主胜”";
			} else if ((record.getString("type").equals("1"))) {
				choose += "“客胜”";
			} else {
				choose += "“打平”";
			}

			txt_time.setText(choose);
			txt_reply.setText(record.getString("bet_score"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return view;
	}
}

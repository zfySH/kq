package cn.kangeqiu.kq.activity.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.CathecticActivity;
import com.nowagme.football.MatchCommentDetailActivity;
import com.nowagme.football.VoteActivity;
import com.nowagme.util.ImagerLoader;

public class CommentView {

	private Activity context;
	private LayoutInflater inflater;
	private TextView name, text, text_name;
	private ImageView logo;
	private TextView txt_time, txt_context;

	public String getComment_id() {
		return comment_id;
	}

	private Button txt_reply;
	ImagerLoader loader = new ImagerLoader();
	String comment_id = "", reply_nickname;

	public String getReply_nickname() {
		return reply_nickname;
	}

	String id;

	public CommentView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(final JSONObject list) throws JSONException {
		View view = inflater.inflate(R.layout.comment_item, null);
		name = (TextView) view.findViewById(R.id.txt_name);
		text = (TextView) view.findViewById(R.id.text);
		text_name = (TextView) view.findViewById(R.id.text_name);
		txt_time = (TextView) view.findViewById(R.id.txt_time);
		txt_context = (TextView) view.findViewById(R.id.txt_context);
		logo = (ImageView) view
				.findViewById(R.id.abc_fragment_nearby__listview__iv_faceimg);
		txt_reply = (Button) view.findViewById(R.id.txt_reply);
		try {
			// new DownAndShowImageTask(list.getJSONObject(position).getString(
			// "user_icon"), oneHolder.logo).execute();
			loader.LoadImage(list.getString("user_icon"), logo);
			name.setText(list.get("nickname").toString());
			txt_time.setText(list.get("time")

			.toString());
			reply_nickname = list.getString("nickname");
			if (!list.isNull("reply")) {

				text.setVisibility(View.VISIBLE);
				id = list.getJSONObject("reply").getString("id");
				text_name.setText(list.getJSONObject("reply").getString(
						"nickname")
						+ "：");
				txt_context.setText(list.getString("body"));
			} else {
				text.setVisibility(View.GONE);
				txt_context.setText(list.getString("body"));
			}

			txt_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if (context instanceof CathecticActivity) {
							((CathecticActivity) context).u_type(list
									.getString("nickname"));
						} else if (context instanceof VoteActivity) {
							((VoteActivity) context).u_type(list
									.getString("nickname"));
						} else if (context instanceof MatchCommentDetailActivity) {
							((MatchCommentDetailActivity) context).setType(2,
									list.getString("nickname"));
							((MatchCommentDetailActivity) context).u_type1();
							comment_id = list.getString("id");
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return view;
	}

}

package cn.kangeqiu.kq.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.MatchCommentDetailActivity;
import com.nowagme.util.ImagerLoader;

public class Adaptercomment1 extends BaseAdapter {
	private LayoutInflater inflater;
	private JSONArray list = new JSONArray();
	// private JSONObject reply = new JSONObject();
	private OneHolder oneHolder;
	String comment_id = "";

	String id;
	ImagerLoader loader = new ImagerLoader();

	// public String getNickname() {
	// return nickname;
	// }

	/**
	 * 上下文对象
	 */
	private Activity context = null;

	public Adaptercomment1(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();

	}

	public void setItem(JSONArray list) {
		if (list == null)
			return;
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return list.getJSONObject(position);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public String getComment_id() {
		return comment_id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		oneHolder = new OneHolder();

		if (view == null) {
			view = inflater.inflate(R.layout.comment_item, null);
			oneHolder.name = (TextView) view.findViewById(R.id.txt_name);
			oneHolder.logo = (ImageView) view
					.findViewById(R.id.abc_fragment_nearby__listview__iv_faceimg);
			oneHolder.txt_time = (TextView) view.findViewById(R.id.txt_time);
			oneHolder.txt_context = (TextView) view
					.findViewById(R.id.txt_context);
			oneHolder.txt_reply = (Button) view.findViewById(R.id.txt_reply);
			oneHolder.text= (TextView) view
					.findViewById(R.id.text);
			oneHolder.text_name= (TextView) view
					.findViewById(R.id.text_name);
			view.setTag(oneHolder);

		} else {
			oneHolder = (OneHolder) view.getTag();
		}

		try {
			// new DownAndShowImageTask(list.getJSONObject(position).getString(
			// "user_icon"), oneHolder.logo).execute();
			loader.LoadImage(list.getJSONObject(position)
					.getString("user_icon"), oneHolder.logo);
			oneHolder.name.setText(list.getJSONObject(position).get("nickname")
					.toString());
			oneHolder.txt_time.setText(list.getJSONObject(position).get("time")
					.toString());
			// reply = (JSONObject) ;
			if (!list.getJSONObject(position).isNull("reply")) {
				oneHolder.text.setVisibility(View.VISIBLE);
				id = list.getJSONObject(position).getJSONObject("reply")
						.getString("id");
				oneHolder.text_name.setText( list.getJSONObject(position).getJSONObject("reply")
						.getString("nickname")+"：");
				oneHolder.txt_context.setText( list.getJSONObject(position).getString("body"));
			} else {
				oneHolder.text.setVisibility(View.GONE);
				oneHolder.txt_context.setText(list.getJSONObject(position)
						.getString("body"));
			}

			oneHolder.txt_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						((MatchCommentDetailActivity) context).setType(2, list
								.getJSONObject(position).getString("nickname"));
						((MatchCommentDetailActivity) context).u_type1();

						comment_id = list.getJSONObject(position).getString(
								"id");
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

	public String getId() {
		return id;
	}

	class OneHolder {
		private TextView name,text,text_name;
		private ImageView logo;
		private TextView txt_time, txt_context;
		private Button txt_reply;

	}

}

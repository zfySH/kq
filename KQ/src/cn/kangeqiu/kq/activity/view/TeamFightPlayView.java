package cn.kangeqiu.kq.activity.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.ImagerLoader;

public class TeamFightPlayView {
	private Activity context;
	private LayoutInflater inflater;
	private ImageView icon;
	private TextView playName;

	public TeamFightPlayView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(String url, String name) {
		View view = inflater.inflate(R.layout.abc_item_play_icon, null);
		icon = (ImageView) view.findViewById(R.id.item_grida_image);
		playName = (TextView) view.findViewById(R.id.play_name);

		ImagerLoader loader = new ImagerLoader();
		loader.LoadImage(url, icon);
		// new DownAndShowImageTask(url, icon).execute();
		playName.setText(name);
		return view;
	}
}

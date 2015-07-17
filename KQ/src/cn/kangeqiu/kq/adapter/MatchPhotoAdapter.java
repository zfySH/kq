package cn.kangeqiu.kq.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import cn.kangeqiu.kq.R;

import com.easemob.applib.utils.Bimp;
import com.nowagme.football.PhotoActivity;

public class MatchPhotoAdapter extends BaseAdapter {
	private ArrayList<String> listfile = new ArrayList<String>();
	private LayoutInflater inflater;
	private Activity context;

//	@Override
//	public int getCount() {
//		return listfile.size();
//	}
	
	public int getCount() {
		if(Bimp.tempSelectBitmap.size() == 9){
			return 9;
		}
		return (Bimp.tempSelectBitmap.size() + 1);
	}

	public MatchPhotoAdapter(Activity context) {
		inflater = context.getLayoutInflater();
		this.context = context;
	}

	public void setItem(ArrayList<String> list) {
		if (list == null)
			return;

		this.listfile = list;

		notifyDataSetChanged();
	}
	
	public void update() {
		loading();
	}

	@Override
	public Object getItem(int arg0) {
		return listfile.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int postion, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.abc_item_published_grida, null);
			viewHolder = new ViewHolder(view);
			view.setTag(R.id.ViewHolder, viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag(R.id.ViewHolder);
		}

		
		
		if (postion ==Bimp.tempSelectBitmap.size()) {
			viewHolder.image.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.icon_addpic_unfocused));
			if (postion == 9) {
				viewHolder.image.setVisibility(View.GONE);
			}
		} else {
//			Bitmap bmp = getLoacalBitmap(listfile.get(postion));
//			viewHolder.image.setImageBitmap(bmp);
			viewHolder.image.setImageBitmap(Bimp.tempSelectBitmap.get(postion).getBitmap());
		}
		viewHolder.image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						PhotoActivity.class);
//				Bundle bundle=new Bundle();
				intent.putExtra("match_id", postion);
				context.startActivity(intent);
				
			}
		});
		// new DownAndShowImageTask(team1.get("icon").toString(),
		// viewHolder.team_icon1).execute();

		viewHolder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// viewHolder.image.r
				listfile.remove(postion);
				notifyDataSetChanged();

			}
		});
		return view;
	}

	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	class ViewHolder {
		ImageView image;
		ImageButton delete;

		public ViewHolder(View view) {
			image = (ImageView) view.findViewById(R.id.item_grida_image);
			delete = (ImageButton) view.findViewById(R.id.img_delete);
		}
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void loading() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (Bimp.max == Bimp.tempSelectBitmap.size()) {
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
						break;
					} else {
						Bimp.max += 1;
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				}
			}
		}).start();
	}

}

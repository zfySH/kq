package cn.kangeqiu.kq.activity.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import cn.kangeqiu.kq.R;

import com.nowagme.football.FragmentHotLine;
import com.nowagme.football.FragmentMatchHourse;
import com.nowagme.football.FragmentOnBoard;

public class MatchTopView {
	private Context context;
	private LayoutInflater inflater;
	private Button On_the_list, Ranking, exercise, Hotline;
	private Button[] mTabs;
	private int currentTabIndex;
	private Fragment[] fragments;
	private View view;
	public static View ll_top;

	public View getTopView() {
		return view.findViewById(R.id.ll_scroll);
	}

	public MatchTopView(Activity context) {

	}

	public MatchTopView(Context ctx, LayoutInflater mInflater) {
		this.context = ctx;
		inflater = mInflater;
	}

	public View getView() {
		view = inflater.inflate(R.layout.abc_match_top, null);
		// m_vp = (ViewPager) view.findViewById(R.id.viewpager);

		ll_top = view.findViewById(R.id.ll_scroll);
		On_the_list = (Button) view.findViewById(R.id.top_buy_layout)
				.findViewById(R.id.On_the_list);
		Ranking = (Button) view.findViewById(R.id.top_buy_layout).findViewById(
				R.id.Ranking);
		exercise = (Button) view.findViewById(R.id.top_buy_layout)
				.findViewById(R.id.exercise);
		Hotline = (Button) view.findViewById(R.id.top_buy_layout).findViewById(
				R.id.Hotline);
		mTabs = new Button[4];
		mTabs[0] = On_the_list;
		mTabs[1] = Ranking;
		mTabs[2] = exercise;
		mTabs[3] = Hotline;
		mTabs[0].setSelected(true);
		On_the_list.setOnClickListener(new txListener(0));
		Ranking.setOnClickListener(new txListener(1));
		exercise.setOnClickListener(new txListener(2));
		Hotline.setOnClickListener(new txListener(3));

		Fragment btFragment = new FragmentOnBoard();
		Fragment secondFragment = new FragmentMatchHourse();
		// Fragment secondFragment = TestFragment
		// .newInstance("this is second fragment");
		Fragment thirdFragment = new com.nowagme.football.FragmentActivity();
		Fragment fourthFragment = new FragmentHotLine();

		fragments = new Fragment[] { btFragment, secondFragment, thirdFragment,
				fourthFragment };
		// 添加显示第一个fragment
		((FragmentActivity) context).getSupportFragmentManager()
				.beginTransaction().add(R.id.fragment_container, btFragment)
				.show(btFragment).commit();

		return view;
	}

	public class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// mPager.setCurrentItem(index);
			if (currentTabIndex != index) {
				FragmentTransaction trx = ((FragmentActivity) context)
						.getSupportFragmentManager().beginTransaction();
				trx.hide(fragments[currentTabIndex]);
				if (!fragments[index].isAdded()) {
					trx.add(R.id.fragment_container, fragments[index]);
				}
				trx.show(fragments[index]).commit();
			}
			mTabs[currentTabIndex].setSelected(false);
			// // 把当前tab设为选中状态
			mTabs[index].setSelected(true);
			// mTabs[currentTabIndex].set
			currentTabIndex = index;

		}
	}
}

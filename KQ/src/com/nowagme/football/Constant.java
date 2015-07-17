package com.nowagme.football;

import cn.kangeqiu.kq.R;

public class Constant {
	
	/**
	 * 球员性别资源ID
	 */
	public final static int[] res_player_sex = new int[]{
		R.drawable.abc_sex_unknown,
		R.drawable.abc_sex_man,
		R.drawable.abc_sex_woman
	};
	
	
	/**
	 * 球场星级资源ID
	 */
	public final static int[] res_court_star = new int[]{
		R.drawable.abc_star_0,
		R.drawable.abc_star_5,
		R.drawable.abc_star_10,
		R.drawable.abc_star_15,
		R.drawable.abc_star_20,
		R.drawable.abc_star_25,
		R.drawable.abc_star_30,
		R.drawable.abc_star_35,
		R.drawable.abc_star_40,
		R.drawable.abc_star_45,
		R.drawable.abc_star_50
	};
	
	/**
	 * 获取球员性别资源ID
	 * @param sex
	 * @return
	 */
	public static int getResourceOfPlayerSex(int sex) {
		return res_player_sex[sex];
	}

	/**
	 * 获取球场星级资源ID.
	 * @param star
	 * @return
	 */
	public static int getResourceOfCourtStar(int star) {
		int index = star/5;
		if(index<0 || index>res_court_star.length-1) return res_court_star[0];
		return res_court_star[index];
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}

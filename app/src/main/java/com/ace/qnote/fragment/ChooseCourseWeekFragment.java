package com.ace.qnote.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ace.qnote.R;
import com.ace.qnote.view.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asd on 10/17/2017.
 */

public class ChooseCourseWeekFragment extends DialogFragment {

    private int endWeek;
    private int startWeek;

    private ResultCallback callback;

    WheelView wheelStartWeek;
    WheelView wheelEndWeek;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_choose_course_week, container, true);

        List<String> fromWeekList = new ArrayList<>();
        List<String> endWeekList = new ArrayList<>();
        //从当前周开始选时间
        for (int i = 1; i <= 20; i++) {
            fromWeekList.add("第" + i + "周");
            endWeekList.add("第" + i + "周");
        }


        WheelView wheelStartWeek = view.findViewById(R.id.wheelStartWeek);
        WheelView wheelEndWeek = view.findViewById(R.id.wheelEndWeek);

        wheelStartWeek.setItems(fromWeekList);
        wheelEndWeek.setItems(endWeekList);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.width = 900;
        lp.height = 1000;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public void onViewClicked() {

        startWeek = wheelStartWeek.getSeletedIndex() + 1;
        endWeek = wheelEndWeek.getSeletedIndex() + 1;
        if(startWeek>endWeek){
            Toast.makeText(getActivity(), "对不起，时间选择不合法！", Toast.LENGTH_SHORT).show();
            return;
        }
        //回调
        if(callback!=null)
            callback.onFinish(startWeek,endWeek);
        //销毁界面
        dismiss();
    }

    public void setCallback(ResultCallback callback) {
        this.callback = callback;
    }


    /**
     * 回调接口
     */
    public interface ResultCallback {
        void onFinish(int startWeek, int endWeek);
    }


}

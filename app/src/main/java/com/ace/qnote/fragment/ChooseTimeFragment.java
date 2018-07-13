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

import java.util.Arrays;

/**
 * Created by asd on 10/17/2017.
 */

public class ChooseTimeFragment extends DialogFragment {
    WheelView wheelWeekday;
    WheelView wheelStartTime;
    WheelView wheelEndTime;

    private Callback callback;

    String[] weekdays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    String[] times = {"第一节", "第二节", "第三节", "第四节", "第五节", "第六节", "第七节", "第八节", "第九节", "第十节"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_course_time, container, true);

        WheelView wheelWeekday = view.findViewById(R.id.wheelWeekday);
        WheelView wheelStartTime = view.findViewById(R.id.wheelStartTime);
        WheelView wheelEndTime = view.findViewById(R.id.wheelEndTime);

        wheelWeekday.setItems(Arrays.asList(weekdays));
        wheelStartTime.setItems(Arrays.asList(times));
        wheelEndTime.setItems(Arrays.asList(times));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.width = 1000;
        lp.height = 900;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onViewClicked() {

        int weekday = wheelWeekday.getSeletedIndex()+1;//下标从0开始的 要加1
        int startSection = wheelStartTime.getSeletedIndex()+1;
        int endSection = wheelEndTime.getSeletedIndex()+1;
        if(startSection>endSection){
            Toast.makeText(getActivity(), "课程时间选择非法，请重新选择", Toast.LENGTH_SHORT).show();
            return;
        }

        callback.onFinish(weekday,startSection,endSection);
        dismiss();
    }


    public interface Callback {
        void onFinish(int weekday, int startSection, int endSection);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

}

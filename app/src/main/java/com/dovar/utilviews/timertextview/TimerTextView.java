package com.dovar.utilviews.timertextview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dovar.utilviews.utils.TimeUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/11/21 0021.
 */
public class TimerTextView extends TextView {

    public TimerTextView(Context context) {
        this(context, null);
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private long time;//计时间隔，默认每秒计时
    private long curTime;//基准时间
    private String offTime;//截止时间
    private String initText;//本控件statTimer时显示的初始文本
    private String offTimeText;//本控件计时截止后的显示文本

    private
    @Builder.Visibility
    int visibility_;//本控件计时截止后的可见性

    private TextView infoView;//关联的TextView
    private int info_visible;//计时截止后infoView的可见性
    private String info_text;//计时截止后infoView的文本，为空时默认设置为offTimeText

    //默认计时显示："Hour:Min:Sec",eg:"03:20:59"
    private boolean showDay;//需要显示天数,"beforeDay"+Day+""+Hour+""+Min+""+sec+""
    private boolean showHour;//需要显示小时,"beforeHour"+Hour+""+Min+""+sec+""
    private boolean showMin;//需要显示分钟,"beforeMin"+Min+""+sec+""
    private boolean showSec;//"beforeSec"+sec+"behindSec"
    private String beforeDay;
    private String beforeHour;
    private String beforeMin;
    private String beforeSec;
    private String behindSec;

    private TimerTask task;//计时任务
    private timerHandler handler = new timerHandler(this);

    /**
     * 开始计时
     */
    private void startTimer() {
        if (offTime == null) return;
        if (curTime < TimeUtils.string2Milliseconds(offTime, new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault()))) {
            if (initText != null) {
                setText(initText);
            } else {
                if (showDay) {
                    setText(getCustomSurplusTime(false, beforeDay, beforeHour, beforeMin, beforeSec, behindSec));
                } else if (showHour) {
                    setText(getCustomSurplusTime(false, beforeHour, beforeMin, beforeSec, behindSec));
                } else if (showMin) {
                    setText(getCustomSurplusTime(false, beforeMin, beforeSec, behindSec));
                } else if (showSec) {
                    setText(getCustomSurplusTime(false, beforeSec, behindSec));
                } else {
                    setText(getSurplusTime());
                }
            }
        } else {
            if (offTimeText != null) {
//                setTextColor(Color.WHITE);
                setText(offTimeText);
            } else {
                setText("逾期未处理");
            }
        }
        if (task == null) {
            MyTimer mTimer = MyTimer.getInstance();
            task = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            };
            mTimer.schedule(task, time, time);
        }
    }


    public long getCurTime() {
        return curTime;
    }

    public TextView getInfoView() {
        return infoView;
    }

    public long getTime() {
        return time;
    }

    public String getOffTimeText() {
        return offTimeText;
    }

    public
    @Builder.Visibility
    int getVisibility_() {
        return visibility_;
    }

    public int getInfo_visible() {
        return info_visible;
    }

    public String getInfo_text() {
        return info_text;
    }

    private void setCurTime(long curTime) {
        this.curTime = curTime;
    }

    public String getOffTime() {
        return offTime;
    }

    private boolean isShowDay() {
        return showDay;
    }

    private boolean isShowHour() {
        return showHour;
    }

    private boolean isShowMin() {
        return showMin;
    }

    private boolean isShowSec() {
        return showSec;
    }

    private TimerTask getTask() {
        return task;
    }

    private String getBeforeDay() {
        return beforeDay;
    }

    private String getBeforeHour() {
        return beforeHour;
    }

    private String getBeforeMin() {
        return beforeMin;
    }

    private String getBeforeSec() {
        return beforeSec;
    }

    private String getBehindSec() {
        return behindSec;
    }

    public OnClickListener getmOnClickListener() {
        return mOnClickListener;
    }

    static class timerHandler extends Handler {
        private WeakReference<TimerTextView> timerTextView;

        timerHandler(TimerTextView timerTextView) {
            this.timerTextView = new WeakReference<>(timerTextView);
        }

        @Override
        public void handleMessage(Message msg) {
            if (timerTextView.get() == null) return;
            TimerTextView tv = timerTextView.get();
            super.handleMessage(msg);
            tv.setCurTime(tv.getCurTime() + tv.getTime());//基准时间调整
            if (tv.getCurTime() < TimeUtils.string2Milliseconds(tv.getOffTime(), new SimpleDateFormat("yy-MM-dd HH:mm:ss"))) {
                if (tv.isShowDay()) {
                    tv.setText(tv.getCustomSurplusTime(true, tv.getBeforeDay(), tv.getBeforeHour(), tv.getBeforeMin(), tv.getBeforeSec(), tv.getBehindSec()));
                } else if (tv.isShowHour()) {
                    tv.setText(tv.getCustomSurplusTime(true, tv.getBeforeHour(), tv.getBeforeMin(), tv.getBeforeSec(), tv.getBehindSec()));
                } else if (tv.isShowMin()) {
                    tv.setText(tv.getCustomSurplusTime(true, tv.getBeforeMin(), tv.getBeforeSec(), tv.getBehindSec()));
                } else if (tv.isShowSec()) {
                    tv.setText(tv.getCustomSurplusTime(true, tv.getBeforeSec(), tv.getBehindSec()));
                } else {
                    tv.setText(tv.getSurplusTime());
                }
            } else {
                if (tv.getTask() != null) {
                    tv.getTask().cancel();
                }
                tv.setVisibility(tv.getVisibility_());
                if (tv.getOffTimeText() != null) {
                    tv.setText(tv.getOffTimeText());
                } else {
                    tv.setText("逾期未处理");
                }
                TextView infoView = tv.getInfoView();
                if (infoView != null) {
                    int info_visible = tv.getInfo_visible();
                    if (info_visible == INVISIBLE) {
                        infoView.setVisibility(INVISIBLE);
                    } else if (info_visible == GONE) {
                        infoView.setVisibility(GONE);
                    } else {
                        infoView.setVisibility(VISIBLE);
                        if (tv.getInfo_text() != null) {
                            infoView.setText(tv.getInfo_text());
                        } else {
                            infoView.setText(tv.getOffTimeText());
                        }
                    }
                }
                tv.setOnClickListener(tv.getmOnClickListener());
            }
        }
    }

    /**
     * 获取剩余时间
     * 以手机系统当前时间作为基准时间计算
     */
    private String getSurplusTimeByNow() {
        if (offTime == null) {
            return "";
        }
        long hour = TimeUtils.getIntervalByNow(offTime, TimeUtils.TimeUnit.HOUR);
        long min = TimeUtils.getIntervalByNow(offTime, TimeUtils.TimeUnit.MIN);
        long sec = TimeUtils.getIntervalByNow(offTime, TimeUtils.TimeUnit.SEC);
        return hour + ":" + (min - hour * 60) + ":" + (sec - min * 60);
    }

    /**
     * 获取剩余时间
     * 根据基准时间计算获取
     */
    private String getSurplusTime() {
        if (curTime == 0) {
            return getSurplusTimeByNow();
        }
        if (offTime == null) {
            return "";
        }
        long hour = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(curTime), offTime, TimeUtils.TimeUnit.HOUR);
        long min = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(curTime), offTime, TimeUtils.TimeUnit.MIN);
        long sec = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(curTime), offTime, TimeUtils.TimeUnit.SEC);
        return hour + ":" + (min - hour * 60) + ":" + (sec - min * 60);
    }

    /**
     * 根据基准时间计算剩余时间
     * 自定义显示格式,eg：请在3天16小时52分钟36秒内确认
     *
     * @param beforeDay  显示在天数前面的文字，eg:请在
     * @param beforeHour 显示在小时前面的文字，eg:天
     * @param beforeMin  显示在分钟前面的文字，eg:小时
     * @param beforeSec  显示在秒前面的文字,eg:分钟
     * @param behindSec  前世在秒后面的文字,eg:秒内确认
     */
    private String getCustomSurplusTime(boolean isCurTime, String beforeDay, String beforeHour, String beforeMin, String beforeSec, String behindSec) {
        if (offTime == null) {
            return "";
        }
        long beginTime;
        if (isCurTime) {
            beginTime = curTime;
        } else {
            beginTime = System.currentTimeMillis();
        }
        long day = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.DAY);
        long hour = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.HOUR);
        long min = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.MIN);
        long sec = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.SEC);
        StringBuilder sb = new StringBuilder();
        if (beforeDay != null) {
            sb.append(beforeDay);
        }
        sb.append(day);//天
        if (beforeHour != null) {
            sb.append(beforeHour);
        }
        sb.append(hour - 24 * day);//小时
        if (beforeMin != null) {
            sb.append(beforeMin);
        }
        sb.append(min - 60 * hour);//分钟
        if (beforeSec != null) {
            sb.append(beforeSec);
        }
        sb.append(sec - 60 * min);//秒
        if (behindSec != null) {
            sb.append(behindSec);
        }
        return sb.toString();
    }

    /**
     * 根据基准时间计算剩余时间
     * 自定义显示格式,eg：请在16小时52分钟36秒内确认
     *
     * @param beforeHour 显示在小时前面的文字，eg:请在
     * @param beforeMin  显示在分钟前面的文字，eg:小时
     * @param beforeSec  显示在秒前面的文字,eg:分钟
     * @param behindSec  前世在秒后面的文字,eg:秒内确认
     */
    private String getCustomSurplusTime(boolean isCurTime, String beforeHour, String beforeMin, String beforeSec, String behindSec) {
        if (offTime == null) {
            return "";
        }
        long beginTime;
        if (isCurTime) {
            beginTime = curTime;
        } else {
            beginTime = System.currentTimeMillis();
        }
        long hour = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.HOUR);
        long min = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.MIN);
        long sec = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.SEC);

        StringBuilder sb = new StringBuilder();
        if (beforeHour != null) {
            sb.append(beforeHour);
        }
        sb.append(hour);//小时
        if (beforeMin != null) {
            sb.append(beforeMin);
        }
        sb.append(min - 60 * hour);//分钟
        if (beforeSec != null) {
            sb.append(beforeSec);
        }
        sb.append(sec - 60 * min);//秒
        if (behindSec != null) {
            sb.append(behindSec);
        }
        return sb.toString();
    }

    /**
     * 根据基准时间计算剩余时间
     * 自定义显示格式,eg：请在52分钟36秒内确认
     *
     * @param beforeMin 显示在分钟前面的文字，eg:请在
     * @param beforeSec 显示在秒前面的文字,eg:分钟
     * @param behindSec 前世在秒后面的文字,eg:秒内确认
     */
    private String getCustomSurplusTime(boolean isCurTime, String beforeMin, String beforeSec, String behindSec) {
        if (offTime == null) {
            return "";
        }
        long beginTime;
        if (isCurTime) {
            beginTime = curTime;
        } else {
            beginTime = System.currentTimeMillis();
        }
        long min = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.MIN);
        long sec = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.SEC);
        StringBuilder sb = new StringBuilder();
        if (beforeMin != null) {
            sb.append(beforeMin);
        }
        sb.append(min);//分钟
        if (beforeSec != null) {
            sb.append(beforeSec);
        }
        sb.append(sec - 60 * min);//秒
        if (behindSec != null) {
            sb.append(behindSec);
        }
        return sb.toString();
    }

    /**
     * 根据基准时间计算剩余时间
     * 自定义显示格式,eg：请在36秒内确认
     *
     * @param beforeSec 显示在秒前面的文字,eg:请在
     * @param behindSec 前世在秒后面的文字,eg:秒内确认
     */
    private String getCustomSurplusTime(boolean isCurTime, String beforeSec, String behindSec) {
        if (offTime == null) {
            return "";
        }
        long beginTime;
        if (isCurTime) {
            beginTime = curTime;
        } else {
            beginTime = System.currentTimeMillis();
        }
        long sec = TimeUtils.getIntervalTime(TimeUtils.milliseconds2String(beginTime), offTime, TimeUtils.TimeUnit.SEC);
        StringBuilder sb = new StringBuilder();
        if (beforeSec != null) {
            sb.append(beforeSec);
        }
        sb.append(sec);//秒
        if (behindSec != null) {
            sb.append(behindSec);
        }
        return sb.toString();
    }


    public void setInfoView(TextView v) {
        infoView = v;
    }

    public void setInfoView(TextView v, int visibility) {
        infoView = v;
        info_visible = visibility;
    }

    /**
     * 关联一个textView
     *
     * @param v          infoView
     * @param visibility infoView计时截止后的可见性
     * @param text       infoView计时截止后的显示文本
     */
    public void setInfoView(TextView v, int visibility, String text) {
        setInfoView(v, visibility);
        info_text = text;
    }

    private OnClickListener mOnClickListener;//计时截止后添加的点击事件

    /**
     * 设置计时截止后的点击事件
     * 计时截止后点击本控件才会响应的点击事件
     */
    public void setTimeOffClickListener(OnClickListener mTimeOffClickListener) {
        if (mTimeOffClickListener != null) {
            mOnClickListener = mTimeOffClickListener;
        }
    }

    /**
     * 停止计时并回收资源
     */
    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);//移除所有任务
            handler = null;
        }
    }

    public void init(TimerConfig config) {
        if (config != null) {
            time = config.time;
            offTime = config.offTime;
            curTime = config.curTime;
            initText = config.initText;
            offTimeText = config.offTimeText;
            visibility_ = config.visibility_;

            showSec = config.showSec;
            showDay = config.showDay;
            showHour = config.showHour;
            showMin = config.showMin;
            beforeDay = config.beforeDay;
            beforeHour = config.beforeHour;
            beforeMin = config.beforeMin;
            beforeSec = config.beforeSec;
            behindSec = config.behindSec;
        }
        startTimer();
    }

    public static class TimerConfig {
        private long time;//计时间隔，默认每秒计时
        private long curTime;//基准时间
        private String offTime;//截止时间
        private String initText;//本控件statTimer时显示的初始文本
        private String offTimeText;//本控件计时截止后的显示文本
        private
        @Builder.Visibility
        int visibility_;


        private boolean showSec;
        private boolean showDay;
        private boolean showHour;
        private boolean showMin;
        private String beforeDay;
        private String beforeHour;
        private String beforeMin;
        private String beforeSec;
        private String behindSec;

        private TimerConfig() {

        }
    }

    public static class Builder {
        private long time = 1000;//计时间隔，默认每秒计时
        private long curTime = System.currentTimeMillis();//基准时间,默认为当前系统时间
        private String initText;//本控件statTimer时显示的初始文本
        private String offTimeText;//本控件计时截止后的显示文本
        private
        @Visibility
        int visibility_ = VISIBLE;//控件计时截止后的可见性,默认VISIBLE

        private boolean showSec;
        private boolean showDay;
        private boolean showHour;
        private boolean showMin;
        private String beforeDay;
        private String beforeHour;
        private String beforeMin;
        private String beforeSec;
        private String behindSec;

        /**
         * @param offTime java时间戳
         */
        public TimerConfig create(long offTime) {
            TimerConfig config = new TimerConfig();
            config.time = time;
            config.offTime = TimeUtils.milliseconds2String(offTime);
            config.curTime = curTime;
            config.initText = initText;
            config.offTimeText = offTimeText;
            config.visibility_ = visibility_;

            config.showSec = showSec;
            config.showDay = showDay;
            config.showHour = showHour;
            config.showMin = showMin;
            config.beforeDay = beforeDay;
            config.beforeHour = beforeHour;
            config.beforeMin = beforeMin;
            config.beforeSec = beforeSec;
            config.behindSec = behindSec;

            return config;
        }

        /**
         * @param offTime 格式：yy-MM-dd HH:mm:ss
         */
        public TimerConfig create(String offTime) {
            TimerConfig config = new TimerConfig();
            config.time = time;
            config.offTime = offTime;
            config.curTime = curTime;
            config.initText = initText;
            config.offTimeText = offTimeText;
            config.visibility_ = visibility_;

            config.showSec = showSec;
            config.showDay = showDay;
            config.showHour = showHour;
            config.showMin = showMin;
            config.beforeDay = beforeDay;
            config.beforeHour = beforeHour;
            config.beforeMin = beforeMin;
            config.beforeSec = beforeSec;
            config.behindSec = behindSec;

            return config;
        }

        /**
         * 设置计时间隔
         *
         * @param mTime 毫秒
         */
        public Builder setTime(long mTime) {
            time = mTime;
            return this;
        }


        /**
         * 设置计时基准时间
         *
         * @param mStartTime ms
         */
        public Builder setStartTime(long mStartTime) {
            this.curTime = mStartTime;
            return this;
        }

        /**
         * 初始文本
         */
        public Builder setInitText(String text) {
            this.initText = text;
            return this;
        }

        /**
         * 设置计时截止后的显示文本
         *
         * @param mOffTimeText
         */
        public Builder setOffTimeText(String mOffTimeText) {
            offTimeText = mOffTimeText;
            return this;
        }

        /**
         * 设置计时截止后控件的可见性
         *
         * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}
         */
        public Builder setOffTimeVisible(@Visibility int visibility) {
            this.visibility_ = visibility;
            return this;
        }

        public Builder setCustomText(String beforeDay, String beforeHour, String beforeMin, String beforeSec, String behindSec) {
            this.showDay = true;
            this.beforeDay = beforeDay;
            this.beforeHour = beforeHour;
            this.beforeMin = beforeMin;
            this.beforeSec = beforeSec;
            this.behindSec = behindSec;
            return this;
        }

        public Builder setCustomText(String beforeHour, String beforeMin, String beforeSec, String behindSec) {
            this.showHour = true;
            this.beforeHour = beforeHour;
            this.beforeMin = beforeMin;
            this.beforeSec = beforeSec;
            this.behindSec = behindSec;
            return this;
        }

        public Builder setCustomText(String beforeMin, String beforeSec, String behindSec) {
            this.showMin = true;
            this.beforeMin = beforeMin;
            this.beforeSec = beforeSec;
            this.behindSec = behindSec;
            return this;
        }

        public Builder setCustomText(String beforeSec, String behindSec) {
            this.showSec = true;
            this.beforeSec = beforeSec;
            this.behindSec = behindSec;
            return this;
        }

        @IntDef({VISIBLE, INVISIBLE, GONE})
        @Retention(RetentionPolicy.SOURCE)
        @interface Visibility {
        }
    }
}

package com.metasequoia.services.radio.factory;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.metasequoia.datebase.RadioDbManager;
import com.metasequoia.services.radio.api.RadioApi;
import com.metasequoia.manager.radio.bean.Frequency;
import com.metasequoia.manager.radio.bean.PublicDef;
import com.metasequoia.manager.radio.listener.OnDataChangeListener;
import com.metasequoia.manager.radio.bean.RadioArea;

public class RadioScanTask extends AsyncTask {
    private static final String TAG = "RadioScanTask";
    private Radio mRadio = null;
    private RadioApi mRadioApi = null;
    private RadioDbManager dbManager;
    private OnDataChangeListener mOnDataChangeListener;

    private boolean direction;
    protected RadioArea mRadioArea = null;

    RadioScanTask(Context context, Radio mRadio, RadioApi mRadioApi, RadioArea mRadioArea, boolean direction, OnDataChangeListener listener) {
        Log.i(TAG, "RadioScanTask..");
        this.mOnDataChangeListener = listener;
        this.mRadioArea = mRadioArea;
        this.mRadio = mRadio;
        this.mRadioApi = mRadioApi;
        this.direction = direction;


        dbManager = RadioDbManager.getInstance();
        dbManager.init(context);
    }

    @Override
    protected Object doInBackground(Object... objects) {
        dbManager.delete(Frequency.class, mRadio.getId());
        Log.i(TAG, "doInBackgroud..." + mRadio.getId());

        try {
            int step = mRadioArea.getFrequencyStep();
            int effectCount = 0;
            int uninumame = 0;
            if(mRadio.getId() == PublicDef.RADIO_AM)
                uninumame = 18;   /** FM获取15个台会停止搜索 **/
            Log.i(TAG, "step:" + step);
            if(direction) { //正向
                Log.i(TAG, "direction:" + direction + "," + mRadioArea.getFrequencyMin() + "," + mRadioArea.getFrequencyMax());
                for(int freq = mRadioArea.getFrequencyMin(); freq <= mRadioArea.getFrequencyMax() && !this.isCancelled(); freq += step) {
                    int result = mRadioApi.getQuality(mRadio.getId(), freq);
                    Log.i(TAG, "result:" + result);
                    if(this.isCancelled()) break;
                    Frequency frequency = new Frequency(0, freq, mRadio.getId(), "UNKNOWN",  0, /*mRadio.getAreaId(),*/ "0");
                    if(result == 1) {
                        //Frequency frequencies = new Frequency(0, freq, mRadio.getId(), "UNKNOWN",  effectCount, /*mRadio.getAreaId(),*/ Integer.toString(effectCount+uninumame));
                        frequency.setIndex(effectCount);
                        frequency.setUninumame(Integer.toString(effectCount+uninumame));
                        dbManager.save(frequency);
                        effectCount++;
                    }
                    this.publishProgress(frequency, result);
                    if(effectCount == PublicDef.EFFECTCOUNT)  break;
                    Thread.sleep(5);
                }
            } else {  //反向
                Log.i(TAG, "direction:" + direction);
                for (int freq = mRadioArea.getFrequencyMax(); freq>=mRadioArea.getFrequencyMin() && !this.isCancelled(); freq -= step) {
                    int result = mRadioApi.getQuality(mRadio.getId(), freq);
                    Log.i(TAG, "1.result:" + result);
                    if (this.isCancelled()) break;
                    Frequency frequency = new Frequency(0, freq, mRadio.getId(), "UNKNOWN", effectCount, /*mRadio.getAreaId(),*/ Integer.toString(effectCount+uninumame));
                    if (result == 1) {
                        //Frequency frequency = new Frequency(0, freq, mRadio.getId(), "UNKNOWN", effectCount, /*mRadio.getAreaId(),*/ Integer.toString(effectCount+uninumame));
                        frequency.setIndex(effectCount);
                        frequency.setUninumame(Integer.toString(effectCount+uninumame));
                        dbManager.save(frequency);
                        effectCount++;

                    }
                    this.publishProgress(freq, result);
                    if (effectCount == PublicDef.EFFECTCOUNT) break;
                    Thread.sleep(5);
                }
            }
            Log.i(TAG, "effectCount:" + effectCount);
            if(effectCount == 0) {
                /**  没有扫描到任何频点时默认写入最后一个频点 **/
                Frequency frequency = new Frequency(0, mRadioArea.getFrequencyMax(), mRadio.getId(), "UNKNOWN", 0, /*mRadio.getAreaId(),*/ Integer.toString(effectCount));
                dbManager.save(frequency);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(TAG, "start search");
//        if(mOnDataChangeListener != null)
//            mOnDataChangeListener.onStart("start");

    }

    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if(mOnDataChangeListener != null)
            mOnDataChangeListener.onEnd("end");
    }

    @Override
    protected void onCancelled(Object result) {
        super.onCancelled(result);
        if(mOnDataChangeListener != null)
            mOnDataChangeListener.onEnd("cancelled");
    }

    protected void onProgressUpdate(Object... value) {
        if(value.length <= 1 || this.isCancelled()) return;

        if(mOnDataChangeListener != null) {
            //RadioService
            mOnDataChangeListener.onScanChanged((Frequency) value[0],(Integer) value[1]);
        }
        super.onProgressUpdate(value);
    }
}
